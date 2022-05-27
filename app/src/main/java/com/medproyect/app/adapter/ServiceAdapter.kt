package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.Service

class ServiceAdapter(private val serviceList:List<Service>): RecyclerView.Adapter<ServiceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ServiceViewHolder(layoutInflater.inflate(R.layout.item_service,parent,false))
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        var item = serviceList[position]

        holder.render(item)
    }

    override fun getItemCount(): Int = serviceList.size
}