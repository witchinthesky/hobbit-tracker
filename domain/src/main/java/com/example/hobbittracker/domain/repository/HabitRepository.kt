package com.example.hobbittracker.domain.repository

import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.utils.Result
import java.time.LocalDate

interface HabitRepository {

    suspend fun getHabit(id: String): Result<Habit>

    suspend fun getHabitsAll(): Result<List<Habit>>

    suspend fun getHabitsByCategory(categoryId: Int) : Result<List<Habit>>

    suspend fun addHabit(habit: Habit): Result<Habit>

    suspend fun updateHabit(id: String, habit: Habit) : Result<Void?>

    suspend fun deleteHabit(id: String): Result<Void?>

    suspend fun setStateHabit(id: String, isComplete: Boolean?): Result<Void?>

    suspend fun setStateDayHabit(id: String, date: LocalDate, isComplete: Boolean = true): Result<Void?>
}