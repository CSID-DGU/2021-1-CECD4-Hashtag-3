package com.example.hashtag

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hashtag.Constants.TAG
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.model.LoginResponse
import com.example.hashtag.upload.model.NetworkClient
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login2.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class Login2Activity : AppCompatActivity() {
    val TAG: String = "Login"
    var blank = false
    var id: String = ""
    var pass: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        var context: Context = this
        btn_katok.setOnClickListener {
            Log.d(TAG, "카톡 로그인 버튼 눌림")
            NetworkClient.initService().get_login().enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                Log.d("응답 내용",response!!.body()!!.toString())
//                    Toast.makeText(baseContext, response!!.body()!!.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("server error","서버에러")
                    Toast.makeText(baseContext, "서버에러", Toast.LENGTH_SHORT).show()
                }
            })
//            val intent = Intent(this@LoginActivity, Login2Activity::class.java)
//            startActivity(intent)
        }
        btn_login.setOnClickListener {
                id = tv_id.text.toString()
                pass = tv_pass.text.toString()
                // 유저가 항목을 다 채우지 않았을 경우
                if (id.isEmpty() || pass.isEmpty()) {
                    blank = true
                }


                if (!blank) {

                    val r_id: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), id)
                    val r_pass: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), pass)
                    val asyncDialog: ProgressDialog = ProgressDialog(this@Login2Activity)
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
                                        val builder = AlertDialog.Builder(context)

                                        builder.setTitle("로그인 실패")
                                            .setMessage("로그인에 실패했습니다.")
                                            .setPositiveButton("확인",
                                                DialogInterface.OnClickListener { dialog, id ->
                                                })

                                        val alertDialog = builder.create()
                                        alertDialog.show()
                                    }else{
                                        asyncDialog.dismiss()
                                        val builder = AlertDialog.Builder(context)

                                        builder.setTitle("로그인 성공")
                                            .setMessage("로그인에 성공했습니다.")
                                            .setPositiveButton("확인",
                                                DialogInterface.OnClickListener { dialog, id ->
                                                    val intentss = Intent(this@Login2Activity, CartActivity::class.java)
                                                    intentss.putExtra("current_user_id",id)
                                                    intentss.putExtra("current_user_email",email)
                                                    startActivity(intentss)

                                                })


                                        val alertDialog = builder.create()
                                        alertDialog.show()
                                    }
                                }


                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                val builder = AlertDialog.Builder(context)

                                builder.setTitle("로그인 요청 오류")
                                    .setMessage("로그인을 실패했습니다.")
                                    .setPositiveButton("확인",
                                        DialogInterface.OnClickListener { dialog, id ->
                                        })

                                val alertDialog = builder.create()
                                alertDialog.show()
                            }
                        })
                } else {
                    if (blank) {
                        Toast.makeText(this, "모든 항목을 작성해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}