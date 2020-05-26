## WebViewHelper使用说明

## 概述
`WebViewHelper`是一个为`Android`与`js`交互时的`WebView`提供一系列帮助方法的类。简单来说，就是为了简洁化`WebView`的使用。
## 使用说明
`WebViewHelper`主要包含以下几个方法：
```
    /**webView基本设置**/
    public void setWebViewConfig(WebView webView, Context context)
    
    /**关闭界面时清理webview配置**/
    public void destoryWebViewConfig(WebView webView,Context context)
    
    /***
     * 设置webview加载时的字节头
     * @param webView
     * @param header 字节头中加的内容
     *
     * 注：此设置加载字节头是 userAgent+header 模式,
     *    若有其他结构组合，可参考本方法写出来。
     */
    public void setUserAgent(WebView webView,String header)
    
    /**WebView加载网址**/
    public boolean loadUrl(WebView webView,String url)
    
    /***
     * 加载 Assets下 html 文件
     * @param webView
     * @param filePath
     * @return
     *
     * 示例：若加载html路径为 assets/test.html,则参数为 “test.html”
     */
    public boolean loadAssetsFile(WebView webView,String filePath)
    
    /**设置webView背景色**/
    public void setBackgroundColor(WebView webView,int color)
```
