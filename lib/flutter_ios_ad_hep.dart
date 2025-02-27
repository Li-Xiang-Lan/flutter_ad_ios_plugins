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

class FlutterIosAdHep{
  static final FlutterIosAdHep _instance = FlutterIosAdHep();
  static FlutterIosAdHep get instance => _instance;

  LoadIosAd? _oneLoadAd;
  LoadIosAd? _twoLoadAd;
  var _adShowing=false;
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
    _setMaxAdListener();
    _oneLoadAd=LoadIosAd(oneAd: true, rewardList: data.oneRewardList, interList: data.oneInterList);
    _twoLoadAd=LoadIosAd(oneAd: false, rewardList: data.twoRewardList, interList: data.twoInterList);
  }

  _setMaxAdListener(){
    AppLovinMAX.setRewardedAdListener(
        RewardedAdListener(
          onAdLoadedCallback: (ad){
            _oneLoadAd?.loadAdSuccess(ad);
            _twoLoadAd?.loadAdSuccess(ad);
          },
          onAdLoadFailedCallback: (ad,error){
            _oneLoadAd?.loadAdFail(ad);
            _twoLoadAd?.loadAdFail(ad);
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
            loadAd(AdType.reward);
            _iosAdCallback?.showFail.call(ad);
          },
          onAdClickedCallback: (ad){
            AdNumHep.instance.updateClickNum();
          },
          onAdHiddenCallback: (ad){
            _adShowing=false;
            loadAd(AdType.reward);
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
            _oneLoadAd?.loadAdSuccess(ad);
            _twoLoadAd?.loadAdSuccess(ad);
          },
          onAdLoadFailedCallback: (ad,error){
            _oneLoadAd?.loadAdFail(ad);
            _twoLoadAd?.loadAdFail(ad);
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
            loadAd(AdType.interstitial);
            _iosAdCallback?.showFail.call(ad);
          },
          onAdClickedCallback: (ad){
            AdNumHep.instance.updateClickNum();
          },
          onAdHiddenCallback: (ad){
            _adShowing=false;
            loadAd(AdType.interstitial);
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
          loadAd(adType);
        }
      }else if(adType==AdType.interstitial){
        if(await AppLovinMAX.isInterstitialReady(resultData.adBean.adId)==true){
          AppLovinMAX.showInterstitial(resultData.adBean.adId);
        }else{
          "flutter ios ad --->$adType not Ready".log();
          _deleteAdCache(resultData.adBean.adId);
          _iosAdCallback?.showFail.call(null);
          loadAd(adType);
        }
      }
    }else{
      loadAd(adType);
      _iosAdCallback?.showFail.call(null);
    }
  }

  loadAd(AdType adType){
    _oneLoadAd?.loadAdByType(adType);
    _twoLoadAd?.loadAdByType(adType);
  }

  _deleteAdCache(String id){
    _oneLoadAd?.deleteCache(id);
    _twoLoadAd?.deleteCache(id);
  }

  AdInfoData? _getAdInfoBeanById(String id){
    var adBean = _oneLoadAd?.getAdInfoBeanById(id);
    adBean ??= _twoLoadAd?.getAdInfoBeanById(id);
    return adBean;
  }

  LoadResultData? getCacheResultData(AdType adType){
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

  updateAdData(ConfigAdData data){
    _oneLoadAd?.updateAdList(data.oneRewardList, data.oneInterList);
    _twoLoadAd?.updateAdList(data.twoRewardList, data.twoInterList);
  }
}