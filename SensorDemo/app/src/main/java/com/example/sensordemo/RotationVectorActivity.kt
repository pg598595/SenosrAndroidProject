package com.example.sensordemo

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_rotation_vector.*

class RotationVectorActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    private lateinit var rotaionSensor: Sensor

    private lateinit var rotationSensorListener: SensorEventListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation_vector)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotaionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)


    }


    override fun onResume() {
        super.onResume()

//====================For Rotation vector  Sensor=================================//

        rotationSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val rotationMatrix = FloatArray(16)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values)
                    // Remap coordinate system
                val remappedRotationMatrix = FloatArray(16)
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix
                )

                    // Convert to orientations
                val orientations = FloatArray(3)
                SensorManager.getOrientation(remappedRotationMatrix, orientations)
                for (i in 0..2) {
                    orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
                }

                when {
                    orientations[2] > 45 -> {
                        window.decorView.setBackgroundColor(Color.YELLOW)
                        tv_rotaionvalue.text = orientations[2].toString() + " Degrees"
                    }
                    orientations[2] < -45 -> {
                        window.decorView.setBackgroundColor(Color.BLUE)
                        tv_rotaionvalue.text = orientations[2].toString()+ " Degrees"
                    }
                    Math.abs(orientations[2]) < 10 -> {
                        window.decorView.setBackgroundColor(Color.WHITE)
                        tv_rotaionvalue.text = orientations[2].toString()+ " Degrees"
                    }


                }

            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }


        sensorManager.registerListener(
            rotationSensorListener,
            rotaionSensor, 2 * 1000 * 1000
        )


    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(rotationSensorListener)

    }
}
