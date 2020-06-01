package com.webproj;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.other.util.WebLogUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnJsBridge;
    private Button mBtnJs;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //打开log
        WebLogUtil.setDebug(true);

        initView();
        initData();
        setListener();
    }

    private void initView() {
        mBtnJsBridge=findViewById(R.id.btn_jsbridge);
        mBtnJs=findViewById(R.id.btn_js);
    }

    private void initData() {
        //打开log
        WebLogUtil.setDebug(true);
    }

    private void setListener() {
        mBtnJsBridge.setOnClickListener(this);
        mBtnJs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.btn_jsbridge://jsBridge与Android交互
               Intent jsBridgeIntent=new Intent(MainActivity.this,JsBridgeActivity.class);
               startActivity(jsBridgeIntent);
               break;
           case R.id.btn_js://js与Android交互
               Intent jsIntent=new Intent(MainActivity.this,JsActivity.class);
               startActivity(jsIntent);
               break;
           default:
               break;
       }
    }

}
