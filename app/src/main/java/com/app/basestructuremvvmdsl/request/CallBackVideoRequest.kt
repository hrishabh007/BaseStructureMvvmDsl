package com.app.basestructuremvvmdsl.request

import com.google.gson.annotations.SerializedName

class CallBackVideoRequest {
    @SerializedName("page")
    var page: Int? = null

    @SerializedName("count")
    var count: Int? = null
}