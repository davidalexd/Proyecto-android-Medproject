package com.medproyect.app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medproyect.app.databinding.ItemClientsBinding
import com.medproyect.app.models.ClientsMyService
import java.text.SimpleDateFormat

class ClientsMyServiceViewHolder(view: View): RecyclerView.ViewHolder(view){
    val binding = ItemClientsBinding.bind(view)
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    fun render(clientsMyServiceModel: ClientsMyService){

        binding.tvnameClient.text = clientsMyServiceModel.nameClient
        binding.datePayment.text=simpleDateFormat.format(clientsMyServiceModel.datePayment.seconds * 1000L).toString()
        binding.nameService.text=clientsMyServiceModel.nameService
        binding.tvPayment.text =clientsMyServiceModel.payment
        Glide.with(binding.ivProfileClient.context).load(clientsMyServiceModel.photoClient).into(binding.ivProfileClient)

    }


}
