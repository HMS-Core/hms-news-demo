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

package com.huawei.industrydemo.news.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.constants.LogConfig;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.inteface.OnLoginListener;
import com.huawei.industrydemo.news.inteface.OnLogoutListener;
import com.huawei.industrydemo.news.inteface.OnNonDoubleClickListener;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.KitTipUtil;
import com.huawei.industrydemo.news.utils.MemberUtil;
import com.huawei.industrydemo.news.utils.hms.AnalyticsUtil;

import static com.huawei.industrydemo.news.constants.RequestCodeConstants.LOGIN_REQUEST_CODE;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class BaseActivity extends AppCompatActivity implements LogConfig {
    private OnLoginListener onLoginListener;

    private AccountAuthParams authParams;

    private View loadView;

    String[] kitList;

    private LinearLayout mFloatLayout;

    /**
     * Control the display of floating window
     */
    protected boolean isShowFloat = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loadView = LayoutInflater.from(this).inflate(R.layout.view_pb, null);
        authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken()
            .setDialogAuth()
            .createParams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSmartAssistantFloat();
    }

    /**
     * addTipView
     */
    private void addTipView() {
        String[] kitInfo = getKitList();
        KitTipUtil.addTipView(this, KitTipUtil.getKitMap(kitInfo));
    }

    public void setKitList(String[] kits) {
        if (kits.length == 0) {
            return;
        }
        kitList = kits.clone();
        if (this.mFloatLayout != null) {
            this.mFloatLayout.setVisibility(View.VISIBLE);
        }
    }

    private String[] getKitList() {
        if (kitList == null) {
            return new String[0];
        }
        return kitList.clone();
    }

    /**
     * showLoadDialog
     */
    public void showLoadDialog() {
        FrameLayout frameLayout = findViewById(android.R.id.content);
        if (loadView != null) {
            loadView.setOnClickListener(v1 -> {
                // Forbidden to click the view
            });
        }
        frameLayout.addView(loadView);
    }

    /**
     * hideLoadDialog
     */
    public void hideLoadDialog() {
        FrameLayout frameLayout = findViewById(android.R.id.content);
        frameLayout.removeView(loadView);
    }

    /**
     * signIn requestCode = RequestCodeConstants#LOGIN_REQUEST_CODE
     *
     * @param onLoginListener OnLoginListener
     */
    public void signIn(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
        AccountAuthService service = AccountAuthManager.getService(this, authParams);
        startActivityForResult(service.getSignInIntent(), LOGIN_REQUEST_CODE);
    }

    /**
     * signOut
     *
     * @param logoutListener logoutListener
     */
    public void signOut(OnLogoutListener logoutListener) {
        AccountAuthService service = AccountAuthManager.getService(this, authParams);
        service.signOut().addOnSuccessListener(aVoid -> {
            new UserRepository().setCurrentUser(null);
            revokingAuth();
            if (logoutListener != null) {
                logoutListener.logoutSuccess();
            }
        });
    }

    private void revokingAuth() {
        // service indicates the AccountAuthService instance generated using the getService method during the sign-in
        // authorization.
        AccountAuthManager.getService(this, authParams).cancelAuthorization().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Processing after a successful authorization revoking.
                Log.i(TAG, "onSuccess: ");
            } else {
                // Handle the exception.
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    int statusCode = ((ApiException) exception).getStatusCode();
                    Log.i(TAG, "onFailure: " + statusCode);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                AuthAccount authAccount = authAccountTask.getResult();
                saveUserAccountInfo(authAccount);
            } else {
                Exception exception = authAccountTask.getException();
                if (exception instanceof ApiException) {
                    Log.e(TAG, "sign in failed : " + ((ApiException) exception).getStatusCode());
                } else {
                    Log.e(TAG, "sign in failed : " + exception);
                }
                if (onLoginListener != null) {
                    onLoginListener.loginFailed(exception.getMessage());
                }
            }
        }
    }

    /**
     * Save User Account
     *
     * @param authAccount Signed-in HUAWEI ID information, including the ID, nickname, profile picture URI, permission,
     *        and access token.
     */
    private void saveUserAccountInfo(AuthAccount authAccount) {
        if (authAccount != null) {
            String openId = authAccount.getOpenId();
            UserRepository userRepository = new UserRepository();
            User user = userRepository.queryByOpenId(openId);
            if (user == null) {
                user = new User();
            }
            user.setOpenId(openId);
            user.setPrivacyFlag(true);
            user.setHuaweiAccount(authAccount);
            userRepository.setCurrentUser(user);

            MemberUtil.getInstance().isMember(this, user, (isMember, isAutoRenewing, productName, time) -> {
                if (onLoginListener != null) {
                    onLoginListener.loginSuccess(authAccount);
                }
            });
            AnalyticsUtil.getInstance(this).setUserId(authAccount.getUid());
        }
    }

    /**
     * display floating window
     */
    @SuppressLint("ClickableViewAccessibility")
    public void showSmartAssistantFloat() {
        Log.d(TAG, "showSmartAssistantFloat");
        if (mFloatLayout != null || !isShowFloat) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.kit_button, null);
        mFloatLayout.setOnClickListener(new OnNonDoubleClickListener() {
            @Override
            public void run(View v) {
                addTipView();
            }
        });

        mFloatLayout.setOnTouchListener(new StackViewTouchListener(mFloatLayout, 18 / 4));

        FrameLayout.LayoutParams layoutParams =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = 250;
        layoutParams.rightMargin = 10;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        addContentView(mFloatLayout, layoutParams);
        if (kitList == null) {
            mFloatLayout.setVisibility(View.INVISIBLE);
        }
    }

    static class StackViewTouchListener implements View.OnTouchListener {

        private final View stackView;

        private final int clickLimitValue;

        private float dynamicX = 0F;

        private float dynamicY = 0F;

        private float downX = 0F;

        private float downY = 0F;

        private boolean isClickState;

        public StackViewTouchListener(View stackView, int clickLimitValue) {
            this.stackView = stackView;
            this.clickLimitValue = clickLimitValue;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float tempX = event.getRawX();
            float tempY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClickState = true;
                    downX = tempX;
                    downY = tempY;
                    dynamicX = stackView.getX() - event.getRawX();
                    dynamicY = stackView.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(tempX - downX) < clickLimitValue && Math.abs(tempY - downY) < clickLimitValue) {
                        isClickState = true;
                    } else {
                        isClickState = false;
                        stackView.setX(event.getRawX() + dynamicX);
                        stackView.setY(event.getRawY() + dynamicY);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (isClickState) {
                        stackView.performClick();
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

}
