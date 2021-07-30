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

package com.huawei.industrydemo.news.base;

import androidx.fragment.app.Fragment;

import com.huawei.industrydemo.news.constants.LogConfig;
import com.huawei.industrydemo.news.utils.KitTipUtil;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class BaseFragment extends Fragment implements LogConfig {

    public void addTipView(String[] kits) {
        KitTipUtil.addTipView(getActivity(), KitTipUtil.getKitMap(kits));
    }

}
