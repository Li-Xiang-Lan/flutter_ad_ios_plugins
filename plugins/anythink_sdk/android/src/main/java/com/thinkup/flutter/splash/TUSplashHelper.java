package com.thinkup.flutter.splash;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.thinkup.core.api.TUAdInfo;
import com.thinkup.core.api.TUAdStatusInfo;
import com.thinkup.core.api.TUNetworkConfirmInfo;
import com.thinkup.core.api.AdError;
import com.thinkup.core.api.TUShowConfig;
import com.thinkup.flutter.TUFlutterEventManager;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.FlutterPluginUtil;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.splashad.api.TUSplashAd;
import com.thinkup.splashad.api.TUSplashAdExtraInfo;
import com.thinkup.splashad.api.TUSplashExListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TUSplashHelper {

    Activity mActivity;
    TUSplashAd mSplashAd;
    String mPlacementId;
    ViewGroup mDecorView;
    ViewGroup mAdContainer;

    public TUSplashHelper() {
        mActivity = FlutterPluginUtil.getActivity();
    }

    private void initSplash(final String placementId, int fetchAdTimeout) {
        mPlacementId = placementId;
        TUSplashExListener splashExListener = new TUSplashExListener() {
            @Override
            public void onDeeplinkCallback(TUAdInfo atAdInfo, boolean isSuccess) {
                MsgTools.printMsg("splash onDeeplinkCallback: " + mPlacementId + ", isSuccess: " + isSuccess);

                Map<String, Object> extraMap = new HashMap<>();
                extraMap.put(Const.CallbackKey.isDeeplinkSuccess, isSuccess);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.DeeplinkCallbackKey,
                        mPlacementId, atAdInfo.toString(), null, extraMap);
            }

            @Override
            public void onDownloadConfirm(Context context, TUAdInfo atAdInfo, TUNetworkConfirmInfo atNetworkConfirmInfo) {
                MsgTools.printMsg("splash onDownloadConfirm: " + mPlacementId);
            }

            @Override
            public void onAdLoaded(boolean isTimeout) {
                MsgTools.printMsg("onAdLoaded: " + mPlacementId + ", isTimeout: " + isTimeout);

                Map<String, Object> extraMap = new HashMap<>();
                extraMap.put(Const.CallbackKey.isTimeout, isTimeout);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.LoadedCallbackKey,
                        mPlacementId, null, null, extraMap);
            }

            @Override
            public void onAdLoadTimeout() {
                MsgTools.printMsg("onAdLoadTimeout: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.TimeoutCallbackKey,
                        mPlacementId, null, null);
            }

            @Override
            public void onNoAdError(AdError adError) {
                MsgTools.printMsg("onNoAdError: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.LoadFailCallbackKey,
                        mPlacementId, null, adError.getFullErrorInfo());
            }

            @Override
            public void onAdShow(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onAdShow: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.ShowCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onAdClick(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onAdClick: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.ClickCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onAdDismiss(TUAdInfo atAdInfo, TUSplashAdExtraInfo atSplashAdExtraInfo) {
                MsgTools.printMsg("onAdDismiss: " + mPlacementId);

                if (mDecorView != null && mAdContainer != null) {
                    mAdContainer.removeAllViews();
                    mDecorView.removeView(mAdContainer);
                }


                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.CloseCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }
        };

        if (fetchAdTimeout > 0) {
            mSplashAd = new TUSplashAd(mActivity, placementId, splashExListener, fetchAdTimeout);
        } else {
            mSplashAd = new TUSplashAd(mActivity, placementId, splashExListener);
        }

        //download
//        try {
//            if (TUSDK.isCnSDK()) {
//                mSplashAd.setAdDownloadListener(new TUAppDownloadListener() {
//                    @Override
//                    public void onDownloadStart(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("splash onDownloadStart: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadStartKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadUpdate(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("splash onDownloadUpdate: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadUpdateKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadPause(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("splash onDownloadPause: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadPauseKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFinish(TUAdInfo atAdInfo, long totalBytes, String fileName, String appName) {
//                        MsgTools.printMsg("splash onDownloadFinish: " + mPlacementId + ", " + totalBytes  + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFinishedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, -1, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFail(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("splash onDownloadFail: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFailedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onInstalled(TUAdInfo atAdInfo, String fileName, String appName) {
//                        MsgTools.printMsg("splash onInstalled: " + mPlacementId + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadInstalledKey,
//                                mPlacementId, atAdInfo.toString(), -1, -1, fileName, appName);
//                    }
//                });
//            }
//        } catch (Throwable e) {
//        }

    }

    public void loadSplash(final String placementId, final Map<String, Object> settings) {
        MsgTools.printMsg("loadSplash: " + placementId + ", settings: " + settings);


        int fetchAdTimeout = -1;
        if (settings != null) {
            try {
                fetchAdTimeout = Integer.parseInt(settings.get(Const.Splash.tolerateTimeout).toString());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (mSplashAd == null) {
            initSplash(placementId, fetchAdTimeout);
        }


        //todo
//        int adViewWidth;
//        int adViewHeight;
//        if (settings != null) {
//            try {
//                Map<String, Object> nativeAdSize = (Map<String, Object>) settings.get(Const.SIZE);
//
//                adViewWidth = Utils.dip2px(mActivity, (double) nativeAdSize.get(Const.WIDTH));
//                adViewHeight = Utils.dip2px(mActivity, (double) nativeAdSize.get(Const.HEIGHT));
//
//                MsgTools.printMsg("loadSplash: " + placementId + ", width: " + adViewWidth + ", height: " + adViewHeight);
//
//                settings.put(TUAdConst.KEY.AD_WIDTH, adViewWidth);
//                settings.put(TUAdConst.KEY.AD_HEIGHT, adViewHeight);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }


        mSplashAd.setLocalExtra(settings);
        mSplashAd.loadAd();
    }

    public void showSplash(final String scenario) {
        MsgTools.printMsg("showSplash: " + mPlacementId + ", scenario: " + scenario);
        showConfigSplash(scenario,null);
    }

    public void showConfigSplash(final String scenario, final String showCustomExt) {
        MsgTools.printMsg("showConfigSplash: " + mPlacementId + ", scenario: " + scenario+ ", showCustomExt: " + showCustomExt);

        if (mSplashAd != null) {
            try {
                mDecorView = mActivity.findViewById(android.R.id.content);
                if (mAdContainer == null) {
                    mAdContainer = new FrameLayout(mActivity);
                    mAdContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                mAdContainer.removeAllViews();
                if (mDecorView != null) {
                    mDecorView.removeView(mAdContainer);
                    mDecorView.addView(mAdContainer);
                }
                TUShowConfig.Builder builder = new TUShowConfig.Builder();
                builder.scenarioId(scenario);
                builder.showCustomExt(showCustomExt);
                mSplashAd.show(mActivity, mAdContainer, null, builder.build());
            } catch (Exception e) {
                MsgTools.printMsg("Splash showAd failed: " + e.getMessage());
                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.SplashCall, Const.SplashCallback.ShowFailedCallbackKey,
                        mPlacementId, null, e.getMessage());
            }
        }
    }

    public boolean isAdReady() {
        MsgTools.printMsg("splash isAdReady: " + mPlacementId);

        boolean isReady = false;
        if (mSplashAd != null) {
            isReady = mSplashAd.isAdReady();
        }

        MsgTools.printMsg("splash isAdReady: " + mPlacementId + ", " + isReady);
        return isReady;
    }

    public Map<String, Object> checkAdStatus() {
        MsgTools.printMsg("splash checkAdStatus: " + mPlacementId);

        Map<String, Object> map = new HashMap<>(5);

        if (mSplashAd != null) {
            TUAdStatusInfo atAdStatusInfo = mSplashAd.checkAdStatus();
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
        MsgTools.printMsg("splash checkValidAdCaches: " + mPlacementId);

        if (mSplashAd != null) {
            List<TUAdInfo> vaildAds = mSplashAd.checkValidAdCaches();
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
        MsgTools.printMsg("entrySplashScenario: " + mPlacementId + "sceneID: " + sceneID);
        TUSplashAd.entryAdScenario(placementId, sceneID);
    }
}
