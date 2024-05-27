package com.app.basestructuremvvmdsl.ui

import com.app.basestructuremvvmdsl.core.BaseRepository
import com.app.basestructuremvvmdsl.model.videodetail.CallBackVideo
import com.app.basestructuremvvmdsl.network.RetrofitService
import com.app.basestructuremvvmdsl.request.CallBackVideoRequest
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class MainRepository @Inject constructor(private val retrofitService: RetrofitService) :
    BaseRepository(retrofitService) {

    suspend fun getVideo(
        scope: CoroutineScope,
        onSuccess: ((CallBackVideo) -> Unit),
        onErrorAction: ((String?) -> Unit),
        callBackVideoRequest: CallBackVideoRequest
    ) =
        sendRequest(
            scope = scope,
            client = {
                retrofitService.callVideoPost(
                    callBackVideoRequest.page,
                    callBackVideoRequest.count
                )
            },
            onSuccess = {
                onSuccess(it)

            },
            onErrorAction = {
                onErrorAction(it)
            }
        )
}