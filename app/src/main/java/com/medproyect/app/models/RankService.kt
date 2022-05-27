package com.medproyect.app.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class RankService(val id:String, val  name:String, val description:String, val photo:String, @ServerTimestamp
val date: Date?=null,val photoSpecialist:String,val nameSpecialist:String)
