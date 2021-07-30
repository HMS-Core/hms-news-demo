/*
 *     Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.industrydemo.news.viewadapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.DislikeAdListener;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseAdapter;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.databinding.ItemNewsListBinding;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.page.activity.news.NewsViewActivity;
import com.huawei.industrydemo.news.utils.ContextUtil;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/19]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class NewsListAdapter extends BaseAdapter<ItemNewsListBinding, News> {

    Activity activity;

    int mHomePosition = -1;

    public NewsListAdapter() {
    }

    public NewsListAdapter(Activity activity, int homePosition) {
        this.activity = activity;
        mHomePosition = homePosition;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_news_list;
    }

    @Override
    public void setItemHolder(@NonNull BaseViewHolder holder, int position, News item) {
        holder.bind.tvTitle.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> NewsViewActivity.start(item));

        holder.bind.publisher.setText(item.getPublisher());
        holder.bind.time.setText(item.getPublishDate());
        holder.bind.lImg.setVisibility(View.VISIBLE);
        holder.bind.img1.setVisibility(View.VISIBLE);
        holder.bind.img2.setVisibility(View.VISIBLE);
        holder.bind.img3.setVisibility(View.VISIBLE);
        holder.bind.img1Small.setVisibility(View.GONE);
        holder.bind.img1Big.setVisibility(View.GONE);
        switch (item.getType()) {
            case BaseType.PICTURE_ONE_SMALL:
                holder.bind.img1Small.setVisibility(View.VISIBLE);
                Glide.with(ContextUtil.context()).load(item.img1).into(holder.bind.img1Small);
                holder.bind.img1.setVisibility(View.GONE);
                holder.bind.img2.setVisibility(View.GONE);
                holder.bind.img3.setVisibility(View.GONE);
                break;
            case BaseType.PICTURE_ONE_BIG:
                Glide.with(ContextUtil.context()).load(item.img1).into(holder.bind.img1Big);
                holder.bind.img1Big.setVisibility(View.VISIBLE);
                holder.bind.img1.setVisibility(View.GONE);
                holder.bind.img2.setVisibility(View.GONE);
                holder.bind.img3.setVisibility(View.GONE);
                break;
            case BaseType.PICTURE_3:
                Glide.with(ContextUtil.context()).load(item.img1).into(holder.bind.img1);
                Glide.with(ContextUtil.context()).load(item.img2).into(holder.bind.img2);
                Glide.with(ContextUtil.context()).load(item.img3).into(holder.bind.img3);
                break;
            case BaseType.PICTURE_0:
            default:
                holder.bind.lImg.setVisibility(View.GONE);
                break;
        }

        if (mHomePosition == BaseType.HOME_CATEGORY_1_ALL) {
            int adsPosition = 2;
            if (position == adsPosition || (getItemCount() <= adsPosition && position == getItemCount() - 1)) {
                holder.bind.scrollViewAd.setVisibility(View.VISIBLE);
                new NativeAdTool(R.layout.native_video_template, activity, holder.bind.scrollViewAd);
            } else {
                holder.bind.scrollViewAd.setVisibility(View.GONE);
            }
        }

    }

    public static class NativeAdTool {

        private static final String TAG = NativeAdTool.class.getSimpleName();

        private final ScrollView adScrollView;

        Activity mActivity;

        public NativeAdTool(int layoutId, Activity activity, ScrollView scrollView) {
            this.layoutId = layoutId;
            mActivity = activity;
            adScrollView = scrollView;
            loadAd("testu7m3hc4gvm");
            Log.d(TAG, "NativeAdTool: ");
        }

        public int layoutId;

        public NativeAd globalNativeAd;

        public VideoOperator.VideoLifecycleListener videoLifecycleListener;

        public VideoOperator.VideoLifecycleListener getVideoLifecycleListener() {
            return videoLifecycleListener;
        }

        /**
         * setVideoLifecycleListener
         * 
         * @param videoLifecycleListener videoLifecycleListener
         */
        public void setVideoLifecycleListener(VideoOperator.VideoLifecycleListener videoLifecycleListener) {
            this.videoLifecycleListener = videoLifecycleListener;
        }

        /**
         * Load a native ad.
         *
         * @param adId ad slot ID.
         */
        public void loadAd(String adId) {
            NativeAdLoader.Builder builder = new NativeAdLoader.Builder(mActivity, adId);
            builder.setNativeAdLoadedListener(nativeAd -> {
                // Display native ad.
                showNativeAd(nativeAd);
            }).setAdListener(new AdListener() {
                @Override
                public void onAdFailed(int errorCode) {
                    Log.e(TAG, "onAdFailed: " + errorCode);
                }
            });

            NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT) // Set custom attributes.
                .build();

            NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();
            nativeAdLoader.loadAd(new AdParam.Builder().build());

        }

        /**
         * Display native ad.
         *
         * @param nativeAd native ad object that contains ad materials.
         */
        public void showNativeAd(NativeAd nativeAd) {
            // Destroy the original native ad.
            if (null != globalNativeAd) {
                globalNativeAd.destroy();
            }
            globalNativeAd = nativeAd;

            // Obtain NativeView.
            final NativeView nativeView = (NativeView) mActivity.getLayoutInflater().inflate(layoutId, null);

            // Register and populate a native ad material view.
            initNativeAdView(globalNativeAd, nativeView);
            globalNativeAd.setDislikeAdListener(new DislikeAdListener() {
                @Override
                public void onAdDisliked() {
                    // Call this method when an ad is closed.
                    adScrollView.removeView(nativeView);
                }
            });

            // Add NativeView to the app UI.
            adScrollView.removeAllViews();
            adScrollView.addView(nativeView);
        }

        /**
         * Register and populate a native ad material view.
         *
         * @param nativeAd native ad object that contains ad materials.
         * @param nativeView native ad view to be populated into.
         */
        public void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {
            // Register a native ad material view.
            nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
            nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
            nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
            nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

            // Populate a native ad material view.
            ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
            nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

            if (null != nativeAd.getAdSource()) {
                ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
            }
            nativeView.getAdSourceView().setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

            if (null != nativeAd.getCallToAction()) {
                ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
            nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

            // Obtain a video controller.
            VideoOperator videoOperator = nativeAd.getVideoOperator();

            // Check whether a native ad contains video materials.
            if (videoOperator.hasVideo()) {
                // Add a video lifecycle event listener.
                videoOperator.setVideoLifecycleListener(videoLifecycleListener);
            }

            // Register a native ad object.
            nativeView.setNativeAd(nativeAd);
        }

    }
}
