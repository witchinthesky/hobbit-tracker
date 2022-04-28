package com.example.hobbittracker.presentation.home.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.presentation.home.HomeService
import com.example.hobbittracker.presentation.home.HomeViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dev.sasikanth.colorsheet.ColorSheet
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_edit_habit.*
import kotlinx.android.synthetic.main.fragment_edit_habit.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class EditHabitFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel<HomeViewModel>()

    private lateinit var act: FragmentActivity

    private var alarmTime: LocalTime? = null

    private var deadline: LocalDate? = null

    private lateinit var currentHabit: Habit

    // save color of habit
    @ColorInt
    private var selectedColor: Int = ColorSheet.NO_COLOR

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_habit, container, false)
        view.colorPicker_button.setOnClickListener {
            setupColorSheet()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act = this.requireActivity()

        hideNavigation()

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

        endTimeCalendar.setOnDateChangedListener { widget, day, selected ->
            if (selected) onDatePicked(widget, day)
        }

        // init spinner
    }

    private fun onEventFinish() {
        vm.replaceFragment(act.supportFragmentManager, DashboardFragment())
    }

    private fun setupColorSheet() {
        selectedColor = currentHabit.color
        setColor(selectedColor)

        val colors = resources.getIntArray(R.array.colors) // get array of colors
        ColorSheet().cornerRadius(8)
            .colorPicker(
                colors = colors,
                selectedColor = selectedColor,
                listener = { color ->
                    selectedColor = color
                    setColor(selectedColor)
                })
            .show(childFragmentManager)
    }

    private fun setColor(@ColorInt color: Int) {
        displayColor.backgroundTintList =
            ColorStateList.valueOf(color) // set color at display color box
        // colorPicker_button.text = ColorSheetUtils.colorToHex(color)  // change to text
    }

    private fun hideNavigation() {
        act.buttomNavigation.visibility = View.INVISIBLE
        act.btn_add.visibility = View.INVISIBLE
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

        currentHabit.endDay.let {
            deadline = it
            endTime.text = mapDateToString(it)
            endTimeCalendar.selectedDate = CalendarDay.from(
                it.year, it.monthValue, it.dayOfMonth
            )
        }
    }

    private fun onTimePicked(time: LocalTime) {
        alarmTime = time
        textView20.text = time.toString()
    }

    private fun onDatePicked(view: MaterialCalendarView, day: CalendarDay) {
        val selectedDay = LocalDate.of(day.year, day.month, day.day)
        val minDay = LocalDate.now().plusDays(1)
        val maxDay = minDay.plusYears(5)

        if (selectedDay < minDay || selectedDay > maxDay) {
            view.clearSelection()
            deadline?.let {
                view.selectedDate = CalendarDay.from(
                    it.year, it.monthValue, it.dayOfMonth
                )
            }
            return
        }

        deadline = selectedDay
        endTime.text = mapDateToString(selectedDay)
    }

    private fun mapDateToString(day: LocalDate) = day.format(
        DateTimeFormatter.ofPattern("dd MMMM yyyy")
    )


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
        return HomeService.validateHandler(
            deadline, HomeService.DeadlineValidator(), act
        )
    }

    private fun onEventDone() {
        if (!validateName() || !validateWeekdays() || !validateDeadline()) return

        val habitName = habitName.text.toString()
        val pickedDays = day_picker.selectedDays
        val reminderTime = alarmTime
        val endDay = deadline!!
        val color = selectedColor
        val category = 0

        val habit = HomeService.mapToHabit(
            habitName,
            pickedDays,
            endDay,
            reminderTime,
            category,
            color,
            id = currentHabit.id,
            createdDay = currentHabit.createdDay,
            reminderId = currentHabit.reminderId
        )

        vm.editHabit(habit)

        onEventFinish()
    }
}


