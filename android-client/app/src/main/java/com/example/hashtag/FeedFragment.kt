package com.example.hashtag

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.CartFeedResponse
import com.example.hashtag.upload.model.Feed
import com.example.hashtag.upload.model.NetworkClient
import kotlinx.android.synthetic.main.activity_cart.tv_total_sum_f
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_feed.view.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import retrofit2.Call
import retrofit2.Response
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class FeedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var dataList1 = ArrayList<Cart>()
    var dataList2 = ArrayList<Feed>()
    val pathData = ArrayList<Cart>()
    val pathData2 = ArrayList<Feed>()
    var total:String? = null
    var naviActivity : NaviActivity? = null
    var login_id: String? = null
    var login_email: String? = null
    var tv: TextView?=null
    var feed: ListView?=null
    var cart:ListView?=null
    var feedAdapter:FeedListAdapter?=null
    var cartAdapter:CartListAdapter?=null
    var toastBefore:Toast?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        naviActivity = context as NaviActivity

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        login_id = arguments?.getSerializable("current_user_id") as? String
        login_email = arguments?.getSerializable("current_user_email") as? String


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_feed, container, false)
        tv=view.findViewById(R.id.tv_total_sum_f);
        feed=view.findViewById(R.id.FeedListView);
        cart=view.findViewById(R.id.CartListView);
        NetworkClient.initService().get_cartfeed().enqueue(object : retrofit2.Callback<CartFeedResponse> {

            override fun onResponse(call: Call<CartFeedResponse>?, response: Response<CartFeedResponse>?) {
                response!!.body()?.let {
                    pathData.addAll(it.carts)
                    pathData2.addAll(it.feeds)
//            tv_1.setText(List.toString())
                    toastBefore?.cancel()
                    val toast = Toast.makeText(getActivity(), "리스트를 받아왔습니다.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
                    toast.show()
                    val handler = Handler()
                    handler.postDelayed(Runnable { toast.cancel() }, 500)

//                    Toast.makeText(getActivity(), "리스트를 받아왔습니다.", Toast.LENGTH_SHORT).show()
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

        view.payBtn5.setOnClickListener {
//            val intentss = Intent(getActivity(), Pay2Activity::class.java)
//            intentss.putExtra("list",pathData)
//            intentss.putExtra("total", total)
//            intentss.putExtra("current_user_id",login_id)
//            intentss.putExtra("current_user_email",login_email)
//            Log.d("pass this", pathData.toString())
//            startActivity(intentss)
//            Toast.makeText(getActivity(), "서버에러", Toast.LENGTH_SHORT).show()
//            Toast.makeText(getActivity(), pathData.toString()+login_id+login_email, Toast.LENGTH_SHORT).show()

            total = cartAdapter?.getTotalPrice().toString().plus("원")
            total?.let { it1 -> login_id?.let { it2 -> login_email?.let { it3 ->
                naviActivity?.callPay2(pathData, it1, it2,
                    it3
                )
            } } }
        }
        view.addBtn5.setOnClickListener {
            login_id?.let { it1 -> login_email?.let { it2 -> naviActivity?.callMenu(it1, it2) } }
        }

        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }
    fun ReviseTotal(setString: String) {
//        Toast.makeText(getActivity(), "총계변경", Toast.LENGTH_SHORT).show();

        getFragmentManager()?.let { refreshFragment(this, it) }
        tv?.setText(setString)
        total = setString
    }
    fun Refresh(revise_feed: ArrayList<Feed>, revise_cart: ArrayList<Cart>) {
//        Toast.makeText(getActivity(), "갱신", Toast.LENGTH_SHORT).show();
        getFragmentManager()?.let { refreshFragment(this, it) }
    }
    fun toastError() {
//        Toast.makeText(getActivity(), "0개 이하입니다.", Toast.LENGTH_SHORT).show();
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FeedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}