package com.example.tp_sncf

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Train(
    //numéro, destination ou
    //provenance, heure, minute)
    val num: String,
    val destination: String,
    // Date de depart
    val depart: String,

    ) {

    override fun toString(): String {

        val hour = depart.split('T')[1].substring(0,2);
        val minutes = depart.split('T')[1].substring(2,4);

        return "$destination - $num :  $hour:$minutes"
    }


}