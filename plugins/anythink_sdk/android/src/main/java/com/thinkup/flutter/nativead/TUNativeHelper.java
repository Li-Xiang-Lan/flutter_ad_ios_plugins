package com.thinkup.flutter.nativead;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.thinkup.core.api.TUAdConst;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.core.api.TUAdStatusInfo;
import com.thinkup.core.api.AdError;
import com.thinkup.flutter.TUFlutterEventManager;
import com.thinkup.flutter.ThinkupSdkPlugin;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.FlutterPluginUtil;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.flutter.utils.Utils;
import com.thinkup.flutter.view.RoundTUNativeView;
import com.thinkup.nativead.api.TUNative;
import com.thinkup.nativead.api.TUNativeAdView;
import com.thinkup.nativead.api.TUNativeDislikeListener;
import com.thinkup.nativead.api.TUNativeEventExListener;
import com.thinkup.nativead.api.TUNativeNetworkListener;
import com.thinkup.nativead.api.TUNativePrepareExInfo;
import com.thinkup.nativead.api.TUNativePrepareInfo;
import com.thinkup.nativead.api.NativeAd;
import com.thinkup.core.api.TUShowConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TUNativeHelper extends ThinkupSdkPlugin {

    String mPlacementId;

    TUNative mTUNative;
    RoundTUNativeView atNativeAdView;//Only for native api(Non-Platform View)
    NativeAd mNativeAd;

    int adViewWidth;
    int adViewHeight;


    ViewInfo pViewInfo;
    ImageView mDislikeView;

    Activity mActivity;

    TUNativePrepareInfo mNativePrepareInfo;

    public TUNativeHelper() {
        mActivity = FlutterPluginUtil.getActivity();
    }

    private void initNative(String placementId) {
        mPlacementId = placementId;

        mTUNative = new TUNative(mActivity, placementId, new TUNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                MsgTools.printMsg("onNativeAdLoaded: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.LoadedCallbackKey,
                        mPlacementId, null, null);
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                MsgTools.printMsg("onNativeAdLoadFail: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.LoadFailCallbackKey,
                        mPlacementId, null, adError.getFullErrorInfo());
            }
        });
    }

    public void loadNative(final String placementId, final Map<String, Object> settings) {
        if (mTUNative == null) {
            initNative(placementId);
        }

        if (mTUNative != null) {
            if (settings != null) {
                try {
                    Map<String, Object> nativeAdSize = (Map<String, Object>) settings.get(Const.SIZE);

                    adViewWidth = Utils.dip2px(mActivity, (double) nativeAdSize.get(Const.WIDTH));
                    adViewHeight = Utils.dip2px(mActivity, (double) nativeAdSize.get(Const.HEIGHT));

                    MsgTools.printMsg("loadNative: " + placementId + ", width: " + adViewWidth + ", height: " + adViewHeight);

                    settings.put(TUAdConst.KEY.AD_WIDTH, adViewWidth);
                    settings.put(TUAdConst.KEY.AD_HEIGHT, adViewHeight);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                try {
                    if (settings.containsKey(Const.Native.isAdaptiveHeight)) {
                        boolean isAdaptiveHeight = (boolean) settings.get(Const.Native.isAdaptiveHeight);//Adaptive height
                        MsgTools.printMsg("loadNative: " + placementId + ", isAdaptiveHeight: " + isAdaptiveHeight);
                        if (isAdaptiveHeight) {
                            settings.put("tt_image_height", 0);
                            settings.put("gdtad_height", -2);
                        }
                    }
                } catch (Throwable e) {
                }

            }

            mTUNative.setLocalExtra(settings);
            mTUNative.makeAdRequest();
        }
    }


    public boolean isReady() {
        MsgTools.printMsg("native isReady: " + mPlacementId);

        boolean isReady = false;
        if (mTUNative != null) {
            TUAdStatusInfo atAdStatusInfo = mTUNative.checkAdStatus();
            isReady = atAdStatusInfo.isReady();
        }

        MsgTools.printMsg("native isReady: " + mPlacementId + ", " + isReady);
        return isReady;
    }

    public Map<String, Object> checkAdStatus() {
        MsgTools.printMsg("native checkAdStatus: " + mPlacementId);

        Map<String, Object> map = new HashMap<>(5);

        if (mTUNative != null) {
            TUAdStatusInfo atAdStatusInfo = mTUNative.checkAdStatus();
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
        MsgTools.printMsg("native checkValidAdCaches: " + mPlacementId);

        if (mTUNative != null) {
            List<TUAdInfo> vaildAds = mTUNative.checkValidAdCaches();
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


    public TUNativeAdView renderNativeView(final Map<String, Object> settings, final String sceneID, boolean isAdaptiveHeight, boolean fromPlatformView, final String showCustomExt) {
        if (mTUNative == null) {
            MsgTools.printMsg("native error, you must call loadNative first");
            return null;
        }
        MsgTools.printMsg("renderNativeView: " + mPlacementId + ", settings: " + (settings != null ? settings.toString() : "") + ", scenario: " + sceneID + ", isAdaptiveHeight: " + isAdaptiveHeight + ", showCustomExt: " + showCustomExt);
        NativeAd nativeAd;

        TUShowConfig.Builder builder = new TUShowConfig.Builder();

        if (!TextUtils.isEmpty(sceneID)) {
            builder.scenarioId(sceneID);
        }
        if (!TextUtils.isEmpty(showCustomExt)) {
            builder.showCustomExt(showCustomExt);
        }

        nativeAd = mTUNative.getNativeAd(builder.build());

        if (nativeAd != null) {

            RoundTUNativeView anyThinkNativeAdView;

            if (fromPlatformView) {
                anyThinkNativeAdView = new RoundTUNativeView(mActivity);

            } else {
                if (atNativeAdView == null) {
                    atNativeAdView = new RoundTUNativeView(mActivity);
                }
                anyThinkNativeAdView = atNativeAdView;
            }

            MsgTools.printMsg("nativeAd:" + nativeAd.toString());
            pViewInfo = parseViewInfo(settings);
            mNativeAd = nativeAd;

            setListener();

            if (isAdaptiveHeight) {//Adaptive height
                if (pViewInfo.rootView != null) {
                    MsgTools.printMsg("nativeAd: rootView, use adaptive height for express");
                    pViewInfo.rootView.mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }

            mNativePrepareInfo = null;

            try {
                if (nativeAd.isNativeExpress()) {
                    MsgTools.printMsg("nativeAd: template-rendering, " + mPlacementId);
                    nativeAd.renderAdContainer(anyThinkNativeAdView, null);
                } else {
                    MsgTools.printMsg("nativeAd: self-rendering, " + mPlacementId);
                    SelfRenderViewUtil selfRenderViewUtil = new SelfRenderViewUtil(mActivity, pViewInfo, mNativeAd.getAdInfo().getNetworkFirmId());
                    mNativePrepareInfo = new TUNativePrepareExInfo();

                    View selfRenderView = selfRenderViewUtil.bindSelfRenderView(mNativeAd.getAdMaterial(), mNativePrepareInfo, pViewInfo);

                    mNativeAd.renderAdContainer(anyThinkNativeAdView, selfRenderView);
                }
            } catch (Throwable e) {

            }

            mNativeAd.prepare(anyThinkNativeAdView, mNativePrepareInfo);


//            NativeRender atUnityRender = new NativeRender(mActivity, pViewInfo);
//            try {
//                if (pViewInfo.dislikeView != null) {
//                    initDislikeView(pViewInfo.dislikeView);
//
//                    atUnityRender.setDislikeView(mDislikeView);
//                }
//
//                nativeAd.renderAdView(anyThinkNativeAdView, atUnityRender);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

//            add dislike button
//            if (pViewInfo.dislikeView != null && mDislikeView != null) {
//                if (mDislikeView.getParent() != null) {
//                    ((ViewGroup) mDislikeView.getParent()).removeView(mDislikeView);
//                }
//
//                anyThinkNativeAdView.addView(mDislikeView);
//            }

//            if (pViewInfo.adLogoView != null) {
//                FrameLayout.LayoutParams adLogoLayoutParams = new FrameLayout.LayoutParams(pViewInfo.adLogoView.mWidth, pViewInfo.adLogoView.mHeight);
//                adLogoLayoutParams.leftMargin = pViewInfo.adLogoView.mX;
//                adLogoLayoutParams.topMargin = pViewInfo.adLogoView.mY;
//                nativeAd.prepare(anyThinkNativeAdView, atUnityRender.getClickViews(), adLogoLayoutParams);
//            } else {
//                nativeAd.prepare(anyThinkNativeAdView, atUnityRender.getClickViews(), null);
//            }

            if (pViewInfo.rootView != null) {
                try {

                    if (pViewInfo.rootView.cornerRadius > 0) {
                        MsgTools.printMsg("rootView setCornerRadius : " + pViewInfo.rootView.cornerRadius);
                        anyThinkNativeAdView.setRadiusInDip(pViewInfo.rootView.cornerRadius);
                    }

                    anyThinkNativeAdView.setLayoutParams(new FrameLayout.LayoutParams(pViewInfo.rootView.mWidth, pViewInfo.rootView.mHeight));

                    anyThinkNativeAdView.setBackgroundColor(Color.parseColor(pViewInfo.rootView.bgcolor));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            if (!fromPlatformView) {
                ViewInfo.addNativeAdView2Activity(mActivity, pViewInfo, anyThinkNativeAdView, -1);
            }

            return anyThinkNativeAdView;

        } else {
            MsgTools.printMsg("nativeAd: " + mPlacementId + ", no cache");
        }

        return null;
    }

    private void setListener() {
        // event
        mNativeAd.setNativeEventListener(new TUNativeEventExListener() {
            @Override
            public void onDeeplinkCallback(TUNativeAdView atNativeAdView, TUAdInfo atAdInfo, boolean isSuccess) {
                MsgTools.printMsg("native onDeeplinkCallback: " + mPlacementId);

                Map<String, Object> extraMap = new HashMap<>();
                extraMap.put(Const.CallbackKey.isDeeplinkSuccess, isSuccess);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.DeeplinkCallbackKey,
                        mPlacementId, atAdInfo.toString(), null, extraMap);
            }

            @Override
            public void onAdImpressed(TUNativeAdView view, TUAdInfo atAdInfo) {
                MsgTools.printMsg("native onAdImpressed: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.ShowCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onAdClicked(TUNativeAdView view, TUAdInfo atAdInfo) {
                MsgTools.printMsg("native onAdClicked: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.ClickCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onAdVideoStart(TUNativeAdView view) {
                MsgTools.printMsg("native onAdImpressed: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.VideoStartKey,
                        mPlacementId, null, null);
            }

            @Override
            public void onAdVideoEnd(TUNativeAdView view) {
                MsgTools.printMsg("native onAdVideoEnd: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.VideoEndKey,
                        mPlacementId, null, null);
            }

            @Override
            public void onAdVideoProgress(TUNativeAdView view, int progress) {
//                    MsgTools.printMsg("native onAdVideoProgress: " + mPlacementId);
//
//                    TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
//                            Const.CallbackMethodCall.NativeCall, Const.NativeCallback.VideoProgressKey,
//                            mPlacementId, null, progress);
            }
        });

        // dislike
        mNativeAd.setDislikeCallbackListener(new TUNativeDislikeListener() {
            @Override
            public void onAdCloseButtonClick(TUNativeAdView view, TUAdInfo atAdInfo) {
                MsgTools.printMsg("native onAdCloseButtonClick: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.NativeCall, Const.NativeCallback.CloseCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }
        });

        // download
//        try {
//            if (TUSDK.isCnSDK()) {
//                mNativeAd.setAdDownloadListener(new TUAppDownloadListener() {
//                    @Override
//                    public void onDownloadStart(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("native onDownloadStart: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadStartKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadUpdate(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("native onDownloadUpdate: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadUpdateKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadPause(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("native onDownloadPause: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadPauseKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFinish(TUAdInfo atAdInfo, long totalBytes, String fileName, String appName) {
//                        MsgTools.printMsg("native onDownloadFinish: " + mPlacementId + ", " + totalBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFinishedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, -1, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFail(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("native onDownloadFail: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFailedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onInstalled(TUAdInfo atAdInfo, String fileName, String appName) {
//                        MsgTools.printMsg("native onInstalled: " + mPlacementId + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadInstalledKey,
//                                mPlacementId, atAdInfo.toString(), -1, -1, fileName, appName);
//                    }
//                });
//            }
//        } catch (Throwable e) {
//        }
    }


    private void initDislikeView(ViewInfo.INFO dislikeInfo) {
        if (mDislikeView == null) {
            mDislikeView = new ImageView(mActivity);
            mDislikeView.setImageResource(Utils.getResId(mActivity, "btn_close", "drawable"));
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dislikeInfo.mWidth, dislikeInfo.mHeight);
        layoutParams.leftMargin = dislikeInfo.mX;
        layoutParams.topMargin = dislikeInfo.mY;

        if (!TextUtils.isEmpty(dislikeInfo.bgcolor)) {
            mDislikeView.setBackgroundColor(Color.parseColor(dislikeInfo.bgcolor));
        }

        mDislikeView.setLayoutParams(layoutParams);
    }

    private ViewInfo parseViewInfo(Map<String, Object> settingsMap) {
        pViewInfo = new ViewInfo();

//        if (activity.isFinishing()) {
//            Log.e("TAG", "settingsMap is null ,user defult");
//            pViewInfo = ViewInfo.createDefualtView(activity);
//        }

        if (settingsMap == null) {
            pViewInfo = ViewInfo.createDefualtView(mActivity);
            return pViewInfo;
        }


        if (settingsMap.containsKey((Const.Native.parent))) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.parent);
            MsgTools.printMsg("parent----> " + tempjson);
            pViewInfo.rootView = pViewInfo.parseINFO(tempjson, "parent", 0, 0);
        }

        if (settingsMap.containsKey(Const.Native.icon)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.icon);
            MsgTools.printMsg("appIcon----> " + tempjson);
            pViewInfo.IconView = pViewInfo.parseINFO(tempjson, "appIcon", 0, 0);
        }

        if (settingsMap.containsKey(Const.Native.mainImage)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.mainImage);
            MsgTools.printMsg("mainImage----> " + tempjson);
            pViewInfo.imgMainView = pViewInfo.parseINFO(tempjson, "mainImage", 0, 0);

        }

        if (settingsMap.containsKey(Const.Native.title)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.title);
            MsgTools.printMsg("title----> " + tempjson);
            pViewInfo.titleView = pViewInfo.parseINFO(tempjson, "title", 0, 0);
        }

        if (settingsMap.containsKey(Const.Native.desc)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.desc);
            MsgTools.printMsg("desc----> " + tempjson);
            pViewInfo.descView = pViewInfo.parseINFO(tempjson, "desc", 0, 0);
        }

        if (settingsMap.containsKey(Const.Native.adLogo)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.adLogo);
            MsgTools.printMsg("adLogo----> " + tempjson);
            pViewInfo.adLogoView = pViewInfo.parseINFO(tempjson, "adLogo", 0, 0);
        }

        if (settingsMap.containsKey(Const.Native.cta)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.cta);
            MsgTools.printMsg("cta----> " + tempjson);
            pViewInfo.ctaView = pViewInfo.parseINFO(tempjson, "cta", 0, 0);
        }

        if (settingsMap.containsKey(Const.Native.dislike)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.dislike);
            MsgTools.printMsg("dislike----> " + tempjson);
            pViewInfo.dislikeView = pViewInfo.parseINFO(tempjson, "dislike", 0, 0);
        }

        if (settingsMap.containsKey(Const.Native.elementsView)) {
            Map<String, Object> tempjson = (Map<String, Object>) settingsMap.get(Const.Native.elementsView);
            MsgTools.printMsg("elementsView----> " + tempjson);
            pViewInfo.elementsView = pViewInfo.parseINFO(tempjson, "elementsView", 0, 0);
        } else {
            ViewInfo.INFO defaultViewInfoForElements = pViewInfo.getDefaultViewInfoForElements(pViewInfo.rootView);
            MsgTools.printMsg("elementsView----> default config: " + defaultViewInfoForElements);
            pViewInfo.elementsView = defaultViewInfoForElements;
        }

        if (settingsMap.containsKey(Const.Native.customView)) {
            List<Map<String, Object>> tempjsonArray = (List<Map<String, Object>>) settingsMap.get(Const.Native.customView);

            if (tempjsonArray != null && tempjsonArray.size() > 0) {
                pViewInfo.customView = new ArrayList<>();

                for (Map<String, Object> map : tempjsonArray) {
                    MsgTools.printMsg("customView----> " + map);
                    pViewInfo.customView.add(pViewInfo.parseINFO(map, "customView", 0, 0));
                }
            }
        }


        return pViewInfo;
    }


    public void showNativeAd(Map<String, Object> settingMap, String scenario, boolean isAdaptiveHeight) {
        renderNativeView(settingMap, scenario, isAdaptiveHeight, false , null);
    }

    public void showNativeAd(Map<String, Object> settingMap, String scenario, boolean isAdaptiveHeight, String showCustomExt) {
        renderNativeView(settingMap, scenario, isAdaptiveHeight, false ,showCustomExt);
    }


    public void removeNativeAd() {
        MsgTools.printMsg("native removeNativeAd: " + mPlacementId);

        if (atNativeAdView != null) {
            if (atNativeAdView.getParent() != null) {
                ((ViewGroup) atNativeAdView.getParent()).removeView(atNativeAdView);
            }
        }
    }

    public void entryScenario(final String placementId,final String sceneID) {
        MsgTools.printMsg("entryNativeScenario: " + mPlacementId + "sceneID: " + sceneID);
        TUNative.entryAdScenario(placementId, sceneID);
    }
}
