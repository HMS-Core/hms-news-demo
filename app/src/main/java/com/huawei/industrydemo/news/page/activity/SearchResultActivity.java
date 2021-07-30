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
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.page.fragment.VideoSearchListFragment;
import com.huawei.industrydemo.news.viewadapter.SearchResPageAdapter;

import static com.huawei.industrydemo.news.constants.KeyConstants.SEARCH_CONTENT;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class SearchResultActivity extends BaseActivity {
    private String searchContent = "%null%";
    private SearchResPageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Intent intent = getIntent();
        if (intent != null) {
            searchContent = intent.getStringExtra(SEARCH_CONTENT);
            initView();
        }
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tv_search_content)).setText(searchContent);
        TabLayout tabLayout = findViewById(R.id.search_tab_layout);
        ViewPager viewPager = findViewById(R.id.search_view_pager);

        adapter = new SearchResPageAdapter(getSupportFragmentManager(), this, "%" + searchContent + "%");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}