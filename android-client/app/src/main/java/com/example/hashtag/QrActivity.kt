package com.example.hashtag

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_qr.*


class QrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        val byteArray = intent.getByteArrayExtra("byteArray")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        val login_id = intent.getSerializableExtra("current_user_id") as? String
        val login_email = intent.getSerializableExtra("current_user_email") as? String
        qr_imgview.setImageBitmap(bmp)
    }

}