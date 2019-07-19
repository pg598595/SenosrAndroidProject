package com.example.sensordemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_TEMPERATURE
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tempetature.*

class TempetatureActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    private lateinit var TempeSensor: Sensor

    private lateinit var TempeSensorListener: SensorEventListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tempetature)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        TempeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    }


    override fun onResume() {
        super.onResume()

//====================For Temp Sensor Register =================================//
        TempeSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val fahrenheit = sensorEvent.values[0] * 9 / 5 + 32
                     currenttemp!!.text = fahrenheit.toString() + " Fahrenheit"
             currenttemp!!.invalidate()
                  Log.i("TAG","onSensorChanged")
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        // Register the listener
        sensorManager.registerListener(
            TempeSensorListener,
            TempeSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(TempeSensorListener);
    }


}




