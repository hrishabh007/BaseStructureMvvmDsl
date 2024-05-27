package  com.app.basestructuremvvmdsl.core


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.app.basestructuremvvmdsl.R


import com.google.android.material.snackbar.Snackbar

import java.io.*
import java.util.*


/**
 * Activity class is parent of all other activities
 *
 * This class contains all the common functions of child activities like init data binding, create viewmodel,
 * ask runtime permissions etc
 */
abstract class BaseActivity<U : BaseViewModel, T : ViewBinding>(private val viewModelClass: Class<U>) :
    AppCompatActivity() {
    private var exitWithAnimation = true
    lateinit var binding: T
    lateinit var viewModel: U
    var context: Context? = null


    /**
     * Handling all type of permission
     */
    var deviceToken: String = ""
    var tokenFetchRetryCount = 0


    lateinit var baseApplication: BaseApplication

    companion object {
        // The indices for the projection array above.

    }

    private val EVENT_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Calendars._ID,                     // 0
        CalendarContract.Calendars.ACCOUNT_NAME,            // 1
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
        CalendarContract.Calendars.OWNER_ACCOUNT            // 3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseApplication = (application as BaseApplication)

        setContentViewBinding()
        setViewModel()
        setDefaultObserver()
        addObserver()

    }


    override fun onDestroy() {
        hideProgress() // to prevent leak

        super.onDestroy()
    }

    private fun setDefaultObserver() {

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

    fun AppCompatActivity.handleApiError(
        view: View, failure: Resource.Failure, retry: (() -> Unit)? = null
    ) {
        when {
            failure.isNetworkError -> view.snackbar(
                "Please check your internet connection",
                retry
            )

            failure.errorCode == 401 -> {
                view.snackbar("401 error")
            }

            else -> {
                val error = failure.errorBody?.string().toString()
                view.snackbar(error)
            }
        }
    }

    open fun dummy() {
        Log.e("", "")
    }

    /**
     * hide progress if showing and show alert from exception
     * @param it Throwable
     */
    /* fun showExceptionAsAlert(it: Throwable, listener: DialogUtil.IL? = null) {
         hideProgress()
         val errorMessage = it.getLocalizeErrMessage(this)
         if (errorMessage != null) {
 //                DialogUtil.alert(this, errorMessage, listener)
             DialogUtil.showAlertDialogAction(
                 this,
                 errorMessage,
                 listener,
                 getString(android.R.string.ok)
             )
         }
     }*/

    /**
     * hide progress if showing and show alert from response
     * @param it Response<*>
     */
    /* fun showAlertFromResponse(it: Response<*>, listener: DialogUtil.IL? = null) {
         hideProgress()
         val errorMessage = (it.getErrorMessage(this))
         if (errorMessage != null) {

 //                DialogUtil.alert(this, errorMessage, listener)
             DialogUtil.showAlertDialogAction(
                 this,
                 errorMessage,
                 listener,
                 getString(android.R.string.ok)
             )

         }
     }*/


    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * This abstract method is used to initialize all observer
     */
    abstract fun addObserver()


    /**
     * init view and other listeners if required
     */
    abstract fun initListener()

    /**
     * create viewmodel object and setup with activity
     */
    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(viewModelClass)
        //   viewModel = ViewModelProviders.of(this).get(viewModelClass)
    }

    override fun onStart() {
        super.onStart()

        (application as BaseApplication).setCurrentActivity(this)
    }

    override fun onStop() {

        super.onStop()
    }

    fun clearAllFragmentsFromBackStack() {
        val supportFragmentManager = supportFragmentManager
        for (i in 0..supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

    fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val backStackEntryAt = manager.getBackStackEntryAt(0)
            manager.popBackStack(backStackEntryAt.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun isNetworkAvailable(): Boolean {
        val conMgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        return netInfo != null
    }

    private var progressDialog: Dialog? = null
    open fun showProgress(isCheckNetwork: Boolean = true) {
        if (isCheckNetwork) {
            if (isNetworkAvailable()) {
                if (progressDialog == null) {
                    /* progressDialog = DialogUtil.createDialogWithoutBounds(
                         this,
                         LayoutInflater.from(this)
                             .inflate(R.layout.layout_loading, null, false)
                     )*/
                }
                progressDialog?.show()
            } else {
                showNoInternetConnection()
            }
        } else {
            if (progressDialog == null) {
                /* progressDialog = DialogUtil.createDialogWithoutBounds(
                     this,
                     LayoutInflater.from(this).inflate(R.layout.layout_loading, null, false)
                 )*/
            }
            progressDialog?.show()
        }
    }

    open fun isProgressDialogShowing(): Boolean {
        return progressDialog?.isShowing ?: false
    }

    fun showNoInternetConnection() {
//        getString(R.string.no_network_connection).showToast(this)
    }

    open fun hideProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }


    private fun setContentViewBinding(): T {

        binding = getViewBinding()
        setContentView(binding.root)
        // binding = DataBindingUtil.setContentView(this, layout)
        initListener()
        return binding
    }

    abstract fun getViewBinding(): T

    /**
     * start activity with default animation
     */
    fun launchActivity(intent: Intent) {
        startActivity(intent)
        setEnterExitAnimation()
    }


    fun launchActivity(intent: Intent, bundle: Bundle?) {
        startActivity(intent, bundle)
        setEnterExitAnimation()
    }

    /**
     * start activity with default animation
     */

    fun launchActivityForResult(intent: Intent, requestCode: Int) {
        // resultLauncher.launch(intent)
        startActivityForResult(intent, requestCode)
        setEnterExitAnimation()
    }

    //    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // There are no request codes
//            val data: Intent? = result.data
//           // doSomeOperations()
//        }
//    }
    fun openActivityForResult(intent: Intent) {
        startForResult.launch(intent)
    }


    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                //do stuff here
            }
        }

    /**
     * call this function after start activity to play default animation
     */
    fun setEnterExitAnimation() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    /**
     * @return layout resource id
     */
    /*  @LayoutRes
      abstract fun getLayoutId(): Int*/

    /**
     * replace fragment with animation
     */
    fun replaceFragment(
        containerId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true,
        animate: Boolean = true
    ) {
        val transaction = supportFragmentManager.beginTransaction()


        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        if (animate) {
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        transaction.replace(containerId, fragment, fragment.javaClass.simpleName)
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (exitWithAnimation) {
            setBackAnimation()
        } else {
            setNoAnimation()
        }
    }

    fun setNoAnimation() {
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
    }

    fun setBackAnimation() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun finish() {
        super.finish()
        if (exitWithAnimation) {
            setBackAnimation()
        } else {
            setNoAnimation()
        }
    }

    private var permissionGranted: (() -> Unit)? = null
    private var permissionDenied: (() -> Unit)? = null
    private lateinit var permissionRational: String


//  endregion ask permission


    /*    private fun logoutUser() {
            val preference = AppPreference(this)
            preference.clearPreference()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            launchActivity(intent)
        }*/

    //region image pick from camera or gallery

    /**
     * Opens the bottom sheet with Camera & Gallery option
     */


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ContextWrapper.wrap(newBase!!, Locale("en_US")))
    }

    public fun changeStatusBarColorWhite() {
        var flags: Int = window.decorView.getSystemUiVisibility()
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.setSystemUiVisibility(flags)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

    }

    public fun changeStatusBarColorBlack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.setSystemUiVisibility(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }


}



