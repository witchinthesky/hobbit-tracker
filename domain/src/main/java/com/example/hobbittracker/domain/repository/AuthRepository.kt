package com.example.hobbittracker.domain.repository

import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.utils.Result

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User>

    suspend fun signInGoogle(
        idToken: String,
        accessToken: String?
    ): Result<User>

    suspend fun signInFacebook(
        token: String
    ): Result<User>

    suspend fun resetPassword(
        email: String
    ): Result<Void?>

    suspend fun currentUser(): User?

    suspend fun logout()
}