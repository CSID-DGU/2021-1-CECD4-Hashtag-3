package com.example.hashtag

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hashtag.upload.model.LoginResponse
import com.example.hashtag.upload.model.NetworkClient
import com.example.hashtag.upload.model.ResponseUpload
import com.kakao.sdk.auth.LoginClient
import kotlinx.android.synthetic.main.fragment_login2.*
import kotlinx.android.synthetic.main.fragment_login2.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login2Fragment : Fragment() {
    var toast_before:Toast?=null
    val TAG: String = "Login"
    var blank = false
    var id: String = ""
    var pass: String = ""
    var user_name:String?=null
    var user_email:String?=null
    var naviActivity : NaviActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        naviActivity = context as NaviActivity
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login2, container, false)

        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {

                toast_before?.cancel()

//        val toast = Toast.makeText(getActivity(),  result_string, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//        toast.show()
                val toast_s=Toast.makeText(
                    getActivity(),
                    "로그인해주세요.",
                    Toast.LENGTH_SHORT
                )
                toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
                toast_s.show()

                val handler = Handler()
                handler.postDelayed(Runnable { toast_s.cancel() }, 500)
              //  Toast.makeText(getActivity(), "로그인해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if (tokenInfo != null) {
                toast_before?.cancel()

//        val toast = Toast.makeText(getActivity(),  result_string, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//        toast.show()
//                val toast_s=Toast.makeText(
//                    getActivity(),
//                    "자동 로그인되었습니다.",
//                    Toast.LENGTH_SHORT
//                )
//                toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//                toast_s.show()
//
//                val handler = Handler()
//                handler.postDelayed(Runnable { toast_s.cancel() }, 500)
               // Toast.makeText(getActivity(), "자동 로그인되었습니다.", Toast.LENGTH_SHORT).show()
//                val intent = Intent(getActivity(), CartActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))


                UserApiClient.instance.me { user, error ->
                    user_name = "${user?.kakaoAccount?.profile?.nickname}"

                }
                UserApiClient.instance.me { user, error ->
                    user_email = "${user?.kakaoAccount?.email}"
//                    user_email ="yejinkwon0928@gmail.com"
                }
//                                user_name="예진"
//                user_email="yejinkwon0928@gmail.com"

                var dataList = ArrayList<ResponseUpload>()
               // dataList.add(ResponseUpload(1,"상품이 없습니다",0,"1"))
                //dataList.add(ResponseUpload(1,"상품이 없습니다",0,"1"))

//                if (dataList != null) {
//                    Toast.makeText(getActivity(), user_name+user_email, Toast.LENGTH_SHORT).show()
                    user_name?.let { user_email?.let { it1 -> naviActivity?.callCart(dataList, it, it1) } }
                //}
//                finish()
            }
        }


//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash", keyHash)


        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(getActivity(), "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(getActivity(), "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(getActivity(), "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(getActivity(), "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(getActivity(), "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(getActivity(), "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(getActivity(), "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(getActivity(), "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(getActivity(), "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (token != null) {



                toast_before?.cancel()

//        val toast = Toast.makeText(getActivity(),  result_string, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//        toast.show()
                val toast_s=Toast.makeText(
                    getActivity(),
                    "로그인에 성공하였습니다.",
                    Toast.LENGTH_SHORT
                )
                toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
                toast_s.show()

                val handler = Handler()
                handler.postDelayed(Runnable { toast_s.cancel() }, 500)





               // Toast.makeText(getActivity(), "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                var dataList = ArrayList<ResponseUpload>()
                dataList.add(ResponseUpload(0,"상품이 없습니다",0,""))
                UserApiClient.instance.me { user, error ->
                    user_name = "${user?.kakaoAccount?.profile?.nickname}"

                }
                UserApiClient.instance.me { user, error ->
                    user_email = "${user?.kakaoAccount?.email}"
                }
                if (dataList != null) {
//                    Toast.makeText(getActivity(), user_email+user_name, Toast.LENGTH_SHORT).show()
                    user_name?.let { user_email?.let { it1 -> naviActivity?.callCart(dataList, it, it1) } }

                }
//                finish()
            }
        }

        view.login.setOnClickListener {
            id = tv_id2.text.toString()
            pass = tv_pass2.text.toString()
            // 유저가 항목을 다 채우지 않았을 경우
            if (id.isEmpty() || pass.isEmpty()) {
                blank = true
            }


            if (!blank) {

                val r_id: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), id)
                val r_pass: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), pass)
                val asyncDialog: ProgressDialog = ProgressDialog(getActivity())
                asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
                asyncDialog.setMessage("로그인 요청 중입니다..!")
                asyncDialog.show()
                NetworkClient.initService().login(r_id, r_pass)
                    .enqueue(object : retrofit2.Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            response.body()?.let{
                                val success = it.success
                                val id = it.name
                                val email = it.email
                                if(success.contains("false")){
                                    asyncDialog.dismiss()
                                    val builder = getActivity()?.let { it1 -> AlertDialog.Builder(it1) }

                                    if (builder != null) {
                                        builder.setTitle("로그인 실패")
                                            .setMessage("로그인에 실패했습니다.")
                                            .setPositiveButton("확인",
                                                DialogInterface.OnClickListener { dialog, id ->
                                                })
                                    }

                                    val alertDialog = builder?.create()
                                    if (alertDialog != null) {
                                        alertDialog.show()
                                    }
                                }else{
                                    asyncDialog.dismiss()
                                    val builder = getActivity()?.let { it1 -> AlertDialog.Builder(it1) }

                                    if (builder != null) {
                                        builder.setTitle("로그인 성공")
                                            .setMessage("로그인에 성공했습니다.")
                                            .setPositiveButton("확인",
                                                DialogInterface.OnClickListener { dialog, id ->
//                                                    val intentss = Intent(getActivity(), CartActivity::class.java)
//                                                    intentss.putExtra("current_user_id",id)
//                                                    intentss.putExtra("current_user_email",email)
//                                                    startActivity(intentss)

                                                    var dataList = ArrayList<ResponseUpload>()
                                                    dataList.add(ResponseUpload(0,"상품이 없습니다",0,""))


                                                    user_name?.let { user_email?.let { it1 ->
                                                        if (dataList != null) {
                                                            naviActivity?.callCart(dataList, it, it1)
                                                        }
                                                    } }


                                                })
                                    }


                                    val alertDialog = builder?.create()
                                    if (alertDialog != null) {
                                        alertDialog.show()
                                    }
                                }
                            }


                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            val builder = getActivity()?.let { it1 -> AlertDialog.Builder(it1) }

                            if (builder != null) {
                                builder.setTitle("로그인 요청 오류")
                                    .setMessage("로그인을 실패했습니다.")
                                    .setPositiveButton("확인",
                                        DialogInterface.OnClickListener { dialog, id ->
                                        })
                            }

                            val alertDialog = builder?.create()
                            if (alertDialog != null) {
                                alertDialog.show()
                            }
                        }
                    })
            } else {
                if (blank) {
                    Toast.makeText(getActivity(), "모든 항목을 작성해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        view.kaka.setOnClickListener {
            if(getActivity()?.let { it1 -> LoginClient.instance.isKakaoTalkLoginAvailable(it1) } == true){
                getActivity()?.let { it1 -> LoginClient.instance.loginWithKakaoTalk(it1, callback = callback) }


            }else{
                getActivity()?.let { it1 -> LoginClient.instance.loginWithKakaoAccount(it1, callback = callback) }
            }
        }
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Login2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}