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

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.agconnect.crash.BuildConfig;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.network.NetworkKit;
import com.huawei.industrydemo.news.MainActivity;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivityViewModel;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.constants.KitConstants;
import com.huawei.industrydemo.news.constants.LogConfig;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.page.activity.SearchActivity;
import com.huawei.industrydemo.news.page.activity.news.NewsPublishActivity;
import com.huawei.industrydemo.news.page.fragment.HomeFragment;
import com.huawei.industrydemo.news.page.fragment.MyFragment;
import com.huawei.industrydemo.news.page.fragment.VideoFragment;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;
import com.huawei.industrydemo.news.utils.agc.RemoteConfigUtil;
import com.huawei.industrydemo.news.utils.hms.AnalyticsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class MainActivityViewModel extends BaseActivityViewModel<MainActivity> implements KitConstants {

    private static final int HOME_INDEX = 0;

    private static final int VIDEO_INDEX = 1;

    private static final int MY_INDEX = 2;

    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    // Home
    private HomeFragment homeFragment;

    // video
    private VideoFragment videoFragment;

    // Mine
    private MyFragment myFragment;

    private CardView homeBar;

    // Records created fragments.
    private final List<Fragment> fragmentList = new ArrayList<>();

    private RadioGroup mTabRadioGroup;

    private final Map<Integer, Integer> pageIndex = new HashMap<>();

    private User user;

    /**
     * constructor
     *
     * @param mainActivity Activity object
     */
    public MainActivityViewModel(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public void initView() {
        homeBar = mActivity.findViewById(R.id.bar_home);

        mActivity.findViewById(R.id.bar_rv_search).setOnClickListener(mActivity);

        mActivity.findViewById(R.id.iv_hms).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.iv_release).setOnClickListener(mActivity);

        mTabRadioGroup = mActivity.findViewById(R.id.tabs_rg);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        pageIndex.put(R.id.tab_home, HOME_INDEX);
        pageIndex.put(R.id.tab_video, VIDEO_INDEX);
        pageIndex.put(R.id.tab_my, MY_INDEX);

    }

    /**
     * initFragment
     */
    public void initFragment() {
        homeFragment = new HomeFragment();
        addFragment(homeFragment);
        showFragment(homeFragment);
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.bar_rv_search:
                mActivity.startActivity(new Intent(mActivity, SearchActivity.class));
                break;
            case R.id.iv_hms:
                break;
            case R.id.iv_release:
                UserRepository userRepository = new UserRepository();
                user = userRepository.getCurrentUser();
                boolean test = false;
                if (user != null || test) {
                    mActivity.startActivity(new Intent(mActivity, NewsPublishActivity.class));
                } else {
                    Toast.makeText(mActivity, R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void addFragment(BaseFragment fragment) {
        if (!fragment.isAdded()) {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.frame_main, fragment).commit();
            fragmentList.add(fragment);
        }
    }

    private void showFragment(BaseFragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                mActivity.getSupportFragmentManager().beginTransaction().hide(frag).commit();
            }
        }
        mActivity.getSupportFragmentManager().beginTransaction().show(fragment).commit();
        if (fragment == homeFragment) {
            mActivity.setKitList(new String[] {ADS_ADS, ML_IMAGE, LOCATION_LBS});
        } else if (fragment == videoFragment) {
            mActivity.setKitList(new String[] {VIDEO_PLAY});
        } else {
            mActivity.setKitList(new String[] {ACCOUNT_LOGIN});
        }
    }

    private final RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener =
        new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(LogConfig.TAG, "onCheckedChanged");
                switch (checkedId) {
                    case R.id.tab_home: // Home
                        if (homeFragment == null) {
                            homeFragment = new HomeFragment();
                        }
                        homeBar.setVisibility(View.VISIBLE);
                        addFragment(homeFragment);
                        showFragment(homeFragment);
                        break;
                    case R.id.tab_video: // Video
                        if (videoFragment == null) {
                            videoFragment = new VideoFragment();
                        }
                        homeBar.setVisibility(View.VISIBLE);
                        addFragment(videoFragment);
                        showFragment(videoFragment);
                        break;
                    case R.id.tab_my: // My
                        if (myFragment == null) {
                            myFragment = new MyFragment();
                        }
                        homeBar.setVisibility(View.GONE);
                        addFragment(myFragment);
                        showFragment(myFragment);
                        break;
                    default:
                        break;
                }
            }
        };

    /**
     * init HMS Core
     */
    public void initHms() {
        initAnalytics();
        RemoteConfigUtil.init();
        initNetworkKit();
        initMLKit();
        user = new UserRepository().getCurrentUser();
        if (user != null) { // update memberStatus
            mActivity.signIn(null);
        }
    }

    private void initAnalytics() {
        if (BuildConfig.DEBUG) {
            HiAnalyticsTools.enableLog();
            AGConnectCrash.getInstance().enableCrashCollection(false);
        }
        AnalyticsUtil.getInstance(mActivity).setAnalyticsEnabled(true);
    }

    private void initNetworkKit() {
        NetworkKit.init(mActivity, new NetworkKit.Callback() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    Log.i(TAG, "Network kit init success");
                } else {
                    AgcUtil.reportFailure(TAG, "Network kit init failed");
                }
            }
        });
    }

    private void initMLKit() {
        MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(mActivity));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        homeFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * backToHomeFragment
     *
     * @return isSuccess
     */
    public boolean backToHomeFragment() {
        if (mTabRadioGroup.getCheckedRadioButtonId() != R.id.tab_home) {
            mTabRadioGroup.check(R.id.tab_home);
            return true;
        }
        return false;
    }

}
