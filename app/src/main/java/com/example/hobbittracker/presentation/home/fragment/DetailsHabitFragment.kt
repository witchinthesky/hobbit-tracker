package com.example.hobbittracker.presentation.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_details_habit.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailsHabitFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_cancel.setOnClickListener {
            onEventFinish()
        }

        btn_edit.setOnClickListener {
            vm.replaceFragment(this.parentFragmentManager, EditHabitFragment())
        }
    }


    private fun onEventFinish() {
        vm.replaceFragment(this.parentFragmentManager, DashboardFragment())
    }

    private fun onEventDone() {
        onEventFinish()
    }
}