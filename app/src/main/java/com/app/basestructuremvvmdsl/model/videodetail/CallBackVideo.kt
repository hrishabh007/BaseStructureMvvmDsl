package com.app.basestructuremvvmdsl.model.videodetail


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName



data class CallBackVideo(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("count_total")
    val countTotal: Int?,
    @SerializedName("pages")
    val pages: Int?,
    @SerializedName("posts")
    val videoPosts: List<VideoPosts>?,
    @SerializedName("status")
    val status: String?
)

