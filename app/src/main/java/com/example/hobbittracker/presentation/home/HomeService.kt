package com.example.hobbittracker.presentation.home

import android.app.Activity
import android.widget.TextView
import android.widget.Toast
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.utils.Result
import com.example.hobbittracker.domain.utils.Validator
import com.google.firebase.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

object HomeService {

    class NameValidator : Validator<String> {
        var minNameLength = 4
        var maxNameLength = 10

        override fun validate(data: String): Result<String> {
            val name = data.trim()

            return when {
                name.isEmpty() ->
                    Result.Error(Exception("Name is empty"))
                name.length < minNameLength ->
                    Result.Error(Exception("Use at least $minNameLength characters"))
                name.length > maxNameLength ->
                    Result.Error(Exception("Use at least $maxNameLength characters"))
                else ->
                    Result.Success(name)
            }
        }
    }

    class WeekdaysValidator : Validator<List<MaterialDayPicker.Weekday>> {
        override fun validate(data: List<MaterialDayPicker.Weekday>):
                Result<List<MaterialDayPicker.Weekday>> {
            return when {
                data.isEmpty() ->
                    Result.Error(Exception("No day selected"))
                else ->
                    Result.Success(data)
            }
        }
    }


    class DeadlineValidator : Validator<String> {
        override fun validate(data: String): Result<String> {
            val sdate = data.trim()
            val date: List<String> = sdate.split('/')

            validateDate(date).let {
                return if (it == null)
                    Result.Success(sdate)
                else
                    Result.Error(it)
            }
        }

        private fun validateDate(date: List<String>): Exception? {
            val err = Exception("Date incorrect")

            if (date.size < 3) return err

            try {
                val newDate = date.map {
                    it.toInt()
                }

                val datetime = LocalDateTime.of(newDate[2], newDate[1], newDate[0], 0, 0, 0)
                val now = LocalDateTime.now()

                if (datetime < now) return err
                if (datetime > now.plusYears(5)) return err
            } catch (e: Exception) {
                return e
            }
            return null
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
        endDay: String,
        reminderTime: LocalTime? = null,
        categoryId: Int = 0,
        color: String? = null
    ): Habit = Habit(
        name = name,
        pickedDays = pickedDays.map {
            DayOfWeek.of(it.ordinal)
        },
        endDay = LocalDate.parse(
            endDay,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        ),
        reminderTime = reminderTime,
        categoryId = categoryId,
        color = color
    )
}