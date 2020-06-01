## js接入流程说明

### 概述
`js接入流程说明`的讲解其实是对封装类`JsAndroidHelper`使用的讲解。`JsAndroidHelper`是一个封装了原始的`android`与`webView`交互的工具类，
其利用最原始的方式实现`android`与`webView`交互,并以接入迅速简单而为开发者喜欢。

### 使用说明
#### 一. 依赖
要使用`JsAndroidHelper`实现`anroid`与`webView`交互,则此库版本不得低于`0.0.4`,即本库引用最低版本设置如下：
```
dependencies {
    implementation 'com.github.ShaoqiangPei:WebPro:0.0.4'
}
```
注:本库完整引用接入请参考[README.md](https://github.com/ShaoqiangPei/WebPro/blob/master/README.md)第一条。
#### 二. JsAndroidHelper主要方法介绍
下面简单介绍下`JsAndroidHelper`中的几个主要方法
```
    /**
     * 初始化设置
     * @param webView webview对象
     * @param requestTag  Android与js交互的标志，需要两端保持一致
     * @param listener Android接收js返回给Android的数据监听
     */
    public void init(WebView webView, String requestTag,OnFunctionListener listener)

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
    public void androidCallJs(WebView webView,String tag,String functionScript)

    /**
     * 供js调用的 Android 方法
     * @param tag 方法tag，用于区分不同的方法
     * @param result  js 调用 Android 时给 Android 的传参
     * @return js 调用 Android 时，Android 给 js 的返回结果
     */
    @JavascriptInterface
    public String jsCallAndroid(String tag,String result) 
```
#### 三. JsAndroidHelper 之 android 调用 js
##### 3.1 使用前准备
在此，我封装了一个工具类`JsAndroidHelper`，下面看看怎么利用它实现`android`调用`js`吧。
在`android`与`js`交互时，我们需要设置一个接口监听，然后在监听中加上`android`与`js`交互的标志，如下：
```
        //webView注册供js调用
        webView.addJavascriptInterface(this,"requestTag");
```
这里的字符串"requestTag"即是`android`与`js`交互的标志，`js`中在调用`android`方法的时候，需要加入相同的交互标志，以上面交互标志为例，则`js`调用`android`方法类似如下：
```
//注：这里的requestTag即为Android与js交互的标志，需要两端保持一致，
//然后test2()为Android提供的供js调用的方法
window.requestTag.test2();
```
因此，在`android`的`MainActivity`中使用`JsAndroidHelper`时，我们需要做以下基本操作：
```
       //声明对象
       private JsAndroidHelper mJsAndroidHelper;

        //初始化对象
        mJsAndroidHelper=new JsAndroidHelper();
        //JsAndroidHelper做初始化设置
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {
                LogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                if("tag1".equals(tag)){
                    return "你很牛啊";
                }
                return null;
            }
        });
```
这里`init(WebView webView, String requestTag,OnFunctionListener listener)`中的`requestTag`，即以上代码中的`hello`就是`android`与`js`交互的标志，需要`android`端的设置和`js`写调用方法时保持一致。
然后,`OnFunctionListener`主要用于接收`js`调用`Android`时传给`adnroid`的值。
ok，以上讲明白以后，下面需要大家理解的是，不论是`android`调用`js`，还是`js`调用`android`，都需要设置`方法tag`,此`方法tag`主要用于区分不同的方法，方便`android`与`js`交互过程中不同业务逻辑方法的区分处理。
##### 3.2 Android 调用 js 无参方法
例如在`js`中有这样一个无参方法：
```
<script type="text/javascript">
    //注:此处的tag为方法tag，必须传入，并且保持Android中方法与此方法的tag一致
    function test2(tag){
            document.getElementById("show").innerHTML="  大师傅似的"
    }
</script>
```
则在`adnroid`中调用`test2(tag)`如下：
```
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag="two4";//方法的tag,要保持唯一性
                String functionScript="test2('"+tag+"')";//js 方法及方法参数拼接
                mJsAndroidHelper.androidCallJs(mWebView,tag,functionScript);//Android调用js
            }
        });
```
##### 3.3 Android 调用 js 有参方法
例如`js`中有一个如下的有参方法：
```
    //注：tag为方法tag，flag为方法参数
    function test2(tag,flag){
        if(flag){
            document.getElementById("show").innerHTML= tag+"  大师傅似的"
        }else{
            document.getElementById("show").innerHTML= tag+"  的身份"
        }
    }
```
则在`android`中要调用这个方法，则如下：
```
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag="two4";//方法的tag,要保持唯一性
                String functionScript="test2('"+tag+"','"+false+"')";//js 方法及方法参数拼接
                mJsAndroidHelper.androidCallJs(mWebView,tag,functionScript);//Android调用js
            }
        });
```
其中`two4`为`方法tag`,而`test2`为`js`中方法名，`functionScript`的值即为 js 方法及方法参数拼接的字符串。
#### 3.4 Android 调用 js，js做调用后对应应答
有时我们会有这样的需求：`android`调用`js`，`js`方法返回对应数据。
那么，这种需求`JsAndroidHelper`也是可以实现的，下面以`android`调用`js`无参方法为例，在`js`中有这样一个方法：
```
    //注:此处的tag为方法tag，必须传入，并且保持Android中方法与此方法的tag一致
    function test2(tag){
            document.getElementById("show").innerHTML="  大师傅似的"

            //android调用"test2"方法后，js给Android提供的返回数据
           window.hello.jsCallAndroid(tag,'我是android调用js后js返回的数据');
    }
```
以上` window.hello.jsCallAndroid(tag,'我是android调用js后js返回的数据');`中`hello`即为`android`与`js`交互的标志，然后`tag`即为`android`调用`js`方法设置的`tag`,这里`js`是做该方法的应答,所以返回的`tag`与`test2(tag)`接收的`tag`保持一致，方便返回值在`android`中做对应处理。然后最后的 " 我是android调用js后js返回的数据" 即为`android`调用`js`后，`js`给`android`的应答数据。
`android`中调用该方法如下：
```
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag="two4";//方法的tag,要保持唯一性
                String functionScript="test2('"+tag+"')";//js 方法及方法参数拼接
                mJsAndroidHelper.androidCallJs(mWebView,tag,functionScript);//Android调用js
            }
        });
```
然后在`android`中我们这样接收`js`提供的返回数据：
```
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {
                LogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                if("two4".equals(tag)){
                    //对Android调用tag="two4" 的 js 方法 所返回的结果提供处理逻辑
                   //.... 
                }
                return null;
            }
        });
```
`android`调用`js`有参方法后`js`提供返回值在`android`中处理的流程同上，这里就不做赘述了。
#### 四. JsAndroidHelper 之 js 调用 android
##### 4.1 整体理解
上文已经讲述了`android`调用`js`方法，那么接下来，我们讲讲`js`调用`anroid`方法。
`js`调用`android`方法的话，我们并不需要在`MainActivity`中写一个方法供`js`调用，因为`JsAndroidHelper`中提供了一个固有方法：
```
    /**
     * 供js调用的 Android 方法
     * @param tag 方法tag，用于区分不同的方法
     * @param result  js 调用 Android 时给 Android 的传参
     * @return js 调用 Android 时，Android 给 js 的返回结果
     */
    @JavascriptInterface
    public String jsCallAndroid(String tag,String result) 
```
供`js`调用。那么在`js`中要想调用`android`功能的时候，你可以这样：
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

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="test1()"/></p>


<p id="show"></p>
</body>

<script type="text/javascript">
    function test1(){
        //hello为Android与js交互标记，两端必须保持一致
        //jsCallAndroid为js调用Android方法，必须这样写
        // tag1 为方法tag，用于区分不同方法的逻辑处理
        window.hello.jsCallAndroid('tag1','我是js调用android的结果');
    }

</script>
</html>
```
然后在`android`中你需要做如下处理：
```
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {
                LogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                if("tag1".equals(tag)){
                    //对js调用Android tag="tag1" 的方法提供处理逻辑
                   //.... 
                }
                return null;
            }
        });
```
即以上`tag1`中的逻辑处理即为`js`调用`android`时在`android`中要做的相应处理。
#### 4.2 js 调用 Android 有参/无参 方法
之前已经讲过，那么这里`js`要调用`android`无参方法的话，在`js`中可以这样写方法：
```
<script type="text/javascript">
    function test1(){
        //hello为Android与js交互标记，两端必须保持一致
        //jsCallAndroid为js调用Android方法，必须这样写
        // tag1 为方法tag，用于区分不同方法的逻辑处理
        window.hello.jsCallAndroid('tag1',null);
    }

</script>
```
即是在调用`jsCallAndroid('tag1',null);`方法时，第二个参数传null
**js 调用 Android**有参方法
`js`调用`android`有参方法的时候，可以类似下面这样：
```
<script type="text/javascript">
    function test1(){
        //hello为Android与js交互标记，两端必须保持一致
        //jsCallAndroid为js调用Android方法，必须这样写
        // tag1 为方法tag，用于区分不同方法的逻辑处理
        window.hello.jsCallAndroid('tag1','我是js调用android的结果');
    }

</script>
```
其中"我是js调用android的结果"即为`js`调用`android`时的有参方法。当然，大家也会郁闷，这里只是一个参数啊，当`android`中的方法涉及到多个参数咋办？
这里，我们可以将`android`方法中需要用到的多个参数按一定规则拼接成一个字符串，由`js`作为一个字符串传给`android`,然后`android`拿到字符串后解析成多个参数，接着执行处理逻辑就行。
`js`传参，然后在`android`中我们这样接收参数：
```
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {
                LogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                //result 即为 js 传给 Android 的参数

                return null;
            }
        });
```
在以上的`getResult(String tag, String result) `方法中，`result`即为 `js `传给 `Android `的参数
#### 4.3 js 调用 Android 方法后，Android 返回数据
同`android`调用`js`后收到`js`"返回结果"一样，在`js`调用完`android`以后，我们仍希望`anroid`可以返回结果。那么怎么处理呢？
ok,首先我们仍需要直接在`js`中调用`android`的类`JsAndroidHelper`中的`public String jsCallAndroid(String tag,String result)`方法，这里我们需要注意的是这个方法是有返回值的(返回`String`),那么在`js`中我们可以调用`android`后这样接收返回结果：
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

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="test1()"/></p>

<p id="show"></p>
</body>

<script type="text/javascript">
    function test1(){
      var temp=window.hello.jsCallAndroid('tag1','我是js调用android的结果');

      document.getElementById("show").innerHTML= 'tag1'+"    "+temp;
    }
</script>
</html>
```
注意以上代码中的：
```
 var temp=window.hello.jsCallAndroid('tag1','我是js调用android的结果');
```
表示`js`调用`adnroid`方法，然后传参为"我是js调用android的结果",方法tag为`tag1`, `temp`即为`js`调用`adnroid`后`android`方法提供的返回值。在Android中，我们可以这样处理：
```
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {
                LogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                if("tag1".equals(tag)){
                    //对js调用Android tag="tag1" 的方法提供处理逻辑
                   //.... 
                   return "你真牛啊";
                }
                return null;
            }
        });
```
注意`android`中以上代码
```
            public String getResult(String tag, String result) {
                LogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                if("tag1".equals(tag)){
                    //对js调用Android tag="tag1" 的方法提供处理逻辑
                   //.... 
                   return "你真牛啊";
                }
                return null;
            }
```
其中 ` if("tag1".equals(tag))`即为`js`调用`android`的一个方法，然后`if`中要处理的逻辑即是`js`调用`Android`方法后，`android`方法要处理的逻辑，结尾` return "你真牛啊";`即表示`android`方法返回给`js`的数据，若不需要给`js`返回数据，则`if`中不需要返回`return`。则整个方法给`js`的返回值为`null`。一般我们认为，当`android`给`js`返回的结果为`null`时，`js`没必要几首这个返回值，同时表示`js`调用`android`后,`android`并未做出返回应答。
#### 五. JsAndroidHelper 在 MainActivity 中使用
`android`与`js`交互，`Webview`是必不可少了，你需要在自己的布局中加入`webView`控件，例如下面这样:
```
    <WebView
        android:id="@+id/web_view"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="30dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn"
        app:layout_constraintBottom_toBottomOf="parent"/>
```
前面已经详细讲过了`JsAndroidHelper`在`android`与`js`交互中的用法，那么下面贴出`JsAndroidHelper` 在 `MainActivity `中使用代码：
```
package com.otherdemo;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    private TextView mTv;
    private Button mBtn;
    private WebView mWebView;
    private WebViewHelper mWebViewHelper;

    private JsAndroidHelper mJsAndroidHelper;

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
        //设置可让界面弹出alert等提示框
        mWebView.setWebChromeClient(new WebChromeClient());
        //设置网页跳转时在webview中显示,而不是在手机自带浏览器上打开
        mWebView.setWebViewClient(new WebViewClient());

        mJsAndroidHelper=new JsAndroidHelper();
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {
                LogUtil.i("====我是MainActivity接收数据啊===tag=" + tag + "    result=" + result);
                if("tag1".equals(tag)){
                    return "你很牛啊";
                }
                return null;
            }
        });

        //加载网页
        mWebViewHelper.loadAssetsFile(mWebView,"test.html");
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
        mWebViewHelper.destoryWebViewConfig(mWebView, MainActivity.this);
        super.onDestroy();
    }
}
```
以上涉及到的`WebViewHelper`不过是封装了`webView`在使用过程中的一些基本设置，大家可以按照自己在使用`webView`需要做的设置写就好了，没必要太关注此类。
然后`WebView`涉及的`test.html`加载，下面给出`test.html`代码：
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

<p><input type="button" id="enter0" value="alert测试" onclick="alert('大家啊')" /></p>

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="test1()"/></p>


<p id="show"></p>
</body>

<script type="text/javascript">
    function test1(){
      var temp=window.hello.jsCallAndroid('tag1','我是js调用android的结果');

      document.getElementById("show").innerHTML= 'tag1'+"    "+temp;
    }

    function test2(tag,flag){
        if(flag){
            document.getElementById("show").innerHTML= tag+"  大师傅似的"
        }else{
            document.getElementById("show").innerHTML= tag+"  的身份"
        }
        //android调用"test2"方法后，js给Android提供的返回数据
        window.hello.jsCallAndroid(tag,'我是android调用js后js返回的数据');
    }
</script>
</html>
```
#### 六. android 与 js 交互需要注意的点
以上我们着重讲了封装类`JsAndroidHelper`的一些基本使用，但是我们还是要清楚`android`与`js`做交互的时候需要注意的点。
##### 6.1 涉及到`android`与`js`交互的类要添加注解
如我们要在`MainActivity`中进行`android`与`js`交互，那么我们的`MainActiivty`中要做以下注解：
```
@SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
public class MainActivity extends AppCompatActivity {
     //以下逻辑省略
     //......
}
```
##### 6.2 MainActivity 中 WeView 要做与 js 交互的基本设置
`android`与`js`交互其实主要靠`WebView`，那么`Webview`中只至少需要做以下设置：
```
WebSettings webviewSettings = webView.getSettings();
//设置WebView允许执行JavaScript脚本
 webviewSettings.setJavaScriptEnabled(true);
//设置开启DOM存储API权限,WebView能够使用DOM storage API
webviewSettings.setDomStorageEnabled(true);

//webView注册供js调用,hello为Android与js交互的标志，可以自行设置字符串，但两端要保持一致
webView.addJavascriptInterface(this,"hello");
```
##### 6.3 Android 中供 js 调用的方法要添加注解
假如`anroid`中由一个`testAndroid()`的方法供`js`调用，那么在`andorid`中此方法要设置为 **public**，并且要添加如下注解：
```
    @JavascriptInterface
    public void testAndroid() {
      //以下逻辑省略
      //......
    }
```
**当然，在使用`JsAndroidHelper`的时候，以上都不需要关注，只要按我以上讲解的使用就行,因为都已经封装到`JsAndroidHelper`中了。**
那么接下来我们还是有几点需要注意。
##### 6.4 android 与 js 交互涉及的代码混淆问题
若你项目中加入了代码混淆，那么在你`proguard-rules.pro`文件中，需要加入以下代码：
```
-keepattributes *JavascriptInterface*

-keep class com.otherdemo.JsAndroidHelper
-keep class com.otherdemo.JsAndroidHelper {public *;}
```
注意,`JsAndroidHelper`以你实际项目路径为准，如果不加此代码，会导致你混淆后出现`js`调用`android`方法无反应。
##### 6.5 android 与 js 交互设置标志问题
通过之前的讲解，我们知道在`android`与`js`交互的时候，会涉及到设置一个标志的问题。在一般的设置中我们是这样的：
```
webView.addJavascriptInterface(this,"hello");
```
在使用`JsAndroidHelper`时,我们的设置是这样的：
```
        mJsAndroidHelper.init(mWebView, "hello", new JsAndroidHelper.OnFunctionListener() {
            @Override
            public String getResult(String tag, String result) {

                return null;
            }
        });
```
以上俩中代码中的`hello`皆为`android`与`js`交互时需要设置的标志，一般来讲，这个标志只要符合`"唯一性"，两端(andorid与js)保持一致性`即可，但是在实测中发现此标志不能设置为`android`，`Android`等字符串，不然会莫名的导致`js`调用`android`方法无反应，所以在设置标志的时候，要避开此类字符串。
#### 七.更多
详细样例代码可参考`com.webproj.JsBridgeActivity`类。  
更多详细使用介绍可参考[Android与js交互详解](https://www.jianshu.com/p/416aefea77a4)

