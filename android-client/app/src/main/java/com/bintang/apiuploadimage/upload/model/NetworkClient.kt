package com.bintang.apiuploadimage.upload.model

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class NetworkClient() {
    companion object{
        fun getClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            return client
        }

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("http://192.168.0.4:5000")
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
//        @Part("kode_barang") kode_barang: RequestBody,
//        @Part("nama_barang") nama_barang: RequestBody,
//        @Part("stock") stock: RequestBody,
//        @Part("deskripsi") deskripsi: RequestBody,
        @Part image:MultipartBody.Part
        ): Call<List<ResponseUpload>>
}