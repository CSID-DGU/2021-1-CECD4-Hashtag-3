package com.example.hashtag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.model.NetworkClient
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_go_login.setOnClickListener{
            val intentss = Intent(this@LoginActivity, Login2Activity::class.java)
            startActivity(intentss)
        }
        btn_go_register.setOnClickListener{
            val intentss = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intentss)
        }

    }
}