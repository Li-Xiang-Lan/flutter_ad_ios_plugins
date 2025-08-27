package com.thinkup.flutter.reward;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

//import com.thinkup.china.api.TUAppDownloadListener;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.core.api.TUAdStatusInfo;
import com.thinkup.core.api.TUNetworkConfirmInfo;
import com.thinkup.core.api.TUShowConfig;
//import com.thinkup.core.api.TUSDK;
import com.thinkup.core.api.AdError;
import com.thinkup.flutter.TUFlutterEventManager;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.FlutterPluginUtil;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.flutter.commonlistener.AdRevenueListenerImpl;

import com.thinkup.rewardvideo.api.TURewardVideoAd;
import com.thinkup.rewardvideo.api.TURewardVideoExListener;
import com.thinkup.rewardvideo.api.TURewardVideoAutoAd;
import com.thinkup.rewardvideo.api.TURewardVideoAutoEventListener;
import com.thinkup.rewardvideo.api.TURewardVideoAutoLoadListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TURewardVideoHelper {

    Activity mActivity;
    TURewardVideoAd mRewardVideoAd;
    String mPlacementId;

    public TURewardVideoHelper() {
        mActivity = FlutterPluginUtil.getActivity();
    }

    private void initRewardVideo(final String placementId) {
        mPlacementId = placementId;
        mRewardVideoAd = new TURewardVideoAd(mActivity, placementId);
        mRewardVideoAd.setAdListener(new TURewardVideoExListener() {
            @Override
            public void onRewardFailed(TUAdInfo atAdInfo) {
                //todo
            }

            @Override
            public void onDeeplinkCallback(TUAdInfo atAdInfo, boolean isSuccess) {
                MsgTools.printMsg("video onDeeplinkCallback: " + mPlacementId);

                Map<String, Object> extraMap = new HashMap<>();
                extraMap.put(Const.CallbackKey.isDeeplinkSuccess, isSuccess);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.DeeplinkCallbackKey,
                        mPlacementId, atAdInfo.toString(), null, extraMap);
            }

            @Override
            public void onDownloadConfirm(Context context, TUAdInfo atAdInfo, TUNetworkConfirmInfo atNetworkConfirmInfo) {
                MsgTools.printMsg("video onDownloadConfirm: " + mPlacementId);
            }

            @Override
            public void onRewardedVideoAdAgainPlayStart(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdAgainPlayStart: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainPlayStartCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onRewardedVideoAdAgainPlayEnd(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdAgainPlayEnd: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainPlayEndCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onRewardedVideoAdAgainPlayFailed(AdError adError, TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdAgainPlayFailed: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainPlayFailCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onRewardedVideoAdAgainPlayClicked(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdAgainPlayClicked: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainClickCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onAgainReward(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onAgainReward: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainRewardCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onAgainRewardFailed(TUAdInfo atAdInfo) {
                //todo
            }


            @Override
            public void onRewardedVideoAdLoaded() {
                MsgTools.printMsg("onRewardedVideoAdLoaded: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.LoadedCallbackKey,
                        mPlacementId, null, null);
            }

            @Override
            public void onRewardedVideoAdFailed(AdError adError) {
                MsgTools.printMsg("onRewardedVideoAdFailed: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.LoadFailCallbackKey,
                        mPlacementId, null, adError.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdPlayStart(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdPlayStart: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.PlayStartCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onRewardedVideoAdPlayEnd(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdPlayEnd: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.PlayEndCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError adError, TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdPlayFailed: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.PlayFailCallbackKey,
                        mPlacementId, atAdInfo.toString(), adError.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdClosed(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdClosed: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.CloseCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onRewardedVideoAdPlayClicked(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onRewardedVideoAdPlayClicked: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.ClickCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onReward(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onReward: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.RewardCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }
        });

        //download
//        try {
//            if (TUSDK.isCnSDK()) {
//                mRewardVideoAd.setAdDownloadListener(new TUAppDownloadListener() {
//                    @Override
//                    public void onDownloadStart(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("video onDownloadStart: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadStartKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadUpdate(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("video onDownloadUpdate: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadUpdateKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadPause(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("video onDownloadPause: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadPauseKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFinish(TUAdInfo atAdInfo, long totalBytes, String fileName, String appName) {
//                        MsgTools.printMsg("video onDownloadFinish: " + mPlacementId + ", " + totalBytes  + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFinishedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, -1, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFail(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("video onDownloadFail: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFailedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onInstalled(TUAdInfo atAdInfo, String fileName, String appName) {
//                        MsgTools.printMsg("video onInstalled: " + mPlacementId + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadInstalledKey,
//                                mPlacementId, atAdInfo.toString(), -1, -1, fileName, appName);
//                    }
//                });
//            }
//        } catch (Throwable e) {
//        }

    }

    public void loadRewardedVideo(final String placementId, final Map<String, Object> settings) {
        MsgTools.printMsg("loadRewardedVideo: " + placementId + ", settings: " + settings);

        if (mRewardVideoAd == null) {
            initRewardVideo(placementId);
        }

        String userId = "";
        String userData = "";
        try {
            if (settings.containsKey(Const.RewardedVideo.USER_ID)) {
                userId = settings.get(Const.RewardedVideo.USER_ID).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (settings.containsKey(Const.RewardedVideo.USER_DTUA)) {
                userData = settings.get(Const.RewardedVideo.USER_DTUA).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MsgTools.printMsg("loadRewardedVideo: " + placementId + ", userId: " + userId + ", userData: " + userData);

        settings.put("user_id", userId);
        settings.put("user_custom_data", userData);


        mRewardVideoAd.setLocalExtra(settings);
        mRewardVideoAd.load();
    }

    public void showRewardedVideo(final String scenario) {
        MsgTools.printMsg("showRewardedVideo: " + mPlacementId + ", scenario: " + scenario);

        if (mRewardVideoAd != null) {
            if (!TextUtils.isEmpty(scenario)) {
                mRewardVideoAd.show(mActivity, scenario);
            } else {
                mRewardVideoAd.show(mActivity);
            }
        }
    }

    public void showConfigRewardedVideo(final String scenario, final String showCustomExt) {
        MsgTools.printMsg("showConfigRewardedVideo: " + mPlacementId + ", scenario: " + scenario + ", customShowExt: " + showCustomExt);

        if (mRewardVideoAd != null) {
            TUShowConfig.Builder builder = new TUShowConfig.Builder();

            if (!TextUtils.isEmpty(scenario)) {
                builder.scenarioId(scenario);
            }
            if (!TextUtils.isEmpty(showCustomExt)) {
                builder.showCustomExt(showCustomExt);
            }
            mRewardVideoAd.show(mActivity, builder.build());
        }
    }

    public boolean isAdReady() {
        MsgTools.printMsg("video isAdReady: " + mPlacementId);

        boolean isReady = false;
        if (mRewardVideoAd != null) {
            isReady = mRewardVideoAd.isAdReady();
        }

        MsgTools.printMsg("video isAdReady: " + mPlacementId + ", " + isReady);
        return isReady;
    }

    public Map<String, Object> checkAdStatus() {
        MsgTools.printMsg("video checkAdStatus: " + mPlacementId);

        Map<String, Object> map = new HashMap<>(5);

        if (mRewardVideoAd != null) {
            TUAdStatusInfo atAdStatusInfo = mRewardVideoAd.checkAdStatus();
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
        MsgTools.printMsg("video checkValidAdCaches: " + mPlacementId);

        if (mRewardVideoAd != null) {
            List<TUAdInfo> vaildAds = mRewardVideoAd.checkValidAdCaches();
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
        MsgTools.printMsg("entryRewardVideoScenario: " + mPlacementId + "sceneID: " + sceneID);
        TURewardVideoAd.entryAdScenario(placementId, sceneID);
    }
}
