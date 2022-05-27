package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.models.UserAppointment
import kotlin.properties.Delegates

class UserAppointmentAdapter(private val userAppointmentList:List<UserAppointment>,
                             private val maxSize: Int = userAppointmentList.size,
                             private val isLimited : Boolean = true) : RecyclerView.Adapter<UserAppointmentViewHolder>() {
    private lateinit var parent : ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAppointmentViewHolder {
        this.parent = parent
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserAppointmentViewHolder(layoutInflater.inflate(R.layout.item_user_appointment,parent,false))
    }

    override fun onBindViewHolder(holder: UserAppointmentViewHolder, position: Int) {
        val item = userAppointmentList[position]
        holder.render(item,parent,isLimited)
    }

    override fun getItemCount(): Int {
        return if(userAppointmentList.size >= maxSize) maxSize else userAppointmentList.size
    }
}