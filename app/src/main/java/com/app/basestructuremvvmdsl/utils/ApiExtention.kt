package com.app.basestructuremvvmdsl.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import com.google.gson.Gson

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * Api extension functions
 */


fun Any.toFieldStringMap(): HashMap<String, String> {
    /*val classAny = this::class
    val paramMap = HashMap<String, Any>()
    for (field in classAny.members) {
        paramMap[field.name] = field.
    }
    return paramMap*/
    val gson = Gson()
    val json = gson.toJson(this)
    val jsonObject = JSONObject(json)
    val param = HashMap<String, String>()
    try {
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = jsonObject.get(key)

            if (value != null && value is String) {
                param[key] = value
            }
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return param
}

fun Any.toFieldRequestBodyMap(): HashMap<String, RequestBody> {
    /*val classAny = this::class
    val paramMap = HashMap<String, Any>()
    for (field in classAny.members) {
        paramMap[field.name] = field.
    }
    return paramMap*/
    val gson = Gson()
    val json = gson.toJson(this)
    val jsonObject = JSONObject(json)
    val param = HashMap<String, RequestBody>()
    try {
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            var value = jsonObject.get(key) // {"path":"/emulated/0/"}
            if (value is JSONObject && value.has("path")) {
                val path = value.getString("path")
                value = File(path)
            }

            val body = getRequestBody(value)
            val reqKey = getRequestKey(key, value)
            if (body != null) {
                param[reqKey] = body
            }
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return param
}

fun <T> getRequestBody(t: T): RequestBody? {
    return when (t) {
        is String -> getStringRequestBody(t as String)
        is File -> getFileRequestBody(t as File)
        else -> null
    }
}

fun <T> getRequestKey(key: String, value: T): String {
    return when (value) {
        is File -> getFileUploadKey(key, value)
        else -> key
    }
}

fun getFileRequestBody(file: File): RequestBody? {
    try {
        val mimeType = getMimeType(file.absolutePath)
        if (mimeType != null) {
            val MEDIA_TYPE = mimeType.toMediaTypeOrNull()
            return RequestBody.create(MEDIA_TYPE, file)
        }
    } catch (e: Exception) {
    }
    return null
}

fun getFileRequestBody(path: String): RequestBody? {
    val file = File(path)
    return getFileRequestBody(file)
}

fun getStringRequestBody(value: String?): RequestBody? {
    try {
        val MEDIA_TYPE_TEXT = "text/plain".toMediaTypeOrNull()
        return RequestBody.create(MEDIA_TYPE_TEXT, value ?: "")
    } catch (e: Exception) {

    }
    return null
}

/* public ProgressRequestBody getFileProgressRequestBody(File file, ProgressRequestBody.UploadCallbacks uploadCallbacks) {
ProgressRequestBody requestBody = new ProgressRequestBody(file, uploadCallbacks);
return requestBody;
}*/

fun getFileUploadKey(key: String, file: File): String {
    return "" + key + "\"; filename=\"" + file.name
}

private fun getMimeType(url: String): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

/**
 * close opened soft keyboard.
 *
 * @param mActivity context
 */
fun hideSoftKeyboard(mActivity: Activity) {
    try {
        val view = mActivity.currentFocus
        if (view != null) {
            val inputManager =
                mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

/**
 * generate setting object with success=0 and current string as message
 */
/*fun String.generateSetting(): Settings {
    return generateSetting("0", this)
}

fun Throwable.getErrorSettings(): Settings {
    val e = this
    val settings = Settings()
    if (e is HttpException && e.code() == 401) {
        settings.success = STATUS_CODE_UNAUTHORIZED

    } else if (e is HttpException && e.code() == 500) {
        settings.message = "Oops.. Please try again later."
        settings.success = STATUS_CODE_EXCEPTION
    } else if (e is SocketTimeoutException || e is ConnectException || e is UnknownHostException || e is IOException *//*|| e is APIConnectionException*//*) {
        settings.message = "No internet connection,Try again once you have an internet connection."
        settings.success = STATUS_CODE_EXCEPTION
    } else {
        settings.success = STATUS_CODE_EXCEPTION
        settings.message = e.toString()
    }
    return settings
}*/


