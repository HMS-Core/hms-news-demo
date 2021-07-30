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

package com.huawei.industrydemo.news.page.activity.news;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.base.BaseActivity;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.constants.Constants;
import com.huawei.industrydemo.news.databinding.ActivityShowArtBinding;
import com.huawei.industrydemo.news.entity.Avatar;
import com.huawei.industrydemo.news.entity.BrowsingHistory;
import com.huawei.industrydemo.news.entity.Favorite;
import com.huawei.industrydemo.news.entity.Follow;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.repository.AvatarRepository;
import com.huawei.industrydemo.news.repository.FavoriteRepository;
import com.huawei.industrydemo.news.repository.FollowRepository;
import com.huawei.industrydemo.news.repository.NewsRepository;
import com.huawei.industrydemo.news.repository.UserRepository;
import com.huawei.industrydemo.news.utils.ContextUtil;
import com.huawei.industrydemo.news.utils.DatabaseUtil;
import com.huawei.industrydemo.news.utils.TranslateUtil;
import com.huawei.industrydemo.news.utils.agc.AppLinkUtils;
import com.huawei.industrydemo.news.utils.hms.LanguageDetectionUtil;
import com.huawei.industrydemo.news.utils.hms.MessagingUtil;
import com.huawei.industrydemo.news.utils.hms.TtsUtil;
import com.huawei.industrydemo.news.utils.richtext.RichUtils;
import com.huawei.industrydemo.news.wight.BaseDialog;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import static android.webkit.WebSettings.LOAD_NO_CACHE;
import static com.huawei.industrydemo.news.constants.BaseType.NEWS_MINE_LOCAL;
import static com.huawei.industrydemo.news.constants.Constants.NEWSREAD;
import static com.huawei.industrydemo.news.constants.KitConstants.ANALYTICS_REPORT;
import static com.huawei.industrydemo.news.constants.KitConstants.ML_TRANSLATION;
import static com.huawei.industrydemo.news.constants.KitConstants.ML_TTS;
import static com.huawei.industrydemo.news.constants.KitConstants.PUSH_NOTIFY;
import static com.huawei.industrydemo.news.utils.hms.AnalyticsUtil.newsReadReport;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class NewsViewActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = NewsViewActivity.class.getSimpleName();

    private static final String KET_NEWS = "KET_NEWS";

    private String selectWords;

    private FavoriteRepository favoriteRepository;

    private News news;

    private User user;

    private FollowRepository followRepository;

    private NewsRepository newsRepository;

    ActivityShowArtBinding binding;

    String title;

    String content;

    String publisher = "";

    String date = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_art);

        UserRepository userRepository = new UserRepository();
        user = userRepository.getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
        title = sharedPreferences.getString("title", "title");
        content = sharedPreferences.getString("content", "");

        newsRepository = new NewsRepository();
        Intent intent = getIntent();
        if (intent != null) {
            String newId = intent.getStringExtra("newId");
            if (null != newId) { // From push message
                news = newsRepository.queryById(newId);
            } else {
                if (intent.getData() != null) { // From Applink
                    newId = intent.getData().getQueryParameter("newId");
                    if (null != newId) {
                        news = newsRepository.queryById(newId);
                    }
                } else { // From News list
                    news = (News) intent.getSerializableExtra(KET_NEWS);
                }
            }

            if (news != null) {
                title = news.getTitle();
                content = news.getContent();
                publisher = news.getPublisher();
                date = news.getPublishDate();
                if (news.getFlag() == NEWS_MINE_LOCAL) {
                    binding.follow.setVisibility(View.GONE);
                    binding.store.setVisibility(View.GONE);
                    if (user == null || user.getHuaweiAccount().getAvatarUri() == Uri.EMPTY) {
                        Glide.with(this).load(R.mipmap.head_my).into(binding.avatar);
                    } else {
                        Glide.with(this).load(user.getHuaweiAccount().getAvatarUriString()).into(binding.avatar);
                    }
                } else {
                    HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
                    Bundle bundle = new Bundle();
                    bundle.putString("NewsID", news.id);
                    bundle.putString("NewTitle", title);
                    bundle.putString("Publisher", publisher);
                    instance.onEvent(NEWSREAD, bundle);
                }
            }
        }

        Log.d(TAG, "onCreate:content " + content);
        initWebView(content);
        binding.title.setText(title);
        binding.publisher.setText(publisher);
        binding.date.setText(date);
        initFavoriteStatus(news);
        initBrowsingHistory(news);
        initFollowStatus(news);
        AvatarRepository avatarRepository = new AvatarRepository();
        Avatar avatar = avatarRepository.queryByFlag(publisher);
        if (avatar != null) {
            String url = avatar.url;
            Glide.with(this).load(url).into(binding.avatar);
        }
        LanguageDetectionUtil.init(this);
        TtsUtil.init(this);
        setKitList(new String[]{ML_TRANSLATION, ML_TTS, PUSH_NOTIFY, ANALYTICS_REPORT});
    }

    private void initFavoriteStatus(News news) {
        if (null == news) {
            binding.store.setVisibility(View.GONE);
            return;
        }
        favoriteRepository = new FavoriteRepository();
        if (favoriteRepository.isFavorite(news.id)) {
            binding.store.setImageResource(R.mipmap.news_favorite);
        } else {
            binding.store.setImageResource(R.mipmap.my_favorite);
        }
    }

    private void initBrowsingHistory(News news) {
        BrowsingHistory browsingHistory = new BrowsingHistory("", "");
        if (news != null) {
            browsingHistory.setNewsId(news.id);
            if (user == null) {
                browsingHistory.setUserId(Constants.TOURIST_USERID);
            } else {
                browsingHistory.setUserId(user.getOpenId());
            }
        }
        DatabaseUtil.getDatabase().browsingHistoryDao().setHistoryData(browsingHistory);
    }

    private void initFollowStatus(News news) {
        if (null == news) {
            binding.follow.setVisibility(View.GONE);
            return;
        }
        followRepository = new FollowRepository();
        if (followRepository.isFollow(news.getPublisher())) {
            binding.follow.setText(R.string.followed);
            binding.follow.setTextColor(getResources().getColor(R.color.text_followed));
            binding.follow.setBackgroundResource(R.drawable.bg_followed);
        } else {
            binding.follow.setText(R.string.add_follow);
            binding.follow.setTextColor(getResources().getColor(R.color.text_add_follow));
            binding.follow.setBackgroundResource(R.drawable.bg_add_follow);
        }
    }

    /**
     * start
     *
     * @param news news
     */
    public static void start(News news) {
        Intent starter = new Intent(ContextUtil.context(), NewsViewActivity.class);
        starter.putExtra(KET_NEWS, news);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextUtil.context().startActivity(starter);
    }

    /**
     * onClick
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.smartRobot:
                read();
                break;
            case R.id.threeDots:

                break;
            case R.id.follow:
                if (null == user) {
                    Toast.makeText(this, R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (followRepository.isFollow(news.getPublisher())) {
                    followRepository.removeFollow(followRepository.queryByFollowId(news.getPublisher()));
                    binding.follow.setText(R.string.add_follow);
                    binding.follow.setTextColor(getResources().getColor(R.color.text_add_follow));
                    binding.follow.setBackgroundResource(R.drawable.bg_add_follow);
                } else {
                    if (null == publisher) {
                        return;
                    }
                    Follow follow = new Follow(user.getOpenId(), publisher);
                    followRepository.addFollow(follow);
                    binding.follow.setText(R.string.followed);
                    binding.follow.setTextColor(getResources().getColor(R.color.text_followed));
                    binding.follow.setBackgroundResource(R.drawable.bg_followed);
                    pushNews();
                }
                EventBus.getDefault().post(BaseType.EVENT_REFRESH_DATA);
                break;
            case R.id.store:
                if (null == user) {
                    Toast.makeText(this, R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (favoriteRepository.isFavorite(news.id)) {
                    favoriteRepository.removeFavorite(favoriteRepository.queryByNewsId(news.id));
                    binding.store.setImageResource(R.mipmap.my_favorite);
                } else {
                    Favorite favorite = new Favorite(user.getOpenId(), news.id);
                    favoriteRepository.addFavorite(favorite);
                    binding.store.setImageResource(R.mipmap.news_favorite);
                }
                break;
            case R.id.share:
                String newsId = news.id;
                Log.d(TAG, "onClickEvent: " + newsId);
                String suffix = "newId=" + newsId + "&";
                new AppLinkUtils(this, Constants.NEWS_DEEP_LINK).createAppLinkingAndShare(suffix);
                break;
            default:
                break;
        }
    }

    private void pushNews() {
        List<News> newsOfPublisher = newsRepository.queryByPublisher(publisher);
        if (null != newsOfPublisher && newsOfPublisher.size() > 1) {
            for (News newsTemp : newsOfPublisher) {
                String titleTemp = newsTemp.getTitle();
                if (!title.equals(titleTemp)) {
                    MessagingUtil.newsNotificationMessage(this, publisher, titleTemp, newsTemp.id);
                    break;
                }
            }
        }
    }

    private void read() {
        Document doc = Jsoup.parse(news.getContent());
        String newsContent = doc.body().text();
        String fullText = news.getTitle() + ". " + RichUtils.returnOnlyText(newsContent);
        Log.d(TAG, "Full text: " + fullText);
        String detectStr = fullText.length() > 100 ? fullText.substring(0, 100) : fullText;
        LanguageDetectionUtil.detect(this, detectStr, language -> {
            TtsUtil.stop();
            if (TtsUtil.changeLanguage(language)) {
                TtsUtil.start(fullText);
                newsReadReport();
            } else {
                String unSupport = getString(R.string.language_not_support, language);
                Toast.makeText(NewsViewActivity.this, unSupport, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * init
     *
     * @param data data
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(String data) {
        WebSettings settings = binding.webView.getSettings();
        settings.setLoadWithOverviewMode(true); // Set WebView overview type
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);

        binding.webView.setVerticalScrollBarEnabled(false); // No vertical scroller
        binding.webView.setHorizontalScrollBarEnabled(false); // No horizontal scroller
        settings.setTextSize(WebSettings.TextSize.NORMAL); // Change character size in HTML
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // Open new window with JS
        settings.setCacheMode(LOAD_NO_CACHE);

        binding.webView.getSettings().setJavaScriptEnabled(true);

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        binding.webView.setWebViewClient(new MyWebViewClient());
        binding.webView.setWebChromeClient(new WebChromeClient());
        data = "</Div><head><style>body{font-size:16px}</style>"
                + "<style>img{ width:100% !important;margin-top:0.4em;margin-bottom:0.4em}</style>"
                + "<style>ul{ padding-left: 1em;margin-top:0em}</style>"
                + "<style>ol{ padding-left: 1.2em;margin-top:0em}</style>" + "</head>" + data;

        ArrayList<String> arrayList = RichUtils.returnImageUrlsFromHtml(data);
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (!arrayList.get(i).contains("http")) {
                    // If doesn't contain http, it must be local path, file prefix is needed.
                    data = data.replace(arrayList.get(i), "file://" + arrayList.get(i));
                }
            }
        }
        binding.webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        webViewGetSelectedData(binding.webView, value -> {
            selectWords = value;
        });
        super.onActionModeStarted(actionMode(mode));
    }

    /**
     * Obtains the selected text content of the WebView.
     *
     * @param webView  webView
     * @param callBack callBack
     */
    public static void webViewGetSelectedData(WebView webView, WebViewGetSelectedDataCallBack callBack) {
        String js = "function selectEnable(document) {"
                + "    document.oncontextmenu = new Function(\"if(event){event.returnValue=true;}\");"
                + "    document.onselectstart = new Function(\"if(event){event.returnValue=true;}\");"
                + "    let frames = document.getElementsByTagName(\"iframe\");" + "    if (frames.length > 0) {"
                + "        for (var i = 0; i < frames.length; i++) {" + "            document = frames[i].contentDocument;"
                + "            selectEnable(document);" + "        }" + "    }" + "}" + "function selectTxt(document) {"
                + "    let rtnTxt = \"\";"
                + "    rtnTxt = document.getSelection ? document.getSelection().toString() : document.selection.createRange().text;"
                + "    let frames = document.getElementsByTagName(\"iframe\");" + "    if (frames.length > 0) {"
                + "        for (var i = 0; i < frames.length; i++) {" + "            document = frames[i].contentDocument;"
                + "            let sltTxt = selectTxt(document);" + "            if (sltTxt != \"\") {"
                + "                rtnTxt = sltTxt;" + "            }" + "        }" + "    }" + "    return rtnTxt;" + "}"
                + "(function(){" + "    selectEnable(document);" + "    return selectTxt(document);" + "})()";

        webView.evaluateJavascript("javascript:" + js, txt -> {
            callBack.onFinished(txt);
        });

    }

    public interface WebViewGetSelectedDataCallBack {
        /**
         * onFinished
         *
         * @param value value
         */
        void onFinished(String value);
    }

    private ActionMode actionMode(ActionMode actionMode) {
        if (actionMode != null) {
            Menu menu = actionMode.getMenu();
            for (int i = (menu.size() - 1); i >= 0; i--) {
                menu.removeItem(menu.getItem(i).getItemId());
            }
            menu.add(getResources().getString(R.string.ml_translate));
            MenuItem menuItem = menu.getItem(0);
            menuItem.setOnMenuItemClickListener(item -> {
                TranslateUtil.initPopupMenu(NewsViewActivity.this, binding.positionMarkers, selectWords);
                return false;
            });
        }
        return actionMode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.repet_editor:
                Intent intent = new Intent(NewsViewActivity.this, NewsPublishActivity.class);
                intent.putExtra("isFrom", 1);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * webView
     */
    private static class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    ;

    private void showStopReadingTips() {
        TtsUtil.pause();
        Bundle bundle = new Bundle();
        bundle.putString(BaseDialog.CONTENT, getString(R.string.stop_reading_tips));
        BaseDialog baseDialog = new BaseDialog(this, bundle, true);
        baseDialog.setConfirmListener(v1 -> {
            TtsUtil.stop();
            baseDialog.dismiss();
        });
        baseDialog.setOnDismissListener(dialog -> TtsUtil.resume());
        baseDialog.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (TtsUtil.isSpeaking()) {
            showStopReadingTips();
            return false;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void onBackPressed() {
        if (TtsUtil.isSpeaking()) {
            showStopReadingTips();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        TtsUtil.shutdown();
        LanguageDetectionUtil.stop();
        super.onDestroy();
    }
}
