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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.inteface.OnItemClickListener;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/24]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class MemberBuyAdapter extends RecyclerView.Adapter<MemberBuyAdapter.ViewHolder> {

    private final List<ProductInfo> productList;

    private final Context context;

    private MemberBuyAdapter.ViewHolder tempSelectHolder;

    private OnItemClickListener onItemClickListener;

    private static final int MULTIPLES = 1000000;

    public MemberBuyAdapter(List<ProductInfo> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberBuyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_buy_member, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MemberBuyAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
            if (tempSelectHolder != null) {
                tempSelectHolder.cvParent
                    .setCardBackgroundColor(context.getResources().getColor(R.color.member_no_select_bdg, null));
                tempSelectHolder.rvContent
                    .setBackground(context.getResources().getDrawable(R.drawable.member_item_no_select_bdg, null));
            }
            tempSelectHolder = holder;
            tempSelectHolder.cvParent
                .setCardBackgroundColor(context.getResources().getColor(R.color.member_select_bdg_2, null));
            tempSelectHolder.rvContent
                .setBackground(context.getResources().getDrawable(R.drawable.member_item_select_bdg, null));
        });

        ProductInfo productInfo = productList.get(position);
        holder.tvName.setText(productInfo.getProductName());

        DecimalFormat df = new DecimalFormat("#####0.00");
        double priceD = ((double) productInfo.getMicrosPrice());
        String price = df.format(priceD / MULTIPLES);
        holder.tvPrice.setText(context.getString(R.string.member_price, productInfo.getCurrency(), price));

        holder.tvOriginPrice.setText(
            context.getString(R.string.member_price, productInfo.getCurrency(), df.format(3 * priceD / MULTIPLES)));
        Paint paint = holder.tvOriginPrice.getPaint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rvContent;

        private TextView tvName;

        private TextView tvPrice;

        private TextView tvOriginPrice;

        private CardView cvParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvContent = itemView.findViewById(R.id.rv_content);
            cvParent = itemView.findViewById(R.id.lv_item);
            tvOriginPrice = itemView.findViewById(R.id.tv_member_origin_price);
            tvName = itemView.findViewById(R.id.tv_member_name);
            tvPrice = itemView.findViewById(R.id.tv_member_price);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
