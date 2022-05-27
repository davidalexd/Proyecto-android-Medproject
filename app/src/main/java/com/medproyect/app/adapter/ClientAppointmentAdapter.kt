package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.ClientAppointment

class ClientAppointmentAdapter(private val clientAppointments : MutableList<ClientAppointment>) : RecyclerView.Adapter<ClientAppointmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientAppointmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientAppointmentViewHolder(layoutInflater.inflate(R.layout.item_client_appointment,parent,false))
    }

    override fun onBindViewHolder(holder: ClientAppointmentViewHolder, position: Int) {
        val item = clientAppointments[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = clientAppointments.size
}