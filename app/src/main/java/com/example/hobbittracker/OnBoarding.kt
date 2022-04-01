package com.example.hobbittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.example.hobbittracker.adapter.OnBoardingViewPagerAdapter
import com.example.hobbittracker.model.OnBoardingData
import com.google.android.material.tabs.TabLayout
import java.util.ArrayList

class OnBoarding : AppCompatActivity() {

    var onBoardingViewPagerAdapter : OnBoardingViewPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var onBoardingViewPager: ViewPager?= null
    var next: TextView? = null
    var prev: TextView? = null
    var position = 0
    var getStarted: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // redirect if on boarding already been viewed
        if(getOnBoardingState()){
            val i = Intent(applicationContext, HomeActivity::class.java)
            startActivity(i)
            finish()
        }

        setContentView(R.layout.activity_on_boarding)

        tabLayout = findViewById(R.id.tab_indicator)
        next = findViewById(R.id.next)
        prev = findViewById(R.id.prev)
        getStarted = findViewById(R.id.get_started)

        getStarted!!.isVisible = false

        val onBoardingData:MutableList<OnBoardingData> = ArrayList()

        onBoardingData.add(OnBoardingData(R.string.on_boarding01_title, R.string.on_boarding01_desc, R.drawable.ic__placeholder))
        onBoardingData.add(OnBoardingData(R.string.on_boarding02_title, R.string.on_boarding01_desc, R.drawable.ic_habits))
        onBoardingData.add(OnBoardingData(R.string.on_boarding03_title, R.string.on_boarding01_desc, R.drawable.ic_progress))
        onBoardingData.add(OnBoardingData(R.string.on_boarding04_title, R.string.on_boarding01_desc, R.drawable.ic_community_support))

        setOnBoardingViewPagerAdapter(onBoardingData)

        next?.setOnClickListener{
            if (position < onBoardingData.size){
                position++
                onBoardingViewPager!!.currentItem = position
            }
        }

        prev?.setOnClickListener{
            if (position > 0){
                position--
                onBoardingViewPager!!.currentItem = position
            }
        }

        getStarted?.setOnClickListener{

            saveOnBoardingState(true)
            val i = Intent(applicationContext, HomeActivity::class.java)
            startActivity(i)
        }

        tabLayout!!.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if(tab.position == onBoardingData.size - 1){

                    next!!.isVisible = false
                    tabLayout!!.isVisible = false
                    prev!!.isVisible = false
                    getStarted!!.isVisible = true

                }
            }
        })
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>){
        onBoardingViewPager = findViewById(R.id.screenPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }


    // TODO: delete this
    fun saveOnBoardingState(state: Boolean){
        // do nothing
    }

    fun getOnBoardingState() : Boolean {
        return false;
    }
}
