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

package com.huawei.industrydemo.news.page.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.service.LocationService;
import com.huawei.industrydemo.news.viewadapter.NewsViewPagerAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.huawei.industrydemo.news.constants.Constants.OFF_LOAD_PAGE_COUNT;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class HomeFragment extends BaseFragment {
    private String TAG = HomeFragment.class.getSimpleName();

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private final static int VIEWPAGE_INIT_PAGE = 1;

    private Activity mActivity = null;

    private LocationService locationService;

    private TextView textViewCity;

    private static final int WIDTH_CH = 78;

    private static final int WIDTH_EN = 38;

    LocationService.ILocationChangedLister iLocationChangedLister = new LocationService.ILocationChangedLister() {
        @Override
        public void locationChanged(Location location) {
            Log.d(TAG, "locationChanged: " + location.getLatitude() + " lon: " + location.getLongitude());
            String text = transLocationToGeoCoder(location);
            textViewCity.setText(text);
        }
    };

    boolean isBound = false;

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBound = true;
            if (binder instanceof LocationService.MyBinder) {
                LocationService.MyBinder myBinder = (LocationService.MyBinder) binder;
                locationService = myBinder.getService();
                Log.d(TAG, "ActivityA onServiceConnected");
                if (isGetLocationPermission()) {
                    locationService.addLocationChangedlister(iLocationChangedLister);
                    locationService.getMyLoction();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            Log.i(TAG, "ActivityA onServiceDisconnected");
            isBound = false;
        }
    };

    private boolean isGetLocationPermission() {
        Log.d(TAG, "isGetLocationPermission: b");
        if (ActivityCompat.checkSelfPermission(mActivity,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
            ActivityCompat.requestPermissions(mActivity, strings, 2);
            Log.d(TAG, "isGetLocationPermission: false");
            return false;
        }
        Log.d(TAG, "isGetLocationPermission: ture");
        return true;
    }

    public HomeFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mActivity = (mActivity == null && getActivity() != null) ? getActivity() : mActivity;
        initView(view);
        if (isGetLocationPermission()) {
            bindLocationService();
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        if (isBound) {
            mActivity.unbindService(conn);
        }
    }

    private void bindLocationService() {
        Intent intent = new Intent(mActivity, LocationService.class);
        mActivity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private void initView(View view) {
        textViewCity = view.findViewById(R.id.city);
        mTabLayout = view.findViewById(R.id.news_tab_layout);
        mViewPager = view.findViewById(R.id.news_view_pager);
        mViewPager.setOffscreenPageLimit(OFF_LOAD_PAGE_COUNT);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            mViewPager.setAdapter(new NewsViewPagerAdapter(activity.getSupportFragmentManager(), activity));
        }
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(VIEWPAGE_INIT_PAGE);
        reflex(mTabLayout);
    }

    private void reflex(final TabLayout tabLayout) {
        int count = tabLayout.getTabCount();

        for (int i = 0; i < count; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab == null) {
                continue;
            }
            CharSequence text = tab.getText();
            String string = (text == null) ? "" : text.toString();
            Log.d(TAG, "reflex: " + string);
            int len = string.length();
            Log.d(TAG, "reflex:len " + len);
            TabLayout.TabView tabView = tab.view;
            ViewGroup.LayoutParams lp = tabView.getLayoutParams();
            int cnCount = getChineseCount(string);
            int enCount = len - cnCount;
            lp.width = WIDTH_CH * cnCount + WIDTH_EN * enCount;
            tabView.setLayoutParams(lp);
        }
    }

    private int getChineseCount(String content) {
        int count = 0;
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    bindLocationService();
                } else {
                    Log.i(TAG, "onRequestPermissionsResult denied");
                }
                break;
            }
            default:
                break;
        }
    }

    private String transLocationToGeoCoder(Location location) {
        String mSendText = "";
        try {
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            mSendText = addressList != null && addressList.size() > 0 ? addressList.get(0).getLocality()
                : mActivity.getString(R.string.empty_address);
            Log.i(TAG, "geocoder  : " + mSendText);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return mSendText;
        }
        return mSendText;
    }
}