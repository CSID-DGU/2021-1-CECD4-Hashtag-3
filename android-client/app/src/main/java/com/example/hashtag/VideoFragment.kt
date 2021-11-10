package com.example.hashtag

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.hashtag.Constants.FILENAME_FORMAT
import com.example.hashtag.Constants.REQUEST_CODE_CAMERA_PERMISSION
import com.example.hashtag.Constants.TAG
import com.example.hashtag.upload.UploadPresenter
import com.example.hashtag.upload.UploadView
import com.example.hashtag.upload.model.Cart
import com.example.hashtag.upload.model.Feed
import com.example.hashtag.upload.model.ResponseUpload
import com.example.hashtag.upload.model.VideoResponse
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.fragment_video.view.*
import okhttp3.internal.notifyAll
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.Serializable
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
var toastBefore:Toast?=null
class VideoFragment : Fragment(),UploadView, Serializable, EasyPermissions.PermissionCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var pathData = ArrayList<ResponseUpload>()
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
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

        requestPermission()
        presenter = UploadPresenter(this)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
        outputDirectory = getOutputDirectory()!!

        cameraExecutor = Executors.newSingleThreadExecutor()

        var second: Int = 0
        kotlin.concurrent.timer(period = 500,initialDelay = 1000){

            takePhoto()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_video, container, false)

        view.complete_btn2.setOnClickListener {
            val lock = Any()
            synchronized(lock){
                lock.notifyAll()
                cameraExecutor.shutdown()
//                val intentss = Intent(getActivity(), FeedActivity::class.java)
//                intentss.putExtra("current_user_id",login_id)
//                intentss.putExtra("current_user_email",login_email)
//                startActivity(intentss)
                login_id?.let { login_email?.let { it1 -> naviActivity?.callFeed(it, it1) } }
            }

        }
        return view
    }
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".png"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(getActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "저장실패: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
//                    val msg = "Photo capture succeeded: $savedUri"


                    // image_path = savedUri.let { FilePath.getPath(this, it) }.toString()
                    image_path = photoFile.toString()
                    // image_path = savedUri.toString()
//                    val msg ="저장성공  $image_path"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, msg)
                    image_path?.let { presenter?.upload_video(it)}


                }
            })
    }
    private fun startCamera() {
        val cameraProviderFuture = getActivity()?.let { ProcessCameraProvider.getInstance(it) }

        if (cameraProviderFuture != null) {
            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder2.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .build()


                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        })
                    }

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer
                    )


                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(getActivity()))
        }
    }


    private fun getOutputDirectory(): File? {
        val mediaDir = getActivity()?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else getActivity()?.filesDir
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.wait()
//    }

    private fun requestPermission() {

        if (getActivity()?.let { CameraUtility.hasCameraPermissions(it) } == true) {
            startCamera()
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept the camera permission to use this app",
                REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept the camera permission to use this app",
                REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startCamera()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
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

    override fun onSuccessupload(List:ArrayList<ResponseUpload>,id:String, email:String) {
        Log.d("success",List.toString())
    }
    override fun onSuccessFeed(List:ArrayList<Cart>, List1:ArrayList<Feed>) {

    }
    override fun onSuccess(List:ArrayList<VideoResponse>) {
        var result_string:String=""
        for (i in 0..List.size-1){
            result_string+=List.get(i).result
            result_string+="\n"
        }
//        tv_result.setText(result_string)



        toastBefore?.cancel()

//        val toast = Toast.makeText(getActivity(),  result_string, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
//        toast.show()
        val toast_s=Toast.makeText(
            getActivity(),
            result_string,
            Toast.LENGTH_SHORT
        )
        toast_s.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        toast_s.show()

        val handler = Handler()
        handler.postDelayed(Runnable { toast_s.cancel() }, 500)
    }
    override fun onErrorServer(message: String) {
//        Toast.makeText(baseContext, "서버에러", Toast.LENGTH_SHORT).show()
    }
    override fun onLoad(message: String) {
//        Toast.makeText(baseContext, "분석 중..", Toast.LENGTH_SHORT).show()
    }
    override fun onLoading(message: String) {
        val asyncDialog : ProgressDialog = ProgressDialog(getActivity())
        asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
        asyncDialog.setMessage("종료 중..")
        asyncDialog.show()

    }
    override fun onQuit(message: String) {
        val asyncDialog : ProgressDialog = ProgressDialog(getActivity())
        asyncDialog.dismiss()
    }
    override fun onSuccessEmpty(message: String){
//        Toast.makeText(getActivity(), "디코딩 정보가 없습니다.사진을 다시 업로드하세요.", Toast.LENGTH_SHORT).show()
        toastBefore?.cancel()
        val toast = Toast.makeText(getActivity(),  "디코딩 정보가 없습니다.사진을 다시 업로드하세요.", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        toast.show()
        val handler = Handler()
        handler.postDelayed(Runnable { toast.cancel() }, 500)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VideoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}