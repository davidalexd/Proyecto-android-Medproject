package com.medproyect.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.databinding.ActivityServiceBookingBinding
import com.medproyect.app.databinding.ActivityServiceDetailsBinding
import com.medproyect.app.models.ClientAppointment
import java.text.SimpleDateFormat

class ServiceBookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServiceBookingBinding
    private lateinit var appointmentId : String
    private lateinit var datetime : String
    private lateinit var price : String
    private lateinit var serviceName : String
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
            finish()
        }
        appointmentId = intent.getStringExtra("appointment_id").toString()
        datetime = intent.getStringExtra("datetime").toString()
        price = intent.getStringExtra("price").toString()
        serviceName = intent.getStringExtra("serviceName").toString()
        getDataAppointment()
        binding.btnCheckout.setOnClickListener{ validateText()}
    }
    private fun validateText(){
        if (binding.etDistrict.text.isEmpty()){
            return Toast.makeText(binding.etDistrict.context, "La direccion no  puede  estar vacio", Toast.LENGTH_SHORT).show()
        }
        openCheckout()
        //binding.etDate.setText(datetime)
    }

    private fun getDataAppointment(){
        val docAppointment = db.collection("appointments").document(appointmentId)
        docAppointment.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.etServiceName.setText(serviceName)
                    binding.etSpecialistName.setText(document.data?.get("nameSpecialist") as String)
                    binding.etDate.setText(datetime)
                    binding.etDuration.setText(document.data?.get("duration") as String)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    private fun openCheckout(){
        val intent = Intent(this,CheckoutActivity::class.java)
        val data = hashMapOf<String,String?>(
            "nameSpecialist" to binding.etSpecialistName.text.toString(),
            "serviceName" to serviceName,
            "appointment" to appointmentId,
            "datetime" to datetime,
            "direccion" to binding.etDistrict.text.toString(),
            "price" to price,
        )
        intent.putExtra("data",data)
        startActivity(intent)
    }
}