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

package com.huawei.industrydemo.news.entity.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huawei.industrydemo.news.constants.BaseType;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.User;

import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
@Dao
public interface NewsDao {
    /**
     * Query a product based on language and number.
     *
     * @param flag news flag
     * @return News list
     */
    @Query("SELECT * FROM news WHERE (flag=:flag)")
    List<News> queryByFlag(int flag);


    /**
     * getAll
     *
     * @return all news
     */
    @Query("SELECT * FROM news")
    List<News> getAll();

    /**
     * Insert a News.
     *
     * @param news user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(News news);

    /**
     * insertNewsList
     *
     * @param newsList newsList
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewsList(List<News> newsList);

    /**
     * deleteAll
     */
    @Query("DELETE FROM News")
    void deleteAll();

    @Query("DELETE FROM News WHERE (flag=:flag)")
    void deleteByFlag(int flag);

    @Query("SELECT * FROM news WHERE (id=:id)")
    News queryById(String id);


    @Query("SELECT * FROM news WHERE (language=:language)")
    List<News> queryByLang(@BaseType.NewsLangType String language);


    @Query("SELECT * FROM news WHERE (category=:cate)")
    List<News> queryByCate(String cate);

    @Query("SELECT * FROM news WHERE (publisher=:publisher)")
    List<News> queryByPublisher(String publisher);

    @Query("SELECT * FROM news WHERE (title like :search or content like :search or category like :search or publisher like :search)")
    List<News> queryBySearch(String search);
}
