package com.app.basestructuremvvmdsl.core

import androidx.lifecycle.MutableLiveData
import com.app.basestructuremvvmdsl.network.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

import retrofit2.Response
import javax.inject.Inject


/**
 * base repository for common task
 */
abstract class BaseRepository(val api: RetrofitService) : SafeApiCall {
    suspend fun logout() = safeApiCall {
        //    api.logout()
    }

    fun <T> sendRequest(
        scope: CoroutineScope,
        client: suspend () -> T,
        onErrorAction: ((String?) -> Unit)?,
        onSuccess: ((T) -> Unit),
    ) {
        makeAPIRequest(scope, client, onSuccess, onErrorAction)
    }

    private fun <T> makeAPIRequest(
        scope: CoroutineScope,
        client: suspend () -> T,
        onSuccess: ((T) -> Unit)? = null,
        onErrorAction: ((String?) -> Unit)? = null
    ) {
        scope.launch {
            try {
                val request = flow {
                    emit(client)
                }.flowOn(Dispatchers.IO)

                request.catch { e ->
                    onErrorAction?.invoke(e.message)
                }.collect {
                    onSuccess?.invoke(it.invoke())
                }

            } catch (e: Exception) {
                onErrorAction?.invoke(e.message)
            }
        }
    }
}
