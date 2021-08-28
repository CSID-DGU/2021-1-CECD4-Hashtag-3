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
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.model.EmailResponse
import com.example.hashtag.upload.model.NetworkClient
import com.example.hashtag.upload.model.ResponseUpload
import kotlinx.android.synthetic.main.activity_pay.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class PayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var context:Context=this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        var result_string:String = "\n"
        val list = intent.getSerializableExtra("list") as? ArrayList<ResponseUpload>
        val total = intent.getSerializableExtra("total") as? String
        if (list != null) {

            for (p in list){
                if(p.count!=0) {
                    result_string += p.name + "    \n" + p.count + "개" + "    " + p.price+"원\n"
                }
            }
            result_string +="총 결제금액:  "
            result_string += total
        }
        tv_pay.setText(result_string)

        emailBtn.setOnClickListener {
            if(edit_email.getText().toString().contains("@"))
            {
                val email: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), edit_email.getText().toString())
                val item: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), result_string)
                val asyncDialog : ProgressDialog = ProgressDialog(this@PayActivity)
                asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
                asyncDialog.setMessage("메일 전송중..!")
                asyncDialog.show()
                NetworkClient.initService().call_email(email, item).enqueue(object : retrofit2.Callback<EmailResponse> {
                    override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                        asyncDialog.dismiss()
                        Log.d("response email",response.body().toString())
                        val builder = AlertDialog.Builder(context)

                        builder.setTitle("결제 완료")
                            .setMessage("더 구매하시겠습니까?")
                            .setPositiveButton("아니오",
                                DialogInterface.OnClickListener { dialog, id ->
                                    val intentss = Intent(this@PayActivity, StartActivity::class.java)
                                    startActivity(intentss)

                                })
                            .setNegativeButton("네",
                                DialogInterface.OnClickListener { dialog, id ->
                                    val intents = Intent(this@PayActivity, CartActivity::class.java)
                                    startActivity(intents)
                                })

                        val alertDialog = builder.create()
                        alertDialog.show()
                    }

                    override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                        val builder = AlertDialog.Builder(context)

                        builder.setTitle("전송 실패")
                            .setMessage("상품 결제를 실패했습니다.")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { dialog, id ->
                                })
                    }
                })
            }else{
                Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
