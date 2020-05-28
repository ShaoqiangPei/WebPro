# WebPro
## 简介
`WebPro`是一个用于`Android`与`js`进行交互的库，主要功能包括`Android`调用`js`和`js`调用`Android`。

## 使用说明
### 一. 库依赖
在你project对应的buid.gradle中添加如下代码：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
在你要使用的module对应的buid.gradle中添加如下代码(以0.0.1版本为例)：
```
	dependencies {
	        implementation 'com.github.ShaoqiangPei:WebPro:0.0.1'
	}
```
在你项目的自定义Application类中对本库Log打印做控制：
```
   //开启log打印
   WebLogUtil.setDebug(true);
```
### 二.主要功能类
util.StringUtil ————  字符串处理工具类  
util.WebLogUtil ————  打印工具类  
JsWebChromeClient ————  自定义WebChromeClient,主要用于处理拍照、相册等兼容问题  
[WebViewHelper](https://github.com/ShaoqiangPei/WebPro/blob/master/read/WebViewHelper%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ————  webview帮助类   
[WebViewClientInterceptor](https://github.com/ShaoqiangPei/WebPro/blob/master/read/WebViewClientInterceptor%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ————  用于拦截webView中url的WebViewClient(继承自WebViewClient)  
### 三. 接入流程
具体参考[接入流程说明](https://github.com/ShaoqiangPei/WebPro/blob/master/read/%E6%8E%A5%E5%85%A5%E6%B5%81%E7%A8%8B%E8%AF%B4%E6%98%8E.md)



