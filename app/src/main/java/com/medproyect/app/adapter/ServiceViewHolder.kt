package com.medproyect.app.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medproyect.app.databinding.ItemServiceBinding
import com.medproyect.app.models.Service
import com.medproyect.app.serviceDetailsActivity

class ServiceViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemServiceBinding.bind(view)

    fun render(serviceModel:Service){
        binding.tvServiceName.text = serviceModel.name
        val createAd = (serviceModel.date?.time?.div(1000L))?.let{
            TimeUtils.getTimeAgo(it.toInt())
        }

        binding.tvWordService.text = createAd.toString()
        Glide.with(binding.ivService.context).load(serviceModel.photo).into(binding.ivService)
        binding.ivService.setOnClickListener{

            val intent = Intent(binding.ivService.context, serviceDetailsActivity::class.java)

            intent.putExtra("idService",serviceModel.id)
            binding.ivService.context.startActivity(intent)

    }

}
}