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

package com.huawei.industrydemo.news.page.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapClient;
import com.huawei.hms.iap.entity.StartIapActivityReq;
import com.huawei.hms.iap.entity.StartIapActivityResult;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.industrydemo.news.MainActivity;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.base.BaseFragmentViewModel;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.constants.LogConfig;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.inteface.OnLoginListener;
import com.huawei.industrydemo.news.page.activity.BrowsingHistoryActivity;
import com.huawei.industrydemo.news.page.activity.FavoriteActivity;
import com.huawei.industrydemo.news.page.activity.FeedbackActivity;
import com.huawei.industrydemo.news.page.activity.FollowActivity;
import com.huawei.industrydemo.news.page.activity.MemberActivity;
import com.huawei.industrydemo.news.page.activity.MineNewsActivity;
import com.huawei.industrydemo.news.page.activity.PrivacyActivity;
import com.huawei.industrydemo.news.page.activity.WebViewActivity;
import com.huawei.industrydemo.news.page.fragment.MyFragment;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.MemberUtil;
import com.huawei.industrydemo.news.utils.SystemUtil;
import com.huawei.industrydemo.news.wight.BaseDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import static com.huawei.industrydemo.news.constants.KeyConstants.WEB_URL;
import static com.huawei.industrydemo.news.wight.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.news.wight.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.news.wight.BaseDialog.CONTENT;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/19]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class MyFragmentViewModel extends BaseFragmentViewModel<MyFragment> {
    private final String TAG = MyFragmentViewModel.class.getSimpleName();

    private ImageView ivHead;

    private TextView tvName;

    private TextView tvLogin;

    private ImageView ivLevel;

    private User mUser;

    private UserRepository mUserRepository;

    private final RequestOptions option = new RequestOptions().circleCrop()
        .placeholder(R.mipmap.head_load)
        .error(R.mipmap.head_my)
        .signature(new ObjectKey(UUID.randomUUID().toString()))
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true);

    /**
     * constructor
     *
     * @param myFragment Fragment object
     */
    public MyFragmentViewModel(MyFragment myFragment) {
        super(myFragment);
    }

    @Override
    public void initView(View view) {
        mUserRepository = new UserRepository();

        view.findViewById(R.id.lv_favorite).setOnClickListener(mFragment);
        view.findViewById(R.id.lv_history).setOnClickListener(mFragment);
        view.findViewById(R.id.lv_follow).setOnClickListener(mFragment);
        view.findViewById(R.id.tv_vip_center).setOnClickListener(mFragment);
        view.findViewById(R.id.tv_my_release).setOnClickListener(mFragment);
        view.findViewById(R.id.tv_privacy).setOnClickListener(mFragment);
        view.findViewById(R.id.tv_survey).setOnClickListener(mFragment);
        view.findViewById(R.id.tv_version).setOnClickListener(mFragment);
        view.findViewById(R.id.tv_logout).setOnClickListener(mFragment);
        view.findViewById(R.id.tv_login).setOnClickListener(mFragment);

        ivLevel = view.findViewById(R.id.iv_level);
        tvName = view.findViewById(R.id.tv_name);
        ivHead = view.findViewById(R.id.iv_head);
        tvLogin = view.findViewById(R.id.tv_login);
    }

    /**
     * checkSignIn
     */
    public void checkSignIn() {
        mUser = mUserRepository.getCurrentUser();
        if (mUser == null) { // no sign
            Log.d(LogConfig.TAG, "no sign");
            tvName.setVisibility(View.GONE);
            ivHead.setVisibility(View.GONE);
            ivLevel.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
            return;
        }

        // sign in
        tvLogin.setVisibility(View.GONE);
        tvName.setVisibility(View.VISIBLE);
        ivLevel.setVisibility(View.VISIBLE);
        ivHead.setVisibility(View.VISIBLE);

        tvName.setText(mUser.getHuaweiAccount().getDisplayName());
        ivLevel.setImageResource(mUser.isMember() ? R.mipmap.icon_vip : R.mipmap.icon_no_vip);

        if (mUser.getHuaweiAccount().getAvatarUri() == Uri.EMPTY) {
            Glide.with(mFragment).load(R.mipmap.head_my).apply(option).into(ivHead);
        } else {
            Glide.with(mFragment).load(mUser.getHuaweiAccount().getAvatarUriString()).apply(option).into(ivHead);
        }
    }

    /**
     * onResume
     */
    public void onResume() {
        MemberUtil.getInstance()
            .isMember(((BaseFragment) mFragment).getActivity(), mUser,
                (isMember, isAutoRenewing, productName, time) -> {
                    if (mUser != null && mUserRepository != null) {
                        mUser = mUserRepository.getCurrentUser();
                    }
                });
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.tv_login:
                signIn();
                break;
            case R.id.tv_vip_center:
                checkMember();
                break;
            case R.id.lv_favorite:
                jumpToTargetActivity(FavoriteActivity.class);
                break;
            case R.id.lv_follow:
                jumpToTargetActivity(FollowActivity.class);
                break;
            case R.id.lv_history:
                jumpToTargetActivity(BrowsingHistoryActivity.class);
                break;
            case R.id.tv_my_release:
                jumpToTargetActivity(MineNewsActivity.class);
                break;
            case R.id.tv_privacy:
                Context context = ((BaseFragment) mFragment).getContext();
                if (context != null) {
                    Intent intentPrivacy = new Intent(context, PrivacyActivity.class);
                    intentPrivacy.putExtra("innerFlag", 1);
                    context.startActivity(intentPrivacy);
                }
                break;
            case R.id.tv_survey:
                Context ctx = ((BaseFragment) mFragment).requireContext();
                ctx.startActivity(new Intent(ctx, FeedbackActivity.class));
                break;
            case R.id.tv_version:
                SystemUtil.checkForUpdates(((BaseFragment) mFragment).getActivity());
                break;
            case R.id.tv_logout:
                signOut();
                break;
            default:
                break;
        }
    }

    private void jumpToTargetActivity(Class<?> cls) {
        Context context = ((BaseFragment) mFragment).getContext();
        if (context == null) {
            return;
        }
        mUser = mUserRepository.getCurrentUser();
        if (mUser == null) {
            Toast.makeText(context, R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    private void checkMember() {
        Context context = ((BaseFragment) mFragment).getContext();
        if (context == null) {
            return;
        }
        mUser = mUserRepository.getCurrentUser();
        if (mUser == null) {
            Toast.makeText(context, R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
            return;
        }

        if (MemberUtil.getInstance().isMemberChecked()) {
            jumpToMemberPage();
        } else {
            FragmentActivity activity = ((BaseFragment) mFragment).getActivity();
            if (null == activity) {
                return;
            }
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).showLoadDialog();
                MemberUtil.getInstance().isMember(activity, mUser, (isMember, isAutoRenewing, productName, time) -> {
                    mUser = mUserRepository.getCurrentUser();
                    ((MainActivity) activity).hideLoadDialog();
                    jumpToMemberPage();
                });
            }
        }
    }

    private void jumpToMemberPage() {
        if (!mUser.isMember()) {
            Context context = ((BaseFragment) mFragment).getContext();
            if (context != null) {
                ((BaseFragment) mFragment).startActivity(new Intent(context, MemberActivity.class));
            }
        } else {
            jumpToMemberMgt();
        }
    }

    private void signIn() {
        MainActivity activity = (MainActivity) ((BaseFragment) mFragment).getActivity();
        if (null == activity) {
            return;
        }
        activity.showLoadDialog();
        activity.signIn(new OnLoginListener() {
            @Override
            public void loginSuccess(AuthAccount authAccount) {
                checkSignIn();
                Toast.makeText(activity, R.string.log_in_success, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(BaseType.EVENT_REFRESH_DATA);
                activity.hideLoadDialog();
            }

            @Override
            public void loginFailed(String errorMsg) {
                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show();
                activity.hideLoadDialog();
            }
        });
    }

    private void signOut() {
        if (mUser == null) {
            Toast.makeText(((BaseFragment) mFragment).getContext(), R.string.please_sign_first, Toast.LENGTH_SHORT)
                .show();
            return;
        }
        Bundle data = new Bundle();
        data.putString(CONFIRM_BUTTON, ((BaseFragment) mFragment).getString(R.string.confirm));
        data.putString(CANCEL_BUTTON, ((BaseFragment) mFragment).getString(R.string.cancel));
        data.putString(CONTENT, ((BaseFragment) mFragment).getString(R.string.confirm_log_out));

        Context context = ((BaseFragment) mFragment).getContext();
        if (context == null) {
            return;
        }
        BaseDialog dialog = new BaseDialog(context, data, true);
        dialog.setConfirmListener(v -> {
            BaseActivity activity = (BaseActivity) mFragment.getActivity();
            if (null != activity) {
                activity.signOut(() -> {
                    checkSignIn();
                    Toast.makeText(mFragment.getContext(), R.string.log_out_success, Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(BaseType.EVENT_REFRESH_DATA);
                });
            }
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void jumpToMemberMgt() {
        StartIapActivityReq req = new StartIapActivityReq();
        req.setType(StartIapActivityReq.TYPE_SUBSCRIBE_MANAGER_ACTIVITY);
        final FragmentActivity activity = ((BaseFragment) mFragment).requireActivity();
        IapClient mClient = Iap.getIapClient(activity);
        Task<StartIapActivityResult> task = mClient.startIapActivity(req);
        task.addOnSuccessListener(result -> {
            if (result != null) {
                result.startActivity(activity);
            }
        }).addOnFailureListener(e -> Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
    }
}
