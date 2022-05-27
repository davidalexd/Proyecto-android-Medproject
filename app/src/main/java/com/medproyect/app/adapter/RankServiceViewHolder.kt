package com.medproyect.app.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medproyect.app.databinding.ItemRankservBinding
import com.medproyect.app.models.RankService
import com.medproyect.app.serviceDetailsActivity

class RankServiceViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemRankservBinding.bind(view)

    fun render(RankServiceModel: RankService){
        val createAd = (RankServiceModel.date?.time?.div(1000L))?.let{
            TimeUtils.getTimeAgo(it.toInt())
        }
        binding.tvServiceName.text = RankServiceModel.name
        binding.tvLastPurchase.text= createAd.toString()
        binding.tvNameSpecialist.text=RankServiceModel.nameSpecialist
        Glide.with(binding.ivService.context).load(RankServiceModel.photo).into(binding.ivService)
        Glide.with(binding.ivSpecialist.context).load(RankServiceModel.photoSpecialist).into(binding.ivSpecialist)
        binding.ivService.setOnClickListener{
            val intent = Intent(binding.ivService.context, serviceDetailsActivity::class.java)
            intent.putExtra("idService",RankServiceModel.id)
            binding.ivService.context.startActivity(intent)
        }
        //itemView.setOnClickListener{
        //   Toast.makeText(binding.ivSuperHero.context,categoryModel.name,Toast.LENGTH_SHORT).show()
        //}
    }

}