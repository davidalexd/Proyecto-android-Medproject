package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.ClientsMyService

class ClientsMyServiceAdapter(private val clientsMyServiceList:List<ClientsMyService>): RecyclerView.Adapter<ClientsMyServiceViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsMyServiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientsMyServiceViewHolder(layoutInflater.inflate(R.layout.item_clients,parent,false))
    }

    override fun onBindViewHolder(holder: ClientsMyServiceViewHolder, position: Int) {
        val item = clientsMyServiceList[position]
        holder.render(item)

    }

    override fun getItemCount(): Int = clientsMyServiceList.size

}
