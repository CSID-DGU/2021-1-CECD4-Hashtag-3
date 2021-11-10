package com.example.hashtag

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.CartFeedResponse
import com.example.hashtag.upload.model.Feed
import com.example.hashtag.upload.model.NetworkClient
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_feed.CartListView
import kotlinx.android.synthetic.main.activity_feed.FeedListView
import kotlinx.android.synthetic.main.activity_feed.tv_total_sum_f
import kotlinx.android.synthetic.main.fragment_feed2.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Feed2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Feed2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var dataList1 = ArrayList<Cart>()
    var dataList2 = ArrayList<Feed>()
    val pathData = ArrayList<Cart>()
    val pathData2 = ArrayList<Feed>()
    var total:String? = null
    var naviActivity : NaviActivity? = null
    var login_id: String? = "예진"
    var login_email: String? = "yejinkwon0928@gmail.com"
    var tv: TextView?=null
    var feed: ListView?=null
    var cart: ListView?=null
    var feedAdapter:FeedListAdapter?=null
    var cartAdapter:CartListAdapter?=null
    var pay: Button?=null
    var add: Button?=null
    var toasts:Toast?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        naviActivity = context as NaviActivity

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        login_email = arguments?.getSerializable("current_user_email") as? String
        dataList2 = (arguments?.getSerializable("feed") as? ArrayList<Feed>)!!
        dataList1 = (arguments?.getSerializable("cart") as? ArrayList<Cart>)!!
    /* = java.util.ArrayList<com.example.hashtag.upload.model.Feed> */
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_feed2, container, false)

        // Inflate the layout for this fragment
        pay=view.findViewById(R.id.pay_more);
        add=view.findViewById(R.id.add_more);
        tv=view.findViewById(R.id.tv_total_sum_f);
        feed=view.findViewById(R.id.FeedListView);
        cart=view.findViewById(R.id.CartListView);
//        val feed: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), dataList2)
        val jsArray = JSONArray(dataList2)
        val jsArray2 = JSONArray(dataList1)
        val gson = Gson()
        val json = gson.toJson(dataList2)
        val json2 = gson.toJson(dataList1)
//        val jsonObject = JSONObject()
//        jsonObject.put("feeds",dataList2)
//        jsonObject.put("carts",dataList1)
//        Log.d("feed",dataList1.toString())
//        Log.d("cart",dataList2.toString())
        val feeds: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), json)
        val carts: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), json2)
//        val carts: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), dataList2.toString())



        NetworkClient.initService().updateFeed(feeds, carts).enqueue(object : retrofit2.Callback<CartFeedResponse> {

            override fun onResponse(call: Call<CartFeedResponse>?, response: Response<CartFeedResponse>?) {
                response!!.body()?.let {

                    pathData.addAll(it.carts)
                    pathData2.addAll(it.feeds)
//            tv_1.setText(List.toString())
                    toasts?.cancel()
                    val toast_s=Toast.makeText(
                        getActivity(),
                        "장바구니를 불러왔습니다.",
                        Toast.LENGTH_SHORT
                    )
                    toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
                    toast_s.show()
                    val handler = Handler()
                    handler.postDelayed(Runnable { toast_s.cancel() }, 500)

                    // Toast.makeText(getActivity(), "피드 들어옴", Toast.LENGTH_SHORT).show()
                    if(pathData!=null&&pathData2!=null) {
                        feedAdapter = getActivity()?.let { it1 -> login_email?.let { it2 ->
                            FeedListAdapter(it1, pathData2,pathData,
                                it2
                            )
                        } }
                        FeedListView.adapter = feedAdapter
                        cartAdapter = pathData?.let { getActivity()?.let { it1 -> login_email?.let { it2 ->
                            CartListAdapter(it1, pathData, pathData2,
                                it2
                            )
                        } } }
                        CartListView.adapter = cartAdapter
                        if (cartAdapter != null) {
                            tv_total_sum_f.setText(cartAdapter!!.getTotalPrice().toString().plus("원"))
                        }
                        if (cartAdapter != null) {
                            total = cartAdapter!!.getTotalPrice().toString().plus("원")
                        }


                    }
                }
            }
            override fun onFailure(call: Call<CartFeedResponse>?, t: Throwable) {

            }
        })
        view.pay_more.setOnClickListener {
//            val intentss = Intent(getActivity(), Pay2Activity::class.java)
//            intentss.putExtra("list",pathData)
//            intentss.putExtra("total", total)
//            intentss.putExtra("current_user_id",login_id)
//            intentss.putExtra("current_user_email",login_email)
//            Log.d("pass this", pathData.toString())
//            startActivity(intentss)
//            Toast.makeText(getActivity(), "서버에러", Toast.LENGTH_SHORT).show()

            toasts?.cancel()

//        val toast = Toast.makeText(getActivity(),  result_string, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//        toast.show()
            val toast_s=Toast.makeText(
                getActivity(),
                "결제를 시작합니다.",
                Toast.LENGTH_SHORT
            )
            toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
            toast_s.show()

            val handler = Handler()
            handler.postDelayed(Runnable { toast_s.cancel() }, 500)
//            Toast.makeText(getActivity(), "결제를 시작합니다.", Toast.LENGTH_SHORT).show()

            total = cartAdapter?.getTotalPrice().toString().plus("원")
            total?.let { it1 -> login_id?.let { it2 -> login_email?.let { it3 ->
                naviActivity?.callPay2(pathData, it1, it2,
                    it3
                )
            } } }
        }
        view.add_more.setOnClickListener {
            login_id?.let { it1 -> login_email?.let { it2 -> naviActivity?.callMenu(it1, it2) } }
        }
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Feed2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Feed2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}