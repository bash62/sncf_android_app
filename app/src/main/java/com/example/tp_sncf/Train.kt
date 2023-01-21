package com.example.tp_sncf

import okhttp3.*
import java.io.IOException
import java.sql.DriverManager
import java.sql.DriverManager.println
import java.sql.Timestamp
import java.time.Instant
import java.util.*

//"pagination":{"start_page":0,"items_on_page":10,"items_per_page":10,"total_result":10},"links":[{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/stop_points\/{stop_point.id}","type":"stop_point","rel":"stop_points","templated":true},{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/commercial_modes\/{commercial_modes.id}","type":"commercial_modes","rel":"commercial_modes","templated":true},{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/stop_areas\/{stop_area.id}","type":"stop_area","rel":"stop_areas","templated":true},{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/physical_modes\/{physical_modes.id}","type":"physical_modes","rel":"physical_modes","templated":true},{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/disruptions\/{disruptions.id}","type":"disruptions","rel":"disruptions","templated":true},{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/routes\/{route.id}","type":"route","rel":"routes","templated":true},{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/physical_modes\/{physical_mode.id}","type":"physical_mode","rel":"physical_modes","templated":true},{"href":"https:\/\/api.sncf.com\/v1\/coverage\/sncf\/commercial_modes\/{commercial_mode.id}",

class Train(
    //numéro, destination ou
    //provenance, heure, minute)
    val num: Int,
    val destination: String,
    // A changer
    val depart: Int,

    ) {

    override fun toString(): String {
        return destination + " - " + num.toString() + " : " + depart.toString()
    }


}