package com.example.hobbittracker.presentation.home.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.domain.utils.sortedlist.SortedMutableList
import kotlinx.android.synthetic.main.item_habit.view.*
import java.time.DayOfWeek
import java.time.LocalDate

class HabitListAdapter(private val context: Context?) :
    RecyclerView.Adapter<HabitListAdapter.HabitListViewHolder>() {

    lateinit var habits: SortedMutableList<Habit>

    private var onDetailsClick: (Habit, Int) -> Unit = fun(_, _) {}


    inner class HabitListViewHolder(
        itemView: View,
        private val detailsClickListener: (Habit, Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val today: DayOfWeek = LocalDate.now().dayOfWeek
        private val tomorrow: DayOfWeek = today.plus(1)

        fun bind(data: Habit, position: Int) {
            itemView.tv_habitName.text = data.name

            for (i in data.pickedDays) {
                itemView.day_picker.selectDay(MaterialDayPicker.Weekday.valueOf(i.name))

                if (i == today) {
                    itemView.textView_isToday.text =
                        context?.resources?.getString(R.string.today) ?: "Today"
                    itemView.textView_isToday.visibility = View.VISIBLE
                    itemView.imageView_dot.visibility = View.VISIBLE
                } else {
                    if (i == tomorrow) {
                        itemView.textView_isToday.text =
                            context?.resources?.getString(R.string.today) ?: "Tomorrow"
                        itemView.textView_isToday.visibility = View.VISIBLE
                        itemView.imageView_dot.visibility = View.INVISIBLE
                    } else {
                        itemView.textView_isToday.visibility = View.INVISIBLE
                        itemView.imageView_dot.visibility = View.INVISIBLE
                    }
                    itemView.textView_isToday.setBackgroundColor(Color.parseColor("#CCFFFFFF"))
                    itemView.button_habit.setBackgroundColor(Color.parseColor("#CCFFFFFF"))
                }
            }

            itemView.day_picker.setDayPressedListener { day, isSelected ->
                if (isSelected) itemView.day_picker.deselectDay(day)
            }

            itemView.button_habit.setOnClickListener {
                detailsClickListener(data, position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitListViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false)
        return HabitListViewHolder(itemView, this::onDetailsClick.get())
    }

    override fun onBindViewHolder(holder: HabitListViewHolder, position: Int) {
        holder.bind(habits[position], position)
    }

    override fun getItemCount(): Int = habits.size


    fun setOnDetailsClick(callback: (Habit, Int) -> Unit) {
        onDetailsClick = callback
    }
}