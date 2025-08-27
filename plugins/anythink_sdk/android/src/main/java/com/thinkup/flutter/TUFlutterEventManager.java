package com.thinkup.flutter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.thinkup.flutter.banner.TUAdBannerManger;
import com.thinkup.flutter.init.TUAdInitManger;
import com.thinkup.flutter.interstitial.TUAdInterstitialManger;
import com.thinkup.flutter.nativead.TUAdNativeManger;
import com.thinkup.flutter.reward.TUAdRewardVideoManger;
import com.thinkup.flutter.splash.TUAdSplashManger;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.FlutterPluginUtil;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.flutter.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.flutter.Log;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class TUFlutterEventManager {

    private static TUFlutterEventManager sInstance;

    private MethodChannel mMethodChannel;

    private TUFlutterEventManager() {

    }

    public synchronized static TUFlutterEventManager getInstance() {
        if (sInstance == null) {
            sInstance = new TUFlutterEventManager();
        }
        return sInstance;
    }

    public void init(BinaryMessenger binaryMessenger) {
        if (mMethodChannel == null) {
            mMethodChannel = new MethodChannel(binaryMessenger, "thinkup_sdk");
            mMethodChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
                @Override
                public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
                    //receive message from flutter
                    try {
                        if (Utils.checkMethodInArray(Const.initMethodNames, methodCall.method)) {
                            TUAdInitManger.getInstance().handleMethodCall(methodCall, result);
                        } else if (Utils.checkMethodInArray(Const.rewardVideoMethodNames, methodCall.method)) {
                            TUAdRewardVideoManger.getInstance().handleMethodCall(methodCall, result);
                        } else if (Utils.checkMethodInArray(Const.interstitialMethodNames, methodCall.method)) {
                            TUAdInterstitialManger.getInstance().handleMethodCall(methodCall, result);
                        } else if (Utils.checkMethodInArray(Const.bannerMethodNames, methodCall.method)) {
                            TUAdBannerManger.getInstance().handleMethodCall(methodCall, result);
                        } else if (Utils.checkMethodInArray(Const.nativeMethodNames, methodCall.method)) {
                            TUAdNativeManger.getInstance().handleMethodCall(methodCall, result);
                        } else if (Utils.checkMethodInArray(Const.splashMethodNames, methodCall.method)) {
                            TUAdSplashManger.getInstance().handleMethodCall(methodCall, result);
                        }

                    } catch (Throwable e) {
                        MsgTools.printMsg("method call error: " + methodCall + ", " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void sendDownloadMsgToFlutter(final String callName, String callbackName, String placementId, String atAdInfoString,
                                         long totalBytes, long currBytes, String fileName, String appName) {

        try {
            final Map<String, Object> paramsMap = new HashMap<>(10);
            paramsMap.put("callbackName", callbackName);
            paramsMap.put("placementID", placementId);

            if (atAdInfoString != null) {
                paramsMap.put("extraDic", Utils.jsonStrToMap(atAdInfoString));
            }

            if (totalBytes > 0) {
                paramsMap.put("totalBytes", totalBytes);
            }
            if (currBytes > 0) {
                paramsMap.put("currBytes", currBytes);
            }
            if (fileName != null) {
                paramsMap.put("fileName", fileName);
            }
            if (appName != null) {
                paramsMap.put("appName", appName);
            }

            FlutterPluginUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mMethodChannel != null) {
                            mMethodChannel.invokeMethod(callName, paramsMap);
                        }
                    } catch (Throwable e) {
                        Log.e(MsgTools.TAG, "sendCallbackMsgToFlutter invokeMethod error: " + callName + ", " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Throwable e) {
            Log.e(MsgTools.TAG, "sendCallbackMsgToFlutter error: " + callbackName + ", " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void sendCallbackMsgToFlutter(final String callName, String callbackName, String placementId, String atAdInfoString, String errorMsg, Map<String, Object> extra) {

        try {
            final Map<String, Object> paramsMap = new HashMap<>(8);
            paramsMap.put(Const.CallbackKey.callbackName, callbackName);
            paramsMap.put(Const.CallbackKey.placementID, placementId);

            if (atAdInfoString != null) {
                paramsMap.put(Const.CallbackKey.extraDic, Utils.jsonStrToMap(atAdInfoString));
            }

            if (errorMsg != null) {
                paramsMap.put(Const.CallbackKey.requestMessage, errorMsg);
            } else {
                paramsMap.put(Const.CallbackKey.requestMessage, "");
            }

            try {
                if (extra != null) {
                    Set<Map.Entry<String, Object>> entries = extra.entrySet();
                    for (Map.Entry<String, Object> entry : entries) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        if (value instanceof Boolean) {
                            if (TextUtils.equals(key, Const.CallbackKey.isDeeplinkSuccess) || TextUtils.equals(key, Const.CallbackKey.isTimeout)) {
                                paramsMap.put(key, ((Boolean) value));
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            FlutterPluginUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mMethodChannel != null) {
                            mMethodChannel.invokeMethod(callName, paramsMap);
                        }
                    } catch (Throwable e) {
                        Log.e(MsgTools.TAG, "sendCallbackMsgToFlutter invokeMethod error: " + callName + ", " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Throwable e) {
            Log.e(MsgTools.TAG, "sendCallbackMsgToFlutter error: " + callbackName + ", " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * send message to flutter
     */
    public void sendCallbackMsgToFlutter(String callName, String callbackName, String placementId, String atAdInfoString, String errorMsg) {
        this.sendCallbackMsgToFlutter(callName, callbackName, placementId, atAdInfoString, errorMsg, null);
    }


    /**
     * send message to flutter
     */
    public void sendMsgToFlutter(final String callName, String key, Object extra) {
        try {
            final Map<String, Object> paramsMap = new HashMap<>(4);
            paramsMap.put(key, extra);


            FlutterPluginUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mMethodChannel != null) {
                            mMethodChannel.invokeMethod(callName, paramsMap);
                        }
                    } catch (Throwable e) {
                        Log.e(MsgTools.TAG, "sendMsgToFlutter invokeMethod error: " + key + ", " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Throwable e) {
            Log.e(MsgTools.TAG, "sendMsgToFlutter error: " + key + ", " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void release() {
        if (mMethodChannel != null) {
            mMethodChannel.setMethodCallHandler(null);
            mMethodChannel = null;
        }
    }

}
