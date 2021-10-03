package com.example.hashtag

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.CartFeedResponse
import com.example.hashtag.upload.model.Feed

class FeedListAdapter (val context: Context, val ItemList: ArrayList<Feed>,val ItemList2: ArrayList<Cart>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = LayoutInflater.from(context).inflate(R.layout.feed_lv_item, null)

        val view2: View = LayoutInflater.from(context).inflate(R.layout.cart_lv_item, null)
        val name = view.findViewById<TextView>(R.id.tv_item_name)
        val command = view.findViewById<TextView>(R.id.tv_item_command)
        val number = view.findViewById<TextView>(R.id.tv_item_count)
        val reBtn = view.findViewById<Button>(R.id.btn_rerun)

        val name1 = view2.findViewById<TextView>(R.id.tv_name)
        val count1 = view2.findViewById<TextView>(R.id.tv_count)
        val price1 = view2.findViewById<TextView>(R.id.tv_price)
        val plusBtn = view2.findViewById<Button>(R.id.plusBtn)
        val minusBtn = view2.findViewById<Button>(R.id.minusBtn)
        val feed = FeedActivity()
        val item = ItemList[position]
        val item2 = ItemList2[position]
        name.text = item.name
        number.text = item.count.toString().plus("개가")
        if (item.command.toString().equals("1"))
            command.text = "추가되었습니다."
        else
            command.text = "삭제되었습니다."
        reBtn.setTag(position)
//        plusBtn.setTag(position)
        reBtn.setOnClickListener {
            if(item.command==1)//더하기 실행취소
         {
                for (i in 0..ItemList2.size-1){
                    if (ItemList2[i].code==item.code)
                        ItemList2[i].count -= item.count
                    Toast.makeText(context, ItemList2[i].count.toString(), Toast.LENGTH_SHORT).show()
                    this.notifyDataSetChanged()
//                        count1.text = ItemList2[i].count.toString().plus("개")
//                    feed.cartAdapter.notifyDataSetChanged()
//                    (context as FeedActivity).ReviseTotal(getTotalPrice().toString().plus("원"))
                }
            }

//            var num = Integer.parseInt(item.count.toString())
//            if(num==0){
//                (context as CartActivity).toastError()
//            }
//            else {
//                num -= 1
//                item.count = num
//                Log.d("clicked minus count", count.toString())
//                count.text = item.count.toString().plus("개")
//
//                (context as CartActivity).ReviseTotal(getTotalPrice().toString().plus("원"))
//            }

        }
//        minusBtn.setOnClickListener {
//            var num = Integer.parseInt(item.count.toString())
//            if(num==0){
//                (context as CartActivity).toastError()
//            }
//            else {
//                num -= 1
//                item.count = num
//                Log.d("clicked minus count", count.toString())
//                count.text = item.count.toString().plus("개")
//
//                (context as CartActivity).ReviseTotal(getTotalPrice().toString().plus("원"))
//            }
//
//        }
//        plusBtn.setOnClickListener {
//            var num = Integer.parseInt(item.count.toString())+1
//            item.count = num
//            Log.d("clicked minus count", count.toString())
//            count.text = item.count.toString().plus("개")
//            (context as CartActivity).ReviseTotal(getTotalPrice().toString().plus("원"))
//
//        }
        return view
    }

    override fun getItem(position: Int): Any {
        return ItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return ItemList.size
    }
    fun getTotalCount():Int{
        var sum = 0
        for (p in ItemList) {
            sum +=p.count
        }
        return  sum
    }
    fun getTotalPrice(): Int {
        var sum = 0
        for (p in ItemList) {
            sum +=p.count*p.price
        }
        return  sum
    }

}
