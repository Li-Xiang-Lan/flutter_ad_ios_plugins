import 'package:applovin_max/applovin_max.dart';
import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';

class IosAdCallback{
  Function(MaxAd? ad,AdInfoData? bean) showSuccess;
  Function(MaxAd? ad) showFail;
  Function() closeAd;
  Function(MaxAd? ad,AdInfoData? bean) onAdRevenuePaidCallback;

  IosAdCallback({
    required this.showSuccess,
    required this.showFail,
    required this.closeAd,
    required this.onAdRevenuePaidCallback,
  });
}