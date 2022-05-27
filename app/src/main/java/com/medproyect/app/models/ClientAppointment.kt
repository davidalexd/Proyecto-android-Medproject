package com.medproyect.app.models

import com.google.firebase.Timestamp

data class ClientAppointment(
    val id : String,
    val service : String,
    val time : Timestamp,
    val name : String,
    val photo : String,
    val price : String,
    val duration: String,
    val isOwner: Boolean
)
