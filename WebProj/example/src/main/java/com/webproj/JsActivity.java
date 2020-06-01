package com.webproj;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.other.JsAndroidHelper;
import com.other.WebViewHelper;
import com.other.util.WebLogUtil;

/**
 * Title:
 * description:
 * autor:pei
 * created on 2020/6/1
 */
public class JsActivity extends AppCompatActivity {

    private TextView mTv;
    private Button mBtn;
    private WebView mWebView;
    private WebViewHelper mWebViewHelper;
    private JsAndroidHelper mJsAndroidHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js);

        initView();
        initData();
        setListener();
    }

    private void initView() {
        mTv = findViewById(R.id.tv);
        mBtn = findViewById(R.id.btn);
        mWebView = findViewById(R.id.web_view);
    }

    private void initData() {
        mWebViewHelper=new WebViewHelper();
        //webview基础设置
        mWebViewHelper.setWebViewConfig(mWebView, JsActivity.this);
        //设置可让界面弹出alert等提示框
        mWebView.setWebChromeClient(new WebChromeClient());
        //设置网页跳转时在webview中显示,而不是在手机自带浏览器上打开
        mWebView.setWebViewClient(new WebViewClient());

        mJsAndroidHelper=new JsAndroidHelper();
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {
                WebLogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                if("tag1".equals(tag)){
                    mTv.setText("js 调用 Android");
                    return "你很牛啊";
                }
                return null;
            }
        });

        //加载网页
        mWebViewHelper.loadAssetsFile(mWebView,"test2.html");
    }

    private void setListener() {
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag="two4";//方法的tag,要保持唯一性
                String functionScript="test2('"+tag+"','"+false+"')";//js 方法及方法参数拼接
                mJsAndroidHelper.androidCallJs(mWebView,tag,functionScript);//Android调用js
            }
        });
    }

    @Override
    protected void onDestroy(){
        //清理webview相关配置
        mWebViewHelper.destoryWebViewConfig(mWebView, JsActivity.this);
        super.onDestroy();
    }

}
