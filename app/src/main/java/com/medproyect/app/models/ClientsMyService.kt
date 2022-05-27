package com.medproyect.app.models

import com.google.firebase.Timestamp

data class  ClientsMyService (
    val nameClient : String,
    val payment : String,
    val photoClient : String,
    val nameService:String,
    val datePayment:Timestamp
    )
