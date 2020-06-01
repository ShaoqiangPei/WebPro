## JsWebChromeClient使用说明

### 概述
`JsWebChromeClient`是一个实现`html`利用`WebView`实现调用`相机`的封装工具类。其目的是在 `js + android`模式下，兼容拍照、相册等。

### 使用说明
#### 一. html调用Android手机相机拍照功能原理
`html`调用`Android`手机相机拍照功能,其原理是在`html`中布局触发`拍照`按钮，然后在`android`的`webview`中设置的`WebChromeClient`来触发调用相机的流程，
然后将拍照的结果又通过`onActivityResult(int requestCode, int resultCode, @Nullable Intent data)`设置传值,
最后将此值设置到`WebChromeClient`中供`html`代码调用，以达到将拍照的结果传到`html`中的效果。这里需要注意的是，
`html`并不能直接完成拍照及接收拍照返回值的整个流程，它通过`webview`的`WebChromeClient`只能做一个触发的作用，
具体的`权限申请`和`拍照流程`还是需要`原生Android`来完成。说到底,`html`所谓的"调用拍照"，
不过是借用`webview`来触发拍照流程和接收拍照返回值，具体的拍照实现还是需要`原生Android`实现。
#### 二. html调用相机简单讲解
`html`要调用相机，则在`html`中也要做一个简单的触发操作，即是在`html`的布局中加入特定“控件”，下面简单贴下`test.html`代码：
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

<p><input type="file" accept="image/*"  capture="camera"/></p>

<p><img src="data:image/IMG_1590653292273.jpg"/></p>

<p id="show"></p>
</body>

<script type="text/javascript">

</script>
</html>
```
其中`<p><input type="file" accept="image/*"  capture="camera"/></p>`便是发起`原生Android`中`webview`设置的`WebChromeClient`的触发。
#### 三. JsWebChromeClient 在 MainActivity 中的使用
`JsWebChromeClient`在`MainActivity`中的使用示例如下：
```
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSION_CODE = 1234;

    private TextView mTextView;
    private Button mButton1;
    private WebView mWebView;
    private WebViewHelper mWebViewHelper;
    private JsWebChromeClient mJsWebChromeClient;

    private File mTempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        initView();
        //初始化数据
        initData();
        //设置监听
        setListener();
        //        //申请权限
        //        requestPermission(MainActivity.PERMISSION_CODE);
    }

    private void requestPermission(int requestCode) {
        String permissions[] = {
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionHelper.getInstance().checkPermissions(permissions, requestCode, MainActivity.this);
    }

    @PermissionSuccess(requestCode = MainActivity.PERMISSION_CODE)
    public void requestSuccess() {
        //申请到权限后的处理
        //......

        LogUtil.i("=====权限申请成功======");

        //拍照(一般使用第三方框架,此处仅作演示)
        mTempFile=mJsWebChromeClient.defaultTake(MainActivity.this);
    }

    @PermissionFail(requestCode = MainActivity.PERMISSION_CODE)
    public void requestFail(){
        //未获取到权限的处理
        //......

        LogUtil.i("=====权限申请失败======");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mTextView = findViewById(R.id.tv);
        mButton1 = findViewById(R.id.btn1);
        mWebView = findViewById(R.id.webView);
    }

    private void initData() {
        mWebViewHelper=new WebViewHelper();
        //webView基本设置
        mWebViewHelper.setWebViewConfig(mWebView,MainActivity.this);
        //设置webview背景色
        mWebView.setBackgroundColor(Color.BLUE);

        mJsWebChromeClient=new JsWebChromeClient();
        mJsWebChromeClient.setOnCameraPermissionListener(new JsWebChromeClient.OnCameraPermissionListener() {
            @Override
            public void permission() {
                LogUtil.i("=====响应html点击事件=======");
                //申请权限
                requestPermission(MainActivity.PERMISSION_CODE);
            }
        });
        mWebView.setWebChromeClient(mJsWebChromeClient);
        //webView加载本地html
        mWebViewHelper.loadAssetsFile(mWebView,"test.html");
    }

    private void setListener(){
        mButton1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.btn1://测试
               LogUtil.i("=====测试====");
               test();
               break;
           default:
               break;
       }
    }

    private void test() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //拍照,相册返回的处理
        mJsWebChromeClient.onActivityResult(requestCode,resultCode,data,mTempFile,MainActivity.this);
    }

}
```
需要注意的是,在`mJsWebChromeClient.setOnCameraPermissionListener`的`public void permission()`中先做`拍照权限申请`,
然后在申请成功的处理中做拍照动作，接着在`Activity`的`onActivityResult(int requestCode, int resultCode, @Nullable Intent data) `中
调用` mJsWebChromeClient.onActivityResult(requestCode,resultCode,data,mTempFile,MainActivity.this);`用以处理拍照回来的结果。
`WebChromeClient`将拍照返回的结果传给`html`中的处理，我已经在`JsWebChromeClient`中做封装处理了，大家无需考虑。
**最后需要注意的是：**我在`MainActivty`中调用的拍照不过是个样例，大家可以替换成自己的拍照框架，但是返回`code`按我给定的`code`设置即可。

