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

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;


/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class CommonPopupWindow extends PopupWindow {
    final PopupController controller;

    @Override
    public int getWidth() {
        return controller.mPopupView.getMeasuredWidth();
    }

    @Override
    public int getHeight() {
        return controller.mPopupView.getMeasuredHeight();
    }

    /**
     * PopItemListener
     */
    public interface PopItemListener {
        void getChildView(View view);
    }

    private CommonPopupWindow(Context context) {
        controller = new PopupController(context, this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setBackgroundAlpha((Activity) controller.context, 1f);
    }

    /**
     * show Up
     *
     * @param view view
     */
    public void showUpView(View view) {
        this.showAsDropDown(view, -(this.getWidth() - view.getMeasuredWidth()) / 2, -(this.getHeight() + view.getMeasuredHeight()));
    }

    /**
     * showUpView
     *
     * @param view View
     * @param bgAlpha float
     */
    public void showUpView(View view, float bgAlpha) {
        this.showAsDropDown(view, -(this.getWidth() - view.getMeasuredWidth()) / 2, -(this.getHeight() + view.getMeasuredHeight()));
        setBackgroundAlpha((Activity) controller.context, bgAlpha);
    }


    /**
     * showBottom
     *
     * @param view    view
     * @param bgAlpha bgAlpha
     */
    public void showBottom(View view, float bgAlpha) {
        this.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        setBackgroundAlpha((Activity) controller.context, bgAlpha);
    }

    /**
     * showDownView
     *
     * @param view view
     */
    public void showDownView(View view) {
        this.showAsDropDown(view, -(this.getWidth() - view.getMeasuredWidth()) / 2, 0);
    }

    /**
     * showDownVie
     *
     * @param view    view
     * @param bgAlpha bgAlpha
     */
    public void showDownView(View view, float bgAlpha) {
        this.showAsDropDown(view, -(this.getWidth() - view.getMeasuredWidth()) / 2, 0);
        setBackgroundAlpha((Activity) controller.context, bgAlpha);
    }

    /**
     * showLeftView
     *
     * @param view view
     */
    public void showLeftView(View view) {
        this.showAsDropDown(view, -view.getMeasuredWidth(), -(this.getHeight() + view.getMeasuredHeight()) / 2);
    }

    /**
     * showLeftView
     *
     * @param view    view
     * @param bgAlpha bgAlpha
     */
    public void showLeftView(View view, float bgAlpha) {
        this.showAsDropDown(view, -view.getMeasuredWidth(), -(this.getHeight() + view.getMeasuredHeight()) / 2);
        setBackgroundAlpha((Activity) controller.context, bgAlpha);
    }

    /**
     * showRightView
     *
     * @param view view
     */
    public void showRightView(View view) {
        this.showAsDropDown(view, view.getMeasuredWidth(), -(this.getHeight() + view.getMeasuredHeight()) / 2);
    }

    /**
     * showRightView
     *
     * @param view    view
     * @param bgAlpha bgAlpha
     */
    public void showRightView(View view, float bgAlpha) {
        this.showAsDropDown(view, view.getMeasuredWidth(), -(this.getHeight() + view.getMeasuredHeight()) / 2);
        setBackgroundAlpha((Activity) controller.context, bgAlpha);
    }

    /**
     * setBackgroundAlpha
     *
     * @param activity activity
     * @param bgAlpha  bgAlpha
     */
    public void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * Builder
     */
    public static class Builder {
        private final PopupController.PopupParams params;
        private PopItemListener listener;

        public Builder(Context context) {
            params = new PopupController.PopupParams(context);
        }

        /**
         * setView
         *
         * @param layoutResId set PopupWindow layout ID
         * @return Builder
         */
        public Builder setView(int layoutResId) {
            params.mView = null;
            params.layoutResId = layoutResId;
            return this;
        }

        /**
         * setView
         *
         * @param view set PopupWindow view
         * @return Builder
         */
        public Builder setView(View view) {
            params.mView = view;
            params.layoutResId = 0;
            return this;
        }

        /**
         * setViewOnclickListener
         *
         * @param listener ViewInterface
         * @return Builder
         */
        public Builder setViewOnclickListener(PopItemListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * setWidthAndHeight default wrap_content
         *
         * @param width width
         * @param height height
         * @return Builder
         */
        public Builder setWidthAndHeight(int width, int height) {
            params.mWidth = width;
            params.mHeight = height;
            return this;
        }


        /**
         * setOutsideTouchable
         *
         * @param touchable true/false
         * @return Builder
         */
        public Builder setOutsideTouchable(boolean touchable) {
            params.isTouchable = touchable;
            return this;
        }

        /**
         * setAnimationStyle
         *
         * @param animationStyle animationStyle
         * @return Builder
         */
        public Builder setAnimationStyle(int animationStyle) {
            params.isShowAnim = true;
            params.animationStyle = animationStyle;
            return this;
        }

        /**
         * create
         *
         * @return CommonPopupWindow
         */
        public CommonPopupWindow create() {
            final CommonPopupWindow popupWindow = new CommonPopupWindow(params.mContext);
            params.apply(popupWindow.controller);
            if (listener != null && params.layoutResId != 0) {
                listener.getChildView(popupWindow.controller.mPopupView);
            }
            measureWidthAndHeight(popupWindow.controller.mPopupView);
            return popupWindow;
        }
    }

    /**
     * measureWidthAndHeight
     *
     * @param view View
     */
    public static void measureWidthAndHeight(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * showAsDropDownLeo
     *
     * @param anchor View
     * @param xoff   int
     * @param yoff   int
     * @param alpha  float
     */
    public void showAsDropDownLeo(View anchor, int xoff, int yoff, float alpha) {
        this.showAsDropDown(anchor, xoff, yoff);
        setBackgroundAlpha((Activity) controller.context, alpha);
    }
}
