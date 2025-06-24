import 'package:applovin_max/applovin_max.dart';
import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';
import 'package:flutter_ad_ios_plugins/data/load_result_data.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_num_hep.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_type.dart';
import 'package:flutter_ad_ios_plugins/hep/hep.dart';

///新版加载广告
class NewLoadIosAd{
  bool interAd;
  final List<AdInfoData> _adInfoList=[];
  final List<String> _loadingList=[];
  final Map<String,LoadResultData> _resultMap={};


  NewLoadIosAd({
    required this.interAd,
    required List<AdInfoData> adInfoList,
  }){
    updateAdList(adInfoList);
  }

  loadAllAd(){
    if(_adInfoList.isEmpty){
      "flutter ios ad --->${interAd?"inter ad":"rv ad"}--->list is empty".log();
      return;
    }
    if(AdNumHep.instance.notLoad()){
      "flutter ios ad --->${interAd?"inter ad":"rv ad"}--->show or click max, not load ad".log();
      return;
    }
    for (var value in _adInfoList) {
      var result = loadAdById(value);
      if(!result){
        continue;
      }
    }
  }

  bool loadAdById(AdInfoData value){
    if (_loadingList.contains(value.adId)) {
      "flutter ios ad --->${interAd ? "inter ad" : "rv ad"}--->${value.adId} is loading".log();
      return false;
    }
    if (checkHasCache(value.adId)) {
      "flutter ios ad --->${interAd ? "inter ad" : "rv ad"}--->${value.adId} has cache".log();
      return false;
    }
    _loadingList.add(value.adId);
    "flutter ios ad --->${interAd ? "inter ad" : "rv ad"}--->start load ${value.adId} ,info=>${value.toString()}".log();
    if (value.adType == AdType.reward) {
      AppLovinMAX.loadRewardedAd(value.adId);
    } else if (value.adType == AdType.interstitial) {
      AppLovinMAX.loadInterstitial(value.adId);
    } else {
      _loadingList.remove(value.adId);
    }
    return true;
  }

  loadAdSuccess(MaxAd ad){
    var adBean = getAdInfoBeanById(ad.adUnitId);
    if(null!=adBean){
      "flutter ios ad --->${interAd?"inter ad":"rv ad"}--->${ad.adUnitId} load ad success".log();
      _loadingList.remove(adBean.adId);
      _resultMap[adBean.adId]=LoadResultData(
        loadTime: DateTime.now().millisecondsSinceEpoch,
        adBean: adBean,
        revenue: ad.revenue,
      );
    }
  }

  loadAdFail(String id){
    var adBean = getAdInfoBeanById(id);
    if(null!=adBean){
      "flutter ios ad --->${interAd?"inter ad":"rv ad"}--->$id load ad fail".log();
      _loadingList.remove(adBean.adId);
      loadAdById(adBean);
    }
  }

  AdInfoData? getAdInfoBeanById(String id){
    var indexWhere = _adInfoList.indexWhere((value)=>value.adId==id);
    if(indexWhere>=0){
      return _adInfoList[indexWhere];
    }
    return null;
  }

  bool checkHasCache(String adId){
    var bean = _resultMap[adId];
    if(null!=bean){
      var expired = (DateTime.now().millisecondsSinceEpoch-bean.loadTime)>bean.adBean.expireTime*1000;
      if(expired){
        deleteCache(bean.adBean.adId);
        return false;
      }
      return true;
    }
    return false;
  }

  LoadResultData? getCashAd(){
    List<LoadResultData> list=[];
    for (var value in _resultMap.keys) {
      var data = _resultMap[value];
      if(null!=data&&checkHasCache(data.adBean.adId)){
        list.add(data);
      }
    }
    if(list.isEmpty){
      return null;
    }
    list.sort((a, b) => (b.revenue).compareTo(a.revenue));
    return list.first;
  }

  deleteCache(String? adId){
    _resultMap.removeWhere((key,value)=>value.adBean.adId==adId);
  }

  updateAdList(List<AdInfoData> adInfoList){
    "flutter ios ad --->${interAd?"inter ad":"rv ad"}--->update ad list ---> adInfoList--->$adInfoList".log();
    _adInfoList.clear();
    adInfoList.sort((a, b) => (b.sort).compareTo(a.sort));
    _adInfoList.addAll(adInfoList);
    loadAllAd();
  }
}