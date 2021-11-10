package com.example.hashtag

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.NetworkClient
import kotlinx.android.synthetic.main.activity_pay.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream

class Pay2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        var context:Context=this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay2)
        var result_string:String = "\n"
        val list = intent.getSerializableExtra("list") as? ArrayList<Cart>
        val total = intent.getSerializableExtra("total") as? String
        val login_id = intent.getSerializableExtra("current_user_id") as? String
        val login_email = intent.getSerializableExtra("current_user_email") as? String
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
        edit_email.setText("kwon990928@naver.com")

        emailBtn.setOnClickListener {
            if(edit_email.getText().toString().contains("@"))
            {
                val email: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), edit_email.getText().toString())
                val item: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), result_string)
                val asyncDialog : ProgressDialog = ProgressDialog(this@Pay2Activity)
                asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
                asyncDialog.setMessage("메일 전송중..!")
                asyncDialog.show()
                NetworkClient.initService().call_email2(email, item).enqueue(object : retrofit2.Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        asyncDialog.dismiss()
                        val image_url =response.body()!!.byteStream()
                        var bitmap:Bitmap = BitmapFactory.decodeStream(image_url)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val byteArray: ByteArray = stream.toByteArray()
                        Log.d("response email",response.body().toString())
                        val builder = AlertDialog.Builder(context)

                        builder.setTitle("결제 완료")
                            .setMessage("더 구매하시겠습니까?")
                            .setPositiveButton("아니오",
                                DialogInterface.OnClickListener { dialog, id ->
                                    val intentss = Intent(this@Pay2Activity, QrActivity::class.java)
                                    intentss.putExtra("byteArray",byteArray)
                                    startActivity(intentss)
                                })
                            .setNegativeButton("네",
                                DialogInterface.OnClickListener { dialog, id ->
                                    val intents = Intent(this@Pay2Activity, CartActivity::class.java)
                                    intents.putExtra("current_user_id",login_id)
                                    intents.putExtra("current_user_email",login_email)
                                    startActivity(intents)
                                })

                        val alertDialog = builder.create()
                        alertDialog.show()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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
