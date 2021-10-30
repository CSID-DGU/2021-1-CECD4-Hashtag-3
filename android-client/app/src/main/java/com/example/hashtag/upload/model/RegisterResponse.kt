package com.example.hashtag.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class RegisterResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("success")
    val success: String
):Serializable