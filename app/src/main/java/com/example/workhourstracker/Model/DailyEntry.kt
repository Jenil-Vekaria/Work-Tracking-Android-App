package com.example.workhourstracker.Model

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.example.workhourstracker.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.daily_entry.view.*
import java.text.SimpleDateFormat

class DailyEntry(val entryData: SingleEntry) : Item<ViewHolder>() {

    private var colors = arrayOf("#64DD17", "#FF6D00", "#DD2C00")
    var addPadding = false
    override fun getLayout(): Int {
        return R.layout.daily_entry
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val timeFormat = SimpleDateFormat("h:mm a")

        //Date
        viewHolder.itemView.Date.text = SimpleDateFormat("MMMM dd, yyyy").format(entryData.shiftStart.getTime())

        //Shift Start and End
        viewHolder.itemView.Starttime.text = timeFormat.format(entryData.getshiftStart().getTime())
        viewHolder.itemView.Endtime.text = timeFormat.format(entryData.getshiftEnd().getTime())

        //Hours worked
        viewHolder.itemView.HoursWorked.text = entryData.getHoursWorked().toString()

        //Break Time
        viewHolder.itemView.BreakTime.text = entryData.getBreak().toString()

        //Paid Break
        if(entryData.paidBreak){ viewHolder.itemView.PaidBreak.visibility = TextView.VISIBLE}
        else{ viewHolder.itemView.PaidBreak.visibility = TextView.INVISIBLE}

        //Overtime
        viewHolder.itemView.Overtime.text = entryData.getovertime().toString()

        //Earned
        viewHolder.itemView.Earned.text = "$" + entryData.getEarned()

        //Pay Rate
        viewHolder.itemView.PayRate.text = entryData.payRate.toString()+"/hr"

        //Paid Break
        if(entryData.paidBreak){
            viewHolder.itemView.PaidBreak.text = "PAID"
        }else{
            viewHolder.itemView.PaidBreak.text = ""
        }


        //Shift Paid State
        if(entryData.shiftPaid){
            viewHolder.itemView.PaidState.visibility = ImageView.VISIBLE
        }else{
            viewHolder.itemView.PaidState.visibility = ImageView.INVISIBLE
        }

        val hoursWorked = entryData.getHoursWorked()
        var colorIndex = 0

        if (hoursWorked.hour >= 12) {
            colorIndex = 2
        } else if (hoursWorked.hour >= 8) {
            colorIndex = 1
        } else {
            colorIndex = 0
        }

        //Setting Entry Background Color
        viewHolder.itemView.colorLayout.setBackgroundColor(Color.parseColor(colors[colorIndex]))
        viewHolder.itemView.bar.setBackgroundColor(Color.parseColor(colors[colorIndex]))

        if (addPadding) {
            viewHolder.itemView.Layout.setPadding(0, 0, 0, 240)
        } else {
            viewHolder.itemView.Layout.setPadding(0, 0, 0, 0)
        }
    }

    fun addPadding() {
        addPadding = true
    }
    fun removePadding(){
        addPadding = false
    }
}