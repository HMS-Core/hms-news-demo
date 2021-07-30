/*
 *
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
 * /
 *
 */

package com.huawei.industrydemo.news.page.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.databinding.ActivityMyNewsBinding;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.repository.NewsRepository;
import com.huawei.industrydemo.news.utils.ContextUtil;
import com.huawei.industrydemo.news.viewadapter.NewsListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class MineNewsActivity extends BaseActivity {

    ActivityMyNewsBinding binding;

    NewsListAdapter newsListAdapter  = new NewsListAdapter();
    private List<News> newsList = new ArrayList<>();

    /**
     * start this
     */
    public static void start() {
        Intent starter = new Intent(ContextUtil.context(), MineNewsActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextUtil.context().startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_news);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_news);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.mine_news);
        findViewById(R.id.iv_back).setOnClickListener(v-> onBackPressed());

        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        binding.recycle.setAdapter(newsListAdapter);

        newsList = new NewsRepository().queryByFlag(BaseType.NEWS_MINE_LOCAL);
        newsListAdapter.refresh(newsList);
    }


}
