package com.example.hashtag.upload

import android.text.TextUtils
import android.util.Log
import com.example.hashtag.upload.model.NetworkClient
import com.example.hashtag.upload.model.ResponseUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
}

