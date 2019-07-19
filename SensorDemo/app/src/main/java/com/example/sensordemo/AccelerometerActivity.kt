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
import kotlinx.android.synthetic.main.activity_accelerometer.*

class AccelerometerActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    private lateinit var acceleromaterSensor: Sensor

    private lateinit var acceleromaterSensorListener: SensorEventListener


    private var lastUpdate: Long = 0
    private var last_x: Float = 0.toFloat()
    private var last_y: Float = 0.toFloat()
    var last_z: Float = 0.toFloat()
    private var SHAKE_THRESHOLD = 600

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        acceleromaterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }


    override fun onResume() {
        super.onResume()

//====================For ACCELEROMETER Sensor Register =================================//
        acceleromaterSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val mySensor = sensorEvent.sensor

                if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = sensorEvent.values[0]
                    val y = sensorEvent.values[1]
                    val z = sensorEvent.values[2]

                    val curTime = System.currentTimeMillis()

                    if (curTime - lastUpdate > 100) {
                        val diffTime = curTime - lastUpdate
                        lastUpdate = curTime

                        val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000

                        if (speed > SHAKE_THRESHOLD) {

                        }

                        last_x = x
                        last_y = y
                        last_z = z
                        tvvaluesofaccelerometer.text = "X : $x  Y : $y  Z : $z"

                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        // Register the listener
        sensorManager.registerListener(
            acceleromaterSensorListener,
            acceleromaterSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(acceleromaterSensorListener);
    }

}
