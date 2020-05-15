package com.webproj;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.TextView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.other.JsWebChromeClient;
import com.other.WebViewHelper;
import com.other.util.WebLogUtil;

public class MainActivity extends AppCompatActivity{

    private TextView mTv;
    private BridgeWebView mWebView;

    private WebViewHelper mWebViewHelper;
    private JsWebChromeClient mJsWebChromeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //打开log
        WebLogUtil.setDebug(true);

        initView();
        initData();
        setListener();
    }

    private void initView() {
        mWebView=findViewById(R.id.webview);
        mTv=findViewById(R.id.tv);
    }

    private void initData() {
        mWebViewHelper=new WebViewHelper();
        mJsWebChromeClient=new JsWebChromeClient();
        //设置打开文件,相册等的处理
        mJsWebChromeClient.setOpenFileChooserCallBack(new JsWebChromeClient.OpenFileChooserCallBack() {
            @Override
            public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {

            }

            @Override
            public void showFileChooserCallBack(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

            }
        });
        //webview基础设置
        mWebViewHelper.setWebViewConfig(mWebView,MainActivity.this);
        //设置WebChromeClient,用以支持webview显示对话框,网站图标,网站title,加载进度等等
        mWebView.setWebChromeClient(mJsWebChromeClient);

        //注:不能设置"mWebView.setWebViewClient(new WebViewClient())",否则辉导致Android与js交互不通
//        //设置WebViewClient向一个网页发送请求，可以返回文本，文件等
//        mWebView.setWebViewClient(new WebViewClient());

        //加载本地html文件
        mWebViewHelper.loadAssetsFile(mWebView,"test.html");
        //加载网址
        //mWebViewHelper.loadUrl(mWebView,"https://www.baidu.com");
    }

    private void setListener() {
        //注册默认方法供js调用
        mWebView.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                WebLogUtil.i("====收到web数据====" + data);

                //发送数据给js
                function.onCallBack("====Android给js数据回传===");
            }
        });

        //注册带tag的方法供js调用
        mWebView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                WebLogUtil.i("得到JS传过来的数据 data ="+data);
                //发送消息给js
                function.onCallBack("传递数据给JS");
            }
        });

        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getDefaultWithOutVaule();
//                getDefaultWithVaule();
                getTagFunction();
            }
        });
    }

    @Override
    protected void onDestroy() {
        //清理webview相关配置
        mWebViewHelper.destoryWebViewConfig(mWebView,MainActivity.this);
        super.onDestroy();
    }

    /**Android调用js默认方法,不接受js返回值**/
    private void getDefaultWithOutVaule(){
        mWebView.send("=====到底啥玩意=====");
    }

    /**Android调用js默认方法,接受js返回值**/
    private void getDefaultWithVaule(){
        mWebView.send("=====到底啥玩意=====", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                WebLogUtil.i("======返回结果啊====="+data);

                mTv.setText(data);
            }
        });
    }

    /**Android调用js带tag方法,接受js返回值**/
    private void getTagFunction(){
        mWebView.callHandler("functionJs", "我是王者荣耀", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                WebLogUtil.i("=======调用js返回数据====data=" + data);
            }
        });
    }

}