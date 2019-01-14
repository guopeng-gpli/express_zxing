package com.example.liguopeng;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;

    private Button btnAdmin;
    private Button btnUser;
    private Button btnCourier;
    private TextView CourierText;
    private TextView UserText;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }






    private void initView() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        btnAdmin=(Button)findViewById(R.id.btn_adimin_login);
        btnUser=(Button)findViewById(R.id.btn_user_login);
        btnCourier=(Button)findViewById(R.id.btn_courier_login);
        CourierText=(TextView)findViewById(R.id.cae_courier_register);
        UserText=(TextView)findViewById(R.id.cae_user_register);

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,AdminloginActivity.class);
                startActivity(i);
            }
        });
        btnUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(MainActivity.this,UserloginActivity.class);
                startActivity(i1);
            }

        });
        btnCourier.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i2=new Intent(MainActivity.this,CourierloginActivity.class);
                startActivity(i2);
            }

        });

        CourierText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i3=new Intent(MainActivity.this,CourierRegisterActivity.class);
                startActivity(i3);
            }

        });
        UserText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i4=new Intent(MainActivity.this,UserRegisterActivity.class);
                startActivity(i4);
            }

        });
    }


}
