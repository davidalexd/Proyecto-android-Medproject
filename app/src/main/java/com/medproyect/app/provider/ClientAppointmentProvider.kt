package com.medproyect.app.provider

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.models.Appointment
import com.medproyect.app.models.ClientAppointment
import com.medproyect.app.models.Service

class ClientAppointmentProvider {

    companion object{
        val clientAppointments = mutableListOf<ClientAppointment>()
    }
}