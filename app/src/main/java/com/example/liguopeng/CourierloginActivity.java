package com.example.liguopeng;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.liguopeng.zxing.activity.CaptureActivity;

public class CourierloginActivity extends AppCompatActivity {
    private Button btn;
    private String self_tel;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courierlogin);
        self_tel=getIntent().getStringExtra("MainActivity");
        Log.d("获得",self_tel);
        btn=(Button)findViewById(R.id.btn_courier_scan);
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("快递员"+self_tel+"扫码页面");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(CourierloginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CourierloginActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    Intent intent = new Intent(CourierloginActivity.this, CaptureActivity.class);
                    intent.putExtra("CourierloginActivity",self_tel);
                    startActivity(intent);
                }

            }
        });
    }
}
