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
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.huawei.industrydemo.news.entity.Avatar;
import com.huawei.industrydemo.news.entity.News;

import java.util.List;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
@Dao
public interface AvatarDao {
    /**
     * Query a product based on language and number.
     *
     * @param flag user OpenId
     * @return Avatar
     */
    @Query("SELECT * FROM avatar WHERE (name=:flag)")
    Avatar queryByOpenId(String flag);

    /**
     * getAll
     * @return all avatars
     */
    @Query("SELECT * FROM Avatar")
    List<Avatar> getAll();

    /**
     * Insert a Avatar.
     *
     * @param avatar user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Avatar avatar);

    /**
     * insertAvatarList
     * @param avatarList AvatarList
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAvatars(List<Avatar> avatarList);

    /**
     * deleteAll
     */
    @Query("DELETE FROM Avatar")
    void deleteAll();

}
