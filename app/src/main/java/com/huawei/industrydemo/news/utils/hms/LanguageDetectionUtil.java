/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.industrydemo.news.utils.hms;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetector;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;

/**
 * Language Detection
 * 
 * @version [News-Demo 2.0.0.300, 2021/6/3]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class LanguageDetectionUtil {

    private static final String TAG = LanguageDetectionUtil.class.getSimpleName();

    private static MLRemoteLangDetector mlRemoteLangDetector;

    /**
     * Language Detection init
     *
     * @param context Context
     */
    public static void init(Context context) {
        MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(context));

        // Method 1: Use default parameter settings to create a language detector.
        mlRemoteLangDetector = MLLangDetectorFactory.getInstance().getRemoteLangDetector();
    }

    /**
     * start detect
     * 
     * @param context Context
     * @param sourceText String
     * @param onSuccessListener Processing logic for detection success.
     */
    public static void detect(Context context, String sourceText, OnSuccessListener<String> onSuccessListener) {
        // Method 2: Return the code of the language with the highest confidence. sourceText indicates the text to be
        // detected, with up to 5000 characters.
        Task<String> firstBestDetectTask = mlRemoteLangDetector.firstBestDetect(sourceText);
        firstBestDetectTask.addOnSuccessListener(onSuccessListener).addOnFailureListener(exception -> {
            // Processing logic for detection failure.
            // Recognition failure.
            if (exception instanceof MLException) {
                MLException mlException = (MLException) exception;
                // Obtain the result code. You can process the result code and customize respective messages
                // displayed to users.
                int errorCode = mlException.getErrCode();
                // Obtain the error information. You can quickly locate the fault based on the result code.
                String errorMessage = mlException.getMessage();
                Log.e(TAG, errorCode + ", " + errorMessage, exception);
            } else {
                // Handle the conversion error.
                Log.e(TAG, exception.getMessage(), exception);
            }
            AgcUtil.reportException(TAG, exception);
            String msg = "Language detect failed: " + exception.getLocalizedMessage();
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Language Detection stop
     */
    public static void stop() {
        if (mlRemoteLangDetector != null) {
            mlRemoteLangDetector.stop();
        }
    }
}
