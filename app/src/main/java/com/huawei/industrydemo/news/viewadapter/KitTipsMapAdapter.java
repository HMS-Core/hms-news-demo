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

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.entity.KitInfo;
import com.huawei.industrydemo.news.utils.KitTipUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.View.GONE;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/20]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class KitTipsMapAdapter extends RecyclerView.Adapter<KitTipsMapAdapter.ViewHolder> {

    // <UsedKitName,KitDes>
    private Map<String, KitInfo> map;

    // <AllKitName,iconId>
    private Map<String, Integer> iconMap;

    // <UsedKitFunction>
    private List<String> kitsFunctionList;

    private Context context;

    public KitTipsMapAdapter(Map<String, KitInfo> map, Context context) {
        this.map = map;
        this.context = context;
        kitsFunctionList = new ArrayList<>(map.keySet());
        this.iconMap = KitTipUtil.getIconMap();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kit_tip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String kitFunction = kitsFunctionList.get(position);
        KitInfo kitInfo = map.get(kitFunction);
        if (kitInfo == null) {
            holder.itemView.setVisibility(GONE);
            return;
        }
        Integer id = iconMap.get(kitFunction);
        holder.ivIcon.setImageResource(id == null ? R.mipmap.tip_kit_defult : id);
        holder.tvKit.setText(kitInfo.getKitNameStr());
        holder.tvFunction.setText(kitInfo.getKitFunctionStr());
        holder.tvDes.setText(kitInfo.getKitDescription());

    }

    @Override
    public int getItemCount() {
        return kitsFunctionList == null ? 0 : kitsFunctionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;

        TextView tvKit;

        TextView tvFunction;

        TextView tvDes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_kit);
            tvKit = itemView.findViewById(R.id.tv_kit);
            tvFunction = itemView.findViewById(R.id.tv_function);
            tvDes = itemView.findViewById(R.id.tv_kit_des);
        }
    }
}
