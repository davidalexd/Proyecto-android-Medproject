package com.medproyect.app.adapter

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.medproyect.app.ServiceBookingActivity
import com.medproyect.app.databinding.ItemClientAppointmentBinding
import com.medproyect.app.models.ClientAppointment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ClientAppointmentViewHolder(view : View) : RecyclerView.ViewHolder(view){

    private val binding = ItemClientAppointmentBinding.bind(view)

    fun render(clientAppointment: ClientAppointment){
        val datetime = clientAppointment.time.toDate()
        binding.tvTime.text = SimpleDateFormat("hh:mm a").format(datetime)
        binding.tvDate.text = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es","ES"))
                            .format(datetime)
        binding.tvPersonName.text = clientAppointment.name
        binding.tvPrice.text = "S/.${clientAppointment.price}"
        binding.tvDuration.text= "${clientAppointment.duration} dias"
        Glide.with(binding.ivImage.context).load(clientAppointment.photo).into(binding.ivImage)

        binding.itemClientAppointment.setOnClickListener {
            if(clientAppointment.isOwner){
               Toast.makeText( binding.itemClientAppointment.context,"No puedes comprar tu servicio",Toast.LENGTH_LONG).show()
            }else{
                openBooking(clientAppointment)
            }
        }
    }

    private fun openBooking(data : ClientAppointment){
        val context = binding.itemClientAppointment.context
        val intent = Intent(context,ServiceBookingActivity::class.java)
        intent.putExtra("appointment_id",data.id )
        intent.putExtra("datetime",SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es","ES"))
                                        .format(data.time.toDate()))
        intent.putExtra("price",data.price)
        intent.putExtra("serviceName",data.service)
        context.startActivity(intent)
    }
}