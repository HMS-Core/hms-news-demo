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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.entity.BrowsingHistory;
import com.huawei.industrydemo.news.entity.Favorite;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.page.activity.news.NewsViewActivity;
import com.huawei.industrydemo.news.repository.NewsRepository;

import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/6/3]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class BrowsingHistoryAdapter extends RecyclerView.Adapter<BrowsingHistoryAdapter.ViewHolder>{
    private Context context;
    private List<BrowsingHistory> list;
    private NewsRepository newsRepository;
    public BrowsingHistoryAdapter(List<BrowsingHistory>list,Context context) {
        this.context = context;
        this.list = list;
        newsRepository = new NewsRepository();
    }
    @NonNull
    @Override
    public BrowsingHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BrowsingHistoryAdapter.ViewHolder holder, int position) {
        BrowsingHistory browsingHistory = list.get(position);
        News news = newsRepository.queryById(browsingHistory.getNewsId());
        holder.tvTitle.setText(news.getTitle());
        holder.tvContent.setText(news.getPublishDate());
        holder.itemView.setOnClickListener(v -> {
            NewsViewActivity.start(news);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : (Math.min(list.size(), 4));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        TextView tvContent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    public List<BrowsingHistory> getList() {
        return list;
    }

    public void setList(List<BrowsingHistory> list) {
        this.list = list;
    }

}
