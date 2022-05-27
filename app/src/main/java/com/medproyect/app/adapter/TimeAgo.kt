package com.medproyect.app.adapter

import java.util.concurrent.TimeUnit

private  const val SECOND_MILLIS=1
private  const val MINUTE_MILLIS=60 * SECOND_MILLIS
private  const val HOUR_MILLIS=60 * MINUTE_MILLIS
private  const val DAY_MILLIS= 24 * HOUR_MILLIS



object TimeUtils {
    fun getTimeAgo(time:Int):String{
        val now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        if(time>now || time <=0){
            return "En el futuro"
        }
        val diff = now - time
        return  when {
            diff< MINUTE_MILLIS -> "Justo ahora"
            diff<2 * MINUTE_MILLIS -> "Hace minutos"
            diff<60 * MINUTE_MILLIS -> "Hace ${diff/ MINUTE_MILLIS} minutos"
            diff<2 * HOUR_MILLIS -> "Hace una hora"
            diff<24 * HOUR_MILLIS -> "Hace ${diff/ HOUR_MILLIS} horas"
            diff<48 * HOUR_MILLIS -> "Ayer"
            else ->"${diff/ DAY_MILLIS} Hace dias atras"
        }
    }
}