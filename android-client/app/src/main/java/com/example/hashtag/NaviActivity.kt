package com.example.hashtag

import android.content.ClipData
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.hashtag.upload.UploadFragment
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.Feed
import com.example.hashtag.upload.model.ResponseUpload
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_login.*


class NaviActivity : AppCompatActivity() {
    var mBundle //main bundle
            : Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi)

        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var bnv_main = findViewById(R.id.bnv_main) as BottomNavigationView

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, homeFragment).commit()
                }
                R.id.cartFragment -> {
                    val boardFragment = CartFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, boardFragment).commit()
                }
                R.id.myPageFragment -> {
                    val settingFragment = MypageFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, settingFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.homeFragment
        }
    }
//    fun addLogin2(){
//        var login2Fragment: Login2Fragment= Login2Fragment()
//
//        var transaction = supportFragmentManager.beginTransaction()
//        transaction.add(R.id.fl_container, login2Fragment)
//        transaction.commit()
//    }
    fun callLogin2() {
        var login2Fragment: Login2Fragment= Login2Fragment()

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, login2Fragment)
        transaction.addToBackStack("Login2Fragment")
        transaction.commit()
    }
    fun callRegister() {
        var registerFragment: RegisterFragment= RegisterFragment()

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, registerFragment)
        transaction.addToBackStack("RegisterFragment")
        transaction.commit()
    }
    fun callCart(list: ArrayList<ResponseUpload>, current_user_id:String, current_user_email:String) {
        var cartFragment: CartFragment= CartFragment()
        var bundle = Bundle()
        bundle.putSerializable("list",list)
        bundle.putString("current_user_id",current_user_id)
        bundle.putString("current_user_email",current_user_email)
        cartFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, cartFragment)
        transaction.addToBackStack("CartFragment")
        transaction.commit()
    }
    fun callFeed(current_user_id:String,current_user_email:String) {
        var feedFragment: FeedFragment= FeedFragment()
        var bundle = Bundle()
        bundle.putString("current_user_id",current_user_id)
        bundle.putString("current_user_email",current_user_email)
        feedFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, feedFragment)
        transaction.addToBackStack("FeedFragment")
        transaction.commit()
    }
    fun refreshFeed(revise_feed: ArrayList<Feed>, revise_cart: ArrayList<Cart>) {
        var feedFragment: FeedFragment= FeedFragment()
        var bundle = Bundle()
        bundle.putSerializable("cart",revise_cart)
        bundle.putSerializable("feed",revise_feed)
        feedFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, feedFragment)
        transaction.addToBackStack("FeedFragment")
        transaction.commit()

    }
    fun callFrag_feed(ItemList: ArrayList<Feed>,
                 ItemList2: ArrayList<Cart>) {
        var feedFragment: FeedFragment= FeedFragment()
//        feedFragment.Refresh()
    }
    fun refresh_wow(ItemList: ArrayList<Feed>,
                    ItemList2: ArrayList<Cart>){
        var feedFragment: FeedFragment= FeedFragment()
        feedFragment.Refresh(ItemList,ItemList2)
    }
    fun refresh_w() {
        var feedFragment: FeedFragment= FeedFragment()
        var transaction = supportFragmentManager.beginTransaction()
        transaction.detach(feedFragment).attach(feedFragment)
        transaction.addToBackStack("FeedFragment")
        transaction.commit()
    }
    fun callFrag_revise(result:String) {
        var feedFragment: FeedFragment= FeedFragment()
        feedFragment.ReviseTotal(result)
//        feedFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, feedFragment)
        transaction.addToBackStack("FeedFragment")
        transaction.commit()
//        (context as FeedFragment).ReviseTotal(getTotalPrice().toString().plus("원"))

    }
//    (context as FeedFragment).ReviseTotal(getTotalPrice().toString().plus("원"))

    fun callUpload(current_user_id:String,current_user_email:String) {
        var uploadFragment: UploadFragment= UploadFragment()
        var bundle = Bundle()
        bundle.putString("current_user_id",current_user_id)
        bundle.putString("current_user_email",current_user_email)
        uploadFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, uploadFragment)
        transaction.addToBackStack("UploadFragment")
        transaction.commit()
    }

    fun callFeed2(email:String, ItemList: ArrayList<Feed>,
                  ItemList2: ArrayList<Cart>) {
        var feed2Fragment: Feed2Fragment= Feed2Fragment()
        var bundle = Bundle()
//        bundle.putString("current_user_id",current_user_id)
        bundle.putSerializable("feed",ItemList)
        bundle.putSerializable("cart",ItemList2)
        bundle.putString("current_user_email",email)
        feed2Fragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, feed2Fragment)
        transaction.addToBackStack("Feed2Fragment")
        transaction.commit()
    }
    fun callVideo(current_user_id:String,current_user_email:String) {
        var videoFragment: VideoFragment= VideoFragment()
        var bundle = Bundle()
        bundle.putString("current_user_id",current_user_id)
        bundle.putString("current_user_email",current_user_email)
        videoFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, videoFragment)
        transaction.addToBackStack("VideoFragment")
        transaction.commit()
    }
    fun callMenu(current_user_id:String,current_user_email:String) {
        var menuFragment: MenuFragment= MenuFragment()
        var bundle = Bundle()
        bundle.putString("current_user_id",current_user_id)
        bundle.putString("current_user_email",current_user_email)
        menuFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, menuFragment)
        transaction.addToBackStack("MenuFragment")
        transaction.commit()
    }

    fun callPay(list: ArrayList<ResponseUpload>?, total:String, current_user_id:String, current_user_email:String) {
        var payfragment = PayFragment()
        var bundle = Bundle()
        bundle.putSerializable("list",list)
        bundle.putString("total",total)
        bundle.putString("current_user_id",current_user_id)
        bundle.putString("current_user_email",current_user_email)
        payfragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌

        var transaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, payfragment)
        transaction.addToBackStack("PayFragment")
        transaction .commit()

    }
    fun callPay2(list: ArrayList<Cart>?, total:String, current_user_id:String, current_user_email:String) {
        var pay2fragment = Pay2Fragment()
        var bundle = Bundle()
        bundle.putSerializable("list",list)
        bundle.putString("total",total)
        bundle.putString("current_user_id",current_user_id)
        bundle.putString("current_user_email",current_user_email)
        pay2fragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌

        var transaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, pay2fragment)
        transaction.addToBackStack("Pay2Fragment")
        transaction .commit()

    }
    fun callQR(byteArray: ByteArray) {
        var QrFragment:QRFragment= QRFragment()
        var bundle = Bundle()
        bundle.putByteArray("byteArray",byteArray)
        QrFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, QrFragment)
        transaction.addToBackStack("QRFragment")
        transaction.commit()
    }

//    fun callCart() {
//        var cartFragment: Login2Fragment= Login2Fragment()
//
//        var transaction = supportFragmentManager.beginTransaction()
//        transaction.add(R.id.fl_container, login2Fragment)
//        transaction.addToBackStack("Login2Fragment")
//        transaction.commit()
//    }
//
fun goBack() {
    onBackPressed()
}
    fun fragBtnClick(bundle: Bundle) {
        this.mBundle = bundle
    } //fragBtnClcick()

}

