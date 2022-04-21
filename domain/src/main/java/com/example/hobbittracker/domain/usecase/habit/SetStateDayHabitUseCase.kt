package com.example.hobbittracker.domain.usecase.habit

import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.utils.Result
import java.time.LocalDate

class SetStateDayHabitUseCase(
    private val categoryHabitsRepository: HabitRepository
) {
    suspend operator fun invoke(
        id: String,
        date: LocalDate,
        isComplete: Boolean = true
    ): Result<Void?> =
        categoryHabitsRepository.setStateDayHabit(id, date, isComplete)
}