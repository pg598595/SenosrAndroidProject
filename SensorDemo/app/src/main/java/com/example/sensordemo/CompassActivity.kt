package com.example.sensordemo

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import kotlinx.android.synthetic.main.activity_compass.*



class CompassActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    private lateinit var compassSensor: Sensor
    var currentDegree = 0f
    private lateinit var compassListener: SensorEventListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

    }


    override fun onResume() {
        super.onResume()

//====================For Comapss Register =================================//
        compassListener = object : SensorEventListener {
            @SuppressLint("SetTextI18n")
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                // get the angle around the z-axis rotated
                val degree = Math.round(sensorEvent.values[0])

                textvalue.text = "Heading: " + java.lang.Float.toString(degree.toFloat()) + " degrees"

                // create a rotation animation (reverse turn degree degrees)
                val ra = RotateAnimation(
                    currentDegree,
                    -degree.toFloat(),
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )

                // how long the animation will take place
                ra.duration = 210

                // set the animation after the end of the reservation status
                ra.fillAfter = true

                // Start the animation
                imageViewCompass.startAnimation(ra)
                currentDegree = -degree.toFloat()

            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        // Register the listener
        sensorManager.registerListener(
            compassListener,
            compassSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(compassListener)
    }


}