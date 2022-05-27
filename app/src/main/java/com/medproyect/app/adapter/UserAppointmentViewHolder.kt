package com.medproyect.app.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.databinding.ItemUserAppointmentBinding
import com.medproyect.app.models.UserAppointment
import java.text.SimpleDateFormat
import java.util.*

class UserAppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
    val binding = ItemUserAppointmentBinding.bind(view)
    private val simpleDateFormat = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es","ES"))
    fun render(userAppointment: UserAppointment, parent: ViewGroup,isLimited : Boolean){
        val layoutParams = binding.itemUserAppointment.layoutParams as RecyclerView.LayoutParams
        layoutParams.marginEnd = if(isLimited) 20 else 0
        layoutParams.bottomMargin = if(isLimited) 0 else 30
        layoutParams.width = if(isLimited) (parent.measuredWidth * 0.8).toInt() else ViewGroup.LayoutParams.MATCH_PARENT
        val datetime = userAppointment.appointmentDate.toDate()
        val datetimeTimestamp=userAppointment.appointmentDate.seconds * 1000L
        val datetimeEnd=userAppointment.duration*24*60*60*1000L
        val datetimeTimestampEnd= datetimeTimestamp + datetimeEnd
        val DayLeft=(datetimeTimestamp-Date().time)/(1000L*60*60*24)
        val DayEnd =(datetimeTimestampEnd -Date().time)/(1000L*60*60*24)
        binding.tvNumberDay.text = SimpleDateFormat("d").format(datetime)
        binding.tvWeekDay.text = SimpleDateFormat("EEE",Locale("es","ES")).format(datetime)
        binding.tvTime.text = SimpleDateFormat("hh:mm a").format(datetime)
        binding.tvUserInCharge.text=userAppointment.userInCharge
        binding.itemUserAppointment.setOnClickListener { openTicket(userAppointment.id,parent.context) }

        if(Date().time<datetimeTimestamp){
            binding.cardUserCites.setCardBackgroundColor(Color.parseColor("#0A11EF"))
            binding.cardDayUserCites.setCardBackgroundColor(Color.parseColor("#1E228F"))
            binding.timeState.text="Cita inicia en ${DayLeft} dias"
        }
        if(datetimeTimestamp<Date().time &&  Date().time<datetimeTimestampEnd){
            binding.cardUserCites.setCardBackgroundColor(Color.parseColor("#F0FF00"))
            binding.cardDayUserCites.setCardBackgroundColor(Color.parseColor("#BDC816"))
            binding.timeState.text="Cita Finaliza en ${DayEnd} dias"
        }
        if(Date().time>datetimeTimestampEnd){
            binding.cardUserCites.setCardBackgroundColor(Color.parseColor("#EF0A0A"))
            binding.cardDayUserCites.setCardBackgroundColor(Color.parseColor("#AD2020"))
            binding.timeState.text="Finalizada el ${simpleDateFormat.format(datetimeTimestampEnd)}"
        }

    }

    private fun openTicket(id: String, context: Context) {
        val ticketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("app://ticket-activity?id=${id}"))
        context.startActivity(ticketIntent)
    }
}