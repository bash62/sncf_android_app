package com.example.tp_sncf.entities

import android.os.Parcel
import android.os.Parcelable


class Train(
    private val num: String,
    private var from: Stop? = null,
    private var to: Stop? = null,
    private val localHour: String,
    private val localMinute: String,
    private var Stops: ArrayList<Stop> = ArrayList<Stop>(),
    private val type: TypeTrain,
    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Stop::class.java.classLoader),
        parcel.readParcelable(Stop::class.java.classLoader),
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("Stops"),
        TypeTrain.valueOf(parcel.readString()!!)
    ) {
    }

    override fun toString(): String {

        return "$localHour"+"h"+"$localMinute - " + to?.getStation()?.getName() + "\n $num - $type" ;
    }


    fun addStop(stop: Stop, departureStation: Boolean, arrivalStation: Boolean) {
        if (departureStation) {
            from = stop
        } else if (arrivalStation) {
            to = stop
        }
        Stops.add(stop)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num)
        parcel.writeParcelable(from, flags)
        parcel.writeParcelable(to, flags)
        parcel.writeString(localHour)
        parcel.writeString(localMinute)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Train> {
        override fun createFromParcel(parcel: Parcel): Train {
            return Train(parcel)
        }

        override fun newArray(size: Int): Array<Train?> {
            return arrayOfNulls(size)
        }
    }
}