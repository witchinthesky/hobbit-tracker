package com.example.hobbittracker.data.storage

import com.example.hobbittracker.data.extension.await
import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.utils.Result
import com.google.firebase.firestore.FirebaseFirestore

class AuthStorage : Storage<String, User> {

    companion object {
        private const val USER_COLLECTION_NAME = "users"
    }

    private val firestoreInstance:FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection = firestoreInstance.collection(USER_COLLECTION_NAME)

    override suspend fun save(key: String, data: User): Result<Void?> {
        return try {
            userCollection.document(key).set(data).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun load(key: String): Result<User> {
        return try {
            when (val resultDocumentSnapshot =
                userCollection.document(key).get().await()) {
                is Result.Success -> {
                    val user = resultDocumentSnapshot.data.toObject(User::class.java)!!
                    Result.Success(user)
                }
                is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
                is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}