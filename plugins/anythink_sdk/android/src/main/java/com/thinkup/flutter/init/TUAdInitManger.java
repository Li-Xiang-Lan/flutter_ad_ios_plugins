package com.thinkup.flutter.init;

import androidx.annotation.NonNull;

import com.thinkup.core.api.TUGDPRAuthCallback;
import com.thinkup.core.api.TUGDPRConsentDismissListener;
import com.thinkup.core.api.TUSDK;
import com.thinkup.core.api.NetTrafficeCallback;
import com.thinkup.flutter.TUFlutterEventManager;
import com.thinkup.flutter.HandleThinkUpMethod;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.FlutterPluginUtil;
import com.thinkup.flutter.utils.MsgTools;

import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class TUAdInitManger implements HandleThinkUpMethod {

    private static class SingletonClassInstance {
        private static final TUAdInitManger instance = new TUAdInitManger();
    }

    public static TUAdInitManger getInstance() {
        return SingletonClassInstance.instance;
    }

    private TUAdInitManger() {
    }

    @Override
    public boolean handleMethodCall(@NonNull MethodCall methodCall, @NonNull final MethodChannel.Result result) throws Exception {

        switch (methodCall.method) {
            case "initThinkUpSDK":
                String appID = methodCall.argument(Const.Init.APP_ID_STR);
                String appKey = methodCall.argument(Const.Init.APP_KEY_STR);

                MsgTools.printMsg("initSDK: " + appID + ", " + appKey);
                TUSDK.init(FlutterPluginUtil.getApplicationContext(), appID, appKey);
                result.success("");
                break;
            case "setLogEnabled":
                Boolean logEnable = methodCall.argument(Const.Init.LOG_ENABLE);

                MsgTools.setLogDebug(logEnable);
                MsgTools.printMsg("setLogEnabled: " + logEnable);
                TUSDK.setNetworkLogDebug(logEnable);
                break;
            case "setChannelStr":
                String channelStr = methodCall.argument(Const.Init.CHANNEL_STR);

                MsgTools.printMsg("setChannelStr: " + channelStr);
                TUSDK.setChannel(channelStr);
                break;
            case "setSubchannelStr":
                String subchannelStr = methodCall.argument(Const.Init.SUB_CHANNEL_STR);

                MsgTools.printMsg("setSubchannelStr: " + subchannelStr);
                TUSDK.setSubChannel(subchannelStr);
                break;
            case "setCustomDataDic":
                Map<String, Object> argument = methodCall.argument(Const.Init.CUSTOM_DTUA_DIC);
                if (argument != null) {
                    MsgTools.printMsg("setCustomDataDic: " + argument);
                    TUSDK.initCustomMap(argument);
                }
                break;
            case "setExludeBundleIDArray":
                MsgTools.printMsg("setExludeBundleIDArray");
                List<String> bundleIdList = methodCall.argument(Const.Init.EXLUDE_BUNDLE_ID_ARRAY);

                if (bundleIdList != null) {

                    int size = bundleIdList.size();
                    for (int i = 0; i < size; i++) {
                        MsgTools.printMsg("setExludeBundleIDArray: " + bundleIdList.get(i));
                    }

//                    TUSDK.setExcludeMyOfferPkgList(bundleIdList);
                    TUSDK.setExcludePackageList(bundleIdList);
                }
                break;
            case "deniedUploadDeviceInfo":
                MsgTools.printMsg("deniedUploadDeviceInfo");
                List<String> deniedUploadDeviceInfoList = methodCall.argument(Const.Init.DENIED_UPLOAD_INFO_ARRAY);

                if (deniedUploadDeviceInfoList != null) {

                    int size = deniedUploadDeviceInfoList.size();
                    if (size > 0) {
                        String[] deniedArray = new String[size];
                        String info;
                        for (int i = 0; i < size; i++) {
                            info = deniedUploadDeviceInfoList.get(i);
                            deniedArray[i] = info;
                            MsgTools.printMsg("deniedUploadDeviceInfo: " + info);
                        }

                        TUSDK.deniedUploadDeviceInfo(deniedArray);
                        break;
                    }
                }

                try {
                    MsgTools.printMsg("deniedUploadDeviceInfo: empty string");
                    TUSDK.deniedUploadDeviceInfo("");
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                break;
            case "setPlacementCustomData":
                String placementIDStr = methodCall.argument(Const.Init.PLACEMENT_ID_STR);
                Map<String, Object> placementCustomDataMap = methodCall.argument(Const.Init.PLACEMENT_CUSTOM_DTUA_DIC);

                MsgTools.printMsg("setPlacementCustomData: " + placementIDStr + ", " + placementCustomDataMap);
                TUSDK.initPlacementCustomMap(placementIDStr, placementCustomDataMap);
                break;
            case "getGDPRLevel":
                int gdprDataLevel = TUSDK.getGDPRDataLevel(FlutterPluginUtil.getApplicationContext());

                MsgTools.printMsg("getGDPRLevel: " + gdprDataLevel);

                String levelString;
                switch (gdprDataLevel) {
                    case TUSDK.PERSONALIZED:
                        levelString = "TUDataConsentSetPersonalized";
                        break;
                    case TUSDK.NONPERSONALIZED:
                        levelString = "TUDataConsentSetNonpersonalized";
                        break;
                    default:
                        levelString = "TUDataConsentSetUnknown";
                        break;
                }
                MsgTools.printMsg("getGDPRLevel: callback to flutter: " + levelString);
                result.success(levelString);
                break;
            case "getUserLocation":
                MsgTools.printMsg("getUserLocation");
                TUSDK.checkIsEuTraffic(FlutterPluginUtil.getApplicationContext(), new NetTrafficeCallback() {
                    @Override
                    public void onResultCallback(boolean b) {
                        MsgTools.printMsg("getUserLocation: onResultCallback: " + b);

                        final String result = b ? "1" : "2";
                        MsgTools.printMsg("getUserLocation: callback to flutter: result: " + result);
                        TUFlutterEventManager.getInstance().sendMsgToFlutter(Const.CallbackMethodCall.InitCallName, Const.InitCallback.locationCallbackKey, result);
                    }

                    @Override
                    public void onErrorCallback(String s) {
                        MsgTools.printMsg("getUserLocation: onErrorCallback: " + s);

                        TUFlutterEventManager.getInstance().sendMsgToFlutter(Const.CallbackMethodCall.InitCallName, Const.InitCallback.locationCallbackKey, "0");//unknown
                    }
                });
                break;
            case "setDataConsentSet":
                String uploadDataLevel = methodCall.argument(Const.Init.GDPR_UPLOAD_DTUA_LEVEL);

                MsgTools.printMsg("setDataConsentSet: " + uploadDataLevel);

                int level;
                switch (uploadDataLevel) {
                    case "TUDataConsentSetPersonalized":
                        level = TUSDK.PERSONALIZED;
                        break;
                    case "TUDataConsentSetNonpersonalized":
                        level = TUSDK.NONPERSONALIZED;
                        break;
                    default:
                        level = TUSDK.UNKNOWN;
                        break;
                }

                TUSDK.setGDPRUploadDataLevel(FlutterPluginUtil.getApplicationContext(), level);
                break;
            case "showGDPRAuth":
                MsgTools.printMsg("showGDPRAuth");

                FlutterPluginUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TUSDK.showGdprAuth(FlutterPluginUtil.getApplicationContext(), new TUGDPRAuthCallback() {
                            @Override
                            public void onAuthResult(int i) {
                                MsgTools.printMsg("showGDPRAuth: onAuthResult: " + i);

                                String result;
                                switch (i) {
                                    case TUSDK.PERSONALIZED:
                                        result = "1";
                                        break;
                                    case TUSDK.NONPERSONALIZED:
                                        result = "2";
                                        break;
                                    default:
                                        result = "0";//unknown
                                        break;
                                }
                                MsgTools.printMsg("showGDPRAuth: onAuthResult: callback to flutter: result: " + result);
                                TUFlutterEventManager.getInstance().sendMsgToFlutter(Const.CallbackMethodCall.InitCallName, Const.InitCallback.consentSetCallbackKey, result);
                            }

                            @Override
                            public void onPageLoadFail() {
                                MsgTools.printMsg("showGDPRAuth: onPageLoadFail");

                                TUFlutterEventManager.getInstance().sendMsgToFlutter(Const.CallbackMethodCall.InitCallName, Const.InitCallback.consentSetCallbackKey, "0");//unknown
                            }
                        });
                    }
                });
                break;

            case "showGDPRConsentDialog":
                MsgTools.printMsg("showGDPRConsentDialog");

                FlutterPluginUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TUSDK.showGDPRConsentDialog(FlutterPluginUtil.getActivity(), new TUGDPRConsentDismissListener() {
                            @Override
                            public void onDismiss(ConsentDismissInfo consentDismissInfo) {
                                MsgTools.printMsg("showGDPRConsentDialog: onDismiss: " + consentDismissInfo);

                                TUFlutterEventManager.getInstance().sendMsgToFlutter(Const.CallbackMethodCall.InitCallName, Const.InitCallback.consentDismissCallbackKey, "");
                            }
                        });
                    }
                });
                break;

            case "setPresetPlacementConfigPath":
                String path = methodCall.argument(Const.Init.PTUH_STR);

                MsgTools.printMsg("setPresetPlacementConfigPath: " + path);
                TUSDK.setLocalStrategyAssetPath(FlutterPluginUtil.getApplicationContext(), path);
                break;

            case "showDebuggerUI":
//                String debugKey = methodCall.argument(Const.DEBUGKEY);
//                try {
//                    TUDebuggerUITest.showDebuggerUI(FlutterPluginUtil.getApplicationContext(), debugKey);
//                } catch (Error e) {
//                    MsgTools.printMsg("showDebuggerUI: " + e.toString());
//                }
                break;

        }
        return true;
    }
}
