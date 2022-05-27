package com.medproyect.app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medproyect.app.databinding.ItemAutoresBinding
import com.medproyect.app.models.AutoresAppointment


class AutoresAppointmentViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemAutoresBinding.bind(view)
    fun render(autoresAppointmentModel: AutoresAppointment){
        Glide.with(binding.ivAutor.context).load(autoresAppointmentModel.photo).into(binding.ivAutor)
        binding.tvNameSpecialist.text=autoresAppointmentModel.name
        binding.tvNameService.text=autoresAppointmentModel.nameService
    }
}


