package com.example.workhourstracker

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.example.workhourstracker.Model.SingleEntry
import com.example.workhourstracker.Model.Time
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_entry.*
import java.text.SimpleDateFormat
import java.util.*

class EntryActivity : AppCompatActivity() {

    /*SELECTED COMPONENTS*/
    private var payRate: Double = 0.0

    private lateinit  var shiftStart: Calendar

    private lateinit var shiftEnd: Calendar

    private var breakTime: Time = Time(0, 0)

    private var paidBreak: Boolean = false

    private var overtime: Time = Time(0, 0)

    private var shiftPaid = false
    /*SELECTED COMPONENTS*/

    private var editMode = false

    companion object {
        const val FILENAME = "MonthlyData"
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        val entry = intent.getParcelableExtra<SingleEntry>(DashboardFragment.EDITENTRY)
        var selectDate = intent.getSerializableExtra(CalendarFragment.DATESELECTED)


        if(entry != null){
            editMode = true
            supportActionBar?.title = "Edit Entry"
            setUpEditMode(entry)
            Log.d(TAG, "EDIT ENTRY\n$entry")
        }else{
            supportActionBar?.title = "Add Entry"
            init()
        }

        if(selectDate != null){
            selectDate = selectDate as Calendar
            StartDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(selectDate.time)
            EndDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(selectDate.time)
            shiftStart = selectDate
            shiftEnd = selectDate
        }

        listenForStartTime()
        listenForStartDate()

        listenForEndTime()
        listenForEndDate()

        listenForBreak()
        listenForPaidBreak()

        listenForOvertime()

        listenForPaidShift()
        listenForSave()
    }

    private fun setUpEditMode(entry: SingleEntry){

        //Pay Rate
        payRate = entry.payRate
        PayRateSelect.setText(entry.payRate.toString())

        //Start Date and Time
        shiftStart = entry.shiftStart
        StartDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftStart.time)
        StartTime.text = SimpleDateFormat("h:mm a", Locale.CANADA).format(shiftStart.time)

        //End Date and Time
        shiftEnd = entry.shiftEnd
        EndDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftEnd.time)
        EndTime.text = SimpleDateFormat("h:mm a", Locale.CANADA).format(shiftEnd.time)

        //Break
        breakTime = entry.breaks
        BreakDuration.text = "${breakTime.hour}h ${breakTime.minute}m"

        //Paid Break
        paidBreak = entry.paidBreak
        paidBreakToggle.setChecked(paidBreak)

        //Overtime
        overtime = entry.overtime
        OvertimeDuration.text = "${overtime.hour}h ${overtime.minute}m"

        //Shift Paid
        shiftPaid = entry.shiftPaid
        if(shiftPaid) {
            btn_paid.setBackgroundColor(Color.parseColor("#DD2C00"))
            btn_paid.setTextColor(Color.WHITE)
        } else {
            btn_paid.setBackgroundColor(Color.WHITE)
            btn_paid.setTextColor(Color.parseColor("#DD2C00"))
        }
    }

    private fun init() {

        shiftStart = Calendar.getInstance()
        shiftStart.set(Calendar.HOUR_OF_DAY, 7)
        shiftStart.set(Calendar.MINUTE, 0)

        StartDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftStart.time)
        StartTime.text = SimpleDateFormat("h:mm a", Locale.CANADA).format(shiftStart.time)

        shiftEnd = Calendar.getInstance()
        shiftEnd.set(Calendar.HOUR_OF_DAY, 19)
        shiftEnd.set(Calendar.MINUTE, 0)

        EndDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftEnd.time)
        EndTime.text = SimpleDateFormat("h:mm a", Locale.CANADA).format(shiftEnd.time)

    }

    /*CLICK EVENT LISTENING*/
    private fun listenForStartDate() {
        StartDate.setOnClickListener {
            var datePickerDialog = DatePickerDialog(this)

            if (::shiftStart.isInitialized) {
                datePickerDialog.updateDate(
                    shiftStart.get(Calendar.YEAR),
                    shiftStart.get(Calendar.MONTH),
                    shiftStart.get(Calendar.DATE)
                )
            }

            datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                shiftStart.set(Calendar.MONTH, month)
                shiftStart.set(Calendar.DATE, dayOfMonth)
                shiftStart.set(Calendar.YEAR, year)

                StartDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftStart.time)
                EndDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftStart.time)
            }

            datePickerDialog.show()
        }
    }

    private fun listenForStartTime() {
        StartTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                shiftStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                shiftStart.set(Calendar.MINUTE, minute)

                StartTime.text = SimpleDateFormat("h:mm a", Locale.CANADA).format(shiftStart.time)

                if (shiftStart.get(Calendar.HOUR_OF_DAY) == 0) {
                    shiftStart.set(Calendar.DATE, shiftStart.get(Calendar.DATE) + 1)
                    StartDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftStart.time)
                }

            }, shiftStart.get(Calendar.HOUR_OF_DAY), shiftStart.get(Calendar.MINUTE), false)

            timePicker.show()
        }
    }


    private fun listenForEndDate() {
        EndDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this)

            if (::shiftEnd.isInitialized) {
                datePickerDialog.updateDate(
                    shiftEnd.get(Calendar.YEAR),
                    shiftEnd.get(Calendar.MONTH),
                    shiftEnd.get(Calendar.DATE)
                )
            }

            datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                shiftEnd.set(Calendar.MONTH, month)
                shiftEnd.set(Calendar.DATE, dayOfMonth)
                shiftEnd.set(Calendar.YEAR, year)

                EndDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftEnd.time)
            }

            datePickerDialog.show()
        }
    }

    private fun listenForEndTime() {
        EndTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                shiftEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                shiftEnd.set(Calendar.MINUTE, minute)

                EndTime.text = SimpleDateFormat("h:mm a", Locale.CANADA).format(shiftEnd.time)

                //IF USER SELECTS 12 AM
                if (shiftEnd.get(Calendar.HOUR_OF_DAY) == 0) {
                    shiftEnd.set(Calendar.DATE, shiftEnd.get(Calendar.DATE) + 1)
                    EndDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(shiftEnd.time)
                }

            }, shiftEnd.get(Calendar.HOUR_OF_DAY), shiftEnd.get(Calendar.MINUTE), false)

            timePicker.show()
        }
    }


    private fun listenForBreak() {
        BreakDuration.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.number_picker_dialog)

            val hourPicker = dialog.findViewById<NumberPicker>(R.id.HourPicker)
            hourPicker.maxValue = 12
            hourPicker.minValue = 0
            hourPicker.wrapSelectorWheel = true

            val minutePicker = dialog.findViewById<NumberPicker>(R.id.MinutePicker)
            minutePicker.maxValue = 59
            minutePicker.minValue = 0
            minutePicker.wrapSelectorWheel = true


            val cancel = dialog.findViewById<Button>(R.id.btn_cancel)
            cancel.setOnClickListener { dialog.dismiss() }

            val ok = dialog.findViewById<Button>(R.id.btn_ok)
            ok.setOnClickListener {
                Toast.makeText(
                    this,
                    "Hour: ${hourPicker.value} Minute: ${minutePicker.value}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                BreakDuration.text = "${hourPicker.value}h ${minutePicker.value}m"
                breakTime = Time(hourPicker.value, minutePicker.value)
            }

            dialog.show()
        }
    }

    private fun listenForPaidBreak() {
        paidBreakToggle.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                paidBreak = true
                Toast.makeText(this, "Paid Break", Toast.LENGTH_SHORT).show()
            } else {
                paidBreak = false
                Toast.makeText(this, "Not Paid Break", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun listenForOvertime() {
        OvertimeDuration.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.number_picker_dialog)

            Toast.makeText(this, "${overtime.hour}:${overtime.minute}", Toast.LENGTH_SHORT).show()
            val hourPicker = dialog.findViewById<NumberPicker>(R.id.HourPicker)
            hourPicker.maxValue = 12
            hourPicker.minValue = 0
            hourPicker.wrapSelectorWheel = true

            val minutePicker = dialog.findViewById<NumberPicker>(R.id.MinutePicker)
            dialog.findViewById<NumberPicker>(R.id.MinutePicker).value = overtime.minute
            minutePicker.maxValue = 59
            minutePicker.minValue = 0
            minutePicker.wrapSelectorWheel = true


            val cancel = dialog.findViewById<Button>(R.id.btn_cancel)
            cancel.setOnClickListener { dialog.dismiss() }

            val ok = dialog.findViewById<Button>(R.id.btn_ok)
            ok.setOnClickListener {
                Toast.makeText(
                    this,
                    "Hour: ${hourPicker.value} Minute: ${minutePicker.value}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                OvertimeDuration.text = "${hourPicker.value}h ${minutePicker.value}m"
                overtime = Time(hourPicker.value, minutePicker.value)
            }

            dialog.show()
        }
    }

    private fun listenForPaidShift() {
        btn_paid.setOnClickListener {
            shiftPaid = !shiftPaid

            if (shiftPaid) {
                btn_paid.setBackgroundColor(Color.parseColor("#DD2C00"))
                btn_paid.setTextColor(Color.WHITE)
            } else {
                btn_paid.setBackgroundColor(Color.WHITE)
                btn_paid.setTextColor(Color.parseColor("#DD2C00"))
            }
        }
    }


    private fun listenForSave() {
        btn_save.setOnClickListener {

            if (PayRateSelect.text.isEmpty()) {
                showErrorMessage("Please select the pay rate")
                return@setOnClickListener
            }

            if (!isSelectedDateValid()) {
                showErrorMessage("The date selection is invalid. Please try again.")
                return@setOnClickListener
            }

            if (!isTimeSelectionValid(breakTime)) {
                showErrorMessage("The break time selection is invalid. Please try again.")
                return@setOnClickListener
            }

            if (doesEntryExist() && !editMode) {
                showWarningMessage("Entry already exists. Do you want to override it.",2)
                return@setOnClickListener

            }

            save()
        }
    }

    private fun save() {

        if (PayRateSelect.text.isNotEmpty()) {
            payRate = PayRateSelect.text.toString().toDouble()
            Log.d(TAG, "$payRate")
        }

        val entry = SingleEntry(payRate, shiftStart, shiftEnd, breakTime, paidBreak, overtime, shiftPaid)
        Log.d(TAG, "$entry")
        storeData(entry)
        this.finish()
    }
    /*CLICK EVENT LISTENING*/






    /*VALIDATION UPON SAVING*/
    private fun isTimeSelectionValid(time: Time): Boolean {
        val difference = Math.abs(shiftEnd.time.time - shiftStart.time.time)
        var minutes = difference / 60000

        val days = (minutes / 1440)
        minutes -= days * 1440

        val hours = (minutes / 60)
        minutes -= hours * 60

        if (time.hour > hours) {
            return false
        }
        if (time.hour == hours.toInt() && time.minute > minutes) {
            return false
        }

        return true
    }

    private fun isSelectedDateValid(): Boolean {
        if (shiftEnd.get(Calendar.YEAR) < shiftStart.get(Calendar.YEAR))
            return false
        if (shiftEnd.get(Calendar.DATE) < shiftStart.get(Calendar.DATE))
            return false
        if (shiftEnd.get(Calendar.MONTH) < shiftStart.get(Calendar.MONTH))
            return false

        return true
    }

    private fun doesEntryExist(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences(FILENAME, MODE_PRIVATE)
        val gson = Gson()

        val monthKey = SimpleDateFormat("MMMM/yyyy", Locale.CANADA).format(shiftStart.time)

        if (isMonthDataInFile(monthKey)) {
            val jsonText = sharedPreferences.getString(monthKey, null)
            val dataType = object : TypeToken<TreeMap<Int, SingleEntry>>() {}.type
            val thisMonthData = gson.fromJson<TreeMap<Int, SingleEntry>>(jsonText, dataType)

            return thisMonthData.containsKey(shiftStart.get(Calendar.DATE))
        }
        return false
    }
    /*VALIDATION UPON SAVING*/





    /*ERROR AND WARNING MESSAGES*/
    private fun showErrorMessage(text: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.error_message_dialog)

        val errorMessageTextView = dialog.findViewById<TextView>(R.id.ErrorMessage)
        errorMessageTextView.text = text

        val ok = dialog.findViewById<Button>(R.id.btn_ok)
        ok.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /* Mode 1 = Deletion
       Mode 2 = Overriding
     */
    private fun showWarningMessage(text: String,mode: Int) {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.warning_message_dialog)

        val errorMessageTextView = dialog.findViewById<TextView>(R.id.WarningMessage)
        errorMessageTextView.text = text

        if(mode == 1){
            dialog.findViewById<Button>(R.id.btn_ok).text = "delete"
        }

        val cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        val ok = dialog.findViewById<Button>(R.id.btn_ok)
        ok.setOnClickListener {
            dialog.dismiss()

            //Deleting The Entry
            if(mode == 1){

                if(editMode)
                    deleteUpdateData(shiftStart.get(Calendar.DATE))
                this.finish()
            }
            else{save()}
        }
        dialog.show()
    }
    /*ERROR AND WARNING MESSAGES*/







    /*STORING DATA*/
    private fun doesDataFileExist(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences(FILENAME, MODE_PRIVATE)
        return sharedPreferences != null
    }

    private fun isMonthDataInFile(key: String): Boolean {
        if (!doesDataFileExist())
            return false

        val sharedPreferences: SharedPreferences = getSharedPreferences(FILENAME, MODE_PRIVATE)
        val getMonthData = sharedPreferences.getString(key, null)
        return getMonthData != null
    }

    private fun storeData(entry: SingleEntry) {

        var thisMonthData = TreeMap<Int, SingleEntry>() //<Month Date, Entry Data>
        val sharedPreferences: SharedPreferences = getSharedPreferences(FILENAME, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()

        val monthKey = SimpleDateFormat("MMMM/yyyy", Locale.CANADA).format(shiftStart.time)

        if (isMonthDataInFile(monthKey)) {
            val jsonText = sharedPreferences.getString(monthKey, null)
            val dataType = object : TypeToken<TreeMap<Int, SingleEntry>>() {}.type
            thisMonthData = gson.fromJson<TreeMap<Int, SingleEntry>>(jsonText, dataType)

            //Add The Data
            thisMonthData.put(shiftStart.get(Calendar.DATE), entry)

            //Convert the data structure into JSON
            val jsonData = gson.toJson(thisMonthData)
            editor.putString(monthKey, jsonData)
            editor.apply()
            Toast.makeText(this, "Appending: Data Saved", Toast.LENGTH_SHORT).show()
        } else {
            thisMonthData.put(shiftStart.get(Calendar.DATE), entry)
            val jsonData = gson.toJson(thisMonthData)
            editor.putString(monthKey, jsonData)
            editor.commit()
            Toast.makeText(this, "New File Created: Data Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteUpdateData(updateKey: Int){
        val sharedPreferences: SharedPreferences = getSharedPreferences(FILENAME, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()

        val monthKey = SimpleDateFormat("MMMM/yyyy", Locale.CANADA).format(shiftStart.time)

        if (isMonthDataInFile(monthKey)) {
            val jsonText = sharedPreferences.getString(monthKey, null)
            val dataType = object : TypeToken<TreeMap<Int, SingleEntry>>() {}.type
            val thisMonthData = gson.fromJson<TreeMap<Int, SingleEntry>>(jsonText, dataType)

            //Update
            thisMonthData.remove(updateKey)

            //Apply Change
            val jsonData = gson.toJson(thisMonthData)
            editor.putString(monthKey, jsonData)
            editor.apply()
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
        }
    }
    /*STORING DATA*/


    //DONT NEED THIS
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_button,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.delete_button->{
                showWarningMessage("Are you sure to delete this entry?",1)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
