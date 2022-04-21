package com.example.hobbittracker.domain.usecase.habit

import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.utils.Result

class GetHabitsByCategoryUseCase(
    private val categoryHabitsRepository: HabitRepository
) {
    suspend operator fun invoke(categoryId: Int) : Result<List<Habit>> =
        categoryHabitsRepository.getHabitsByCategory(categoryId)
}