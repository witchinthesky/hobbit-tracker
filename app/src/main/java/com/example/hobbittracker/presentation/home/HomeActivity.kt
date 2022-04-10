package com.example.hobbittracker.presentation.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.MainActivity
import com.example.hobbittracker.presentation.auth.AuthViewModel
import com.michalsvec.singlerowcalendar.*
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.selected_calendar_item.view.*
import org.koin.android.ext.android.inject
import java.util.*


class HomeActivity : AppCompatActivity() {

    // private val vm: AuthViewModel by inject()
    // private lateinit var binding: ActivityHomeBinding
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // set current date to calendar and current month to currentMonth variable
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]


        // fix glitches at bottom menu
        bottomNavigationView.background = null
        bottomNavigationView.itemIconTintList = null
        add_task.imageTintList = null


      //  binding = ActivityHomeBinding.inflate(layoutInflater)
       // val view = binding.root
       // setContentView(view)

        /* debug information
        binding.btnLogOut.setOnClickListener {
            logOut()
        }

        // Vew toast in bottom region if View Model change toast live data
        vm.toast.observe(this) { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                vm.onToastShown()
            }
        }

        // current user saved in viewModel, after registration, login or another query to server user
        vm.currentUserLD.observe(this) {
            binding.tvUserName.text = it.name
            binding.tvUserEmail.text = it.email
        }
        */

        val myCalendarViewManager = object : CalendarViewManager {

            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                val cal = Calendar.getInstance()
                cal.time = date

                return if(isSelected){
                     R.layout.selected_calendar_item
                }
                else{
                     R.layout.unselected_calendar_item
                }
                // return item layout files, which you have created
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                // bind data to calendar item views
              //  holder.itemView. = DateUtils.getDayNumber(date)
              //  holder.itemView.setOnTouchListener
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

            }
        }

        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
               // tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
              // tvDay.text = DateUtils.getDayName(date)
                super.whenSelectionChanged(isSelected, position, date)
            }


        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                // in this example sunday and saturday can't be selected, others can
                return true

            }
        }
        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar = home_one_line_calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonth())
            init()
        }


    }
/*
    private fun logOut() {
        vm.logOutUser()
        vm.startActivity(this, MainActivity::class.java, clearTasks = true)
    }
*/
    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        var currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }

    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }
}