/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.industrydemo.news.page.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzer;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerFactory;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerSetting;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionResult;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.utils.ContextUtil;

import static com.huawei.industrydemo.news.utils.hms.AnalyticsUtil.superResultionReport;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/24]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class ImageSuperActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ImageSuperActivity.class.getSimpleName();
    private MLImageSuperResolutionAnalyzer analyzer;
    private static final int INDEX_1X = 0;
    private static final int INDEX_3X = 1;
    private static final int INDEX_ORIGINAL = 2;
    private ImageView imageView;
    private TextView clickView;
    private Bitmap srcBitmap;
    private int selectItem = INDEX_1X;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int superImageId = R.mipmap.superresolution_image;
        Intent intent = getIntent();
        if (intent != null) {
            superImageId = intent.getIntExtra("Image_Id",R.mipmap.superresolution_image);
        }

        setContentView(R.layout.activity_image_super_resolution);
        imageView = findViewById(R.id.image);
        clickView = findViewById(R.id.clickme);
        srcBitmap = BitmapFactory.decodeResource(getResources(), superImageId);
        findViewById(R.id.button_1x).setOnClickListener(this);
        findViewById(R.id.button_3x).setOnClickListener(this);
        findViewById(R.id.button_original).setOnClickListener(this);
        findViewById(R.id.lOut).setOnClickListener(this);
        findViewById(R.id.image).setOnClickListener(this);
        createAnalyzer(selectItem);
        detectImage(INDEX_ORIGINAL);

    }

    public static void start(int imageId) {
        Intent starter = new Intent(ContextUtil.context(), ImageSuperActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra("Image_Id",imageId);
        ContextUtil.context().startActivity(starter);
        superResultionReport();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_1x:
                detectImage(INDEX_1X);
                break;
            case R.id.button_3x:
                detectImage(INDEX_3X);
                break;
            case R.id.button_original:
                detectImage(INDEX_ORIGINAL);
                break;
            case R.id.lOut:
            case R.id.image:
                detectImage(INDEX_3X);
                findViewById(R.id.image).setOnClickListener(v -> finish());
                break;
            default:
                onBackPressed();
                break;
        }
    }

    private void release() {
        if (analyzer == null) {
            return;
        }
        analyzer.stop();
    }

    private void detectImage(int type) {
        if (type == INDEX_ORIGINAL) {
            setImage(srcBitmap);
            return;
        }

        // The analyzer only supports a single instance.
        // If you want to switch to a different scale, you need to release the model and recreate it.
        if (type != selectItem) {
            release();
            createAnalyzer(type);
        }

        if (analyzer == null) {
            return;
        }
        selectItem = type;

        // Create an MLFrame by using the bitmap.
        MLFrame frame = MLFrame.fromBitmap(srcBitmap);
        Task<MLImageSuperResolutionResult> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLImageSuperResolutionResult>() {
            public void onSuccess(MLImageSuperResolutionResult result) {
                clickView.setVisibility(View.GONE);

                // Recognition success.
                setImage(result.getBitmap());
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                // Recognition failure.
                Log.e(TAG, e.getMessage());
                Toast.makeText(getApplicationContext(), "Failed：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImage(final Bitmap bitmap) {
        ImageSuperActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    private void createAnalyzer(int type) {
        if (type == INDEX_1X) {
            // Method 1: use the default setting, that is, 1x image super resolution.
            analyzer = MLImageSuperResolutionAnalyzerFactory.getInstance().getImageSuperResolutionAnalyzer();
        } else {
            // Method 2: using the custom setting.
            MLImageSuperResolutionAnalyzerSetting setting = new MLImageSuperResolutionAnalyzerSetting.Factory()
                    // set the scale of image super resolution to 3x.
                    .setScale(MLImageSuperResolutionAnalyzerSetting.ISR_SCALE_3X)
                    .create();
            analyzer = MLImageSuperResolutionAnalyzerFactory.getInstance().getImageSuperResolutionAnalyzer(setting);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (srcBitmap != null) {
            srcBitmap.recycle();
        }
        release();
    }
}