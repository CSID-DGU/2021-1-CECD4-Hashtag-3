package com.example.hashtag.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EmailResponse(

    @SerializedName("success")
    var success:String

): Serializable
