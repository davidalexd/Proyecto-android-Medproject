package com.medproyect.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.databinding.ActivityTicketBinding
import java.text.SimpleDateFormat
import java.util.*

class TicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketBinding
    private val db = FirebaseFirestore.getInstance()
    private val ticketData = mutableMapOf<String,String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }
    private fun setup(){
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            val userAppointment = uri!!.getQueryParameter("id")
            Glide.with(binding.ivQr.context).load(getQrUrl(uri.toString())).into(binding.ivQr)
            db.collection("user_appointments")
                .document(userAppointment.toString())
                .get()
                .addOnSuccessListener { res ->
                    val appointmentId = res.data?.get("appointment_id").toString()
                    val userId = res.data?.get("user_id").toString()
                    db.collection("profiles")
                        .whereEqualTo("userID",userId)
                        .get()
                        .addOnSuccessListener { userResult ->
                            val user = userResult.first()
                            val fullName = String.format("%s %s",user.data["firtsName"],user.data["lastName"])
                            binding.tvClientName.text = fullName
                        }
                    db.collection("appointments")
                        .document(appointmentId.toString())
                        .get()
                        .addOnSuccessListener { appointmentResult ->
                            val serviceId = appointmentResult.data?.get("serviceID")
                            val date = (appointmentResult.data?.get("itemDate") as Timestamp).toDate()
                            val formattedDate = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es","ES"))
                                .format(date)
                            binding.tvDate.text = formattedDate
                            db.collection("service")
                                .document(serviceId.toString())
                                .get()
                                .addOnSuccessListener { serviceResult ->
                                    val serviceName = serviceResult.data?.get("itemName").toString()
                                    binding.tvService.text = serviceName
                                    binding.tvDuration.text="${appointmentResult.data?.get("duration").toString()} dias"
                                    binding.tvspecialist.text=appointmentResult.data?.get("nameSpecialist").toString()
                                    binding.tvdireccion.text=res.data?.get("direccion").toString()
                                }
                        }
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, GeneralActivity::class.java))
    }
    private fun getQrUrl(url: String): String {
        return "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=$url"
    }
}