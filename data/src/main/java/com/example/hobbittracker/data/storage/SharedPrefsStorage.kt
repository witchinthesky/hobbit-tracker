package com.example.hobbittracker.data.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.hobbittracker.domain.utils.Result

class SharedPrefsStorage<Data>(
    context: Context,
    private val defaultValue: Data
) : Storage<String, Data> {

    companion object {
        private const val SHARED_PREFS_STORAGE_NAME = "settings"
    }

    private val storage = context.getSharedPreferences(
        SHARED_PREFS_STORAGE_NAME + "_preferences", MODE_PRIVATE
    )

    override suspend fun save(key: String, data: Data): Result<Void?> {
        val edit = storage.edit()
        when (data) {
            is String -> edit.putString(key, data)
            is Boolean -> edit.putBoolean(key, data)
            is Long -> edit.putLong(key, data)
            is Int -> edit.putInt(key, data)
            is Float -> edit.putFloat(key, data)
            else -> return Result.Error(IllegalArgumentException())
        }
        edit.apply()
        return Result.Success(null)
    }

    override suspend fun load(key: String): Result<Data> {
        val res = when (defaultValue) {
            is Boolean -> storage.getBoolean(key, defaultValue)
            is Int -> storage.getInt(key, defaultValue)
            is Long -> storage.getLong(key, defaultValue)
            is Float -> storage.getFloat(key, defaultValue)
            is String -> storage.getString(key, defaultValue)
            else -> return Result.Error(IllegalArgumentException())
        }
        return Result.Success(res as Data)
    }
}