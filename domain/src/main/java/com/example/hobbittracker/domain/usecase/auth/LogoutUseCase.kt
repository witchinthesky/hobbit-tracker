package com.example.hobbittracker.domain.usecase.auth

import com.example.hobbittracker.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() =
        authRepository.logout()
}