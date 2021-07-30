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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.page.fragment.NewsListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class NewsViewPagerAdapter extends FragmentPagerAdapter {
    private final String[] types;

    private final List<BaseFragment> list;

    public NewsViewPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.types = context.getResources().getStringArray(R.array.news_type);
        this.list = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            NewsListFragment newsListFragment = new NewsListFragment(i);
            list.add(newsListFragment);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return types[position];
    }

    @Override
    public int getCount() {
        return types == null ? 0 : types.length;
    }
}
