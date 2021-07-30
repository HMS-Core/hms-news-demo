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

package com.huawei.industrydemo.news.utils.hms;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.common.util.AGCUtils;
import com.huawei.hms.network.httpclient.HttpClient;
import com.huawei.hms.network.httpclient.Request;
import com.huawei.hms.network.httpclient.RequestBody;
import com.huawei.hms.network.httpclient.Response;
import com.huawei.hms.network.httpclient.ResponseBody;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.constants.Constants;
import com.huawei.industrydemo.news.constants.KeyConstants;
import com.huawei.industrydemo.news.repository.AppConfigRepository;
import com.huawei.industrydemo.news.utils.JsonUtil;
import com.huawei.industrydemo.news.utils.agc.RemoteConfigUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessagingUtil {
    private static final String SEND_API_PRE = "https://push-api.cloud.huawei.com/v1/";

    private static final String SEND_API_POST = "/messages:send";

    private static final String GET_ACCESS_TOKEN_API = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";

    private static final String NEWS_INTENT =
        "intent://com.huawei.industrydemo.news/newsDetail?#Intent;scheme=pushscheme;launchFlags=0x4000000;S.newId=%s;end";

    private static String appSecret;

    private static String msgJson;

    private static final String TAG = MessagingUtil.class.getSimpleName();

    public static void newsNotificationMessage(Context context, String account, String newsTitle, String newsId) {
        appSecret = RemoteConfigUtil.getAppSecret();
        if (null == appSecret) {
            return;
        }
        new Thread(() -> {
            String accessToken = getAccessToken(context);
            Log.d(TAG, "accessToken: " + accessToken);
            if (!TextUtils.isEmpty(accessToken)) {
                String msgIntent = String.format(Locale.ROOT, NEWS_INTENT, newsId);
                String msgContent = getMsgContent(context, account, newsTitle, msgIntent);
                String response = sendNotificationMessage(context, accessToken, msgContent);
                Log.d(TAG, "response:" + response);
            }
        }).start();
    }

    private static String getMsgContent(Context context, String bodyArg1, String bodyArg2, String intent) {
        if (null == msgJson) {
            InputStream inputStream = context.getResources().openRawResource(R.raw.message);
            msgJson = JsonUtil.getJson(inputStream);
        }
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        String pushToken = appConfigRepository.getStringValue(KeyConstants.PUSH_TOKEN);
        return String.format(Locale.ROOT, msgJson, bodyArg1, bodyArg2, intent, pushToken);
    }

    private static String getAccessToken(Context context) {
        String appId = AGCUtils.getAppId(context);
        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(GET_ACCESS_TOKEN_API).method("POST");
        requestBuilder.requestBody(new RequestBody() {
            @Override
            public String contentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                Map<String, String> params = new HashMap<>();
                // client id & client secret
                params.put("client_id", appId);
                params.put("client_secret", appSecret);
                params.put("grant_type", "client_credentials");
                StringBuffer buffer = new StringBuffer();
                if (!params.isEmpty()) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        buffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append("&");
                    }
                }
                // Delete the last character &. One more character is added. The body setting is complete.
                buffer.deleteCharAt(buffer.length() - 1);
                byte[] mydata = buffer.toString().getBytes(Charset.defaultCharset());

                outputStream.write(mydata, 0, mydata.length);
                outputStream.flush();
            }
        });

        try {
            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
            if (response.getCode() == 200) {
                InputStream is = response.getBody().getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] byteBuffer = new byte[1024];
                while ((len = is.read(byteBuffer)) != -1) {
                    message.write(byteBuffer, 0, len);
                }
                is.close();
                message.close();
                String msg = new String(message.toByteArray(), Charset.defaultCharset());
                if (!TextUtils.isEmpty(msg)) {
                    String tempAT = msg.substring(msg.indexOf("access_token") + 15, msg.length() - 1);
                    return tempAT.substring(0, tempAT.indexOf("\"")).replaceAll("\\\\", Constants.EMPTY);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private static String sendNotificationMessage(Context context, String accessToken, String msgContent) {
        Log.d(TAG, msgContent);
        String sendApi = SEND_API_PRE + AGCUtils.getAppId(context) + SEND_API_POST;
        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(sendApi).method("POST");
        requestBuilder.addHeader("Authorization", accessToken);
        requestBuilder.requestBody(new RequestBody() {
            @Override
            public String contentType() {
                return "application/json";
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                outputStream.write(msgContent.getBytes(Charset.defaultCharset()));
                outputStream.flush();
            }
        });

        try {
            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
            if (response.getCode() == 200) {
                InputStream is = response.getBody().getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    message.write(buffer, 0, len);
                }
                is.close();
                message.close();
                return new String(message.toByteArray(), Charset.defaultCharset());
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }
}
