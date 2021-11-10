package com.example.hashtag

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hashtag.upload.CartActivity
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_mypage.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MypageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MypageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var naviActivity : NaviActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        naviActivity = context as NaviActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                user_nickname.text="로그인이 필요합니다."
                logout_btn.text="로그인"
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
                logout_btn.text="로그아웃"
                UserApiClient.instance.me { user, error ->
                    user_nickname.text = "${user?.kakaoAccount?.profile?.nickname}님 \n안녕하세요:)"
//            user_image.set
                }
            }
        }





        logout_btn.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    //        val toast = Toast.makeText(getActivity(),  result_string, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//        toast.show()
                    val toast_s=Toast.makeText(
                        getActivity(),
                        "로그아웃 실패",
                        Toast.LENGTH_SHORT
                    )
                    toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
                    toast_s.show()

                    val handler = Handler()
                    handler.postDelayed(Runnable { toast_s.cancel() }, 500)

                    //  Toast.makeText(getActivity(), "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    //        val toast = Toast.makeText(getActivity(),  result_string, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//        toast.show()
                    val toast_s=Toast.makeText(
                        getActivity(),
                        "로그아웃 성공",
                        Toast.LENGTH_SHORT
                    )
                    toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
                    toast_s.show()

                    val handler = Handler()
                    handler.postDelayed(Runnable { toast_s.cancel() }, 500)
                   // Toast.makeText(getActivity(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(getActivity(), NaviActivity::class.java)
                startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
//                finish()
            }
        }

//        val kakao_unlink_button = findViewById<Button>(R.id.kakao_unlink_button) // 로그인 버튼
//
//        kakao_unlink_button.setOnClickListener {
//            UserApiClient.instance.unlink { error ->
//                if (error != null) {
//                    Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
//                }else {
//                    Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
//                    finish()
//                }
//            }
//        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MypageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                MypageFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}