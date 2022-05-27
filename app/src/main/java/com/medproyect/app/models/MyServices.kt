package com.medproyect.app.models

import com.google.firebase.Timestamp
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MyServices(val  id_user:String, val name:String, val photo:String,val category:String,val description:String, val  cites: Int, @Exclude val key: String? = null){
}
