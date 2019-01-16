package com.example.liguopeng;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText reg_telText;
    private EditText reg_nameText;
    private EditText reg_pwdText;
    private EditText reg_ped2Text;
    private Button btn_register;
    String tel,name,password,password2;
    MyHandler handler;
    final  String baseURL="http://192.168.43.251/dashboard/sql/insert_userinfo.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initview();

        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针
    }

    private  void initview(){
        reg_telText=(EditText)findViewById(R.id.cae_register_tel);
        reg_nameText=(EditText)findViewById(R.id.cae_register_name);
        reg_pwdText=(EditText)findViewById(R.id.cae_register_password);
        reg_ped2Text=(EditText)findViewById(R.id.cae_register_password2);
        btn_register=(Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel = reg_telText.getText().toString();
               password = reg_pwdText.getText().toString();
                name=reg_nameText.getText().toString();
           password2 = reg_ped2Text.getText().toString();
                if (TextUtils.isEmpty(password) || ! TextUtils.equals(password, password2)) {
                    reg_ped2Text.setError("输入的密码不一致");
                    return;
                }
                register(tel,name,password);

            }
        });
    }


    private void register(final String tel, final String name,final String password) {
        new GetThread().start();//用get方法发送
    }


    class GetThread extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = baseURL+ "?tel="+tel+ "&name="+name+"&pwd=" + password2;
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
                    if(resultData.equals("insert_successful")){
                        //Toast.makeText(UserRegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Log.d("register","相等");
                        Looper.prepare();
                        showRes("注册成功");
                    }
                    else{
                        Looper.prepare();
                        showRes("注册失败, 用户名可能已经存在");
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
            Toast.makeText(UserRegisterActivity.this,bundle.get("res").toString(),Toast.LENGTH_SHORT).show();
            Log.d("register","toast");
            Looper.loop();
        }
    }



}
