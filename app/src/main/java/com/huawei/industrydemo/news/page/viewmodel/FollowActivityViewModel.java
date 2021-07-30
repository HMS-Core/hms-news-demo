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

package com.huawei.industrydemo.news.page.viewmodel;

import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivityViewModel;
import com.huawei.industrydemo.news.entity.Follow;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.page.activity.FollowActivity;
import com.huawei.industrydemo.news.repository.FollowRepository;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.viewadapter.FollowListAdapter;

import java.util.List;

public class FollowActivityViewModel extends BaseActivityViewModel<FollowActivity> {
    private RecyclerView recyclerView;

    private FollowListAdapter followListAdapter;

    /**
     * constructor
     *
     * @param followActivity Activity object
     */
    public FollowActivityViewModel(FollowActivity followActivity) {
        super(followActivity);
    }

    @Override
    public void initView() {
        mActivity.findViewById(R.id.iv_back).setOnClickListener(mActivity);
        ((TextView) mActivity.findViewById(R.id.tv_title)).setText(R.string.my_follow);
        recyclerView = mActivity.findViewById(R.id.rv_follow);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        followListAdapter = new FollowListAdapter(mActivity, getFollow());
        recyclerView.setAdapter(followListAdapter);
    }

    private List<Follow> getFollow() {
        UserRepository userRepository = new UserRepository();
        User user = userRepository.getCurrentUser();
        FollowRepository followRepository = new FollowRepository();
        return followRepository.queryByUserId(user.getOpenId());
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
}
