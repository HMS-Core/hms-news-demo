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

package com.huawei.industrydemo.news.wight.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */

class PopupController {
    private int layoutResId; // layout id

    Context context;

    private final PopupWindow popupWindow;

    View mPopupView; // Popup View layout

    private View mView;

    PopupController(Context context, PopupWindow popupWindow) {
        this.context = context;
        this.popupWindow = popupWindow;
    }

    /**
     * setView
     *
     * @param layoutResId layoutResId
     */
    public void setView(int layoutResId) {
        mView = null;
        this.layoutResId = layoutResId;
        installContent();
    }

    /**
     * setView
     *
     * @param view view
     */
    public void setView(View view) {
        mView = view;
        this.layoutResId = 0;
        installContent();
    }

    private void installContent() {
        if (layoutResId != 0) {
            mPopupView = LayoutInflater.from(context).inflate(layoutResId, null);
        } else if (mView != null) {
            mPopupView = mView;
        }
        popupWindow.setContentView(mPopupView);
    }

    /**
     * setWidthAndHeight
     *
     * @param width width
     * @param height height
     */
    private void setWidthAndHeight(int width, int height) {
        if (width == 0 || height == 0) {
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            popupWindow.setWidth(width);
            popupWindow.setHeight(height);
        }
    }

    /**
     * setAnimationStyle
     *
     * @param animationStyle animationStyle
     */
    private void setAnimationStyle(int animationStyle) {
        popupWindow.setAnimationStyle(animationStyle);
    }

    /**
     * setOutsideTouchable
     *
     * @param touchable touchable
     */
    private void setOutsideTouchable(boolean touchable) {
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(touchable);
        popupWindow.setFocusable(touchable);
    }

    static class PopupParams {
        /**
         * id
         */
        public int layoutResId;

        /**
         * mContext
         */
        public Context mContext;

        /**
         * pop width
         */
        public int mWidth;

        /**
         * pop height
         */
        public int mHeight;

        /**
         * isShowAnim
         */
        public boolean isShowAnim;

        /**
         * animation Id
         */
        public int animationStyle;

        /**
         * mView
         */
        public View mView;

        /**
         * isTouchable
         */
        public boolean isTouchable = true;

        public PopupParams(Context mContext) {
            this.mContext = mContext;
        }

        /**
         * apply
         *
         * @param controller controller
         */
        public void apply(PopupController controller) {
            if (mView != null) {
                controller.setView(mView);
            } else if (layoutResId != 0) {
                controller.setView(layoutResId);
            } else {
                throw new IllegalArgumentException("PopupView's contentView is null");
            }
            controller.setWidthAndHeight(mWidth, mHeight);
            controller.setOutsideTouchable(isTouchable);

            if (isShowAnim) {
                controller.setAnimationStyle(animationStyle);
            }
        }
    }
}
