package com.example.hashtag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hashtag.upload.UploadActivity
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        btn_photo.setOnClickListener {
            val intents = Intent(this@MenuActivity, UploadActivity::class.java)
            startActivity(intents)
        }

        btn_video.setOnClickListener {
            val intents = Intent(this@MenuActivity, VideoActivity::class.java)
            startActivity(intents)
        }
    }
}