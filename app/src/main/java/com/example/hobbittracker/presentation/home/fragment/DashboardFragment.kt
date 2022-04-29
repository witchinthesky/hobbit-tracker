package com.example.hobbittracker.presentation.home.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.entity.Habit
import com.example.hobbittracker.presentation.home.HomeViewModel
import com.example.hobbittracker.presentation.home.adapter.HabitListAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class DashboardFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var rvAdapter: HabitListAdapter

    private val detailsFragment: Fragment = DetailsHabitFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showNavigation()

        initRecyclerView()

        initAdapter()

        vm.habitsMLD.observe(viewLifecycleOwner) {
            updateAdapter(vm.habits)
        }

        initCategories()

        vm.categoriesMLD.observe(viewLifecycleOwner) {
            updateCategories(vm.categories)
        }
    }

    private fun showNavigation() {
        requireActivity().btn_add.visibility = View.VISIBLE
        requireActivity().buttomNavigation.visibility = View.VISIBLE
    }

    private fun initAdapter() {
        rvAdapter.setOnDetailsClick { _, index ->
            vm.currentHabitPositionMLD.value = index
            vm.replaceFragment(requireActivity().supportFragmentManager, detailsFragment)
        }
    }

    private fun initCategories() {
        categoryPicker.check(R.id.category_all)
        vm.pullHabitsAll()

        if (HomeViewModel.USER_VERIFIED) {
            categoryPicker.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.category_all -> vm.pullHabitsAll()
                        R.id.category_1 -> vm.pullHabitsByCategory(1)
                        R.id.category_2 -> vm.pullHabitsByCategory(2)
                        R.id.category_3 -> vm.pullHabitsByCategory(3)
                    }
                }
            }
        } else {
            listOf(category_1, category_2, category_3).forEach {
                it.isEnabled = false
            }
            btn_getPremium.visibility = View.VISIBLE
            btn_getPremium.setOnClickListener {
                vm.replaceFragment(
                    requireActivity().supportFragmentManager,
                    BillingFragment()
                )
            }
        }
    }

    private fun initRecyclerView() {
        rvAdapter = HabitListAdapter(context)
        rv_habitlist.adapter = rvAdapter
        rv_habitlist.layoutManager = LinearLayoutManager(context)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter(habits: List<Habit>) {
        rvAdapter.habits = habits
        rvAdapter.notifyDataSetChanged()
    }

    private fun updateCategories(categories: Array<CategoryHabits>) {
        // categories[0] has value: "None" and don`t changed
        if (categories.size >= 4) {
            category_1.text = categories[1].name
            category_2.text = categories[2].name
            category_3.text = categories[3].name
        }
    }
}