package com.example.hobbittracker.presentation.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.usecase.habit.AddHabitUseCase
import com.example.hobbittracker.domain.utils.Result
import com.example.hobbittracker.presentation.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val app: Application,
    private val addHabitUseCase: AddHabitUseCase
) : BaseViewModel(app) {

    private val _currentHabitsMLD = MutableLiveData<MutableList<Habit>>()
    val currentHabitsMLD: LiveData<MutableList<Habit>>
        get() = _currentHabitsMLD

    fun addHabit(habit: Habit) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = addHabitUseCase(habit)) {
                    is Result.Success -> {
                        _currentHabitsMLD.value?.add(result.data)
                        _toast.value = app.getString(R.string.add_habit_successful)
                    }
                    is Result.Error -> {
                        _toast.value = app.getString(R.string.add_habit_failed) +
                                result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = app.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }
}