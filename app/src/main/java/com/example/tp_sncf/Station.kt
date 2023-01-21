package com.example.tp_sncf



class Station (
    //CODE_UIC;LIBELLE;long;lat
    val codeUic: Int,
    val libelle: String,
    val long: Float,
    val lat: Float,
        ){

    override fun toString(): String {
        return libelle;
        //return codeUic.toString()+"-"+libelle+"-"+long.toString()+"-"+lat.toString()
    }
}


