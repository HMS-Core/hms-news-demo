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

package com.huawei.industrydemo.news.page.viewmodel;

import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivityViewModel;
import com.huawei.industrydemo.news.entity.BrowsingHistory;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.page.activity.BrowsingHistoryActivity;
import com.huawei.industrydemo.news.repository.NewsRepository;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.DatabaseUtil;
import com.huawei.industrydemo.news.viewadapter.NewsListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.huawei.industrydemo.news.constants.Constants.TOURIST_USERID;

/**
 * @version [News-Demo 2.0.0.300, 2021/6/3]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class BrowsingHistoryViewModel extends BaseActivityViewModel<BrowsingHistoryActivity> {
    private RecyclerView recyclerView;

    private NewsListAdapter browsingHistoryAdapter;

    /**
     * constructor
     *
     * @param browsingHistoryActivity Activity object
     */
    public BrowsingHistoryViewModel(BrowsingHistoryActivity browsingHistoryActivity) {
        super(browsingHistoryActivity);
    }

    @Override
    public void initView() {
        mActivity.findViewById(R.id.iv_back).setOnClickListener(mActivity);
        ((TextView) mActivity.findViewById(R.id.tv_title)).setText(R.string.title_history);
        recyclerView = mActivity.findViewById(R.id.rv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<BrowsingHistory> res = getBrowsingHistoryData();
        Collections.reverse(res);
        browsingHistoryAdapter = new NewsListAdapter();
        recyclerView.setAdapter(browsingHistoryAdapter);
    }

    private List<BrowsingHistory> getBrowsingHistoryData() {
        UserRepository userRepository = new UserRepository();
        List<BrowsingHistory> list;
        User user = userRepository.getCurrentUser();
        if (user == null) {
            list = DatabaseUtil.getDatabase().browsingHistoryDao().queryByUserId(TOURIST_USERID);
        } else {
            list = DatabaseUtil.getDatabase().browsingHistoryDao().queryByUserId(user.getOpenId());
        }

        return list;
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }

    public void onResume() {
        NewsRepository newsRepository = new NewsRepository();
        List<BrowsingHistory> browsingHistories = getBrowsingHistoryData();
        List<News> news = new ArrayList<>();
        for (BrowsingHistory b : browsingHistories) {
            News new1 = newsRepository.queryById(b.getNewsId());
            news.add(new1);
        }

        if (null != browsingHistoryAdapter) {
            browsingHistoryAdapter.refresh(news);
        }
    }
}
