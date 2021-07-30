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

package com.huawei.industrydemo.news.page.activity.news;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.databinding.ActivityNewsCoverBinding;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.repository.NewsRepository;
import com.huawei.industrydemo.news.utils.ContextUtil;
import com.huawei.industrydemo.news.utils.richtext.RealPathFromUriUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 * adb shell am start -n com.huawei.industrydemo.news/.page.activity.news.NewsCoverActivity
 */
public class NewsCoverActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = NewsCoverActivity.class.getSimpleName();

    private static final String KET_NEWS = "KET_NEWS";
    private static final int IMAGE_PICK = 104;

    ActivityNewsCoverBinding binding;

    private int type;

    private News news;

    /**
     * start NewsCoverActivity
     *
     * @param news news
     */
    public static void start(News news) {
        Intent starter = new Intent(ContextUtil.context(), NewsCoverActivity.class);
        starter.putExtra(KET_NEWS, news);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextUtil.context().startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_cover);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_cover);
        initView();

        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            news = (News) intent.getSerializableExtra(KET_NEWS);
            if (news != null) {
                binding.title.setText(news.getTitle());
                binding.publisher.setText(news.getPublisher());
            }
        }
    }

    private void initView() {

        binding.grounp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.pictureNo:
                        binding.add.setVisibility(View.GONE);
                        type = BaseType.PICTURE_0;
                        break;
                    case R.id.pictureOne: // picture One
                    default:
                        type = BaseType.PICTURE_ONE_SMALL;
                        binding.add.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if (ActivityCompat.checkSelfPermission(NewsCoverActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NewsCoverActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectImage(IMAGE_PICK);
                } else {
                    Toast.makeText(NewsCoverActivity.this, "Need storage permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.publish:
                if (news != null) {
                    news.setType(type);
                    Toast.makeText(this, getResources().getString(R.string.publish_success), Toast.LENGTH_SHORT).show();
                    new NewsRepository().insert(news);
                    EventBus.getDefault().post(BaseType.EVENT_REFRESH_DATA);
                    Log.d(TAG, "onClick:getAll news " + new NewsRepository().getAll());
                }
                finish();
                break;
            default:
                Log.d(TAG, "onClick: ");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: 1");
        if (requestCode == IMAGE_PICK) {
            String imgPath = "";
            if (data != null) {
                String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                Log.e(TAG, realPathFromUri);
                imgPath = realPathFromUri;
                binding.tvAdd.setVisibility(View.GONE);
                Glide.with(NewsCoverActivity.this).load(imgPath).into(binding.ivAdd);
                news.setImg1(imgPath);
            } else {
                Toast.makeText(this, "Choose image again", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "onActivityResult 2: " + imgPath);
        }
    }

    /**
     * selectImage
     *
     * @param requestCode requestCode
     */
    public void selectImage(int requestCode) {
        Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
        intent1.setType("image/*");
        intent1.putExtra("crop", true);
        intent1.putExtra("scale", true);
        startActivityForResult(intent1, requestCode);
    }

}
