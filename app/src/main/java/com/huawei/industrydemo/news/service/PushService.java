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

package com.huawei.industrydemo.news.service;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.BaseException;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.industrydemo.news.constants.KeyConstants;
import com.huawei.industrydemo.news.repository.AppConfigRepository;
import com.huawei.industrydemo.news.utils.hms.AnalyticsUtil;

public class PushService extends HmsMessageService {
    private static final String TAG = com.huawei.industrydemo.news.service.PushService.class.getSimpleName();

    /**
     * Called when you have obtained the token from the Push Kit server through the getToken(String subjectId) method of
     * HmsInstanceId in the multi-sender scenario. Bundle contains some extra return data, which can be obtained through
     * key values such as HmsMessageService.SUBJECT_ID.
     *
     * @param token Token returned by the HMS Core Push SDK.
     * @param bundle Other data returned by the Push SDK except tokens.
     */
    @Override
    public void onNewToken(String token, Bundle bundle) {
        Log.i(TAG, "received refresh token:" + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            // This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback
            // processing.
            refreshedToken(token);
        }
    }

    private void refreshedToken(String token) {
        Log.i(TAG, "sending token to local. token:" + token);
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        if (!token.equals(appConfigRepository.getStringValue(KeyConstants.PUSH_TOKEN))) {
            appConfigRepository.setStringValue(KeyConstants.PUSH_TOKEN, token);
            AnalyticsUtil.getInstance(this).setPushToken(token);
        }
    }

    /**
     * Called when you fail to apply for a token from the Push Kit server through the getToken(String subjectId) method
     * of HmsInstanceId in the multi-sender scenario.
     * Bundle contains some extra return data, which can be obtained through HmsMessageService.SUBJECT_ID.
     *
     * @param e Exception of the BaseException type, which is returned when the app fails to call the getToken method to
     *        apply for a token.
     * @param bundle Other data returned by the Push SDK except tokens.
     */
    public void onTokenError(Exception e, Bundle bundle) {
        super.onTokenError(e);
        int errCode = e instanceof BaseException ? ((BaseException) e).getErrorCode() : -1;
        String errInfo = e.getMessage();
        Log.i(TAG, "onTokenError called, errCode:" + errCode + ",errInfo=" + errInfo);
    }
}
