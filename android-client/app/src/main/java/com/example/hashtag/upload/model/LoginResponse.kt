package com.example.hashtag.upload.model
import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    var email_address: String
//    @SerializedName("phone")
//    var phone: String,
): Serializable
