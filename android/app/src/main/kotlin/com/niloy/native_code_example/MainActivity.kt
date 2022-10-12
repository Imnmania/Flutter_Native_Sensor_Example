package com.niloy.native_code_example

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val METHOD_CHANNEL_NAME = "com.niloy.native_code_example/method"
    private val PRESSURE_CHANNEL_NAME = "com.niloy.native_code_example/pressure"

    private var methodChannel: MethodChannel? = null
    private var sensorManager: SensorManager? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        /// Setup Channels
        setupChannels(this, flutterEngine.dartExecutor.binaryMessenger)
    }

    override fun onDestroy() {
        teardownChannels()
        super.onDestroy()
    }

    private fun setupChannels(context: Context, messenger: BinaryMessenger) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)

        methodChannel?.setMethodCallHandler { call, result ->
            if (call.method == "isSensorAvailable") {
                result.success(sensorManager?.getSensorList(Sensor.TYPE_PRESSURE)?.isNotEmpty())
            } else {
                result.notImplemented()
            }
        }
    }

    private fun teardownChannels() {
        methodChannel?.setMethodCallHandler(null)
    }
}
