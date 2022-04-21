package com.example.hobbittracker.di

import com.example.hobbittracker.domain.usecase.auth.*
import com.example.hobbittracker.domain.usecase.category.GetCategoriesAllUseCase
import com.example.hobbittracker.domain.usecase.category.GetCategoryUseCase
import com.example.hobbittracker.domain.usecase.category.UpdateCategoryUseCase
import com.example.hobbittracker.domain.usecase.habit.*
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


    factory<GetCategoryUseCase> {
        GetCategoryUseCase(categoryHabitsRepository = get())
    }

    factory<GetCategoriesAllUseCase> {
        GetCategoriesAllUseCase(categoryHabitsRepository = get())
    }

    factory<UpdateCategoryUseCase> {
        UpdateCategoryUseCase(categoryHabitsRepository = get())
    }


    factory<GetHabitUseCase> {
        GetHabitUseCase(categoryHabitsRepository = get())
    }

    factory<GetHabitsAllUseCase> {
        GetHabitsAllUseCase(categoryHabitsRepository = get())
    }

    factory<GetHabitsByCategoryUseCase> {
        GetHabitsByCategoryUseCase(categoryHabitsRepository = get())
    }

    factory<AddHabitUseCase> {
        AddHabitUseCase(categoryHabitsRepository = get())
    }

    factory<UpdateHabitUseCase> {
        UpdateHabitUseCase(categoryHabitsRepository = get())
    }

    factory<DeleteHabitUseCase> {
        DeleteHabitUseCase(categoryHabitsRepository = get())
    }

    factory<SetStateHabitUseCase> {
        SetStateHabitUseCase(categoryHabitsRepository = get())
    }

    factory<SetStateDayHabitUseCase> {
        SetStateDayHabitUseCase(categoryHabitsRepository = get())
    }

}