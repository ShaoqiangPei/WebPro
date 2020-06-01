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
[JsWebChromeClient](https://github.com/ShaoqiangPei/WebPro/blob/master/read/JsWebChromeClient%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ————  自定义WebChromeClient,主要用于处理拍照、相册等兼容问题  
[WebViewHelper](https://github.com/ShaoqiangPei/WebPro/blob/master/read/WebViewHelper%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ————  webview帮助类   
[WebViewClientInterceptor](https://github.com/ShaoqiangPei/WebPro/blob/master/read/WebViewClientInterceptor%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ————  用于拦截webView中url的WebViewClient(继承自WebViewClient)  
### 三. 接入流程
#### 3.1 Android 与 js 交互方式介绍
目前主要有两种方式实现`android`与`js`交互。  
- `js`与`android`交互
- `jsBridge`实现`js`与`android`交互  

**`js`与`android`交互** 是原生的`android`与`js`交互  
**`jsBridge`实现`js`与`android`交互** 是一个封装的库利用`jsBridge`实现的`android`与`js`交互  
`jsBridge`实现方式更安全，但是使用体积较大，接入相对复杂。`js`接入方式相对简洁，但是安全性上稍差。当然，这个会涉及到`android`与`html`的交互问题，那么在接入选择上我们需要做一下考虑：  
1. 若项目较大，考虑安全性较高的情况下，推荐使用`jsBridge`接入方式
2  若项目不是很大，安全性要求不高，并且想快速接入，可选择`js`接入方式
3. 看与`android`对接的`web`端使用情况，若`web`端使用的是`jsBridge`,则`android`使用`jsBridge`接入，若`web`端使用的是`js`方式,则`android`也使用`js`接入,这样可以加快对接进程。
#### 3.2 具体两种方式的接入流程
- `jsBridge`接入流程可参考[`jsBridge`接入流程说明](https://github.com/ShaoqiangPei/WebPro/blob/master/read/%E6%8E%A5%E5%85%A5%E6%B5%81%E7%A8%8B%E8%AF%B4%E6%98%8E.md)  
- `js`接入流程可参考[`js`接入流程说明]()



