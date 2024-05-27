package com.app.basestructuremvvmdsl.model.videodetail

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class VideoPosts(
    @SerializedName("cat_id")
    val catId: Int?=null,
    @SerializedName("category_name")
    val categoryName: String?=null,
    @SerializedName("comments_count")
    val commentsCount: Int?=null,
    @SerializedName("content_type")
    val contentType: String?=null,
    @SerializedName("news_date")
    val newsDate: String?=null,
    @SerializedName("news_description")
    val newsDescription: String?=null,
    @SerializedName("news_image")
    val newsImage: String?=null,
    @SerializedName("news_title")
    val newsTitle: String?=null,
    @SerializedName("nid")
    val nid: Int?=null,
    @SerializedName("video_id")
    val videoId: String?=null,
    @SerializedName("video_url")
    val videoUrl: String?=null
)