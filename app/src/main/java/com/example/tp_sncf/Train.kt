package com.example.tp_sncf


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