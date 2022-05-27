package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.Appointment


class AppointmentAdapter(private val appointmentList:List<Appointment>): RecyclerView.Adapter<AppointmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AppointmentViewHolder(layoutInflater.inflate(R.layout.item_cites,parent,false))
    }
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val item = appointmentList[position]
        holder.render(item)

    }
    override fun getItemCount(): Int = appointmentList.size


}

