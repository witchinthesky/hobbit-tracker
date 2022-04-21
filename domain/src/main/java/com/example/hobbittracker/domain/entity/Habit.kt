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
)