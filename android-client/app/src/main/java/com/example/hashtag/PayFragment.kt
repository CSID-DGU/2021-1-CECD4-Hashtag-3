package com.example.hashtag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.model.EmailResponse
import com.example.hashtag.upload.model.NetworkClient
import com.example.hashtag.upload.model.ResponseUpload
import kotlinx.android.synthetic.main.activity_pay.*
import kotlinx.android.synthetic.main.fragment_pay.view.*
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
 * Use the [PayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var result_string:String = "\n"
    var login_id: String? = null
    var login_email: String? = null
    var list: ArrayList<ResponseUpload>? = null
    var total: String? = null
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
        var view = inflater.inflate(R.layout.fragment_pay2, container, false)
        view.tv_pay.setMovementMethod(ScrollingMovementMethod())
        // Inflate the layout for this fragment
        list = arguments?.getString("list") as? ArrayList<ResponseUpload>
        total = arguments?.getString("total") as? String
        login_id = arguments?.getString("current_user_id") as? String
        login_email = arguments?.getString("current_user_email") as? String

        if (list != null) {

            for (p in list!!){
                if(p.count!=0) {
                    result_string += p.name + "    \n" + p.count + "개" + "    " + p.price+"원\n"
                }
            }
            result_string +="총 결제금액:  "
            result_string += total
        }
        tv_pay.setText(result_string)

        view.emailBtn.setOnClickListener {
            if(edit_email.getText().toString().contains("@"))
            {
                val email: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), edit_email.getText().toString())
                val item: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), result_string)
                val asyncDialog : ProgressDialog = ProgressDialog(getActivity())
                asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
                asyncDialog.setMessage("메일 전송중..!")
                asyncDialog.show()
                NetworkClient.initService().call_email(email, item).enqueue(object : retrofit2.Callback<EmailResponse> {
                    override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                        asyncDialog.dismiss()
                        Log.d("response email",response.body().toString())
                        val builder = getActivity()?.let { it1 -> AlertDialog.Builder(it1) }

                        if (builder != null) {
                            builder.setTitle("결제 완료")
                                .setMessage("더 구매하시겠습니까?")
                                .setPositiveButton("아니오",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        val intentss = Intent(getActivity(), StartActivity::class.java)
                                        startActivity(intentss)

                                    })
                                .setNegativeButton("네",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        val intents = Intent(getActivity(), CartActivity::class.java)
                                        startActivity(intents)
                                    })
                        }

                        val alertDialog = builder?.create()
                        if (alertDialog != null) {
                            alertDialog.show()
                        }
                    }

                    override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                        val builder = getActivity()?.let { it1 -> AlertDialog.Builder(it1) }

                        if (builder != null) {
                            builder.setTitle("전송 실패")
                                .setMessage("상품 결제를 실패했습니다.")
                                .setPositiveButton("확인",
                                    DialogInterface.OnClickListener { dialog, id ->
                                    })
                        }
                    }
                })
            }else{
                Toast.makeText(getActivity(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        }
return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}