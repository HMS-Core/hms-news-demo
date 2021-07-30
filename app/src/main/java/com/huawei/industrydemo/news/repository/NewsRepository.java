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

package com.huawei.industrydemo.news.repository;

import android.util.Log;

import com.huawei.industrydemo.news.AppDatabase;
import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.dao.NewsDao;
import com.huawei.industrydemo.news.utils.DatabaseUtil;
import com.huawei.industrydemo.news.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;



/**
 * News Repository
 * 
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class NewsRepository {
    private String TAG = NewsRepository.class.getSimpleName();
    private final NewsDao newsDao;

    private final AppDatabase database;

    public NewsRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.newsDao = database.newsDao();
    }

    /**
     * queryByFlag
     * @param flag flag
     * @return news list
     */
    public List<News> queryByFlag(int flag) {
        return newsDao.queryByFlag(flag);
    }


    /**
     * queryByCate
     * @param flag  cate
     * @return news list
     */
    public List<News> queryByCate(String flag) {
        return newsDao.queryByCate(flag);
    }


    public List<News> getAll() {
        return newsDao.getAll();
    }

    /**
     * insertNewsList
     * @param newsList news list
     */
    public void insertNewsList(List<News> newsList) {
        newsDao.insertNewsList(newsList);
    }

    /**
     * insert
     * @param news news
     */
    public void insert(News news) {
        newsDao.insert(news);
    }

    /**
     * deleteAll
     */
    public void deleteAll() {
        newsDao.deleteAll();
    }

    /**
     * deleteByFlag
     * @param flag news flag
     */
    public void deleteByFlag(@BaseType.NewsFlag int flag) {
        newsDao.deleteByFlag(flag);
        Log.d(TAG, "deleteByFlag:flag " + flag + " getAll() " + getAll());
    }

    /**
     * queryById
     * @param id id
     * @return news
     */
    public News queryById(String id) {
        return newsDao.queryById(id);
    }

    /**
     * queryByLang
     * @param lang  language
     * @return news
     */
    public List<News> queryByLang(@BaseType.NewsLangType  String lang) {
        return newsDao.queryByLang(lang);
    }

    /**
     * queryHomeAll recommend
     * @return newsList
     */
    public List<News> queryHomeAll(){
        List<News> newsList = new ArrayList<>();
        newsList.addAll(queryByLang(SystemUtil.getLanguage()));
        newsList.addAll(queryByFlag(BaseType.NEWS_MINE_LOCAL));
        return newsList;
    }

    public List<News> queryByPublisher(String publisher) {
        return newsDao.queryByPublisher(publisher);
    }
}