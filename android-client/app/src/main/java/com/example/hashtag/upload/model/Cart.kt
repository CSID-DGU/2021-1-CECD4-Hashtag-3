package com.example.hashtag.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class Cart(
    @SerializedName("code")
    var code: Int,
    @SerializedName("count")
    var count: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("price")
    var price: Int
):Serializable