package com.example.sensordemo

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class GyroscopeActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    private lateinit var gyroscopeSensor: Sensor

    private lateinit var gyroscopeSensorListener: SensorEventListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gyroscope)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    }


    override fun onResume() {
        super.onResume()

//====================For Gyroscope Sensor Register =================================//
        gyroscopeSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                if(sensorEvent.values[2] > 0.5f) { // anticlockwise
                    window.decorView.setBackgroundColor(Color.BLUE)



                } else if(sensorEvent.values[2] < -0.5f) {
                    window.decorView.setBackgroundColor(Color.YELLOW)

                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

            // Register the listener
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(gyroscopeSensorListener);
    }


}
