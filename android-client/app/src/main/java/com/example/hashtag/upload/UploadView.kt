package com.example.hashtag.upload

import com.example.hashtag.upload.model.*

interface UploadView {
    fun isEmpty(msg: String)
    fun onSuccessupload(List:ArrayList<ResponseUpload>)
    fun onSuccessEmpty(message: String)
    fun onSuccessFeed(List:ArrayList<Cart>,List1:ArrayList<Feed>)
    fun onSuccess(List:ArrayList<VideoResponse>)
    fun onErrorServer(message: String)
    fun onLoad(message: String)
    fun onLoading(message: String)
    fun onQuit(message: String)
}
