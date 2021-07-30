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
package com.huawei.industrydemo.news.utils.hms;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.SYSTEM_UI_FLAG_VISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.RelativeLayout.ALIGN_BOTTOM;
import static android.widget.RelativeLayout.ALIGN_PARENT_TOP;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.huawei.industrydemo.news.MainApplication;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.constants.Constants;
import com.huawei.industrydemo.news.entity.Video;
import com.huawei.industrydemo.news.page.activity.VideoActivity;
import com.huawei.industrydemo.news.utils.SystemUtil;
import com.huawei.industrydemo.news.utils.TimeUtil;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/21]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class VideoController implements SeekBar.OnSeekBarChangeListener, SurfaceHolder.Callback {
    private static final String TAG = VideoController.class.getSimpleName();

    private WisePlayer wisePlayer;

    private VideoActivity.VideoView mVideoView;

    private Activity mActivity;

    private Video mVideo;

    private SurfaceView surfaceView;

    private int startTime = -1;

    private int playbackTimeWhenTrackingTouch = 0;

    private boolean hasReported = false;

    private boolean isUserTrackingTouch = false;

    private boolean isSuspend = false;

    private boolean isVideoPlaying = false;

    private boolean isReady = false;

    private final Handler updateViewHandler =
            new Handler(
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(@NonNull Message msg) {
                            if (msg.what == Constants.PLAYING) {
                                if (!isUserTrackingTouch) {
                                    updatePlayProgressView(wisePlayer.getCurrentTime(), wisePlayer.getBufferTime());
                                    if (isVideoPlaying) {
                                        updateViewHandler.sendEmptyMessageDelayed(
                                                Constants.PLAYING, Constants.DELAY_MILLIS_500);
                                    }
                                    if (startTime == -1) {
                                        startTime = wisePlayer.getCurrentTime();
                                    } else if (!hasReported
                                            && (wisePlayer.getCurrentTime() - startTime + playbackTimeWhenTrackingTouch
                                            >= 4000)) {
                                        AnalyticsUtil.videoPlaybackReport();
                                        hasReported = true;
                                    }
                                }
                            }
                            return false;
                        }
                    });

    /**
     * init
     *
     * @param videoView videoView
     * @param video     video
     * @param activity  activity
     */
    public void init(VideoActivity.VideoView videoView, Video video, Activity activity) {
        if (wisePlayer == null) {
            wisePlayer = MainApplication.getWisePlayerFactory().createWisePlayer();
        }

        this.mActivity = activity;
        this.mVideoView = videoView;
        this.mVideo = video;

        mVideoView.seekBar.setOnSeekBarChangeListener(this);
        mVideoView.rlVideo.setOnClickListener(
                v ->
                        mVideoView.fvController.setVisibility(
                                mVideoView.fvController.getVisibility() == INVISIBLE ? VISIBLE : INVISIBLE));
        mVideoView.ivBack.setOnClickListener(v -> back());
        mVideoView.ivVideoStart.setOnClickListener(v -> changePlayState());
        mVideoView.ivFull.setOnClickListener(v -> setFullScreen());
    }

    private void back() {
        if (!SystemUtil.isPortrait(mActivity)) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mVideoView.ivFull.setVisibility(VISIBLE);
            mVideoView.vBgd.setLayoutParams(
                    new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) mActivity.getResources().getDimension(R.dimen.video_size)));
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mActivity.getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_VISIBLE);
            wisePlayer.setSurfaceChange();
        } else {
            mActivity.finish();
        }
    }

    private void setFullScreen() {
        if (SystemUtil.isPortrait(mActivity)) {
            mVideoView.ivFull.setVisibility(GONE);
            mVideoView.vBgd.setLayoutParams(
                    new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // Set up the full screen
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mActivity
                    .getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            wisePlayer.setSurfaceChange();
        }
    }

    /**
     * load video
     */
    public void load() {
        Log.d(TAG, "load");
        surfaceView = new SurfaceView(mActivity);
        mVideoView.rlVideo.addView(surfaceView, 0);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(ALIGN_BOTTOM, R.id.view_bgd);
        layoutParams.addRule(ALIGN_PARENT_TOP);
        surfaceView.setLayoutParams(layoutParams);

        if (wisePlayer != null) {
            mVideoView.tvTip.setVisibility(View.GONE);
            wisePlayer.setVideoType(0);
            wisePlayer.setCycleMode(1);
            wisePlayer.setPlayUrl(mVideo.getVideoUrl());
            wisePlayer.setLoadingListener(
                    new WisePlayer.LoadingListener() {
                        @Override
                        public void onLoadingUpdate(WisePlayer wisePlayer, int i) {
                            Log.d(TAG, "onLoadingUpdate");
                            int visibility = mVideoView.pbLoad.getVisibility();
                            if (visibility == GONE || visibility == INVISIBLE) {
                                mActivity.runOnUiThread(() -> mVideoView.pbLoad.setVisibility(View.VISIBLE));
                            }
                        }

                        @Override
                        public void onStartPlaying(WisePlayer wisePlayer) {
                            Log.d(TAG, "onStartPlaying");
                            if (mVideoView.pbLoad.getVisibility() == VISIBLE) {
                                mActivity.runOnUiThread(() -> mVideoView.pbLoad.setVisibility(GONE));
                            }
                        }
                    });
            wisePlayer.setReadyListener(
                    wisePlayer -> {
                        isReady = true;
                        Log.d(TAG, "onReady isVideoPlaying:" + isVideoPlaying);
                        if (isVideoPlaying) {
                            start();
                        }
                    });
            wisePlayer.setPlayEndListener(wisePlayer -> updatePlayCompleteView());

            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            wisePlayer.ready();
        }
    }

    /**
     * start
     */
    public void start() {
        mActivity.runOnUiThread(
                () -> {
                    if (wisePlayer == null) {
                        mVideoView.rlVideo.setVisibility(View.VISIBLE);
                        mVideoView.tvTip.setVisibility(View.VISIBLE);
                        return;
                    }
                    updatePlayView();
                    Log.d(TAG, "start isReady:" + isReady + " isVideoPlaying:" + isVideoPlaying);
                    if (isReady) {
                        wisePlayer.start();
                        isVideoPlaying = true;
                    }
                });
    }

    /**
     * removeUpdateViewHandler
     */
    public void removeUpdateViewHandler() {
        startTime = -1;
        playbackTimeWhenTrackingTouch = 0;
        hasReported = false;
        updateViewHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated wisePlayer != null:" + (wisePlayer != null));
        if (wisePlayer != null) {
            if (surfaceView != null) {
                wisePlayer.setView(surfaceView);
            }
            if (isSuspend) {
                isSuspend = false;
                isVideoPlaying = true;
                mVideoView.ivVideoStart.setImageResource(R.mipmap.ic_pause);
                wisePlayer.resume(PlayerConstants.ResumeType.KEEP);
                updateViewHandler.sendEmptyMessage(Constants.PLAYING);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed wisePlayer != null:" + (wisePlayer != null));
        isSuspend = true;
        isVideoPlaying = false;
        if (mVideoView != null) {
            mVideoView.ivVideoStart.setImageResource(R.mipmap.ic_play);
        }

        if (wisePlayer != null) {
            wisePlayer.suspend();
            updateViewHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * stop
     */
    public void stop() {
        if (wisePlayer != null) {
            isVideoPlaying = false;

            // release
            wisePlayer.setErrorListener(null);
            wisePlayer.setEventListener(null);
            wisePlayer.setResolutionUpdatedListener(null);
            wisePlayer.setReadyListener(null);
            wisePlayer.setLoadingListener(null);
            wisePlayer.setPlayEndListener(null);
            wisePlayer.setSeekEndListener(null);

            wisePlayer.release();
            wisePlayer = null;
            if (mVideoView != null && mVideoView.rlAd != null) {
                mVideoView.rlAd.removeView(surfaceView);
            }
            mVideo = null;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserTrackingTouch = true;
        if (!hasReported) {
            playbackTimeWhenTrackingTouch += wisePlayer.getCurrentTime() - startTime;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (!hasReported) {
            startTime = seekBar.getProgress();
        }
        isUserTrackingTouch = false;
        wisePlayer.seek(seekBar.getProgress());
        updateViewHandler.sendEmptyMessage(Constants.PLAYING);
    }

    private void updatePlayProgressView(int progress, int bufferPosition) {
        progress = Math.max(progress, 0);
        bufferPosition = Math.max(bufferPosition, 0);
        mVideoView.seekBar.setProgress(progress);
        mVideoView.seekBar.setSecondaryProgress(bufferPosition);
        mVideoView.tvCurrentTime.setText(TimeUtil.formatLongToTimeStr(progress));
    }

    private void changePlayState() {
        Log.d(TAG, " changePlayState isPlaying:" + isVideoPlaying);
        if (isVideoPlaying) {
            wisePlayer.pause();
            isVideoPlaying = false;
            updateViewHandler.removeCallbacksAndMessages(null);
            mVideoView.ivVideoStart.setImageResource(R.mipmap.ic_play);
        } else {
            wisePlayer.start();
            isVideoPlaying = true;
            updateViewHandler.sendEmptyMessage(Constants.PLAYING);
            mVideoView.pbLoad.setVisibility(View.GONE);
            mVideoView.ivVideoStart.setImageResource(R.mipmap.ic_pause);
        }
    }

    private void updatePlayCompleteView() {
        mVideoView.ivVideoStart.setImageResource(R.mipmap.ic_play);
        isVideoPlaying = false;
        wisePlayer.reset();
        wisePlayer.setPlayUrl(mVideo.getVideoUrl());
        mVideoView.seekBar.setProgress(0);
        updateViewHandler.removeCallbacksAndMessages(null);
    }

    private void updatePlayView() {
        if (wisePlayer != null) {
            isVideoPlaying = true;
            int totalTime = wisePlayer.getDuration();
            mVideoView.seekBar.setMax(totalTime);
            mVideoView.tvTotalTime.setText(TimeUtil.formatLongToTimeStr(totalTime));
            mVideoView.tvCurrentTime.setText(TimeUtil.formatLongToTimeStr(0));
            updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING, Constants.DELAY_MILLIS_500);
            mVideoView.seekBar.setProgress(0);
            mVideoView.ivVideoStart.setImageResource(R.mipmap.ic_pause);
        }
    }

    /**
     * setPlaying
     *
     * @param playing isPlaying
     */
    public void setPlaying(boolean playing) {
        isVideoPlaying = playing;
    }

    /**
     * getHolder
     *
     * @return VideoActivity.VideoView
     */
    public VideoActivity.VideoView getHolder() {
        return mVideoView;
    }
}
