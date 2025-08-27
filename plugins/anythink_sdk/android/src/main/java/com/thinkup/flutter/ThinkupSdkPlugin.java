package com.thinkup.flutter;

import androidx.annotation.NonNull;

import com.thinkup.flutter.utils.FlutterPluginUtil;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/**
 * ThinkupSdkPlugin
 */
public class ThinkupSdkPlugin implements FlutterPlugin, ActivityAware {

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        //init event manager
        TUFlutterEventManager.getInstance().init(flutterPluginBinding.getBinaryMessenger());
        //init platformView manager
        TUPlatformViewManager.getInstance().init(flutterPluginBinding);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        //release
        TUFlutterEventManager.getInstance().release();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        FlutterPluginUtil.setActivity(activityPluginBinding.getActivity());
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}
