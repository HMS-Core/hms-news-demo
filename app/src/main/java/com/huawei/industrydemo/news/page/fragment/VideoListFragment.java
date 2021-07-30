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

package com.huawei.industrydemo.news.page.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.entity.Video;
import com.huawei.industrydemo.news.viewadapter.VideoListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class VideoListFragment extends BaseFragment {
    private String type;

    private List<Video> list;

    /**
     * default
     */
    public VideoListFragment() {
    }

    /**
     * @param type video type
     */
    public VideoListFragment(String type,List<Video> list) {
        this.type = type;
        this.list = list;
        filterData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        initView(view);
        return view;
    }

    private void filterData() {
        if (list == null || list.size() == 0) {
            return;
        }
        List<Video> temp = new ArrayList<>();
        for (Video video : list) {
            String tempType = video.getType();
            if (tempType == null) {
                continue;
            }
            if (tempType.equals(type)) {
                temp.add(video);
            }
        }
        list = temp;
    }

    private void initView(View view) {
        if (list == null || list.size() == 0) {
            view.findViewById(R.id.tv_no_video).setVisibility(View.VISIBLE);
            return;
        }
        view.findViewById(R.id.tv_no_video).setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.rv_video);
        recyclerView.setItemViewCacheSize(list.size());
        VideoListAdapter videoListAdapter = new VideoListAdapter(getActivity(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoListAdapter);
    }

    static class VideoListResult extends TypeToken<List<Video>> {
        public VideoListResult() {
        }
    }
}