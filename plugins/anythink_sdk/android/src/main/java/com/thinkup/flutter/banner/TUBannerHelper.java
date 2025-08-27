package com.thinkup.flutter.banner;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.thinkup.banner.api.TUBannerExListener;
import com.thinkup.banner.api.TUBannerView;
//import com.thinkup.china.api.TUAppDownloadListener;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.core.api.TUAdStatusInfo;
import com.thinkup.core.api.TUNetworkConfirmInfo;
import com.thinkup.core.api.TUShowConfig;

//import com.thinkup.core.api.TUSDK;
import com.thinkup.core.api.AdError;
import com.thinkup.flutter.TUFlutterEventManager;
import com.thinkup.flutter.ThinkupSdkPlugin;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.FlutterPluginUtil;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.flutter.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TUBannerHelper extends ThinkupSdkPlugin {

    Activity mActivity;
    String mPlacementId;
    TUBannerView mBannerView;

    public TUBannerHelper() {
        mActivity = FlutterPluginUtil.getActivity();
    }

    public void initBanner(String placementId) {
        mPlacementId = placementId;
        mBannerView = new TUBannerView(mActivity);
        mBannerView.setPlacementId(mPlacementId);
        mBannerView.setBannerAdListener(new TUBannerExListener() {
            @Override
            public void onDeeplinkCallback(boolean isRefresh, TUAdInfo atAdInfo, boolean isSuccess) {
                MsgTools.printMsg("banner onDeeplinkCallback: " + mPlacementId);

                Map<String, Object> extraMap = new HashMap<>();
                extraMap.put(Const.CallbackKey.isDeeplinkSuccess, isSuccess);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.DeeplinkCallbackKey,
                        mPlacementId, atAdInfo.toString(), null, extraMap);
            }

            @Override
            public void onDownloadConfirm(Context context, TUAdInfo atAdInfo, TUNetworkConfirmInfo atNetworkConfirmInfo) {
                MsgTools.printMsg("banner onDownloadConfirm: " + mPlacementId);
            }

            @Override
            public void onBannerLoaded() {
                MsgTools.printMsg("onBannerLoaded: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.LoadedCallbackKey,
                        mPlacementId, null, null);
            }

            @Override
            public void onBannerFailed(AdError adError) {
                MsgTools.printMsg("onBannerFailed: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.LoadFailCallbackKey,
                        mPlacementId, null, adError.getFullErrorInfo());
            }

            @Override
            public void onBannerClicked(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onBannerClicked: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.ClickCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onBannerShow(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onBannerShow: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.ShowCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onBannerClose(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onBannerClose: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.CloseCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onBannerAutoRefreshed(TUAdInfo atAdInfo) {
                MsgTools.printMsg("onBannerAutoRefreshed: " + mPlacementId);

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.RefreshCallbackKey,
                        mPlacementId, atAdInfo.toString(), null);
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                MsgTools.printMsg("onBannerAutoRefreshFail: " + mPlacementId + ", " + adError.getFullErrorInfo());

                TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                        Const.CallbackMethodCall.BannerCall, Const.BannerCallback.RefreshFailCallbackKey,
                        mPlacementId, null, adError.getFullErrorInfo());
            }
        });

        //download
//        try {
//            if (TUSDK.isCnSDK()) {
//                mBannerView.setAdDownloadListener(new TUAppDownloadListener() {
//                    @Override
//                    public void onDownloadStart(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("banner onDownloadStart: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadStartKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadUpdate(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("banner onDownloadUpdate: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadUpdateKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadPause(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("banner onDownloadPause: " + mPlacementId);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadPauseKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFinish(TUAdInfo atAdInfo, long totalBytes, String fileName, String appName) {
//                        MsgTools.printMsg("banner onDownloadFinish: " + mPlacementId + ", " + totalBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFinishedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, -1, fileName, appName);
//                    }
//
//                    @Override
//                    public void onDownloadFail(TUAdInfo atAdInfo, long totalBytes, long currBytes, String fileName, String appName) {
//                        MsgTools.printMsg("banner onDownloadFail: " + mPlacementId + ", " + totalBytes + ", " + currBytes + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadFailedKey,
//                                mPlacementId, atAdInfo.toString(), totalBytes, currBytes, fileName, appName);
//                    }
//
//                    @Override
//                    public void onInstalled(TUAdInfo atAdInfo, String fileName, String appName) {
//                        MsgTools.printMsg("banner onInstalled: " + mPlacementId + ", " + fileName + ", " + appName);
//
//                        TUFlutterEventManager.getInstance().sendDownloadMsgToFlutter(Const.CallbackMethodCall.DownloadCall, Const.DownloadCallCallback.DownloadInstalledKey,
//                                mPlacementId, atAdInfo.toString(), -1, -1, fileName, appName);
//                    }
//                });
//            }
//        } catch (Throwable e) {
//        }
    }

    public void loadBanner(final String placementId, final Map<String, Object> settings) {
        MsgTools.printMsg("loadBanner: " + placementId + ", settings: " + settings);

        FlutterPluginUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBannerView == null) {
                    initBanner(placementId);
                }

                if (settings != null) {
                    try {
                        Map<String, Object> map = (Map<String, Object>) settings.get(Const.SIZE);
                        int width = Utils.dip2px(mActivity, Double.parseDouble(map.get(Const.WIDTH).toString()));
                        int height = Utils.dip2px(mActivity, Double.parseDouble(map.get(Const.HEIGHT).toString()));

                        if (mBannerView != null) {
                            if (mBannerView.getLayoutParams() == null) {
                                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
                                mBannerView.setLayoutParams(lp);
                            } else {
                                mBannerView.getLayoutParams().width = width;
                                mBannerView.getLayoutParams().height = height;
                            }

                            MsgTools.printMsg("loadBanner: " + mPlacementId + ", width: " + width + ", height: " + height);

                            settings.put("key_width", width);
                            settings.put("key_height", height);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    try {
                        Object adaptiveWidthObject = settings.get(Const.Banner.adaptiveWidth);
                        if (adaptiveWidthObject != null) {
                            int width = Utils.dip2px(mActivity, Double.parseDouble(adaptiveWidthObject.toString()));
                            MsgTools.printMsg("loadBanner: " + mPlacementId + ", adaptiveWidth: " + width);
                            settings.put("adaptive_width", width);
                        }
                    } catch (Throwable e) {
                    }

                    try {
                        Object adaptiveOrientationObject = settings.get(Const.Banner.adaptiveOrientation);
                        if (adaptiveOrientationObject != null) {
                            int orientation = Utils.dip2px(mActivity, Integer.parseInt(adaptiveOrientationObject.toString()));
                            MsgTools.printMsg("loadBanner: " + mPlacementId + ", adaptiveOrientation: " + orientation);
                            settings.put("adaptive_orientation", orientation);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                }
                mBannerView.setLocalExtra(settings);

                mBannerView.loadAd();
            }
        });
    }


    public boolean isAdReady() {
        MsgTools.printMsg("banner isAdReady: " + mPlacementId);

        boolean isReady = false;
        if (mBannerView != null) {
            TUAdStatusInfo atAdStatusInfo = mBannerView.checkAdStatus();
            if (atAdStatusInfo != null) {
                isReady = atAdStatusInfo.isReady();
            }
        }
        MsgTools.printMsg("banner isAdReady: " + mPlacementId + ", " + isReady);
        return isReady;
    }

    public Map<String, Object> checkAdStatus() {
        MsgTools.printMsg("banner checkAdStatus: " + mPlacementId);

        Map<String, Object> map = new HashMap<>(5);

        if (mBannerView != null) {
            TUAdStatusInfo atAdStatusInfo = mBannerView.checkAdStatus();
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
        MsgTools.printMsg("banner checkValidAdCaches: " + mPlacementId);

        if (mBannerView != null) {
            List<TUAdInfo> vaildAds = mBannerView.checkValidAdCaches();
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


    public TUBannerView getBannerView() {
        return mBannerView;
    }

    public void showBannerWithRect(final Map<String, Object> settings, final String scenarioParam) {

        MsgTools.printMsg("showBannerWithRect: " + mPlacementId + ", scenario: " + scenarioParam);

        String scenario = scenarioParam;
        if (TextUtils.isEmpty(scenario)) {
            scenario = "";
        }

        String showCustomExtInMap = (String)settings.get(Const.SHOW_CUSTOM_EXT);
        if (TextUtils.isEmpty(showCustomExtInMap)) {
            showCustomExtInMap = "";
        }

        if (mBannerView != null) {

            int x = 0;
            int y = 0;
            int width = 0;
            int height = 0;

            if (settings != null) {
                Map<String, Object> size = (Map<String, Object>) settings.get(Const.SIZE);

                x = getIntValue(size.get(Const.X));
                y = getIntValue(size.get(Const.Y));
                width = getIntValue(size.get(Const.WIDTH));
                height = getIntValue(size.get(Const.HEIGHT));
            }

            final int finalX = x;
            final int finalY = y;
            final int finalWidth = width;
            final int finalHeight = height;
            final String finalShowCustomExtInMap = showCustomExtInMap;
            final String finalScenario = scenario;

            FlutterPluginUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int width = mBannerView.getLayoutParams().width;
                    int height = mBannerView.getLayoutParams().height;
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                    layoutParams.leftMargin = (int) finalX;
                    layoutParams.topMargin = (int) finalY;
                    if (mBannerView.getParent() != null) {
                        ((ViewGroup) mBannerView.getParent()).removeView(mBannerView);
                    }

                    TUShowConfig.Builder builder = new TUShowConfig.Builder();
                    builder.scenarioId(finalScenario);
                    builder.showCustomExt(finalShowCustomExtInMap);
                    mBannerView.setShowConfig(builder.build());
                    MsgTools.printMsg("showBannerWithRect x: " + finalX + "y:" + finalY + "w:" + finalWidth + "h:" + finalHeight + "scenario:" + finalScenario + "finalShowCustomExtInMap:" + finalShowCustomExtInMap);
                    mActivity.addContentView(mBannerView, layoutParams);
                }
            });
        }
    }

    private int getIntValue(Object object) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        throw new IllegalArgumentException("TUBannerHelper getIntValue -- Non-numeric types cannot be converted to integer types");
    }

    public void showBannerWithPosition(final String position, final String scenarioParam, final String showCustomExtParam) {
        MsgTools.printMsg("showBannerWithPosition: " + mPlacementId + ", scenario: " + scenarioParam + ", showCustomExt: " + showCustomExtParam);

        FlutterPluginUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBannerView != null) {
                    int width = mBannerView.getLayoutParams().width;
                    int height = mBannerView.getLayoutParams().height;
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                    if (position.equals(Const.POSITION_TOP)) {
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    } else {
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                    }

                    String scenario = scenarioParam;
                    if (TextUtils.isEmpty(scenario)) {
                        scenario = "";
                    }

                    String showCustomExt = showCustomExtParam;
                    if (TextUtils.isEmpty(showCustomExt)) {
                        showCustomExt = "";
                    }

                    TUShowConfig.Builder builder = new TUShowConfig.Builder();
                    builder.scenarioId(scenario);
                    builder.showCustomExt(showCustomExt);
                    mBannerView.setShowConfig(builder.build());
                    mActivity.addContentView(mBannerView, layoutParams);
                }
            }
        });
    }

    public void reshowBanner() {
        MsgTools.printMsg("reshowBanner: " + mPlacementId);

        FlutterPluginUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBannerView != null) {
                    mBannerView.setVisibility(View.VISIBLE);
                } else {
                    MsgTools.printMsg("reshowBanner error, you must call loadBanner first, placementId: " + mPlacementId);
                }
            }
        });
    }

    public void hideBanner() {
        MsgTools.printMsg("hideBanner: " + mPlacementId);

        FlutterPluginUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBannerView != null) {
                    mBannerView.setVisibility(View.GONE);
                } else {
                    MsgTools.printMsg("hideBanner error, you must call loadBanner first, placementId: " + mPlacementId);
                }
            }
        });
    }

    public void removeBanner() {
        MsgTools.printMsg("removeBanner: " + mPlacementId);

        FlutterPluginUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBannerView != null) {
                    if (mBannerView.getParent() != null) {
                        ViewParent viewParent = mBannerView.getParent();
                        ((ViewGroup) viewParent).removeView(mBannerView);
                    }
                } else {
                    MsgTools.printMsg("removeBanner error, you must call loadBanner first, placementId: " + mPlacementId);
                }
            }
        });
    }

    public void entryScenario(final String placementId,final String sceneID) {
        MsgTools.printMsg("entryBannerScenario: " + mPlacementId + "sceneID: " + sceneID);
        TUBannerView.entryAdScenario(placementId, sceneID);
    }

}
