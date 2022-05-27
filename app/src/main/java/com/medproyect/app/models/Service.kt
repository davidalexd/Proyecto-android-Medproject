package com.medproyect.app.models

import com.google.firebase.firestore.ServerTimestamp

import java.util.*

data class Service(val id:String,
                   val  name:String,
                   @ServerTimestamp
                   val date: Date?=null,
                   val description:String,
                   val rank:Int,
                   val photo:String)