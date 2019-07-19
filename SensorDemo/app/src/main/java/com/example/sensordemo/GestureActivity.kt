package com.example.sensordemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.activity_gesture.*
import android.view.View.OnTouchListener



class GestureActivity : AppCompatActivity() {

    lateinit var event: MotionEvent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture)

        linerLayout.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            tvTouchEvent.text = "Movement inside linear layout"
            Log.d("TAG", "Movement inside linear layout")
                false
            }

        btnCLicking.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            tvTouchEvent.text = "Movement on button of Linear Layout"
            Log.d("TAG", "Movement on button")
            false
        }

        }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG", "Movement occurred in activity")
        tvTouchEvent.text = "Movement occurred in activity"
        //val action: Int = MotionEventCompat.getActionMasked(event)

        return false
    }





}
