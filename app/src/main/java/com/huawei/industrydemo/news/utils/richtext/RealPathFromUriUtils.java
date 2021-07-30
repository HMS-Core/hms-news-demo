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

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class RealPathFromUriUtils {
    /**
     * getRealPath
     *
     * @param context context
     * @param uri     uri
     * @return Path
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * The absolute path for obtaining images based on the URI is applicable to API 19 or earlier.
     *
     * @param context context object
     * @param uri     picture's uri
     * @return If the image corresponding to the URI exists, the absolute path of the image is returned. Otherwise, null is returned.
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * Applies to API 19 or later and obtains the absolute path of images based on the URI.
     *
     * @param context c
     * @param uri     Uri of pic
     * @return If the image corresponding to the URI exists, the absolute path of the image is returned. Otherwise, null is returned.
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // If the URI is of the document type, the document ID is used for processing.
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // split the char ':'
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = Uri.parse("content://downloads/public_downloads/" + Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // If is content  Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // If the URI is of the file type, directly obtain the path corresponding to the image.
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * Obtains the _data column in the database table, that is, returns the file path corresponding to the URI.
     *
     * @param context context
     * @param uri uri
     * @param selection selection
     * @param selectionArgs selectionArgs
     * @return path
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(columnIndex);
        }
        if (cursor != null) {
            cursor.close();
        }
        return path;
    }

    /**
     * isMediaDocument
     *
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * isDownloadsDocument
     *
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}
