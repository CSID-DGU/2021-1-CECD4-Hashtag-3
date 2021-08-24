package com.bintang.apiuploadimage.upload

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.bintang.apiuploadimage.R
import com.bintang.apiuploadimage.upload.model.ResponseUpload

class CartActivity : AppCompatActivity() {
    private lateinit var textView1: TextView
    var dataList1 = ArrayList<ResponseUpload>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        textView1 = findViewById(R.id.tv_2) as TextView

        val pathData = intent.getSerializableExtra("key") as? ArrayList<ResponseUpload>

        if(pathData!=null) {

            textView1.setText(pathData.toString())
        }

        val intent_addBtn = findViewById(R.id.addBtn) as Button
        intent_addBtn.setOnClickListener {
            val intents = Intent(this@CartActivity, UploadActivity::class.java)
            startActivity(intents)
        }
    }
}