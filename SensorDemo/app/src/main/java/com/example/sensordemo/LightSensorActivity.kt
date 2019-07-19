package com.example.sensordemo

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_light_sensor.*

class LightSensorActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    private lateinit var lightSensor: Sensor

    private lateinit var lightSensorListener: SensorEventListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light_sensor)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }


    override fun onResume() {
        super.onResume()

//====================For Light Sensor Register =================================//
        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                if(sensorEvent.values[0] > 20000) {
                    window.decorView.setBackgroundColor(Color.BLUE)
                    tvValueoflight.text = sensorEvent.values[0].toString()
                }
                else{
                    window.decorView.setBackgroundColor(Color.YELLOW)
                    tvValueoflight.text = sensorEvent.values[0].toString()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        // Register the listener
        sensorManager.registerListener(
            lightSensorListener,
            lightSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(lightSensorListener);
    }

}
