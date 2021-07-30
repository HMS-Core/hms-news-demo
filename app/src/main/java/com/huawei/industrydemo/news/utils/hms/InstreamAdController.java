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

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.instreamad.InstreamAd;
import com.huawei.hms.ads.instreamad.InstreamAdLoadListener;
import com.huawei.hms.ads.instreamad.InstreamAdLoader;
import com.huawei.hms.ads.instreamad.InstreamMediaStateListener;
import com.huawei.hms.ads.instreamad.InstreamView;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.constants.Constants;
import com.huawei.industrydemo.news.inteface.InstreamAdListener;
import com.huawei.industrydemo.news.page.activity.VideoActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.huawei.industrydemo.news.constants.LogConfig.TAG;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/20]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class InstreamAdController {

    private List<InstreamAd> instreamAds = new ArrayList<>();

    private final Activity mContext;

    private InstreamView mInstreamView;

    private int maxAdDuration;

    private InstreamAdListener mListener;

    private VideoActivity.VideoView mVideoView;

    private boolean isPlaying;

    public InstreamAdController(Activity context, VideoActivity.VideoView videoView) {
        this.mContext = context;
        this.mVideoView = videoView;
        this.mInstreamView = new InstreamView(context);
        this.isPlaying = false;
    }

    public void showAds(InstreamAdListener listener) {
        mInstreamView.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mListener = listener;

        int totalDuration = 60;
        int maxCount = 4;
        InstreamAdLoader.Builder builder = new InstreamAdLoader.Builder(mContext, Constants.INSTREAM_AD_ID);
        InstreamAdLoader adLoader = builder.setTotalDuration(totalDuration)
                .setMaxCount(maxCount)
                .setInstreamAdLoadListener(adLoadListener)
                .build();
        mVideoView.rlAd.addView(mInstreamView, 0);

        mVideoView.tvSkip.setOnClickListener(v -> {
            if (mListener != null) {
                isPlaying = false;
                mListener.onAdEnd();
            }
            removeInstream();
        });
        mInstreamView.setInstreamMediaStateListener(mediaStateListener);
        adLoader.loadAd(new AdParam.Builder().build());
    }

    private final InstreamMediaStateListener mediaStateListener = new InstreamMediaStateListener() {
        @Override
        public void onMediaProgress(int per, int playTime) {
            updateCountDown(playTime);
        }

        @Override
        public void onMediaStart(int playTime) {
            mVideoView.tvSkip.setVisibility(View.VISIBLE);
            updateCountDown(playTime);
            isPlaying = true;
        }

        @Override
        public void onMediaPause(int playTime) {
            updateCountDown(playTime);
        }

        @Override
        public void onMediaStop(int playTime) {
            setIsPlaying(false);
            updateCountDown(playTime);
        }

        @Override
        public void onMediaCompletion(int playTime) {
            updateCountDown(playTime);
            isPlaying = false;
            if (mListener != null) {
                mListener.onAdEnd();
            }
            removeInstream();

        }

        @Override
        public void onMediaError(int playTime, int errorCode, int extra) {
            updateCountDown(playTime);
        }
    };

    private void playInstreamAds(List<InstreamAd> ads) {
        isPlaying = true;
        maxAdDuration = getMaxInstreamDuration(ads);
        mInstreamView.setInstreamAds(ads);
    }

    private int getMaxInstreamDuration(List<InstreamAd> ads) {
        int duration = 0;
        for (InstreamAd ad : ads) {
            duration += ad.getDuration();
        }
        return duration;
    }

    private void updateCountDown(long playTime) {
        long currentTime = (maxAdDuration - playTime) / 1000;
        String time = String.valueOf(currentTime);
        mContext.runOnUiThread(() -> mVideoView.tvSkip.setText(mContext.getString(R.string.ad_tips, time)));
    }

    private final InstreamAdLoadListener adLoadListener = new InstreamAdLoadListener() {
        @Override
        public void onAdLoaded(final List<InstreamAd> ads) {
            if (null == ads) {
                Log.w(TAG, "null == ads");
                if (mListener != null) {
                    mListener.onAdEnd();
                }
                return;
            }
            if (ads.size() == 0) {
                Log.w(TAG, "ads.size() == 0");
                if (mListener != null) {
                    mListener.onAdEnd();
                }
                return;
            }

            Iterator<InstreamAd> it = ads.iterator();
            while (it.hasNext()) {
                InstreamAd ad = it.next();
                if (ad.isExpired()) {
                    it.remove();
                }
            }

            if (mListener != null) {
                mListener.onAdStart();
            }

            instreamAds = ads;
            playInstreamAds(instreamAds);
        }

        @Override
        public void onAdFailed(int errorCode) {
            Log.w(TAG, "onAdFailed: " + errorCode);
            if (mListener != null) {
                setIsPlaying(false);
                mListener.onAdEnd();
            }
        }
    };

    /**
     * remove ads
     */
    public void removeInstream() {
        Log.d(TAG, "removeInstream " + isPlaying);
        if (null != mInstreamView) {
            mInstreamView.onClose();
            mInstreamView.destroy();
            instreamAds.clear();
        }
    }

    /**
     * pause ad
     */
    public void pause() {
        Log.d(TAG, "ad pause isPlaying:" + isPlaying);
        if (null != mInstreamView && mInstreamView.isPlaying()) {
            Log.d(TAG, " instreamView.isPlaying() " + mInstreamView.isPlaying());
            mInstreamView.pause();
        }
    }

    /**
     * play ad
     */
    public void play() {
        Log.d(TAG, "ad play:" + isPlaying);
        if (null != mInstreamView && !mInstreamView.isPlaying()) {
            Log.d(TAG, " instreamView.isPlaying() " + mInstreamView.isPlaying());
            mInstreamView.play();
        }
    }

    /**
     * clear ad
     *
     * @param isClear true set mVideoView = null;
     */
    public void clear(boolean isClear) {
        if (isPlaying) {
            return;
        }
        if (null != mInstreamView) {
            mInstreamView.removeInstreamMediaStateListener();
            mInstreamView.removeInstreamMediaChangeListener();
            mInstreamView.removeMediaMuteListener();
            mInstreamView.destroy();
            mListener = null;
            maxAdDuration = 0;
        }
        if (isClear && null != mVideoView) {
            mVideoView.rlAd.removeViewAt(0);
            mInstreamView = null;
            mVideoView.rlAd.setVisibility(View.GONE);
            mVideoView = null;
        }
    }

    /**
     * isPlaying
     *
     * @return isPlaying
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * setIsPlaying
     *
     * @param isPlaying IsPlaying
     */
    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

}
