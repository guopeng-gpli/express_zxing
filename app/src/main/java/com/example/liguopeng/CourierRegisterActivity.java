package com.example.liguopeng;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//insert_courierinfo
public class CourierRegisterActivity extends AppCompatActivity {
    private EditText mReg_telText;
    private EditText mReg_nameText;
    private EditText mReg_regText;
    private EditText mReg_pwdText;
    private EditText mReg_ped2Text;
    private Button btn_register;
    private String tel,name,region,password,password2;
    private MyHandler handler;
    final  String baseURL="http://192.168.11.103/dashboard/sql/insert_courierinfo.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_register);
        initview();

        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针
    }

    private  void initview(){
        mReg_telText=(EditText)findViewById(R.id.cae_c_res_tel);
        mReg_nameText=(EditText)findViewById(R.id.cae_c_res_name);
        mReg_regText=(EditText)findViewById(R.id.cae_c_res_region);
        mReg_pwdText=(EditText)findViewById(R.id.cae_c_res_pwd);
        mReg_ped2Text=(EditText)findViewById(R.id.cae_c_res_pwd2);
        btn_register=(Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel = mReg_telText.getText().toString();
                password = mReg_pwdText.getText().toString();
                name=mReg_nameText.getText().toString();
                region=mReg_regText.getText().toString();
                password2 = mReg_ped2Text.getText().toString();
                if (TextUtils.isEmpty(password) || ! TextUtils.equals(password, password2)) {
                    mReg_ped2Text.setError("输入的密码不一致");
                    return;
                }
                register(tel,name,region,password);

            }
        });
    }


    private void register(final String tel, final String name,final String reg,final String password) {
        new GetThread().start();//用get方法发送
    }


    class GetThread extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = baseURL+ "?tel="+tel+ "&name="+name+"&pwd=" + password2+"&region="+region;
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
                    System.out.print("get方法取回内容：" + resultData);
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
            Toast.makeText(CourierRegisterActivity.this,bundle.get("res").toString(),Toast.LENGTH_SHORT).show();
            Log.d("register","toast");
            Looper.loop();
        }
    }


}
