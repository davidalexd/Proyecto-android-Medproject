package com.medproyect.app

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.adapter.UserAppointmentAdapter
import com.medproyect.app.databinding.FragmentMyAppointmentsBinding
import com.medproyect.app.models.UserAppointment
import com.medproyect.app.provider.UserAppointmentProvider

class MyAppintments : Fragment() {
    private var _binding: FragmentMyAppointmentsBinding?=null
    private  val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvUserAppointments : RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAppointmentsBinding.inflate(inflater,container,false)
        rvUserAppointments = binding.rvUserAppointments
        // Inflate the layout for this fragment
        setup()
        fillData()
        return binding.root
    }
    companion object {
        fun newInstance() = MyAppintments()
    }
    private fun setup(){

        val recyclerView = rvUserAppointments
        val manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = manager
        recyclerView.adapter = UserAppointmentAdapter(UserAppointmentProvider.userAppointments, isLimited = false)
    }

    private fun fillData() {
        UserAppointmentProvider.userAppointments.clear()
        val id = auth.uid
        val recyclerViewUserAppointments = rvUserAppointments
        db.collection("user_appointments")
            .whereEqualTo("user_id",id)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val docAppointment = db.collection("appointments").document(doc["appointment_id"].toString())
                    docAppointment.get()
                        .addOnSuccessListener { appointment ->
                            if (appointment != null) {
                                activity?.runOnUiThread  {
                                    val userAppointment = UserAppointment(
                                        doc.id,
                                        appointment["itemDate"] as Timestamp,
                                        appointment.id,
                                        appointment["nameSpecialist"].toString(),
                                        (appointment["duration"] as String).toInt()
                                    )
                                    UserAppointmentProvider.userAppointments.add(userAppointment)
                                    recyclerViewUserAppointments.adapter?.notifyDataSetChanged()
                                }
                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    }
