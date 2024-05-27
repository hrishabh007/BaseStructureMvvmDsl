package com.app.basestructuremvvmdsl.core.flow

sealed class ApiState<out T> {
    object Loading : ApiState<Nothing>()

    object Empty : ApiState<Nothing>()

    data class Success<out T>(val data: T) : ApiState<T>()

    data class Failure(
        val errorMessage: String?
    ) : ApiState<Nothing>()
}
