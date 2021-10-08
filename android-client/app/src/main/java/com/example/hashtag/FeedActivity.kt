package com.example.hashtag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.CartFeedResponse
import com.example.hashtag.upload.model.Feed
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.addBtn
import kotlinx.android.synthetic.main.activity_cart.payyBtn
import kotlinx.android.synthetic.main.activity_cart.tv_total_sum
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {
    var dataList1 = ArrayList<Cart>()
    var dataList2 = ArrayList<Feed>()

    var total:String? = null

    fun ReviseTotal(setString: String) {
        tv_total_sum.setText(setString)
        total = setString
    }
    fun Refresh(revise_feed: ArrayList<Feed>, revise_cart: ArrayList<Cart>) {
        val intent = Intent(this@FeedActivity, FeedActivity::class.java)
        intent.putExtra("cart",revise_cart)
        intent.putExtra("feed",revise_feed)
        startActivity(intent)
    }
    fun toastError() {
        Toast.makeText(this, "0개 이하입니다.", Toast.LENGTH_SHORT).show();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val pathData = intent.getSerializableExtra("cart") as? ArrayList<Cart>
        val pathData2 = intent.getSerializableExtra("feed") as? ArrayList<Feed>
        if(pathData!=null&&pathData2!=null) {
            val feedAdapter = FeedListAdapter(this, pathData2,pathData)
            FeedListView.adapter = feedAdapter
            val cartAdapter = pathData?.let { CartListAdapter(this, it) }
            CartListView.adapter = cartAdapter
            tv_total_sum.setText(cartAdapter.getTotalPrice().toString().plus("원"))
            total = cartAdapter.getTotalPrice().toString().plus("원")

            payyBtn.setOnClickListener {
                val intentss = Intent(this@FeedActivity, Pay2Activity::class.java)
                intentss.putExtra("list",pathData)
                intentss.putExtra("total", total)
                Log.d("pass this", pathData.toString())
                startActivity(intentss)
            }
        }


        addBtn.setOnClickListener {
            val intents = Intent(this@FeedActivity, MenuActivity::class.java)
            startActivity(intents)
        }

    }

}