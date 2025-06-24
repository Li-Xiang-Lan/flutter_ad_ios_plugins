import 'package:applovin_max/applovin_max.dart';
import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';

class IosLoadAdResultCallback{
  Function(AdInfoData? bean) startLoadAdCallback;
  Function(MaxAd? ad,AdInfoData? bean) loadAdSuccessCallback;
  Function(AdInfoData? bean) loadAdFailCallback;

  IosLoadAdResultCallback({
    required this.startLoadAdCallback,
    required this.loadAdSuccessCallback,
    required this.loadAdFailCallback,
  });
}