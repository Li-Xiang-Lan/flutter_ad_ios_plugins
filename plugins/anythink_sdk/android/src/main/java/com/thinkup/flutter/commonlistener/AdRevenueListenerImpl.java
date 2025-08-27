package com.thinkup.flutter.commonlistener;

import androidx.annotation.NonNull;

import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.MsgTools;
import com.thinkup.flutter.TUFlutterEventManager;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import com.thinkup.core.api.TUAdRevenueListener;
import com.thinkup.core.api.TUAdInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdRevenueListenerImpl implements TUAdRevenueListener {
    private String placementId;

    public AdRevenueListenerImpl(String placementId) {
        this.placementId = placementId;
    }

    @Override
    public void onAdRevenuePaid(TUAdInfo adInfo) {
        MsgTools.printMsg("onAdRevenuePaid: " + adInfo + "placementID: " + placementId);

        TUFlutterEventManager.getInstance().sendCallbackMsgToFlutter(
                Const.CallbackMethodCall.CommonADCall, Const.CommonADCallBack.AdShowRevenueCallbackKey,
                placementId, adInfo.toString(), null);
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }
}

