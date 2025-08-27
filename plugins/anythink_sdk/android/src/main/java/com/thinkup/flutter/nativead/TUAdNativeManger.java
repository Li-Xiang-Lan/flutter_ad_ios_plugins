package com.thinkup.flutter.nativead;

import androidx.annotation.NonNull;

import com.thinkup.flutter.HandleThinkUpMethod;
import com.thinkup.flutter.utils.Const;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class TUAdNativeManger implements HandleThinkUpMethod {
    static Map<String, TUNativeHelper> pidHelperMap = new ConcurrentHashMap<>();

    private static class SingletonClassInstance {
        private static final TUAdNativeManger instance = new TUAdNativeManger();
    }

    private TUAdNativeManger() {
    }

    public static TUAdNativeManger getInstance() {
        return SingletonClassInstance.instance;
    }

    @Override
    public boolean handleMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) throws JSONException {

        String placementID = methodCall.argument(Const.PLACEMENT_ID);
        Map<String, Object> settingMap = methodCall.argument(Const.EXTRA_DIC);

        TUNativeHelper helper = getHelper(placementID);
        switch (methodCall.method) {
            case "loadNativeAd":
                if (helper != null) {
                    helper.loadNative(placementID, settingMap);
                }
                break;
            case "nativeAdReady":
                if (helper != null) {
                    boolean ready = helper.isReady();
                    result.success(ready);
                } else {
                    result.success(false);
                }
                break;
            case "checkNativeAdLoadStatus":
                if (helper != null) {
                    Map<String, Object> map = helper.checkAdStatus();
                    result.success(map);
                } else {
                    result.success(new HashMap<String, Object>(1));
                }
                break;
            case "getNativeValidAds":
                if (helper != null) {
                    String s = helper.checkValidAdCaches();
                    result.success(s);
                } else {
                    result.success("");
                }
                break;

            case "showNativeAd":
                if (helper != null) {
                    boolean isAdaptiveHeight = false;
                    try {
                        isAdaptiveHeight = (Boolean) methodCall.argument(Const.Native.isAdaptiveHeight);
                    } catch (Throwable e) {
                    }

                    helper.showNativeAd(settingMap, null, isAdaptiveHeight);
                }
                break;
            case "showSceneNativeAd":
                if (helper != null) {
                    String scenario = methodCall.argument(Const.SCENE_ID);

                    boolean isAdaptiveHeight = false;
                    try {
                        isAdaptiveHeight = (Boolean) methodCall.argument(Const.Native.isAdaptiveHeight);
                    } catch (Throwable e) {
                    }

                    helper.showNativeAd(settingMap, scenario, isAdaptiveHeight);
                }
            case "showSceneNativeAdWithCustomExt":
                if (helper != null) {
                    String scenario = methodCall.argument(Const.SCENE_ID);
                    String showCustomExt = methodCall.argument(Const.SHOW_CUSTOM_EXT);

                    boolean isAdaptiveHeight = false;
                    try {
                        isAdaptiveHeight = (Boolean) methodCall.argument(Const.Native.isAdaptiveHeight);
                    } catch (Throwable e) {
                    }

                    helper.showNativeAd(settingMap, scenario, isAdaptiveHeight,showCustomExt);
                }
            case "removeNativeAd":
                if (helper != null) {
                    helper.removeNativeAd();
                }
                break;
            case "entryNativeScenario":
                if (helper != null) {
                    String scenario = methodCall.argument(Const.SCENE_ID);
                    helper.entryScenario(placementID,scenario);
                }
                break;
        }
        return true;
    }


    public TUNativeHelper getHelper(String placementId) {
        TUNativeHelper helper;

        if (!pidHelperMap.containsKey(placementId)) {
            helper = new TUNativeHelper();
            pidHelperMap.put(placementId, helper);
        } else {
            helper = pidHelperMap.get(placementId);
        }

        return helper;
    }
}
