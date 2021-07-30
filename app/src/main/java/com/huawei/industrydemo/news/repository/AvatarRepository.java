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

import com.huawei.industrydemo.news.AppDatabase;
import com.huawei.industrydemo.news.entity.Avatar;
import com.huawei.industrydemo.news.entity.News;
import com.huawei.industrydemo.news.entity.dao.AvatarDao;
import com.huawei.industrydemo.news.utils.DatabaseUtil;

import java.util.List;


/**
 * News Repository
 * 
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class AvatarRepository {
    private final AvatarDao avatarDao;

    private final AppDatabase database;

    public AvatarRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.avatarDao = database.avatarDao();
    }

    public Avatar queryByFlag(String flag) {
        return avatarDao.queryByOpenId(flag);
    }

    public List<Avatar> getAll() {
        return avatarDao.getAll();
    }

    /**
     * insertAvatarList
     * @param avatars Avatar list
     */
    public void insertAvatarList(List<Avatar> avatars) {
        avatarDao.deleteAll();
        avatarDao.insertAvatars(avatars);
    }

    /**
     * insert
     * @param avatar Avatar
     */
    public void insert(Avatar avatar) {
        avatarDao.insert(avatar);
    }
}