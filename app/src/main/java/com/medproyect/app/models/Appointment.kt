package com.medproyect.app.models

import com.google.firebase.Timestamp
import com.google.firebase.database.Exclude

data class Appointment(val date:Timestamp,val durationService:Int, val  status:Boolean,val specialistname:String,val photospecialist:String,val itemPrice:String, @Exclude val key: String? = null)