import 'dart:async';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static const methodChannel =
      MethodChannel("com.niloy.native_code_example/method");
  static const pressureChannel =
      EventChannel("com.niloy.native_code_example/pressure");
  String _sensorAvailable = "Unknown";
  double _pressureReading = 0.0;
  StreamSubscription? pressureSubscription;

  /// This function checks if the sensor exists or not
  Future<void> _checkAvailability() async {
    try {
      /// "isSensorAvailable" is the same string we used in kotlin
      /// both must be same, no typo allowed
      bool available = await methodChannel.invokeMethod("isSensorAvailable");
      setState(() {
        _sensorAvailable = available.toString();
      });
    } on PlatformException catch (e) {
      log(e.toString());
    } catch (e) {
      log(e.toString());
    }
  }

  /// This function reads sensor data from a stream
  void _startReading() {
    pressureSubscription =
        pressureChannel.receiveBroadcastStream().listen((event) {
      setState(() {
        _pressureReading = event;
      });
    });
  }

  /// This will stop the stream from reading
  void _stopReading() {
    pressureSubscription?.cancel();
    setState(() {
      _pressureReading = 0.0;
    });
  }

  @override
  void dispose() {
    super.dispose();
    _startReading();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Native Code Example"),
        centerTitle: true,
        elevation: 0,
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text("Sensor Available: $_sensorAvailable"),
            ElevatedButton(
              onPressed: _checkAvailability,
              child: const Text("Check for Sensor Availability"),
            ),
            const SizedBox(
              height: 50,
            ),
            Text("Sensor Reading: $_pressureReading"),
            ElevatedButton(
              onPressed: _startReading,
              child: const Text("Start Reading"),
            ),
            ElevatedButton(
              onPressed: _stopReading,
              child: const Text("Stop Reading"),
            )
          ],
        ),
      ),
    );
  }
}
