package com.example.hobbittracker.presentation.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.utils.Validator
import com.example.hobbittracker.presentation.home.HomeService
import com.example.hobbittracker.presentation.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_change_categories.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeCategoriesFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields()

        btn_cancel.setOnClickListener {
            onEventFinish()
        }

        btn_save_changes.setOnClickListener {
            onEventDone()
        }
    }

    private fun onEventFinish() {
        vm.replaceFragment(
            requireActivity().supportFragmentManager,
            SettingsFragment()
        )
    }

    private fun initFields() {
        listOf(category1_text, category2_text, category3_text)
            .forEachIndexed { index, it ->
                it.setText(vm.categories[index+1].name)
            }
    }

    private fun validateFields(): Boolean {
        val validator = categoryValidator()

        listOf(category1_text, category2_text, category3_text).forEach {
            if (!HomeService.textViewValidateHandler(it, validator))
                return false
        }
        return true
    }

    private fun categoryValidator(): Validator<String> {
        val validator = HomeService.NameValidator()
        validator.maxNameLength = 6
        validator.minNameLength = 3

        return validator
    }

    private fun onEventDone() {
        if (!validateFields()) return

        val categories = listOf(
            category1_text.text.toString(),
            category2_text.text.toString(),
            category3_text.text.toString()
        ).mapIndexed { i, it ->
            CategoryHabits(i+1, it, -1)
        }

        vm.updateCategories(categories)

        onEventFinish()
    }
}


