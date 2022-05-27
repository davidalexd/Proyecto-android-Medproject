package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.MyServices


class MyServiceAdapter(private val myServiceList:List<MyServices>): RecyclerView.Adapter<MyServiceViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyServiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyServiceViewHolder(layoutInflater.inflate(R.layout.myservices_content,parent,false))
    }

    override fun onBindViewHolder(holder: MyServiceViewHolder, position: Int) {
        val item = myServiceList[position]
        holder.render(item)

    }

    override fun getItemCount(): Int = myServiceList.size

}