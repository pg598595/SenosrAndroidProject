package com.example.sensordemo

import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.Image
import android.media.ImageReader
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera2.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class Camera2Activity : AppCompatActivity() {

    private val TAG = "AndroidCameraApi"
    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    private val REQUEST_CAMERA_PERMISSION = 200




    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private lateinit var cameraCaptureSessions: CameraCaptureSession

    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private var imageDimension: Size? = null
    private var imageReader: ImageReader? = null

    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null



//================================Texture Listener===============and override methods=========================
    private var textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {

        @SuppressLint("NewApi")
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            //open your camera here

            Log.i(TAG,"===onSurfaceTextureAvailable==")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                openCamera()
            }
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            // Transform you image captured size according to the surface width and height
            Log.i(TAG,"===onSurfaceTextureSizeChanged==")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            Log.i(TAG,"===onSurfaceTextureDestroyed==")
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            Log.i(TAG,"===onSurfaceTextureUpdated==")
        }
         }


//=====================================Camera states ========================================
    private val stateCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {
                //This is called when the camera is open
                Log.e(TAG, "onOpened")
                cameraDevice = camera
                Log.i(TAG,"Decvie name: ===== $cameraDevice")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    createCameraPreview()
                }
            }


            override fun onDisconnected(camera: CameraDevice) {
                cameraDevice!!.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                cameraDevice!!.close()
                cameraDevice = null
            }
        }
    } else {
        TODO("VERSION.SDK_INT < LOLLIPOP")
    }

//============================On create method===================================================

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_camera2)


                texture.surfaceTextureListener = textureListener



                btn_takepicture.setOnClickListener { takePicture() }
            }



//=================================Start BackGround Thread and Stop Background thread===========================

            private fun startBackgroundThread() {
                mBackgroundThread = HandlerThread("Camera Background")
                mBackgroundThread!!.start()
                mBackgroundHandler = Handler(mBackgroundThread!!.looper)
            }

            private fun stopBackgroundThread() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    mBackgroundThread!!.quitSafely()
//                }
                mBackgroundThread!!.quitSafely()
                try {
                    mBackgroundThread!!.join()
                    mBackgroundThread = null
                    mBackgroundHandler = null
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

//===========================Taking Picture and saving to storage ===============================================

            @SuppressLint("NewApi")
            private fun takePicture() {
                if (null == cameraDevice) {
                    Log.e(TAG, "cameraDevice is null")
                    return
                }
                var manager: CameraManager? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                }
                try {
                    val characteristics = manager!!.getCameraCharacteristics(cameraDevice!!.id)
                    var jpegSizes: Array<Size>? = null
                    if (characteristics != null) {
                        jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.getOutputSizes(
                            ImageFormat.JPEG
                        )

                        Log.i("Take Picture","jpeg size================= $jpegSizes")

                    }

                    //===================size of pic========================
                    var width = 640
                    var height = 480


                    //===============Check if jpeg size is null===========================
                    if (jpegSizes != null && jpegSizes.isNotEmpty()) {
                        width = jpegSizes[0].width
                        height = jpegSizes[0].height
                    }




                    val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
                    val outputSurfaces = ArrayList<Surface>(2)
                    outputSurfaces.add(reader.surface)
                    outputSurfaces.add(Surface(texture!!.surfaceTexture))
                    val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                    captureBuilder.addTarget(reader.surface)
                    captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)



                    //=============handle Orientation========================

                    val rotation = windowManager.defaultDisplay.rotation
                    captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))

                    //===================adding image File to storage ===============================




                    val s = SimpleDateFormat("ddMMyyyyhhmmss")
                    val format = s.format(Date())
                    Log.i(TAG,"Current Date and Time is: $format")

                    val file = File(Environment.getExternalStorageDirectory().toString() + "/$format.jpg")

                    val readerListener = object : ImageReader.OnImageAvailableListener {
                        override fun onImageAvailable(reader: ImageReader) {
                            var image: Image? = null
                            try {
                                image = reader.acquireLatestImage()
                                val buffer = image!!.planes[0].buffer
                                val bytes = ByteArray(buffer.capacity())
                                buffer.get(bytes)
                                save(bytes)
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                image?.close()
                            }
                        }

                        @Throws(IOException::class)
                        private fun save(bytes: ByteArray) {
                            var output: OutputStream? = null
                            try {
                                output = FileOutputStream(file)
                                output.write(bytes)
                            } finally {
                                output?.close()
                            }
                        }
                    }


                    reader.setOnImageAvailableListener(readerListener, mBackgroundHandler)
                    val captureListener = object : CameraCaptureSession.CaptureCallback() {
                        override fun onCaptureCompleted(
                            session: CameraCaptureSession,
                            request: CaptureRequest,
                            result: TotalCaptureResult
                        ) {
                            super.onCaptureCompleted(session, request, result)
                            Toast.makeText(this@Camera2Activity, "Saved:$file", Toast.LENGTH_SHORT).show()
                            createCameraPreview()
                        }
                    }

                    cameraDevice!!.createCaptureSession(outputSurfaces, object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            try {
                                session.capture(captureBuilder.build(), captureListener, mBackgroundHandler)
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }

                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {}
                    }, mBackgroundHandler)
                }


                catch (e: CameraAccessException) {
                    e.printStackTrace()
                }

            }



//===============================Create camera view and set in Texture View =====================================================================

            private fun createCameraPreview() {
                try {
                    val texture = texture!!.surfaceTexture!!
                    texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
                    val surface = Surface(texture)
                    captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    captureRequestBuilder.addTarget(surface)
                    cameraDevice!!.createCaptureSession(Arrays.asList(surface), object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                                //The camera is already closed
                            if (null == cameraDevice) {
                                return
                            }
                                // When the session is ready, we start displaying the preview.
                            cameraCaptureSessions = cameraCaptureSession
                            updatePreview()
                        }

                        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                            Toast.makeText(this@Camera2Activity, "Configuration change", Toast.LENGTH_SHORT).show()
                        }
                    }, null)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
                private fun updatePreview() {
                    if (null == cameraDevice) {
                        Log.e(TAG, "updatePreview error, return")
                    }
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                    try {
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }

                }




//==============================open Camera with specific id====================================================================================
            @SuppressLint("NewApi")
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(Build.VERSION_CODES.M)
            private fun openCamera() {
                val manager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                Log.e(TAG, "is camera open")


                        try {

                            //==========================================give Id as 1 for Front and Id 0 for Back camera==============================================
                            cameraId = manager.cameraIdList[1]
                            Log.i("TAG","cameraId is ============ $cameraId")
                            val characteristics = manager.getCameraCharacteristics(cameraId!!)

                            Log.i("TAG","camera dTEAILS ===Lens ============ ${characteristics.get(CameraCharacteristics.LENS_FACING)}")
                            Log.i("TAG","camera dTEAILS ===effect ============ ${characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS)}")
                            Log.i("TAG","camera dTEAILS ====Modes ============ ${characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES)}")
                            Log.i("TAG","camera dTEAILS =====AWB Modes ============ ${characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)}")
                            Log.i("TAG","camera dTEAILS ==== Flash info============ ${characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)}")
                            Log.i("TAG","camera dTEAILS ==== Info Version============ ${characteristics.get(CameraCharacteristics.INFO_VERSION)}")


                            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!

                            Log.i("TAG","camera dTEAILS ==== Map============ $map")


                            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]


                                    //============================= Add permission for camera and let user grant the permission====================================
                                            if (ActivityCompat.checkSelfPermission(
                                                    this,
                                                    Manifest.permission.CAMERA
                                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                                    this,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                ) != PackageManager.PERMISSION_GRANTED
                                            ) {
                                                ActivityCompat.requestPermissions(
                                                    this@Camera2Activity,
                                                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                                    REQUEST_CAMERA_PERMISSION
                                                )
                                                return
                                            }
                                        manager.openCamera(cameraId!!, stateCallback, null)

                        }


                        catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }

                Log.e(TAG, "openCamera X")
            }





//==========================close Camera===============================================================================

                 private fun closeCamera() {
                 if (null != cameraDevice) {
                 cameraDevice!!.close()
                 cameraDevice = null
                 }
                 if (null != imageReader) {
                 imageReader!!.close()
                 imageReader = null
                 }
                 }




//================For Pemission Request=======================================================================================

                    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                        if (requestCode == REQUEST_CAMERA_PERMISSION) {
                            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // close the app
                                Toast.makeText(
                                    this@Camera2Activity,
                                    "Sorry!!!, you can't use this app without granting permission",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                        }
                    }


//=====================On resume method========================================
            @SuppressLint("NewApi")
            override fun onResume() {
                super.onResume()
                Log.e(TAG, "onResume")
                startBackgroundThread()
                if (texture!!.isAvailable) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        openCamera()
                    }
                } else {
                    texture!!.surfaceTextureListener = textureListener
                }
            }



//=====================On Pause method========================================
            override fun onPause() {
                Log.e(TAG, "onPause")
                closeCamera()
                stopBackgroundThread()
                super.onPause()
            }


}