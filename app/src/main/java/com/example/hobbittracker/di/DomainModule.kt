package com.example.hobbittracker.di

import com.example.hobbittracker.domain.usecase.auth.*
import org.koin.dsl.module

val domainModule = module {
    factory<LoginUseCase> {
        LoginUseCase(authRepository = get())
    }

    factory<RegisterUseCase> {
        RegisterUseCase(authRepository = get())
    }

    factory<SignInGoogleUseCase> {
        SignInGoogleUseCase(authRepository = get())
    }

    factory<SignInFacebookUseCase> {
        SignInFacebookUseCase(authRepository = get())
    }

    factory<LogoutUseCase> {
        LogoutUseCase(authRepository = get())
    }

    factory<ResetPasswordUseCase> {
        ResetPasswordUseCase(authRepository = get())
    }

    factory<CurrentUserUseCase> {
        CurrentUserUseCase(authRepository = get())
    }
}