package com.example.hobbittracker.presentation.home.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.MainActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val rm: RemindersManager by inject()

    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     * */
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra("habitId")
        val habitReminderId = intent.getStringExtra("habitReminderId")!!.toInt()
        val habitName = intent.getStringExtra("habitName")!!

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendReminderNotification(
            applicationContext = context,
            habitReminderId,
            habitName,
        )
        // Remove this line if you don't want to reschedule the reminder
        rm.setHabitReminder(context.applicationContext, habitId!!)
    }
}

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    notificationId: Int,
    habitName: String
) {
    val title = applicationContext.getString(R.string.title_notification_reminder)
    val description = applicationContext.getString(R.string.description_notification_reminder) + habitName
    val channelId = applicationContext.getString(R.string.reminders_notification_channel_id)

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    contentIntent.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setContentTitle(title)
        .setContentText(description)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_SOUND)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(notificationId, builder.build())
}