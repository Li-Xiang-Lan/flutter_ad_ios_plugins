import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';

class ConfigAdData{
  int maxShowNum;
  int maxClickNum;
  bool isNewPlan; //新方案
  List<AdInfoData> oneRewardList;
  List<AdInfoData> oneInterList;
  List<AdInfoData> twoRewardList;
  List<AdInfoData> twoInterList;
  List<AdInfoData> newInterList; //新方案插屏list
  List<AdInfoData> newRewardList; //新方案激励list
  ConfigAdData({
    required this.maxShowNum,
    required this.maxClickNum,
    required this.isNewPlan,
    required this.oneRewardList,
    required this.oneInterList,
    required this.twoRewardList,
    required this.twoInterList,
    required this.newInterList,
    required this.newRewardList,
  });
}