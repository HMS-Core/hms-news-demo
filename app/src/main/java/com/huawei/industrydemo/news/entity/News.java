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

package com.huawei.industrydemo.news.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.huawei.industrydemo.news.constants.BaseType;

import java.io.Serializable;
import java.util.Objects;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/19]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */

@Entity
public class News implements Serializable, Comparable<News> {

    @Ignore
    private static final long serialVersionUID = 123456789L;

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "category")
    @BaseType.NewsCategory
    public String category;

    @ColumnInfo(name = "type")
    @BaseType.PictureCount
    public int type;

    @Nullable
    public String img1;
    @Nullable
    public String img2;
    @Nullable
    public String img3;

    @Nullable
    @BaseType.NewsLangType
    public String language;

    @Nullable
    public String publisher;

    @ColumnInfo(name = "publish_date")
    public String publishDate;

    @BaseType.NewsFlag
    public int flag = BaseType.NEWS_CLOUD_STORE;

    public News(@NonNull String id) {
        this.id = id;
    }

    public News(@NonNull String id, String s, String s1) {
        this.id = id;
        title =s;
        content=s1;
    }

    public String getTitle() {
        return title;
    }

    public News setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public News setContent(String content) {
        this.content = content;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public News setCategory(String category) {
        this.category = category;
        return this;
    }

    public int getType() {
        return type;
    }

    public News setType(int type) {
        this.type = type;
        return this;
    }

    @Nullable
    public String getImg1() {
        return img1;
    }

    public News setImg1(@Nullable String img1) {
        this.img1 = img1;
        return this;
    }

    @Nullable
    public String getImg2() {
        return img2;
    }

    public News setImg2(@Nullable String img2) {
        this.img2 = img2;
        return this;
    }

    @Nullable
    public String getImg3() {
        return img3;
    }

    public News setImg3(@Nullable String img3) {
        this.img3 = img3;
        return this;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    public News setLanguage(@Nullable String language) {
        this.language = language;
        return this;
    }

    @Nullable
    public String getPublisher() {
        return publisher;
    }

    public News setPublisher(@Nullable String publisher) {
        this.publisher = publisher;
        return this;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public News setPublishDate(String publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public int getFlag() {
        return flag;
    }

    public News setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", language='" + language + '\'' +
                ", category='" + category + '\'' +
                ", flag='" + flag + '\'' +
                ", img1='" + img1 + '\'' +
                ", img2='" + img2 + '\'' +
                ", img3='" + img3 + '\'' +
                '}';
    }

    @Override
    public int compareTo(News o) {
        return o.publishDate.compareTo(this.publishDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof News)) {
            return false;
        }
        return ((News) obj).publishDate.equals(this.publishDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publishDate);
    }
}
