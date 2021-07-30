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

package com.huawei.industrydemo.news.page.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.industrydemo.news.MainApplication;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.constants.Constants;
import com.huawei.industrydemo.news.constants.KeyConstants;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.entity.Video;
import com.huawei.industrydemo.news.inteface.InstreamAdListener;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.DatabaseUtil;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;
import com.huawei.industrydemo.news.utils.agc.AppLinkUtils;
import com.huawei.industrydemo.news.utils.agc.RemoteConfigUtil;
import com.huawei.industrydemo.news.utils.hms.InstreamAdController;
import com.huawei.industrydemo.news.utils.hms.VideoController;
import com.huawei.industrydemo.news.viewadapter.VideoThumbnailAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class VideoActivity extends BaseActivity {
    private VideoView mVideoView;

    private Video video;

    private List<Video> list;

    private VideoController videoController;

    private InstreamAdController instreamAdController;

    private static InitFactoryCallback CALL_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent == null) {
            errorNull();
            return;
        }

        // from Activity
        getFromActivity(intent);
        if (video != null) {
            list.remove(intent.getIntExtra(KeyConstants.VIDEO_INDEX, 0));
            initView();
            initVideo();
            return;
        }

        // from AppLinking
        getFromAppLinking(intent);
        if (video == null) {
            errorNull();
            return;
        }

        if (MainApplication.getWisePlayerFactory() == null) {
            CALL_BACK = new InitFactoryCallback() {
                @Override
                public void onSuccess(WisePlayerFactory wisePlayerFactory) {
                    runOnUiThread(() -> {
                        initView();
                        initVideo();
                    });
                }

                @Override
                public void onFailure(int errorCode, String reason) {
                    AgcUtil.reportFailure(TAG, "onFailure errorCode:" + errorCode + " reason:" + reason);
                }
            };
        } else {
            initView();
            initVideo();
        }
    }

    private void getFromActivity(Intent intent) {
        video = new Gson().fromJson(intent.getStringExtra(KeyConstants.VIDEO_ITEM), Video.class);
        list = new Gson().fromJson(intent.getStringExtra(KeyConstants.VIDEO_LIST), new ListVideoReference().getType());
    }

    private void getFromAppLinking(Intent intent) {
        String videoUrl = intent.getData().getQueryParameter(KeyConstants.VIDEO_APP_LINK);
        Log.d(TAG, videoUrl);
        if (videoUrl == null) {
            errorNull();
            return;
        }
        getVideoList();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Video temp = list.get(i);
            if (videoUrl.equals(temp.getVideoUrl())) {
                video = temp;
                list.remove(i);
                break;
            }
        }
    }

    private void errorNull() {
        finish();
        Toast.makeText(this, R.string.video_url_null, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        mVideoView = new VideoView(findViewById(android.R.id.content));
        mVideoView.tvTitle.setText(video.getTitle());
        mVideoView.ivShare.setOnClickListener(v -> share(video.getVideoUrl()));

        RecyclerView recyclerView = findViewById(R.id.rv_video);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VideoThumbnailAdapter adapter = new VideoThumbnailAdapter(list, this);
        recyclerView.setAdapter(adapter);
    }

    private void initVideo() {
        videoController = new VideoController();
        videoController.init(mVideoView, video, this);

        User user = new UserRepository().getCurrentUser();
        if (user != null && user.isMember()) {
            showVideoLayout();
            videoController.setPlaying(true);
            videoController.load();
            return;
        }

        instreamAdController = new InstreamAdController(this, mVideoView);
        instreamAdController.showAds(new InstreamAdListener() {
            @Override
            public void onAdStart() {
                mVideoView.rlAd.setVisibility(View.VISIBLE);
                videoController.load();
            }

            @Override
            public void onAdEnd() {
                showVideoLayout();
                videoController.setPlaying(true);
                videoController.start();
                instreamAdController.clear(false);
            }
        });
    }

    private void share(String videoUrl) {
        String suffix;
        try {
            suffix = KeyConstants.VIDEO_APP_LINK + "=" + URLEncoder.encode(videoUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, R.string.video_url_null, Toast.LENGTH_SHORT).show();
            return;
        }
        new AppLinkUtils(this, Constants.VIDEO_DEEP_LINK).createAppLinkingAndShare(suffix);
    }

    private void getVideoList() {
        String key = getString(R.string.video_key);
        AGConnectConfig config = AgcUtil.getConfig();
        Map<String, Object> results = config.getMergedAll();
        if (results.containsKey(key)) {
            String json = config.getValueAsString(key);
            ListVideoReference listTypeToken = new ListVideoReference();
            list = new Gson().fromJson(json, listTypeToken.getType());
            DatabaseUtil.getDatabase().videoDao().deleteAll();
            DatabaseUtil.getDatabase().videoDao().insertVideoList(list);
        } else {
            Log.e(TAG, key + " is null!");
            RemoteConfigUtil.fetch();
        }
    }

    private void showVideoLayout() {
        mVideoView.rlVideo.setVisibility(View.VISIBLE);
        mVideoView.rlAd.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (instreamAdController != null) {
            instreamAdController.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (instreamAdController != null && instreamAdController.isPlaying()) {
            instreamAdController.play();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (instreamAdController != null) {
            instreamAdController.clear(true);
        }

        if (videoController != null) {
            videoController.removeUpdateViewHandler();
            videoController.stop();
        }
    }

    public static class VideoView {
        public TextView tvSkip;

        public TextView tvTitle;

        public TextView tvTip;

        public ImageView ivShare;

        public ImageView ivVideoStart;

        public ImageView ivBack;

        public RelativeLayout rlVideo;

        public RelativeLayout rlAd;

        public ProgressBar pbLoad;

        public SeekBar seekBar;

        public TextView tvCurrentTime;

        public TextView tvTotalTime;

        public FrameLayout fvController;

        public ImageView ivFull;

        public View vBgd;

        public VideoView(@NonNull View itemView) {
            rlAd = itemView.findViewById(R.id.view_instream_ad);
            tvTitle = itemView.findViewById(R.id.tv_video_title);
            tvSkip = itemView.findViewById(R.id.tv_instream_skip);
            ivShare = itemView.findViewById(R.id.iv_share);
            rlVideo = itemView.findViewById(R.id.view_video);
            tvTip = itemView.findViewById(R.id.tv_tip);
            ivVideoStart = itemView.findViewById(R.id.play_btn);
            seekBar = itemView.findViewById(R.id.seek_bar);
            tvCurrentTime = itemView.findViewById(R.id.tv_current_time);
            tvTotalTime = itemView.findViewById(R.id.tv_total_time);
            pbLoad = itemView.findViewById(R.id.pb_buffer);
            fvController = itemView.findViewById(R.id.fv_controller);
            ivBack = itemView.findViewById(R.id.iv_back);
            ivFull = itemView.findViewById(R.id.iv_full);
            vBgd = itemView.findViewById(R.id.view_bgd);
        }
    }

    public static class ListVideoReference extends TypeReference<List<Video>> {
        public ListVideoReference() {
        }
    }

    /**
     * getCallBack
     *
     * @return InitFactoryCallback
     */
    public static InitFactoryCallback getCallBack() {
        return CALL_BACK;
    }
}