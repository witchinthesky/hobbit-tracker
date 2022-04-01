package com.example.hobbittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
        return false
    }
}