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

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseFragment;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.entity.Follow;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.page.activity.ImageSuperActivity;
import com.huawei.industrydemo.news.repository.FollowRepository;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.SystemUtil;
import com.huawei.industrydemo.news.utils.storage.ICloudDataChangedLister;
import com.huawei.industrydemo.news.repository.NewsRepository;
import com.huawei.industrydemo.news.utils.storage.NewsRequestUtil;
import com.huawei.industrydemo.news.viewadapter.NewsListAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class NewsListFragment extends BaseFragment {
    private String TAG = NewsListFragment.class.getSimpleName();

    private int position;

    private SmartRefreshLayout smartRefreshLayout;

    private RecyclerView recyclerView;

    private NewsListAdapter newsListAdapter;

    private TextView tvNoData;

    private TextView tvTipLogin;

    private ViewGroup lImageSuper;

    private View imageSuper1;
    private View imageSuper2;
    private View imageSuper3;

    private List<News> list;

    private List<News> newsList = new ArrayList<>();

    private boolean isOnStop = false;

    private boolean isInitNewsInRoom = false;

    private int lastIndex = -1;

    private Activity mActivity;

    private final String NEWS_JSON_PATH = "news_info/json/";
    private NewsRequestUtil newsRequestUtil;
    private NewsRepository newsRepository;

    public NewsListFragment() {

    }

    public NewsListFragment(int position) {
        // Required empty public constructor
        this.position = position;
    }

    ICloudDataChangedLister iCloudDataChangedLister = new ICloudDataChangedLister() {
        @Override
        public void dataUpdated(Object o, int downloadCount) {
            Log.d(TAG, this + " dataUpdated:downloadCount " + downloadCount);
            News news = (News) o;
            newsList.add(news);
            if (!isInitNewsInRoom) {
                newsRepository.insert(news);
                newsListAdapter.refresh(SystemUtil.filterNewsByLang(newsList));
            }
            Log.d(TAG, "dataUpdated:room  " + newsRepository.getAll());
            int fileCount = newsRequestUtil.getFileCount();
            Log.d(TAG, "dataUpdated:cloud fileCount " + fileCount);
            if (fileCount == downloadCount) {
                Log.w(TAG, "dataUpdated: finish");
                newsRepository.deleteByFlag(BaseType.NEWS_CLOUD_STORE);
                Collections.sort(newsList);
                newsRepository.insertNewsList(newsList);
                refreshDataFromRoom();
                smartRefreshLayout.finishRefresh(true);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        Log.d(TAG, "onMessageEvent:event " + event);
        if (BaseType.EVENT_REFRESH_DATA.equals(event)) {
            refreshDataFromRoom();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = mActivity == null ? (Activity) context : mActivity;
        EventBus.getDefault().register(this);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: 1 " + this);
        mActivity = (mActivity == null && getActivity() != null) ? (Activity) getActivity() : mActivity;
        newsListAdapter = new NewsListAdapter(mActivity, position);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initView(view);
        newsRequestUtil = new NewsRequestUtil();
        newsRepository = new NewsRepository();

        isInitNewsInRoom = refreshDataFromRoom();
        if (position == BaseType.HOME_CATEGORY_1_ALL) {
            getNewsFromCloud();
        }
        return view;
    }

    private boolean refreshDataFromRoom() {
        Log.d(TAG, "refreshDataFromRoom:position " + position);
        checkUserLogin();
        List<News> newsListFromRoom = newsRepository.queryHomeAll();
        if (newsListFromRoom.size() > 0) {
            Collections.sort(newsListFromRoom);
            Log.i(TAG, "refreshDataFromRoom:newsListFromRoom " + newsListFromRoom.toString());
            lImageSuper.setVisibility(View.GONE);
            switch (position) {
                case BaseType.HOME_CATEGORY_0_FOLLOW:
                    FollowRepository followRepository = new FollowRepository();
                    UserRepository userRepository = new UserRepository();
                    User user = userRepository.getCurrentUser();
                    if (user != null) {
                        List<Follow> follows = followRepository.queryByUserId(user.getOpenId());
                        List<News> newsListFollow = new ArrayList<>();
                        for (int i = 0; i < follows.size(); i++) {
                            List<News> tempNews = newsRepository.queryByPublisher(follows.get(i).getFollowId());
                            Log.d(TAG, "refreshDataFromRoom:followId " + follows.get(i).getFollowId());
                            newsListFollow.addAll(tempNews);
                        }
                        refreshDateByOrder(newsListFollow);
                    }
                    break;
                case BaseType.HOME_CATEGORY_1_ALL:
                    lImageSuper.setVisibility(View.VISIBLE);
                    refreshDateByOrder(newsListFromRoom);
                    break;
                default:
                    String cate = String.valueOf(position - BaseType.HOME_CATEGORY_1_DIFF);
                    List<News> newsListCate = newsRepository.queryByCate(cate);
                    newsListCate = SystemUtil.filterNewsByLang(newsListCate);
                    refreshDateByOrder(newsListCate);
                    break;
            }

            return true;
        } else {
            return false;
        }
    }

    private void refreshDateByOrder(List<News> newsListCate) {
        if (newsListCate == null) {
            Log.e(TAG, "refreshDateByOrder: null ");
            return;
        }
        Collections.sort(newsListCate);
        newsListAdapter.refresh(newsListCate);
    }


    private void initView(View view) {
        smartRefreshLayout = view.findViewById(R.id.sf_news);
        recyclerView = view.findViewById(R.id.rv_news);
        tvNoData = view.findViewById(R.id.tv_no_data);
        tvTipLogin = view.findViewById(R.id.tv_please_login);
        lImageSuper = view.findViewById(R.id.lImageSuper);
        imageSuper1 = view.findViewById(R.id.superimg1);
        imageSuper2 = view.findViewById(R.id.superimg2);
        imageSuper3 = view.findViewById(R.id.superimg3);
        setListener();
        initList();
        list = new ArrayList<>();

        // pull down refresh
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            lastIndex = -1;
            list.clear();
            if (newsListAdapter != null) {
                newsListAdapter.notifyDataSetChanged();
            }
            Log.d(TAG, "OnRefreshListener: ");
            getNewsFromCloud();
        });

        // Load more
        if (false) {
            smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> getNewsFromCloud());
            smartRefreshLayout.autoRefresh();
        }
    }

    private void setListener() {
        imageSuper1.setOnClickListener(v -> ImageSuperActivity.start(R.mipmap.superresolution_image));
        imageSuper2.setOnClickListener(v -> ImageSuperActivity.start(R.mipmap.superresolution_image2));
        imageSuper3.setOnClickListener(v -> ImageSuperActivity.start(R.mipmap.superresolution_image3));
    }


    private void checkUserLogin() {
        Log.d(TAG, "checkUserLogin:position " + position);
        if (position == BaseType.HOME_CATEGORY_0_FOLLOW) {
            UserRepository userRepository = new UserRepository();
            User user = userRepository.getCurrentUser();
            if (user != null) {
                recyclerView.setVisibility(View.VISIBLE);
                tvTipLogin.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvTipLogin.setVisibility(View.VISIBLE);
            }
            Log.i(TAG, "checkUserLogin:user " + user);
        }
    }

    private void getNewsFromCloud() {
        if (!isNetworkConnected(mActivity)) {
            Toast.makeText(mActivity, "Please check network state", Toast.LENGTH_SHORT).show();
            smartRefreshLayout.finishRefresh(false);
            return;
        }
        checkUserLogin();
        newsList = new ArrayList<>();
        newsRequestUtil.setiCloudDataChangedLister(iCloudDataChangedLister);
        newsRequestUtil.getFileList(NEWS_JSON_PATH);
    }

    @Override
    public void onStop() {
        super.onStop();
        isOnStop = true;
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsListAdapter);
    }


    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

}