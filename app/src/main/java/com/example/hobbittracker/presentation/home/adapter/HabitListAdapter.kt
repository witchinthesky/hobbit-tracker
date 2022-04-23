package com.example.hobbittracker.presentation.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.Habit
import kotlinx.android.synthetic.main.item_habit.view.*
import java.time.DayOfWeek
import java.time.LocalDate

class HabitListAdapter(private val context: Context?) :
    RecyclerView.Adapter<HabitListAdapter.HabitListViewHolder>() {

    lateinit var habits: List<Habit>

    private var onDetailsClick: (Habit, Int) -> Unit = fun(_, _) {}

/*    private var habits = SortedList(Habit::class.java,
        object : SortedList.Callback<Habit>() {
            override fun compare(o1: Habit, o2: Habit): Int {
                return o1.compareTo(o2)
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int) {
                notifyItemRangeChanged(position, count)
            }

            override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: Habit, item2: Habit): Boolean {
                return item1.id == item2.id
            }
        })*/

    inner class HabitListViewHolder(
        itemView: View,
        private val detailsClickListener: (Habit, Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val today: DayOfWeek = LocalDate.now().dayOfWeek
        private val tomorrow: DayOfWeek = today.plus(1)

        fun bind(data: Habit, position: Int) {
            itemView.tv_habitName.text = data.name

            val pickDays = data.pickedDays.map {
                MaterialDayPicker.Weekday.valueOf(it.name)
            }
            itemView.day_picker.clearSelection()
            itemView.day_picker.setSelectedDays(pickDays)

            when {
                today in data.pickedDays -> {
                    itemView.textView_isToday.text =
                        context?.resources?.getString(R.string.today) ?: "Today"
                }
                tomorrow in data.pickedDays -> {
                    itemView.textView_isToday.text =
                        context?.resources?.getString(R.string.tomorrow) ?: "Tomorrow"
                    itemView.imageView_dot.visibility = View.INVISIBLE
                }
                else -> {
                    itemView.textView_isToday.visibility = View.INVISIBLE
                    itemView.imageView_dot.visibility = View.INVISIBLE
                }
            }

            itemView.day_picker.setDayPressedListener { day, isSelected ->
                if (!pickDays.contains(day))
                    itemView.day_picker.deselectDay(day)
                else if (!isSelected)
                    itemView.day_picker.selectDay(day)
            }

            itemView.button_habit.setOnClickListener {
                detailsClickListener(data, position)
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitListAdapter.HabitListViewHolder {
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

/*    fun addList(list: List<Habit>) {
        habits.beginBatchedUpdates()
        list.forEach { habits.add(it) }
        habits.endBatchedUpdates()
    }

    operator fun get(position: Int): Habit {
        return habits.get(position)
    }

    fun clearList() {
        habits.beginBatchedUpdates()
        habits.clear()
        habits.endBatchedUpdates()
    }*/
}