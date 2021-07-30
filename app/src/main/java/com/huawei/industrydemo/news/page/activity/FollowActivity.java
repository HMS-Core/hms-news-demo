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

import android.os.Bundle;
import android.view.View;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.page.viewmodel.FollowActivityViewModel;

public class FollowActivity extends BaseActivity implements View.OnClickListener {
    private FollowActivityViewModel followActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        followActivityViewModel = new FollowActivityViewModel(this);
        followActivityViewModel.initView();
    }

    @Override
    public void onClick(View v) {
        followActivityViewModel.onClickEvent(v.getId());
    }
}