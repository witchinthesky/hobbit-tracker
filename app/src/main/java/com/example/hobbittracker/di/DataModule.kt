package com.example.hobbittracker.di

import com.example.hobbittracker.data.repository.AuthRepositoryImpl
import com.example.hobbittracker.data.repository.CategoryHabitsRepositoryImpl
import com.example.hobbittracker.data.repository.HabitRepositoryImpl
import com.example.hobbittracker.data.storage.AuthStorage
import com.example.hobbittracker.data.storage.Storage
import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.repository.AuthRepository
import com.example.hobbittracker.domain.repository.CategoryHabitsRepository
import com.example.hobbittracker.domain.repository.HabitRepository
import org.koin.dsl.module

val dataModule = module {
    single<Storage<String, User>> {
        AuthStorage()
    }

    single<AuthRepository> {
        AuthRepositoryImpl(storage = get())
    }

    single<HabitRepository> {
        HabitRepositoryImpl(
            authStorage = AuthStorage(),
            currentUserUseCase = get()
        )
    }

    single<CategoryHabitsRepository> {
        CategoryHabitsRepositoryImpl()
    }
}