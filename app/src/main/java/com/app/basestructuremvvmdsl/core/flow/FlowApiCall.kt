package com.app.basestructuremvvmdsl.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

interface FlowApiCall {
    suspend fun <T> makeAPIRequest(
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