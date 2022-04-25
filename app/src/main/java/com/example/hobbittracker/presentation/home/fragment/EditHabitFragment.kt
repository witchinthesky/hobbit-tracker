package com.example.hobbittracker.presentation.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.presentation.home.HomeService
import com.example.hobbittracker.presentation.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_edit_habit.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class EditHabitFragment : Fragment() {

    private var alarmTime: LocalTime? = null

    private val vm: HomeViewModel by sharedViewModel<HomeViewModel>()

    private lateinit var act: FragmentActivity

    private lateinit var currentHabit: Habit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act = this.requireActivity()

        setCurrentHabit()

        initFields()

        btn_cancel.setOnClickListener {
            onEventFinish()
        }

        btn_done.setOnClickListener {
            onEventDone()
        }

        switcher.setOnCheckedChangeListener { btn, isChecked ->
            if (isChecked)
                TimePickerBottomSheet.Builder(parentFragmentManager)
                    .setOnSaveListener(this::onTimePicked)
                    .setOnCancelListener { btn.isChecked = false }
                    .build()
                    .show()
            else
                alarmTime = null
        }

        // init spinner



    }

    private fun setCurrentHabit() {
        val index = vm.currentHabitPositionMLD.value
        if (index != null)
            currentHabit = vm.habits[index]
        else onEventFinish()
    }

    private fun initFields() {
        tv_habitNameTitle.text = currentHabit.name

        habitName.setText(currentHabit.name)

        day_picker.setSelectedDays(
            currentHabit.pickedDays.map {
                MaterialDayPicker.Weekday.valueOf(
                    it.name
                )
            }
        )

        currentHabit.reminderTime?.let() {
            switcher.isChecked = true
            onTimePicked(it)
        }

            /*
        categorySelector.setText(
            vm.categories[currentHabit.categoryId].name
        )
*/
        colorName.setText(
            currentHabit.color
        )

        endTime.setText(
            currentHabit.endDay.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
        )
    }

    private fun onEventFinish() {
        vm.replaceFragment(act.supportFragmentManager, DetailsHabitFragment())
    }

    private fun onTimePicked(time: LocalTime) {
        alarmTime = time
        textView20.text = time.toString()
    }

    private fun validateName(): Boolean {
        return HomeService.textViewValidateHandler(
            habitName, HomeService.NameValidator()
        )
    }

    private fun validateWeekdays(): Boolean {
        return HomeService.validateHandler(
            day_picker.selectedDays, HomeService.WeekdaysValidator(), act
        )
    }

    private fun validateDeadline(): Boolean {
        return HomeService.textViewValidateHandler(
            endTime, HomeService.DeadlineValidator()
        )
    }

    private fun onEventDone() {
        if (!validateName() || !validateWeekdays() || !validateDeadline()) return

        val habitName = habitName.text.toString()
        val pickedDays = day_picker.selectedDays
        val reminderTime = alarmTime
            // val category = categorySelector.text.toString()
        val color = colorName.text.toString()
        val endDay = endTime.text.toString()

        val habit = HomeService.mapToHabit(
            habitName,
            pickedDays,
            endDay,
            reminderTime,
            0,
            color,
            id = currentHabit.id,
            createdDay = currentHabit.createdDay
        )

        vm.editHabit(habit)

        onEventFinish()
    }
}


