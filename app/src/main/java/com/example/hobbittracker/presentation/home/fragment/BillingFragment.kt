package com.example.hobbittracker.presentation.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.home.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_billing.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BillingFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideNavigation()

        btn_cancel.setOnClickListener {
            onEventFinish()
        }
    }

    private fun onEventFinish() {
        vm.replaceFragment(
            requireActivity().supportFragmentManager,
            DashboardFragment()
        )
    }

    private fun hideNavigation() {
        requireActivity().buttomNavigation.visibility = View.GONE
        requireActivity().btn_add.visibility = View.GONE
    }
}