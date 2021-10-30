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
        val login_id = intent.getSerializableExtra("current_user_id") as? String
        val login_email = intent.getSerializableExtra("current_user_email") as? String

        btn_photo.setOnClickListener {
            val intents = Intent(this@MenuActivity, UploadActivity::class.java)
            intents.putExtra("current_user_id",login_id)
            intents.putExtra("current_user_email",login_email)
            startActivity(intents)
        }

        btn_video.setOnClickListener {
            val intents = Intent(this@MenuActivity, VideoActivity::class.java)
            intents.putExtra("current_user_id",login_id)
            intents.putExtra("current_user_email",login_email)
            startActivity(intents)
        }
    }
}