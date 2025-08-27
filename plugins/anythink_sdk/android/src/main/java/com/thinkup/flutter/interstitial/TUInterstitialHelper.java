package com.thinkup.flutter.interstitial;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

//import com.thinkup.china.api.TUAppDownloadListener;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.core.api.TUAdStatusInfo;
import com.thinkup.core.api.TUNetworkConfirmInfo;
//import com.thinkup.core.api.TUSDK;
import com.thinkup.core.api.AdError;
import com.thinkup.core.api.TUShowConfig;

import com.thinkup.flutter.TUFlutterEventManager;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.FlutterPluginUtil;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.flutter.utils.Utils;
import com.thinkup.interstitial.api.TUInterstitial;
import com.thinkup.interstitial.api.TUInterstitialExListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TUInterstitialHelper {

    Activity mActivity;
    TUInterstitial mInterstitialAd;
    String mPlacementId;

    public TUInterstitialHelper() {
        mActivity = FlutterPluginUtil.getActivity();
    }

    private void initInterstitial(final String placementId) {
        mPlacementId = placementId;
        mInterstitialAd = new TUInterstitial(mActivity, placementId);
        mInterstitialAd.setAdListener(new TUInterstitialExListener() {
            @Override
            public void onDeeplinkCallback(TUAdInfo atAdInfo, boolean isSuccess) {
                MsgTools.printMsg("interstitial onDeeplinkCallback: " + mPlacementId);

                Map<String, Object> extraMap = new HashMap<>();
                extraMap.put(Const.CallbackKey.isDeeplinkSuccess, isSuccess);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.DeeplinkCallbackKey,
                        mPlacementId, atAdInfo.toString(), null, extraMap);
            }

            @Override
            public void onDownloadConfirm(Context context, TUAdInfo atAdInfo, TUNetworkConfirmInfo atNetworkConfirmInfo) {
                MsgTools.printMsg("interstitial onDownloadConfirm: " + mPlacementId);
            }

            @Override
            public void onInterstitialAdLoaded() {
                MsgTools.printMsg("onInterstitialAdLoaded: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.LoadedCallbackKey,
                        mPlacementId, null, null);
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                MsgTools.printMsg("onInterstitialAdLoadFail: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.LoadFailCallbackKey,
                        mPlacementId, null, adError.getFullErrorInfo());
            }

            @Override
            public void onInterstitialAdClicked(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onInterstitialAdClicked: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.ClickCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onInterstitialAdShow(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onInterstitialAdShow: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.ShowCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onInterstitialAdClose(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onInterstitialAdClose: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.CloseCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onInterstitialAdVideoStart(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onInterstitialAdVideoStart: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.PlayStartCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onInterstitialAdVideoEnd(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onInterstitialAdVideoEnd: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.PlayEndCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                MsgTools.printMsg("onInterstitialAdVideoError: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.InterstitialCall, Const.InterstitialCallback.PlayFailCallbackKey,
                        mPlacementId, null, adError.getFullErrorInfo());
            }
        });

        //download
//        try {
//            if (TUSDK.isCnSDK()) {
//                mInterstitialAd.setAdDownloadListener(new TUAppDownloadListener() {
//                    @Override
//                    public void onDownloadStart(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("interstitial onDownloadStart: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadStartKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadUpdate(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("interstitial onDownloadUpdate: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadUpdateKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadPause(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("interstitial onDownloadPause: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadPauseKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFinish(TUAdInfo atAdInfo, long totalBytes, String fileName, String appName) {
//                        MsgTools.printMsg("interstitial onDownloadFinish: " + mPlacementId + ", " + totalBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFinishedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, -1, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFail(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("interstitial onDownloadFail: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFailedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onInstalled(TUAdInfo atAdInfo, String fileName, String appName) {
//                        MsgTools.printMsg("interstitial onInstalled: " + mPlacementId + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadInstalledKey,
//                                mPlacementId, atAdInfo.toString(), -1, -1, fileName, appName);
//                    }
//                });
//            }
//        } catch (Throwable e) {
//        }
    }

    public void loadInterstitial(final String placementId, final Map<String, Object> settings) {
        MsgTools.printMsg("loadInterstitial: " + placementId + ", settings: " + settings);

        if (mInterstitialAd == null) {
            initInterstitial(placementId);
        }

        if (settings != null) {

            try {
                if (settings.containsKey(Const.Interstitial.UseRewardedVideoAsInterstitialKey)) {
                    if ((boolean) settings.get(Const.Interstitial.UseRewardedVideoAsInterstitialKey)) {

                        MsgTools.printMsg("loadInterstitial: " + placementId + ", is_use_rewarded_video_as_interstitial: " + true);
                        settings.put("is_use_rewarded_video_as_interstitial", true);
                    }
                }
            } catch (Throwable e) {
            }

            try {
                Map<String, Object> map = (Map<String, Object>) settings.get(Const.SIZE);
                if (map != null) {
                    int width = Utils.dip2px(mActivity, Double.parseDouble(map.get(Const.WIDTH).toString()));
                    int height = Utils.dip2px(mActivity, Double.parseDouble(map.get(Const.HEIGHT).toString()));

                    MsgTools.printMsg("loadInterstitial: " + placementId + ", width: " + width + ", height: " + height);

                    settings.put("key_width", width);
                    settings.put("key_height", height);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        mInterstitialAd.setLocalExtra(settings);
        mInterstitialAd.load();
    }

    public void showInterstitialAd(final String scenario) {
        MsgTools.printMsg("showInterstitialAd: " + mPlacementId + ", scenario: " + scenario);

        if (mInterstitialAd != null) {
            if (!TextUtils.isEmpty(scenario)) {
                mInterstitialAd.show(mActivity, scenario);
            } else {
                mInterstitialAd.show(mActivity);
            }
        }
    }

    public void showConfigInterstitialAd(final String scenario, final String showCustomExt) {
        MsgTools.printMsg(" == showConfigInterstitialAd: " + mPlacementId + ", scenario: " + scenario + ", customShowExt: " + showCustomExt);

        if (mInterstitialAd != null) {

            TUShowConfig.Builder builder = new TUShowConfig.Builder();
            if (!TextUtils.isEmpty(scenario)) {
                builder.scenarioId(scenario);
            }
            if (!TextUtils.isEmpty(showCustomExt)) {
                builder.showCustomExt(showCustomExt);
            }
            mInterstitialAd.show(mActivity, builder.build());
        }
    }

    public boolean isAdReady() {
        MsgTools.printMsg("interstitial isAdReady: " + mPlacementId);

        boolean isReady = false;
        if (mInterstitialAd != null) {
            isReady = mInterstitialAd.isAdReady();
        }

        MsgTools.printMsg("interstitial isAdReady: " + mPlacementId + ", " + isReady);
        return isReady;
    }

    public Map<String, Object> checkAdStatus() {
        MsgTools.printMsg("interstitial checkAdStatus: " + mPlacementId);

        Map<String, Object> map = new HashMap<>(5);

        if (mInterstitialAd != null) {
            TUAdStatusInfo atAdStatusInfo = mInterstitialAd.checkAdStatus();
            boolean loading = atAdStatusInfo.isLoading();
            boolean ready = atAdStatusInfo.isReady();
            TUAdInfo atTopAdInfo = atAdStatusInfo.getTUTopAdInfo();

            map.put("isLoading", loading);
            map.put("isReady", ready);

            if (atTopAdInfo != null) {
                map.put("adInfo", atTopAdInfo.toString());
            }

            return map;
        }

        map.put("isLoading", false);
        map.put("isReady", false);

        return map;
    }

    public String checkValidAdCaches() {
        MsgTools.printMsg("interstitial checkValidAdCaches: " + mPlacementId);

        if (mInterstitialAd != null) {
            List<TUAdInfo> vaildAds = mInterstitialAd.checkValidAdCaches();
            if (vaildAds == null) {
                return "";
            }

            JSONArray jsonArray = new JSONArray();

            int size = vaildAds.size();
            for (int i = 0; i < size; i++) {
                try {
                    jsonArray.put(new JSONObject(vaildAds.get(i).toString()));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return jsonArray.toString();
        }
        return "";
    }

    public void entryScenario(final String placementId,final String sceneID) {
        MsgTools.printMsg("entryInterstitialScenario: " + mPlacementId + "sceneID: " + sceneID);
        TUInterstitial.entryAdScenario(placementId, sceneID);
    }
}
