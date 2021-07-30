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

package com.huawei.industrydemo.news.utils.richtext;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class RichUtils {

    private static final String TAG = RichUtils.class.getSimpleName();

    /**
     * returnImageUrlsFromHtml
     *
     * @param content content
     * @return pic urls of the rich text
     */
    public static ArrayList<String> returnImageUrlsFromHtml(String content) {
        ArrayList<String> imageSrcList = new ArrayList<String>();
        if (TextUtils.isEmpty(content)) {
            return imageSrcList;
        }
        String htmlCode = content;
        Pattern p = Pattern.compile(
            "<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>",
            Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            imageSrcList.add(src);
        }

        return imageSrcList;
    }

    /**
     * returnOnlyText
     *
     * @param htmlStr htmlStr
     * @return text of RichText
     */
    public static String returnOnlyText(String htmlStr) {
        if (TextUtils.isEmpty(htmlStr)) {
            return "";
        } else {
            String regFormat = "\\s*|\t|\r|\n";
            String regTag = "<[^>]*>";
            return htmlStr.replaceAll("&nbsp;", " ")
                    .replaceAll(regTag, "");
        }
    }

    /**
     * isEmpty
     *
     * @param htmlStr richText
     * @return isEmpty
     */
    public static boolean isEmpty(String htmlStr) {
        ArrayList<String> images = returnImageUrlsFromHtml(htmlStr);
        String text = returnOnlyText(htmlStr);
        return TextUtils.isEmpty(text) && images.size() == 0;
    }
}
