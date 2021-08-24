package com.bintang.apiuploadimage

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bintang.apiuploadimage.upload.CartActivity
import com.bintang.apiuploadimage.upload.model.ResponseUpload
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.main_lv_item.*

class MainListAdapter (val context: Context, val ItemList: ArrayList<ResponseUpload>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.main_lv_item, null)
        val view2: View = LayoutInflater.from(context).inflate(R.layout.activity_cart, null)


        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */

        val name = view.findViewById<TextView>(R.id.tv_name)
        val count = view.findViewById<TextView>(R.id.tv_count)
        val price = view.findViewById<TextView>(R.id.tv_price)
        val plusBtn = view.findViewById<Button>(R.id.plusBtn)
        val minusBtn = view.findViewById<Button>(R.id.minusBtn)
        val cart = CartActivity()
        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val item = ItemList[position]
//        val resourceId = context.resources.getIdentifier(dog.photo, "drawable", context.packageName)
//        dogPhoto.setImageResource(resourceId)
        name.text = item.name
        count.text = item.count.toString().plus("개")
        price.text = item.price.toString().plus("원")
        minusBtn.setTag(position)
        plusBtn.setTag(position)
        minusBtn.setOnClickListener {
            var num = Integer.parseInt(item.count.toString())
            if(num==0){
                (context as CartActivity).toastError()
            }
            else {
                num -= 1
                item.count = num
                Log.d("clicked minus count", count.toString())
                count.text = item.count.toString().plus("개")

                (context as CartActivity).ReviseTotal(getTotalPrice().toString().plus("원"))
            }

        }
        plusBtn.setOnClickListener {
            var num = Integer.parseInt(item.count.toString())+1
            item.count = num
            Log.d("clicked minus count", count.toString())
            count.text = item.count.toString().plus("개")
            (context as CartActivity).ReviseTotal(getTotalPrice().toString().plus("원"))

        }
        return view
    }

    override fun getItem(position: Int): Any {
        return ItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
//    fun getAllItem(){
//        return ItemList
//    }
    fun minusFun(posit: Int){
        var num = Integer.parseInt(ItemList[posit].count.toString())-1

        var items = ItemList[posit]
        items.count = num
    }
    fun plusFun(posit: Int){
        var num = Integer.parseInt(ItemList[posit].count.toString())+1

        var items = ItemList[posit]
        items.count = num
    }
    override fun getCount(): Int {
        return ItemList.size
    }
    fun getTotalPrice(): Int {
        var sum = 0
        for (p in ItemList) {
           sum +=p.count*p.price
        }
        return  sum
    }

}