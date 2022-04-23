package com.example.hobbittracker.presentation.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.usecase.category.GetCategoriesAllUseCase
import com.example.hobbittracker.domain.usecase.habit.*
import com.example.hobbittracker.domain.utils.Result
import com.example.hobbittracker.presentation.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val app: Application,
    private val getCategoriesAllUseCase: GetCategoriesAllUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val getHabitsAllUseCase: GetHabitsAllUseCase,
    private val getHabitsByCategoryUseCase: GetHabitsByCategoryUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
) : BaseViewModel(app) {


    var habits: MutableList<Habit> = mutableListOf()
        private set
    private val _habitsMLD = MutableLiveData<Long>(0L)
    val habitsMLD: LiveData<Long>
        get() = _habitsMLD


    var categories: Array<CategoryHabits> = arrayOf()
        private set
    private val _categoriesMLD = MutableLiveData<Long>(0L)
    val categoriesMLD: LiveData<Long>
        get() = _categoriesMLD


    val currentHabitPositionMLD = MutableLiveData<Int?>(0)


    private fun notifyListMLD(mld: MutableLiveData<Long>) {
        mld.value = System.currentTimeMillis()
    }


    fun addHabit(habit: Habit) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = addHabitUseCase(habit)) {
                    is Result.Success -> {
                        habits.add(result.data)
                        habits.sort()
                        notifyListMLD(_habitsMLD)
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
                        categories = result.data.toTypedArray()
                        notifyListMLD(_categoriesMLD)
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
        if (habitsPullMethod == (-1).toByte()) return

        launchDataLoad {
            viewModelScope.launch {
                when (val result = getHabitsAllUseCase()) {
                    is Result.Success -> {
                        habits = result.data.toMutableList()
                        habits.sort()
                        habitsPullMethod = -1
                        notifyListMLD(_habitsMLD)
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
        if (habitsPullMethod == categoryId.toByte()) return
        else
            launchDataLoad {
                viewModelScope.launch {
                    when (val result = getHabitsByCategoryUseCase(categoryId)) {
                        is Result.Success -> {
                            habits = result.data.toMutableList()
                            habits.sort()
                            habitsPullMethod = categoryId.toByte()
                            notifyListMLD(_habitsMLD)
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

    fun editHabit(habit: Habit) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = updateHabitUseCase(habit.id, habit)) {
                    is Result.Success -> {
                        val index = currentHabitPositionMLD.value
                        if (index != null) {
                            habits[index] = habit
                            habits.sort()
                            notifyListMLD(_habitsMLD)
                            currentHabitPositionMLD.value = habits.indexOf(habit)
                            _toast.value = app.getString(R.string.habit_update_successful)
                        }
                    }
                    is Result.Error -> {
                        _toast.value = app.getString(R.string.habit_update_failed) +
                                "<br>" + result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = app.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = deleteHabitUseCase(habit.id)) {
                    is Result.Success -> {
                        val index = currentHabitPositionMLD.value
                        if (index != null) {
                            habits.removeAt(index)
                            notifyListMLD(_habitsMLD)
                            _toast.value = app.getString(R.string.habit_update_successful)
                        }
                    }
                    is Result.Error -> {
                        _toast.value = app.getString(R.string.habit_update_failed) +
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