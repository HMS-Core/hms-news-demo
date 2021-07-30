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

package com.huawei.industrydemo.news.base;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public abstract class BaseAdapter<V extends ViewDataBinding, D>
    extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {

    private final String TAG = this.getClass().getSimpleName() + "_Base";

    private List<D> mData = new ArrayList<>();

    public abstract int getLayoutId();

    public abstract void setItemHolder(@NonNull BaseViewHolder holder, int position, D d);

    public void refresh(List<D> data) {
        mData = data;
        notifyDataSetChanged();
    }

    /**
     * get Data
     * 
     * @return mData
     */
    public List<D> getmData() {
        return mData;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
        return new BaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.BaseViewHolder holder, int position) {

        if (mData != null && position < mData.size() && mData.get(position) != null) {
            D d = mData.get(position);
            setItemHolder(holder, position, d);

        } else {
            Log.e(TAG, "onBindViewHolder: data error :" + mData);
        }
    }

    @Override
    public int getItemCount() {
        int count = mData.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    /**
     * BaseViewHolder
     */
    public class BaseViewHolder extends RecyclerView.ViewHolder {
        public V bind;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            bind = DataBindingUtil.bind(itemView);
        }
    }

}
