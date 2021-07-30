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
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.entity.Filters;
import com.huawei.industrydemo.news.utils.FilterUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/6/8]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private Context context;

    private List<Filters> filterList;

    private ImageView imageView;

    private View view;

    private Bitmap bitmap;

    private JSONObject authJson;

    public FilterAdapter(List<Filters> list, Context context, ImageView imageView, Bitmap bitmap, JSONObject authJson) {
        this.context = context;
        this.filterList = list;
        this.imageView = imageView;
        this.bitmap = bitmap;
        this.authJson = authJson;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_image_with_image, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != 0) {
                    FilterUtil.getInstance().startFilter(bitmap, imageView, String.valueOf(position), authJson);
                } else {
                    imageView.setImageBitmap(bitmap);
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Filters filters = filterList.get(position);
        holder.textViewName.setText(filters.getName());
        holder.imageViewWithFilter.setImageResource(filters.getImageId());
    }

    @Override
    public int getItemCount() {
        return filterList == null ? 0 : filterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View filterView;

        TextView textViewName;

        ImageView imageViewWithFilter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            filterView = itemView;
            textViewName = itemView.findViewById(R.id.filter_name);
            imageViewWithFilter = itemView.findViewById(R.id.image_with_filter);
        }
    }

}
