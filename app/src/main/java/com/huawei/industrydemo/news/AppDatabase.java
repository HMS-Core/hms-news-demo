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

package com.huawei.industrydemo.news;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.huawei.industrydemo.news.entity.AppConfig;
import com.huawei.industrydemo.news.entity.BrowsingHistory;
import com.huawei.industrydemo.news.entity.Favorite;
import com.huawei.industrydemo.news.entity.Follow;
import com.huawei.industrydemo.news.entity.User;
import com.huawei.industrydemo.news.entity.Video;
import com.huawei.industrydemo.news.entity.dao.AppConfigDao;
import com.huawei.industrydemo.news.entity.dao.BrowsingHistoryDao;
import com.huawei.industrydemo.news.entity.dao.FavoriteDao;
import com.huawei.industrydemo.news.entity.Avatar;
import com.huawei.industrydemo.news.entity.dao.AvatarDao;
import com.huawei.industrydemo.news.entity.dao.FollowDao;

import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.dao.NewsDao;
import com.huawei.industrydemo.news.entity.dao.UserDao;
import com.huawei.industrydemo.news.entity.dao.VideoDao;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
@Database(entities = {User.class, AppConfig.class, Follow.class, News.class, Avatar.class, Favorite.class,
    BrowsingHistory.class, Video.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Get AppConfigDao
     *
     * @return AppConfigDao
     */
    public abstract AppConfigDao appConfigDao();

    /**
     * Get UserDao
     *
     * @return UserDao
     */
    public abstract UserDao userDao();

    /**
     * Get FollowDao
     * 
     * @return FollowDao
     */
    public abstract FollowDao followDao();

    /**
     * NewsDao
     * 
     * @return NewsDao
     */
    public abstract NewsDao newsDao();

    /**
     * favoriteDao
     * 
     * @return favoriteDao
     */
    public abstract FavoriteDao favoriteDao();

    /**
     * AvatarDao
     * 
     * @return AvatarDao
     */
    public abstract AvatarDao avatarDao();

    /**
     * BrowsingHistoryDao
     * 
     * @return BrowsingHistoryDao
     */
    public abstract BrowsingHistoryDao browsingHistoryDao();

    /**
     * VideoDao
     *
     * @return VideoDao
     */
    public abstract VideoDao videoDao();
}
