package com.niloy.native_code_example

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    /// These channel names must be same as Flutter side
    private val METHOD_CHANNEL_NAME = "com.niloy.native_code_example/method"
    private val PRESSURE_CHANNEL_NAME = "com.niloy.native_code_example/pressure"

    /// These are just different things that are initialized later
    /// for now we made them nullable, we can also use lateInit
    private var sensorManager: SensorManager? = null
    private var methodChannel: MethodChannel? = null
    private var pressureChannel: EventChannel? = null
    private var pressureStreamHandler: StreamHandler? = null

    /// This acts as initState() function
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        /// Setup Channels
        setupChannels(this, flutterEngine.dartExecutor.binaryMessenger)
    }

    /// This acts as dispose() function
    override fun onDestroy() {
        teardownChannels()
        super.onDestroy()
    }

    /// Setting up different channels in one place
    private fun setupChannels(context: Context, messenger: BinaryMessenger) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        /// Async functionalities starts here
        methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        methodChannel?.setMethodCallHandler { call, result ->
            /// "isSensorAvailable" is just a string that connects us to Flutter side
            /// this value must be same for both sides
            if (call.method == "isSensorAvailable") {
                /// this could be any sensors, but for this example, we are using "pressure" sensor
                /// "Sensor" here is a enum type, so it makes easier to select sensors
                result.success(sensorManager?.getSensorList(Sensor.TYPE_PRESSURE)?.isNotEmpty())
            } else {
                result.notImplemented()
            }
        }

        /// Stream functionalities starts here
        pressureChannel = EventChannel(messenger, PRESSURE_CHANNEL_NAME)
        pressureStreamHandler = StreamHandler(sensorManager!!, Sensor.TYPE_PRESSURE)
        pressureChannel?.setStreamHandler(pressureStreamHandler)
    }

    /// Setting up cancel functions in one place
    private fun teardownChannels() {
        methodChannel?.setMethodCallHandler(null)
        pressureChannel?.setStreamHandler(null)
    }
}
