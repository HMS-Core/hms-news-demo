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

package com.huawei.industrydemo.news.utils;

import android.content.Context;

import androidx.room.Room;

import com.huawei.industrydemo.news.AppDatabase;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class DatabaseUtil {
    private static final String DATABASE_NAME = "news.db";

    private static volatile DatabaseUtil instance;

    private final AppDatabase database;

    private DatabaseUtil(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();
    }

    private static void getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseUtil.class) {
                if (instance == null) {
                    instance = new DatabaseUtil(context);
                }
            }
        }
    }

    public static void init(Context context) {
        getInstance(context);
    }

    public static AppDatabase getDatabase() {
        return instance.database;
    }
}
