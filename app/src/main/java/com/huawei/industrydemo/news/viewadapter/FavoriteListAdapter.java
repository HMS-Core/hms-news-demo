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

package com.huawei.industrydemo.news.viewadapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseAdapter;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.databinding.ItemFavoriteBinding;
import com.huawei.industrydemo.news.entity.Favorite;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.page.activity.news.NewsViewActivity;
import com.huawei.industrydemo.news.repository.FavoriteRepository;
import com.huawei.industrydemo.news.repository.NewsRepository;
import com.huawei.industrydemo.news.utils.ContextUtil;
import com.huawei.industrydemo.news.wight.BaseDialog;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/19]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class FavoriteListAdapter extends BaseAdapter<ItemFavoriteBinding, Favorite> {
    private final Context context;

    private final FavoriteRepository favoriteRepository;

    private final NewsRepository newsRepository;

    public FavoriteListAdapter(Context context) {
        this.context = context;
        favoriteRepository = new FavoriteRepository();
        newsRepository = new NewsRepository();
    }

    @Override
    public int getLayoutId() {
        return R.layout.item__favorite;
    }

    @Override
    public void setItemHolder(@NonNull BaseViewHolder holder, int position, Favorite favorite) {
        News item = newsRepository.queryById(favorite.getNewsId());
        holder.bind.tvTitle.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> NewsViewActivity.start(item));

        holder.bind.publisher.setText(item.getPublisher());
        holder.bind.time.setText(item.getPublishDate());
        holder.bind.lImg.setVisibility(View.VISIBLE);
        holder.bind.img1.setVisibility(View.VISIBLE);
        holder.bind.img2.setVisibility(View.VISIBLE);
        holder.bind.img3.setVisibility(View.VISIBLE);
        holder.bind.img1Small.setVisibility(View.GONE);
        holder.bind.img1Big.setVisibility(View.GONE);
        switch (item.getType()) {
            case BaseType.PICTURE_ONE_SMALL:
                holder.bind.img1Small.setVisibility(View.VISIBLE);
                Glide.with(ContextUtil.context()).load(item.img1).into(holder.bind.img1Small);
                holder.bind.img1.setVisibility(View.GONE);
                holder.bind.img2.setVisibility(View.GONE);
                holder.bind.img3.setVisibility(View.GONE);
                break;
            case BaseType.PICTURE_ONE_BIG:
                Glide.with(ContextUtil.context()).load(item.img1).into(holder.bind.img1Big);
                holder.bind.img1Big.setVisibility(View.VISIBLE);
                holder.bind.img1.setVisibility(View.GONE);
                holder.bind.img2.setVisibility(View.GONE);
                holder.bind.img3.setVisibility(View.GONE);
                break;
            case BaseType.PICTURE_3:
                Glide.with(ContextUtil.context()).load(item.img1).into(holder.bind.img1);
                Glide.with(ContextUtil.context()).load(item.img2).into(holder.bind.img2);
                Glide.with(ContextUtil.context()).load(item.img3).into(holder.bind.img3);
                break;
            case BaseType.PICTURE_0:
            default:
                holder.bind.lImg.setVisibility(View.GONE);
                break;
        }
        holder.bind.ivFavorite.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(BaseDialog.CONTENT,
                String.valueOf(context.getResources().getText(R.string.undo_favorite_alert)));
            BaseDialog baseDialog = new BaseDialog(context, bundle, true);
            baseDialog.setConfirmListener(v1 -> {
                favoriteRepository.removeFavorite(favorite);
                getmData().remove(favorite);
                notifyDataSetChanged();
                baseDialog.dismiss();
            });
            baseDialog.show();
        });
    }
}
