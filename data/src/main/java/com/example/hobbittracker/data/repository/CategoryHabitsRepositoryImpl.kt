package com.example.hobbittracker.data.repository

import android.util.Log
import com.example.hobbittracker.data.extension.await
import com.example.hobbittracker.data.storage.AuthStorage
import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.repository.CategoryHabitsRepository
import com.example.hobbittracker.domain.usecase.auth.CurrentUserUseCase
import com.example.hobbittracker.domain.utils.Result
import com.google.firebase.firestore.CollectionReference

class CategoryHabitsRepositoryImpl(
    private val authStorage: AuthStorage,
    private val currentUserUseCase: CurrentUserUseCase
) : CategoryHabitsRepository {

    companion object {
        private const val COLLECTION_NAME = "categories"
        private const val TAG = "CategoryHabitsRepositoryImpl"
    }

    override suspend fun getCategory(id: Int): Result<CategoryHabits> {
        return try {
            when (val result =
                getCollection().document(id.toString()).get().await()) {
                is Result.Success -> {
                    val category = result.data
                        .toObject(CategoryHabits::class.java)!!
                    Result.Success(category)
                }
                is Result.Error -> {
                    Log.e(TAG, result.exception.message.toString())
                    Result.Error(result.exception)
                }
                is Result.Canceled -> {
                    Log.w(TAG, result.exception?.message ?: "Habit Request Canceled!")
                    Result.Canceled(result.exception)
                }
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message.toString())
            Result.Error(exception)
        }
    }

    override suspend fun getCategoriesAll(): Result<List<CategoryHabits>> {
        return try {
            when (val result =
                getCollection().get().await()) {
                is Result.Success -> {
                    val cats = result.data.toObjects(CategoryHabits::class.java)
                    Result.Success(cats)
                }
                is Result.Error -> {
                    Log.e(TAG, result.exception.message.toString())
                    Result.Error(result.exception)
                }
                is Result.Canceled -> {
                    Log.w(TAG, result.exception?.message ?: "Habit Request Canceled!")
                    Result.Canceled(result.exception)
                }
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message.toString())
            Result.Error(exception)
        }
    }

    override suspend fun updateCategory(categoryHabits: CategoryHabits): Result<Void?> {
        val updatedCategory = mapOf(
            "name" to categoryHabits.name,
            "color" to categoryHabits.color
        )

        return try {
            val documentQuery = getCollection()
                .document(categoryHabits.id.toString())
                .update(updatedCategory)

            when (val result = documentQuery.await()) {
                is Result.Success -> Result.Success(null)
                is Result.Error -> {
                    Log.e(TAG, result.exception.message.toString())
                    Result.Error(result.exception)
                }
                is Result.Canceled -> {
                    Log.w(TAG, result.exception?.message ?: "Habit Request Canceled!")
                    Result.Canceled(result.exception)
                }
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message.toString())
            Result.Error(exception)
        }
    }


    private var _collection: CollectionReference? = null
    private suspend fun getCollection(): CollectionReference {
        _collection?.let { return it }

        val currentUser = currentUserUseCase()
            ?: throw RuntimeException("User is not authorized!")

        val docRoot = authStorage.collection.document(currentUser.id)

        _collection = docRoot.collection(COLLECTION_NAME)
        checkCollection(_collection!!)
        return _collection!!
    }

    private suspend fun checkCollection(collection: CollectionReference) {
        val result = collection.limit(1).get().await()
        if (result is Result.Success && result.data.isEmpty)
            createCategories(collection)
    }

    private suspend fun createCategories(collection: CollectionReference): Result<Void?> {
        var res: Result<Void?> = Result.Success(null)

        listOf<CategoryHabits>(
            CategoryHabits(0, "None"),
            CategoryHabits(1, "Music"),
            CategoryHabits(2, "Study"),
            CategoryHabits(3, "Work")
        ).forEach {
            try {
                val doc = collection.document(it.id.toString())
                when (val result = doc.set(it).await()) {
                    is Result.Error -> {
                        Log.e(TAG, result.exception.message.toString())
                        res = result
                    }
                    is Result.Canceled -> {
                        val mes = result.exception?.message
                            ?: "Create category request canceled!"
                        Log.w(TAG, mes)
                        res = result
                    }
                    else -> {}
                }
            } catch (exception: Exception) {
                Log.e(TAG, exception.message!!)
                res = Result.Error(exception)
            }
        }

        return res
    }
}