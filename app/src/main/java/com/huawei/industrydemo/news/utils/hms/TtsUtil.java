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

package com.huawei.industrydemo.news.utils.hms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;
import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.utils.agc.AgcUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Text to Speech
 * 
 * @version [News-Demo 2.0.0.300, 2021/5/20]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */
public class TtsUtil {

    private static final String TAG = TtsUtil.class.getSimpleName();

    private static final int MAX_LENGTH = 500;

    private static MLTtsEngine mlTtsEngine;

    private static boolean isSpeaking = false;

    private static List<String> supportLanguageList;

    /**
     * TTS init
     *
     * @param context Context
     */
    public static void init(Context context) {
        String[] languages = context.getResources().getStringArray(R.array.tts_support_list);
        supportLanguageList = Arrays.asList(languages);
        MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(context));

        MLTtsConfig mlTtsConfig = new MLTtsConfig().setLanguage(MLTtsConstants.TTS_EN_US)
            .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
            .setSpeed(1.0f)
            .setVolume(1.0f);
        mlTtsEngine = new MLTtsEngine(mlTtsConfig);

        MLTtsCallback callback = new MLTtsCallback() {
            @Override
            public void onError(String taskId, MLTtsError err) {
                Log.e(TAG, "onError (taskId: " + taskId + ", " + err.toString());
                setIsSpeaking(false);
                switch (err.getErrorId()) {
                    case MLTtsError.ERR_NET_CONNECT_FAILED:
                        Toast.makeText(context, R.string.net_connect_falied,Toast.LENGTH_SHORT).show();
                        break;
                    default: 
                }
            }

            @Override
            public void onWarn(String taskId, MLTtsWarn warn) {
                Log.w(TAG, "onWarn (taskId: " + taskId + ", " + warn.toString());
            }

            @Override
            public void onRangeStart(String taskId, int start, int end) {
                Log.d(TAG, "onRangeStart (taskId: " + taskId + ", start: " + start + ", end: " + end);
            }

            @Override
            // taskId: ID of a TTS task corresponding to the audio.
            // audioFragment: audio data.
            // offset: offset of the audio segment to be transmitted in the queue. One TTS task corresponds to a TTS
            // queue.
            // range: text area where the audio segment to be transmitted is located; range.first (included): start
            // position; range.second (excluded): end position.
            public void onAudioAvailable(String taskId, MLTtsAudioFragment audioFragment, int offset,
                Pair<Integer, Integer> range, Bundle bundle) {
                // Audio stream callback API, which is used to return the synthesized audio data to the app.
            }

            @Override
            public void onEvent(String taskId, int eventId, Bundle bundle) {
                Log.d(TAG, "onEvent (taskId: " + taskId + ", eventId: " + eventId);
                switch (eventId) {
                    case MLTtsConstants.EVENT_PLAY_START:
                        // Called when playback starts.
                        setIsSpeaking(true);
                        break;
                    case MLTtsConstants.EVENT_PLAY_STOP:
                        // Called when playback stops.
                        setIsSpeaking(false);
                        break;
                    case MLTtsConstants.EVENT_PLAY_RESUME:
                        // Called when playback resumes.
                        setIsSpeaking(true);
                        break;
                    case MLTtsConstants.EVENT_PLAY_PAUSE:
                        // Called when playback pauses.
                        setIsSpeaking(false);
                        break;
                    default:
                        break;
                }
            }
        };

        mlTtsEngine.setTtsCallback(callback);
    }

    /**
     * change TTS Language
     *
     * @param target target language
     * @return is Support
     */
    public static boolean changeLanguage(String target) {
        Log.d(TAG, "language: " + target);
        if (!supportLanguageList.contains(target)) {
            Log.e(TAG, "not support");
            return false;
        }
        MLTtsConfig mlTtsConfig = new MLTtsConfig();
        switch (target) {
            case "zh":
                mlTtsConfig.setLanguage(MLTtsConstants.TTS_ZH_HANS).setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ZH);
                break;
            case "en":
                mlTtsConfig.setLanguage(MLTtsConstants.TTS_EN_US).setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN);
                break;
            case "fr":
                mlTtsConfig.setLanguage(MLTtsConstants.TTS_LAN_FR_FR).setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_FR);
                break;
            case "de":
                mlTtsConfig.setLanguage(MLTtsConstants.TTS_LAN_DE_DE).setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_DE);
                break;
            case "es":
                mlTtsConfig.setLanguage(MLTtsConstants.TTS_LAN_ES_ES).setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ES);
                break;
            case "it":
                mlTtsConfig.setLanguage(MLTtsConstants.TTS_LAN_IT_IT).setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_IT);
                break;
            default:
                break;
        }
        mlTtsEngine.updateConfig(mlTtsConfig);
        return true;
    }

    /**
     * TTS start
     * 
     * @param allText all text
     */
    public static void start(String allText) {
        if (allText.length() <= MAX_LENGTH) {
            TtsUtil.append(allText);
        } else {
            TtsUtil.append(allText.substring(0, MAX_LENGTH));
            int i = MAX_LENGTH;
            while (i + MAX_LENGTH <= allText.length()) {
                TtsUtil.append(allText.substring(i, i + MAX_LENGTH));
                i = i + MAX_LENGTH;
            }
            if (i < allText.length()) {
                TtsUtil.append(allText.substring(i));
            }
        }
    }

    /**
     * TTS append
     * 
     * @param nextText next paragraph of text
     * @return taskId
     */
    private static String append(String nextText) {
        Log.d(TAG, nextText);
        return mlTtsEngine.speak(nextText, MLTtsEngine.QUEUE_APPEND);
    }

    /**
     * TTS pause
     */
    public static void pause() {
        mlTtsEngine.pause();
    }

    /**
     * TTS resume
     */
    public static void resume() {
        mlTtsEngine.resume();
    }

    /**
     * TTS stop
     */
    public static void stop() {
        mlTtsEngine.stop();
    }

    /**
     * TTS shutdown
     */
    public static void shutdown() {
        if (mlTtsEngine != null) {
            mlTtsEngine.shutdown();
        }
    }

    /**
     * Obtains the TTS status.
     * 
     * @return isSpeaking
     */
    public static boolean isSpeaking() {
        return isSpeaking;
    }

    /**
     * Set the TTS status.
     * 
     * @param isSpeaking boolean
     */
    public static void setIsSpeaking(boolean isSpeaking) {
        TtsUtil.isSpeaking = isSpeaking;
    }
}
