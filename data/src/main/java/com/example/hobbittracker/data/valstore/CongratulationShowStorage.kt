package com.example.hobbittracker.data.valstore

import android.content.Context
import com.example.hobbittracker.data.storage.SharedPrefsStorage
import com.example.hobbittracker.data.storage.Storage

class CongratulationShowStorage(
    context: Context,
    private val habitId: String
) : ValueStorage<String, Boolean>() {

    override val storage: Storage<String, Boolean> =
        SharedPrefsStorage<Boolean>(context, false)

    override val key: String = "CONGRATULATIONS_SHOWED_$habitId"
}
