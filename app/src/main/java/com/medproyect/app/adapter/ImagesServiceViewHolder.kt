package com.medproyect.app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.medproyect.app.databinding.ItemPhotoBinding
import com.medproyect.app.models.ImagesService


class ImagesServiceViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemPhotoBinding.bind(view)
    fun render(ImagesServiceModel: ImagesService){
        Glide.with(binding.ivPhotosServices.context)
            .load(ImagesServiceModel.imageUrl).centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivPhotosServices)


    }

}
