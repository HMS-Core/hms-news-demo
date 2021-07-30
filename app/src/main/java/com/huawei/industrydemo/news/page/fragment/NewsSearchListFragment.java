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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.utils.DatabaseUtil;
import com.huawei.industrydemo.news.viewadapter.NewsListAdapter;

import java.util.List;


public class NewsSearchListFragment extends BaseFragment {
    private String searchContent;
    private List<News> list;

    // Required empty public constructor
    public NewsSearchListFragment() {
    }

    /**
     * constructor
     * @param searchContent searchContent
     */
    public NewsSearchListFragment(String searchContent) {
        this.searchContent = searchContent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_search, container, false);
        initData();
        initView(view);
        return view;
    }

    private void initData() {
        list = DatabaseUtil.getDatabase().newsDao().queryBySearch(searchContent);
    }

    private void initView(View view) {
        if(list == null || list.size()==0){
            view.findViewById(R.id.tv_no_data).setVisibility(View.VISIBLE);
            return;
        }
        view.findViewById(R.id.tv_no_data).setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.rv_news);
        NewsListAdapter adapter = new NewsListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.refresh(list);
    }
}