package com.example.hobbittracker.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbittracker.R
import com.example.hobbittracker.data.valstore.OnBoardingStateStorage
import com.example.hobbittracker.presentation.auth.AuthViewModel
import com.example.hobbittracker.presentation.auth.LoginActivity
import com.example.hobbittracker.presentation.home.HomeActivity
import com.example.hobbittracker.presentation.onboarding.OnBoarding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val vm: AuthViewModel by inject()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Running in coroutine to execute a task on a different thread, and don't suspend main thread
        coroutineScope.launch {
            // redirect if on boarding not been viewed
            if (!getOnBoardingState())
                vm.startActivity(
                    this@MainActivity, OnBoarding::class.java,
                    clearTasks = true, finish = true
                )
            // redirect if user doesn't logged
            else if (vm.getCurrentUser() != null) {
                vm.startActivity(
                    this@MainActivity, HomeActivity::class.java,
                    clearTasks = true, finish = true
                )
            }
        }
        // view Login activity while another coroutines is running
        vm.startActivity(this@MainActivity, LoginActivity::class.java, finish = true)
    }

    // TODO: delete
    private suspend fun getOnBoardingState(): Boolean =
        OnBoardingStateStorage(this@MainActivity.applicationContext)()
}