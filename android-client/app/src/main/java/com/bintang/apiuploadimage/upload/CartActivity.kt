package com.bintang.apiuploadimage.upload

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bintang.apiuploadimage.MainListAdapter
import com.bintang.apiuploadimage.PayActivity
import com.bintang.apiuploadimage.R
import com.bintang.apiuploadimage.upload.model.ResponseUpload
import kotlinx.android.synthetic.main.activity_cart.*


class CartActivity : AppCompatActivity() {
    //private lateinit var textView1: TextView
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
       // textView1 = findViewById(R.id.tv_2) as TextView

        val pathData = intent.getSerializableExtra("key") as? ArrayList<ResponseUpload>

        if(pathData!=null) {

           // textView1.setText(pathData.toString())
            val itemAdapter = MainListAdapter(this, pathData)
            mainListView.adapter = itemAdapter


//            mainListView.setOnItemClickListener(OnItemClickListener { parent, view, position, id -> })
            //itemAdapter.getTotalPrice().toString()
//            var sum = 0
//            for (p in pathData) {
//                sum +=p.count*p.price
//            }

                tv_total_sum.setText(itemAdapter.getTotalPrice().toString().plus("원"))
                total = itemAdapter.getTotalPrice().toString().plus("원")
//            mainListView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
//
//                Log.d("---------","wow" )
//                Toast.makeText(this, "wowowow", Toast.LENGTH_SHORT).show();
//            }

//            plusBtn.setOnClickListener {
//                Log.d("---------","wow" )
//            }
//


            payyBtn.setOnClickListener {
                val intentss = Intent(this@CartActivity, PayActivity::class.java)
                intentss.putExtra("list",pathData)
                intentss.putExtra("total", total)
                Log.d("pass this", pathData.toString())
                startActivity(intentss)
            }
        }


        addBtn.setOnClickListener {
            val intents = Intent(this@CartActivity, UploadActivity::class.java)
            startActivity(intents)
        }

    }


}

