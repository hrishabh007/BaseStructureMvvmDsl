package  com.app.basestructuremvvmdsl.core

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.*
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp

import org.json.JSONObject
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


/**
 * Base application class to manage basic things like current language, app background / foreground etc
 */
@HiltAndroidApp
class BaseApplication : Application(), DefaultLifecycleObserver {


    var weakActivity: WeakReference<BaseActivity<*, *>>? = null
    var branchJson: String? = null

    init {
        appContext = WeakReference(this)
    }

    companion object {

        var IS_APP_IN_FOREGROUND = false
        var appContext: WeakReference<BaseApplication>? = null
        fun applicationContext(): Context? {
            return appContext!!.get()
        }


    }

    lateinit var handler: Handler
    override fun onCreate() {
        super<Application>.onCreate()
        // Branch logging for debugging
        handler = Handler(Looper.myLooper()!!)
        appUpdateDialogShown = false
        // Branch object initialization
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -2)
        // TODO: 11/10/21 change
        /*Logger.deleteDataBeforeTime(calendar.timeInMillis)*/
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }


    fun setCurrentActivity(activity: BaseActivity<*, *>) {
        weakActivity = WeakReference(activity)
    }

    fun getCurrentActivity(): BaseActivity<*, *>? {
        return weakActivity?.get()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onCreate(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        appInForeground()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        appInBackground()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }


    fun appInForeground() {
        IS_APP_IN_FOREGROUND = true
//        Log.e("appInForeground", "appInForeground")
        // callCheckAppVersionStatus()
    }

    /*fun callCheckAppVersionStatus() {
        val map = HashMap<String, String>()
        val otherInfoJson = JSONObject(ApiClient.getDeviceParams() as Map<String, String>)
        map["other_info_json"] = otherInfoJson.toString()
        map["device_type"] = "Android"
        if (AppPreference(this).isContainsAndValue("deviceToken")) {
            map["device_token"] = AppPreference(this).getDeviceToken()
        }
        if (AppPreference(this).isContainsAndValue("data")) {
            val userData = AppPreference(this).getLoginResponse()
            map["user_id"] = userData.user_id ?: ""
        }
        map["version_number"] = getAppVersionName()

        val call = ApiClient.apiService.appVersionStatus(map)
        ApiClient.call(call, object : RetrofitCallback<WSListResponse<AppVersionStatus>>() {
            override fun onSuccess(response: Response<WSListResponse<AppVersionStatus>>?) {
                if (response?.body()?.settings?.isSuccess == true) {
                    val list = response.body()?.data
                    if (list?.isNotEmpty() == true) {
                        val appVersionStatus = list[0]
                        if (appVersionStatus.forceLogout == "Yes") {
                            val sessionExpireEvent = SessionExpireEvent()
                            sessionExpireEvent.unAuthorisedAccess = true
                            EventBus.getDefault().post(sessionExpireEvent)
                        }
                    }
                }
            }

            override fun onError(response: Response<*>?) {

            }

            override fun onException(e: Throwable) {

            }
        })
    }*/


    fun appInBackground() {
        IS_APP_IN_FOREGROUND = false
    }

    var appUpdateDialogShown = false

    /* fun isAppUpdateDialogLaterDaysExpire(): Boolean {

         val currentDateTime = Calendar.getInstance()
         val time = AppPreference(this).getAppUpdateDialogLaterPressDate()
         if (time <= 0) {
             return true
         } else {
             val updateDialogShownTime = Calendar.getInstance()
             updateDialogShownTime.timeInMillis = time

             val msDiff: Long = currentDateTime.timeInMillis - updateDialogShownTime.timeInMillis
             val daysDiff: Long = TimeUnit.MILLISECONDS.toDays(msDiff)

             return daysDiff >= 5
         }
     }*/

    /*fun showUpdateDialog(forceUpdate: Boolean): Dialog? {
        val bindingX = LayoutUpdateDialogBinding.inflate(LayoutInflater.from(getCurrentActivity()!!))
        val builder = Dialog(getCurrentActivity()!!)
        builder.setContentView(bindingX.root)

        bindingX.tvLater.setOnClickListener {
            builder.dismiss()
            AppPreference(this).setAppUpdateDialogLaterPressDate()
        }

        bindingX.tvUpgradeNow.setOnClickListener {
            getCurrentActivity()?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            if (!forceUpdate) {
                builder.dismiss()
            }
        }
        if (forceUpdate) {
            bindingX.tvLater.visibility = View.GONE
        }

        builder.setCancelable(!forceUpdate)
        builder.show()
        return builder
    }*/

    /*  override fun attachBaseContext(newBase: Context?) {
          super.attachBaseContext(updateBaseLocale(newBase!!))
      }

      fun updateBaseLocale(context: Context) : Context {

          val locale = Locale("en_US")
          if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
              return updateResources(context, locale);
          }

          return updateResourcesLocaleLegacy(context, locale);
      }

      fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
          val resources: Resources = context.resources
          val configuration: Configuration = resources.getConfiguration()
          configuration.locale = locale
          resources.updateConfiguration(configuration, resources.getDisplayMetrics())
          return context
      }

      private fun updateResources(context: Context, locale: Locale): Context{
          val configuration = Configuration(context.resources.configuration)
          configuration.setLocale(locale);
          return context.createConfigurationContext(configuration);
      }*/

    fun <T : ViewModel> T.createFactory(): ViewModelProvider.Factory {
        val viewModel = this
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModel as T
        }
    }
}
