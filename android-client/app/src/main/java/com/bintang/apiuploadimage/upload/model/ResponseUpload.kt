package com.bintang.apiuploadimage.upload.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseUpload(

	@SerializedName("count")
	var count:Int,


	@SerializedName("name")
	val name:String,


	@SerializedName("price")
	val price:Int
) : Serializable


