package com.example.hobbittracker.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.home.fragment.DashboardFragment
import com.example.hobbittracker.presentation.home.fragment.NewHabitFragment
import com.example.hobbittracker.presentation.home.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity : AppCompatActivity() {
    private val tag = this::class.java.simpleName

    private val vm: HomeViewModel by viewModel()

    // some fragments are better to be created new so that they are not saved
    // creates fragments for bottom menu navigation
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        HomeService.app = application

        initNavigation()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(DashboardFragment())
                }
                R.id.setting -> {
                    replaceFragment(settingsFragment)
                }
            }
            true
        }

        btn_add.setOnClickListener {
            replaceFragment(NewHabitFragment())
        }

        vm.toast.observe(this) { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                vm.onToastShown()
            }
        }

        vm.pullCategoriesAll()

        replaceFragment(DashboardFragment())
    }

    private fun initNavigation() {
        // fix glitches at bottom menu
        bottomNavigationView.background = null
        bottomNavigationView.itemIconTintList = null
        btn_add.imageTintList = null
        // show navigation
        btn_add.visibility = View.VISIBLE
        buttomNavigation.visibility = View.VISIBLE
    }

    private fun replaceFragment(fragment: Fragment) {
        vm.replaceFragment(supportFragmentManager, fragment)
    }
}