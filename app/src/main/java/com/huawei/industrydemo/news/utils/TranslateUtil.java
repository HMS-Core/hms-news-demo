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
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.wight.TranslateResultDialog;

import static com.huawei.industrydemo.news.utils.hms.AnalyticsUtil.textTranslationReport;

public class TranslateUtil {
    public static void initPopupMenu(Context context, View view, String content) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        String[] languages = context.getResources().getStringArray(R.array.language_list);
        String[] isoLangList = context.getResources().getStringArray(R.array.iso_639_1_list);
        Menu menu = popupMenu.getMenu();
        for (int i = 0; i < languages.length; i++) {
            menu.add(0, i, 0, languages[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            MLRemoteTranslateSetting setting =
                new MLRemoteTranslateSetting.Factory().setTargetLangCode(isoLangList[item.getItemId()]).create();
            MLRemoteTranslator mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
            Task<String> task = mlRemoteTranslator.asyncTranslate(content);
            task.addOnSuccessListener(text -> {
                TranslateResultDialog dialog = new TranslateResultDialog(context, text);
                textTranslationReport();
                dialog.show();
            }).addOnFailureListener(e -> {
            });
            return true;
        });
        popupMenu.show();
    }

}
