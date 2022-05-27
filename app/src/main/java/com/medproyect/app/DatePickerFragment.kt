package com.medproyect.app

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

class DatePickerFragment(val listener: (day:Int,month:Int,year:Int)->Unit):DialogFragment(),DatePickerDialog.OnDateSetListener{
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth,month,year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
     val c =Calendar.getInstance()
        val day=c.get(Calendar.DAY_OF_MONTH)
        val month =c.get(Calendar.MONTH)
        val year =c.get(Calendar.YEAR)
        val picker = DatePickerDialog(activity as Context,this,year,month,day)
        picker.datePicker.minDate= c.timeInMillis
        return  picker
    }
}