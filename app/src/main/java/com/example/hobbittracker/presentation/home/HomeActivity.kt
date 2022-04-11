package com.example.hobbittracker.presentation.home

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.fragments.*
import com.example.hobbittracker.presentation.MainActivity
import com.example.hobbittracker.presentation.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarMenuView
import com.google.android.material.navigation.NavigationView
import com.michalsvec.singlerowcalendar.*
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.selected_calendar_item.view.*
import org.koin.android.ext.android.inject
import java.util.*


class HomeActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0


    // creates fragments for bottom menu navigation
    private val dashboardFragment = DashboardFragment()
    private val comunityfragment = ComunityFragment()
    private val settingsFragment = SettingsFragment()
    private val statisticsFragment = StatisticsFragment()
    private val newHabitFragment = NewHabitFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // fix glitches at bottom menu
        bottomNavigationView.background = null
        bottomNavigationView.itemIconTintList = null
        add_task.imageTintList = null


        // set home fragment
         // replaceFragment(NewHabitFragment())

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    homeContainer.visibility = VISIBLE
                    replaceFragment(dashboardFragment)
                }
                R.id.account -> {
                    homeContainer.visibility = INVISIBLE
                    replaceFragment(comunityfragment)
                }
                R.id.comunity -> {
                    homeContainer.visibility = INVISIBLE
                    replaceFragment(settingsFragment)
                }
                R.id.setting -> {
                    homeContainer.visibility = INVISIBLE
                    replaceFragment(statisticsFragment)
                }
            }
            true
        }

        add_task.setOnClickListener {

            if (homeContainer.visibility == INVISIBLE){
                // save task & change icon to add
                val newHabitData = newHabitFragment.getHabit()
            }
            else {
                // open add habit form
                replaceFragment(newHabitFragment)
                homeContainer.visibility = INVISIBLE
            }

        }

        // Calendar for home tab
        val myCalendarViewManager = object : CalendarViewManager {

            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // val cal = Calendar.getInstance()
                calendar.time = date

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
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
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


    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
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


    private fun replaceFragment(fragment: Fragment) : Boolean{
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
            return true
        }
        return false
    }
}