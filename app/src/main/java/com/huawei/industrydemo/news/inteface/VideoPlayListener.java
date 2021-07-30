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

package com.huawei.industrydemo.news.inteface;

import android.view.SurfaceView;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/21]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public interface VideoPlayListener {
    /**
     * This function is used to initial the video kit.
     *
     * @param videoUrl The video link url.
     * @param surfaceView The interface which is used to play the video.
     */
    void initVideo(String videoUrl, SurfaceView surfaceView);
}
