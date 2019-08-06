package com.example.workhourstracker

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workhourstracker.Model.DailyEntry
import com.example.workhourstracker.Model.SingleEntry
import com.github.clans.fab.FloatingActionButton
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment(val toolbar: Toolbar): Fragment(), CompactCalendarView.CompactCalendarViewListener{


    /*Calendar Formatting*/
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.CANADA)
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.CANADA)
    /*Calendar Formatting*/

    /*All Entries For Current Month*/
    private var thisMonthData = TreeMap<Int,SingleEntry>()
    /*All Entries For Current Month*/


    lateinit var selectedDate: Calendar
    private var monthCurrentlyViewing: String = ""
    private var adapter = GroupAdapter<ViewHolder>()
    
    /*COMPONENTS*/
    lateinit var compactcalendar_view: CompactCalendarView

    lateinit var textCurrentDate: TextView

    lateinit var btnAddEntry: FloatingActionButton

    lateinit var recylerView: RecyclerView

    lateinit var calendarLogo: ImageView
    lateinit var noEntry: TextView
    /*COMPONENTS*/
    
    companion object{
        val TAG = "MainActivity"
        val FILENAME = "MonthlyData"
        val EDITENTRY = "EditEntry"
        val DATESELECTED = "DateSelected"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        toolbar.title = "Calendar"
        val view: View = inflater.inflate(R.layout.fragment_calendar,container,false)

        initializeComponents(view)

        loadData()
        setUpCalenderFragment()
        listenForAddEntry()

        return view
    }

    override fun onStart() {
        super.onStart()
        loadData()
        setUpCalenderFragment()
    }



    /*Initializing All The Components*/
    private fun initializeComponents(view: View) {
        selectedDate = Calendar.getInstance()

        btnAddEntry = view.AddEntry
        compactcalendar_view = view.compactcalendar_view
        textCurrentDate = view.TextViewCurrentDate
        noEntry = view.TextViewNoEntryTitle
        calendarLogo = view.CalenderLogo
        recylerView = view.RecylerViewEntry
    }
    /*Initializing All The Components*/



    /*Setup The Screen*/
    private fun setUpAdapter(){
        recylerView.layoutManager = LinearLayoutManager(context)
        recylerView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->

            val entry = item as DailyEntry

            val intent = Intent(view.context, EntryActivity::class.java)
            intent.putExtra(EDITENTRY,entry.entryData)
            startActivity(intent)
        }
    }
    private fun setUpCalenderFragment() {

        compactcalendar_view.setUseThreeLetterAbbreviation(true)
        compactcalendar_view.setCurrentSelectedDayBackgroundColor(Color.parseColor("#3cbeb2"))
        compactcalendar_view.setCurrentSelectedDayTextColor(Color.BLACK)
        compactcalendar_view.setCurrentSelectedDayIndicatorStyle(CompactCalendarView.NO_FILL_LARGE_INDICATOR)
        compactcalendar_view.setListener(this)

        selectedDate = Calendar.getInstance()
        toolbar.title = monthFormat.format(selectedDate.getTime())
        textCurrentDate.text = dateFormat.format(selectedDate.getTime())

        if(compactcalendar_view.getEvents(selectedDate.time).size > 0){
            adapter.clear()
            adapter.add(DailyEntry(thisMonthData.get(selectedDate.get(Calendar.DATE))!!))
            hideNoEntryCurrentScreen()
        }else{
            adapter.clear()
            showNoEntryCurrentScreen()
        }

        monthCurrentlyViewing = SimpleDateFormat("MMMM/yyyy").format(selectedDate.getTime())

        setUpAdapter()
    }
    /*Setup The Screen*/




    /*Loading All The Data*/
    private fun doesDataFileExist(): Boolean {
        val sharedPreferences: SharedPreferences = context?.getSharedPreferences(
            EntryActivity.FILENAME,
            AppCompatActivity.MODE_PRIVATE
        )!!
        if (sharedPreferences == null)
            return false

        return true
    }

    private fun isMonthDataInFile(key: String): Boolean {
        if (!doesDataFileExist())
            return false

        val sharedPreferences: SharedPreferences = context?.getSharedPreferences(
            EntryActivity.FILENAME,
            AppCompatActivity.MODE_PRIVATE
        )!!
        val getMonthData = sharedPreferences.getString(key, null)
        if (getMonthData == null)
            return false

        return true
    }

    private fun loadData(){
        val month_key = SimpleDateFormat("MMMM/yyyy").format(selectedDate.time)

        if (isMonthDataInFile(month_key)) {
            val sharedPreferences: SharedPreferences =
                context?.getSharedPreferences(DashboardFragment.FILENAME, AppCompatActivity.MODE_PRIVATE)!!
            val gson = Gson()
            val jsonText = sharedPreferences.getString(month_key, null)
            val dataType = object : TypeToken<TreeMap<Int, SingleEntry>>() {}.getType()
            thisMonthData = gson.fromJson<TreeMap<Int, SingleEntry>>(jsonText, dataType)

            loadEvents()
        }
    }

    private fun loadEvents() {
        compactcalendar_view.removeAllEvents()

        for(key in thisMonthData.keys){
            val eventDate = thisMonthData.get(key)?.shiftStart!!
            val timeInMillis = getMillis(eventDate)

            Log.d(TAG,"Date: ${dateFormat.format(eventDate.time)} Time Millis: ${timeInMillis}")

            val event = Event(Color.parseColor("#3cbeb2"),timeInMillis)
            compactcalendar_view.addEvent(event)
        }
    }
    /*Loading All The Data*/


    /*Listen For Click Events*/
    private fun listenForAddEntry(){
        btnAddEntry.setOnClickListener {
            val intent = Intent(context,EntryActivity::class.java)
            intent.putExtra(DATESELECTED,selectedDate)
            startActivity(intent)
        }
    }
    /*Listen For Click Events*/



    private fun showNoEntryCurrentScreen(){
        calendarLogo.visibility = ImageView.VISIBLE
        noEntry.visibility = TextView.VISIBLE
    }
    private fun hideNoEntryCurrentScreen(){
        calendarLogo.visibility = ImageView.INVISIBLE
        noEntry.visibility = TextView.INVISIBLE
    }


    private fun getMillis(date: Calendar): Long{
        val fullDate = SimpleDateFormat("yyyy/M/dd").format(date.getTime()) + " 12:00:00"
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date: Date = format.parse(fullDate)
        val millis = date.getTime()

        return millis
    }



    /*Interaction With The Calendar*/
    override fun onDayClick(dateClicked: Date?) {
        compactcalendar_view.setCurrentDayTextColor(Color.parseColor("#3cbeb2"))
        compactcalendar_view.setCurrentDayBackgroundColor(Color.TRANSPARENT)

        compactcalendar_view.setCurrentSelectedDayBackgroundColor(Color.parseColor("#3cbeb2"))
        compactcalendar_view.setCurrentSelectedDayTextColor(Color.BLACK)
        compactcalendar_view.setCurrentSelectedDayIndicatorStyle(CompactCalendarView.NO_FILL_LARGE_INDICATOR)

        selectedDate.setTime(dateClicked)

        if(compactcalendar_view.getEvents(selectedDate.time).size > 0){
            adapter.clear()
            adapter.add(DailyEntry(thisMonthData.get(dateClicked?.date)!!))
            hideNoEntryCurrentScreen()
        }else{
            adapter.clear()
            showNoEntryCurrentScreen()
        }

    }

    override fun onMonthScroll(firstDayOfNewMonth: Date?) {

        toolbar.title = monthFormat.format(firstDayOfNewMonth?.getTime())
        compactcalendar_view.setCurrentSelectedDayTextColor(Color.BLACK)
        compactcalendar_view.setCurrentSelectedDayBackgroundColor(Color.TRANSPARENT)
    }
    /*Interaction With The Calendar*/

}