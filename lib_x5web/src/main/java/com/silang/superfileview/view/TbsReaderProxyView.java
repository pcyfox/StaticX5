package com.silang.superfileview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.elvishew.xlog.XLog;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.silang.superfileview.QbSdkManager;
import com.silang.superfileview.XUtils;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tk.edu.lib_video.video.MultiSampleVideo;

import java.io.File;

/**
 * Created by 12457 on 2017/8/29.
 */

public class TbsReaderProxyView extends FrameLayout implements TbsReaderView.ReaderCallback {
    private static final String TAG = "TbsReaderProxyView";
    private TbsReaderView mTbsReaderView;
    private X5WebView webView;
    private final MultiSampleVideo player;

    private final Context context;
    // "http://soft.imtt.qq.com/browser/tes/feedback.html",
    // "http://debugtbs.qq.com/",
    private boolean isStop = false;
    private OnLoadListener onLoadListener;
    private String currentFileOrUrl = "";
    private boolean isWebViewTouchable = true;

    public TbsReaderProxyView(Context context) {
        this(context, null, 0);
    }

    public TbsReaderProxyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TbsReaderProxyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        player = new MultiSampleVideo(context);
        addView(player, new LinearLayout.LayoutParams(-1, -1));
        loadReaderView();
    }


    private void loadReaderView() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
            mTbsReaderView.setVisibility(View.VISIBLE);
            removeView(mTbsReaderView);
            mTbsReaderView = null;
        }
        mTbsReaderView = new TbsReaderView(context, this);
        addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
        isStop = false;
        mTbsReaderView.setBackgroundColor(Color.WHITE);
        mTbsReaderView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            long startCallTime;
            int count = 0;

            @Override
            public void onLayoutChange(final View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (onLoadListener != null) {
                    if (count == 0) {
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (SystemClock.uptimeMillis() - startCallTime < 400) {
                                    v.postDelayed(this, 200);
                                } else {
                                    if (!TextUtils.isEmpty(currentFileOrUrl)) {
                                        onLoadListener.onLoadFinish(currentFileOrUrl);
                                    }
                                    count = 0;
                                }

                            }
                        }, 400);
                    }
                    startCallTime = SystemClock.uptimeMillis();
                    count++;
                }
            }
        });
    }


    private OnGetFilePathListener mOnGetFilePathListener;

    public void setOnGetFilePathListener(OnGetFilePathListener mOnGetFilePathListener) {
        this.mOnGetFilePathListener = mOnGetFilePathListener;
    }


    private void showCurrentView(View v) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != v) {
                child.setVisibility(View.GONE);
                if (child instanceof GSYVideoPlayer) {
                    pauseVideo();
                } else if (child instanceof WebView) {
                    webView.stopLoading();
                    String data = "</Div><head><style></head>";
                    loadTextHtml("", data);
                }
            }
        }

        if (onLoadListener != null && !TextUtils.isEmpty(currentFileOrUrl)) {
            onLoadListener.onLoadStop(currentFileOrUrl);
        }
        isStop = true;
        if (v != null) {
            v.setVisibility(View.VISIBLE);
        }
    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    public OpenResult displayDocFile(File mFile) {
        return displayDocFile(mFile.toString());
    }

    public boolean open(String urlOrPath) {
        Log.d(TAG, "open urlOrPath:" + urlOrPath);
        if (XUtils.isTbModeFile(new File(urlOrPath))) {
            displayDocFile(urlOrPath);
            return true;
        } else if (XUtils.isQBModeFile(new File(urlOrPath))) {
            openUrl(urlOrPath);
            return true;
        }
        return false;
    }

    public OpenResult displayDocFile(String path) {
        if (!QbSdkManager.isIsInitOk()) {
            return new OpenResult(false, "X5内核加载失败！");
        }
        Log.d(TAG, "displayDocFile() called with: path = [" + path + "]");
        if (TextUtils.isEmpty(path) || !new File(path).exists() || !new File(path).canRead()) {
            Log.e(TAG, "无法访问文件!");
            return new OpenResult(false, "文件访问失败！");
        }
        showCurrentView(mTbsReaderView);

        currentFileOrUrl = path;
        if (onLoadListener != null && !TextUtils.isEmpty(path)) {
            onLoadListener.onLoadStart(path);
        }
        if (!XUtils.isSupportFile(new File(path))) {
            Log.e(TAG, "displayDocFile() called with: path = [" + path + "] isUnSupportFile ");
            return new OpenResult(false, "不支持的文件类型！");
        }
        if (isStop || mTbsReaderView == null) {
            loadReaderView();
        }


        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                XLog.e(TAG + "displayFile:创建 TbsReaderTemp 失败");
                return new OpenResult(false, "创建文件临时加载空间失败");
            }
        }
        //加载文件
        Bundle localBundle = new Bundle();
        localBundle.putString("filePath", path);
        localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");
        try {
            boolean canOpen = mTbsReaderView.preOpen(getFileType(path), false);
            if (canOpen) {
                if (View.VISIBLE != mTbsReaderView.getVisibility()) {
                    mTbsReaderView.setVisibility(View.VISIBLE);
                }
                mTbsReaderView.openFile(localBundle);
            } else {
                return new OpenResult(false, "预加载文件失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OpenResult(true, "open success!");
    }

    /**
     * 直接使用浏览器打开url
     *
     * @param url
     */
    public void openUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        showCurrentView(webView);
        currentFileOrUrl = url;
        if (onLoadListener != null) {
            onLoadListener.onLoadStart(url);
        }
        if (webView == null) {
            loadXWebView();
        }
        webView.loadUrl(url);
        Log.d(TAG, "displayUrl() called with: url = [" + url + "]");
    }

    public void openLocalImage(String path) {
        showCurrentView(webView);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        currentFileOrUrl = path;
        if (onLoadListener != null) {
            onLoadListener.onLoadStart(path);
        }
        Log.d(TAG, "openImage() called with: url = [" + path + "]");

        if (webView == null) {
            loadXWebView();
        }
        if (mTbsReaderView != null) {
            mTbsReaderView.setVisibility(View.GONE);
        }

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        String imageUrl = "file://" + path;
        String data = "<HTML><IMG src=\"" + imageUrl + "\"" + "width=" + bitmap.getWidth() + "height=" + bitmap.getHeight() + "/>";
        bitmap.recycle();
        webView.loadDataWithBaseURL(imageUrl, data, "text/html", "utf-8", null);
        if (onLoadListener != null) {
            onLoadListener.onLoadFinish(path);
        }
    }


    public void openImageUrl(String url) {
        showCurrentView(webView);
        if (onLoadListener != null) {
            onLoadListener.onLoadStart(url);
        }
        String data = "<HTML><IMG src=\"" + url + "\"" + "/>";
        loadTextHtml(url, data);
        if (onLoadListener != null) {
            onLoadListener.onLoadFinish(url);
        }
    }


    public void openVideoUrl(final String url) {
        openVideoUrl(url, true);
    }

    public void openVideoUrl(final String url, boolean useCache) {
        showCurrentView(player);
        player.setVisibility(View.VISIBLE);
        player.setUp(url, useCache, "");
        player.startPlayLogic();
    }


    public void pauseVideo() {
        if (player != null) {
            player.onVideoPause();
        }
    }

    public void resumeVideo() {
        player.onVideoResume();
    }

    public MultiSampleVideo getPlayer() {
        return player;
    }

    public void loadTextHtml(String url, String data) {
        currentFileOrUrl = url;
        currentFileOrUrl = url;
        if (onLoadListener != null) {
            onLoadListener.onLoadStart(url);
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Log.d(TAG, "loadDataWithBaseURL() called with: url = [" + url + "], data = [" + data + "]");
        if (webView == null) {
            loadXWebView();
        }
        if (mTbsReaderView != null) {
            mTbsReaderView.setVisibility(View.GONE);
        }
        webView.setVisibility(View.VISIBLE);
        webView.loadDataWithBaseURL(url, data, "text/html", "utf-8", null);
    }


    //用于X5内核诊断
    public void displayDebugPage() {
        openUrl("http://debugtbs.qq.com/");
    }


    private void loadXWebView() {
        if (webView != null) {
            return;
        }
        webView = new X5WebView(getContext()) {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (onLoadListener != null && TextUtils.isEmpty(s)) {
                    onLoadListener.onLoadFinish(s);
                }
            }

            @Override
            public void onPageStart(WebView webView, String s) {
                super.onPageStart(webView, s);
                if (onLoadListener != null) {
                    onLoadListener.onLoadStart(s);
                }
            }
        };
        webView.setCanTouchable(isWebViewTouchable);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                Log.d(TAG, "onJsAlert() called with: webView = [" + webView + "], s = [" + s + "], s1 = [" + s1 + "], jsResult = [" + jsResult + "]");
                return super.onJsAlert(webView, s, s1, jsResult);
            }
        });
        addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, -1));
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }

        str = paramString.substring(i + 1);
        return str;
    }

    public void show() {
        if (mOnGetFilePathListener != null) {
            mOnGetFilePathListener.onGetFilePath(this);
        }
    }

    public void setWebViewTouchable(boolean isTouchable) {
        isWebViewTouchable = isTouchable;
        if (webView != null) {
            webView.setCanTouchable(isTouchable);
        }
    }


    /***
     * 将获取File路径的工作，“外包”出去
     */
    public interface OnGetFilePathListener {
        void onGetFilePath(TbsReaderProxyView mSuperFileView2);
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
        Log.d(TAG, "onCallBackAction() called with: integer = [" + integer + "], o = [" + o + "], o1 = [" + o1 + "]");
    }

    public void stopDisplay() {
        showCurrentView(null);
    }


    public void release() {
        stopDisplay();
        if (player != null) {
            player.release();
        }
        if (webView != null) {
            webView.release();
        }
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }

    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }


    public interface OnLoadListener {
        void onLoadFinish(String currentFileOrUrl);

        void onLoadStart(String currentFileOrUrl);

        void onLoadStop(String currentFileOrUrl);
    }

    public void initVideoCache(String VIDEO_URL) {

    }

    public void onPause() {
        stopDisplay();
    }

    public void hideVideoView() {

    }

    public static class OpenResult {
        public final boolean isOpenSuccess;
        public final String mag;

        public OpenResult(boolean isOpenSuccess, String mag) {
            this.isOpenSuccess = isOpenSuccess;
            this.mag = mag;
        }
    }

}

