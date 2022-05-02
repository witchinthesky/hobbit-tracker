package com.example.hobbittracker.presentation.home.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.usecase.habit.GetHabitUseCase
import com.example.hobbittracker.domain.usecase.habit.GetHabitsAllUseCase
import com.example.hobbittracker.domain.utils.Result
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class RemindersManager(
    private val getHabitsAllUseCase: GetHabitsAllUseCase,
    private val getHabitUseCase: GetHabitUseCase
) {

    var habits = mutableMapOf<String, Habit>()

    @SuppressLint("UnspecifiedImmutableFlag")
    fun setHabitReminder(
        context: Context,
        habitId: String
    ) {
        val habit: Habit = getHabit(habitId) ?: return

        if (habit.pickedDays.isEmpty()) return
        val time = habit.reminderTime ?: return
        var now = LocalDateTime.now()
        if (now.toLocalTime() > time)
            now = now.plusDays(1)
        val day: LocalDate = getNearestPickedDay(
            now.toLocalDate(),
            habit.pickedDays
        )

        val reminderTime = LocalDateTime
            .of(day, time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(
            context.applicationContext,
            AlarmReceiver::class.java
        ).let { intent ->
            PendingIntent.getBroadcast(
                context.applicationContext,
                habit.reminderId,
                intent.apply {
//                    action = "com.example.hobbittracker.presentation.home.notifications.AlarmReceiver"
                    putExtra("habitId", habit.id)
                    putExtra("habitReminderId", habit.reminderId.toString())
                    putExtra("habitName", habit.name)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(reminderTime, intent),
            intent
        )
    }


    fun setHabitsAllReminders(context: Context) {
        val h = when (val res = runBlocking {
            getHabitsAllUseCase()
        }) {
            is Result.Success -> res.data
            is Result.Error -> null
            is Result.Canceled -> null
        } ?: return

        habits = h.associateBy { it.id } as MutableMap<String, Habit>

        habits.forEach {
            setHabitReminder(context, it.key)
        }
    }


    fun deleteHabitReminder(
        context: Context,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, reminderId, intent, 0)
        }
        alarmManager.cancel(intent)
    }


    private fun getHabit(id: String): Habit? {
        return if (habits.containsKey(id)) habits[id]
        else runBlocking {
            when (val res = getHabitUseCase(id)) {
                is Result.Success -> {
                    habits[res.data.id] = res.data
                    res.data
                }
                is Result.Error -> null
                is Result.Canceled -> null
            }
        }
    }

    private fun getNearestPickedDay(day: LocalDate, pickedDays: List<DayOfWeek>): LocalDate {
        var date = day

        for (i in 0 until 7) {
            if (date.dayOfWeek in pickedDays) break
            else date = date.plusDays(1)
        }
        return date
    }

    companion object {
        fun createNotificationsChannels(context: Context) {
            val channel = NotificationChannel(
                context.getString(R.string.reminders_notification_channel_id),
                context.getString(R.string.reminders_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            ContextCompat
                .getSystemService(context, NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }
}