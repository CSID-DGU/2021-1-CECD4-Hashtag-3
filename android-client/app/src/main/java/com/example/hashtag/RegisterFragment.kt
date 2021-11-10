package com.example.hashtag

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hashtag.upload.model.NetworkClient
import com.example.hashtag.upload.model.RegisterResponse
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_register.*

import retrofit2.Call
import retrofit2.Response
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val TAG: String = "Register"
    var blank = false
    var id: String = ""
    var pass: String = ""
    var name: String = ""
    var phone: String = ""
    var email: String = ""

    var naviActivity:NaviActivity?=null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_register, container, false)
        view.fbtn_register.setOnClickListener {
            Log.d(TAG, "회원가입 버튼 눌림")

            id = ftv_id.text.toString()
            pass = ftv_pass.text.toString()
            name = ftv_user_name.text.toString()
            email = ftv_email.text.toString()
            phone = ftv_phone.text.toString()

            // 유저가 항목을 다 채우지 않았을 경우
            if (id.isEmpty() || pass.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                blank = true
            }


            if (!blank) {

                val r_id: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), id)
                val r_pass: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), pass)
                val r_email: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), email)
                val r_name: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), name)
                val r_phone: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), phone)
                val asyncDialog: ProgressDialog = ProgressDialog(getActivity())
                asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
                asyncDialog.setMessage("회원가입 요청 중입니다..!")
                asyncDialog.show()
                NetworkClient.initService().register(r_id, r_pass, r_email, r_name, r_phone)
                    .enqueue(object : retrofit2.Callback<RegisterResponse> {
                        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                            response.body()?.let{
                                val success = it.success
                                val id = it.id
                                val password = it.password
                                val name = it.name
                                val email = it.email
                                val phone = it.phone
                                if(success.contains("false")){
                                    asyncDialog.dismiss()
                                    Log.d("response email", response.body().toString())
                                    val builder = getActivity()?.let { it1 -> AlertDialog.Builder(it1) }

                                    if (builder != null) {
                                        builder.setTitle("회원가입 실패")
                                            .setMessage("회원 가입에 실패했습니다.")
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
                                        builder.setTitle("회원가입 성공")
                                            .setMessage("회원가입에 성공했습니다.")
                                            .setPositiveButton("확인",
                                                DialogInterface.OnClickListener { dialog, id ->
                                                    val intentss = Intent(getActivity(), Login2Activity::class.java)
                                                    startActivity(intentss)

                                                })
                                    }


                                    val alertDialog = builder?.create()
                                    if (alertDialog != null) {
                                        alertDialog.show()
                                    }
                                }
                            }


                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            val builder = getActivity()?.let { it1 -> AlertDialog.Builder(it1) }

                            if (builder != null) {
                                builder.setTitle("회원가입 요청 오류")
                                    .setMessage("회원가입을 실패했습니다.")
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

        return view }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}