package com.example.hobbittracker.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment(
    private val supportFragmentManager: FragmentManager
) : BottomSheetDialogFragment() {

    private val TAG = this.javaClass.simpleName

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            manager.beginTransaction()
                .remove(this)
                .commit()
            super.show(manager, tag)
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            Toast.makeText(this.context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun show() {
        show(supportFragmentManager, TAG)
    }

    abstract class Builder<T> {
        open val argumentsBundle = Bundle()
        abstract fun build(): T
    }
}