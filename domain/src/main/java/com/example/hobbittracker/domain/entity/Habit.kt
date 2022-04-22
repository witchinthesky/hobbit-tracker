package com.example.hobbittracker.domain.entity

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class Habit(
    val id: String = "",
    val name: String,
    val pickedDays: List<DayOfWeek>,
    val endDay: LocalDate,
    val reminderTime: LocalTime? = null,
    val categoryId: Int = 0,
    val color: String? = null,
    val completedDays: List<LocalDate> = listOf(),
    @field:JvmField
    val isComplete: Boolean? = null
) : Comparable<Habit> {

    override fun compareTo(other: Habit): Int {
        val today: DayOfWeek = LocalDate.now().dayOfWeek
        val tomorrow: DayOfWeek = today.plus(1)

        val todayCompareFirst: Int = if (this.pickedDays.contains(today)) 1 else 0
        val todayCompareSecond: Int = if (other.pickedDays.contains(today)) 1 else 0

        val tomorrowCompareFirst: Int =
            if (this.pickedDays.contains(tomorrow)) 1 else 0
        val tomorrowCompareSecond: Int =
            if (other.pickedDays.contains(tomorrow)) 1 else 0

        return when {
            todayCompareFirst < todayCompareSecond -> 1
            tomorrowCompareFirst < tomorrowCompareSecond -> 0
            else -> -1
        }
    }
}