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

import com.google.gson.Gson;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class JsonUtil {
    private static final String TAG = JsonUtil.class.getSimpleName();

    /**
     * Obtain JSON string from the input stream.
     * 
     * @param inputStream InputStream
     * @return String
     */
    public static String getJson(InputStream inputStream) {
        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            AgcUtil.reportException(TAG, e);
        }
        return jsonString.toString();
    }

    /**
     * JSONToObject
     * 
     * @param json string
     * @param type Class name
     * @return Object of Class
     * @throws ClassNotFoundException exception
     */
    public static <T> T jsonToObject(String json, Class<T> type) throws ClassNotFoundException {
        Gson gson = new Gson();
        String className = type.getName();
        if (!className.equals(News.class.getName())) {
            throw new ClassNotFoundException(className);
        }
        return gson.fromJson(json, type);
    }
}
