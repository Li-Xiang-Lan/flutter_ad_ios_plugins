import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';

class ConfigAdData{
  int maxShowNum;
  int maxClickNum;
  List<AdInfoData> oneRewardList;
  List<AdInfoData> oneInterList;
  List<AdInfoData> twoRewardList;
  List<AdInfoData> twoInterList;
  ConfigAdData({
    required this.maxShowNum,
    required this.maxClickNum,
    required this.oneRewardList,
    required this.oneInterList,
    required this.twoRewardList,
    required this.twoInterList,
  });
}