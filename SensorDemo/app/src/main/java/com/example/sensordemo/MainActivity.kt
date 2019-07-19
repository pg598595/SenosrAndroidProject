package com.example.sensordemo

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

//, GestureDetector.OnGestureListener,
//    GestureDetector.OnDoubleTapListener
class MainActivity : AppCompatActivity() , GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener{

    lateinit var sensorManager: SensorManager

    private lateinit var mDetector: GestureDetectorCompat

    //Tracking Velocity
    private var mVelocityTracker: VelocityTracker? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetList.setOnClickListener {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
            Log.i("List of Sensors","$deviceSensors")


            //val sensorList = mgr.getSensorList(Sensor.TYPE_ALL)
            val devicename = StringBuilder()
            for (s in deviceSensors) {
                devicename.append(s.name + "\n")
            }
            listOfSensor.visibility = View.VISIBLE
            listOfSensor.text = devicename





        }

        btnGyroscope.setOnClickListener {
            val intent = Intent(this, GyroscopeActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        btnProximity.setOnClickListener {
            val intent = Intent(this, ProximityActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        btnRotation.setOnClickListener {
            val intent = Intent(this, RotationVectorActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        btnStepCount.setOnClickListener {
            val intent = Intent(this, StepCountActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        btnLightSensor.setOnClickListener {
            val intent = Intent(this, LightSensorActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        btnTempSensor.setOnClickListener {
            val intent = Intent(this, TempetatureActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        btnAccelerometer.setOnClickListener {
            val intent = Intent(this, AccelerometerActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        btnCamera2.setOnClickListener {
            val intent = Intent(this, Camera2Activity::class.java)
            // start your next activity
            startActivity(intent)
        }
        btnCameraX.setOnClickListener {
            val intent = Intent(this, CameraXDemo::class.java)
            // start your next activity
            startActivity(intent)
        }

        mDetector = GestureDetectorCompat(this, this)
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this)
    }




    //=========================================Detect Touch Events On Screen========================================//

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }


    override fun onDown(event: MotionEvent): Boolean {
        Log.d("TAG", "onDown: $event")
        return true
    }

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.d("TAG", "onFling: $event1 $event2")
        return true
    }

    override fun onLongPress(event: MotionEvent) {
        Log.d("TAG", "onLongPress: $event")
    }

    override fun onScroll(
        event1: MotionEvent,
        event2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.d("TAG", "onScroll: $event1 $event2")
        return true
    }

    override fun onShowPress(event: MotionEvent) {
        Log.d("TAG", "onShowPress: $event")
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        Log.d("TAG", "onSingleTapUp: $event")
        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        Log.d("TAG", "onDoubleTap: $event")
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        Log.d("TAG", "onDoubleTapEvent: $event")
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        Log.d("TAG", "onSingleTapConfirmed: $event")
        return true
    }


}
