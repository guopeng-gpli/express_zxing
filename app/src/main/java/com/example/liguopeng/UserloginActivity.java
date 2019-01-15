package com.example.liguopeng;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.sql.BatchUpdateException;

public class UserloginActivity extends AppCompatActivity {
    private Button btn_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        btn_add=(Button)findViewById(R.id.btn_add_express);
        btn_add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserloginActivity.this,AddExpressActivity.class);
                startActivity(i);
            }
        });

    }

}
