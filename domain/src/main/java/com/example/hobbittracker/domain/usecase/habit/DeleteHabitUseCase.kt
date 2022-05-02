package com.example.hobbittracker.domain.usecase.habit

import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.utils.Result

class DeleteHabitUseCase(
    private val categoryHabitsRepository: HabitRepository
) {
    suspend operator fun invoke(id: String): Result<Void?> =
        categoryHabitsRepository.deleteHabit(id)
}