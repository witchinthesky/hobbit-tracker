package com.example.hobbittracker.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hobbittracker.data.valstore.OnBoardingStateStorage
import com.example.hobbittracker.presentation.home.HomeActivity
import com.example.hobbittracker.presentation.onboarding.OnBoarding
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        // redirect if on boarding already been viewed
        if (getOnBoardingState()) {
            val i = Intent(applicationContext, HomeActivity::class.java)
            startActivity(i)
            finish()
        }
        else{
            val i = Intent(applicationContext, OnBoarding::class.java)
            startActivity(i)
            finish()
        }

    }

    // TODO: delete
    private fun getOnBoardingState(): Boolean {
        val context = this.applicationContext
        return runBlocking {
            OnBoardingStateStorage(context)()
        }
    }
}