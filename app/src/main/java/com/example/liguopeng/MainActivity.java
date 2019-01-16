package com.example.liguopeng;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private Button btnAdmin;
    private Button btnUser;
    private Button btnCourier;
    private TextView CourierText;
    private TextView UserText;
    private EditText mPhone;
    private EditText mPwd;
    private MyHandler handler;
    private String tel;
    private String password;
    private String type;
    final String baseURL="http://192.168.43.251/dashboard/sql/login_judge.php";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针
    }






    private void initView() {

        final TextInputLayout inputLayout= (TextInputLayout) findViewById(R.id.login_phone);
      //  inputLayout.setHint("请输入姓名");

        EditText text=inputLayout.getEditText();
        inputLayout.setErrorEnabled(true);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=11){
                    inputLayout.setError("手机号码应为11位");
                }else{
                    inputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        toolbar=(Toolbar)findViewById(R.id.toolbar);
        btnAdmin=(Button)findViewById(R.id.btn_adimin_login);
        btnUser=(Button)findViewById(R.id.btn_user_login);
        btnCourier=(Button)findViewById(R.id.btn_courier_login);
        CourierText=(TextView)findViewById(R.id.cae_courier_register);
        UserText=(TextView)findViewById(R.id.cae_user_register);
        mPhone=(EditText)findViewById(R.id.phone_for_login) ;
        mPwd=(EditText)findViewById(R.id.pwd_for_login);
        tel=mPhone.getText().toString();
        password=mPwd.getText().toString();
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="admin";
                tel=mPhone.getText().toString();
                password=mPwd.getText().toString();
                if(judge_phone(tel)){
                    new GetThread().start();//用get方法发送
                }

//                Intent i=new Intent(MainActivity.this,AdminloginActivity.class);
//                startActivity(i);
            }
        });
        btnUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                type="user";
                tel=mPhone.getText().toString();
                password=mPwd.getText().toString();
                if(judge_phone(tel)){
                    new GetThread().start();//用get方法发送
                }

            }

        });
        btnCourier.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                type="courier";
                tel=mPhone.getText().toString();
                password=mPwd.getText().toString();
                if(judge_phone(tel)){
                    new GetThread().start();//用get方法发送
                }

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
    class GetThread extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = baseURL+ "?tel="+tel+ "&pwd="+password+"&type=" + type;
            Log.d(TAG,urlStr);
            InputStream is = null;
            String resultData = "";
            try {
                URL url = new URL(urlStr); //URL对象
                conn = (HttpURLConnection) url.openConnection(); //使用URL打开一个链接,下面设置这个连接
                conn.setRequestMethod("GET"); //使用get请求

                if (conn.getResponseCode() == 200) {//返回200表示连接成功
                    is = conn.getInputStream(); //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine = "";
                    while ((inputLine = bufferReader.readLine()) != null) {
                        resultData += inputLine ;
                    }
                    //System.out.print("get方法取回内容：" + resultData);
                    if(resultData.equals("1")){
                        //Toast.makeText(UserRegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                       // Log.d("register","相等");
                       // Looper.prepare();
                        showRes("1");
                    }
                    else{
                        Looper.prepare();
                        showRes("0");
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void showRes(String res){
        Bundle bundle=new Bundle();
        bundle.putString("res",res);//bundle中也可以放序列化或包裹化的类对象数据

        Message msg=handler.obtainMessage();//每发送一次都要重新获取
        msg.setData(bundle);
        handler.sendMessage(msg);//用handler向主线程发送信息
    }
    class MyHandler extends Handler {
        @Override
        //接收别的线程的信息并处理
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            String login_res=bundle.get("res").toString();
            if(login_res.equals("1")){
                if(type.equals("user")){
                    Intent i=new Intent(MainActivity.this,UserloginActivity.class);
                    i.putExtra(TAG,tel);
                    startActivity(i);
                }else if (type.equals("admin")){
                    //admin
                    Intent i=new Intent(MainActivity.this,AdminloginActivity.class);
                    startActivity(i);
                }else if (type.equals("courier")) {
                    //courier

                    Intent i = new Intent(MainActivity.this, CourierloginActivity.class);
                    i.putExtra(TAG,tel);
                    startActivity(i);
                }
                Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            }

            if (login_res.equals("0")) {
                Toast.makeText(MainActivity.this, "登录失败，请检查用户名或密码", Toast.LENGTH_SHORT).show();
            }
            //Log.d("register","toast");
           // Looper.loop();


        }
    }
    public  boolean judge_phone(final String num){
        boolean b=true;
        if (num.length()!=11)
            b=false;

        return  b;


    }


    @Override
    protected void onResume() {
        super.onResume();
        mPhone.setText("");
        mPwd.setText("");
    }
}
