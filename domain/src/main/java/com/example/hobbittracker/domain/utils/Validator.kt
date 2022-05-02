package com.example.hobbittracker.domain.utils

interface Validator<T> {

    fun validate(data: T) : Result<T>

//    operator fun invoke(data: T) : Result<T> = validate(data)
}