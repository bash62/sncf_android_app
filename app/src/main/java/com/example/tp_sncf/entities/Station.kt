package com.example.tp_sncf.entities

import android.os.Parcel
import android.os.Parcelable


class Station (

    private val name: String,
    private val lon: Double,
    private val lat: Double,
    private val codeUIC: Int,
        ): Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt()
    ) {
    }

    fun getName(): String {
        return name
    }
    fun getCodeUIC(): Int {
        return codeUIC
    }
    override fun toString(): String {
        return name;

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(lon)
        parcel.writeDouble(lat)
        parcel.writeInt(codeUIC)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }
}


