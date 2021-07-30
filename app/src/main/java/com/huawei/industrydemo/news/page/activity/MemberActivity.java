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

package com.huawei.industrydemo.news.page.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.support.api.client.Status;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.inteface.MemberRefundListener;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.MemberUtil;
import com.huawei.industrydemo.news.utils.SystemUtil;
import com.huawei.industrydemo.news.viewadapter.MemberBuyAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.huawei.hms.analytics.type.HAEventType.UPDATEMEMBERSHIPLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.CURRVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.PREVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.REASON;
import static com.huawei.industrydemo.news.constants.KitConstants.IAP_SUB;
import static com.huawei.industrydemo.news.constants.RequestCodeConstants.BUY_INTENT;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class MemberActivity extends BaseActivity implements View.OnClickListener {
    private static final int TYPE_SUBSCRIBED_PRODUCT = 2;
    private static final String SUBSCRIBED_PRODUCT_1 = "hms_member_one_week_auto";
    private static final String SUBSCRIBED_PRODUCT_2 = "hms_member_one_month_auto";
    private static final String SUBSCRIBED_PRODUCT_3 = "hms_member_one_year_auto";

    private RecyclerView recyclerView;
    private TextView tvStatus;

    private ProductInfo currentProductInfo = null;

    private final RequestOptions option = new RequestOptions().circleCrop()
            .placeholder(R.mipmap.head_load)
            .error(R.mipmap.head_my)
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);

    private User mUser;

    private UserRepository mUserRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setStatusBarColor(this, R.color.member_title_start);
        SystemUtil.setAndroidNativeLightStatusBar(this, true);
        setContentView(R.layout.activity_member);
        initView();
        initMemberList();
        setKitList(new String[]{IAP_SUB});
    }

    private void initView() {
        mUserRepository = new UserRepository();
        mUser = mUserRepository.getCurrentUser();

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_buy).setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.member_title);
        ((TextView) findViewById(R.id.tv_name)).setText(mUser.getHuaweiAccount().getDisplayName());

        tvStatus = findViewById(R.id.tv_status);
        recyclerView = findViewById(R.id.rv_member);

        ImageView ivHead = findViewById(R.id.iv_head);
        if (mUser.getHuaweiAccount().getAvatarUri() == Uri.EMPTY) {
            Glide.with(this).load(R.mipmap.head_my).apply(option).into(ivHead);
        } else {
            Glide.with(this).load(mUser.getHuaweiAccount().getAvatarUriString()).apply(option).into(ivHead);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_buy:
                buyNow();
                break;
            default:
                break;
        }
    }

    private void initMemberList() {
        ProductInfoReq req = new ProductInfoReq();
        ArrayList<String> list = new ArrayList<>();
        list.add(SUBSCRIBED_PRODUCT_1);
        list.add(SUBSCRIBED_PRODUCT_2);
        list.add(SUBSCRIBED_PRODUCT_3);
        req.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        req.setProductIds(list);
        Iap.getIapClient(this).obtainProductInfo(req)
                .addOnSuccessListener(result -> {
                    List<ProductInfo> productInfos = result.getProductInfoList();
                    if (productInfos == null || productInfos.size() == 0) {
                        return;
                    }

                    MemberBuyAdapter adapter = new MemberBuyAdapter(productInfos, this);
                    adapter.setOnItemClickListener(position -> {
                        currentProductInfo = productInfos.get(position);
                    });
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MemberActivity.this, 3, RecyclerView.VERTICAL, false);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    if (e instanceof IapApiException) {
                        IapApiException apiException = (IapApiException) e;
                        Log.d(TAG, "Status:" + apiException.getStatus());
                        Log.d(TAG, "returnCode:" + apiException.getStatusCode());
                    }
                    Log.d(TAG, "failure:" + e.toString());
                    Toast.makeText(this, "error:" + e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    private void buyNow() {
        if (currentProductInfo == null) {
            Toast.makeText(MemberActivity.this, R.string.member_buy_tip, Toast.LENGTH_SHORT).show();
            return;
        }
        PurchaseIntentReq req = new PurchaseIntentReq();
        req.setProductId(currentProductInfo.getProductId());
        req.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        req.setDeveloperPayload("");
        Iap.getIapClient(this).createPurchaseIntent(req).addOnSuccessListener(result -> {
            Status status = result.getStatus();
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(this, BUY_INTENT);
                } catch (IntentSender.SendIntentException exp) {
                    Log.d(TAG, "onFailure" + exp.toString());
                    Toast.makeText(this, exp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Log.d(TAG, "Status:" + apiException.getStatus());
                Log.d(TAG, "returnCode:" + apiException.getStatusCode());
            }
            Log.d(TAG, "failure:" + e.toString());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BUY_INTENT: // PMS Offering creation order interface
                try {
                    showResultInfo(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
                break;
            default:
                break;
        }
    }

    private void showResultInfo(Intent data) throws JSONException {
        PurchaseResultInfo result =
                Iap.getIapClient(this).parsePurchaseResultInfoFromIntent(data);
        int resCode = result.getReturnCode();
        if (resCode == 0) {
            Toast.makeText(this, R.string.buy_member_success, Toast.LENGTH_SHORT).show();
            InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(result.getInAppPurchaseData());
            MemberUtil.getInstance().refund(this, inAppPurchaseData.getSubscriptionId(), inAppPurchaseData.getPurchaseToken(), new MemberRefundListener() {
                @Override
                public void onSuccess(String msg) {
                    Log.d(TAG, "onSuccess:" + msg);
                    updateUser();
                }

                @Override
                public void onFailed(String msg) {
                    Log.d(TAG, "onFailed:" + msg);
                    updateUser();
                }
            });

            /* Report category view event */
            HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
            Bundle bundle = new Bundle();

            bundle.putString(PREVLEVEL, "Non-Member");
            bundle.putString(CURRVLEVEL, "Member");
            bundle.putString(REASON, "Member Purchase");
            instance.onEvent(UPDATEMEMBERSHIPLEVEL, bundle);

        } else if (resCode == 60051) {
            Toast.makeText(this, R.string.buy_member_tip_2, Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, result.getErrMsg());
    }

    private void updateUser() {
        runOnUiThread(() -> MemberUtil.getInstance().isMember(MemberActivity.this, mUser, (isMember, isAutoRenewing, productName, time) -> {
            mUser = mUserRepository.getCurrentUser();
            if (!isMember) {
                tvStatus.setText(R.string.member_no_enable);
            } else if (isAutoRenewing) {
                tvStatus.setText(getString(R.string.member_des_1, productName, time));
            } else {
                tvStatus.setText(getString(R.string.member_des_2, productName, time));
            }
        }));
    }
}