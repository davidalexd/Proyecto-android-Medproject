package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.RankService


class RankServiceAdapter(private val rankServiceList:List<RankService>): RecyclerView.Adapter<RankServiceViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankServiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RankServiceViewHolder(layoutInflater.inflate(R.layout.item_rankserv,parent,false))
    }

    override fun onBindViewHolder(holder: RankServiceViewHolder, position: Int) {
        val item = rankServiceList[position]
        holder.render(item)

    }

    override fun getItemCount(): Int = rankServiceList.size

}