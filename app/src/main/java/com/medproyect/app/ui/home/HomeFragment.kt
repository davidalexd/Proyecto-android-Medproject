package com.medproyect.app.ui.home

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.MyAppintments
import com.medproyect.app.R
import com.medproyect.app.adapter.AutoresAppointmentAdapter
import com.medproyect.app.adapter.CategoryAdapter
import com.medproyect.app.adapter.UserAppointmentAdapter
import com.medproyect.app.databinding.FragmentHomeBinding
import com.medproyect.app.models.AutoresAppointment
import com.medproyect.app.models.Category
import com.medproyect.app.models.UserAppointment
import com.medproyect.app.provider.AutoresAppointmentProvider
import com.medproyect.app.provider.CategoryProvider
import com.medproyect.app.provider.UserAppointmentProvider
import com.medproyect.app.ui.miservicios.MyServiceFragment

class HomeFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private var _binding:FragmentHomeBinding?=null
    private  val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private var Urlsearch:String ="https://media.discordapp.net/attachments/873053673255735346/979008135329095700/giphy.gif"
    private var UrlNoSearch:String ="https://media.discordapp.net/attachments/873053673255735346/979011460426510406/giphy_2.gif"
    private lateinit var rvUserAppointments : RecyclerView
    companion object {
        fun newInstance() = HomeFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        rvUserAppointments = binding.rvlastUserAppointments
        setup()
        fillData()
        getMyAutores()
        initRecyclerView()
        initAppointmentRecycler()
        initAutoresRecycler()
        return binding.root
    }
    private fun initRecyclerView() {
        val manager = GridLayoutManager(activity,2)
        val recyclerView =binding.reciclerCategories
        recyclerView.layoutManager = manager
        recyclerView.adapter = CategoryAdapter(CategoryProvider.categoryList)
    }
    private fun initAppointmentRecycler(){
        val manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        rvUserAppointments.layoutManager = manager
        rvUserAppointments.adapter = UserAppointmentAdapter(UserAppointmentProvider.userAppointments,3)
    }

    private  fun initAutoresRecycler(){
        val managerAutores= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        val recyclerViewAutores =binding.reciclerAutores
        recyclerViewAutores.layoutManager=managerAutores
        recyclerViewAutores.adapter = AutoresAppointmentAdapter(AutoresAppointmentProvider.AutoresAppointmentList)
    }
    private  fun getMyAutores(){
        Glide.with(binding.ivnoLoader.context).load(Urlsearch).into(binding.ivnoLoader)

        val recyclerViewAutores =binding.reciclerAutores
        AutoresAppointmentProvider.AutoresAppointmentList.clear()
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    activity?.runOnUiThread {
                        val docService = db.collection("service").document(document.data.get("serviceID").toString())
                        docService.get()
                            .addOnSuccessListener { service ->
                                if (service != null) {
                                    binding.ivnoLoader.visibility = View.GONE
                                    AutoresAppointmentProvider.AutoresAppointmentList.add(
                                        AutoresAppointment(
                                            document.data.get("nameSpecialist") as String,
                                            document.data.get("photoSpecialist") as String,
                                            service.data?.get("itemName") as String,
                                        )
                                    )
                                    recyclerViewAutores.adapter?.notifyDataSetChanged()

                                } else {
                                    Log.d(TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(TAG, "get failed with ", exception)
                            }


                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }
    private  fun setup(){
        Glide.with(binding.ivnoLoaderCat.context).load(Urlsearch).into(binding.ivnoLoaderCat)
        val recyclerView =binding.reciclerCategories
        CategoryProvider.categoryList.clear()
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    activity?.runOnUiThread {
                        binding.ivnoLoaderCat.visibility = View.GONE
                        CategoryProvider.categoryList.add(
                            Category(document.id,document.data.get("itemName") as String,
                                    document.data.get("photo") as String
                            )
                        )
                        recyclerView.adapter?.notifyDataSetChanged()
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

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