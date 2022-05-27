package com.medproyect.app.models

import com.google.firebase.Timestamp

data class UserAppointment(
    val id : String,
    val appointmentDate : Timestamp,
    val appointmentId : String,
    val userInCharge : String,
    val duration: Int,
)
