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

package com.huawei.industrydemo.news.entity.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huawei.industrydemo.news.entity.BrowsingHistory;

import java.util.List;
/**
 * @version [News-Demo 2.0.0.300, 2021/6/3]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
@Dao
public interface BrowsingHistoryDao {
    @Query("SELECT * FROM BrowsingHistory WHERE (userId=:userId)")
    List<BrowsingHistory> queryByUserId(String userId);

    @Query("SELECT * FROM BrowsingHistory WHERE (newsId=:newsId)")
    BrowsingHistory queryByNewsId(String newsId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setHistoryData(BrowsingHistory data);

}
