package com.example.hashtag.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class Feed(
    @SerializedName("code")
    val code: String,
    @SerializedName("command")
    val command: Int,
    @SerializedName("count")
    val count: Int,
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int
):Serializable