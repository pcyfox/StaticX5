package com.silang.superfileview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.silang.superfileview.XUtils;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import static com.tencent.smtt.sdk.WebSettings.LOAD_NO_CACHE;

public class X5WebView extends WebView {
    private static final String TAG = "X5WebView";
    private WebPageLoadListener webPageLoadListener;
    private boolean isCanTouchable = true;

    public X5WebView(Context arg0) {
        super(arg0);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        init();
    }

    public boolean isUseX5Web() {
        return getX5WebViewExtension() != null;
    }

    private void init() {
        initWebViewSettings();
        WebViewClient client = new WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (XUtils.isURL(url)) {
                    Log.d(TAG, "shouldOverrideUrlLoading() called with: view = [" + view + "], url = [" + url + "]");
                    view.loadUrl(url);
                    if (webPageLoadListener != null) {
                        webPageLoadListener.onStartLoad(view, url);
                    }
                    onPageStart(view, url);
                } else {
                    Log.e(TAG, "shouldOverrideUrlLoading() called with: view = [" + view + "], url = [" + url + "]");
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                Log.d(TAG, "onPageFinished() called with: webView = [" + webView + "], s = [" + s + "]");
                super.onPageFinished(webView, s);
                X5WebView.this.onPageFinished(webView, s);
                if (webPageLoadListener != null) {
                    webPageLoadListener.onPageFinished(webView, s);
                }
            }


            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                Log.e(TAG, "onReceivedError() called with:  url = [" + webResourceRequest.getUrl() + "], webResourceError ErrorCode = " + webResourceError.getErrorCode() + " webResourceError Description= [" + webResourceError.getDescription() + "]");
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (webPageLoadListener != null) {
                    webPageLoadListener.onReceivedError(webView, webResourceRequest, webResourceError);
                }
            }

            @Override
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                Log.e(TAG, "onReceivedHttpError() called with: url = [" + webResourceRequest.getUrl() + "], webResourceRequest = [" + webResourceRequest + "], webResourceResponse = [" + webResourceResponse + "]");
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                if (webPageLoadListener != null) {
                    webPageLoadListener.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                }
            }
        };
        this.setWebViewClient(client);

        getView().setClickable(true);
        IX5WebViewExtension extension = getX5WebViewExtension();
        if (extension == null) {
            Log.e(TAG, "X5WebView()  X5  加载失败！ ");
        } else {
            Log.e(TAG, "X5WebView()  X5  加载成功！ ");
        }
    }

    public void onPageFinished(WebView webView, String s) {
    }


    public void onPageStart(WebView webView, String s) {
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        // 1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
        // 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(LOAD_NO_CACHE);
        webSetting.setAppCacheEnabled(false);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isCanTouchable) return false;
        return super.dispatchTouchEvent(ev);
    }

    public WebPageLoadListener getWebPageLoadListener() {
        return webPageLoadListener;
    }

    public void setWebPageLoadListener(WebPageLoadListener webPageLoadListener) {
        this.webPageLoadListener = webPageLoadListener;
    }

    public boolean isCanTouchable() {
        return isCanTouchable;
    }

    public void setCanTouchable(boolean canTouchable) {
        isCanTouchable = canTouchable;
    }

    public void release() {

        //加载null内容
        loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        //清除历史记录
        clearHistory();
        //移除WebView
        ((ViewGroup) getParent()).removeView(this);
        //销毁VebView
        destroy();
        webPageLoadListener = null;
    }


}
