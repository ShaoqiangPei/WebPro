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
注:本库完整引用接入请参考
