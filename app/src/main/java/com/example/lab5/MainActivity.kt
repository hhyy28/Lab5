package com.example.lab5

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lab5.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val gravity = FloatArray(3)
    private val linear = FloatArray(3)
    private val alpha = 0.8f

    private var maxG = 0f
    private var sumG = 0f
    private var countG = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        binding.btnReset.setOnClickListener {
            maxG = 0f
            sumG = 0f
            countG = 0
            binding.tvMax.text = "0.00 G"
            binding.tvAvg.text = "0.00 G"
            binding.historyView.clear()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        linear[0] = event.values[0] - gravity[0]
        linear[1] = event.values[1] - gravity[1]
        linear[2] = event.values[2] - gravity[2]

        val g = 9.81f
        val gxForce = linear[0] / g
        val gyForce = linear[1] / g
        val gzForce = linear[2] / g

        val magnitude = sqrt(gxForce * gxForce + gyForce * gyForce + gzForce * gzForce)

        binding.tvCurrent.text = String.format("%.2f G", magnitude)
        binding.tvCurrent.setTextColor(
            when {
                magnitude < 0.5f -> 0xFF4CAF50.toInt()
                magnitude < 1.0f -> 0xFFFFC107.toInt()
                else -> 0xFFF44336.toInt()
            }
        )

        binding.tvX.text = String.format("%.2f", gxForce)
        binding.tvY.text = String.format("%.2f", gyForce)
        binding.tvZ.text = String.format("%.2f", gzForce)

        binding.tvDirection.text = getDirection(gxForce, gyForce)

        binding.gForceView.updateForce(gxForce, gyForce, magnitude)

        if (magnitude > maxG) {
            maxG = magnitude
            binding.tvMax.text = String.format("%.2f G", maxG)
        }

        sumG += magnitude
        countG++
        binding.tvAvg.text = String.format("%.2f G", sumG / countG)

        binding.historyView.addValue(magnitude)
    }

    private fun getDirection(x: Float, y: Float): String {
        if (abs(x) < 0.15f && abs(y) < 0.15f) return "—"
        val horizontal = when {
            x > 0.15f -> "вправо"
            x < -0.15f -> "вліво"
            else -> ""
        }
        val vertical = when {
            y > 0.15f -> "прискорення"
            y < -0.15f -> "гальмування"
            else -> ""
        }
        return listOf(vertical, horizontal).filter { it.isNotEmpty() }.joinToString(", ")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}