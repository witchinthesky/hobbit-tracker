package com.example.hobbittracker.domain.usecase.habit

import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.utils.Result

class GetHabitUseCase(
    private val categoryHabitsRepository: HabitRepository
) {
    suspend operator fun invoke(id: String): Result<Habit> =
        categoryHabitsRepository.getHabit(id)
}