package com.example.hobbittracker.domain.usecase.auth

import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.repository.AuthRepository
import com.example.hobbittracker.domain.utils.Result

class SignInGoogleUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        idToken: String,
        accessToken: String?
    ): Result<User> = authRepository.signInGoogle(idToken, accessToken)
}