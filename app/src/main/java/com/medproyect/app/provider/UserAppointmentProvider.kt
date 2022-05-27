package com.medproyect.app.provider

import com.medproyect.app.models.UserAppointment

class UserAppointmentProvider {
    companion object {
        val userAppointments = mutableListOf<UserAppointment>()
    }
}