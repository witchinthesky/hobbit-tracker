package com.example.hobbittracker.presentation.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.fragments.ComunityFragment
import com.example.hobbittracker.fragments.DashboardFragment
import com.example.hobbittracker.fragments.SettingsFragment
import com.example.hobbittracker.fragments.StatisticsFragment
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
import kotlinx.android.synthetic.main.selected_calendar_item.view.*
import org.koin.android.ext.android.inject
import java.util.*


class HomeActivity : AppCompatActivity() {


    // creates fragments for bottom menu navigation
    private val dashboardFragment = DashboardFragment()
    private val comunityfragment = ComunityFragment()
    private val settingsFragment = SettingsFragment()
    private val statisticsFragment = StatisticsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // fix glitches at bottom menu
        bottomNavigationView.background = null
        bottomNavigationView.itemIconTintList = null
        add_task.imageTintList = null


        // set home fragment
        replaceFragment(dashboardFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(dashboardFragment)
                }
                R.id.account -> {
                    replaceFragment(comunityfragment)
                }
                R.id.comunity -> {
                    replaceFragment(settingsFragment)
                }
                R.id.setting -> {
                    replaceFragment(statisticsFragment)
                }
            }
            true
        }
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