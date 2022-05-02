package com.example.hobbittracker.presentation.home.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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


class EditHabitFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val vm: HomeViewModel by sharedViewModel<HomeViewModel>()

    private lateinit var act: FragmentActivity

    private lateinit var currentHabit: Habit

    private var alarmTime: LocalTime? = null

    private var deadline: LocalDate? = null

    private var selectedCategoryId: Int = 0

    // save color of habit
    @ColorInt
    private var selectedColor: Int = ColorSheet.NO_COLOR


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

        initSpinner()

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
    }


    // ------------ main
    private fun onEventFinish() {
        vm.replaceFragment(act.supportFragmentManager, DetailsHabitFragment())
    }

    private fun setCurrentHabit() {
        val index = vm.currentHabitPositionMLD.value
        if (index != null)
            currentHabit = vm.habits[index]
        else onEventFinish()
    }


    // ------------- init
    private fun initFields() {
        tv_premium_title.text = currentHabit.name

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

        selectedColor = currentHabit.color
        setColor(selectedColor)

        selectedCategoryId = currentHabit.categoryId
//        tagNamedesc.text = vm.categories[selectedCategoryId].name

        currentHabit.endDay.let {
            deadline = it
            endTime.text = mapDateToString(it)
            endTimeCalendar.selectedDate = CalendarDay.from(
                it.year, it.monthValue, it.dayOfMonth
            )
        }
    }

    private fun hideNavigation() {
        act.buttomNavigation.visibility = View.INVISIBLE
        act.btn_add.visibility = View.INVISIBLE
    }

    // -------------- color picker
    private fun setupColorSheet() {
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


    // --------- Spinner
    private fun initSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            vm.categories.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySelector.adapter = adapter

        categorySelector.let {
            it.setSelection(selectedCategoryId)
            if (!HomeViewModel.USER_VERIFIED) {
                it.isEnabled = false
                btn_getPremium.visibility = VISIBLE
                btn_getPremium.setOnClickListener {
                    vm.replaceFragment(
                        requireActivity().supportFragmentManager,
                        BillingFragment()
                    )
                }
            } else {
                it.onItemSelectedListener = this
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        selectedCategoryId = vm.categories.find {
            it.name == parent.getItemAtPosition(pos).toString()
        }?.id ?: 0
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }


    // -------- date and time pickers
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


    // ---------- validators
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

    // ------- End
    private fun onEventDone() {
        if (!validateName() || !validateWeekdays() || !validateDeadline()) return

        val habitName = habitName.text.toString()
        val pickedDays = day_picker.selectedDays
        val reminderTime = alarmTime
        val endDay = deadline!!
        val color = selectedColor

        val habit = HomeService.mapToHabit(
            habitName, pickedDays, endDay, reminderTime, selectedCategoryId, color,
            id = currentHabit.id,
            createdDay = currentHabit.createdDay,
            reminderId = currentHabit.reminderId
        )

        vm.editHabit(habit)

        onEventFinish()
    }
}


