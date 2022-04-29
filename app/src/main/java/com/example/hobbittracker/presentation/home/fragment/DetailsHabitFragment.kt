package com.example.hobbittracker.presentation.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.data.valstore.CongratulationShowStorage
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.presentation.home.HomeViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_user_contrags.view.*
import kotlinx.android.synthetic.main.fragment_details_habit.*
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.roundToInt

class DetailsHabitFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var currentHabit: Habit

    private lateinit var analyticsInfo: AnalyticsInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideNavigation()

        vm.habitsMLD.observe(viewLifecycleOwner) {
            setCurrentHabit()

            initToolbar()

            initTopBlock()

            initAnalytics()

            initCalendar()

            initCompletionState()
        }

        initHabitButtons()
    }


    // ----------- Main
    private fun hideNavigation() {
        requireActivity().buttomNavigation.visibility = View.INVISIBLE
        requireActivity().btn_add.visibility = View.INVISIBLE
    }

    private fun initCompletionState() {
        runBlocking {
            val storage = CongratulationShowStorage(
                requireContext(),
                currentHabit.id
            )

            if (analyticsInfo.completionRate > 99) {
                if (currentHabit.isComplete != true) {
                    vm.completeHabit()
                    storage(false)
                    initTopBlock()
                }
                if (!storage()) {
                    startCongratulationsDialog()
                    storage(true)
                }
            } else if (LocalDate.now() >= currentHabit.endDay) {
                if (currentHabit.isComplete != false) {
                    vm.missedHabit()
                    initTopBlock()
                }
            } else if (currentHabit.isComplete != null) {
                vm.duringHabit()
                initTopBlock()
            }
        }
    }

    private fun startCongratulationsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_user_contrags,
            null
        )
        val dialog: AlertDialog = AlertDialog
            .Builder(requireContext())
            .setView(dialogView)
            .show()

        dialogView.btn_createNewHabit.setOnClickListener {
            vm.replaceFragment(
                requireActivity().supportFragmentManager,
                NewHabitFragment()
            )
            dialog.dismiss()
        }

        dialogView.btn_continue.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun initToolbar() {
        btn_cancel.setOnClickListener {
            onEventFinish()
        }

        btn_edit.setOnClickListener {
            vm.replaceFragment(requireActivity().supportFragmentManager, EditHabitFragment())
        }

        tv_premium_title.text = currentHabit.name
    }

    private fun initTopBlock() {
        // title
        tv_habitNameTitle2.text = currentHabit.name

        // is complete text
        tv_isCompleted.text = when (currentHabit.isComplete) {
            true -> getString(R.string.details_is_complete)
            false -> getString(R.string.details_is_missed)
            null -> getString(R.string.details_is_during)
        }

        // repeat Days
        currentHabit.pickedDays.map {
            it.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            )
        }.let {
            tv_repeatDays.text =
                if (it.size == 7) getString(R.string.details_repeat_everyday)
                else getString(R.string.details_repeat_at) + it.joinToString(prefix = " ")
        }

        // reminder time alarm
        val time = currentHabit.reminderTime?.toString()
        tv_reminderTime.text =
            if (time == null) getString(R.string.details_remind_dont)
            else getString(R.string.details_remind_at) + " $time"
    }

    private fun initHabitButtons() {
        val now = LocalDate.now()
        btn_markAsComplete.setOnClickListener {
            vm.completeHabitDay(now)
            onEventFinish()
        }

        btn_markAsMissed.setOnClickListener {
            vm.missedHabitDay(now)
            onEventFinish()
        }

        btn_deleteHabit.setOnClickListener {
            vm.deleteHabit(currentHabit)
            onEventFinish()
        }
    }

    private fun onEventFinish() {
        vm.replaceFragment(requireActivity().supportFragmentManager, DashboardFragment())
    }

    private fun setCurrentHabit() {
        val index = vm.currentHabitPositionMLD.value
        if (index != null)
            currentHabit = vm.habits[index]
        else onEventFinish()
    }


    // -------------- Analytics

    private class AnalyticsInfo(
        private val pickedDays: List<DayOfWeek>,
        private val completedDays: MutableList<LocalDate>,
        private val endDay: LocalDate,
        private val startDay: LocalDate
    ) {
        var longestStreak: Int = 0
            private set

        var currentStreak: Int = 0
            private set

        var averageStreak: Int = 0
            private set

        var completionRate: Float = 0.0f
            private set

        init {
            calculate()
        }

        fun calculate() {
            if (completedDays.isEmpty()) return
            // init
            completedDays.sort()
            var current_strike = 0
            var sum_strike = 0
            var count_strike = 0
            var max_strike = 0
            var nearest_day = getNearestPickedDay(startDay.minusDays(1))
            var completion_days = 0
            var missed_days = 0


            // calc before first completed
            while (nearest_day < completedDays[0]) {
                missed_days++
                nearest_day = getNearestPickedDay(nearest_day)
            }
            // calc after first completed day
            for (i in completedDays) {
                if (i > nearest_day) {
                    if (max_strike < current_strike) {
                        max_strike = current_strike
                    }
                    sum_strike += current_strike
                    count_strike++
                    current_strike = 0
                    missed_days++
                    nearest_day = getNearestPickedDay(i)
                } else if (i == nearest_day) {
                    nearest_day = getNearestPickedDay(i)
                    completion_days++
                }

                current_strike++
            }
            if (max_strike < current_strike)
                max_strike = current_strike
            sum_strike += current_strike
            count_strike++

            // calc after last completed day
            while (nearest_day <= endDay) {
                missed_days++
                nearest_day = getNearestPickedDay(nearest_day)
            }

            // end
            currentStreak = current_strike
            longestStreak = max_strike
            averageStreak = (sum_strike / count_strike.toFloat()).roundToInt()
            completionRate = (completion_days / (missed_days + completion_days).toFloat() * 100)
            if (completionRate.isNaN()) completionRate = 0f
        }

        private fun getNearestPickedDay(day: LocalDate): LocalDate {
            var date = day.plusDays(1)
            if (pickedDays.isEmpty()) return date

            for (i in 0 until 7) {
                if (date.dayOfWeek in pickedDays) break
                else date = date.plusDays(1)
            }
            return date
        }
    }

    private fun initAnalytics() {
        analyticsInfo = AnalyticsInfo(
            currentHabit.pickedDays,
            currentHabit.completedDays,
            currentHabit.endDay,
            currentHabit.createdDay
        )
        tv_longestStreak.text = analyticsInfo.longestStreak.toString()
        tv_currentStreak.text = analyticsInfo.currentStreak.toString()
        tv_averageStreak.text = analyticsInfo.averageStreak.toString()
        val completionRateText = String.format("%.1f", analyticsInfo.completionRate) + " %"
        tv_completionRate.text = completionRateText
    }


    // --------- Calendar

    private fun initCalendar() {
        val completedDays = currentHabit.completedDays.map {
            CalendarDay.from(it.year, it.monthValue, it.dayOfMonth)
        }
        val endDay = currentHabit.endDay.let {
            CalendarDay.from(it.year, it.monthValue, it.dayOfMonth)
        }
        calendarView_habitDetail.removeDecorators()
        calendarView_habitDetail.addDecorators(DecoratorDays(completedDays))
        calendarView_habitDetail.addDecorators(DecoratorEndDay(endDay))
    }

    inner class DecoratorDays(
        private val dayList: List<CalendarDay>
    ) : DayViewDecorator {

        val drawable = ContextCompat.getDrawable(
            requireActivity().applicationContext,
            R.drawable.icon_calendar_check
        )

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dayList.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setSelectionDrawable(drawable!!)
        }
    }

    inner class DecoratorEndDay(
        private val endDay: CalendarDay
    ) : DayViewDecorator {

        val drawable = ContextCompat.getDrawable(
            requireActivity().applicationContext,
            R.drawable.icon_calendar_end_day
        )

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return endDay == day
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setSelectionDrawable(drawable!!)
        }
    }
}