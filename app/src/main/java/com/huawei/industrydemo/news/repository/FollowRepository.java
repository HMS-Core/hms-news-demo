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

import com.huawei.industrydemo.news.entity.Follow;
import com.huawei.industrydemo.news.entity.dao.FollowDao;
import com.huawei.industrydemo.news.utils.DatabaseUtil;

import java.util.List;

/**
 * Favorite Repository
 *
 * @version [News-Demo 2.0.0.300, 2021/6/1]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class FollowRepository {
    private final FollowDao followDao;

    public FollowRepository() {
        this.followDao = DatabaseUtil.getDatabase().followDao();
    }

    public boolean isFollow(String followId) {
        Follow follow = queryByFollowId(followId);
        return null != follow;
    }

    public Follow queryByFollowId(String followId) {
        return followDao.queryByFollowId(followId);
    }

    public List<Follow> queryByUserId(String userId) {
        return followDao.queryByUserId(userId);
    }

    public void addFollow(Follow follow) {
        followDao.insert(follow);
    }

    public void removeFollow(Follow follow) {
        followDao.delete(follow);
    }
}
