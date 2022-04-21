package com.example.hobbittracker.domain.usecase.category

import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.repository.CategoryHabitsRepository
import com.example.hobbittracker.domain.utils.Result

class GetCategoriesAllUseCase(
    private val categoryHabitsRepository: CategoryHabitsRepository
) {
    suspend operator fun invoke(): Result<List<CategoryHabits>> =
        categoryHabitsRepository.getCategoriesAll()
}