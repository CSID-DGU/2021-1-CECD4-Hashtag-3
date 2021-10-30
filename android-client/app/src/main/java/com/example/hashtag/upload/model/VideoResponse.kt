package com.example.hashtag.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VideoResponse(
    @SerializedName("count")
    var count:Int,

    @SerializedName("name")
    val name:String,

    @SerializedName("price")
    val price:Int,

    @SerializedName("success")
    val success:String,

    @SerializedName("result")
    var result: String
): Serializable
