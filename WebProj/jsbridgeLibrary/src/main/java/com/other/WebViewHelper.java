package com.other;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.other.util.StringUtil;

/**
 * Title:webview帮助类
 * description:
 * autor:pei
 * created on 2020/5/14
 */
public class WebViewHelper {

    /**webView基本设置**/
    public void setWebViewConfig(WebView webView, Context context){
        if (webView == null) {
            return;
        }
        WebSettings webviewSettings = webView.getSettings();
        webviewSettings.setSupportZoom(false);//不支持缩放
        webviewSettings.setJavaScriptEnabled(true);//设置WebView允许执行JavaScript脚本
        // 自适应屏幕大小
        webviewSettings.setUseWideViewPort(true);//当前页面包含viewport属性标签，在标签中指定宽度值生效
        webviewSettings.setLoadWithOverviewMode(true);//设置WebView使用预览模式加载界面
        String cacheDirPath = context.getFilesDir().getAbsolutePath() + "cache/";
        webviewSettings.setAppCachePath(cacheDirPath);
        webviewSettings.setAppCacheEnabled(true);//设置Application缓存API开启
        //设置支持html5标签
        webviewSettings.setDomStorageEnabled(true);//设置开启DOM存储API权限,WebView能够使用DOM storage API
        webviewSettings.setSaveFormData(false);//设置webview是否保存表单数据,默认为true

        webviewSettings.setAllowFileAccess(true);//设置在WebView内部允许访问文件
        webviewSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        webviewSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//设置缓存加载模式
        webviewSettings.setTextZoom(100);//设置WebView中加载页面字体变焦百分比，默认100.
        webviewSettings.setSupportMultipleWindows(true);//设置webview支持多屏窗口
        //webView支持https和http混合加载模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webviewSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    /**关闭界面时清理webview配置**/
    public void destoryWebViewConfig(WebView webView,Context context) {
        if(webView!=null) {
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearCache(true);
        }
        if(context!=null) {
            //清空缓存，解决webview加载界面不全的问题
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    /***
     * 设置webview加载时的字节头
     * @param webView
     * @param header 字节头中加的内容
     *
     * 注：此设置加载字节头是 userAgent+header 模式,
     *    若有其他结构组合，可参考本方法写出来。
     */
    public void setUserAgent(WebView webView,String header){
        //设置webview请求头
        if(webView!=null&& StringUtil.isNotEmpty(header)) {
            String userAgent = webView.getSettings().getUserAgentString();
            webView.getSettings().setUserAgentString(userAgent + header);
        }
    }

    /**WebView加载网址**/
    public boolean loadUrl(WebView webView,String url){
        if(webView!=null&&StringUtil.isNotEmpty(url)){
            webView.loadUrl(url);
            return true;
        }
        return false;
    }

    /***
     * 加载 Assets下 html 文件
     * @param webView
     * @param filePath
     * @return
     *
     * 示例：若加载html路径为 assets/test.html,则参数为 “test.html”
     */
    public boolean loadAssetsFile(WebView webView,String filePath){
        if(webView!=null&&StringUtil.isNotEmpty(filePath)){
            webView.loadUrl("file:///android_asset/"+filePath);
            return true;
        }
        return false;
    }

    /**设置webView背景色**/
    public void setBackgroundColor(WebView webView,int color){
        webView.setBackgroundColor(color);
    }

}
