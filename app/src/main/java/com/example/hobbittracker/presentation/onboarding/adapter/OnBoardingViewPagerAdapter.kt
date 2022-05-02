package com.example.hobbittracker.presentation.onboarding.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.onboarding.model.OnBoardingData

class OnBoardingViewPagerAdapter(
    private var context: Context,
    private var OnBoardingDataList: List<OnBoardingData>
    ) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return OnBoardingDataList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(context).inflate(R.layout.layout_onboarding_screen, null)

        val image: ImageView = view.findViewById(R.id.imageView)
        val title: TextView = view.findViewById(R.id.titleboard)
        val desc: TextView = view.findViewById(R.id.desc)

        image.setImageResource(OnBoardingDataList[position].image)
        title.setText(OnBoardingDataList[position].title)
        desc.setText(OnBoardingDataList[position].desc)

        container.addView(view)
        return view
    }

}