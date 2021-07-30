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

package com.huawei.industrydemo.news.inteface;

import android.util.Log;
import android.view.View;

import static com.huawei.industrydemo.news.constants.LogConfig.TAG;

/**
 * Prevents the button from clicking continuously.
 * 
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/14]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public abstract class OnNonDoubleClickListener implements View.OnClickListener {
    // Interval between two clicks, in milliseconds
    private final int spaceTime;

    private long lastClickTime;

    public OnNonDoubleClickListener() {
        this.spaceTime = 500;
    }

    public OnNonDoubleClickListener(int spaceTime) {
        this.spaceTime = spaceTime;
    }

    /**
     * Check whether to click repeatedly
     *
     * @return boolean
     */
    private synchronized boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick = currentTime - lastClickTime <= spaceTime;
        lastClickTime = currentTime;
        return isClick;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "OnNonDoubleClickListener.onClick()");
        if (!isDoubleClick()) {
            run(v);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    public abstract void run(View v);
}
