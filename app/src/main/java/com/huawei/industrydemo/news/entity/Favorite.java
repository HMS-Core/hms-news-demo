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

package com.huawei.industrydemo.news.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/19]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
@Entity(primaryKeys = {"userId", "newsId"})
public class Favorite {
    @NonNull
    private String userId;

    @NonNull
    private String newsId;

    public Favorite(@NonNull String userId, @NonNull String newsId) {
        this.userId = userId;
        this.newsId = newsId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(@NonNull String newsId) {
        this.newsId = newsId;
    }
}
