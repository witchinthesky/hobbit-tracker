package com.example.hobbittracker.domain.usecase.habit

import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.utils.Result

class GetHabitsAllUseCase(
    private val categoryHabitsRepository: HabitRepository
) {
    suspend operator fun invoke(): Result<List<Habit>> =
        categoryHabitsRepository.getHabitsAll()
}