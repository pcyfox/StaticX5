package com.silang.superfileview.webplayer;


import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.silang.superfileview.VideoHtmlAssetLoader;
import com.silang.superfileview.view.WebPageLoadListener;
import com.silang.superfileview.view.X5WebView;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.WeakReference;

import static android.util.Base64.DEFAULT;

public class WebVideoPlayManager implements WebPlayerListener, WebPlayerController {
    private static final String TAG = "WebVideoPlayManager";
    private String currentUrl;
    private WebPlayerListener webPlayerListener;

    private WebVideoPlayManager() {
    }

    private static WebVideoPlayManager instance = new WebVideoPlayManager();

    private WeakReference<X5WebView> webViewWeakReference;

    public static WebVideoPlayManager getInstance() {
        return instance;
    }

    public void init(X5WebView webView) {
        this.webViewWeakReference = new WeakReference<>(webView);
        webView.addJavascriptInterface(this, "Android");
    }

    public WebPlayerListener getWebPlayerListener() {
        return webPlayerListener;
    }

    public void setWebPlayerListener(WebPlayerListener webPlayerListener) {
        this.webPlayerListener = webPlayerListener;
    }

    @Override
    public void load(final String url) {
        currentUrl = url;

        Log.d(TAG, "load() called with: url = [" + url + "]");
        X5WebView webView = webViewWeakReference.get();
        if (webView == null) return;
/*
        String html = videoHtmlAssetLoader.loadGroupData(webView.getContext(), url);
        webView.loadDataWithBaseURL(url, html, "text/html", "utf-8", null);
*/
        webView.loadUrl("file:///android_asset/web/VideoPlayer.html");
        // 只需要将第一种方法的loadUrl()换成下面该方法即可
        webView.setWebPageLoadListener(new WebPageLoadListener() {
            @Override
            public void onStartLoad(WebView webView, String s) {

            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                Log.d(TAG, "onPageFinished() called with: webView = [" + webView + "], s = [" + s + "]");
                //webView.loadUrl("javascript:set_video_listener()");
                String raw = "javascript:set_url(" + "'URL'" + ")";
                String base54Url = new String(Base64.encode(url.getBytes(), DEFAULT));
                webView.loadUrl(raw.replace("URL", base54Url));
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {

            }

            @Override
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {

            }
        });
    }

    @Override
    public void play() {
        Log.d(TAG, "play() called");
        if (webViewWeakReference == null) return;
        X5WebView webView = webViewWeakReference.get();
        if (webView == null) return;
        webView.loadUrl("javascript:play()");
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause() called");
        if (webViewWeakReference == null) return;
        X5WebView webView = webViewWeakReference.get();
        if (webView == null) return;
        webView.loadUrl("javascript:pause()");
        onPause();
    }

    @Override
    public void seekTo(int position) {
        Log.d(TAG, "seekTo() called with: position = [" + position + "]");
        if (webViewWeakReference == null) return;
        X5WebView webView = webViewWeakReference.get();
        if (webView == null) return;
        webView.loadUrl("javascript:seek_to('position')".replace("position", position + ""));
    }

    @Override
    public void muted(boolean isMuted) {
        Log.d(TAG, "muted() called with: isMuted = [" + isMuted + "]");
        if (webViewWeakReference == null) return;
        X5WebView webView = webViewWeakReference.get();
        if (webView == null) return;
        webView.loadUrl("javascript:muted('isMuted')".replace("isMuted", isMuted + ""));
    }

    @JavascriptInterface
    public void print(String log) {
        Log.d(TAG, "WebPrint in JS log: " + log);
    }


    @Override
    @JavascriptInterface
    public void onInit() {
        Log.d(TAG, "onInit() called");
        if (webPlayerListener != null) {
            webPlayerListener.onInit();
        }
    }

    @Override
    @JavascriptInterface
    public void onCanPlay() {
        Log.d(TAG, "onCanPlay() called");
        if (webViewWeakReference == null) return;
        X5WebView webView = webViewWeakReference.get();
        webView.onPageFinished(webView, currentUrl);
        if (webPlayerListener != null) {
            webPlayerListener.onCanPlay();
        }
    }

    @Override
    @JavascriptInterface
    public void onPlay() {
        Log.d(TAG, "onPlay() called");
        if (webPlayerListener != null) {
            webPlayerListener.onPlay();
        }
    }

    @Override
    @JavascriptInterface
    public void onPause() {
        Log.d(TAG, "onPause() called");
        if (webPlayerListener != null) {
            webPlayerListener.onPause();
        }
    }

    @Override
    @JavascriptInterface
    public void onEnded() {
        Log.d(TAG, "onEnded() called");
        if (webPlayerListener != null) {
            webPlayerListener.onEnded();
        }
    }

    @Override
    @JavascriptInterface
    public void onTimeUpDate(int currentTime) {
        //   Log.d(TAG, "timeUpDate() called with: currentTime = [" + currentTime + "]");
        if (webPlayerListener != null) {
            webPlayerListener.onTimeUpDate(currentTime);
        }
    }

    @Override
    @JavascriptInterface
    public void onSeeked() {
        Log.d(TAG, "onSeeked() called");
        if (webPlayerListener != null) {
            webPlayerListener.onSeeked();
        }
    }

    @Override
    @JavascriptInterface
    public void onSeeking() {
        if (webPlayerListener != null) {
            webPlayerListener.onSeeking();
        }
    }

    @Override
    @JavascriptInterface
    public void onPlaying() {
        Log.d(TAG, "onPlaying() called");
        if (webPlayerListener != null) {
            webPlayerListener.onPlaying();
        }
    }

    @Override
    @JavascriptInterface
    public void onLoadedMetadata() {
        Log.d(TAG, "onLoadedMetadata() called");
        if (webPlayerListener != null) {
            webPlayerListener.onLoadedMetadata();
        }
    }

    public void release() {
        Log.d(TAG, "release() called");
        if (webViewWeakReference != null) {
            webViewWeakReference.clear();
            webViewWeakReference = null;
        }
        currentUrl = null;
        webPlayerListener = null;
    }
}
