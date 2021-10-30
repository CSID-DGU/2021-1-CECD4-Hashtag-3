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
import com.example.hashtag.upload.model.NetworkClient
import com.example.hashtag.upload.model.RegisterResponse
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    val TAG: String = "Register"
    var blank = false
    var id: String = ""
    var pass: String = ""
    var name: String = ""
    var phone: String = ""
    var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var context: Context = this
        btn_katok.setOnClickListener {
            Log.d(TAG, "회원가입 버튼 눌림")

            id = tv_id.text.toString()
            pass = tv_pass.text.toString()
            name = tv_user_name.text.toString()
            email = tv_email.text.toString()
            phone = tv_phone.text.toString()

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
                val asyncDialog: ProgressDialog = ProgressDialog(this@RegisterActivity)
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
                                   val builder = AlertDialog.Builder(context)

                                   builder.setTitle("회원가입 실패")
                                       .setMessage("회원 가입에 실패했습니다.")
                                       .setPositiveButton("확인",
                                           DialogInterface.OnClickListener { dialog, id ->
                                           })

                                   val alertDialog = builder.create()
                                   alertDialog.show()

                               }else{
                                   asyncDialog.dismiss()
                                   val builder = AlertDialog.Builder(context)

                                   builder.setTitle("회원가입 성공")
                                       .setMessage("회원가입에 성공했습니다.")
                                       .setPositiveButton("확인",
                                           DialogInterface.OnClickListener { dialog, id ->
                                               val intentss = Intent(this@RegisterActivity, Login2Activity::class.java)
                                               startActivity(intentss)

                                           })


                                   val alertDialog = builder.create()
                                   alertDialog.show()
                               }
                           }


                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            val builder = AlertDialog.Builder(context)

                            builder.setTitle("회원가입 요청 오류")
                                .setMessage("회원가입을 실패했습니다.")
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


