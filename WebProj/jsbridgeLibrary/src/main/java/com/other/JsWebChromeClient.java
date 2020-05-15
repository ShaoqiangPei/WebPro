package com.other;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.other.util.WebLogUtil;

/**
 * Title:兼容拍照、相册等的WebChromeClient
 * description:
 * autor:pei
 * created on 2020/5/14
 */
public class JsWebChromeClient extends WebChromeClient {

    private OpenFileChooserCallBack mOpenFileChooserCallBack;

    public void setOpenFileChooserCallBack(OpenFileChooserCallBack callBack) {
        mOpenFileChooserCallBack = callBack;
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    //For Android 3.0 - 4.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        if (mOpenFileChooserCallBack != null) {
            mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg, acceptType);
        }
    }

    // For Android > 5.0
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (mOpenFileChooserCallBack != null) {
            mOpenFileChooserCallBack.showFileChooserCallBack(filePathCallback, fileChooserParams);
        }
        return true;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        WebLogUtil.i( cm.messageLevel() + "--" + cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
        return true;
    }

    public interface OpenFileChooserCallBack {

        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        void showFileChooserCallBack(ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams);
    }

}
