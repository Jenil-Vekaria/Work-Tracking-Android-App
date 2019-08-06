package com.example.workhourstracker.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Time(val hour: Int, val minute: Int): Parcelable {
    override fun toString(): String {
        var result = ""
        if (hour != 0) {
            result += "${hour}h"
        }
        if (minute != 0) {
            result += " ${minute}m"
        }

        if (hour == 0 && minute == 0) {
            return "N/A"
        }
        return result
    }

}