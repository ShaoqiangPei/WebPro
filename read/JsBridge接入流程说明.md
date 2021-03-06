## JsBridge接入流程说明

### 一. 库依赖
在使用本库时，先要添加本库依赖，这里就不多做解释了。以下讲解均建立在已经添加了本库依赖的基础之上。
### 二. 在布局中引入控件
需要在布局中引入`BridgeWebView`控件，以`MainActivity`的布局`activity_main.xml`为例，具体如下：
```
    <com.github.lzyzsd.jsbridge.BridgeWebView
        android:id="@+id/web_view"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="30dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn"
        app:layout_constraintBottom_toBottomOf="parent"/>
```
### 三. 在MainActivity中使用示例
先贴出本库在`MainActivity`中使用示例代码：
```
package com.otherdemo;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.other.JsWebChromeClient;
import com.other.WebViewHelper;
import com.other.util.WebLogUtil;

public class JsBridgeActivity extends AppCompatActivity {

    private TextView mTv;
    private Button mBtn;
    private BridgeWebView mWebView;

    private WebViewHelper mWebViewHelper;
    private JsWebChromeClient mJsWebChromeClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //打开log
        WebLogUtil.setDebug(true);

        initView();
        initData();
        setListener();
    }


    private void initView() {
        mTv=findViewById(R.id.tv);
        mBtn=findViewById(R.id.btn);
        mWebView=findViewById(R.id.web_view);
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
        //webView设置背景色
        mWebViewHelper.setBackgroundColor(mWebView,Color.BLUE);
        //设置WebChromeClient,用以支持webview显示对话框,网站图标,网站title,加载进度等等
        mWebView.setWebChromeClient(mJsWebChromeClient);

        //注:不能设置"mWebView.setWebViewClient(new WebViewClient())",否则辉导致Android与js交互不通
        //        //设置WebViewClient向一个网页发送请求，可以返回文本，文件等
        //        mWebView.setWebViewClient(new WebViewClient());

        //加载本地html文件
        mWebViewHelper.loadAssetsFile(mWebView,"test.html");
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

        mBtn.setOnClickListener(new View.OnClickListener() {
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

                mTv.setText("=======调用js返回数据====data=" + data);
            }
        });
    }
}
```
#### 四. JsBridge与Android交互详细介绍
通过`MainActivty`的代码，我们只能有个模糊的交互使用概念。那么，下面让我们对`Android`与`Js`交互，做一个详细的梳理。
`Android`与`Js`交互主要分为以下两个板块：
- Android 调用 js
- js 调用 Android

那么接下来就详细讲解这些调用的使用。
##### 4.1 Android调用js初始化方法(无返回值)
在`Android`中可以调用`js`初始化时的方法，此方法主要用于`Android`向`js`传值，具体在`Android`中调用方法传值给`js`如下：
```
    mBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Android调用js默认方法,不接受js返回值(注:mWebView为BridgeWebView实例对象)
            mWebView.send("=====到底啥玩意=====");
        }
    });
```
##### 4.2 Android调用js初始化方法(有返回值)
`android`传值给`js`后还想收到`js`回复咋整，则Android中调用代码如下：
```
    mBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Android调用js默认方法,接受js返回值(注:mWebView为BridgeWebView实例对象)
            mWebView.send("=====到底啥玩意=====", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                WebLogUtil.i("======返回结果啊====="+data);

                mTv.setText(data);
            }
        });
        }
    });
```
以上两个方法都是`Android`调用`js`中的初始化方法，那么`js`要咋写？下面给出`js`范例：
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

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="testClick()" /></p>
<p><input type="button" id="enter1" value="js调用Android(调用自定义tag的方法)" onclick="testClick1()" /></p>

<p id="show"></p>
</body>

<script type="text/javascript">
        //初始化jsBridge库
        function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge)
            } else {
                document.addEventListener(
                    'WebViewJavascriptBridgeReady'
                    , function() {
                        callback(WebViewJavascriptBridge)
                    },
                    false
                );
            }
        }

       /*初始化执行    webView.send("hello1111111111"); 无回调函数
                      webView.send("zq", new CallBackFunction()  有回调函数
       */
        connectWebViewJavascriptBridge(function(bridge) {
            bridge.init(function(message, responseCallback) {
                //初始化时Android调用js,js接受Android传过来的值
                document.getElementById("show").innerHTML = "data = " + message;

                var data="初始化Android调用js时，js返回给Android的数据";
                responseCallback(data);
            });
        })
  </script>
</html>
```
这里，`Html`代码不是主要内容，主要看看`js`，即`<script type="text/javascript">...</script> `模块代码。
首先，因为是`JsBridge`库达到`Android`与`js`交互，所以在`js`代码中也要有关于`JsBridge`库的设置，则js中必须写的代码有:
```
        //初始化jsBridge库
        function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge)
            } else {
                document.addEventListener(
                    'WebViewJavascriptBridgeReady'
                    , function() {
                        callback(WebViewJavascriptBridge)
                    },
                    false
                );
            }
        }

       /*初始化执行    webView.send("hello1111111111"); 无回调函数
                      webView.send("zq", new CallBackFunction()  有回调函数
       */
        connectWebViewJavascriptBridge(function(bridge) {
            bridge.init(function(message, responseCallback) {
                //初始化时Android调用js,js接受Android传过来的值
                document.getElementById("show").innerHTML = "data = " + message;

                var data="初始化Android调用js时，js返回给Android的数据";
                responseCallback(data);
            });
        })
```
`connectWebViewJavascriptBridge(callback)`方法定义`JsBridge`库的初始化，然后js初始化时调用`connectWebViewJavascriptBridge(function(bridge) `,
在`bridge.init(function(message, responseCallback) `方法中接收`Android`传过来的值，
然后通过` responseCallback(data);`对`Android`调用`Js`初始化方法做出应答。
##### 4.3 Android调用js带tag方法
之前讲过的两个`Android`调用`js`的`send`方法，并不能解决所有问题，原因有两个：
- send 方法只能调用 js 中初始化时的方法
- send 方法的调用无法对不同功能的实现做区分

因此，我们需要有带`Tag`的方法可使用，此`Tag`主要用于区分不同的方法，用以达到实现不同的功能。
在`Android`中调用一个`Js`中带`tag`的方法，`Android`中代码如下：
```
    /**Android调用js带tag方法,接受js返回值(注:webView为BridgeWebView对象)**/
    private void getTagFunction(){
        mWebView.callHandler("functionJs", "我是王者荣耀", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                WebLogUtil.i("=======调用js返回数据====data=" + data);

                mTv.setText("=======调用js返回数据====data=" + data);
            }
        });
    }
```
这里需要注意的是`callHandler(String handlerName, String data, CallBackFunction callBack)`方法中
- `handlerName` : 为方法`Tag`,需要与`js`中注册的方法`tag`一致
-  `data`:  android 给 js 传参数据
-  `callBack`: 为 Android调用js后返回数据监听

然后在`Js`中需要注册`Android`中调用的带`Tag`的方法。接着看之前的 `Html`中的`js`部分代码，我们需要在`js`中初始 `Jsbridge`库的方法体中注册带`Tag`的方法供`Android`调用，具体编写代码的位置如下：
```
       /*初始化执行    webView.send("hello1111111111"); 无回调函数
                      webView.send("zq", new CallBackFunction()  有回调函数
       */
        connectWebViewJavascriptBridge(function(bridge) {
            //初始化
            bridge.init(function(message, responseCallback) {
                //初始化时Android调用js,js接受Android传过来的值
                document.getElementById("show").innerHTML = "data = " + message;

                var data="初始化Android调用js时，js返回给Android的数据";
                responseCallback(data);
            });

            //此处注册带tag的方法供Android调用
            //......
        })
```
则之前`Android`要调用的带`tag`的方法，在`js`中编写如下：
```
<script type="text/javascript">
        //初始化jsBridge库
        function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge)
            } else {
                document.addEventListener(
                    'WebViewJavascriptBridgeReady'
                    , function() {
                        callback(WebViewJavascriptBridge)
                    },
                    false
                );
            }
        }

       /*初始化执行    webView.send("hello1111111111"); 无回调函数
                      webView.send("zq", new CallBackFunction()  有回调函数
       */
        connectWebViewJavascriptBridge(function(bridge) {
            bridge.init(function(message, responseCallback) {
                //初始化时Android调用js,js接受Android传过来的值
                document.getElementById("show").innerHTML = "data = " + message;

                var data="初始化Android调用js时，js返回给Android的数据";
                responseCallback(data);
            });

            //android调用js
            bridge.registerHandler("functionJs", function(data, responseCallback) {
                //展示Android传过来的参数
                document.getElementById("show").innerHTML = ("Android端: = " + data);

                //回传数据给Android
                var responseData = "Javascript 数据";
                responseCallback(responseData);
            });
        })
  </script>
```
这里需要注意的是` bridge.registerHandler("functionJs", function(data, responseCallback)`方法中，`"functionJs"`即为`js`方法的`tag`,需要和`android`中`callHandler(String handlerName, String data, CallBackFunction callBack)`方法中的`handlerName`保持一致。然后此方法中的`data`即为`Android`方法中传过来的参数，`responseCallback`是用来给`Android`方法提供返回值的。

ok,接着让我们来了解下`js`调用`Android`的方法。
#### 4.4 Js调用Android方法(默认方法)
我们需要在`html`中的按钮中模拟`js`调用`Android`的方法，下面贴出`html`代码：
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

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="testClick()" /></p>

<p id="show"></p>
</body>

<script type="text/javascript">
        //初始化jsBridge库
        function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge)
            } else {
                document.addEventListener(
                    'WebViewJavascriptBridgeReady'
                    , function() {
                        callback(WebViewJavascriptBridge)
                    },
                    false
                );
            }
        }

       /*初始化执行    webView.send("hello1111111111"); 无回调函数
                      webView.send("zq", new CallBackFunction()  有回调函数
       */
        connectWebViewJavascriptBridge(function(bridge) {
            bridge.init(function(message, responseCallback) {
                //初始化时Android调用js,js接受Android传过来的值
                document.getElementById("show").innerHTML = "data = " + message;

                var data="初始化Android调用js时，js返回给Android的数据";
                responseCallback(data);
            });
        })

        /**js调用Android(调用默认方法)**/
        function testClick() {
            //send message to native
            var data = "我是webview给Android的数据";

            window.WebViewJavascriptBridge.send(
                data
                , function(responseData) {
                   //alert(responseData);
                   document.getElementById("show").innerHTML = "data = " + responseData;
                }
            );
        }
  </script>
</html>
```
大家可以看到`testClick() `方法是处在`script`代码中第一层级的，由于使用到`Jsbridge`库，则初始化的两个方法不能少，`js`调用`android`默认方法)`testClick()`与两个初始化相关方法并列同级。然后`testClick()`中包裹的` window.WebViewJavascriptBridge.send`携带有两个参数：
- data ：为`js`调用`android`方法时，给`android`方法传参
- function(responseData)中的  responseData： 为接收`android`端方法返回值

在`Android`上，需要注册默认方法，供js调用，则在`Anroid`上注册该方法代码如下：
```
    private void setListener(){
        mButton1.setOnClickListener(this);

        //注册默认方法供js调用
        webView.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogUtil.i("====收到web数据====" + data);

                //发送数据给js
                function.onCallBack("====Android给js数据回传===");
            }
        });
    }
```
`handler(String data, CallBackFunction function)`中的`data`为`js`调用`Android`方法的传参，而`function`用于`Android`端给`js`端提供返回值。
#### 4.5 Js调用Android方法(带Tag标志)
与`Android`可以调用带`tag`的`js`方法一样，`js`也可以调用`android`带`tag`的方法。
在`html`中写一个调用`android`带`tag`的方法代码如下：
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

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="testClick()" /></p>
<p><input type="button" id="enter1" value="js调用Android(调用自定义tag的方法)" onclick="testClick1()" /></p>

<p id="show"></p>
</body>

<script type="text/javascript">
        //初始化jsBridge库
        function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge)
            } else {
                document.addEventListener(
                    'WebViewJavascriptBridgeReady'
                    , function() {
                        callback(WebViewJavascriptBridge)
                    },
                    false
                );
            }
        }

       /*初始化执行    webView.send("hello1111111111"); 无回调函数
                      webView.send("zq", new CallBackFunction()  有回调函数
       */
        connectWebViewJavascriptBridge(function(bridge) {
            bridge.init(function(message, responseCallback) {
                //初始化时Android调用js,js接受Android传过来的值
                document.getElementById("show").innerHTML = "data = " + message;

                var data="初始化Android调用js时，js返回给Android的数据";
                responseCallback(data);
            });

        })

        /**js调用Android(调用自定义tag的方法)**/
        function testClick1() {

            //参数一：调用java中的方法   submitFromWeb是方法名，必须和Android中注册时候的方法名称保持一致
            //参数二：返回给Android端的数据，可以为字符串，json等信息
            //参数三：js接收到Android传递过来的数据之后的相应处理逻辑
            window.WebViewJavascriptBridge.callHandler(
                    'submitFromWeb'
                    ,'我是自定义的js方法，可调用Android'  //该类型是任意类型
                , function(responseData) {
                document.getElementById("show").innerHTML = "得到Java传过来的数据 data = " + responseData
            }
            );
        }
  </script>
</html>
```
如上代码` testClick1()`在`js`代码中是和`Jsbridge`库初始化的几个方法同一层级并列的，然后` testClick1()`中涉及到的三个参数`window.WebViewJavascriptBridge.callHandler(
parameter1，paramete2，function(responseData) {});`
- parameter1: `js`调用`android`方法的`tag`，需要与`android`中的`tag`保持一致
- paramete2: `js`给`android`的传参
- responseData： 接收`android`方法的返回值

相应的在`Android`中需要注册带`tag`的方法，在`android`中注册方法代码如下：
```
    private void setListener(){
        mButton1.setOnClickListener(this);

        //注册带tag的方法供js调用
        webView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogUtil.i("得到JS传过来的数据 data ="+data);
                //发送消息给js
                function.onCallBack("传递数据给JS");
            }
        });
    }
```
这里`registerHandler(String handlerName, BridgeHandler handler)`方法中第一个参数`handlerName`为注册`tag`方法的`tag`,需要与`js`中调用它的方法的`tag`保持一致，然后该方法内部的`handler(String data, CallBackFunction function)`中`data`为`js`调用`android`传过来的参数，`function`为`android`给`js`方法返回值所用到的对象。
至此，`android`与`js`交互细节讲解完毕。
#### 五.JsBridge与Android交互注意的问题
我们需要注意的是:
1. 当`js`调用`android`方法时，需要在`android`中写好注册方法供`js`调用。当`android`调用`js`方法时，需要在`js`中写好注册方法供`android`调用。
2. `js`中必须写两个关于初始化`jsBridge`库的方法，`js`调用`android`方法时，`js`方法的书写要与初始化方法同层级。`js`注册方法供`android`调用时，`js`方法要写到`jsbridge`库初始化之内
3. 无论怎么调用，`js`和`android`中涉及`tag`方法调用时，两边的`tag`都要一 一对应
4. `android`中，给`BridgeWebView`控件做基本设置时，不可添加以下代码：
```
        //注:不能设置"mWebView.setWebViewClient(new WebViewClient())",否则辉导致Android与js交互不通
        //        //设置WebViewClient向一个网页发送请求，可以返回文本，文件等
        //        mWebView.setWebViewClient(new WebViewClient());
```
不然会导致`Android`与`js`交互不通。
#### 六.界面退出时释放webview设置
在`WebView`界面退出时，别忘了在`obdesrory()`方法中释放`WebView`的一些基本设置:
```
    @Override
    protected void onDestroy() {
        //清理webview相关配置
        mWebViewHelper.destoryWebViewConfig(mWebView,MainActivity.this);
        super.onDestroy();
    }
```
#### 七. test.html书写样例
`Jsbridge`实现`Android`与`js`交互过程中，`js`代码书写一定要写对位置，也要写规范，若`js`写得有问题，及时`Android`写`ok`了,也会导致`android`和`js`交互不通的情况。
下面贴出上文中加载的`test.html`代码，供大家参考：
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

<p><input type="button" id="enter" value="js调用Android(调用默认方法)" onclick="testClick()" /></p>
<p><input type="button" id="enter1" value="js调用Android(调用自定义tag的方法)" onclick="testClick1()" /></p>

<p id="show"></p>
</body>

<script type="text/javascript">
        //初始化jsBridge库
        function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge)
            } else {
                document.addEventListener(
                    'WebViewJavascriptBridgeReady'
                    , function() {
                        callback(WebViewJavascriptBridge)
                    },
                    false
                );
            }
        }

       /*初始化执行    webView.send("hello1111111111"); 无回调函数
                      webView.send("zq", new CallBackFunction()  有回调函数
       */
        connectWebViewJavascriptBridge(function(bridge) {
            bridge.init(function(message, responseCallback) {
                //初始化时Android调用js,js接受Android传过来的值
                document.getElementById("show").innerHTML = "data = " + message;

                var data="初始化Android调用js时，js返回给Android的数据";
                responseCallback(data);
            });

            //android调用js
            bridge.registerHandler("functionJs", function(data, responseCallback) {
                //展示Android传过来的参数
                document.getElementById("show").innerHTML = ("Android端: = " + data);

                //回传数据给Android
                var responseData = "Javascript 数据";
                responseCallback(responseData);
            });
        })

        /**js调用Android(调用默认方法)**/
        function testClick() {
            //send message to native
            var data = "我是webview给Android的数据";

            window.WebViewJavascriptBridge.send(
                data
                , function(responseData) {
                   //alert(responseData);
                   document.getElementById("show").innerHTML = "data = " + responseData;
                }
            );
        }

        /**js调用Android(调用自定义tag的方法)**/
        function testClick1() {

            //参数一：调用java中的方法   submitFromWeb是方法名，必须和Android中注册时候的方法名称保持一致
            //参数二：返回给Android端的数据，可以为字符串，json等信息
            //参数三：js接收到Android传递过来的数据之后的相应处理逻辑
            window.WebViewJavascriptBridge.callHandler(
                    'submitFromWeb'
                    ,'我是自定义的js方法，可调用Android'  //该类型是任意类型
                , function(responseData) {
                document.getElementById("show").innerHTML = "得到Java传过来的数据 data = " + responseData
            }
            );
        }
  </script>
</html>
```





