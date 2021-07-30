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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.databinding.ActivityPublishBinding;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.richtext.KeyBoardUtils;
import com.huawei.industrydemo.news.utils.richtext.RealPathFromUriUtils;
import com.huawei.industrydemo.news.utils.richtext.RichUtils;
import com.huawei.industrydemo.news.wight.RichEditor;
import com.huawei.industrydemo.news.wight.popup.CommonPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 *        adb shell am start -n com.huawei.industrydemo.news/.page.activity.news.NewsPublishActivity
 */
public class NewsPublishActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = NewsPublishActivity.class.getSimpleName();

    ActivityPublishBinding binding;

    private static final String[] APP_PERMISSIONS =
        {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    private CommonPopupWindow popupWindowEditImage;

    private String currentUrl = "";

    private static final int EDIT_NORMAL = 0;

    private static final int EDIT_RE = 1;

    private int editMode;

    private static final int REQUEST_PERMISSIONS_CODE = 1000;

    private User user;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        Log.d(TAG, "onMessageEvent:event " + event);
        if (BaseType.EVENT_REFRESH_DATA.equals(event)) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: 1");
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_publish);
        editMode = getIntent().getIntExtra("isFrom", EDIT_NORMAL);
        binding.setOnClickListener(this);

        initPop();
        initEditor();
        if (editMode == EDIT_RE) {
            SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
            String title = sharedPreferences.getString("title", "title");
            String content = sharedPreferences.getString("content", "");
            binding.editName.setText(title);
            binding.richEditor.setHtml(content);
        }

        UserRepository userRepository = new UserRepository();
        user = userRepository.getCurrentUser();
        requestPermission(APP_PERMISSIONS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void requestPermission(String[] permission) {
        boolean isPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                isPermission = true;
                Log.d(TAG, "requestPermission: ");
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, REQUEST_PERMISSIONS_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, permission)) {
                ActivityCompat.requestPermissions(this, permission, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            isPermission = true;
        }
        Log.w(TAG, "requestPermission: " + isPermission);
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initEditor() {
        binding.richEditor.setEditorFontSize(18);
        binding.richEditor.setEditorFontColor(getResources().getColor(R.color.black1b));
        binding.richEditor.setEditorBackgroundColor(Color.WHITE);
        binding.richEditor.setPadding(10, 10, 10, 10);
        binding.richEditor.setPlaceholder(getString(R.string.begin_rich_text));

        binding.txtPublish.setClickable(false);
        binding.txtPublish.setOnClickListener(null);

        binding.richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                Log.e(TAG, "rich text change>>> " + text);
                checkEmpty();
            }
        });

        binding.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEmpty();

            }
        });

        binding.richEditor.setOnDecorationChangeListener(new RichEditor.OnDecorationStateListener() {
            @Override
            public void onStateChangeListener(String text, List<RichEditor.Type> types) {
                ArrayList<String> flagArr = new ArrayList<>();
                for (int i = 0; i < types.size(); i++) {
                    flagArr.add(types.get(i).name());
                }

                if (flagArr.contains("BOLD")) {
                    binding.buttonBold.setImageResource(R.mipmap.rich_bold_);
                } else {
                    binding.buttonBold.setImageResource(R.mipmap.rich_bold);
                }

                if (flagArr.contains("UNDERLINE")) {
                    binding.buttonUnderline.setImageResource(R.mipmap.rich_underline_);
                } else {
                    binding.buttonUnderline.setImageResource(R.mipmap.rich_underline);
                }

                if (flagArr.contains("ORDEREDLIST")) {
                    binding.buttonListUl.setImageResource(R.mipmap.rich_list_ul);
                    binding.buttonListOl.setImageResource(R.mipmap.rich_list_ol_);
                } else {
                    binding.buttonListOl.setImageResource(R.mipmap.rich_list_ol);
                }

                if (flagArr.contains("UNORDEREDLIST")) {
                    binding.buttonListOl.setImageResource(R.mipmap.rich_list_ol);
                    binding.buttonListUl.setImageResource(R.mipmap.rich_list_ul_);
                } else {
                    binding.buttonListUl.setImageResource(R.mipmap.rich_list_ul);
                }

            }
        });

        binding.richEditor.setImageClickListener(new RichEditor.ImageClickListener() {
            @Override
            public void onImageClick(String imageUrl) {
                currentUrl = imageUrl;
                editImage();
            }
        });
    }

    private void checkEmpty() {
        String html = binding.richEditor.getHtml();
        if (!TextUtils.isEmpty(binding.editName.getText().toString().trim()) && html != null
            && !TextUtils.isEmpty(html)) {
            binding.txtPublish.setSelected(true);
            binding.txtPublish.setEnabled(true);
            binding.txtPublish.setClickable(true);
            binding.txtPublish.setOnClickListener(this);
        } else {
            binding.txtPublish.setSelected(false);
            binding.txtPublish.setEnabled(false);
            binding.txtPublish.setClickable(false);
            binding.txtPublish.setOnClickListener(null);
        }
    }

    private final static int IMAGE_CROP = 11;

    private String editUrl;

    private void editImage() {
        if (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // TODO: 2021/5/20
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(ImagePicker.EXTRA_CROP_FILE_PATH_IN, currentUrl);
            String destDir = "";
            try {
                destDir = getFilesDir().getCanonicalPath();
                editUrl = currentUrl;
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileName = "SampleCropImage" + System.currentTimeMillis() + ".jpg";
            intent.putExtra(ImagePicker.EXTRA_CROP_FILE_PATH_OUT, destDir + fileName);
            startActivityForResult(intent, IMAGE_CROP);
            popupWindowEditImage.dismiss();

        } else {
            Toast.makeText(this, "Need storage permission", Toast.LENGTH_SHORT).show();
        }

    }

    private void initPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.newapp_pop_picture, null);
        view.findViewById(R.id.linear_cancle).setOnClickListener(v -> popupWindowEditImage.dismiss());

        view.findViewById(R.id.linear_editor).setOnClickListener(v -> editImage());

        view.findViewById(R.id.linear_delete_pic).setOnClickListener(v -> {
            // delete image
            String removeUrl = "<img src=\"" + currentUrl + "\" alt=\"dachshund\" width=\"100%\"><br>";

            String newUrl = binding.richEditor.getHtml().replace(removeUrl, "");
            currentUrl = "";
            binding.richEditor.setHtml(newUrl);
            if (RichUtils.isEmpty(binding.richEditor.getHtml())) {
                binding.richEditor.setHtml("");
            }
            popupWindowEditImage.dismiss();
        });
        popupWindowEditImage = new CommonPopupWindow.Builder(this).setView(view)
            .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            .setOutsideTouchable(true)
            .create();

        popupWindowEditImage.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                binding.richEditor.setInputEnabled(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_finish:
                finish();
                break;
            case R.id.txt_publish:
                SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                // TODO: 2021/5/28
                edit.putString("content", binding.richEditor.getHtml());
                edit.putString("title", binding.editName.getText().toString().trim());
                edit.commit();

                String idTime = String.valueOf(System.currentTimeMillis());
                News news = new News(idTime);
                news.setTitle(binding.editName.getText().toString().trim());
                news.setContent(binding.richEditor.getHtml());
                news.setFlag(BaseType.NEWS_MINE_LOCAL);
                if (user != null) {
                    news.setPublisher(user.getHuaweiAccount().getDisplayName());
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
                String date = simpleDateFormat.format(new Date().getTime());
                news.setPublishDate(date);
                NewsCoverActivity.start(news);
                break;
            case R.id.button_rich_do:
                // Redo
                binding.richEditor.redo();
                break;
            case R.id.button_rich_undo:
                // undo
                binding.richEditor.undo();
                break;
            case R.id.button_bold:
                // bold
                againEdit();
                binding.richEditor.setBold();
                break;
            case R.id.button_underline:
                // underline
                againEdit();
                binding.richEditor.setUnderline();
                break;
            case R.id.button_list_ul:
                // Point Sequential
                againEdit();
                binding.richEditor.setBullets();
                break;
            case R.id.button_list_ol:
                // Number Sequential
                againEdit();
                binding.richEditor.setNumbers();
                break;
            case R.id.button_image:
                if (!TextUtils.isEmpty(binding.richEditor.getHtml())) {
                    ArrayList<String> arrayList = RichUtils.returnImageUrlsFromHtml(binding.richEditor.getHtml());
                    if (arrayList.size() >= 9) {
                        Toast.makeText(this, "Max 9 images~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectImage(IMAGE_PICK_CROP);
                    KeyBoardUtils.closeKeybord(binding.editName, this);
                } else {
                    Toast.makeText(this, "Need storage permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private static final int IMAGE_PICK_CROP = 104;

    private void againEdit() {
        // If not focused correctly, get the focus and show the keyboard
        binding.richEditor.focusEditor();
        KeyBoardUtils.openKeybord(binding.editName, this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: 1");
        if (requestCode == IMAGE_PICK_CROP) {
            againEdit();
            String imgPath = "";

            if (data != null) {
                String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                Log.e(TAG, realPathFromUri);
                imgPath = realPathFromUri;
            } else {
                Toast.makeText(this, "Choose image again", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "onActivityResult 2: " + imgPath);
            binding.richEditor.insertImage(imgPath, "dachshund");

            currentUrl = imgPath;
            editImage();

            KeyBoardUtils.openKeybord(binding.editName, this);
            binding.richEditor.postDelayed(() -> binding.richEditor.scrollToBottom(), 200);
        } else if (resultCode == RESULT_OK) {
            // TODO: 2021/5/20
            if (data == null) {
                return;
            }
            String outPath = data.getStringExtra(ImagePicker.EXTRA_OUTPUT_URI);
            Log.d(TAG, "onActivityResult: " + outPath);

            if (!TextUtils.isEmpty(outPath)) {
                String newHtml = binding.richEditor.getHtml().replace(editUrl, outPath);
                binding.richEditor.setHtml(newHtml);
                currentUrl = "";
            }
        }
    }
}