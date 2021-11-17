package com.example.hashtag.upload.model

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class NetworkClient() {
    companion object{
        fun getClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
//                .connectTimeout(1,TimeUnit.MINUTES)
                .readTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
            return client
        }

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
//                .baseUrl("https://hashtag-server-biqey.run.goorm.io")
                .baseUrl("http://3.36.131.36:5000")
//                .baseUrl("http://192.168.0.9:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build()
        }
        fun initService(): ApiService {
            return getRetrofit().create(ApiService::class.java)
        }
    }
}
interface ApiService {
    @Multipart
    @POST("/image")
    fun upload(
        @Part img:MultipartBody.Part
    ): Call<List<ResponseUpload>>
    @Multipart
    @POST("/tracking")
    fun upload_video(
        @Part img:MultipartBody.Part
    ): Call<List<VideoResponse>>
    @GET("/getCartFeed")
    fun get_cartfeed(): Call<CartFeedResponse>
    @Multipart
    @POST("/updateFeed")
    fun updateFeed(
        @Part("feeds") feeds: RequestBody,
        @Part("carts") carts: RequestBody
    ): Call<CartFeedResponse>
    @GET("/oauth/kakao")
    fun get_login(): Call<ResponseBody>
    @Multipart
    @POST("/mail")
    fun call_email(
        @Part("email") email: RequestBody,
        @Part("item") item: RequestBody
    ): Call<EmailResponse>
    @Multipart
    @POST("/mail")
    @Streaming
    fun call_email2(
        @Part("email") email: RequestBody,
        @Part("item") item: RequestBody
    ): Call<ResponseBody>
    @Multipart
    @POST("/oauth/register")
    fun register(
        @Part("id") id: RequestBody,
        @Part("password") pass: RequestBody,
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody
    ): Call<RegisterResponse>
    @Multipart
    @POST("/oauth/login")
    fun login(
        @Part("id") id: RequestBody,
        @Part("password") pass: RequestBody
    ): Call<LoginResponse>
}
