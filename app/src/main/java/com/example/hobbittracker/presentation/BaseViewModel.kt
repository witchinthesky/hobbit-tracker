package com.example.hobbittracker.presentation

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hobbittracker.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val tag = this.javaClass.simpleName

    protected val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner


    fun onToastShown() {
        _toast.value = null
    }

    protected fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                Log.e(tag, error.message.toString())
                _toast.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }

    fun startActivity(
        activity: Activity,
        cls: Class<*>,
        clearTasks: Boolean = false,
        finish: Boolean = false
    ) {
        val i = Intent(activity, cls)
        if (clearTasks) {
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                .or(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(i)
        if (finish) activity.finish()
    }

    fun replaceFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}