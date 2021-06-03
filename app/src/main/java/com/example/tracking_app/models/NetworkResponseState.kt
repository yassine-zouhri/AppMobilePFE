package com.example.tracking_app.models

sealed class NetworkResponseState<out T : Any> {

    data class Loading<out T : Any>(val data: T?) : NetworkResponseState<T>()

    data class Success<out T : Any>(val data: T) : NetworkResponseState<T>()

    data class Error<out T : Any>(val exception: Exception) : NetworkResponseState<T>()
}