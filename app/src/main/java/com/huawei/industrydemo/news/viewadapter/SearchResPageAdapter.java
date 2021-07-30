/*
 *
 *  *     Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *
 *
 */

package com.huawei.industrydemo.news.viewadapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.page.fragment.NewsSearchListFragment;
import com.huawei.industrydemo.news.page.fragment.VideoSearchListFragment;

/**
 * @version [News-Demo 2.0.0.300, 2021/6/9]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class SearchResPageAdapter extends FragmentPagerAdapter {
    private final String[] types;
    private final NewsSearchListFragment newsSearchFragment;
    private final VideoSearchListFragment videoSearchFragment;

    public SearchResPageAdapter(@NonNull FragmentManager fm, Context context, String searchContent) {
        super(fm);
        this.types = context.getResources().getStringArray(R.array.search_type);
        newsSearchFragment = new NewsSearchListFragment(searchContent);
        videoSearchFragment = new VideoSearchListFragment(searchContent);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return position == 0 ? newsSearchFragment : videoSearchFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return types[position];
    }

    @Override
    public int getCount() {
        return types == null ? 0 : types.length;
    }

    /**
     * getVideoSearchFragment
     *
     * @return VideoSearchFragment
     */
    public VideoSearchListFragment getVideoSearchFragment() {
        return videoSearchFragment;
    }
}
