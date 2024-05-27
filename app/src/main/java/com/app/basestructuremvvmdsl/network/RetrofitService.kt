package com.app.basestructuremvvmdsl.network


import com.app.basestructuremvvmdsl.config.Constants
import com.app.basestructuremvvmdsl.model.videodetail.CallBackVideo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

import java.util.concurrent.TimeUnit


interface RetrofitService {

//    @Headers("Content-Type: application/json")
//    @POST(EndPoints.SOCIALLOGIN)
//    suspend fun callSocialAPi(@Body userData: UserInfo): SocialLogin
//
//    @Multipart
//    @POST(EndPoints.NORMALLOGIN)
//    suspend fun callLoginAPi(@Part("email") email: RequestBody, @Part("password") password: RequestBody): Login
//
//   @Streaming
//    @FormUrlEncoded
//    @POST(EndPoints.NORMALLOGIN)
//    suspend fun callNormalAPibody(@FieldMap fieldMap: HashMap<String, String>): Login
//
//
//    @GET(EndPoints.VIDEOLIST)
//    suspend fun callVideoListApi(): VideoList


    /* @Headers("Content-Type: application/json")
     @POST(EndPoints.SOCIALLOGIN)
     suspend fun callSocialAPi(
         @Part("social_id") SocialId: RequestBody,
         @Part("social_type") SocialType: RequestBody?
     ): SocialLogin*/

    @GET(EndPoints.GETCALLVIDEOPOST)
    suspend fun callVideoPost(
        @Query("page") page: Int?,
        @Query("count") count: Int?
    ): CallBackVideo

    companion object {
        // private val BASE_URL = "https://lgbtq.schedulesoftware.net/"
        //  private val BASE_URL = "http://192.168.1.242/"
        private val interceptor = run {

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }
        val okHttpClient =
            OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
              .addInterceptor(RequestInterceptor(object :
                     RequestInterceptor.OnRequestInterceptor {
                     override fun provideBodyMap(): HashMap<String, String> {
                         val hashMap = HashMap<String, String>()
                         hashMap["api_key"] = Constants.API_KEY
//                         hashMap["Ip-Address"] = "1214"
//                         hashMap["Device-Id"] = "12345"
//                         hashMap["Device-Type"] = "Android"
//                         hashMap["Device-Name"] = "Android"

                         return hashMap
                     }

                     override fun provideHeaderMap(): HashMap<String, String> {
                         val hashMap = HashMap<String, String>()
//                       if (MainApplication.sharedPreference.getPref(USER_ACCESS_TOKEN, "").isNotEmpty()) {
//                             hashMap["Authorization"] = "Bearer " + MainApplication.sharedPreference.getPref(USER_ACCESS_TOKEN, "")
//                         }

                        return hashMap
                    }

                    override fun removeFromBody(): ArrayList<String> {
                        return arrayListOf()
                    }

                }))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
        var retrofitService: RetrofitService? = null
        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(Constants.BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}
