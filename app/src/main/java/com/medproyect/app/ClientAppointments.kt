package com.medproyect.app

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.medproyect.app.adapter.ClientAppointmentAdapter
import com.medproyect.app.databinding.ActivityClientAppointmentsBinding
import com.medproyect.app.models.ClientAppointment
import com.medproyect.app.provider.ClientAppointmentProvider
import java.util.*

class ClientAppointments : AppCompatActivity() {
    private lateinit var binding: ActivityClientAppointmentsBinding
    private val  user= Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView : RecyclerView
    private lateinit var serviceId : String
    private lateinit var serviceName : String
    private var Urlsearch:String ="https://media.discordapp.net/attachments/873053673255735346/979008135329095700/giphy.gif"
    private var UrlNoSearch:String ="https://media.discordapp.net/attachments/873053673255735346/979011460426510406/giphy_2.gif"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
            finish()
        }
        recyclerView = binding.rvClientAppointments
        serviceId = intent.getStringExtra("serviceId").toString()
        serviceName = intent.getStringExtra("serviceName").toString()
        initRecyclerView()
    }
    private fun initRecyclerView(){
        fillData()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ClientAppointmentAdapter(ClientAppointmentProvider.clientAppointments)
    }

    private fun fillData() {
        Glide.with(binding.imageLoader.context).load(Urlsearch).into(binding.imageLoader)
        ClientAppointmentProvider.clientAppointments.clear()
        db.collection("appointments")
            //.whereGreaterThan("itemDate",Timestamp.now().toDate())
            .whereEqualTo("serviceID",serviceId)
            .whereEqualTo("isAvailable",true)
            .addSnapshotListener{ snapshot,e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    if(snapshot.isEmpty){
                        Glide.with(binding.imageLoader.context).load(UrlNoSearch).into(binding.imageLoader)
                        //binding.tvEmptyMessage.visibility = GONE
                    }else{
                        this.runOnUiThread {
                            ClientAppointmentProvider.clientAppointments.clear()
                            val result = snapshot.filter{ item -> (item.data["itemDate"] as Timestamp).seconds * 1000L > Date().time}
                            for(doc in result){
                                val docService = db.collection("service").document(doc.data["serviceID"].toString())
                                docService.get()
                                    .addOnSuccessListener { service ->
                                        if (service != null) {
                                            binding.imageLoader.visibility = GONE
                                            ClientAppointmentProvider.clientAppointments.add(
                                                ClientAppointment(
                                                    doc.id,
                                                    serviceName,
                                                    doc.data["itemDate"] as Timestamp,
                                                    doc.data["nameSpecialist"] as String,
                                                    doc.data["photoSpecialist"] as String,
                                                    doc.data["itemPrice"] as String,
                                                    doc.data["duration"] as String,
                                                    user.uid == service.data?.get("idUser"))
                                            )
                                            recyclerView.adapter?.notifyDataSetChanged()
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
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }
}