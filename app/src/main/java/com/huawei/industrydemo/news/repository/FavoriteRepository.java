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

package com.huawei.industrydemo.news.repository;

import com.huawei.industrydemo.news.entity.Favorite;
import com.huawei.industrydemo.news.entity.dao.FavoriteDao;
import com.huawei.industrydemo.news.utils.DatabaseUtil;

import java.util.List;

/**
 * Favorite Repository
 *
 * @version [News-Demo 2.0.0.300, 2021/6/1]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class FavoriteRepository {
    private final FavoriteDao favoriteDao;

    public FavoriteRepository() {
        this.favoriteDao = DatabaseUtil.getDatabase().favoriteDao();
    }

    public boolean isFavorite(String newsId) {
        Favorite favorite = queryByNewsId(newsId);
        return null != favorite;
    }

    public List<Favorite> queryByUserId(String userId) {
        return favoriteDao.queryByUserId(userId);
    }

    public void addFavorite(Favorite favorite) {
        favoriteDao.insert(favorite);
    }

    public void removeFavorite(Favorite favorite) {
        favoriteDao.delete(favorite);
    }

    public Favorite queryByNewsId(String newsId) {
        return favoriteDao.queryByNewsId(newsId);
    }
}
