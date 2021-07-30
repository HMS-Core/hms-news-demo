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

package com.huawei.industrydemo.news.utils.storage;

import android.util.Log;

import com.huawei.agconnect.cloud.storage.core.DownloadTask;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.utils.JsonUtil;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;


public class NewsRequestUtil extends StorageUtil {
    private final String TAG = this.getClass().getSimpleName();

    public NewsRequestUtil() {
        super();
    }

    @Override
    public void downloadSuccess(String downloadFilePath, DownloadTask.DownloadResult downloadResult,
        int downLoadCount) {
        Log.d(TAG, "downloadFile: " + downloadFilePath);
        String result = getDataByFile(downloadFilePath);
        Log.d(TAG, "downloadFile: " + result);
        try {
            if (downloadFilePath.contains(".json")) {
                News news = JsonUtil.jsonToObject(result, News.class);
                Log.w(TAG, "downloadFile:json " + news.toString());
                datalistenTrig(news, downLoadCount);
            } else {
                Log.e(TAG, "downloadFile:file path not .json: " + result);
            }
        } catch (ClassNotFoundException e) {
            AgcUtil.reportException(TAG, e);
        }
    }

}
