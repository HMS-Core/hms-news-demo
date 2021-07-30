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

package com.huawei.industrydemo.news.page.activity.news;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.image.vision.A;
import com.huawei.hms.image.vision.crop.CropLayoutView;
import com.huawei.hms.image.vision.sticker.StickerLayout;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.constants.LogConfig;
import com.huawei.industrydemo.news.entity.Filters;
import com.huawei.industrydemo.news.utils.FilterUtil;
import com.huawei.industrydemo.news.utils.MemberUtil;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;
import com.huawei.industrydemo.news.utils.agc.RemoteConfigUtil;
import com.huawei.industrydemo.news.viewadapter.FilterAdapter;
import com.huawei.secure.android.common.util.LogsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.huawei.industrydemo.news.constants.RequestCodeConstants.PERMISSION_REQUEST_CODE;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class EditActivity extends BaseActivity {
    private JSONObject authJson;

    private String inFilePath;

    private String outFilePath;

    private ImageView imageView;

    private StickerLayout mStickerLayout;

    private RelativeLayout stickerScreenshot;

    private CropLayoutView cropLayoutView;

    private RecyclerView recyclerView;

    private RadioGroup mEditRadioGroup;

    private Bitmap bitmap;

    private boolean isSaved;

    String rootPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        init();
        getDataFromIntent();

        cropLayoutView = findViewById(R.id.cropImageView);
        mStickerLayout = findViewById(R.id.sticker_container);
        stickerScreenshot = findViewById(R.id.stikcerScreenShot);
        recyclerView = findViewById(R.id.recycler_filter);
        imageView = findViewById(R.id.image);
        mEditRadioGroup = findViewById(R.id.Edit_radio);
        mEditRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);

        stickerScreenshot.setDrawingCacheEnabled(true);
        stickerScreenshot.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        stickerScreenshot.layout(0, 0, stickerScreenshot.getMeasuredWidth(), stickerScreenshot.getMeasuredHeight());
        stickerScreenshot.buildDrawingCache();

        FilterUtil.getInstance().initFilter(this, authJson);
        getDiskBitmap(inFilePath);
        imageView.setImageBitmap(bitmap);

        findViewById(R.id.save_crop).setVisibility(View.VISIBLE);
        findViewById(R.id.save_sticker).setVisibility(View.GONE);
        findViewById(R.id.save).setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        cropLayoutView.setVisibility(View.VISIBLE);
        Bitmap croppedImage = ((BitmapDrawable) (imageView).getDrawable()).getBitmap();
        imageView.setVisibility(View.GONE);
        findViewById(R.id.sticker_all).setVisibility(View.GONE);
        cropLayoutView.setImageBitmap(croppedImage);
    }

    private void init() {
        try {
            rootPath = getBaseContext().getFilesDir().getPath() + "/vgmap/";
            initStickerData();
        } catch (Exception e) {
            LogsUtil.e(TAG, "Exception: " + e.getMessage());
        }
        new Thread(() -> {
            try {
                authJson = new JSONObject();
                authJson.put("projectId", AgcUtil.getProjectId(EditActivity.this));
                authJson.put("appId", AgcUtil.getAppId(EditActivity.this));
                authJson.put("authApiKey", AgcUtil.getApiKey(EditActivity.this));
                authJson.put("clientSecret", AgcUtil.getClientSecret(EditActivity.this));
                authJson.put("clientId", AgcUtil.getClientId(EditActivity.this));

                String clientSecret = RemoteConfigUtil.getAppSecret();
                String accessTokenJson =
                        MemberUtil.getInstance()
                                .getAccessToken(
                                        getString(R.string.app_at_url),
                                        AgcUtil.getAppId(EditActivity.this),
                                        clientSecret);
                String accessToken = new JSONObject(accessTokenJson).getString("access_token");
                authJson.put("token", accessToken);
                Log.d(TAG, authJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        })
                .start();
    }

    private void initStickerData() {
        int permissionCheck =
                ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            copyAssetsFileToDirs(getBaseContext(), "vgmap", rootPath);
        } else {
            ActivityCompat.requestPermissions(
                    EditActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * copyAssetsFileToDirs
     *
     * @param context context
     * @param oldPath oldPath
     * @param newPath newPath
     * @return boolean
     */
    public static boolean copyAssetsFileToDirs(Context context, String oldPath, String newPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                if (file.exists() || file.mkdirs()) {
                    for (String fileName : fileNames) {
                        copyAssetsFileToDirs(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                    }
                }
            } else {
                is = context.getAssets().open(oldPath);
                fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }
        return true;
    }

    private Bitmap getDiskBitmap(String pathString) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            File file = new File(pathString);
            if (file.exists()) {
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(pathString, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        inFilePath = intent.getStringExtra(ImagePicker.EXTRA_CROP_FILE_PATH_IN);
        Log.d(TAG, "getDataFromIntent:inFilePath " + inFilePath);
        outFilePath = intent.getStringExtra(ImagePicker.EXTRA_CROP_FILE_PATH_OUT);
        Log.d(TAG, "getDataFromIntent:outFilePath " + outFilePath);
    }

    /**
     * set Result Uri
     *
     * @param outPath set the uri to last activity
     */
    protected void setResultUri(String outPath) {
        setResult(RESULT_OK, new Intent().putExtra(ImagePicker.EXTRA_OUTPUT_URI, outPath));
    }

    /**
     * button onClick
     *
     * @param view view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (isSaved == false) {
                    setResultUri(inFilePath);
                } else {
                    String res = save();
                    if ("".equals(res)) {
                        setResultUri(inFilePath);
                    } else {
                        outFilePath = res;
                        setResultUri(outFilePath);
                    }

                }
                finish();
                break;
            case R.id.save_crop:
                Bitmap croppedImage = cropLayoutView.getCroppedImage();
                cropLayoutView.setImageBitmap(croppedImage);
                bitmap = croppedImage;
                imageView.setImageBitmap(croppedImage);
                outFilePath = save();
                break;
            case R.id.save_sticker:
                List<A> stickerList = mStickerLayout.getStickerList();
                if (stickerList != null) {
                    for (int i = 0; i < stickerList.size(); i++) {
                        stickerList.get(i).setFocus(false);
                    }
                }
                mStickerLayout.invalidate();
                Bitmap bitmapSticker = getViewBitmap(stickerScreenshot);
                if (bitmapSticker != null) {
                    bitmap = bitmapSticker;
                }
                imageView.setImageBitmap(bitmap);
                outFilePath = save();
                break;
            case R.id.save:
                outFilePath = save();
                break;
            case R.id.art1:
                addSticker(rootPath + "textArt1", "");
                break;
            case R.id.art2:
                addSticker(rootPath + "sticker3", "sticker_14_editable.png");
                break;
            case R.id.art3:
                addSticker(rootPath + "sticker3", "sticker_10_editable.png");
                break;
            case R.id.art4:
                addSticker(rootPath + "sticker3", "sticker_13_editable.png");
                break;
            default:
                break;
        }
    }

    private Bitmap getViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        return Bitmap.createBitmap(cacheBitmap);
    }

    private int addSticker(String rootPath, String fileName) {
        return mStickerLayout.addSticker(rootPath, fileName);
    }

    File filePic;

    private String save() {
        isSaved = true;
        FileOutputStream fos = null;
        try {
            filePic = new File(outFilePath);
            fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            Log.d("IOException", ioException.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return filePic.getCanonicalPath();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return "";
    }

    private final RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener =
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Log.d(LogConfig.TAG, "onCheckedChanged");
                    switch (checkedId) {
                        case R.id.filter:
                            findViewById(R.id.save_crop).setVisibility(View.GONE);
                            findViewById(R.id.save_sticker).setVisibility(View.GONE);
                            findViewById(R.id.save).setVisibility(View.VISIBLE);
                            startView();
                            break;
                        case R.id.crop:
                            imageView.setVisibility(View.GONE);
                            findViewById(R.id.save_crop).setVisibility(View.VISIBLE);
                            findViewById(R.id.save_sticker).setVisibility(View.GONE);
                            findViewById(R.id.save).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            cropLayoutView.setVisibility(View.VISIBLE);
                            Bitmap croppedImage = ((BitmapDrawable) (imageView).getDrawable()).getBitmap();
                            findViewById(R.id.sticker_all).setVisibility(View.GONE);
                            cropLayoutView.setImageBitmap(croppedImage);
                            break;
                        case R.id.sticker:
                            findViewById(R.id.save_crop).setVisibility(View.GONE);
                            findViewById(R.id.save_sticker).setVisibility(View.VISIBLE);
                            findViewById(R.id.save).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            cropLayoutView.setVisibility(View.GONE);
                            findViewById(R.id.sticker_all).setVisibility(View.VISIBLE);
                            mStickerLayout.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            };

    private void startView() {
        List<Filters> list = new ArrayList<>();
        Filters origin = new Filters(getString(R.string.filter_type_1), R.drawable.origin);
        list.add(origin);
        Filters black = new Filters(getString(R.string.filter_type_2), R.drawable.black);
        list.add(black);
        Filters gray = new Filters(getString(R.string.filter_type_3), R.drawable.gray);
        list.add(gray);
        Filters lazy = new Filters(getString(R.string.filter_type_4), R.drawable.lazy);
        list.add(lazy);
        Filters littleBlue = new Filters(getString(R.string.filter_type_5), R.drawable.little_blue);
        list.add(littleBlue);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setVisibility(View.VISIBLE);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        FilterAdapter adapter = new FilterAdapter(list, this, imageView, bitmap, authJson);

        findViewById(R.id.sticker_all).setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        cropLayoutView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
