package com.example.hobbittracker.domain.usecase.habit

import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.utils.Result

class UpdateHabitUseCase(
    private val categoryHabitsRepository: HabitRepository
) {
    suspend operator fun invoke(id: String, habit: Habit) : Result<Void?> =
        categoryHabitsRepository.updateHabit(id, habit)
}