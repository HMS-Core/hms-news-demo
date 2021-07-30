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

package com.huawei.industrydemo.news.utils.agc;

import android.content.Context;
import android.util.Log;

import com.huawei.agconnect.AGConnectOptions;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class AgcUtil {
    private static String apiKey;

    private static String projectId;

    private static String appId;

    private static String clientSecret;

    private static String clientId;

    private static volatile AGConnectConfig config;

    private static volatile AGConnectOptions options;

    public static synchronized AGConnectConfig getConfig() {
        if (config == null) {
            config = AGConnectConfig.getInstance();
        }
        return config;
    }

    private static synchronized AGConnectOptions getOptions(Context context) {
        if (options == null) {
            options = new AGConnectOptionsBuilder().build(context);
        }
        return options;
    }

    /**
     * reportException
     *
     * @param tag TAG
     * @param throwable Throwable
     */
    public static void reportException(String tag, Throwable throwable) {
        Log.e(tag, throwable.getMessage(), throwable);
        AGConnectCrash.getInstance().recordException(throwable);
    }

    /**
     * reportFailure
     *
     * @param tag TAG
     * @param failureMsg String
     */
    public static void reportFailure(String tag, String failureMsg) {
        Log.w(tag, failureMsg);
        AGConnectCrash.getInstance().log(Log.WARN, failureMsg);
    }

    /**
     * Obtain getProjectId.
     *
     * @param context context
     * @return project_id
     */
    public static synchronized String getProjectId(Context context) {
        if (projectId == null) {
            projectId = getOptions(context).getString("client/project_id");
        }
        return projectId;
    }

    /**
     * Obtain App Id.
     *
     * @param context context
     * @return appId
     */
    public static synchronized String getAppId(Context context) {
        if (appId == null) {
            appId = getOptions(context).getString("client/app_id");
        }
        return appId;
    }

    /**
     * Obtain Api Key.
     *
     * @param context context
     * @return apiKey
     */
    public static synchronized String getApiKey(Context context) {
        if (apiKey == null) {
            apiKey = getOptions(context).getString("client/api_key");
        }
        return apiKey;
    }

    /**
     * Obtain getClientSecret.
     *
     * @param context context
     * @return appId
     */
    public static synchronized String getClientSecret(Context context) {
        if (clientSecret == null) {
            clientSecret = getOptions(context).getString("client/client_secret");
        }
        return clientSecret;
    }

    /**
     * Obtain getClientId.
     *
     * @param context context
     * @return appId
     */
    public static synchronized String getClientId(Context context) {
        if (clientId == null) {
            clientId = getOptions(context).getString("oauth_client/client_id");
        }
        return clientId;
    }
}
