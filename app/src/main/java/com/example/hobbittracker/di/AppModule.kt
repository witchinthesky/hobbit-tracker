package com.example.hobbittracker.di

import com.example.hobbittracker.presentation.auth.AuthViewModel
import com.example.hobbittracker.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<AuthViewModel> {
        AuthViewModel(
            app = get(),
            loginUseCase = get(),
            registerUseCase = get(),
            signInFacebookUseCase = get(),
            signInGoogleUseCase = get(),
            currentUserUseCase = get(),
            resetPasswordUseCase = get(),
            logoutUseCase = get()
        )
    }

    viewModel<HomeViewModel> {
        HomeViewModel(
            app = get(),
            getCategoriesAllUseCase = get(),
            addHabitUseCase = get(),
            getHabitsAllUseCase = get(),
            getHabitsByCategoryUseCase = get()
        )
    }
}