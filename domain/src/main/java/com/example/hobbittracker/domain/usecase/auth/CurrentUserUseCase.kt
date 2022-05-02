package com.example.hobbittracker.domain.usecase.auth

import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.repository.AuthRepository

class CurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? = authRepository.currentUser()
}