package com.example.shake_practice

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.shake_practice.databinding.ActivityMainBinding
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    var mbinding: ActivityMainBinding? = null
    val binding get() = mbinding!!
    
    val TAG: String = "로그"

    private var accel: Float = 0.0f //초기
    private var accelCurrent: Float = 0.0f //이동하는 치수
    private var accelLast: Float = 0.0f
    
    //당장이 아니라 나중에 값을 넣겠다. 온크레이트 이후에 설정을 넣겠다. 
    private lateinit var sensorManager: SensorManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        Log.d(TAG, "MainActivity - onCreate() called")

        //센서 매니저를 설정한다.
        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH //지구 중력값 주기
        accelLast = SensorManager.GRAVITY_EARTH

    }

    //흔들었을때 센서 감지
    override fun onSensorChanged(event: SensorEvent?) {
        Log.d(TAG, "MainActivity - onSensorChanged() called")

        val x:Float = event?.values?.get(0) as Float
        val y:Float = event?.values?.get(0) as Float
        val z:Float = event?.values?.get(0) as Float

        binding.x.text = "X: " + x.toInt().toString()
        binding.y.text = "Y: " + y.toInt().toString()
        binding.z.text = "Z: " + z.toInt().toString()

        accelLast = accelCurrent
        accelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta: Float = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        //액셀 치수가 30이 넘어가면 흔들었다고 휴대폰이 판단한다.
        if(accel > 30){
            Log.d(TAG, "흔들었다.")

            //화난얼굴을 보여주고 1초뒤에 웃는 얼굴을 보여준다.
            binding.smile.setImageResource(R.drawable.ic_free_icon_angry_187140) //화난 얼굴

            //웃는 얼굴
            Handler(Looper.myLooper()!!).postDelayed({
                binding.smile.setImageResource(R.drawable.ic_free_icon_joke_185034) //웃는 얼굴
            }, 1000L)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "MainActivity - onAccuracyChanged() called")
    }

    //단말기가 켜졌을 때, 센서매니저 리스너 실행
    override fun onResume() {
        Log.d(TAG, "MainActivity - onResume() called")
        sensorManager.registerListener(this, sensorManager
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            ,SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }

    //휴대폰이 꺼졌을때는 매니저를 꺼준다.
    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }
}