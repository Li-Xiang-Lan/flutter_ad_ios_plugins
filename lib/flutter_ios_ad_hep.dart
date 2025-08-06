import 'package:applovin_max/applovin_max.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';
import 'package:flutter_ad_ios_plugins/data/config_ad_data.dart';
import 'package:flutter_ad_ios_plugins/data/load_result_data.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_num_hep.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_type.dart';
import 'package:flutter_ad_ios_plugins/hep/hep.dart';
import 'package:flutter_ad_ios_plugins/hep/ios_ad_callback.dart';
import 'package:flutter_ad_ios_plugins/hep/ios_load_ad_result_callback.dart';
import 'package:flutter_ad_ios_plugins/load/new_load_ios_ad.dart';

typedef FengKongLogic = bool Function();

class FlutterIosAdHep{
  static final FlutterIosAdHep _instance = FlutterIosAdHep();
  static FlutterIosAdHep get instance => _instance;

  //新方案加载插屏和激励
  NewLoadIosAd? _newIntLoadIosAd;
  NewLoadIosAd? _newRvLoadIosAd;
  var _adShowing=false,_priceSwitch=false;
  IosAdCallback? _iosAdCallback;
  FengKongLogic? _fengKongLogic;

  initMax({
    required String maxKey,
    required ConfigAdData data,
    required IosLoadAdResultCallback iosLoadAdResultCallback,
    bool showMediationDebugger=false,
  })async{
    await AppLovinMAX.initialize(maxKey);
    if(kDebugMode&&showMediationDebugger){
      AppLovinMAX.showMediationDebugger();
    }
    _setMaxAdListener();
    _newIntLoadIosAd=NewLoadIosAd(interAd: true, iosLoadAdResultCallback: iosLoadAdResultCallback);
    _newRvLoadIosAd=NewLoadIosAd(interAd: false, iosLoadAdResultCallback: iosLoadAdResultCallback);
    updateAdData(data);
  }

  _setMaxAdListener(){
    AppLovinMAX.setRewardedAdListener(
        RewardedAdListener(
          onAdLoadedCallback: (ad){
            _newIntLoadIosAd?.loadAdSuccess(ad);
            _newRvLoadIosAd?.loadAdSuccess(ad);
          },
          onAdLoadFailedCallback: (ad,error){
            _newIntLoadIosAd?.loadAdFail(ad);
            _newRvLoadIosAd?.loadAdFail(ad);
          },
          onAdDisplayedCallback: (ad){
            _adShowing=true;
            _deleteAdCache(ad.adUnitId);
            AdNumHep.instance.updateShowNum();
            _iosAdCallback?.showSuccess.call(ad,_getAdInfoBeanById(ad.adUnitId));
          },
          onAdDisplayFailedCallback: (ad,error){
            _adShowing=false;
            _deleteAdCache(ad.adUnitId);
            loadAd(_getAdInfoBeanById(ad.adUnitId));
            _iosAdCallback?.showFail.call(ad);
          },
          onAdClickedCallback: (ad){
            AdNumHep.instance.updateClickNum();
          },
          onAdHiddenCallback: (ad){
            _adShowing=false;
            loadAd(_getAdInfoBeanById(ad.adUnitId));
            _iosAdCallback?.closeAd.call();
          },
          onAdReceivedRewardCallback: (ad,reward){

          },
          onAdRevenuePaidCallback: (ad){
            _iosAdCallback?.revenuePaid.call(ad,_getAdInfoBeanById(ad.adUnitId));
          },
        )
    );

    AppLovinMAX.setInterstitialListener(
        InterstitialListener(
          onAdLoadedCallback: (ad){
            _newIntLoadIosAd?.loadAdSuccess(ad);
            _newRvLoadIosAd?.loadAdSuccess(ad);
          },
          onAdLoadFailedCallback: (ad,error){
            _newIntLoadIosAd?.loadAdFail(ad);
            _newRvLoadIosAd?.loadAdFail(ad);
          },
          onAdDisplayedCallback: (ad){
            _adShowing=true;
            _deleteAdCache(ad.adUnitId);
            AdNumHep.instance.updateShowNum();
            _iosAdCallback?.showSuccess.call(ad,_getAdInfoBeanById(ad.adUnitId));
          },
          onAdDisplayFailedCallback: (ad,error){
            _adShowing=false;
            _deleteAdCache(ad.adUnitId);
            loadAd(_getAdInfoBeanById(ad.adUnitId));
            _iosAdCallback?.showFail.call(ad);
          },
          onAdClickedCallback: (ad){
            AdNumHep.instance.updateClickNum();
          },
          onAdHiddenCallback: (ad){
            _adShowing=false;
            loadAd(_getAdInfoBeanById(ad.adUnitId));
            _iosAdCallback?.closeAd.call();
          },
          onAdRevenuePaidCallback: (ad){
            _iosAdCallback?.revenuePaid.call(ad,_getAdInfoBeanById(ad.adUnitId));
          },
        )
    );
  }

  showAd({
    required AdType adType,
    required IosAdCallback iosAdCallback,
  })async{
    if(_adShowing){
      "flutter ios ad --->ad showing".log();
      iosAdCallback.showFail.call(null);
      return;
    }
    if(checkFk()){
      "flutter ios ad --->fengkong not show ad".log();
      iosAdCallback.showFail.call(null);
      return;
    }
    _iosAdCallback=iosAdCallback;
    var resultData = getCacheResultData(adType);
    if(null!=resultData){
      "flutter ios ad --->start show ad --->type:$adType--->${resultData.adBean.toString()}".log();
      var newAdType = resultData.adBean.adType;
      if(newAdType==AdType.reward){
        if(await AppLovinMAX.isRewardedAdReady(resultData.adBean.adId)==true){
          AppLovinMAX.showRewardedAd(resultData.adBean.adId);
        }else{
          "flutter ios ad --->$newAdType not Ready".log();
          _deleteAdCache(resultData.adBean.adId);
          _iosAdCallback?.showFail.call(null);
          loadAd(resultData.adBean);
        }
      }else if(newAdType==AdType.interstitial){
        if(await AppLovinMAX.isInterstitialReady(resultData.adBean.adId)==true){
          AppLovinMAX.showInterstitial(resultData.adBean.adId);
        }else{
          "flutter ios ad --->$newAdType not Ready".log();
          _deleteAdCache(resultData.adBean.adId);
          _iosAdCallback?.showFail.call(null);
          loadAd(resultData.adBean);
        }
      }
    }else{
      loadAdWhenNoCache(adType);
      _iosAdCallback?.showFail.call(null);
    }
  }

  loadAd(AdInfoData? infoData){
    if(null==infoData){
      return;
    }
    _newIntLoadIosAd?.loadAdById(infoData);
    _newRvLoadIosAd?.loadAdById(infoData);
  }

  loadAdWhenNoCache(AdType adType){
    if(adType==AdType.interstitial){
      _newIntLoadIosAd?.loadAllAd();
    }else if(adType==AdType.reward){
      _newRvLoadIosAd?.loadAllAd();
    }
  }

  _deleteAdCache(String id){
    _newIntLoadIosAd?.deleteCache(id);
    _newRvLoadIosAd?.deleteCache(id);
  }

  AdInfoData? _getAdInfoBeanById(String id){
    var adBean = _newIntLoadIosAd?.getAdInfoBeanById(id);
    adBean ??= _newRvLoadIosAd?.getAdInfoBeanById(id);
    return adBean;
  }

  LoadResultData? getCacheResultData(AdType adType){
    if(adType==AdType.interstitial){
      var cashAd = _newIntLoadIosAd?.getCashAd();
      "flutter ios ad --->get int cache--->ID: ${cashAd?.adBean.adId}--->revenue:${cashAd?.revenue}".log();
      return cashAd;
    }else if(adType==AdType.reward){
      if(!_priceSwitch){
        var cashAd = _newRvLoadIosAd?.getCashAd();
        "flutter ios ad --->get rv cache--->only contrast rv--->ID: ${cashAd?.adBean.adId}--->revenue:${cashAd?.revenue}".log();
        return cashAd;
      }else{
        var list = (_newIntLoadIosAd?.getHasCacheResultList()??[])+(_newRvLoadIosAd?.getHasCacheResultList()??[]);
        if(list.isEmpty){
          "flutter ios ad --->get rv cache--->contrast rv and int--->ID: no--->revenue: no".log();
          return null;
        }
        list.sort((a, b) => (b.revenue).compareTo(a.revenue));
        var first = list.first;
        "flutter ios ad --->get rv cache--->contrast rv and int--->ID: ${first.adBean.adId}--->revenue: ${first.revenue}".log();
        return first;
      }
    }else{
      return null;
    }
  }

  updateAdData(ConfigAdData data){
    _priceSwitch=data.priceSwitch;
    _newIntLoadIosAd?.updateAdList(data.newInterList);
    _newRvLoadIosAd?.updateAdList(data.newRewardList);
  }

  bool adShowing()=>_adShowing;

  setFengkongLogic(FengKongLogic logic){
    _fengKongLogic=logic;
  }

  bool checkFk(){
    if(null==_fengKongLogic){
      return false;
    }
    return _fengKongLogic!();
  }

  setEverydayWatchAdNum(int maxShow){
    AdNumHep.instance.setMaxShowNum(maxShow);
  }
}