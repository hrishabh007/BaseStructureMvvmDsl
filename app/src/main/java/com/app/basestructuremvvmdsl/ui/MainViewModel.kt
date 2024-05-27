package com.app.basestructuremvvmdsl.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.basestructuremvvmdsl.core.BaseRepository
import com.app.basestructuremvvmdsl.core.BaseViewModel
import com.app.basestructuremvvmdsl.core.Resource
import com.app.basestructuremvvmdsl.core.flow.ApiState
import com.app.basestructuremvvmdsl.model.videodetail.CallBackVideo
import com.app.basestructuremvvmdsl.network.RetrofitService
import com.app.basestructuremvvmdsl.request.CallBackVideoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val fragmentVideoRepository: MainRepository
) : BaseViewModel(app) {


    private val _onVideoList = MutableStateFlow<ApiState<CallBackVideo>?>(ApiState.Empty)
    val onVideoList: StateFlow<ApiState<CallBackVideo>?> = _onVideoList
    private val _onVideoListNext = MutableStateFlow<ApiState<List<CallBackVideo>?>>(ApiState.Empty)
    val onVideoListNext: StateFlow<ApiState<CallBackVideo>?> = _onVideoList

    fun getVideo(callBackVideoRequest: CallBackVideoRequest) = viewModelScope.launch {
        _onVideoList.value = ApiState.Loading
        fragmentVideoRepository.getVideo(
            scope = viewModelScope,
            onSuccess = {
                _onVideoList.value = ApiState.Success(it)
                if (it.videoPosts?.size!! <= 0) {
                    _onVideoList.value = ApiState.Empty
                }
            }, onErrorAction = {

                _onVideoList.value = ApiState.Failure(it)
            }, callBackVideoRequest
        )
    }

    fun getVideoNext(callBackVideoRequest: CallBackVideoRequest) = viewModelScope.launch {
        _onVideoListNext.value = ApiState.Loading
        fragmentVideoRepository.getVideo(
            scope = viewModelScope,
            onSuccess = {
                _onVideoList.value = ApiState.Success(it)

            }, onErrorAction = {

                _onVideoList.value = ApiState.Failure(it)
            }, callBackVideoRequest
        )
    }
}