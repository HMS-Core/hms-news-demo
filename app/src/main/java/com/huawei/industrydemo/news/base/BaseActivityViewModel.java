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

package com.huawei.industrydemo.news.base;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public abstract class BaseActivityViewModel<T extends BaseActivity> {

    // Activity object
    protected T mActivity;

    /**
     * constructor
     *
     * @param t Activity object
     */
    public BaseActivityViewModel(T t) {
        this.mActivity = t;
    }

    /**
     * Used to initialize the layout
     */
    public abstract void initView();

    /**
     * Set the click event.
     *
     * @param viewId Control ID
     */
    public abstract void onClickEvent(int viewId);

    /**
     * onActivityResult
     * 
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data Intent data
     */
    public abstract void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    /**
     * onRequestPermissionsResult
     * 
     * @param requestCode requestCode
     * @param permissions permissions
     * @param grantResults grantResults
     */
    public abstract void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults);

}
