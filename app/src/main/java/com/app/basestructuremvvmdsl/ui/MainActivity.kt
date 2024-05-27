package com.app.basestructuremvvmdsl.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.basestructuremvvmdsl.R
import com.app.basestructuremvvmdsl.config.Constants.COUNT
import com.app.basestructuremvvmdsl.core.BaseActivity
import com.app.basestructuremvvmdsl.core.flow.ApiState
import com.app.basestructuremvvmdsl.databinding.ActivityMainBinding
import com.app.basestructuremvvmdsl.request.CallBackVideoRequest
import com.app.basestructuremvvmdsl.utils.InternetConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(MainViewModel::class.java) {
    private val pageStart: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadFirstPage()
    }

    private fun recentnewsRequest(page: Int, count: Int): CallBackVideoRequest {
        val recentnews = CallBackVideoRequest().apply {
            this.page = page
            this.count = count
        }
        return recentnews
    }

    private fun showErrorView(throwable: Throwable?) {
        if (binding.lyError.errorLayout.visibility == View.GONE) {
            binding.lyError.errorLayout.visibility = View.VISIBLE


            if (!InternetConnection.checkConnection(this)) {
                binding.lyError.errorTxtCause.setText(R.string.error_msg_no_internet)
            } else {
                if (throwable is TimeoutException) {
                    binding.lyError.errorTxtCause.setText(R.string.error_msg_timeout)
                } else {
                    binding.lyError.errorTxtCause.setText(R.string.error_msg_unknown)
                }
            }
            binding.lyError.errorBtnRetry.setOnClickListener {
                loadFirstPage()
            }
        }
    }

    private fun loadFirstPage() {

        if (InternetConnection.checkConnection(this)) {
            viewModel.getVideo(recentnewsRequest(pageStart, COUNT))
        } else {
            showErrorView(null)
        }
    }

    override fun addObserver() {
        lifecycleScope.launch {
            viewModel.onVideoList.collect {
                when (it) {
                    ApiState.Empty -> {
                        //   binding.lyNoitem.lyMainNoitem.visibility = View.VISIBLE
                    }

                    ApiState.Loading -> {
                        showProgress()
                        /*  viewModel.loadingDetection.postValue(true)*/
                    }

                    is ApiState.Failure -> {
                        hideProgress()
                        showErrorView(null)
                        // handleApiError(it)
                    }

                    is ApiState.Success -> {
                        hideProgress()
                        Log.e("Vishal",">>>>>>>> Success")
                        // carListAdapter.replaceData(it.data)
                    }

                    else -> {}
                }
            }
        }

    }

    override fun initListener() {

    }

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
}