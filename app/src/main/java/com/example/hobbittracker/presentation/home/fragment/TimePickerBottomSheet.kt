package com.example.hobbittracker.presentation.home.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_time_picker.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class TimePickerBottomSheet(
    supportFragmentManager: FragmentManager
) : BaseBottomSheetDialogFragment(supportFragmentManager) {

    private var onSaveClickListener: ((
        time: LocalTime
    ) -> Unit)? = null

    private var onCancelClickListener: (() -> Unit)? = null

    private var isAmSelected: Boolean = true

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
        initTimePicker()
        setUpViews()
    }

    private fun setUpViews() {
        btn_cancel.setOnClickListener {
            onCancelClickListener?.let { it()}
            dismiss()
        }

        btn_save.setOnClickListener {
            savePickedTime()
            dismiss()
        }
    }

    private fun savePickedTime() {
        val time = LocalDateTime.ofInstant(
            time_picker.date.toInstant(),
            ZoneId.systemDefault()
        ).toLocalTime()

        onSaveClickListener?.let { it(time) }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancelClickListener?.let { it() }
    }

    private fun initTimePicker() {
//        time_picker.setIsAmPm(true)
    }

    class Builder(
        private val fragmentManager: FragmentManager
    ) : BaseBottomSheetDialogFragment.Builder<TimePickerBottomSheet>() {

        private var onSaveListener: ((
            time: LocalTime
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


        fun setOnSaveListener(callback: (time: LocalTime) -> Unit): Builder {
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
