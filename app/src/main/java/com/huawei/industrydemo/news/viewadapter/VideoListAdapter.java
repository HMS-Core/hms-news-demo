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

package com.huawei.industrydemo.news.viewadapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import com.google.gson.Gson;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.constants.KeyConstants;
import com.huawei.industrydemo.news.entity.Video;
import com.huawei.industrydemo.news.page.activity.VideoActivity;

import java.util.List;
import java.util.UUID;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/20]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    private final List<Video> list;

    private final Activity context;

    private final RequestOptions option = new RequestOptions()
            .placeholder(R.mipmap.video_bgd)
            .error(R.mipmap.video_bgd)
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(true);

    public VideoListAdapter(Activity context, List<Video> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = list.get(position);
        holder.tvTitle.setText(video.getTitle());
        holder.tvVideoSize.setText(video.getDuration());

        Glide.with(context).load(video.getPreviewUrl()).apply(option).into(holder.ivVideoBgd);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra(KeyConstants.VIDEO_INDEX, position);
            intent.putExtra(KeyConstants.VIDEO_ITEM, new Gson().toJson(video));
            intent.putExtra(KeyConstants.VIDEO_LIST, new Gson().toJson(list));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvVideoSize;

        public TextView tvTitle;

        public ImageView ivVideoBgd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_video_title);
            tvVideoSize = itemView.findViewById(R.id.tv_video_size);
            ivVideoBgd = itemView.findViewById(R.id.iv_video_bdg);
        }
    }
}
