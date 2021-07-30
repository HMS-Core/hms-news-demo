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

package com.huawei.industrydemo.news.constants;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public interface BaseType {

    /*
     * Technical_enablement
     * Application_Scenario
     * Information_Activity
     * Expert_Insight
     */
    String TECHNICAL_ENABLEMENT = "0";

    String APPLICATION_SCENARIO = "1";

    String INFORMATION_ACTIVITY = "2";

    String EXPERT_INSIGHT = "3";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TECHNICAL_ENABLEMENT, APPLICATION_SCENARIO, INFORMATION_ACTIVITY, EXPERT_INSIGHT})
    @interface NewsCategory {
    }

    String NEWS_LANG_EN = "en";
    String NEWS_LANG_ZN = "zh";

    @StringDef({NEWS_LANG_EN, NEWS_LANG_ZN})
    @interface NewsLangType {
    }

    int HOME_CATEGORY_0_FOLLOW = 0;

    int HOME_CATEGORY_1_ALL = 1;

    int HOME_CATEGORY_1_DIFF = 2;

    int NEWS_MINE_LOCAL = 100;

    int NEWS_CLOUD_STORE = 0;

    @IntDef({NEWS_MINE_LOCAL, NEWS_CLOUD_STORE})
    @interface NewsFlag {
    }

    int PICTURE_ONE_BIG = 0;

    int PICTURE_3 = 1;

    int PICTURE_0 = 2;

    int PICTURE_ONE_SMALL = 3;

    @IntDef({PICTURE_0, PICTURE_ONE_SMALL, PICTURE_ONE_BIG, PICTURE_3})
    @interface PictureCount {
    }

    String EVENT_REFRESH_DATA = "0";

    @StringDef({EVENT_REFRESH_DATA})
    @interface Event {
    }
}
