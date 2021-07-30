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

package com.huawei.industrydemo.news.wight;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.huawei.industrydemo.news.R;
import com.huawei.industrydemo.news.utils.ContextUtil;
import com.huawei.industrydemo.news.utils.richtext.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @version [News-Demo 2.0.0.300, 2021/5/18]
 * @see [Related Classes/Methods]
 * @since [News-Demo 2.0.0.300]
 */

public class RichEditor extends WebView {
    private static final String TAG = RichEditor.class.getSimpleName();

    /**
     * Type
     */
    public enum Type {
        BOLD,
        ITALIC,
        SUBSCRIPT,
        SUPERSCRIPT,
        STRIKETHROUGH,
        UNDERLINE,
        H1,
        H2,
        H3,
        H4,
        H5,
        H6,
        ORDEREDLIST,
        UNORDEREDLIST,
        JUSTIFYCENTER,
        JUSTIFYFULL,
        JUSTIFYLEFT,
        JUSTIFYRIGHT
    }

    /**
     * OnTextChangeListener
     */
    public interface OnTextChangeListener {

        void onTextChange(String text);
    }

    /**
     * OnDecorationStateListener
     */
    public interface OnDecorationStateListener {

        void onStateChangeListener(String text, List<Type> types);
    }

    /**
     * AfterInitialLoadListener
     */
    public interface AfterInitialLoadListener {
        void onAfterInitialLoad(boolean isReady);
    }

    private static final String SETUP_HTML = "file:///android_asset/editor.html";
    private static final String CALLBACK_SCHEME = "re-callback://";
    private static final String STATE_SCHEME = "re-state://";
    private boolean isReady = false;
    private String mContents = "";
    private OnTextChangeListener mTextChangeListener;
    private OnDecorationStateListener mDecorationStateListener;
    private AfterInitialLoadListener mLoadListener;

    /**
     * @param context context
     */
    public RichEditor(Context context) {
        this(context, null);
    }

    /**
     * @param context context
     * @param attrs   attrs
     */
    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setAllowFileAccess(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebviewClient());
        loadUrl(SETUP_HTML);

        applyAttributes(context, attrs);
    }

    /**
     * createWebviewClient
     *
     * @return EditorWebViewClient
     */
    protected EditorWebViewClient createWebviewClient() {
        return new EditorWebViewClient();
    }

    /**
     * setOnTextChangeListener
     *
     * @param listener OnTextChangeListener
     */
    public void setOnTextChangeListener(OnTextChangeListener listener) {
        mTextChangeListener = listener;
    }

    /**
     * setOnDecorationChangeListener
     *
     * @param listener OnDecorationStateListener
     */
    public void setOnDecorationChangeListener(OnDecorationStateListener listener) {
        mDecorationStateListener = listener;
    }

    /**
     * setOnInitialLoadListener
     *
     * @param listener AfterInitialLoadListener
     */
    public void setOnInitialLoadListener(AfterInitialLoadListener listener) {
        mLoadListener = listener;
    }

    private void callback(String text) {
        mContents = text.replaceFirst(CALLBACK_SCHEME, "");
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(mContents);
        }
    }

    private void stateCheck(String text) {
        String state = text.replaceFirst(STATE_SCHEME, "").toUpperCase(Locale.ROOT);
        List<Type> types = new ArrayList<>();
        for (Type type : Type.values()) {
            if (TextUtils.indexOf(state, type.name()) != -1) {
                types.add(type);
            }
        }

        if (mDecorationStateListener != null) {
            mDecorationStateListener.onStateChangeListener(state, types);
        }
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        final int[] attrsArray = new int[]{
                android.R.attr.gravity
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);

        int gravity = ta.getInt(0, NO_ID);
        switch (gravity) {
            case Gravity.LEFT:
                exec("javascript:RE.setTextAlign(\"left\")");
                break;
            case Gravity.RIGHT:
                exec("javascript:RE.setTextAlign(\"right\")");
                break;
            case Gravity.TOP:
                exec("javascript:RE.setVerticalAlign(\"top\")");
                break;
            case Gravity.BOTTOM:
                exec("javascript:RE.setVerticalAlign(\"bottom\")");
                break;
            case Gravity.CENTER_VERTICAL:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                break;
            case Gravity.CENTER_HORIZONTAL:
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
            case Gravity.CENTER:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
            default:
                break;
        }

        ta.recycle();
    }

    /**
     * setHtml
     *
     * @param contents String
     */
    public void setHtml(String contents) {
        if (contents == null) {
            contents = "";
        }
        try {
            exec("javascript:RE.setHtml('" + URLEncoder.encode(contents, "UTF-8") + "');");
        } catch (UnsupportedEncodingException e) {
            // No handling
            Log.d(TAG, e.getMessage());
        }
        mContents = contents;
    }

    /**
     * getHtml
     *
     * @return String
     */
    public String getHtml() {
        return mContents;
    }

    /**
     * setEditorFontColor
     *
     * @param color int
     */
    public void setEditorFontColor(int color) {
        String hex = convertHexColorString(color);
        exec("javascript:RE.setBaseTextColor('" + hex + "');");
    }

    /**
     * setEditorFontSize
     *
     * @param px int
     */
    public void setEditorFontSize(int px) {
        exec("javascript:RE.setBaseFontSize('" + px + "px');");
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        exec("javascript:RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom
                + "px');");
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // still not support RTL.
        setPadding(start, top, end, bottom);
    }

    /**
     * setEditorBackgroundColor
     *
     * @param color color
     */
    public void setEditorBackgroundColor(int color) {
        setBackgroundColor(color);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resid) {
        Bitmap bitmap = Utils.decodeResource(getContext(), resid);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    @Override
    public void setBackground(Drawable background) {
        Bitmap bitmap = Utils.toBitmap(background);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    /**
     * setBackground
     *
     * @param url url
     */
    public void setBackground(String url) {
        exec("javascript:RE.setBackgroundImage('url(" + url + ")');");
    }

    /**
     * setEditorWidth
     *
     * @param px int
     */
    public void setEditorWidth(int px) {
        exec("javascript:RE.setWidth('" + px + "px');");
    }

    /**
     * setEditorHeight
     *
     * @param px int
     */
    public void setEditorHeight(int px) {
        exec("javascript:RE.setHeight('" + px + "px');");
    }

    /**
     * setPlaceholder
     *
     * @param placeholder String
     */
    public void setPlaceholder(String placeholder) {
        exec("javascript:RE.setPlaceholder('" + placeholder + "');");
    }

    /**
     * setInputEnabled
     *
     * @param inputEnabled Boolean
     */
    public void setInputEnabled(Boolean inputEnabled) {
        exec("javascript:RE.setInputEnabled(" + inputEnabled + ")");
    }

    /**
     * loadCSS
     *
     * @param cssFile String
     */
    public void loadCSS(String cssFile) {
        String jsCSSImport = "(function() {" +
                "    var head  = document.getElementsByTagName(\"head\")[0];" +
                "    var link  = document.createElement(\"link\");" +
                "    link.rel  = \"stylesheet\";" +
                "    link.type = \"text/css\";" +
                "    link.href = \"" + cssFile + "\";" +
                "    link.media = \"all\";" +
                "    head.appendChild(link);" +
                "}) ();";
        exec("javascript:" + jsCSSImport + "");
    }

    /**
     * undo
     */
    public void undo() {
        exec("javascript:RE.undo();");
    }

    /**
     * redo
     */
    public void redo() {
        exec("javascript:RE.redo();");
    }

    /**
     * setBold
     */
    public void setBold() {
        exec("javascript:RE.setBold();");
    }

    /**
     * setItalic
     */
    public void setItalic() {
        exec("javascript:RE.setItalic();");
    }

    /**
     * setSubscript
     */
    public void setSubscript() {
        exec("javascript:RE.setSubscript();");
    }

    /**
     * setSuperscript
     */
    public void setSuperscript() {
        exec("javascript:RE.setSuperscript();");
    }

    /**
     * setStrikeThrough
     */
    public void setStrikeThrough() {
        exec("javascript:RE.setStrikeThrough();");
    }

    /**
     * setUnderline
     */
    public void setUnderline() {
        exec("javascript:RE.setUnderline();");
    }

    /**
     * setTextColor
     *
     * @param color color
     */
    public void setTextColor(int color) {
        exec("javascript:RE.prepareInsert();");

        String hex = convertHexColorString(color);
        exec("javascript:RE.setTextColor('" + hex + "');");
    }

    /**
     * setTextBackgroundColor
     *
     * @param color color
     */
    public void setTextBackgroundColor(int color) {
        exec("javascript:RE.prepareInsert();");

        String hex = convertHexColorString(color);
        exec("javascript:RE.setTextBackgroundColor('" + hex + "');");
    }

    /**
     * setFontSize
     *
     * @param fontSize fontSize
     */
    public void setFontSize(int fontSize) {
        if (fontSize > 7 || fontSize < 1) {
            Log.e("RichEditor", "Font size should have a value between 1-7");
        }
        exec("javascript:RE.setFontSize('" + fontSize + "');");
    }

    /**
     * removeFormat
     */
    public void removeFormat() {
        exec("javascript:RE.removeFormat();");
    }

    /**
     * setHeading
     *
     * @param heading heading
     */
    public void setHeading(int heading) {
        exec("javascript:RE.setHeading('" + heading + "');");
    }

    /**
     * setIndent
     */
    public void setIndent() {
        exec("javascript:RE.setIndent();");
    }

    /**
     * setOutdent
     */
    public void setOutdent() {
        exec("javascript:RE.setOutdent();");
    }

    /**
     * setAlignLeft
     */
    public void setAlignLeft() {
        exec("javascript:RE.setJustifyLeft();");
    }

    /**
     * setAlignCenter
     */
    public void setAlignCenter() {
        exec("javascript:RE.setJustifyCenter();");
    }

    /**
     * setAlignRight
     */
    public void setAlignRight() {
        exec("javascript:RE.setJustifyRight();");
    }

    /**
     * setBlockquote
     */
    public void setBlockquote() {
        exec("javascript:RE.setBlockquote();");
    }

    /**
     * setBullets
     */
    public void setBullets() {
        exec("javascript:RE.setBullets();");
    }

    /**
     * setNumbers
     */
    public void setNumbers() {
        exec("javascript:RE.setNumbers();");
    }

    /**
     * insertImage
     *
     * @param url url
     * @param alt alt
     */
    public void insertImage(String url, String alt) {
        exec("javascript:RE.prepareInsert();");
        String testStr = "<img src=\"" + url + "\" alt=\"dachshund\"  width=\"100%\">" + "<br><br>";
        Toast.makeText(ContextUtil.context(), ContextUtil.context().getString(R.string.click_img_to_edit),Toast.LENGTH_LONG).show();
        exec("javascript:RE.insertHTML('" + testStr + "');");
    }

    /**
     * the image according to the specific width of the image automatically
     *
     * @param url   url
     * @param alt   alt
     * @param width width
     */
    public void insertImage(String url, String alt, int width) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertImageW('" + url + "', '" + alt + "','" + width + "');");
    }

    /**
     * {@link RichEditor#insertImage(String, String)} will show the original size of the image.
     * So this method can manually process the image by adjusting specific width and height to fit into different mobile screens.
     *
     * @param url    url
     * @param alt    alt
     * @param width  width
     * @param height height
     */
    public void insertImage(String url, String alt, int width, int height) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertImageWH('" + url + "', '" + alt + "','" + width + "', '" + height + "');");
    }

    /**
     * insertVideo
     *
     * @param url url
     */
    public void insertVideo(String url) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertVideo('" + url + "');");
    }

    /**
     * insertVideo
     *
     * @param url   url
     * @param width width
     */
    public void insertVideo(String url, int width) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertVideoW('" + url + "', '" + width + "');");
    }

    /**
     * insertVideo
     *
     * @param url    url
     * @param width  width
     * @param height height
     */
    public void insertVideo(String url, int width, int height) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertVideoWH('" + url + "', '" + width + "', '" + height + "');");
    }

    /**
     * insertAudio
     *
     * @param url url
     */
    public void insertAudio(String url) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertAudio('" + url + "');");
    }

    /**
     * insertYoutubeVideo
     *
     * @param url url
     */
    public void insertYoutubeVideo(String url) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertYoutubeVideo('" + url + "');");
    }

    /**
     * insertYoutubeVideo
     *
     * @param url   url
     * @param width width
     */
    public void insertYoutubeVideo(String url, int width) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertYoutubeVideoW('" + url + "', '" + width + "');");
    }

    /**
     * insertYoutubeVideo
     *
     * @param url    url
     * @param width  width
     * @param height height
     */
    public void insertYoutubeVideo(String url, int width, int height) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertYoutubeVideoWH('" + url + "', '" + width + "', '" + height + "');");
    }

    /**
     * insertLink
     *
     * @param href  href
     * @param title title
     */
    public void insertLink(String href, String title) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertLink('" + href + "', '" + title + "');");
    }

    /**
     * insertTodo
     */
    public void insertTodo() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTodo('" + Utils.getCurrentTime() + "');");
    }

    /**
     * focusEditor
     */
    public void focusEditor() {
        requestFocus();
        exec("javascript:RE.focus();");
    }

    /**
     * clearFocusEditor
     */
    public void clearFocusEditor() {
        exec("javascript:RE.blurFocus();");
    }

    private String convertHexColorString(int color) {
        return String.format(Locale.ROOT, "#%06X", (0xFFFFFF & color));
    }

    /**
     * exec
     *
     * @param trigger String
     */
    protected void exec(final String trigger) {
        Log.w(TAG, "exec:trigger " + trigger );
        Log.d(TAG, "exec:isReady " + isReady);
        if (isReady) {
            load(trigger);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(trigger);
                }
            }, 100);
        }
    }

    private void load(String trigger) {
        Log.d(TAG, "load:trigger " + trigger);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, value -> Log.w(TAG, "onReceiveValue:value " + value));
        } else {
            loadUrl(trigger);
        }
    }

    /**
     * EditorWebViewClien
     */
    protected class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
            Log.d(TAG, "onPageFinished:isReady " + isReady);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String decode = Uri.decode(url);
            if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                callback(decode);
                return true;
            } else if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
                stateCheck(decode);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final String url = request.getUrl().toString();

            String decode = Uri.decode(url);

            if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                callback(decode);
                return true;
            } else if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
                stateCheck(decode);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }
    }


    private int height;

    /**
     * Call this method to scroll to the bottom after instantiating WebView
     */
    public void scrollToBottom() {
        int temp = computeVerticalScrollRange();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(height, temp);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(animation != null && animation.getAnimatedValue() != null){
                    int nowHeight = (int) animation.getAnimatedValue();
                    height = nowHeight;
                    scrollTo(0, height);
                    if (height == temp) {
                        // Call it again to solve the problem of not slipping the bottom.
                        scrollTo(0, computeVerticalScrollRange());
                    }
                }
            }
        });
        valueAnimator.start();
    }


    private static float downX = 0;
    private static float downY = 0;
    private static float moveX = 0;
    private static float moveY = 0;
    private static long currentMS = 0;

    /**
     * ImageClickListener
     */
    public interface ImageClickListener {
        void onImageClick(String imageUrl);
    }

    private ImageClickListener imageClickListener;

    public void setImageClickListener(ImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
        if (this.imageClickListener != null) {
            RichEditor.this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = (int) event.getX();
                            downY = (int) event.getY();
                            moveX = 0;
                            moveY = 0;
                            currentMS = System.currentTimeMillis(); // long currentMS
                            break;

                        case MotionEvent.ACTION_MOVE:
                            moveX += Math.abs(event.getX() - downX);
                            moveY += Math.abs(event.getY() - downY);
                            downX = event.getX();
                            downY = event.getY();
                            break;

                        case MotionEvent.ACTION_UP:
                            long moveTime = System.currentTimeMillis() - currentMS;

                            if (moveTime < 400 && (moveX < 25 && moveY < 25)) {

                                HitTestResult mResult = getHitTestResult();
                                if (mResult != null) {
                                    final int type = mResult.getType();
                                    if (type == HitTestResult.IMAGE_TYPE) {

                                        String imageUrl = mResult.getExtra();
                                        postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (imageClickListener != null) {
                                                    if (imageUrl.contains("file://")) {

                                                        String newImageUrl = imageUrl.replace("file://", "");
                                                        imageClickListener.onImageClick(newImageUrl);
                                                    } else {
                                                        imageClickListener.onImageClick(imageUrl);
                                                    }
                                                }
                                            }
                                        }, 200);

                                    } else {

                                    }
                                }
                            }
                            break;

                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }
}