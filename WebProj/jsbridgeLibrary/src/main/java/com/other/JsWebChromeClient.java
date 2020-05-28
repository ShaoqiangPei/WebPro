package com.other;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.other.util.WebLogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Title:兼容拍照、相册等的WebChromeClient
 * description:
 * autor:pei
 * created on 2020/5/14
 */
public class JsWebChromeClient extends WebChromeClient {

    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1;//选择照片
    public static final int REQUEST_CODE_TAKE_PHOTO = 2;//拍照
    public static final int REQUEST_CODE_CROP = 3;//裁剪

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private OnCameraPermissionListener mOnCameraPermissionListener;

    /**设置相机触发的监听,一般在这里面做拍照,存储等权限申请**/
    public void setOnCameraPermissionListener(OnCameraPermissionListener listener){
        this.mOnCameraPermissionListener=listener;
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    //For Android 3.0 - 4.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooserCallBack(uploadMsg, acceptType);
    }

    // For Android > 5.0
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        showFileChooserCallBack(filePathCallback, fileChooserParams);
        return true;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        WebLogUtil.i( cm.messageLevel() + "--" + cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
        return true;
    }

    private void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType){
        this.mUploadMessage=uploadMsg;
        if(mOnCameraPermissionListener!=null){
            mOnCameraPermissionListener.permission();
        }
    }

    private void showFileChooserCallBack(ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams){
        this.mFilePathCallback=filePathCallback;
        if(mOnCameraPermissionListener!=null){
            mOnCameraPermissionListener.permission();
        }
    }

    public interface OnCameraPermissionListener{

        void permission();
    }

    /**默认拍照(作参考)**/
    public File defaultTake(AppCompatActivity activity) {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        // Create the storage directory if it does not exist
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        activity.startActivityForResult(chooserIntent, JsWebChromeClient.REQUEST_CODE_TAKE_PHOTO);
        return file;
    }

    /**相册(选择照片)**/
    public void defaultChoosePhoto(AppCompatActivity activity){
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(albumIntent, REQUEST_CODE_CHOOSE_PHOTO);
    }

    /**取消(取消拍照或选择照片的时候调用处理,若不做处理会导致第二次点击webview中调用拍照的照片的时候无响应)**/
    public void cancel(){
        if(mFilePathCallback!=null) {
            mFilePathCallback.onReceiveValue(null);
        }
    }

    /**拍照，相册返回的处理**/
    public void onActivityResult(int requestCode, int resultCode, Intent data, File tempImageFile, AppCompatActivity activity){
        if (resultCode == Activity.RESULT_OK) {
            if(tempImageFile==null){
                WebLogUtil.i("======tempImageFile=照片文件不能为null=======");
                return;
            }
            Uri imageUri=null;
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                imageUri = Uri.fromFile(tempImageFile);
            } else if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = activity.getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    imageUri=Uri.fromFile(new File(picturePath));
                } else {
                    WebLogUtil.i("======不可用========");
                }
            } else if (requestCode == REQUEST_CODE_CROP) {

            }
            if(null!=imageUri){
                if(mFilePathCallback!=null){
                    doFilePathCallback(data,imageUri);
                }else if(mUploadMessage!=null){
                    if(data==null){
                        mUploadMessage.onReceiveValue(imageUri);
                        mUploadMessage = null;
                    }else {
                        mUploadMessage.onReceiveValue(data.getData());
                        mUploadMessage = null;
                    }
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            cancel();
        }
    }

    private void doFilePathCallback(Intent data, Uri imageUri){
        Uri[] results = null;
        if (data == null) {
            results = new Uri[]{imageUri};
        } else {
            String dataString = data.getDataString();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            if (dataString != null)
                results = new Uri[]{Uri.parse(dataString)};
        }
        if(results!=null){
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }else{
            results = new Uri[]{imageUri};
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
    }

}
