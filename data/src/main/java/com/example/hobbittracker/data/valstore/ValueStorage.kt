package com.example.hobbittracker.data.valstore

import com.example.hobbittracker.data.storage.Storage
import com.example.hobbittracker.domain.utils.Result
import java.util.concurrent.CancellationException

abstract class ValueStorage<Key, Data> {

    protected abstract val storage: Storage<Key, Data>
    protected abstract val key: Key

    open suspend fun save(value: Data) {
        storage.save(key, value)
    }

    open suspend fun load(): Data {
        return when (val res = storage.load(key)) {
            is Result.Success -> res.data
            is Result.Error -> throw res.exception
            is Result.Canceled ->
                if (res.exception != null)
                    throw res.exception!!
                else throw CancellationException()
        }
    }

    open suspend operator fun invoke(): Data = load()

    open suspend operator fun invoke(value: Data) = save(value)
}