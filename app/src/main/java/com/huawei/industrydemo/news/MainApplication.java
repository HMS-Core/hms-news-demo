/*
    Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.industrydemo.news;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.huawei.hms.opendevice.OpenDevice;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptionsExt;
import com.huawei.industrydemo.news.page.activity.VideoActivity;
import com.huawei.industrydemo.news.repository.AppConfigRepository;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;
import com.huawei.industrydemo.news.utils.hms.AnalyticsUtil;
import com.huawei.industrydemo.news.utils.ContextUtil;
import com.huawei.industrydemo.news.utils.DatabaseUtil;
import com.huawei.industrydemo.news.utils.SystemUtil;

import static com.huawei.industrydemo.news.constants.KeyConstants.LAST_LANGUAGE;
import static com.huawei.industrydemo.news.constants.LogConfig.TAG;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class MainApplication extends Application {
    private static MainApplication mApplication;

    private static WisePlayerFactory wisePlayerFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(this);
        ContextUtil.init(this);
        DatabaseUtil.init(this);
        AnalyticsUtil.getInstance(this).setAnalyticsEnabled(false);
        refreshLanguage(SystemUtil.getLanguage());
        initWisePlayer();
        Glide.with(this);
    }

    private static class MyInitFactoryCallback implements InitFactoryCallback {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            Log.d(TAG, "onSuccess wisePlayerFactory:" + wisePlayerFactory);
            MainApplication.wisePlayerFactory = wisePlayerFactory;
            if (VideoActivity.getCallBack() != null) {
                VideoActivity.getCallBack().onSuccess(wisePlayerFactory);
            }
        }

        @Override
        public void onFailure(int errorCode, String reason) {
            AgcUtil.reportFailure(TAG, "onFailure errorCode:" + errorCode + " reason:" + reason);
        }
    }

    /**
     * init video kit
     */
    public void initWisePlayer() {
        Log.d(TAG, "initWisePlayer");
        // Call the getOdid method to obtain the ODID.
        OpenDevice.getOpenDeviceClient(this).getOdid().addOnSuccessListener(odidResult -> {
            String odid = odidResult.getId();
            Log.d(TAG, "getODID successfully, the ODID is " + odid);
            // DeviceId test is used in the demo, specific access to incoming deviceId after encryption
            WisePlayerFactoryOptionsExt factoryOptions =
                    new WisePlayerFactoryOptionsExt.Builder().setDeviceId(odid).build();
            WisePlayerFactory.initFactory(this, factoryOptions, new MyInitFactoryCallback());
        }).addOnFailureListener(myException -> AgcUtil.reportException(TAG, myException));
    }

    private void refreshLanguage(String newLanguage) {
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        String lastLan = appConfigRepository.getStringValue(LAST_LANGUAGE);
        if (lastLan == null || !lastLan.equals(newLanguage)) {
            Log.d(TAG, "on Language Changed");
            appConfigRepository.setStringValue(LAST_LANGUAGE, newLanguage);
        }
    }

    /**
     * Get WisePlayer Factory
     *
     * @return WisePlayer Factory
     */
    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }

    private static void setContext(MainApplication application) {
        mApplication = application;
    }

    /**
     * getContext
     *
     * @return Context
     */
    public static Context getContext() {
        return mApplication.getApplicationContext();
    }
}
