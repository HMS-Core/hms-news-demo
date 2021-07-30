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

package com.huawei.industrydemo.news.page.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.entity.Video;
import com.huawei.industrydemo.news.utils.DatabaseUtil;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;
import com.huawei.industrydemo.news.utils.agc.RemoteConfigUtil;
import com.huawei.industrydemo.news.viewadapter.VideoViewPagerAdapter;

import java.util.List;
import java.util.Map;

import static com.huawei.industrydemo.news.constants.Constants.OFF_LOAD_PAGE_COUNT;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class VideoFragment extends BaseFragment {

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private VideoViewPagerAdapter adapter;

    private List<Video> allList;

    public VideoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        getVideoList(view);
        return view;
    }

    private void getVideoList(View view) {
        String key = getString(R.string.video_key);
        AGConnectConfig config = AgcUtil.getConfig();
        Map<String, Object> results = config.getMergedAll();
        if (results.containsKey(key)) {
            String json = config.getValueAsString(key);
            VideoListFragment.VideoListResult listTypeToken = new VideoListFragment.VideoListResult();
            allList = new Gson().fromJson(json, listTypeToken.getType());
            DatabaseUtil.getDatabase().videoDao().deleteAll();
            DatabaseUtil.getDatabase().videoDao().insertVideoList(allList);
        } else {
            Log.e(TAG, key + " is null!");
            RemoteConfigUtil.fetch();
        }
        initView(view);
    }

    private void initView(View view) {
        mTabLayout = view.findViewById(R.id.video_tab_layout);
        mViewPager = view.findViewById(R.id.video_view_pager);
        mViewPager.setOffscreenPageLimit(OFF_LOAD_PAGE_COUNT);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            adapter = new VideoViewPagerAdapter(activity.getSupportFragmentManager(), allList, activity);
        }

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}