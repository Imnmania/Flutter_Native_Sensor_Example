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

  /// This is where we call the native code from Flutter
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
            Text(_sensorAvailable),
            ElevatedButton(
              onPressed: _checkAvailability,
              child: const Text("Check for Sensor"),
            ),
          ],
        ),
      ),
    );
  }
}
