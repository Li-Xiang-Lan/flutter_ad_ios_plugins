package com.thinkup.flutter.banner;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.thinkup.banner.api.TUBannerView;
import com.thinkup.flutter.utils.Const;
import com.thinkup.flutter.utils.MsgTools;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.platform.PlatformView;


public class TUAndroidBannerView implements PlatformView {

    TUBannerView mBannerView;

    public TUAndroidBannerView(Context context, BinaryMessenger messenger, int viewID, Map<String, Object> args) {
        try {
            String placementID = (String) args.get(Const.PlatformViewKeys.PlacementID);
            String scenario = (String) args.get(Const.PlatformViewKeys.SceneID);

            MsgTools.printMsg("TUAndroidBannerView: " + placementID + ", scenario: " + scenario);

            if (TextUtils.isEmpty(placementID)) {
                MsgTools.printMsg("TUAndroidBannerView: placementId = null");
                return;
            }

            TUBannerHelper helper = TUAdBannerManger.getInstance().getHelper(placementID);
            mBannerView = helper.getBannerView();

            if (!TextUtils.isEmpty(scenario) && mBannerView != null) {
                mBannerView.setScenario(scenario);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView() {
        return mBannerView;
    }

    @Override
    public void dispose() {

    }
}
