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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

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
                    val habit = result.data.toObject(FirebaseHabit::class.java)!!
                    Result.Success(mapToHabit(habit))
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

    override suspend fun getHabitsAll(): Result<List<Habit>> {
        return try {
            when (val result = collection.get().await()) {
                is Result.Success -> {
                    val habits = result.data.toObjects(FirebaseHabit::class.java)
                    Result.Success(habits.map { mapToHabit(it) })
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
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun getHabitsByCategory(categoryId: Int): Result<List<Habit>> {
        return try {
            val query = collection.whereEqualTo("categoryId", categoryId)

            when (val result = query.get().await()) {
                is Result.Success -> {
                    val habits = result.data.toObjects(FirebaseHabit::class.java)
                    Result.Success(habits.map { mapToHabit(it) })
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
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun addHabit(habit: Habit): Result<Habit> {
        return try {
            val doc = collection.document()
            val newHabit = habit.copy(id = doc.id)

            when (val result = doc.set(mapToFirebaseHabit(newHabit)).await()) {
                is Result.Success -> Result.Success(newHabit)
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
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun updateHabit(id: String, habit: Habit): Result<Void?> {
        return try {
            val doc = collection.document(id)

            val newHabit = mapToFirebaseHabit(habit)

            val query = mapOf(
                "name" to newHabit.name,
                "pickedDays" to newHabit.pickedDays,
                "endDay" to newHabit.endDay,
                "reminderTime" to newHabit.reminderTime,
                "categoryId" to newHabit.categoryId,
                "color" to newHabit.color,
                "completedDays" to newHabit.completedDays
            )

            when (val result = doc.update(query).await()) {
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
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }

    override suspend fun deleteHabit(id: String): Result<Void?> {
        return try {
            val doc = collection.document(id)
            when (val result = doc.delete().await()) {
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
            val newDate = Date.from(
                date.atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )

            val query = collection
                .document(id)
                .update(
                    "completedDays",
                    if (isComplete) FieldValue.arrayUnion(newDate)
                    else FieldValue.arrayRemove(newDate)
                )

            when (val result = query.await()) {
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
            Log.e(TAG, exception.message!!)
            Result.Error(exception)
        }
    }


    private fun setCollection() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser =
                currentUserUseCase() ?: throw RuntimeException("User is not authorized!")

            val docRoot = authStorage.collection.document(currentUser.id)

            collection = docRoot.collection(COLLECTION_NAME)
        }
    }


    private data class FirebaseHabit(
        val id: String = "",
        val name: String,
        val pickedDays: List<DayOfWeek>,
        val endDay: Date,
        val reminderTime: Date? = null,
        val categoryId: Int = 0,
        val color: String? = null,
        val completedDays: List<Date> = listOf(),
        @field:JvmField
        val isComplete: Boolean? = null
    )

    private fun mapToFirebaseHabit(habit: Habit): FirebaseHabit {
        val endDay = Date.from(
            habit.endDay
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        )

        val completedDays: List<Date> = habit.completedDays.map {
            Date.from(
                it.atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )
        }

        val reminderTime = habit.reminderTime?.let {
            Date.from(
                it.atDate(LocalDate.of(1970,1,1))
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            )
        }

        return FirebaseHabit(
            id = habit.id,
            name = habit.name,
            pickedDays = habit.pickedDays,
            endDay = endDay,
            reminderTime = reminderTime,
            categoryId = habit.categoryId,
            color = habit.color,
            completedDays = completedDays,
            isComplete = habit.isComplete
        )
    }


    private fun mapToHabit(habit: FirebaseHabit): Habit {
        val endDay = LocalDate.from(
            habit.endDay.toInstant()
        )

        val completedDays: List<LocalDate> = habit.completedDays.map {
            LocalDate.from(
                habit.endDay.toInstant()
            )
        }

        val reminderTime = habit.reminderTime?.let {
            LocalTime.from(it.toInstant())
        }

        return Habit(
            id = habit.id,
            name = habit.name,
            pickedDays = habit.pickedDays,
            endDay = endDay,
            reminderTime = reminderTime,
            categoryId = habit.categoryId,
            color = habit.color,
            completedDays = completedDays,
            isComplete = habit.isComplete
        )
    }
}