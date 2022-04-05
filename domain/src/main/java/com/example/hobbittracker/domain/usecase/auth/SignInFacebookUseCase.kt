package com.example.hobbittracker.domain.usecase.auth

import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.repository.AuthRepository
import com.example.hobbittracker.domain.utils.Result

class SignInFacebookUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        token: String
    ): Result<User> = authRepository.signInFacebook(token)
}