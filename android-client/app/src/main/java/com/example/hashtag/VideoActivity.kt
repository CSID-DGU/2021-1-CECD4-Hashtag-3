package com.example.hashtag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pub.devrel.easypermissions.EasyPermissions
import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.hashtag.Constants.FILENAME_FORMAT
import com.example.hashtag.Constants.REQUEST_CODE_CAMERA_PERMISSION
import com.example.hashtag.Constants.TAG
import com.example.hashtag.upload.CartActivity
import com.example.hashtag.upload.UploadPresenter
import com.example.hashtag.upload.UploadView
import com.example.hashtag.upload.model.*
import kotlinx.android.synthetic.main.activity_video.*
import pub.devrel.easypermissions.AppSettingsDialog
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.Serializable
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


typealias LumaListener = (luma: Double) -> Unit

class VideoActivity : AppCompatActivity(),UploadView, Serializable, EasyPermissions.PermissionCallbacks{
    var pathData = ArrayList<ResponseUpload>()
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var image_path : String? = null
    private var presenter: UploadPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        requestPermission()
        presenter = UploadPresenter(this)

        complete_btn.setOnClickListener {
            onDestroy()
            val intentss = Intent(this@VideoActivity, FeedActivity::class.java)
            startActivity(intentss)
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        var second: Int = 0
        kotlin.concurrent.timer(period = 1000,initialDelay = 1000){

           takePhoto()
        }


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
            ContextCompat.getMainExecutor(this),
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
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
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

        }, ContextCompat.getMainExecutor(this))
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun requestPermission() {

        if (CameraUtility.hasCameraPermissions(this)) {
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
        AlertDialog.Builder(this)
            .setTitle("사진 선택 미완료")
            .setMessage("사진을 업로드해주세요.")
            .setNegativeButton("확인", DialogInterface.OnClickListener{ dialogInterface, i ->

            }).show()
    }

    override fun onSuccessupload(List:ArrayList<ResponseUpload>) {
        Log.d("success",List.toString())
    }
    override fun onSuccessFeed(List:ArrayList<Cart>, List1:ArrayList<Feed>) {

    }
    override fun onSuccess(message: String) {
    }
    override fun onErrorServer(message: String) {
        Toast.makeText(baseContext, "서버에러", Toast.LENGTH_SHORT).show()
    }
    override fun onLoad(message: String) {
        Toast.makeText(baseContext, "분석 중..", Toast.LENGTH_SHORT).show()
    }
    override fun onLoading(message: String) {
        val asyncDialog : ProgressDialog = ProgressDialog(this@VideoActivity)
        asyncDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
        asyncDialog.setMessage("종료 중..")
        asyncDialog.show()

    }
    override fun onQuit(message: String) {
        val asyncDialog : ProgressDialog = ProgressDialog(this@VideoActivity)
        asyncDialog.dismiss()
    }

}