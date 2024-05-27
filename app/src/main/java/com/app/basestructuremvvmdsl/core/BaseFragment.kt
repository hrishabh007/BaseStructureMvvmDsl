package  com.app.basestructuremvvmdsl.core


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.app.basestructuremvvmdsl.R


import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * This class is parent of all fragment class
 *
 * This class contains all common method of fragment(s).
 */

abstract class BaseFragment<VD : ViewBinding, VM : BaseViewModel>(private val viewModelClass: Class<VM>) :
    Fragment(),
    View.OnClickListener {


    companion object {
        var clickedTimeMillis = 0L
    }

    lateinit var binding: VD
    var mRootView: View? = null
    lateinit var viewModel: VM

    lateinit var baseActivity: BaseActivity<*, *>
//    lateinit var unregisterKeyBoardEvent: Unregistrar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("current fragment", this.toString())


        if (mRootView == null) {
            binding = getViewBinding()
            mRootView = binding.root
            if (!(::viewModel.isInitialized)) {
                setViewModel()
                setDefaultObserver()
                addObserver()
                iniViews()
                initListener()
            } else {
                // note: this three method are under if condition before. but it cause issue with tab change.
                // when we change tab and come again, livedata observer are not getting called. so added this else
                setViewModel()
                setDefaultObserver()
                addObserver()
            }
            initOnFragmentInitialize()
//            //to prevent focus highlight in api level 26 and higher
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mRootView is ViewGroup) {
//                baseActivity.disableHighlight(mRootView as ViewGroup)
//            }
        } else {
            container?.removeView(mRootView)
        }


        return mRootView
    }

    abstract fun getViewBinding(): VD
    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = activity as BaseActivity<*, *>
        EventBus.getDefault().register(this)
        //retainInstance = true

    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)

        super.onDetach()
    }
    /* fun View.snackbar(message: String, action: (() -> Unit)? = null) {
         val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
         action?.let {
             snackbar.setAction("Retry") {
                 it()
             }
         }
         snackbar.show()
     }*/
    /**
     * this function is added to capture event from eventbus when child doesn't have any subscriber. this function
     * prevent crash if child doesn't have subscriber
     * @param any Any
     */
    @Subscribe
    fun nothing(any: Any) {
//        To prevent no subscribe crash
    }


    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - clickedTimeMillis < 500) {
            // prevent click if user1 click with in 500 milli
            clickedTimeMillis = currentTime
            return@onClick
        }
        clickedTimeMillis = currentTime
    }

    /**
     * Setting up view model of fragment
     */
    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(viewModelClass)
        // viewModel = ViewModelProviders.of(this).get(viewModelClass)
    }

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * This abstract method is used to initialize view, change view setting etc. it will call every time if fragment is new or user1 coming from back stack
     *
     */
    abstract fun initOnFragmentInitialize()

    /**
     * This abstract method is used to initialize or create views object.
     */
    abstract fun iniViews()

    /**
     * This abstract method is used to initialize all listener
     */
    abstract fun initListener()

    /**
     * This abstract method is used to initialize all observer
     */
    abstract fun addObserver()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * DO NOT CHANGE ORDER OF FOLLOWING FUNTION CALL
         */
//        unregisterKeyBoardEvent = KeyboardVisibilityEvent.registerEventListener(baseActivity, this)


    }

    private fun setDefaultObserver() {

    }

    override fun onDestroyView() {
//        unregisterKeyBoardEvent.unregister()
        super.onDestroyView()
    }

    open fun showProgress(isCheckNetwork: Boolean = true) {

        baseActivity.showProgress(isCheckNetwork)
    }

    open fun hideProgress() {

        baseActivity.hideProgress()
    }

//    /**
//     * callback function for keyboard visibility change. if no fragment added callback this will used.
//     */
//    override fun onVisibilityChanged(isOpen: Boolean) {
////        ignore
//    }

    /**
     * Method to pop fragment
     */
    fun popBackStack() {
        activity?.onBackPressed()
    }


    /**
     * start activity with default animation
     */
    fun launchActivity(intent: Intent) {
        baseActivity.launchActivity(intent)
    }

    /**
     * start activity with default animation
     */
    fun launchActivity(intent: Intent, bundle: Bundle?) {
        baseActivity.launchActivity(intent, bundle)

    }

    /**
     * start activity with default animation
     */
    fun launchActivityForResult(intent: Intent, requestCode: Int) {
        baseActivity.launchActivityForResult(intent, requestCode)
    }

    /*fun pushFragment(fragment: Fragment, @IdRes containerId: Int, addToStack: Boolean) {
        baseActivity.pushFragment(fragment, containerId, addToStack, true)
    }*/

    fun popFragment() {
       /* baseActivity.supportFragmentManager.popBackStack(
            fragment.javaClass.name,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )*/
         baseActivity.supportFragmentManager.popBackStack()
    }

    fun View.snackbar(message: String, action: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        action?.let {
            snackbar.setAction("Retry") {
                it()
            }
        }
        snackbar.show()
    }

    fun Fragment.handleApiError(
        failure: Resource.Failure, retry: (() -> Unit)? = null
    ) {
        when {
            failure.isNetworkError -> requireView().snackbar(
                "Please check your internet connection",
                retry
            )
            failure.errorCode == 401 -> {
                requireView().snackbar(resources.getString(R.string.error_msg_unknown))
            }
            else -> {
                val error = failure.errorBody?.string().toString()
                requireView().snackbar(error)
            }
        }
    }



}