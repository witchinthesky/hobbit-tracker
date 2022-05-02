package com.example.hobbittracker.domain.usecase.auth

import com.example.hobbittracker.domain.repository.AuthRepository
import com.example.hobbittracker.domain.utils.Result

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Void?> =
        authRepository.resetPassword(email)
}