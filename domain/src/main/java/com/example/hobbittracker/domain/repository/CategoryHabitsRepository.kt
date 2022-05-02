package com.example.hobbittracker.domain.repository

import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.utils.Result

interface CategoryHabitsRepository {

    suspend fun getCategory(id: Int) : Result<CategoryHabits>

    suspend fun getCategoriesAll() : Result<List<CategoryHabits>>

    suspend fun updateCategory(categoryHabits: CategoryHabits) : Result<Void?>

}