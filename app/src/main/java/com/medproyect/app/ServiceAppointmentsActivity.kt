package com.medproyect.app

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.adapter.AppointmentAdapter
import com.medproyect.app.adapter.MyServiceAdapter
import com.medproyect.app.databinding.ActivityServiceAppointmentsBinding
import com.medproyect.app.models.Appointment
import com.medproyect.app.provider.AppointmentProvider
import com.medproyect.app.provider.MyserviceProvider

class ServiceAppointmentsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityServiceAppointmentsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
            finish()
        }
        val nameService = intent.getStringExtra("name_service").toString()
        val idService = intent.getStringExtra("id_service").toString()
        getMyAppointment(idService)
        initReclyclerView()

        binding.nameService.text = nameService
        binding.newFloatingActionButton.setOnClickListener {
            val intent = Intent(this, AddAppointment::class.java)
            intent.putExtra("id_service", idService)
            startActivity(intent)
        }

    }

    private fun initReclyclerView() {
        val manager = GridLayoutManager(this, 1)
        val recyclerView = binding.mycitesRecyclerView
        recyclerView.layoutManager = manager
        recyclerView.adapter = AppointmentAdapter(AppointmentProvider.AppointmentList)
    }

    private fun getMyAppointment(idService:String) {
        val recyclerView =binding.mycitesRecyclerView
        AppointmentProvider.AppointmentList.clear()
        db.collection("appointments")
            .whereEqualTo("serviceID", idService)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                AppointmentProvider.AppointmentList.clear()
                for (doc in value!!) {
                    this.runOnUiThread {
                AppointmentProvider.AppointmentList.add(
                    Appointment(
                            doc.data.get("itemDate") as Timestamp,
                            (doc.data?.get("duration") as String).toInt(),
                            doc.data.get("isAvailable") as Boolean,
                            doc.data.get("nameSpecialist") as String,
                            doc.data.get("photoSpecialist") as String,
                            doc.data.get("itemPrice") as String,
                            doc.id
                            )
                )
                recyclerView.adapter?.notifyDataSetChanged()
                }

                    }
                }
            }
    }







