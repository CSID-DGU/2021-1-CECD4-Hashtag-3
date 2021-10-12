package com.example.hashtag

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hashtag.upload.model.Cart
import kotlinx.android.synthetic.main.activity_qr.*

class QrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        val bitmap = intent.getSerializableExtra("bitmap") as? Bitmap
        QR_imgview.setImageBitmap(bitmap)
    }

}