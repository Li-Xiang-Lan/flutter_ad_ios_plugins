package com.thinkup.flutter.nativead;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.nativead.api.TUNativeAdView;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.platform.PlatformView;


public class TUAndroidNativeView implements PlatformView {

    TUNativeAdView anyThinkNativeAdView;

    public TUAndroidNativeView(Context context, BinaryMessenger messenger, int viewID, Map<String, Object> args) {
        try {
            String placementID = (String) args.get(Const.PlatformViewKeys.PlacementID);
            String scenario = (String) args.get(Const.PlatformViewKeys.SceneID);
            boolean isAdaptiveHeight = (Boolean) args.get(Const.PlatformViewKeys.isAdaptiveHeight);
            Map<String, Object> settings = (Map<String, Object>) args.get(Const.PlatformViewKeys.ExtraMap);

            MsgTools.printMsg("TUAndroidNativeView: " + placementID + ", scenario: " + scenario + ", settings: " + settings + ", isAdaptiveHeight: " + isAdaptiveHeight);

            if (TextUtils.isEmpty(placementID)) {
                MsgTools.printMsg("TUAndroidNativeView: placementId = null");
                return;
            }
            String showCustomExt = (String)settings.get(Const.SHOW_CUSTOM_EXT);

            TUNativeHelper helper = TUAdNativeManger.getInstance().getHelper(placementID);
            anyThinkNativeAdView = helper.renderNativeView(settings, scenario, isAdaptiveHeight, true,showCustomExt);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Override
    public View getView() {
        return anyThinkNativeAdView;
    }

    @Override
    public void dispose() {

    }
}
