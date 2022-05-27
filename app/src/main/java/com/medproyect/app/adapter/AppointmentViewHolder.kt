package com.medproyect.app.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medproyect.app.EditAppointmentActivity
import com.medproyect.app.databinding.ItemCitesBinding
import com.medproyect.app.models.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemCitesBinding.bind(view)
    private val simpleDateFormat = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es","ES"))
    private val simpleDateFormatTime =SimpleDateFormat("hh:mm a")
    fun render(appointmentModel: Appointment){
        binding.tvTime.text = simpleDateFormatTime.format(appointmentModel.date.seconds * 1000L)
        binding.btnreschedule.visibility = View.INVISIBLE;
        binding.tvPricecite.text=appointmentModel.itemPrice
        binding.tvdate.text =simpleDateFormat.format(appointmentModel.date.seconds * 1000L)
        binding.tvspecialist.text = appointmentModel.specialistname
        Glide.with(binding.ivImgSpecial.context).load(appointmentModel.photospecialist).into(binding.ivImgSpecial)

        if(appointmentModel.status){

            binding.tvStatuscite.text="Disponible"
            if(appointmentModel.date.seconds * 1000L<Date().time){
                binding.btnreschedule.visibility = View.VISIBLE;
                binding.tvStatuscite.text="Cita vencida"
                binding.btnreschedule.setOnClickListener{
                    val intent = Intent( binding.btnreschedule.context, EditAppointmentActivity::class.java)
                    intent.putExtra("id_appointment",appointmentModel.key)
                    binding.btnreschedule.context.startActivity(intent)
                }
            }
        }else{
            var duration=appointmentModel.durationService*24*60*60*1000L
            var EndDate= appointmentModel.date.seconds * 1000L+ duration
            binding.tvStatuscite.text="Ocupado finaliza el ${simpleDateFormat.format(EndDate)}"

            if(Date().time>EndDate){
                binding.btnreschedule.visibility = View.VISIBLE;
                binding.tvStatuscite.text="Citas finalizada con el cliente"
                binding.btnreschedule.setOnClickListener{
                    val intent = Intent( binding.btnreschedule.context, EditAppointmentActivity::class.java)
                    intent.putExtra("id_appointment",appointmentModel.key)
                    binding.btnreschedule.context.startActivity(intent)
                }
            }
        }


    }
}



