package com.example.hashtag.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class Cart(
    @SerializedName("code")
    var code: String,
    @SerializedName("command")
    var command: Int,
    @SerializedName("count")
    var count: Int,
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("price")
    var price: Int

):Serializable