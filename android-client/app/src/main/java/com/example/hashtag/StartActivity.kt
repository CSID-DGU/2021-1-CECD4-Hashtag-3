package com.example.hashtag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.hashtag.upload.CartActivity
import com.kakao.sdk.common.util.Utility


class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val intent_startBtn = findViewById(R.id.btn_katok) as Button
        intent_startBtn.setOnClickListener {
            val keyHash = Utility.getKeyHash(this)
            Log.d("now is the ", keyHash)
            val intent = Intent(this@StartActivity, CartActivity::class.java)
            startActivity(intent)
        }
    }
}
