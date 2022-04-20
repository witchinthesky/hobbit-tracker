package com.example.hobbittracker.presentation.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_time_picker.*

class TimePickerBottomSheet(
    supportFragmentManager: FragmentManager
) : BaseBottomSheetDialogFragment(supportFragmentManager) {

    private var onSaveClickListener: ((
        hours: Int,
        minute: Int,
        isAmSelected: Boolean
    ) -> Unit)? = null

    private var onCancelClickListener: (() -> Unit)? = null

    override fun getTheme(): Int {
        return R.style.bottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_save.setOnClickListener {
            savePickedTime()
            dismiss()
        }

        btn_am.setOnClickListener {
            switchTimeMidday(isAmSelected = true)
        }

        btn_pm.setOnClickListener {
            switchTimeMidday(isAmSelected = false)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun switchTimeMidday(isAmSelected: Boolean) {
        if (isAmSelected) {
            btn_am.setTextColor(R.color.purple_700)
            btn_pm.setTextColor(R.color.orange_700)
            btn_am.backgroundTintList = ColorStateList.valueOf(R.color.orange_700)
            btn_pm.backgroundTintList = ColorStateList.valueOf(R.color.orange_100)
        } else {
            btn_am.setTextColor(R.color.orange_700)
            btn_pm.setTextColor(R.color.purple_700)
            btn_am.backgroundTintList = ColorStateList.valueOf(R.color.orange_100)
            btn_pm.backgroundTintList = ColorStateList.valueOf(R.color.orange_700)
        }
    }

    private fun savePickedTime() {
        val hours = time_picker.getCurrentlySelectedTime("HH").toInt()
        val minute = time_picker.getCurrentlySelectedTime("MM").toInt()
        val isAmSelected = time_picker.getCurrentlySelectedTime("FORMAT") == "AM"

        onSaveClickListener?.let { it(hours, minute, isAmSelected) }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancelClickListener?.let { it() }
    }

    class Builder(
        private val fragmentManager: FragmentManager
    ) : BaseBottomSheetDialogFragment.Builder<TimePickerBottomSheet>() {

        private var onSaveListener: ((
            hours: Int,
            minute: Int,
            isAmSelected: Boolean
        ) -> Unit)? = null

        private var onCancelListener: (() -> Unit)? = null

        override fun build(): TimePickerBottomSheet =
            TimePickerBottomSheet(fragmentManager)
                .apply {
                    arguments = argumentsBundle.apply {
                        onSaveClickListener = onSaveListener
                        onCancelClickListener = onCancelListener
                    }
                }


        fun setOnSaveListener(callback: (hours: Int, minute: Int, isAmSelected: Boolean) -> Unit): Builder {
            onSaveListener = callback
            return this
        }

        fun setOnCancelListener(callback: () -> Unit): Builder {
            onCancelListener = callback
            return this
        }
    }

    companion object {
        private val TAG = TimePickerBottomSheet::class.java.simpleName
    }
}
