## WebViewClientInterceptor使用说明

### 概述
`WebViewClientInterceptor`继承自`WebViewClient`，主要用于处理`WebView`加载网页的拦截。
### 使用说明
#### 一.WebView 加载原理
`WebView`加载原理从 `WebView`设置`url`开始，然后请求响应实体，最后将结果显示到ui屏幕上。
知道了大致原理，然后在拦截的时候，可以从两个方面着手：
- 第一个是在设置`url`时修改url
- 第二个是在响应实体替换实体

这样就可以达到拦截webview加载的功能了。要在`url`加载期拦截，则可以重写`WebViewClient`的`shouldOverrideUrlLoading`方法。
如果要在响应实体阶段拦截，可以重写`WebViewClient`的`shouldInterceptRequest`方法。接下来让我们具体讲讲这两个方法之于拦截的使用吧。
#### 二. webView加载拦截
基于拦截的处理，可以在`WebView`的`url`加载阶段和响应实体阶段进行拦截替换。  
加载`url`阶段的拦截方法是`WebViewClient`的`shouldOverrideUrlLoading`方法  
响应实体阶段的拦截方法是`WebViewClient`的`shouldInterceptRequest`方法  
#### 三. 拦截之shouldOverrideUrlLoading的使用
`shouldOverrideUrlLoading`是在`webView`加载`url`阶段执行拦截的。我继承`WebViewClient`封装了一个`WebViewClientInterceptor`类，
用于在webview使用过程中执行拦截功能。现在看看`WebViewClientInterceptor`拦截`url`在`Activity`中的使用。  
若要在`MainActivity`中拦截webView的url加载，可像下面这样：
```
public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebViewHelper mWebViewHelper;
    private WebViewClientInterceptor mWebViewClientInterceptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtil.setDebug(true);

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
        mWebViewHelper.setWebViewConfig(mWebView, MainActivity.this);
        mWebViewClientInterceptor = new WebViewClientInterceptor();

        mWebViewClientInterceptor.setOnOverrideUrlListener(new WebViewClientInterceptor.OnOverrideUrlListener() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                ToastUtil.shortShow("这是拦截url的操作,原url="+webView.getUrl());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                ToastUtil.shortShow("这是拦截url的操作,原url="+webView.getUrl());
                return true;
            }
        });

        //设置WebViewClient向一个网页发送请求，可以返回文本，文件等
        mWebView.setWebViewClient(mWebViewClientInterceptor);
        //设置可让界面弹出alert等提示框
        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.loadUrl("file:///android_asset/test.html");
    }

    @Override
    protected void onDestroy() {
        //清理webview相关配置
        mWebViewHelper.destoryWebViewConfig(mWebView, MainActivity.this);
        super.onDestroy();
    }
}
```
接着贴出`test.html`代码：
```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="content-type" content="text/html" charset="UTF-8">
    <!--手机高宽度撑满屏幕-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>JS交互</title>
</head>
<body>

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="alert('hello')" /></p>
<p><input type="button" id="enter1" value="js调用Android(调用自定义tag的方法)" onclick="testBaidu()" /></p>

<p id="show"></p>
</body>

<script type="text/javascript">
        function testBaidu(){
           window.location.href="http://www.baidu.com"
        }
</script>
</html>
```
#### 四. 拦截之shouldInterceptRequest的使用
`shouldInterceptRequest`是在`WebView`加载网页的响应实体阶段进行拦截.`shouldInterceptRequest`一般用于拦截替换,而这种替换一般分为
`本地网页替换`和`加载网络网页替换`。  
**本地Html替换加载**你可以这样：
```
public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebViewHelper mWebViewHelper;
    private WebViewClientInterceptor mWebViewClientInterceptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtil.setDebug(true);

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
        mWebViewHelper.setWebViewConfig(mWebView, MainActivity.this);
        mWebViewClientInterceptor = new WebViewClientInterceptor();

        mWebViewClientInterceptor.setOnInterceptorListener(new WebViewClientInterceptor.OnInterceptorListener() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String htmlPage = "<html>\n" +
                        "<title>千度</title>\n" +
                        "<body>\n" +
                        "<a href=\"www.taobao.com\">千度</a>,比百度知道的多10倍\n" +
                        "</body>\n" +
                        "<html>";
                InputStream inputStream = mWebViewClientInterceptor.getLocalHtmlPageStream(htmlPage, null);
                WebResourceResponse response = mWebViewClientInterceptor.getWebResourceResponse(inputStream, WebViewClientInterceptor.UTF_8);

                return response;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                String htmlPage = "<html>\n" +
                        "<title>千度</title>\n" +
                        "<body>\n" +
                        "<a href=\"www.taobao.com\">千度</a>,比百度知道的多10倍\n" +
                        "</body>\n" +
                        "<html>";
                InputStream inputStream = mWebViewClientInterceptor.getLocalHtmlPageStream(htmlPage, null);
                WebResourceResponse response = mWebViewClientInterceptor.getWebResourceResponse(inputStream, WebViewClientInterceptor.UTF_8);

                return response;
            }
        });


        //设置WebViewClient向一个网页发送请求，可以返回文本，文件等
        mWebView.setWebViewClient(mWebViewClientInterceptor);
        //设置可让界面弹出alert等提示框
        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.loadUrl("https://www.baidu.com/");
    }

    private void setListener() {
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        //清理webview相关配置
        mWebViewHelper.destoryWebViewConfig(mWebView, MainActivity.this);
        super.onDestroy();
    }
}
```
**网络Html替换加载**你可以这样：
```
public class MainActivity extends AppCompatActivity {

    private TextView mTv;
    private Button mBtn;
    private WebView mWebView;
    private WebViewHelper mWebViewHelper;
    private WebViewClientInterceptor mWebViewClientInterceptor;
    private String mTempResult;
    private long mLastTime;

    public String getmTempResult() {
        return mTempResult;
    }

    public void setmTempResult(String mTempResult) {
        this.mTempResult = mTempResult;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtil.setDebug(true);

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
        mWebViewClientInterceptor = new WebViewClientInterceptor();
        //webview基础设置
        mWebViewHelper.setWebViewConfig(mWebView, MainActivity.this);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return mWebViewClientInterceptor.getDataByUrl("https://github.com/");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setmTempResult(s);
            }
        }.execute();

        mWebViewClientInterceptor.setOnInterceptorListener(new WebViewClientInterceptor.OnInterceptorListener() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                mLastTime=System.currentTimeMillis();
                while (true) {
                    LogUtil.i("============等待=========");
                    if (getmTempResult() != null||(System.currentTimeMillis()-mLastTime)>1000*10) {
                        break;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(getmTempResult()!=null) {
                    String temp=getmTempResult();
                    if(temp.contains("utf-8")){
                        temp=temp.replaceAll("utf-8","");
                    }

                    InputStream inputStream = mWebViewClientInterceptor.getLocalHtmlPageStream(temp, null);
                    WebResourceResponse response = mWebViewClientInterceptor.getWebResourceResponse(inputStream, WebViewClientInterceptor.UTF_8);
                    return response;
                }
                return null;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                mLastTime=System.currentTimeMillis();
                while (true) {
                    LogUtil.i("============等待=========");
                    if (getmTempResult() != null||(System.currentTimeMillis()-mLastTime)>1000*10) {
                        break;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(getmTempResult()!=null) {
                    String temp=getmTempResult();
                    if(temp.contains("utf-8")){
                        temp=temp.replaceAll("utf-8","");
                    }

                    InputStream inputStream = mWebViewClientInterceptor.getLocalHtmlPageStream(temp, null);
                    WebResourceResponse response = mWebViewClientInterceptor.getWebResourceResponse(inputStream, WebViewClientInterceptor.UTF_8);
                    return response;
                }
                return null;
            }
        });

        //设置WebViewClient向一个网页发送请求，可以返回文本，文件等
        mWebView.setWebViewClient(mWebViewClientInterceptor);
        //设置可让界面弹出alert等提示框
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("https://www.baidu.com/");
    }

    @Override
    protected void onDestroy() {
        //清理webview相关配置
        mWebViewHelper.destoryWebViewConfig(mWebView, MainActivity.this);
        super.onDestroy();
    }
}
```
这里我`WebView`显示成了`html`源码了，也不知道啥原因，有知道的大大可以指点一二。由于这个不是重点，本文就暂不讨论。
#### 五.需要注意的点
`shouldOverrideUrlLoading`和`shouldInterceptRequest`都可以做`webView`加载网页的拦截，但是他们拦截的主体不一样：
- `shouldOverrideUrlLoading`拦截的是`url`加载阶段
- `shouldInterceptRequest`加载的是响应主体阶段

拦截的内容不一样：
- `shouldOverrideUrlLoading`主要拦截`url`
- `shouldInterceptRequest`可拦截`url`,`js`，`css`等

也就是说整体而言`shouldInterceptRequest`拦截的范围比`shouldOverrideUrlLoading`广。但是`shouldOverrideUrlLoading`能响应本地`html`文件加载，如`assets`文件夹下的`html`加载，
而`shouldInterceptRequest`只能响应`url`之类的，而不响应本地文件加载。还有一个需要注意的是，`shouldOverrideUrlLoading`的拦截处在`shouldInterceptRequest`上游(由`webView`加载原理决定)，
所以在`shouldInterceptRequest`拦截的时候，我们一般不重写`shouldOverrideUrlLoading`，这是为了保证`shouldOverrideUrlLoading`方法返回为`false`，若`shouldOverrideUrlLoading`方法返回为`true`，
则表示"上游"已经拦截了，那这时再在`shouldInterceptRequest`进行拦截已经不起作用了。
#### 六.返回键拦截
`WebView`关于返回键的拦截也是比较常用的操作，一般是重写`Activity`的`onKeyDown(int keyCode, KeyEvent event)`方法，下面贴出主要代码：
```
public class MainActivity extends AppCompatActivity {
     //其他代码省略
     //......
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //按返回键操作并且能回退网页
            if (keyCode == KeyEvent.KEYCODE_BACK && !mWebView.canGoBack()) {
                //关闭界面
                ToastUtil.shortShow("关闭界面");
                return true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }
}
```


