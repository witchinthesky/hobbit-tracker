package com.example.hobbittracker.di

import com.example.hobbittracker.presentation.auth.AuthViewModel
import org.koin.dsl.module

val appModule = module {
    single<AuthViewModel> {
        AuthViewModel(
            loginUseCase = get(),
            registerUseCase = get(),
            signInFacebookUseCase = get(),
            signInGoogleUseCase = get(),
            currentUserUseCase = get(),
            resetPasswordUseCase = get(),
            logoutUseCase = get()
        )
    }
}