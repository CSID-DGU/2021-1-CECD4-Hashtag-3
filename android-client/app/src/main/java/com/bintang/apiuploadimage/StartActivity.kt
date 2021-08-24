package com.bintang.apiuploadimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.bintang.apiuploadimage.upload.CartActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val intent_startBtn = findViewById(R.id.startBtn) as Button
        intent_startBtn.setOnClickListener {
            val intent = Intent(this@StartActivity, CartActivity::class.java)
            startActivity(intent)
        }
    }
}