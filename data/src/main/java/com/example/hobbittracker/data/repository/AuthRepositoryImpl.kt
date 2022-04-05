package com.example.hobbittracker.data.repository

import android.util.Log
import com.example.hobbittracker.data.extension.await
import com.example.hobbittracker.data.storage.Storage
import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.repository.AuthRepository
import com.example.hobbittracker.domain.utils.Result
import com.google.firebase.auth.*

class AuthRepositoryImpl(
    private val storage: Storage<String, User>
) : AuthRepository {

    private val tag = this.javaClass.simpleName

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val currentUser: User? = null;

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        try {
            return when (val resultDocumentSnapshot =
                firebaseAuth.signInWithEmailAndPassword(email, password).await()) {
                is Result.Success -> {
                    resultDocumentSnapshot.data.user?.let { firebaseUser ->
                        Log.i(tag, "Login Successful!")
                        return loadUserInStorage(firebaseUser.uid)
                    }
                    Result.Error(RuntimeException("User not loaded"))
                }
                is Result.Error -> {
                    Log.e(tag, "${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled -> {
                    Log.e(tag, "${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User> {
        try {
            return when (val resultDocumentSnapshot =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()) {
                is Result.Success -> {
                    resultDocumentSnapshot.data.user?.let { firebaseUser ->
                        val user = mapToDomain(firebaseUser, email = email, name = name)
                        Log.i(tag, "Registration Successful!")
                        return saveUserInStorage(user)
                    }
                    Result.Error(RuntimeException("Sign in is successfully, but response model is empty"))
                }
                is Result.Error -> {
                    Log.e(tag, "${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled -> {
                    Log.e(tag, "${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }

    override suspend fun signInGoogle(
        idToken: String,
        accessToken: String?
    ): Result<User> {
        try {
            val credential: AuthCredential =
                GoogleAuthProvider.getCredential(idToken, accessToken)

            return when (val result = signInWithCredential(credential)) {
                is Result.Success -> {
                    Log.d(tag, "signInWithCredential Google Success - ${result.data?.user?.uid}")
                    saveUserInStorageAtApi(result)
                }
                is Result.Error -> {
                    Log.e(tag, "${result.exception}")
                    Result.Error(result.exception)
                }
                is Result.Canceled -> {
                    Log.e(tag, "${result.exception}")
                    Result.Canceled(result.exception)
                }
            }

        } catch (exception: Exception) {
            Log.e(tag, exception.message.toString())
            return Result.Error(exception)
        }
    }


    override suspend fun signInFacebook(
        token: String
    ): Result<User> {
        try {
            val credential = FacebookAuthProvider.getCredential(token)
            return when (val result = signInWithCredential(credential)) {
                is Result.Success -> {
                    Log.i(tag, "signInWithCredential Facebook Success - ${result.data?.user?.uid}")
                    saveUserInStorageAtApi(result)
                }
                is Result.Error -> {
                    Log.e(tag, "${result.exception}")
                    Result.Error(result.exception)
                }
                is Result.Canceled -> {
                    Log.e(tag, "${result.exception}")
                    Result.Canceled(result.exception)
                }
            }
        } catch (exception: Exception) {
            Log.e(tag, exception.message.toString())
            return Result.Error(exception)
        }
    }

    override suspend fun resetPassword(email: String): Result<Void?> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun currentUser(): User? {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser == null) null
        else when (val result = storage.load(currentUser.uid)) {
            is Result.Success -> {
                result.data
            }
            is Result.Error -> {
                Log.e(tag, "${result.exception}")
                null
            }
            is Result.Canceled -> {
                Log.e(tag, "${result.exception}")
                null
            }
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
//        Firebase.auth.signOut()
    }


    private suspend fun signInWithCredential(
        authCredential: AuthCredential
    ): Result<AuthResult?> =
        firebaseAuth.signInWithCredential(authCredential).await()


    private suspend fun saveUserInStorageAtApi(
        result: Result.Success<AuthResult?>
    ): Result<User> {
        result.data?.user?.providerData?.get(1)?.let {
            it.displayName?.let { name ->

                val email: String = if (it.email == null) {
                    Log.e(tag, "user email is empty"); ""
                } else it.email.toString()

                val pict: String = if (it.photoUrl == null) "" else it.photoUrl.toString()

                return saveUserInStorage(
                    mapToDomain(firebaseUser = result.data!!.user!!, name = name, email = email, pict)
                )
//                throw NullPointerException("user email is empty")
            }
            Log.e(tag, "user name is empty")
            throw NullPointerException("user name is empty")
        }
        Log.e(tag, "user model is empty")
        throw NullPointerException("user model is empty")
    }

    private suspend fun loadUserInStorage(userId: String): Result<User> =
        resultWrapper(storage.load(userId)) {
            Log.d(tag, it.toString())
            Result.Success(it)
        }

    private suspend fun saveUserInStorage(user: User): Result<User> =
        resultWrapper(storage.save(user.id, user)) {
            Log.i(tag, "User saved!")
            Result.Success(user)
        }


    private fun <D, O> resultWrapper(
        result: Result<D>,
        action: (data: D) -> Result<O>
    ): Result<O> {
        return when (result) {
            is Result.Success -> {
                action(result.data)
            }
            is Result.Error -> {
                Log.e(tag, "${result.exception}")
                Result.Error(result.exception)
            }
            is Result.Canceled -> {
                Log.e(tag, "${result.exception}")
                Result.Canceled(result.exception)
            }
        }
    }

    private fun mapToDomain(
        firebaseUser: FirebaseUser,
        name: String,
        email: String,
        profilePicture: String = ""
    ): User = User(
        id = firebaseUser.uid,
        name = name,
        email = email,
        profilePicture = profilePicture
    )
}