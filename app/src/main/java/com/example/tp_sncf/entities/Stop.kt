package com.example.tp_sncf.entities;

import android.os.Parcel
import android.os.Parcelable

class Stop (
        private val hourArrival: String,
        private val minuteArrival: String,
        private val hourDeparture: String,
        private val minuteDeparture: String,
        private var station: Station? = null,
    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Station::class.java.classLoader)
    ) {
    }

    fun getHourArrival(): String {
            return hourArrival
        }

        fun getMinuteArrival(): String {
            return minuteArrival
        }

        fun getHourDeparture(): String {
            return hourDeparture
        }

        fun getMinuteDeparture(): String {
            return minuteDeparture
        }

        fun getStation(): Station? {
            return station
        }

        fun setStation(station: Station?) {
            this.station = station
        }

        override fun toString(): String {
            return "$hourDeparture`h`$minuteDeparture: - $station.getName()"
        }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hourArrival)
        parcel.writeString(minuteArrival)
        parcel.writeString(hourDeparture)
        parcel.writeString(minuteDeparture)
        parcel.writeParcelable(station, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stop> {
        override fun createFromParcel(parcel: Parcel): Stop {
            return Stop(parcel)
        }

        override fun newArray(size: Int): Array<Stop?> {
            return arrayOfNulls(size)
        }
    }

}
