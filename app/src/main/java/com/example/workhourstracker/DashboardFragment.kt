package com.example.workhourstracker


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workhourstracker.Model.DailyEntry
import com.example.workhourstracker.Model.SingleEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kal.rackmonthpicker.RackMonthPicker
import com.kal.rackmonthpicker.listener.DateMonthDialogListener
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.daily_entry.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.AddEntry
import kotlinx.android.synthetic.main.fragment_dashboard.view.TextViewNoEntryTitle
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private val adapter = GroupAdapter<ViewHolder>()

    /*COMPONENTS*/
    lateinit var recyclerViewMontlyEnties: RecyclerView

    lateinit var btnAddEntry: FloatingActionButton

    lateinit var selectMonthYear: TextView

    lateinit var calendarLogo: ImageView
    lateinit var textNoEntry: TextView

    /*COMPONENTS*/

    private var monthlyData = TreeMap<Int,SingleEntry>()

    companion object {
        val FILENAME = "MonthlyData"
        val EDITENTRY= "EditEntry"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        init(view)
        loadData()

        changeMonthYear()
        listenForAddEntry()

        recyclerViewMontlyEnties.layoutManager = LinearLayoutManager(context)
        recyclerViewMontlyEnties.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData() {
        adapter.clear()
        val key = selectMonthYear.text.substring(0, selectMonthYear.text.indexOf(" ")) +
                "/" + selectMonthYear.text.substring(selectMonthYear.text.indexOf(" ") + 1)

        if (isMonthDataInFile(key)) {
            val sharedPreferences: SharedPreferences =
                context?.getSharedPreferences(FILENAME, AppCompatActivity.MODE_PRIVATE)!!
            val gson = Gson()
            val jsonText = sharedPreferences.getString(key, null)
            val dataType = object : TypeToken<TreeMap<Int, SingleEntry>>() {}.getType()
            monthlyData = gson.fromJson<TreeMap<Int, SingleEntry>>(jsonText, dataType)

            for (key in monthlyData.keys) {
                val entry = DailyEntry(monthlyData.get(key)!!)

                if (adapter.itemCount >= 6) {
                    entry.addPadding = true

                    val previousItem = adapter.getItem(adapter.itemCount - 1) as DailyEntry
                    adapter.remove(previousItem)
                    previousItem.addPadding = false
                    adapter.add(previousItem)
                }
                adapter.add(entry)
                hideNoEntryTitle()
            }

            adapter.setOnItemClickListener { item, view ->

                val entry = item as DailyEntry

                val intent = Intent(view.context, EntryActivity::class.java)
                intent.putExtra(EDITENTRY,entry.entryData)
                startActivity(intent)
            }

        } else {
            showNoEntryTitle()
        }
    }

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

    private fun showNoEntryTitle() {
        calendarLogo.visibility = ImageView.VISIBLE
        textNoEntry.visibility = TextView.VISIBLE
    }

    private fun hideNoEntryTitle() {
        calendarLogo.visibility = ImageView.INVISIBLE
        textNoEntry.visibility = TextView.INVISIBLE
    }

    private fun init(view: View) {
        recyclerViewMontlyEnties = view.RecylerViewMontlyEntries
        btnAddEntry = view.AddEntry
        selectMonthYear = view.TextViewDashboardMonthYear

        calendarLogo = view.CalenderLogo
        textNoEntry = view.TextViewNoEntryTitle

        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("MMMM yyyy").format(calendar.time)

        selectMonthYear.text = format
    }


    private fun listenForAddEntry() {
        btnAddEntry.setOnClickListener {
            val intent = Intent(context, EntryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changeMonthYear() {
        selectMonthYear.setOnClickListener {
            val picker = RackMonthPicker(context)
            picker.setLocale(Locale.ENGLISH)
                .setPositiveButton(DateMonthDialogListener { month, startDate, endDate, year, monthLabel ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.MONTH, month - 1)
                    calendar.set(Calendar.YEAR, year)
                    val date = SimpleDateFormat("MMMM yyyy").format(calendar.getTime())
                    selectMonthYear.text = date
                    loadData()
                })
                .setNegativeButton(OnCancelMonthDialogListener {
                    it.dismiss()
                }).show()
        }
    }


}