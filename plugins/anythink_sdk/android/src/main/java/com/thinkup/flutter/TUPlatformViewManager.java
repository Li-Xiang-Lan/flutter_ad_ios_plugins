package com.thinkup.flutter;

import com.thinkup.flutter.banner.TUBannerViewFactory;
import com.thinkup.flutter.nativead.TUNativeViewFactory;
import com.thinkup.flutter.utils.Const;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

public class TUPlatformViewManager {

    private static TUPlatformViewManager sInstance;

    private TUPlatformViewManager() {
    }

    public synchronized static TUPlatformViewManager getInstance() {
        if (sInstance == null) {
            sInstance = new TUPlatformViewManager();
        }
        return sInstance;
    }

    public void init(FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(Const.PlatformViewName.Banner, new TUBannerViewFactory(flutterPluginBinding.getBinaryMessenger()));
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(Const.PlatformViewName.Native, new TUNativeViewFactory(flutterPluginBinding.getBinaryMessenger()));
    }

}
