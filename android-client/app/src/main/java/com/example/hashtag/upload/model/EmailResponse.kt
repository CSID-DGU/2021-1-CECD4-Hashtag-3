package com.example.hashtag.upload.model
import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EmailResponse(

    @SerializedName("image")
    var image:Bitmap

): Serializable
