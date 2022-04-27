package com.example.hobbittracker.presentation.home

import android.app.Activity
import android.app.Application
import android.widget.TextView
import android.widget.Toast
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.utils.Result
import com.example.hobbittracker.domain.utils.Validator
import com.google.firebase.Timestamp
import java.time.*
import java.util.*

object HomeService {

    lateinit var app: Application

    class NameValidator : Validator<String> {
        var minNameLength = 4
        var maxNameLength = 10

        override fun validate(data: String): Result<String> {
            val name = data.trim()

            val empty = app.getString(R.string.validate_name_empty)
            val min_len = app.getString(R.string.validate_min_length)
                .replace("[*]", minNameLength.toString())
            val max_len = app.getString(R.string.validate_max_length)
                .replace("[*]", maxNameLength.toString())

            return when {
                name.isEmpty() ->
                    Result.Error(Exception(empty))
                name.length < minNameLength ->
                    Result.Error(Exception(min_len))
                name.length > maxNameLength ->
                    Result.Error(Exception(max_len))
                else ->
                    Result.Success(name)
            }
        }
    }

    class WeekdaysValidator : Validator<List<MaterialDayPicker.Weekday>> {
        override fun validate(data: List<MaterialDayPicker.Weekday>):
                Result<List<MaterialDayPicker.Weekday>> {
            val empty = app.getString(R.string.validate_days_empty)

            return when {
                data.isEmpty() ->
                    Result.Error(Exception(empty))
                else ->
                    Result.Success(data)
            }
        }
    }


    class DeadlineValidator : Validator<LocalDate?> {
        override fun validate(data: LocalDate?): Result<LocalDate?> {
            val empty = app.getString(R.string.validate_date_empty)

            return when (data) {
                null -> Result.Error(Exception(empty))
                else -> Result.Success(data)
            }
        }

        companion object {
            fun mapToTimestamp(year: String, month: String, day: String): Timestamp {
                return Timestamp(
                    Date(
                        LocalDateTime
                            .of(year.toInt(), month.toInt(), day.toInt(), 0, 0, 0)
                            .atZone(ZoneOffset.UTC)
                            .toEpochSecond()
                    )
                )
            }
        }
    }


    fun textViewValidateHandler(textView: TextView, validator: Validator<String>): Boolean {
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

    fun <T> validateHandler(value: T, validator: Validator<T>, activity: Activity): Boolean {
        return when (val result = validator.validate(value)) {
            is Result.Success -> {
                true
            }
            is Result.Error -> {
                Toast.makeText(activity, result.exception.message, Toast.LENGTH_SHORT).show()
                false
            }
            else -> false
        }
    }

    fun mapToHabit(
        name: String,
        pickedDays: List<MaterialDayPicker.Weekday>,
        endDay: LocalDate,
        reminderTime: LocalTime? = null,
        categoryId: Int = 0,
        color: String? = null,
        id: String = "",
        createdDay: LocalDate? = null,
        reminderId: Int? = 0
    ): Habit = Habit(
        id = id,
        name = name,
        pickedDays = pickedDays.map {
            DayOfWeek.valueOf(it.name)
        },
        reminderTime = reminderTime,
        reminderId = reminderId ?: System.currentTimeMillis().toInt(),
        createdDay = createdDay ?: LocalDate.now(),
        endDay = endDay,
        categoryId = categoryId,
        color = color
    )
}