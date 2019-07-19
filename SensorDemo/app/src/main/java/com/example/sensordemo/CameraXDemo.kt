package com.example.sensordemo

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import java.io.File

class CameraXDemo : AppCompatActivity() {


    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")
    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_xdemo)

        textureView = findViewById(R.id.view_finder)

        if (allPermissionsGranted()) {
            startCamera() //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun startCamera() {

        CameraX.unbindAll()

        val aspectRatio: Rational = Rational(textureView.width, textureView.height)

        var screen: Size = Size(textureView.width, textureView.height) //size of the screen


        val pConfig = PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build()
        val preview = Preview(pConfig)

        preview.onPreviewOutputUpdateListener = Preview.OnPreviewOutputUpdateListener { output ->
            //to update the surface texture we have to destroy it first then re-add it

            val parent = textureView.parent as ViewGroup
            parent.removeView(textureView)
            parent.addView(textureView, 0)

            textureView.surfaceTexture = output.surfaceTexture
            updateTransform()
        }


        val imageCaptureConfig = ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            .setTargetRotation(windowManager.defaultDisplay.rotation).build()
        val imgCap = ImageCapture(imageCaptureConfig)

        findViewById<View>(R.id.imgCapture).setOnClickListener {
            val file =
                File(Environment.getExternalStorageDirectory().toString() + "/" + System.currentTimeMillis() + ".png")
            imgCap.takePicture(file, object : ImageCapture.OnImageSavedListener {
                override fun onImageSaved(file: File) {
                    val msg = "Pic captured at " + file.absolutePath
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                }

                override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
                    val msg = "Pic capture failed : $message"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    cause?.printStackTrace()
                }
            })
        }

//bind to lifecycle:
        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imgCap)
    }

    private fun updateTransform() {
        val mx = Matrix()
        val w = textureView.measuredWidth.toFloat()
        val h = textureView.measuredHeight.toFloat()

        val cX = w / 2f
        val cY = h / 2f

        val rotationDgr: Int = when (textureView.rotation.toInt()) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        mx.postRotate(rotationDgr.toFloat(), cX, cY)
        textureView.setTransform(mx)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {

        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
