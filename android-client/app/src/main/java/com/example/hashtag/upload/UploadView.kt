package com.example.hashtag.upload

import com.example.hashtag.upload.model.ResponseUpload

interface UploadView {
    fun isEmpty(msg: String)
    fun onSuccessupload(List:ArrayList<ResponseUpload>)
    fun onErrorServer(message: String)
    fun onLoading(message: String)
    fun onQuit(message: String)
}
