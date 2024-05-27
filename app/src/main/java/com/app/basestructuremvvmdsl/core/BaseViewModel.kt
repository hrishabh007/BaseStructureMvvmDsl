package com.app.basestructuremvvmdsl.core

import android.app.Application
import androidx.lifecycle.*

import retrofit2.Response
import javax.inject.Inject


/**
 * View model base class with common functionality
 */

open class BaseViewModel (app: Application) : AndroidViewModel(app), DefaultLifecycleObserver  {


    private val TAG = "LifeCycleAwareModel"

    //    var settingObserver = MutableLiveData<Settings>()
    var focusOnErrorObserver = MutableLiveData<Int>()
    var exceptionObserver = MutableLiveData<Throwable>()
    var errorResponseObserver = MutableLiveData<Response<*>>()
    var defaultLoadingLiveData = MutableLiveData<EventLoading<Boolean>>()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // clear value on stop
        exceptionObserver.postValue(IllegalArgumentException(""))
        /*errorResponseObserver.postValue(-1)*/
//        settingObserver.postValue(null)
        /*focusOnErrorObserver.postValue(null)*/
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }


}
