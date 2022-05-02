package com.example.hobbittracker.presentation.auth

import android.util.Patterns
import android.widget.TextView
import com.example.hobbittracker.domain.utils.Result
import com.example.hobbittracker.domain.utils.Validator
import com.google.android.gms.auth.api.identity.BeginSignInRequest

object AuthService {

    private const val MIN_NAME_LENGTH = 4
    private const val MIN_EMAIL_LENGTH = 6
    private const val MIN_PASSWORD_LENGTH = 4

    interface AuthValidator : Validator<String> {

        override fun validate(data: String): Result<String>
    }

    class NameValidator : AuthValidator {
        override fun validate(data: String): Result<String> {
            val name = data.trim()

            return when {
                name.isEmpty() ->
                    Result.Error(Exception("Name is empty"))
                name.length < MIN_NAME_LENGTH ->
                    Result.Error(Exception("Use at least $MIN_EMAIL_LENGTH characters"))
                else ->
                    Result.Success(name)
            }
        }
    }

    class EmailValidator : AuthValidator {
        override fun validate(data: String): Result<String> {
            val email = data.trim()

            return when {
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    Result.Error(Exception("Enter a valid email"))
                email.isEmpty() ->
                    Result.Error(Exception("Email is empty"))
                email.length < MIN_EMAIL_LENGTH ->
                    Result.Error(Exception("Use at least $MIN_EMAIL_LENGTH characters"))
                else ->
                    Result.Success(email)
            }
        }

    }

    class PasswordValidator : AuthValidator {
        override fun validate(data: String): Result<String> {
            val password = data.trim()

            return when {
                password.length < MIN_PASSWORD_LENGTH ->
                    Result.Error(Exception("Use at least $MIN_PASSWORD_LENGTH characters"))
                password.isEmpty() ->
                    Result.Error(Exception("Password is empty"))
                else ->
                    Result.Success(password)
            }
        }
    }

    fun textViewValidateHandler(validator: AuthValidator, textView: TextView): Boolean {
        return when (val result = validator.validate(textView.text.toString())) {
            is Result.Success -> {
                textView.text = result.data
                true
            }
            is Result.Error -> {
                textView.error = result.exception.message
                false
            }
            else -> false
        }
    }

    fun createGoogleSignInRequest(webClientId: String, onlyAuthorizedAccounts: Boolean): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(webClientId)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(onlyAuthorizedAccounts)
                    .build()
            )
            .build()
}