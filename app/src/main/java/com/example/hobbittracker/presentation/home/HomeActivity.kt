package com.example.hobbittracker.presentation.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.home.fragment.DashboardFragment
import com.example.hobbittracker.presentation.home.fragment.NewHabitFragment
import com.example.hobbittracker.presentation.home.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class HomeActivity : AppCompatActivity() {
    private val tag = this::class.java.simpleName

    private val vm: HomeViewModel by viewModel()

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0


    // some fragments are better to be created new so that they are not saved
    // creates fragments for bottom menu navigation
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        HomeService.app = application
        // fix glitches at bottom menu
        bottomNavigationView.background = null
        bottomNavigationView.itemIconTintList = null
        btn_done.imageTintList = null

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

        btn_done.setOnClickListener {
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

    private fun replaceFragment(fragment: Fragment) {
        vm.replaceFragment(supportFragmentManager, fragment)
    }
}