/*
 *     Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.industrydemo.news.entity;

import com.huawei.industrydemo.news.MainApplication;


/**
 * @version [News-Demo 2.0.0.300, 2021/5/20]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class KitInfo {

    private String kitOriginame;


    private String kitOrigiFunc;

    // The Kit Function name string id
    private int kitFunction;

    // The kit function name which is used to show on the UI
    private String kitFunctionStr;

    private String userId;

    // The Kit name string id
    private int kitName;

    // The Kit name which is used to show on the UI
    private String kitNameStr;

    private Integer kitDescription;

    private String kitUrl;

    private String[] kitColors;

    public KitInfo() {
    }


    public KitInfo( String kitOriginame,  String kitOrigiFunc, int kitName, int kitFunction,
                    String userId, Integer kitDescription, int kitUrl, String... kitColors) {
        setKitOriginame(kitOriginame);
        setKitOrigiFunc(kitOrigiFunc);
        setKitFunction(kitFunction);
        setUserId(userId);
        setKitName(kitName);
        setKitDescription(kitDescription);
        String kitUrlStr = MainApplication.getContext().getResources().getString(kitUrl);
        setKitUrl(kitUrlStr);
        setKitColors(kitColors);
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId) {
        this.userId = userId;
    }


    public String getKitOriginame() {
        return kitOriginame;
    }

    public void setKitOriginame( String kitOriginame) {
        this.kitOriginame = kitOriginame;
    }


    public String getKitOrigiFunc() {
        return kitOrigiFunc;
    }

    public void setKitOrigiFunc( String kitOrigiFunc) {
        this.kitOrigiFunc = kitOrigiFunc;
    }

    public int getKitFunction() {
        return kitFunction;
    }

    public void setKitFunction(int kitFunction) {
        this.kitFunction = kitFunction;
        String tempStr = MainApplication.getContext().getResources().getString(kitFunction);
        setKitFunctionStr(tempStr);
    }

    public String getKitFunctionStr() {
        return kitFunctionStr;
    }

    public void setKitUrl(String kitUrl) {
        this.kitUrl = kitUrl;
    }

    public void setKitFunctionStr(String kitFunctionStr) {
        this.kitFunctionStr = kitFunctionStr;
    }

    public String getKitNameStr() {
        return kitNameStr;
    }

    public void setKitNameStr(String kitNameStr) {
        if (kitNameStr != null) {
            this.kitNameStr = kitNameStr;
        }
    }

    public int getKitName() {
        return kitName;
    }

    public void setKitName(int kitName) {
        this.kitName = kitName;
        String tempStr = MainApplication.getContext().getResources().getString(kitName);
        setKitNameStr(tempStr);
    }

    public Integer getKitDescription() {
        return kitDescription;
    }

    public void setKitDescription(Integer kitDescription) {
        this.kitDescription = kitDescription;
    }

    public String getKitUrl() {
        return kitUrl;
    }

    public String[] getKitColors() {
        if(kitColors==null){
            return new String[0];
        }
        return kitColors.clone();
    }

    private void setKitColors(String[] kitColors) {
        this.kitColors = kitColors;
    }
}
