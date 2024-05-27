package com.app.basestructuremvvmdsl.core

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

interface SafeApiCall {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Log.e("checkdataforthrow",">>>>>>>>>"+apiCall.invoke())
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {

                    is HttpException -> {
                        Log.e("checkdataforthrow",">>>>>>>>>"+throwable.response()?.errorBody())
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        Log.e("checkdataforthrow",">>>>>>>>>"+throwable)
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }
}
