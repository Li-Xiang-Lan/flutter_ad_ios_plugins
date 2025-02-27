import 'package:applovin_max/applovin_max.dart';
import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';
import 'package:flutter_ad_ios_plugins/data/load_result_data.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_num_hep.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_type.dart';
import 'package:flutter_ad_ios_plugins/hep/hep.dart';

class LoadIosAd{
  bool oneAd;

  final List<AdInfoData> _rewardList=[];
  final List<AdInfoData> _interList=[];
  final List<AdType> _loadingList=[];
  final Map<AdType,LoadResultData> _resultMap={};


  LoadIosAd({
    required this.oneAd,
    required List<AdInfoData> rewardList,
    required List<AdInfoData> interList,
  }){
    updateAdList(rewardList,interList);
    loadAdByType(AdType.reward);
    loadAdByType(AdType.interstitial);
  }

  loadAdByType(AdType adType){
    if(AdNumHep.instance.notLoad()){
      "flutter ios ad --->${oneAd?"one ad":"two ad"}--->show or click max, not load ad".log();
      return;
    }
    if(_loadingList.contains(adType)){
      "flutter ios ad --->${oneAd?"one ad":"two ad"}--->$adType is loading".log();
      return;
    }
    if(null!=getCacheAd(adType)){
      "flutter ios ad --->${oneAd?"one ad":"two ad"}--->$adType has cache".log();
      return;
    }
    var list = adType==AdType.interstitial?_interList:_rewardList;
    if(list.isEmpty){
      "flutter ios ad --->${oneAd?"one ad":"two ad"}--->$adType list is empty".log();
      return;
    }
    _loadingList.add(adType);
    _startLoadAd(adType, list.first);
  }

  _startLoadAd(AdType type, AdInfoData bean){
    "flutter ios ad --->${oneAd?"one ad":"two ad"}--->start load $type ad ,info=>${bean.toString()}".log();
    if(type==AdType.reward){
      AppLovinMAX.loadRewardedAd(bean.adId);
    }else if(type==AdType.interstitial){
      AppLovinMAX.loadInterstitial(bean.adId);
    }else{
      _loadingList.remove(type);
    }
  }

  loadAdSuccess(MaxAd ad){
    var adBean = getAdInfoBeanById(ad.adUnitId);
    if(null!=adBean){
      "flutter ios ad --->${oneAd?"one ad":"two ad"}--->${ad.adUnitId} load ad success".log();
      _loadingList.remove(adBean.adType);
      _resultMap[adBean.adType]=LoadResultData(loadTime: DateTime.now().millisecondsSinceEpoch, adBean: adBean);
    }
  }

  loadAdFail(String id){
    var adBean = getAdInfoBeanById(id);
    if(null!=adBean){
      "flutter ios ad --->${oneAd?"one ad":"two ad"}--->$id load ad fail".log();
      var nextAdBean = _getNextAdBean(id);
      if(null!=nextAdBean){
        _startLoadAd(adBean.adType,nextAdBean);
      }else{
        "flutter ios ad --->${oneAd?"one ad":"two ad"}--->no next ad, end load".log();
        _loadingList.remove(adBean.adType);
        loadAdByType(adBean.adType);
      }
    }
  }

  AdInfoData? _getNextAdBean(String id){
    var indexWhere = _rewardList.indexWhere((value)=>value.adId==id);
    if(indexWhere>=0&&_rewardList.length>indexWhere+1){
      return _rewardList[indexWhere+1];
    }

    var indexWhere2 = _interList.indexWhere((value)=>value.adId==id);
    if(indexWhere2>=0&&_interList.length>indexWhere2+1){
      return _interList[indexWhere2+1];
    }
    return null;
  }

  AdInfoData? getAdInfoBeanById(String id){
    var indexWhere = _interList.indexWhere((value)=>value.adId==id);
    if(indexWhere>=0){
      return _interList[indexWhere];
    }
    var indexWhere2 = _rewardList.indexWhere((value)=>value.adId==id);
    if(indexWhere2>=0){
      return _rewardList[indexWhere2];
    }
    return null;
  }

  LoadResultData? getCacheAd(AdType type){
    var bean = _resultMap[type];
    if(null!=bean){
      var expired = (DateTime.now().millisecondsSinceEpoch-bean.loadTime)>bean.adBean.expireTime*1000;
      if(expired){
        deleteCache(bean.adBean.adId);
        return null;
      }
      return bean;
    }
    return null;
  }

  deleteCache(String? adId){
    _resultMap.removeWhere((key,value)=>value.adBean.adId==adId);
  }

  updateAdList(List<AdInfoData> rewardList,List<AdInfoData> interList){
    _rewardList.clear();
    _interList.clear();
    _rewardList.addAll(rewardList);
    _interList.addAll(interList);
  }
}