package com.example.hashtag.upload

import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.CartFeedResponse
import com.example.hashtag.upload.model.Feed
import com.example.hashtag.upload.model.ResponseUpload

interface UploadView {
    fun isEmpty(msg: String)
    fun onSuccessupload(List:ArrayList<ResponseUpload>)
    fun onSuccessFeed(List:ArrayList<Cart>,List1:ArrayList<Feed>)
    fun onSuccess(message: String)
    fun onErrorServer(message: String)
    fun onLoad(message: String)
    fun onLoading(message: String)
    fun onQuit(message: String)
}
