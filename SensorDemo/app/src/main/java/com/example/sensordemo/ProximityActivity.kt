package com.example.sensordemo

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.os.PowerManager



class ProximityActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager

    private lateinit var proximitySensor: Sensor

    private lateinit var proximitySensorListener: SensorEventListener

    lateinit var  powerManager: PowerManager
    lateinit var wakeLock: PowerManager.WakeLock
    private val field:Int = 0x00000020
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proximity)
        powerManager= getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(field, localClassName)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)


    }


    override fun onResume() {
        super.onResume()

//====================For Proximity Sensor=================================//

         proximitySensorListener = object : SensorEventListener {
             override fun onSensorChanged(sensorEvent: SensorEvent) {
//                if(sensorEvent.values[0] < proximitySensor.maximumRange) {
//                    // Detected something nearby
//                    window.decorView.setBackgroundColor(Color.RED)
//                    if(!wakeLock.isHeld) {
//                        wakeLock.acquire()
//                    }
//
//
//                } else {
//                    // Nothing is nearby
//                    window.decorView.setBackgroundColor(Color.GREEN)
//                    if(wakeLock.isHeld) {
//                        wakeLock.release()
//                    }
//
//                }
                 if (!wakeLock.isHeld) {
                     wakeLock.acquire()
                 } else {
                     wakeLock.release()
                 }

            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }


        sensorManager.registerListener(
            proximitySensorListener,
            proximitySensor, 2 * 1000 * 1000
        )


    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(proximitySensorListener)

    }


}