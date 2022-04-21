package com.example.hobbittracker.data.repository

import android.util.Log
import com.example.hobbittracker.data.extension.await
import com.example.hobbittracker.data.storage.AuthStorage
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.repository.HabitRepository
import com.example.hobbittracker.domain.usecase.auth.CurrentUserUseCase
import com.example.hobbittracker.domain.utils.Result
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitRepositoryImpl(
    private val authStorage: AuthStorage,
    private val currentUserUseCase: CurrentUserUseCase
) : HabitRepository {
    
    companion object {
        private const val COLLECTION_NAME = "habits"
        
        private const val TAG = "HabitRepositoryImpl"
    }

    private lateinit var collection: CollectionReference

    init {
        setCollection()
    }

    override suspend fun getHabit(id: String): Result<Habit> {
        return try {
            when (val result = collection.document(id).get().await()) {
                is Result.Success -> {
                    val habit = result.data.toObject(Habit::class.java)!!
                    Result.Success(habit)
                }
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun getHabitsAll(): Result<List<Habit>> {
        return try {
            when (val result = collection.get().await()) {
                is Result.Success -> {
                    val habits = result.data.toObjects(Habit::class.java)
                    Result.Success(habits)
                }
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun getHabitsByCategory(categoryId: Int): Result<List<Habit>> {
        return try {
            val query = collection.whereEqualTo("categoryId", categoryId)

            when (val result = query.get().await()) {
                is Result.Success -> {
                    val habits = result.data.toObjects(Habit::class.java)
                    Result.Success(habits)
                }
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun addHabit(habit: Habit): Result<Habit> {
        return try {
            val doc = collection.document()
            val newHabit = habit.copy(id = doc.id)

            when (val result = doc.set(newHabit).await()) {
                is Result.Success -> {
                    Result.Success(newHabit)
                }
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun updateHabit(id: String, habit: Habit): Result<Void?> {
        return try {
            val doc = collection.document(id)

            val query = mapOf(
                "name" to habit.name,
                "pickedDays" to habit.pickedDays,
                "endDay" to habit.endDay,
                "reminderTime" to habit.reminderTime,
                "categoryId" to habit.categoryId,
                "color" to habit.color,
                "completedDays" to habit.completedDays
            )

            when (val result = doc.update(query).await()) {
                is Result.Success -> Result.Success(null)
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun deleteHabit(id: String): Result<Void?> {
        return try {
            val doc = collection.document(id)
            when (val result = doc.delete().await()) {
                is Result.Success -> Result.Success(null)
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun setStateHabit(id: String, isComplete: Boolean): Result<Void?> {
        return try {
            val query = collection
                .document(id)
                .update("complete", isComplete)

            when (val result = query.await()) {
                is Result.Success -> Result.Success(null)
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun setStateDayHabit(
        id: String,
        date: LocalDate,
        isComplete: Boolean
    ): Result<Void?> {
        return try {
            val query = collection
                .document(id)
                .update(
                    "completedDays",
                    if (isComplete) FieldValue.arrayUnion(date)
                    else FieldValue.arrayRemove(date)
                )

            when (val result = query.await()) {
                is Result.Success -> Result.Success(null)
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }


    private fun setCollection() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = currentUserUseCase() ?: throw RuntimeException("User is not authorized!")

            val docRoot = authStorage.collection.document(currentUser.id)

            collection = docRoot.collection(COLLECTION_NAME)
        }
    }
}