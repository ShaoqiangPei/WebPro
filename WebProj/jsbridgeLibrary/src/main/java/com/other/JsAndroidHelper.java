package com.other;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.other.util.StringUtil;
import com.other.util.WebLogUtil;

/**
 * Title:android 与 js 交互帮助类
 * description:
 * autor:pei
 * created on 2020/5/29
 */
@SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
public class JsAndroidHelper {

    private OnFunctionListener mOnFunctionListener;

    /**
     * 初始化设置
     * @param webView webview对象
     * @param requestTag  Android与js交互的标志，需要两端保持一致
     * @param listener Android接收js返回给Android的数据监听
     */
    public void init(WebView webView, String requestTag,OnFunctionListener listener){
        if(webView==null){
            throw new NullPointerException("====webView 不能为空======");
        }
        //requestTag 不能为空
        if(StringUtil.isEmpty(requestTag)){
            throw new NullPointerException("====requestTag 不能为空======");
        }
        //requestTag 不能为类似"android","Android","ANDRID"的字符串，否则会导致js调用Android无效
        if("android".equalsIgnoreCase(requestTag)){
            throw new SecurityException("====requestTag 不能为类似 'android','Android','ANDRID'的字符串，否则会导致js调用Android无效======");
        }
        //webView注册供js调用
        webView.addJavascriptInterface(this,requestTag);
        //设置监听
        this.mOnFunctionListener=listener;
    }


    /***
     * Android调用js
     *
     * @param webView webview对象
     * @param functionScript js方法及方法参数拼接成的字符串
     *
     * eg： String tag="two2";  //tag 用于区分方法调用的不同功能，要保证唯一性
     *      String functionScript="test2('"+tag+"','"+false+"')";  //js方法拼接
     *      androidCallJs(mWebView,tag,functionScript);    //Android 调用 js
     */
    public void androidCallJs(WebView webView,String tag,String functionScript){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//sdk>19才有用
            WebLogUtil.i("=========sdk>19=========");
            webView.evaluateJavascript("javascript:"+functionScript, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String response) {
                    WebLogUtil.i("===sdk>19===收到返回数据===tag="+tag+"   response="+response);
                    if(mOnFunctionListener!=null){
                        mOnFunctionListener.getResult(tag,response);
                    }
                }
            });
        }else{
            WebLogUtil.i("=========sdk<=19=========");
            webView.loadUrl("javascript:"+functionScript);
        }
    }

    /**
     * 供js调用的 Android 方法
     * @param tag 方法tag，用于区分不同的方法
     * @param result  js 调用 Android 时给 Android 的传参
     * @return js 调用 Android 时，Android 给 js 的返回结果
     */
    @JavascriptInterface
    public String jsCallAndroid(String tag,String result) {
        String backData=null;//android给js返回的数据
        WebLogUtil.i("====js返回给android的数据=====tag=" + tag + "  result=" + result);
        if(mOnFunctionListener!=null){
            backData=mOnFunctionListener.getResult(tag,result);
        }
        return backData;
    }


    public interface OnFunctionListener{
        String getResult(String tag, String result);
    }

}
