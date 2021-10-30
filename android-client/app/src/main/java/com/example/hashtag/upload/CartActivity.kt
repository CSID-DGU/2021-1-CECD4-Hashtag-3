package com.example.hashtag.upload

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hashtag.*
import com.example.hashtag.upload.model.ResponseUpload
import kotlinx.android.synthetic.main.activity_cart.*


class CartActivity : AppCompatActivity() {
    var dataList1 = ArrayList<ResponseUpload>()
    var total:String? = null



    fun ReviseTotal(setString: String) {
        tv_total_sum.setText(setString)
        total = setString
    }
    fun toastError() {
        Toast.makeText(this, "0개 이하입니다.", Toast.LENGTH_SHORT).show();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val login_id = intent.getSerializableExtra("current_user_id") as? String
        val login_email = intent.getSerializableExtra("current_user_email") as? String
        val pathData = intent.getSerializableExtra("key") as? ArrayList<ResponseUpload>

        if(pathData!=null) {
            val itemAdapter = MainListAdapter(this, pathData)
            mainListView.adapter = itemAdapter
            tv_total_sum.setText(itemAdapter.getTotalPrice().toString().plus("원"))
            total = itemAdapter.getTotalPrice().toString().plus("원")

            payyBtn.setOnClickListener {
                val intentss = Intent(this@CartActivity, Pay2Activity::class.java)
                intentss.putExtra("list",pathData)
                intentss.putExtra("total", total)
                Log.d("pass this", pathData.toString())
                startActivity(intentss)
            }
        }


        addBtn.setOnClickListener {
            val intents = Intent(this@CartActivity, MenuActivity::class.java)
            intents.putExtra("current_user_id",login_id)
            intents.putExtra("current_user_email",login_email)
            startActivity(intents)
        }

    }


}


