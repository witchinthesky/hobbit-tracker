package com.example.hobbittracker.presentation.home.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent{
    /*
    * restart reminders alarms when user's device reboots
    * */
    private val rm: RemindersManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            rm.setHabitsAllReminders(context.applicationContext)
        }
    }
}