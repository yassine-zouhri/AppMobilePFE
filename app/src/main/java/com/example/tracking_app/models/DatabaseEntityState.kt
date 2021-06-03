package com.example.tracking_app.models

sealed class DatabaseEntityState<out T : Any> {

    data class Success<out T : Any>(val data: T) : DatabaseEntityState<T>()

    data class Error<out T : Any>(val exception: Exception) : DatabaseEntityState<T>()
}