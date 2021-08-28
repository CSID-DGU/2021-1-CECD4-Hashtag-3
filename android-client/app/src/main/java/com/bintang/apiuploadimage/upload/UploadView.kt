package com.bintang.apiuploadimage.upload

import android.os.Message
import com.bintang.apiuploadimage.upload.model.ResponseUpload

interface UploadView {
    fun isEmpty(msg: String)
    fun onSuccessupload(List:ArrayList<ResponseUpload>)
    fun onErrorServer(message: String)
    fun onLoading(message: String)
    fun onQuit(message: String)
}