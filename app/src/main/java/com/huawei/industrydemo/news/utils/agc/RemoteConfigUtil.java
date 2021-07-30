/*
 *     Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.industrydemo.news.utils.agc;

import android.util.Log;

import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.agconnect.remoteconfig.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static com.huawei.industrydemo.news.constants.KeyConstants.LATEST_VERSION_NUM;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/27]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class RemoteConfigUtil {
    private static final String TAG = RemoteConfigUtil.class.getSimpleName();

    private static final AtomicInteger TIMES = new AtomicInteger(0);

    private static String appSecret;

    /**
     * init data of Remote Config
     */
    public static void init() {
        Map<String, Object> map = new HashMap<>();
        map.put(LATEST_VERSION_NUM, BuildConfig.VERSION_CODE);
        AGConnectConfig config = AgcUtil.getConfig();
        if (config == null) {
            AgcUtil.reportException(TAG, new NullPointerException());
            return;
        }
        config.applyDefault(map);
        fetch();
    }

    /**
     * Fetches latest parameter values from the cloud at the default interval of 12 hours.
     * If the method is called within an interval, cached data is returned.
     */
    public static void fetch() {
        Log.d(TAG, "RemoteConfigUtil.fetch()");
        if (TIMES.get() == 2) {
            return;
        }
        try {
            AGConnectConfig config = AgcUtil.getConfig();
            if (config == null) {
                AgcUtil.reportException(TAG, new NullPointerException());
                return;
            }
            config.fetch().addOnSuccessListener(configValues -> {
                TIMES.set(0);
                config.apply(configValues);
            }).addOnFailureListener(exception -> {
                AgcUtil.reportException(TAG, exception);
                if (TIMES.addAndGet(1) == 2) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            TIMES.set(0);
                        }
                    }, 150000);
                }
            });
        } catch (Exception exception) {
            AgcUtil.reportException(TAG, exception);
        }
    }

    public static String getAppSecret() {
        if (null != appSecret) {
            return appSecret;
        }
        AGConnectConfig config = AgcUtil.getConfig();
        Map<String, Object> results = config.getMergedAll();
        if (results.containsKey("App_Secret")) {
            appSecret = config.getValueAsString("App_Secret");
        } else {
            Log.e(TAG, "App_Secret is null!");
            fetch();
        }
        return appSecret;
    }
}
