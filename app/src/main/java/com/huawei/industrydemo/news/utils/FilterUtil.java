/*
 *
 *  *     Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *
 *
 */

package com.huawei.industrydemo.news.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.huawei.hms.image.vision.ImageVision;
import com.huawei.hms.image.vision.ImageVisionImpl;
import com.huawei.hms.image.vision.bean.ImageVisionResult;
import com.huawei.secure.android.common.util.LogsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version [News-Demo 2.0.0.300, 2021/6/8]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class FilterUtil {
    public static final String TAG = "FilterActivity";

    private static volatile FilterUtil instance;

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    ImageVisionImpl imageVisionFilterAPI;


    public static FilterUtil getInstance() {
        if (instance == null) {
            synchronized (MemberUtil.class) {
                if (instance == null) {
                    instance = new FilterUtil();
                }
            }
        }
        return instance;
    }

    public void initFilter(final Context context, JSONObject authJson) {
        imageVisionFilterAPI = ImageVision.getInstance(context);
        imageVisionFilterAPI.setVisionCallBack(new ImageVision.VisionCallBack() {
            @Override
            public void onSuccess(int successCode) {
                imageVisionFilterAPI.init(context, authJson);
            }

            @Override
            public void onFailure(int errorCode) {
                LogsUtil.e(TAG, "ImageVisionAPI fail, errorCode: " + errorCode);
            }
        });
    }

    public void startFilter(Bitmap bitmap, ImageView imageView, final String filterType, final JSONObject authJson) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                JSONObject taskJson = new JSONObject();
                try {
                    taskJson.put("intensity", "1");
                    taskJson.put("filterType", filterType);
                    taskJson.put("compressRate", "1");
                    jsonObject.put("requestId", "112312");
                    jsonObject.put("taskJson", taskJson);
                    jsonObject.put("authJson", authJson);
                    final ImageVisionResult visionResult = imageVisionFilterAPI.getColorFilter(jsonObject, bitmap);
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap image = visionResult.getImage();
                            imageView.setImageBitmap(image);
                        }
                    });
                } catch (JSONException e) {
                    LogsUtil.e(TAG, "JSONException: " + e.getMessage());
                }
            }
        };
        executorService.execute(runnable);
    }

}
