/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.huawei.industrydemo.news.entity.Favorite;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.page.activity.FavoriteActivity;
import com.huawei.industrydemo.news.repository.FavoriteRepository;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.viewadapter.FavoriteListAdapter;

import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/19]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class FavoriteActivityViewModel extends BaseActivityViewModel<FavoriteActivity> {
    private RecyclerView recyclerView;

    private FavoriteListAdapter favoriteListAdapter;

    /**
     * constructor
     *
     * @param favoriteActivity Activity object
     */
    public FavoriteActivityViewModel(FavoriteActivity favoriteActivity) {
        super(favoriteActivity);
    }

    @Override
    public void initView() {
        mActivity.findViewById(R.id.iv_back).setOnClickListener(mActivity);
        ((TextView) mActivity.findViewById(R.id.tv_title)).setText(R.string.title_favorite);
        recyclerView = mActivity.findViewById(R.id.rv_favorite);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        favoriteListAdapter = new FavoriteListAdapter(mActivity);
        recyclerView.setAdapter(favoriteListAdapter);
    }

    private List<Favorite> getFavorite() {
        UserRepository userRepository = new UserRepository();
        User user = userRepository.getCurrentUser();
        FavoriteRepository favoriteRepository = new FavoriteRepository();
        return favoriteRepository.queryByUserId(user.getOpenId());
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
        if (null != favoriteListAdapter) {
            favoriteListAdapter.refresh(getFavorite());
        }
    }
}
