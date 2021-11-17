package com.example.hashtag

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.Feed
import com.example.hashtag.upload.model.ResponseUpload
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_List = "list"
private const val ARG_total = "total"
private const val ARG_userid = "userid"
private const val ARG_useremail = "useremail"
/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var naviActivity : NaviActivity? = null
    var dataList1 = ArrayList<ResponseUpload>()
    var total:String? = null
    var user_name:String?=null
    var user_email:String?=null
    var pathData:ArrayList<ResponseUpload>?=null
    var feedAdapter:FeedListAdapter?=null
    var cartAdapter:CartListAdapter?=null
    var mainListAdapter:MainListAdapter?=null
    fun ReviseTotal(setString: String) {
        tv_total_sum_f.setText(setString)
        total = setString
    }
    fun toastError() {
        Toast.makeText(getActivity(), "0개 이하입니다.", Toast.LENGTH_SHORT).show();
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        naviActivity = context as NaviActivity

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pathData = arguments?.getSerializable("list") as? ArrayList<ResponseUpload>
        user_name = arguments?.getString("current_user_id") as? String
        user_email = arguments?.getString("current_user_email") as? String
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//            user_image.set
        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {

                val builder = getActivity()?.let { AlertDialog.Builder(it) }

                if (builder != null) {
                    builder.setTitle("로그인이 필요합니다:)")
                        .setMessage("로그인하시겠습니까?")
                        .setNegativeButton("네",
                            DialogInterface.OnClickListener { dialog, id ->
                                naviActivity?.callLogin2()
                            })
                }

                val alertDialog = builder?.create()
                if (alertDialog != null) {
                    alertDialog.show()
                }
            }
            else if (tokenInfo != null) {
                UserApiClient.instance.me { user, error ->
                    user_name = "${user?.kakaoAccount?.profile?.nickname}"

                }
                UserApiClient.instance.me { user, error ->
                    user_email = "${user?.kakaoAccount?.email}"
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_cart, container, false)

//        var resultss = ArrayList<ResponseUpload>()
//        resultss.add(ResponseUpload(1,"하늘보리 500ml",1200,"1"))
//        resultss.add(ResponseUpload(1,"농심 김치사발면 86g",880,"1"))
        if(pathData!=null) {
            mainListAdapter = getActivity()?.let { MainListAdapter(it, pathData!!) }
            view.mainListView.adapter = mainListAdapter
            if (mainListAdapter != null) {
                view.tv_total_sum.setText(mainListAdapter!!.getTotalPrice().toString().plus("원"))
            }
            if (mainListAdapter != null) {
                total = mainListAdapter!!.getTotalPrice().toString().plus("원")
            }

            view.payBtn5.setOnClickListener {
//                val intentss = Intent(getActivity(), Pay2Activity::class.java)
//                intentss.putExtra("list",pathData)
//                intentss.putExtra("total", total)
//                Log.d("pass this", pathData.toString())
//                startActivity(intentss)
                user_name="예진"
                user_email="yejinkwon0928@gmail.com"
                if (user_name != null) {
                    if (user_email != null) {
                        total?.let { it1 -> naviActivity?.callPay(pathData, it1, user_name!!, user_email!!) }
                    }
                }

            }
        }else{
            val toast_s=Toast.makeText(
                getActivity(),
                "카트에 담긴 상품이 없어요.",
                Toast.LENGTH_SHORT
            )
            toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
            toast_s.show()

            val handler = Handler()
            handler.postDelayed(Runnable { toast_s.cancel() }, 500)
//            Toast.makeText(getActivity(), "카트에 담긴 상품이 없어요.", Toast.LENGTH_SHORT).show()

        }

        view.addBtn5.setOnClickListener {
//            val intents = Intent(getActivity(), MenuActivity::class.java)
//            intents.putExtra("current_user_id",login_id)
//            intents.putExtra("current_user_email",login_email)
//            startActivity(intents)
            user_name?.let { it1 -> user_email?.let { it2 -> naviActivity?.callMenu(it1, it2) } }
        }
        return view
    }

    companion object {
        fun newInstance(list:ArrayList<ResponseUpload>, total:String): CartFragment {
            val args = Bundle().apply {
                putSerializable(ARG_List, list)
                putSerializable(ARG_total, total)
            }
            return CartFragment().apply {
                arguments = args
            }
        }
        fun newInstance2(userid:String, useremail:String): CartFragment {
            val args = Bundle().apply {
                putSerializable(ARG_userid, userid)
                putSerializable(ARG_useremail, useremail)
            }
            return CartFragment().apply {
                arguments = args
            }
        }
    }
}