
import 'dart:async';
import 'package:flutter/services.dart';
import 'dart:io';

final ATFlutterChannel = AnythinkSdk();

class AnythinkSdk {
  static final MethodChannel channel = MethodChannel(Platform.isIOS ? 'anythink_sdk' : 'thinkup_sdk');

  static Future<String?> get platformVersion async {
    final String? version = await channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
