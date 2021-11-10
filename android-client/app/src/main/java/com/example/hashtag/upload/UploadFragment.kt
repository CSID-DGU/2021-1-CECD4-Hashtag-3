package com.example.hashtag.upload

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
//import com.example.hashtag.FeedActivity
import com.example.hashtag.NaviActivity
import com.example.hashtag.R
import com.example.hashtag.upload.model.*
import com.example.hashtag.upload.utils.FilePath
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.fragment_upload.view.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadFragment : Fragment(), UploadView, Serializable {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var REQUEST_IMAGE_GALLERY = 0
    private var REQUEST_IMAGE_CAMERA = 1
    private var REQUEST_PERMISSION = 2
    private var image_path : String? = null
    private var presenter: UploadPresenter? = null
    var naviActivity : NaviActivity? = null
    var login_id: String? = null
    var login_email: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        naviActivity = context as NaviActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        login_id = arguments?.getSerializable("current_user_id") as? String
        login_email = arguments?.getSerializable("current_user_email") as? String
        presenter = UploadPresenter(this)

        permissionLocation()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_upload, container, false)
        view.btnUpload2.setOnClickListener {
//            Toast.makeText(getActivity(), login_id.toString(), Toast.LENGTH_SHORT).show()

            if (login_id != null) {
                login_email?.let { actionPhoto(login_id!!, it) }
            }

        }

        view.btnTambah2.setOnClickListener {
            actionUpload()
        }
        return view    }
    fun permissionLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_PERMISSION
            )
        }
    }

    private fun actionPhoto(id:String, email:String) {
        image_path?.let { presenter?.upload(it,id, email) }
    }

    private fun actionUpload() {
        getActivity()?.let {
            AlertDialog.Builder(it)
                .setMessage("사진 선택")
                .setPositiveButton("갤러리", DialogInterface.OnClickListener { dialogInterface, i ->
                    Intent(Intent.ACTION_GET_CONTENT).also {
                        it.type = "image/*"
                        startActivityForResult(it, REQUEST_IMAGE_GALLERY)
                    }
                })
                .setNegativeButton("카메라", DialogInterface.OnClickListener { dialogInterface, i ->
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                        startActivityForResult(it, REQUEST_IMAGE_CAMERA)
                    }
                }).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_IMAGE_GALLERY) {
            resultGallery(data)
        }else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_IMAGE_CAMERA) {
            resultCamera(data)
        }

    }

    private fun resultCamera(data: Intent?) {

        val image = data?.extras?.get("data")
        val random = Random.nextInt(0, 999999)
        val camera = "Camera$random"

        image_path = persistImage(image as Bitmap, camera)
//        Toast.makeText(this, image_path.toString(), Toast.LENGTH_SHORT).show()

        action_image2.setImageBitmap(BitmapFactory.decodeFile(image_path))


    }

    private fun resultGallery(data: Intent?) {
        val image_bitmap = onSelectFromGalleryResult(data)
        action_image2.setImageBitmap(image_bitmap)
    }

    fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }
    fun rotate(bitmap: Bitmap?, degrees: Int): Bitmap? { // 이미지 회전 및 이미지 사이즈 압축
        var bitmap = bitmap
        if (degrees != 0 && bitmap != null) {
            val m = Matrix()
            m.setRotate(degrees.toFloat(), bitmap.width.toFloat() / 2,
                bitmap.height.toFloat() / 2)
            try {
                val converted = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.width, bitmap.height, m, true)
                if (bitmap != converted) {
                    bitmap.recycle()
                    bitmap = converted
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 4
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1280, 1280, true) // 이미지 사이즈 줄이기
                }
            } catch (ex: OutOfMemoryError) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap
    }

    private fun onSelectFromGalleryResult(data: Intent?): Bitmap {
        var bm: Bitmap? = null
        if (data !=null) {
            try {

                // changeProfilePath = absolutelyPath(data!!.data)
                image_path = data.data?.let { getActivity()?.let { it1 -> FilePath.getPath(it1, it) } }.toString()
                val contentResolver = requireActivity().contentResolver
                bm = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                val exif = image_path?.let { ExifInterface(it) }
                val exifOrientation: Int = exif!!.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val exifDegree = exifOrientationToDegrees(exifOrientation)
                bm = rotate(bm, exifDegree)
//                Toast.makeText(this, image_path.toString(), Toast.LENGTH_SHORT).show()
//               bm = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
        return bm!!
    }
    private fun persistImage(bitmap: Bitmap, date: String): String {
        val dirFile = getActivity()?.filesDir
        val imageFile = File(dirFile, date+ ".jpeg")
        val image_path = imageFile.path

        val os: OutputStream?
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            if (os != null) {
                os.flush()
            }
            if (os != null) {
                os.close()
            }
        }catch (e: Exception) {
            Log.e(javaClass.simpleName, getString(R.string.error_writing_bitmap), e)
        }
        return image_path
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION ) {

            Toast.makeText(getActivity(), "권한 허용", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(getActivity(), "권한 불허용", Toast.LENGTH_SHORT).show()
        }
    }

    override fun isEmpty(msg: String) {
        getActivity()?.let {
            AlertDialog.Builder(it)
                .setTitle("사진 선택 미완료")
                .setMessage("사진을 업로드해주세요.")
                .setNegativeButton("확인", DialogInterface.OnClickListener{ dialogInterface, i ->

                }).show()
        }
    }

    override fun onSuccessupload(List:ArrayList<ResponseUpload>, id: String, email: String) {
        Log.d("success",List.toString())
//            tv_1.setText(List.toString())
//        var intent = Intent(getActivity(), CartActivity::class.java)
//        intent.putExtra("list",List)
//        intent.putExtra("current_user_id",id)
//        intent.putExtra("current_user_email",email)
//        startActivity(intent)
//
        naviActivity?.callCart(List,id,email)
    }
    override fun onSuccessFeed(List:ArrayList<Cart>, List1:ArrayList<Feed>) {
        Log.d("success",List.toString())
//            tv_1.setText(List.toString())
//        var intent = Intent(getActivity(), FeedActivity::class.java)
//        intent.putExtra("key",List)
//        startActivity(intent)
    }
    override fun onSuccess(List:ArrayList<VideoResponse>) {
        getActivity()?.let {
            AlertDialog.Builder(it)
                .setTitle("분석 중")
                .setMessage("이미지를 분석 중입니다.")
                .setNegativeButton("OK", DialogInterface.OnClickListener{ dialogINterface, i ->

                }).show()
        }
    }
    override fun onErrorServer(message: String) {
        getActivity()?.let {
            AlertDialog.Builder(it)
                .setTitle("서버 연결에러")
                .setMessage("Error Server")
                .setNegativeButton("OK", DialogInterface.OnClickListener{dialogINterface, i ->

                }).show()
        }
    }
    override fun onLoading(message: String) {
        val asyncDialog : ProgressDialog = ProgressDialog(getActivity())
        asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
        asyncDialog.setMessage("이미지를 분석중..!")
        asyncDialog.show()

    }
    override fun onQuit(message: String) {
        val asyncDialog : ProgressDialog = ProgressDialog(getActivity())
        asyncDialog.dismiss()
    }
    override fun onLoad(message: String) {

        Toast.makeText(getActivity(), "분석 중..", Toast.LENGTH_SHORT).show()
    }
    override fun onSuccessEmpty(message: String){
        Toast.makeText(getActivity(), "디코딩 정보가 없습니다.사진을 다시 업로드하세요.", Toast.LENGTH_SHORT).show()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}