package com.example.hobbittracker.presentation.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.usecase.category.GetCategoriesAllUseCase
import com.example.hobbittracker.domain.usecase.habit.AddHabitUseCase
import com.example.hobbittracker.domain.usecase.habit.GetHabitsAllUseCase
import com.example.hobbittracker.domain.usecase.habit.GetHabitsByCategoryUseCase
import com.example.hobbittracker.domain.utils.Result
import com.example.hobbittracker.domain.utils.sortedlist.SortedMutableList
import com.example.hobbittracker.domain.utils.sortedlist.sortedMutableListOf
import com.example.hobbittracker.domain.utils.sortedlist.toSortedMutableList
import com.example.hobbittracker.presentation.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val app: Application,
    private val getCategoriesAllUseCase: GetCategoriesAllUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val getHabitsAllUseCase: GetHabitsAllUseCase,
    private val getHabitsByCategoryUseCase: GetHabitsByCategoryUseCase
) : BaseViewModel(app) {

    private val _habitsMLD = MutableLiveData<SortedMutableList<Habit>>(sortedMutableListOf())
    val habitsMLD: LiveData<SortedMutableList<Habit>>
        get() = _habitsMLD

    private val _categoriesMLD = MutableLiveData<Array<CategoryHabits>>(arrayOf())
    val categoriesMLD: LiveData<Array<CategoryHabits>>
        get() = _categoriesMLD

    val currentHabitPositionMLD = MutableLiveData<Int>(0)

    fun addHabit(habit: Habit) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = addHabitUseCase(habit)) {
                    is Result.Success -> {
                        _habitsMLD.value?.add(result.data)
                        _toast.value = app.getString(R.string.add_habit_successful)
                    }
                    is Result.Error -> {
                        _toast.value = app.getString(R.string.add_habit_failed) +
                                "<br>" + result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = app.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }

    fun pullCategoriesAll() {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = getCategoriesAllUseCase()) {
                    is Result.Success -> {
                        _categoriesMLD.value = result.data.toTypedArray()
                    }
                    is Result.Error -> {
                        _toast.value = app.getString(R.string.categories_loading_failed) +
                                "<br>" + result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = app.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }


    private var habitsPullMethod: Byte = -2

    fun pullHabitsAll() {
        if(habitsPullMethod == (-1).toByte()) return

        launchDataLoad {
            viewModelScope.launch {
                when (val result = getHabitsAllUseCase()) {
                    is Result.Success -> {
                        _habitsMLD.value = result.data.toSortedMutableList()
                        habitsPullMethod = -1
                    }
                    is Result.Error -> {
                        _toast.value = app.getString(R.string.habits_loading_failed) +
                                "<br>" + result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = app.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }

    fun pullHabitsByCategory(categoryId: Int) {
        if(habitsPullMethod == categoryId.toByte()) return

        launchDataLoad {
            viewModelScope.launch {
                when (val result = getHabitsByCategoryUseCase(categoryId)) {
                    is Result.Success -> {
                        _habitsMLD.value = result.data.toSortedMutableList()
                        habitsPullMethod = categoryId.toByte()
                    }
                    is Result.Error -> {
                        _toast.value = app.getString(R.string.habits_loading_failed) +
                                "<br>" + result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = app.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }
}