package com.other;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import com.other.util.StringUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Title:用于拦截webView中url的WebViewClient
 * description:
 * autor:pei
 * created on 2020/5/19
 */
public class WebViewClientInterceptor extends WebViewClient {

    public static final String UTF_8="UTF-8";
    public static final String GBK="GBK";

    //https://blog.csdn.net/cw19920617/article/details/79486191
    //https://cloud.tencent.com/developer/ask/61353
    //https://www.jianshu.com/p/08920c2bb128

    private OnOverrideUrlListener mOnOverrideUrlListener;
    private OnInterceptorListener mOnInterceptorListener;

    /**设置url加载的监听**/
    public void setOnOverrideUrlListener(OnOverrideUrlListener listener){
        this.mOnOverrideUrlListener=listener;
    }

    /**设置响应实体的监听**/
    public void setOnInterceptorListener(OnInterceptorListener listener){
        this.mOnInterceptorListener=listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
        //返回true表示拦截url加载,false表示默认加载url
        if(mOnOverrideUrlListener!=null){
            return mOnOverrideUrlListener.shouldOverrideUrlLoading(webView,request);
        }
        return super.shouldOverrideUrlLoading(webView, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        //返回true表示拦截url加载,false表示默认加载url
        if(mOnOverrideUrlListener!=null){
            return mOnOverrideUrlListener.shouldOverrideUrlLoading(webView,url);
        }
        return super.shouldOverrideUrlLoading(webView, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest request) {
        //返回null表示加载默认请求,否则加载拦截后改变的实体
        if(mOnInterceptorListener!=null){
            return mOnInterceptorListener.shouldInterceptRequest(webView,request);
        }
        return super.shouldInterceptRequest(webView, request);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
        //返回null表示加载默认请求,否则加载拦截后改变的实体
        if(mOnInterceptorListener!=null){
            return mOnInterceptorListener.shouldInterceptRequest(webView,url);
        }
        return super.shouldInterceptRequest(webView, url);
    }


    /**拦截url加载的监听**/
    public interface OnOverrideUrlListener{
        boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request);
        boolean shouldOverrideUrlLoading(WebView webView, String url);
    }

    /**拦截相应实体的监听**/
    public interface OnInterceptorListener{
        WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request);
        WebResourceResponse shouldInterceptRequest(WebView view, String url);
    }


    public WebResourceResponse getWebResourceResponse(InputStream data,String charset) {
        return new WebResourceResponse("text/javascript", charset, data);
    }

    /**
     * 将 html 写的界面转成 io 流
     * @param htmlPage html代码
     * @param charset  WebViewClientInterceptor.UTF_8 或 WebViewClientInterceptor.GBK
     *                 为 null 时表示使用默认编码集
     * @return
     */
    public InputStream getLocalHtmlPageStream(String htmlPage,String charset){
//        String htmlPage = "<html>\n" +
//                "<title>千度</title>\n" +
//                "<body>\n" +
//                "<a href=\"www.taobao.com\">千度</a>,比百度知道的多10倍\n" +
//                "</body>\n" +
//                "<html>";
        InputStream stream=null;
        if(StringUtil.isNotEmpty(charset)) {
            stream = new ByteArrayInputStream(htmlPage.getBytes(Charset.forName(charset)));
        }else{
            stream=new ByteArrayInputStream(htmlPage.getBytes());
        }
        return stream;
    }


    public String getDataByUrl(String url){
        //url="http://www.importnew.com/";
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            URL tempUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) tempUrl
                    .openConnection();
            //            连接主机超时时间
            httpURLConnection.setConnectTimeout(10 * 1000);
            //            设置从主机读取数据超时
            httpURLConnection.setReadTimeout(40 * 1000);
            bufferedReader = new BufferedReader(new
                    InputStreamReader(httpURLConnection
                    .getInputStream()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return stringBuilder.toString();
    }

}
