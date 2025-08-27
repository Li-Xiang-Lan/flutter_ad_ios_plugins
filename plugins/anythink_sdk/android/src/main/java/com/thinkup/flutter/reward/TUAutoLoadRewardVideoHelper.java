package com.thinkup.flutter.reward;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

//import com.thinkup.china.api.TUAppDownloadListener;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.core.api.TUAdStatusInfo;
import com.thinkup.core.api.TUNetworkConfirmInfo;
import com.thinkup.core.api.TUShowConfig;
import com.thinkup.core.api.TUAdConst;
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
import com.thinkup.flutter.commonlistener.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

public class TUAutoLoadRewardVideoHelper {

    Activity mActivity;
    //当前全自动加载的所有广告位ID
    public Set<String> placementIDs = Collections.synchronizedSet(new HashSet<>());

    private static TUAutoLoadRewardVideoHelper instance;

    private TUAutoLoadRewardVideoHelper() {
        mActivity = FlutterPluginUtil.getActivity();
    }

    public static TUAutoLoadRewardVideoHelper getInstance() {
        if (instance == null) {
            synchronized (TUAutoLoadRewardVideoHelper.class) {
                if (instance == null) {
                    instance = new TUAutoLoadRewardVideoHelper();
                }
            }
        }
        return instance;
    }

    public final TURewardVideoAutoLoadListener autoLoadListener = new TURewardVideoAutoLoadListener() {
        @Override
        public void onRewardVideoAutoLoaded(String placementId) {
            MsgTools.printMsg("onRewardVideoAutoLoaded: " + placementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.LoadedCallbackKey,
                    placementId, null, null);
        }

        @Override
        public void onRewardVideoAutoLoadFail(String placementId, AdError adError) {
            MsgTools.printMsg("onRewardVideoAutoLoadFail: " + placementId + ", " + adError.getFullErrorInfo());

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.LoadFailCallbackKey,
                    placementId, null, adError.getFullErrorInfo());
        }
    };

    public final TURewardVideoAutoEventListener autoEventListener = new TURewardVideoAutoEventListener() {

        @Override
        public void onRewardedVideoAdPlayStart(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdPlayStart: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.PlayStartCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        @Override
        public void onRewardedVideoAdPlayEnd(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdPlayEnd: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.PlayEndCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        @Override
        public void onRewardedVideoAdPlayFailed(AdError adError, TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdPlayFailed: " + mPlacementId + ", " + adError.getFullErrorInfo());

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.PlayFailCallbackKey,
                    mPlacementId, atAdInfo.toString(), adError.getFullErrorInfo());
        }

        @Override
        public void onRewardedVideoAdClosed(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdClosed: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.CloseCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        @Override
        public void onRewardedVideoAdPlayClicked(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdPlayClicked: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.ClickCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        @Override
        public void onReward(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onReward: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.RewardCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        @Override
        public void onRewardFailed(TUAdInfo atAdInfo) {
            //todo
        }

        public void onDeeplinkCallback(TUAdInfo atAdInfo, boolean isSuccess) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("video onDeeplinkCallback: " + mPlacementId);

            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put(Const.CallbackKey.isDeeplinkSuccess, isSuccess);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.DeeplinkCallbackKey,
                    mPlacementId, atAdInfo.toString(), null, extraMap);
        }

        public void onDownloadConfirm(Context context, TUAdInfo atAdInfo, TUNetworkConfirmInfo networkConfirmInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("video onDownloadConfirm: " + mPlacementId);
        }

        //again listener
        public void onRewardedVideoAdAgainPlayStart(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdAgainPlayStart: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainPlayStartCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        public void onRewardedVideoAdAgainPlayEnd(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdAgainPlayEnd: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainPlayEndCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        public void onRewardedVideoAdAgainPlayFailed(AdError adError, TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdAgainPlayFailed: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainPlayFailCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        public void onRewardedVideoAdAgainPlayClicked(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onRewardedVideoAdAgainPlayClicked: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainClickCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        public void onAgainReward(TUAdInfo atAdInfo) {
            String mPlacementId = atAdInfo.getPlacementId();
            MsgTools.printMsg("onAgainReward: " + mPlacementId);

            TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                    Const.CallbackMethodCall.rewardedVideoCall, Const.RewardVideoCallback.AgainRewardCallbackKey,
                    mPlacementId, atAdInfo.toString(), null);
        }

        @Override
        public void onAgainRewardFailed(TUAdInfo atAdInfo) {
            //todo
        }
    };

    // 检查给定的placementID是否在集合中
    public boolean containsPlacementID(String placementID) {
        return placementIDs.contains(placementID);
    }

    public void autoLoadRewardedVideo(String[] placementIds) {
        if (placementIds == null && placementIds.length <= 0) {
            MsgTools.printMsg("autoLoadRewardedVideo autoLoadRewardedVideo: The placementIds parameter is null or empty.");
            return;
        }
        for (String id : placementIds) {
            if (!TextUtils.isEmpty(id)) { // 检查元素既不是null也不是空字符串
                MsgTools.printMsg("autoLoadRewardedVideo add: " + id);
                placementIDs.add(id);
            }
        }
        TURewardVideoAutoAd.init(mActivity, placementIds, autoLoadListener);
        TURewardVideoAutoAd.addPlacementId(placementIds);
    }

    public void removePlacementId(String[] placementIds) {
        if (placementIds == null && placementIds.length <= 0) {
            // Handle the case where placementIds parameter is null or empty.
            MsgTools.printMsg("autoLoadRewardedVideo removePlacementId: The placementIds parameter is null or empty.");
            return;
        }
        for (String id : placementIds) {
            if (!TextUtils.isEmpty(id)) { // 检查元素既不是null也不是空字符串
                MsgTools.printMsg("autoLoadRewardedVideo remove: " + id);
                placementIDs.remove(id);
            }
        }
        TURewardVideoAutoAd.removePlacementId(placementIds);
    }

    public void showAutoLoadRewardedVideoAD(final String placementId, final String scenario) {
        String scenceID = scenario;
        if (!TextUtils.isEmpty(scenario)) {
            scenceID = "";
        }
        TUShowConfig.Builder builder = new TUShowConfig.Builder();
        builder.scenarioId(scenceID);
        builder.showCustomExt(null);
        TURewardVideoAutoAd.show(mActivity, placementId, builder.build(), autoEventListener, new AdRevenueListenerImpl(placementId));
    }

    public void autoLoadRewardedVideoSetLocalExtra(final String placementId,final Map<String, Object> settings) {
        String userId = "";
        String userData = "";
        try {
            if (settings.containsKey(Const.RewardedVideo.USER_ID)) {
                userId = settings.get(Const.RewardedVideo.USER_ID).toString();
                settings.remove(Const.RewardedVideo.USER_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (settings.containsKey(Const.RewardedVideo.USER_DTUA)) {
                userData = settings.get(Const.RewardedVideo.USER_DTUA).toString();
                settings.remove(Const.RewardedVideo.USER_DTUA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MsgTools.printMsg("autoLoadRewardedVideoSetLocalExtra: " + placementId + ", userId: " + userId + ", userData: " + userData);

        settings.put(TUAdConst.KEY.USER_ID, userId);
        settings.put(TUAdConst.KEY.USER_CUSTOM_DATA, userData);

        TURewardVideoAutoAd.setLocalExtra(placementId, settings);
    }

    public boolean isAdReady(String placementId) {
        boolean isReady = false;
        if (TextUtils.isEmpty(placementId)) {
            return isReady;
        }
        isReady = TURewardVideoAutoAd.isAdReady(placementId);
        MsgTools.printMsg("auto load video isAdReady: " + placementId + ", " + isReady);
        return isReady;
    }

    public Map<String, Object> checkAdStatus(String mPlacementId) {
        MsgTools.printMsg("auto load video checkAdStatus: " + mPlacementId);

        Map<String, Object> map = new HashMap<>(5);

        if (TextUtils.isEmpty(mPlacementId)) {
            map.put("isLoading", false);
            map.put("isReady", false);

            return map;
        }

        TUAdStatusInfo atAdStatusInfo = TURewardVideoAutoAd.checkAdStatus(mPlacementId);
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

    public String checkValidAdCaches(String mPlacementId) {
        MsgTools.printMsg("auto load video checkValidAdCaches: " + mPlacementId);

        if (TextUtils.isEmpty(mPlacementId)) {
            return "";
        }

        List<TUAdInfo> vaildAds = TURewardVideoAutoAd.checkValidAdCaches(mPlacementId);
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

    public void entryScenario(final String placementId,final String sceneID) {
        if (TextUtils.isEmpty(placementId)) {
            MsgTools.printMsg("auto load video entryRewardVideoScenario empty placementId");
            return;
        }
        MsgTools.printMsg("auto load video entryRewardVideoScenario: " + placementId + "sceneID: " + sceneID);
        TURewardVideoAutoAd.entryAdScenario(placementId, sceneID);
    }
}



