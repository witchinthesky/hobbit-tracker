package com.example.hobbittracker.presentation.home.fragment

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.MainActivity
import com.example.hobbittracker.presentation.auth.AuthViewModel
import com.example.hobbittracker.presentation.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_edit_habit.btn_cancel
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()
    private val vma: AuthViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_cancel.setOnClickListener {
            onEventFinish()
        }

        btn_logout.setOnClickListener {
            logOut()
        }

        btn_notification.setOnClickListener {
            notificationClickEvent()
        }
    }

    private fun notificationClickEvent() {
        createAndroidNotificationChannel(requireContext())
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, REMINDERS_CHANNEL_ID)
        startActivity(intent)
    }

    private fun onEventFinish() {
        vm.replaceFragment(
            requireActivity().supportFragmentManager,
            DashboardFragment()
        )
    }

    private fun logOut() {
        vma.logOutUser()
        vma.startActivity(
            requireActivity(),
            MainActivity::class.java,
            clearTasks = true
        )
    }

    companion object {
        private const val REMINDERS_CHANNEL_ID = "REMINDERS"
        private fun createAndroidNotificationChannel(context: Context) {
            val notificationManager = context.getSystemService(Activity.NOTIFICATION_SERVICE)
                    as NotificationManager
            val channel = NotificationChannel(
                REMINDERS_CHANNEL_ID,
                context.resources.getString(R.string.reminder),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}
