package com.example.hashtag.upload

import android.text.TextUtils
import android.util.Log
import com.example.hashtag.upload.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File


class UploadPresenter(val view: UploadView) {

    fun upload(image: String) {
        if (TextUtils.isEmpty(image)) {
            Log.d("이미지 선택 미완료", image)
            view.isEmpty("사진 선택x")
        }
        else {
            val file = File(image)
            val image_path = image
            Log.d("전송할 이미지",image )
            Log.d("전송할 이미지 path",image_path )
            view.onLoading("ll")
            val image: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val requestFile = MultipartBody.Part.createFormData("requestFile", file.name, image)

            NetworkClient.initService().upload(requestFile).enqueue(object : retrofit2.Callback<List<ResponseUpload>> {

                override fun onResponse(call: Call<List<ResponseUpload>>?, response: Response<List<ResponseUpload>>?) {
                    view.onQuit("ll")
                    var dataList = ArrayList<ResponseUpload>()
                    dataList.addAll(response!!.body()!!)
                    Log.d("image is", image_path)
                    Log.d("datalist is....",dataList.toString())
                    response.body()?.let { view.onSuccessupload(dataList)
                    }
                }
                override fun onFailure(call: Call<List<ResponseUpload>>?, t: Throwable) {
                    view.onErrorServer(t.localizedMessage)
                }
            })
        }
    }
    fun upload_video(image: String) {
        if (TextUtils.isEmpty(image)) {
            Log.d("이미지 선택 미완료", image)
            view.isEmpty("사진 선택x")
        }
        else {
            val file = File(image)
            val image_path = image
            Log.d("전송할 이미지",image )
            Log.d("전송할 이미지 path",image_path )
            view.onLoad("ll")
            val image: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val requestFile = MultipartBody.Part.createFormData("requestFile", file.name, image)

            NetworkClient.initService().upload_video(requestFile).enqueue(object : retrofit2.Callback<List<ResponseUpload>> {

                override fun onResponse(call: Call<List<ResponseUpload>>?, response: Response<List<ResponseUpload>>?) {
                  view.onSuccess("분석 정상")
                    file.delete()

                }
                override fun onFailure(call: Call<List<ResponseUpload>>?, t: Throwable) {
                    view.onErrorServer("서버 오류")
                    file.delete()
                }
            })
        }
    }
    fun get_cartfeed() {

            NetworkClient.initService().get_cartfeed().enqueue(object : retrofit2.Callback<CartFeedResponse> {

                override fun onResponse(call: Call<CartFeedResponse>?, response: Response<CartFeedResponse>?) {

                    response!!.body()?.let {
                        var dataList = ArrayList<Cart>()
                        dataList.addAll(it.carts)
                        var dataList2 = ArrayList<Feed>()
                        dataList2.addAll(it.feeds)
                        view.onSuccessFeed(dataList, dataList2)
                    }
                }
                override fun onFailure(call: Call<CartFeedResponse>?, t: Throwable) {
                    view.onErrorServer(t.localizedMessage)
                }
            })
        }

}

