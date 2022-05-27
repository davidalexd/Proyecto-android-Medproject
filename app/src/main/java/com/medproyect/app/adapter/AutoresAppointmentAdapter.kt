package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.AutoresAppointment


class AutoresAppointmentAdapter(private val AutoresAppointmentList:List<AutoresAppointment>): RecyclerView.Adapter<AutoresAppointmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoresAppointmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AutoresAppointmentViewHolder(layoutInflater.inflate(R.layout.item_autores,parent,false))
    }
    override fun onBindViewHolder(holder: AutoresAppointmentViewHolder, position: Int) {
        val item = AutoresAppointmentList[position]
        holder.render(item)

    }
    override fun getItemCount(): Int = AutoresAppointmentList.size


}

