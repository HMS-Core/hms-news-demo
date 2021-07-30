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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.constants.KitConstants;
import com.huawei.industrydemo.news.entity.KitInfo;
import com.huawei.industrydemo.news.viewadapter.KitTipsMapAdapter;

import java.util.HashMap;
import java.util.Map;

import static com.huawei.industrydemo.news.constants.LogConfig.TAG;

/**
 * Util of Showing Used Kits
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class KitTipUtil implements KitConstants {
    // <KitFunction,iconId>
    private final static Map<String, Integer> ICON_MAP;

    // <KitFunction,KitInfo>
    private final static Map<String, KitInfo> KIT_DES_MAP;

    private final static String[] KIT_DEFAULT_COLOR;

    static {
        KIT_DEFAULT_COLOR = new String[]{"#41a1f7", "#64d1f2"};

        ICON_MAP = new HashMap<>();
        ICON_MAP.put(ADS_ADS, R.mipmap.tip_ads);
        ICON_MAP.put(ML_ASR, R.mipmap.tip_ml);
        ICON_MAP.put(ML_TRANSLATION, R.mipmap.tip_ml);
        ICON_MAP.put(ML_IMAGE, R.mipmap.tip_ml);
        ICON_MAP.put(ML_TTS, R.mipmap.tip_ml);
        ICON_MAP.put(ACCOUNT_LOGIN, R.mipmap.tip_account);
        ICON_MAP.put(ANALYTICS_REPORT, R.mipmap.tip_analytics);
        ICON_MAP.put(PUSH_NOTIFY, R.mipmap.tip_push);
        ICON_MAP.put(VIDEO_PLAY, R.mipmap.tip_video);
        ICON_MAP.put(IAP_SUB, R.mipmap.tip_iap);
        ICON_MAP.put(LOCATION_LBS, R.mipmap.tip_kit_defult);

        KIT_DES_MAP = new HashMap<>();
        KIT_DES_MAP.put(ADS_ADS, new KitInfo(ADS, ADS_ADS, R.string.adskit_name, R.string.ads_ads, "", R.string.ads_des,
                R.string.ads_link, "#f89205", "#f30000"));

        KIT_DES_MAP.put(ML_ASR,
                new KitInfo(ML, ML_ASR, R.string.mlkit_name, R.string.ml_asr, "", R.string.ml_asr_des, R.string.ml_link, "#f1a977", "#fe5e9c"));
        KIT_DES_MAP.put(ML_TRANSLATION, new KitInfo(ML, ML_TRANSLATION, R.string.mlkit_name, R.string.ml_translate, "",
                R.string.ml_translation_des, R.string.ml_link, "#f1a977", "#fe5e9c"));
        KIT_DES_MAP.put(ML_IMAGE, new KitInfo(ML, ML_IMAGE, R.string.mlkit_name, R.string.ml_resolution, "",
                R.string.ml_resolution_des, R.string.ml_link, "#f1a977", "#fe5e9c"));
        KIT_DES_MAP.put(ML_TTS, new KitInfo(ML, ML_TTS, R.string.mlkit_name, R.string.ml_tts, "",
                R.string.ml_tts_des, R.string.ml_link, "#f1a977", "#fe5e9c"));

        KIT_DES_MAP.put(ACCOUNT_LOGIN, new KitInfo(ACCOUNT, ACCOUNT_LOGIN, R.string.accountkit_name,
                R.string.account_login, "", R.string.account_login_des, R.string.account_link, "#41a1f7", "#64d1f2"));

        KIT_DES_MAP.put(ANALYTICS_REPORT,
                new KitInfo(ANALYTICS, ANALYTICS_REPORT, R.string.analytics_name, R.string.analytics_report, "",
                        R.string.analytics_report_des, R.string.analytics_link, "#d678ea", "#607ae9"));

        KIT_DES_MAP.put(PUSH_NOTIFY, new KitInfo(PUSH, PUSH_NOTIFY, R.string.push_name, R.string.push_message, "",
                R.string.push_notify_des, R.string.push_link, "#22b2d7", "#3ee0af"));

        KIT_DES_MAP.put(VIDEO_PLAY, new KitInfo(VIDEO, VIDEO_PLAY, R.string.video_name, R.string.video_play, "",
                R.string.video_play_des, R.string.video_link, "#f1a977", "#fe5e9c"));

        KIT_DES_MAP.put(IAP_SUB, new KitInfo(IAP, IAP_SUB, R.string.iap_name, R.string.member_purchase, "",
                R.string.iap_sub_des, R.string.iap_link, "#22b2d7", "#3ee0af"));

        KIT_DES_MAP.put(LOCATION_LBS, new KitInfo(LOCATION, LOCATION_LBS, R.string.location_name, R.string.location_lbs,
                "", R.string.location_lbs_des, R.string.location_link, "#22b2d7", "#3ee0af"));

    }

    /**
     * getKitMap by String[]
     *
     * @param params kits
     * @return Map<String, KitInfo>
     */
    public static Map<String, KitInfo> getKitMap(String... params) {
        Map<String, KitInfo> map = new HashMap<>();
        for (String kitFunction : params) {
            map.put(kitFunction, KIT_DES_MAP.get(kitFunction));
        }
        return map;
    }

    /**
     * addTipView
     *
     * @param activity activity
     * @param kits     kits
     */
    public static void addTipView(Activity activity, Map<String, KitInfo> kits) {

        if (kits == null || kits.size() == 0) {
            Log.d(TAG, " kits.size() == 0");
            return;
        }

        FrameLayout frameLayout = activity.findViewById(android.R.id.content);

        final View view = LayoutInflater.from(activity).inflate(R.layout.view_tip, null);

        view.setOnClickListener(v -> view.setVisibility(View.GONE));

        RecyclerView recyclerView = view.findViewById(R.id.rv_tips);
        KitTipsMapAdapter kitTipsAdapter = new KitTipsMapAdapter(kits, activity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(kitTipsAdapter);

        frameLayout.addView(view);
    }

    public static Map<String, Integer> getIconMap() {
        return ICON_MAP;
    }

    public static Map<String, KitInfo> getKitDesMap() {
        return KIT_DES_MAP;
    }
}
