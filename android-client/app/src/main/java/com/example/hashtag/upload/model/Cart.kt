package com.example.hashtag.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class Cart(
    @SerializedName("code")
    val code: Int,
    @SerializedName("count")
    var count: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int
):Serializable