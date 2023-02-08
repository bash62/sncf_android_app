package com.example.tp_sncf.entities

import android.os.Parcel
import android.os.Parcelable


class Train(
    private val num: String,
    private var from: Stop? = null,
    private var to: Stop? = null,
    private val localHour: String,
    private val localMinute: String,
    private val type: TypeTrain,
    ) : Parcelable {

    var stops: ArrayList<Stop> = arrayListOf()

    constructor(parcel: Parcel) : this (
        parcel.readString()!!,
        parcel.readParcelable(Stop::class.java.classLoader),
        parcel.readParcelable(Stop::class.java.classLoader),
        parcel.readString()!!,
        parcel.readString()!!,
        TypeTrain.valueOf(parcel.readString()!!)
    ) {
        parcel.readList(stops, Stop::class.java.classLoader)

    }

    fun getFrom(): Stop? {
        return from
    }

    fun getTo(): Stop? {
        return to
    }
    fun toMapsTextView(): String {
        return  type.toString() + " n°" + num + "\n"+from?.getStation()?.getName() + "-" + to?.getStation()?.getName()
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
        stops.add(stop)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num)
        parcel.writeParcelable(from, flags)
        parcel.writeParcelable(to, flags)
        parcel.writeString(localHour)
        parcel.writeString(localMinute)
        parcel.writeString(type.name)
        parcel.writeList(stops)
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