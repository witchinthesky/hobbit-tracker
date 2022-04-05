package com.example.hobbittracker.domain.usecase.auth

import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.repository.AuthRepository
import com.example.hobbittracker.domain.utils.Result

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> = authRepository.login(email = email, password = password)
}