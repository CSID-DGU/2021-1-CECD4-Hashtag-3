package com.example.hashtag.upload.model

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
            return client
        }

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://hashtag-server-biqey.run.goorm.io")
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
    @POST("/getItem")
    fun upload(
        @Part requestFile:MultipartBody.Part
    ): Call<List<ResponseUpload>>
    @Multipart
    @POST("/mail")
    fun call_email(

        @Part("email") email: RequestBody,
        @Part("item") item: RequestBody


    ): Call<EmailResponse>


}
