package com.example.hobbittracker.data.storage

import com.example.hobbittracker.domain.utils.Result

interface Storage<Key, Data> {

    suspend fun save(key: Key, data: Data): Result<Void?>

    suspend fun load(key: Key): Result<Data>

}