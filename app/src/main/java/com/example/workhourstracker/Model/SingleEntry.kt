package com.example.workhourstracker.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class SingleEntry(
    val payRate: Double,
    val shiftStart: Calendar,
    val shiftEnd: Calendar,
    val breaks: Time,
    val paidBreak: Boolean,
    val overtime: Time,
    val shiftPaid: Boolean
): Parcelable {


    override fun toString(): String {
        val format = SimpleDateFormat("MMMM dd,yyyy h:mm a")

        val result =
            "\nPayrate: $payRate" +
                    "\nShift Start: ${format.format(shiftStart.time)}" +
                    "\nShift End: ${format.format(shiftEnd.time)}" +
                    "\nBreaks: ${breaks.hour}:${breaks.minute}" +
                    "\nPaid Break: $paidBreak" +
                    "\nOvertime: ${overtime.hour}:${overtime.minute}" +
                    "\nPaid: $shiftPaid"

        return result
    }

    fun getshiftStart(): Calendar {
        return shiftStart
    }

    fun getshiftEnd(): Calendar {
        return shiftEnd
    }

    fun getBreak(): Time {
        return breaks
    }

    fun getovertime():Time {
        return overtime
    }


    fun getEarned(): Double {
        var format = DecimalFormat("##.##")
        var workedHoursObject:Time? = null

        if(paidBreak){
            workedHoursObject = getShiftHour()
        }else{
            workedHoursObject = getHoursWorked()
        }

        var overtimeWorked = overtime.hour + overtime.minute/60.0
        val hoursWorked = workedHoursObject.hour + (workedHoursObject.minute / 60.0)
        val result = format.format((hoursWorked * payRate) + (overtimeWorked*payRate*2))


        return result.toString().toDouble()
    }

    //WITH BREAKS
    fun getHoursWorked(): Time {
        val diff = Math.abs(shiftEnd.time.time - shiftStart.time.time)
        var minutes = diff / 60000

        val days = (minutes / 1440)
        minutes -= days * 1440

        var hours = (minutes / 60)
        minutes -= hours * 60

        hours -= breaks.hour

        if (minutes >= breaks.minute) {
            minutes -= breaks.minute
        } else {
            hours--
            minutes = 60 + (minutes - breaks.minute)
        }

        return Time(Math.toIntExact(hours), Math.toIntExact(minutes))
    }

    fun getShiftHour(): Time {
        val diff = Math.abs(shiftEnd.time.time - shiftStart.time.time)
        var minutes = diff / 60000

        val days = (minutes / 1440)
        minutes -= days * 1440

        var hours = (minutes / 60)
        minutes -= hours * 60

        return Time(Math.toIntExact(hours), Math.toIntExact(minutes))
    }
}