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

package com.huawei.industrydemo.news.viewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.entity.Avatar;
import com.huawei.industrydemo.news.entity.Follow;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.repository.AvatarRepository;
import com.huawei.industrydemo.news.repository.FollowRepository;
import com.huawei.industrydemo.news.repository.NewsRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FollowListAdapter extends RecyclerView.Adapter<FollowListAdapter.ViewHolder> {
    private List<Follow> followList;

    private Context context;

    private FollowRepository followRepository;

    private AvatarRepository avatarRepository;

    private NewsRepository newsRepository;

    public FollowListAdapter(Context context, List<Follow> followList) {
        this.followList = followList;
        this.context = context;
        followRepository = new FollowRepository();
        avatarRepository = new AvatarRepository();
        newsRepository = new NewsRepository();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_follow, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Follow follow = followList.get(position);
        Avatar avatar = avatarRepository.queryByFlag(follow.getFollowId());
        if (avatar != null) {
            String url = avatar.url;
            Glide.with(context).load(url).into(holder.ivIcon);
        }
        holder.tvAccount.setText(follow.getFollowId());
        setLastPublishedTime(follow.getFollowId(), holder.tvUpdateTime);
        holder.tvFollowStatus.setOnClickListener(view -> {
            if (follow.isFollow()) {
                holder.tvFollowStatus.setText(R.string.add_follow);
                holder.tvFollowStatus.setTextColor(context.getResources().getColor(R.color.text_add_follow));
                holder.tvFollowStatus.setBackgroundResource(R.drawable.bg_add_follow);
                followRepository.removeFollow(follow);
                follow.setFollow(false);
            } else {
                holder.tvFollowStatus.setText(R.string.followed);
                holder.tvFollowStatus.setTextColor(context.getResources().getColor(R.color.text_followed));
                holder.tvFollowStatus.setBackgroundResource(R.drawable.bg_followed);
                follow.setFollow(true);
                followRepository.addFollow(follow);
            }
        });
    }

    private void setLastPublishedTime(String publisher, TextView updateTime) {
        long updateData = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        List<News> newsOfPublisher = newsRepository.queryByPublisher(publisher);
        Collections.sort(newsOfPublisher);
        try {
            Date publishTime = format.parse(newsOfPublisher.get(0).getPublishDate());
            updateData = (new Date().getTime() - publishTime.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (updateData < 1) {
            updateTime.setText(String.format(Locale.ROOT, context.getResources().getString(R.string.update_within_day),
                updateData + 1));
        } else if (updateData < 3) {
            updateTime.setText(String.format(Locale.ROOT, context.getResources().getString(R.string.update_within_days),
                updateData + 1));
        } else {
            updateTime.setText(String.format(Locale.ROOT, context.getResources().getString(R.string.update_on),
                newsOfPublisher.get(0).getPublishDate().split(" ")[0]));
        }
    }

    @Override
    public int getItemCount() {
        return followList == null ? 0 : followList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;

        TextView tvAccount;

        TextView tvUpdateTime;

        TextView tvFollowStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvAccount = itemView.findViewById(R.id.tv_account);
            tvUpdateTime = itemView.findViewById(R.id.tv_update_time);
            tvFollowStatus = itemView.findViewById(R.id.tv_follow_status);
        }
    }
}
