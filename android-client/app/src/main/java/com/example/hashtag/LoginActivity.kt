package com.example.hashtag

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.model.EmailResponse
import com.example.hashtag.upload.model.LoginResponse
import com.example.hashtag.upload.model.NetworkClient
import com.kakao.sdk.common.util.Utility
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_katok.setOnClickListener {
            NetworkClient.initService().get_login().enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                Log.d("응답 내용",response!!.body()!!.toString())
//                    Toast.makeText(baseContext, response!!.body()!!.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("server error","서버에러")
                    Toast.makeText(baseContext, "서버에러", Toast.LENGTH_SHORT).show()
                }
            })
//            val intent = Intent(this@LoginActivity, Login2Activity::class.java)
//            startActivity(intent)
        }
    }
}