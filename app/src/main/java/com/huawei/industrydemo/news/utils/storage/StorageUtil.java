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
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.DownloadTask;
import com.huawei.agconnect.cloud.storage.core.ListResult;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.hmf.tasks.Task;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.utils.ContextUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class StorageUtil {
    private final String TAG = this.getClass().getSimpleName();

    private AGCStorageManagement mAGCStorageManagement;

    private ICloudDataChangedLister iCloudDataChangedLister;

    private int fileCount;

    private  int downLoadCount = 0;

    /**
     * setiNewsChangedLister
     * @param iCloudDataChangedLister INewsChangedLister
     * @return NewsRequestUtil
     */
    public void setiCloudDataChangedLister(ICloudDataChangedLister iCloudDataChangedLister) {
        this.iCloudDataChangedLister = iCloudDataChangedLister;
    }

    /**
     * data listen Trig
     * @param s s
     * @param count  of download
     */
    public void datalistenTrig(Object s, int count) {
        if (iCloudDataChangedLister != null) {
            iCloudDataChangedLister.dataUpdated(s, count);
        }
    }

    /**
     * StorageUtil
     */
    public StorageUtil() {
        initAGCStorageManagement();
    }

    private void initAGCStorageManagement() {
        try{
            mAGCStorageManagement = AGCStorageManagement.getInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "initAGCStorageManagement:mAGCStorageManagement " + mAGCStorageManagement);
    }


    /**
     * getFileList from cloud
     * @param path of cloud
     */
    public void getFileList(String path) {
        Log.d(TAG, "getNewsFromCloud:path "+ path );
        if (!isStorageInitSus()) {
            return;
        }

        downLoadCount = 0;
        StorageReference storageReference = mAGCStorageManagement.getStorageReference(path);
        Task<ListResult> listResultTask = null;
        // TODO: 2021/6/1
        listResultTask = storageReference.listAll();
        try {
            listResultTask
                    .addOnSuccessListener(listResult ->{
                        List<StorageReference> storageReferences = listResult.getFileList();
                        fileCount = storageReferences.size();
                        for (StorageReference storageReference1 :
                                storageReferences) {
                            String storagePath = storageReference1.getPath();
                            Log.w(TAG, "getFileList: " + storagePath);
                            downloadFile(storagePath);
                        }

                    })
                    .addOnFailureListener(e ->
                            e.printStackTrace()
                    );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isStorageInitSus() {
        if (mAGCStorageManagement == null) {
            Toast.makeText(ContextUtil.context(),"Cloud storage data don't find ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * getFileCount
     * @return fileCount of cloud dir
     */
    public int getFileCount() {
        return fileCount;
    }

    /**
     * downloadFile from storage
     *
     * @param filePath filePath of storage
     */
    public void downloadFile(String filePath) {
        if (!isStorageInitSus()) {
            return;
        }
        String fileName = "download_" + System.currentTimeMillis() + getSuffix(filePath);
        String agcSdkDirPath = getAGCSdkDirPath();
        Log.d(TAG, "downloadFile:agcSdkDirPath " + agcSdkDirPath);
        Log.d(TAG, "downloadFile:fileName " + fileName);
        final File file = new File(agcSdkDirPath, fileName);
        String downloadFilePath = "";
        try {
            downloadFilePath = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String path = downloadFilePath;
        StorageReference storageReference = mAGCStorageManagement.getStorageReference(filePath);
        DownloadTask downloadTask = storageReference.getFile(file);
        try {
            downloadTask
                    .addOnSuccessListener(downloadResult -> {
                        {
                            downLoadCount=downLoadCount+1;
                            downloadSuccess(path, downloadResult, downLoadCount);

                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * downloadSuccess
     * @param downloadFilePath downloadFilePath
     * @param downloadResult downloadResult
     * @param downLoadCount downLoadCount
     */
    public void downloadSuccess(String downloadFilePath, DownloadTask.DownloadResult downloadResult, int downLoadCount) {

    }

    private String getAGCSdkDirPath() {
        String path = "";
        try {
            path = ContextUtil.context().getExternalCacheDir().getCanonicalPath() + "/AGCSdk/";
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(path);
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            Log.d(TAG, "getAGCSdkDirPath:  " + isSuccess);
            if (!isSuccess) {
                Log.e(TAG, "getAGCSdkDirPath: Failed ");
            }
        }
        return path;
    }


    /**
     * getSuffix
     * @param originalFilename String originalFilename = "tim.g (1).jpg";//timg (1).jpg
     * @return suffix
     */
    private String getSuffix(String originalFilename) {
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf);
        return suffix;
    }

    /**
     * getDataByFile
     * @param filepath of sdcard
     * @return data of file
     */
    public String getDataByFile(String filepath) {
        String result = "";
        FileInputStream fin = null;
        try {
            File file = new File(filepath);
            int length = (int) file.length();
            byte[] buff = new byte[length];
            fin = new FileInputStream(file);
            int i = fin.read(buff);
            Log.d(TAG, "getDataByFile:i " + i);
            fin.close();
            result = new String(buff, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
