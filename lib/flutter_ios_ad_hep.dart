import 'package:applovin_max/applovin_max.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_ad_ios_plugins/data/ad_info_data.dart';
import 'package:flutter_ad_ios_plugins/data/config_ad_data.dart';
import 'package:flutter_ad_ios_plugins/data/load_result_data.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_num_hep.dart';
import 'package:flutter_ad_ios_plugins/hep/ad_type.dart';
import 'package:flutter_ad_ios_plugins/hep/hep.dart';
import 'package:flutter_ad_ios_plugins/hep/ios_ad_callback.dart';
import 'package:flutter_ad_ios_plugins/load/load_ios_ad.dart';
import 'package:flutter_ad_ios_plugins/load/new_load_ios_ad.dart';

class FlutterIosAdHep{
  static final FlutterIosAdHep _instance = FlutterIosAdHep();
  static FlutterIosAdHep get instance => _instance;

  LoadIosAd? _oneLoadAd;
  LoadIosAd? _twoLoadAd;
  //新方案加载插屏和激励
  NewLoadIosAd? _newIntLoadIosAd;
  NewLoadIosAd? _newRvLoadIosAd;
  var _adShowing=false,_isNewPlan=false;
  IosAdCallback? _iosAdCallback;

  initMax({
    required String maxKey,
    required ConfigAdData data,
    bool showMediationDebugger=false,
  })async{
    await AppLovinMAX.initialize(maxKey);
    if(kDebugMode&&showMediationDebugger){
      AppLovinMAX.showMediationDebugger();
    }
    _isNewPlan=data.isNewPlan;
    _setMaxAdListener();
    if(_isNewPlan){
      _newIntLoadIosAd=NewLoadIosAd(interAd: true, adInfoList: data.newInterList);
      _newRvLoadIosAd=NewLoadIosAd(interAd: false, adInfoList: data.newRewardList);
    }else{
      _oneLoadAd=LoadIosAd(oneAd: true, rewardList: data.oneRewardList, interList: data.oneInterList);
      _twoLoadAd=LoadIosAd(oneAd: false, rewardList: data.twoRewardList, interList: data.twoInterList);
    }
  }

  _setMaxAdListener(){
    AppLovinMAX.setRewardedAdListener(
        RewardedAdListener(
          onAdLoadedCallback: (ad){
            if(_isNewPlan){
              _newIntLoadIosAd?.loadAdSuccess(ad);
              _newRvLoadIosAd?.loadAdSuccess(ad);
            }else{
              _oneLoadAd?.loadAdSuccess(ad);
              _twoLoadAd?.loadAdSuccess(ad);
            }
          },
          onAdLoadFailedCallback: (ad,error){
            if(_isNewPlan){
              _newIntLoadIosAd?.loadAdFail(ad);
              _newRvLoadIosAd?.loadAdFail(ad);
            }else{
              _oneLoadAd?.loadAdFail(ad);
              _twoLoadAd?.loadAdFail(ad);
            }
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
            _iosAdCallback?.onAdRevenuePaidCallback.call(ad,_getAdInfoBeanById(ad.adUnitId));
          },
        )
    );

    AppLovinMAX.setInterstitialListener(
        InterstitialListener(
          onAdLoadedCallback: (ad){
            if(_isNewPlan){
              _newIntLoadIosAd?.loadAdSuccess(ad);
              _newRvLoadIosAd?.loadAdSuccess(ad);
            }else{
              _oneLoadAd?.loadAdSuccess(ad);
              _twoLoadAd?.loadAdSuccess(ad);
            }
          },
          onAdLoadFailedCallback: (ad,error){
            if(_isNewPlan){
              _newIntLoadIosAd?.loadAdFail(ad);
              _newRvLoadIosAd?.loadAdFail(ad);
            }else{
              _oneLoadAd?.loadAdFail(ad);
              _twoLoadAd?.loadAdFail(ad);
            }
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
            _iosAdCallback?.onAdRevenuePaidCallback.call(ad,_getAdInfoBeanById(ad.adUnitId));
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
    _iosAdCallback=iosAdCallback;
    var resultData = getCacheResultData(adType);
    if(null!=resultData){
      "flutter ios ad --->start show ad --->type:$adType--->${resultData.adBean.toString()}".log();
      if(adType==AdType.reward){
        if(await AppLovinMAX.isRewardedAdReady(resultData.adBean.adId)==true){
          AppLovinMAX.showRewardedAd(resultData.adBean.adId);
        }else{
          "flutter ios ad --->$adType not Ready".log();
          _deleteAdCache(resultData.adBean.adId);
          _iosAdCallback?.showFail.call(null);
          loadAd(resultData.adBean);
        }
      }else if(adType==AdType.interstitial){
        if(await AppLovinMAX.isInterstitialReady(resultData.adBean.adId)==true){
          AppLovinMAX.showInterstitial(resultData.adBean.adId);
        }else{
          "flutter ios ad --->$adType not Ready".log();
          _deleteAdCache(resultData.adBean.adId);
          _iosAdCallback?.showFail.call(null);
          loadAd(resultData.adBean);
        }
      }
    }else{
      if(_isNewPlan){
        if(adType==AdType.interstitial){
          _newIntLoadIosAd?.loadAllAd();
        }else if(adType==AdType.reward){
          _newRvLoadIosAd?.loadAllAd();
        }
      }else{
        _oneLoadAd?.loadAdByType(adType);
        _twoLoadAd?.loadAdByType(adType);
      }
      _iosAdCallback?.showFail.call(null);
    }
  }

  loadAd(AdInfoData? infoData){
    if(null==infoData){
      return;
    }
    if(_isNewPlan){
      _newIntLoadIosAd?.loadAdById(infoData);
      _newRvLoadIosAd?.loadAdById(infoData);
    }else{
      _oneLoadAd?.loadAdByType(infoData.adType);
      _twoLoadAd?.loadAdByType(infoData.adType);
    }
  }

  _deleteAdCache(String id){
    if(_isNewPlan){
      _newIntLoadIosAd?.deleteCache(id);
      _newRvLoadIosAd?.deleteCache(id);
    }else{
      _oneLoadAd?.deleteCache(id);
      _twoLoadAd?.deleteCache(id);
    }
  }

  AdInfoData? _getAdInfoBeanById(String id){
    if(_isNewPlan){
      var adBean = _newIntLoadIosAd?.getAdInfoBeanById(id);
      adBean ??= _newRvLoadIosAd?.getAdInfoBeanById(id);
      return adBean;
    }else{
      var adBean = _oneLoadAd?.getAdInfoBeanById(id);
      adBean ??= _twoLoadAd?.getAdInfoBeanById(id);
      return adBean;
    }
  }

  LoadResultData? getCacheResultData(AdType adType){
    if(_isNewPlan){
      if(adType==AdType.interstitial){
        return _newIntLoadIosAd?.getCashAd();
      }else if(adType==AdType.reward){
        return _newRvLoadIosAd?.getCashAd();
      }else{
        return null;
      }
    }else{
      var oneResult = _oneLoadAd?.getCacheAd(adType);
      if(null!=oneResult){
        return oneResult;
      }
      var twoResult = _twoLoadAd?.getCacheAd(adType);
      if(null!=twoResult){
        return twoResult;
      }
      return null;
    }
  }

  updateAdData(ConfigAdData data){
    _isNewPlan=data.isNewPlan;
    if(_isNewPlan){

    }else{
      _oneLoadAd?.updateAdList(data.oneRewardList, data.oneInterList);
      _twoLoadAd?.updateAdList(data.twoRewardList, data.twoInterList);
    }
  }

  bool adShowing()=>_adShowing;
}