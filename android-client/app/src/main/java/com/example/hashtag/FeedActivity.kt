package com.example.hashtag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.CartFeedResponse
import com.example.hashtag.upload.model.Feed
import com.example.hashtag.upload.model.NetworkClient
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.addBtn
import kotlinx.android.synthetic.main.activity_cart.payyBtn
import kotlinx.android.synthetic.main.activity_cart.tv_total_sum
import kotlinx.android.synthetic.main.activity_feed.*
import retrofit2.Call
import retrofit2.Response

class FeedActivity : AppCompatActivity() {
    var dataList1 = ArrayList<Cart>()
    var dataList2 = ArrayList<Feed>()
    val pathData = ArrayList<Cart>()
    val pathData2 = ArrayList<Feed>()
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
        val login_id = intent.getSerializableExtra("current_user_id") as? String
        val login_email = intent.getSerializableExtra("current_user_email") as? String
        NetworkClient.initService().get_cartfeed().enqueue(object : retrofit2.Callback<CartFeedResponse> {

            override fun onResponse(call: Call<CartFeedResponse>?, response: Response<CartFeedResponse>?) {
                response!!.body()?.let {
                    pathData.addAll(it.carts)
                    pathData2.addAll(it.feeds)
//            tv_1.setText(List.toString())
                    Toast.makeText(baseContext, "피드 들어옴", Toast.LENGTH_SHORT).show()
                    if(pathData!=null&&pathData2!=null) {
                        val feedAdapter = FeedListAdapter(this@FeedActivity, pathData2,pathData)
                        FeedListView.adapter = feedAdapter
                        val cartAdapter = pathData?.let { CartListAdapter(this@FeedActivity, pathData) }
                        CartListView.adapter = cartAdapter
                        tv_total_sum.setText(cartAdapter.getTotalPrice().toString().plus("원"))
                        total = cartAdapter.getTotalPrice().toString().plus("원")


                    }
                }
            }
            override fun onFailure(call: Call<CartFeedResponse>?, t: Throwable) {

            }
        })
        payyBtn.setOnClickListener {
            val intentss = Intent(this@FeedActivity, Pay2Activity::class.java)
            intentss.putExtra("list",pathData)
            intentss.putExtra("total", total)
            intentss.putExtra("current_user_id",login_id)
            intentss.putExtra("current_user_email",login_email)
            Log.d("pass this", pathData.toString())
            startActivity(intentss)
            Toast.makeText(baseContext, "서버에러", Toast.LENGTH_SHORT).show()
        }
        addBtn.setOnClickListener {
            val intents = Intent(this@FeedActivity, MenuActivity::class.java)
            startActivity(intents)
        }

    }

}