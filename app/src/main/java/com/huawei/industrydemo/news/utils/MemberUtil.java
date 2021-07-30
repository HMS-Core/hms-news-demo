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

package com.huawei.industrydemo.news.utils;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.hms.network.httpclient.HttpClient;
import com.huawei.hms.network.httpclient.Request;
import com.huawei.hms.network.httpclient.RequestBody;
import com.huawei.hms.network.httpclient.Response;
import com.huawei.hms.network.httpclient.ResponseBody;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.inteface.MemberCheckCallback;
import com.huawei.industrydemo.news.inteface.MemberRefundListener;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;
import com.huawei.industrydemo.news.utils.agc.RemoteConfigUtil;
import com.huawei.industrydemo.news.utils.hms.AnalyticsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.huawei.hms.analytics.type.HAEventType.UPDATEMEMBERSHIPLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.CURRVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.PREVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.REASON;
import static com.huawei.industrydemo.news.constants.LogConfig.TAG;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class MemberUtil {
    private static volatile MemberUtil instance;

    private static final int TYPE_SUBSCRIBED_PRODUCT = 2;

    private boolean isMemberChecked = false;

    private boolean isChecking = false;

    private MemberUtil() {
    }

    public static MemberUtil getInstance() {
        if (instance == null) {
            synchronized (MemberUtil.class) {
                if (instance == null) {
                    instance = new MemberUtil();
                }
            }
        }
        return instance;
    }

    /**
     * isMember
     *
     * @param activity activity
     * @param user user
     * @param memberCheckCallback memberCheckCallback
     */
    public void isMember(Activity activity, User user, MemberCheckCallback memberCheckCallback) {
        if (isChecking) {
            Log.d(TAG, "isMember is Checking");
            return;
        }
        isChecking = true;
        Log.d(TAG, "isMember START");
        OwnedPurchasesReq getPurchaseReq = new OwnedPurchasesReq();
        getPurchaseReq.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        getPurchaseReq.setContinuationToken("");
        Iap.getIapClient(activity).obtainOwnedPurchaseRecord(getPurchaseReq).addOnSuccessListener(result -> {
            List<String> list = result.getPlacedInappPurchaseDataList();
            if (list == null || list.size() == 0) {
                list = result.getInAppPurchaseDataList();
            }
            if (list == null || list.size() == 0) {
                isChecking = false;
                isMemberChecked = true;
                if (memberCheckCallback != null) {
                    Log.d(TAG, "not has data");
                    setUserMemberInfo(user, false, false, 0);

                    memberCheckCallback.onResult(false, false, "", "");
                }
                return;
            }
            for (String item : list) {
                try {
                    InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(item);
                    boolean subIsvalid = inAppPurchaseData.isSubValid();
                    if (subIsvalid) {
                        isMemberChecked = true;
                        String productName = inAppPurchaseData.getProductName();
                        long expirationDate = inAppPurchaseData.getExpirationDate();
                        boolean isAutoRenewing = inAppPurchaseData.isAutoRenewing();
                        String time =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).format(expirationDate);
                        if (memberCheckCallback != null) {
                            memberCheckCallback.onResult(subIsvalid, isAutoRenewing, productName, time);
                        }
                        setUserMemberInfo(user, isAutoRenewing, true, expirationDate);
                        Log.d(TAG, "is Member");
                        isChecking = false;
                        return;
                    }
                } catch (JSONException e) {
                    AgcUtil.reportException(TAG, e);
                }
            }

            /* If member relation expired, it needs to be reported */
            if (isMember(user)) {
                Bundle bundle = new Bundle();
                bundle.putString(PREVLEVEL, "Member");
                bundle.putString(CURRVLEVEL, "Non-Member");
                bundle.putString(REASON, "Member Purchase");
                AnalyticsUtil.getInstance(activity).onEvent(UPDATEMEMBERSHIPLEVEL, bundle);
            }
            isMemberChecked = true;
            isChecking = false;
            Log.d(TAG, "is not Member");
            setUserMemberInfo(user, false, false, 0);
            if (memberCheckCallback != null) {
                memberCheckCallback.onResult(false, false, "", "");
            }

        }).addOnFailureListener(e -> {
            isMemberChecked = true;
            isChecking = false;
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Log.d(TAG, "Status:" + apiException.getStatus());
                Log.d(TAG, "returnCode:" + apiException.getStatusCode());
            } else {
                Log.d(TAG, "failure:" + e.toString());
                AGConnectCrash.getInstance().recordException(e);
            }
            Log.d(TAG, "isMember FAILD");
            if (memberCheckCallback != null) {
                memberCheckCallback.onResult(false, false, "", "");
            }
        });
    }

    private void setUserMemberInfo(User user, boolean isAutoRenewing, boolean isMember, long expirationDate) {
        if (user != null) {
            UserRepository userRepository = new UserRepository();
            user.setAutoRenewing(isAutoRenewing);
            user.setExpirationDate(expirationDate);
            user.setMember(isMember);
            userRepository.setCurrentUser(user);
        }
    }

    /**
     * isMember
     *
     * @param user user
     * @return is Member:true is not Member:false
     */
    public boolean isMember(User user) {
        return user != null && user.isMember() && (user.isAutoRenewing()
                || (!user.isAutoRenewing() && user.getExpirationDate() > System.currentTimeMillis()));
    }

    /**
     * refund by IAP server API
     *
     * @param activity activity
     * @param subscriptionId subscriptionId
     * @param purchaseToken purchaseToken
     * @param memberRefundListener Listener
     */
    public void refund(Activity activity, String subscriptionId, String purchaseToken,
        MemberRefundListener memberRefundListener) {
        new Thread(() -> {
            try {
                String msg;
                String clientId = AgcUtil.getClientId(activity);
                String clientSecret = RemoteConfigUtil.getAppSecret();
                msg = getAccessToken(activity.getString(R.string.app_at_url), clientId, clientSecret);
                if (!TextUtils.isEmpty(msg)) {
                    if (msg.contains("access_token")) {
                        JSONObject accessJson = new JSONObject(msg);
                        String accessToken =
                            "APPAT:" + accessJson.get("access_token");
                        Log.d(TAG, "accessToken:" + accessToken);
                        accessToken =
                            Base64.getEncoder().encodeToString(accessToken.getBytes(Charset.defaultCharset()));
                        Log.d(TAG, "accessToken:" + accessToken);
                        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
                        Request.Builder requestBuilder =
                            httpClient.newRequest().url(activity.getString(R.string.member_refund_url)).method("POST");
                        requestBuilder.addHeader("Authorization", "Basic " + accessToken);
                        requestBuilder.requestBody(new RequestBody() {
                            @Override
                            public String contentType() {
                                return "application/json; charset=UTF-8";
                            }

                            @Override
                            public void writeTo(OutputStream outputStream) throws IOException {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("purchaseToken", purchaseToken);
                                jsonObject.addProperty("subscriptionId", subscriptionId);
                                outputStream.write(jsonObject.toString().getBytes(Charset.defaultCharset()));
                                outputStream.flush();
                            }
                        });

                        String res;
                        try {
                            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
                            if (response.getCode() == 200) {
                                InputStream is = response.getBody().getInputStream();
                                ByteArrayOutputStream message = new ByteArrayOutputStream();
                                int len;
                                byte[] byteBuffer = new byte[1024];
                                while ((len = is.read(byteBuffer)) != -1) {
                                    message.write(byteBuffer, 0, len);
                                }
                                is.close();
                                message.close();
                                res = new String(message.toByteArray(), Charset.defaultCharset());
                                if(memberRefundListener!=null){
                                    memberRefundListener.onSuccess(res);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (memberRefundListener != null) {
                                memberRefundListener.onFailed(e.toString());
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (memberRefundListener != null) {
                    memberRefundListener.onFailed(e.toString());
                }
            }
        }).start();
    }

    public String getAccessToken(String atUrl, String appId, String secretKey) {
        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(atUrl).method("POST");
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
                params.put("client_secret", secretKey);
                params.put("grant_type", "client_credentials");
                StringBuilder buffer = new StringBuilder();
                if (!params.isEmpty()) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        buffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append("&");
                    }
                }
                // delete the last ‘&‘
                buffer.deleteCharAt(buffer.length() - 1);
                byte[] data = buffer.toString().getBytes(Charset.defaultCharset());

                outputStream.write(data, 0, data.length);
                outputStream.flush();
            }
        });

        String msg = "";
        try {
            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
            if (response.getCode() == 200) {
                InputStream is = response.getBody().getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len;
                byte[] byteBuffer = new byte[1024];
                while ((len = is.read(byteBuffer)) != -1) {
                    message.write(byteBuffer, 0, len);
                }
                is.close();
                message.close();
                msg = new String(message.toByteArray(), Charset.defaultCharset());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "accessToken:" + msg);
        return msg;
    }

    public boolean isMemberChecked() {
        return isMemberChecked;
    }
}
