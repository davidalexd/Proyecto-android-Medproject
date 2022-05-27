package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.ImagesService

class ImagesServiceAdapter(private val imagesServiceList:List<ImagesService>):RecyclerView.Adapter<ImagesServiceViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesServiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ImagesServiceViewHolder(layoutInflater.inflate(R.layout.item_photo,parent,false))
    }

    override fun onBindViewHolder(holder: ImagesServiceViewHolder, position: Int) {
        val item = imagesServiceList[position]
        holder.render(item)
    }
    override fun getItemCount(): Int = imagesServiceList.size

}