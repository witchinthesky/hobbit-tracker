package com.example.hobbittracker.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbittracker.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_new_habit.*

class NewHabitFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reminder.setOnClickListener{
            // TODO: don`t render picker when it already rendering
            openTimePicker()
        }
        isReminder.setOnClickListener{
           // isReminder.isChecked = !(isReminder.isChecked)
            reminder.isEnabled = isReminder.isChecked
        }
    }

    private fun openTimePicker() {

        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        // create picker
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Set Alarm")
            .build()

        picker.show(childFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {

            Log.d("Alarm", "Success set alarm")
            val h = picker.hour
            val m = picker.minute
            alarmTime.text = "$h:$m"
            Log.d("Alarm", "$h:$m")
        }
    }

    // save data for uploading to database
    fun getHabit(){

        val name = habitName.text
        val pickedDays = day_picker.selectedDays
        val isSetReminder = isReminder.isChecked
        val data = alarmTime.text

    }
}