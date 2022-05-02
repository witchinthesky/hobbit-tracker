package com.example.hobbittracker.domain.usecase.habit

import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.utils.Result

class SetStateHabitUseCase(
    private val categoryHabitsRepository: HabitRepository
) {
    suspend operator fun invoke(id: String, isComplete: Boolean?): Result<Void?> =
        categoryHabitsRepository.setStateHabit(id, isComplete)
}