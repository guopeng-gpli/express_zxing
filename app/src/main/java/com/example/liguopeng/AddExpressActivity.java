package com.example.liguopeng;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class AddExpressActivity extends AppCompatActivity {
    private EditText exp_num;
    private EditText exp_send_tel;
    private EditText exp_send_name;
    private EditText exp_rec_tel;
    private EditText exp_rec_name;
    private EditText exp_add;
    private Button btn_add;
    private String num,send_tel,send_name,rec_tel,rec_name,address;
    private String self_tel;
    private MyHandler handler;
    final  String baseURL="http://192.168.43.251/dashboard/sql/insert_expressinfo.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_express_information);
        self_tel=getIntent().getStringExtra("UserloginActivity");
        initview();
        handler=new MyHandler();
    }

    private void initview(){
        final TextInputLayout inputLayout1= (TextInputLayout) findViewById(R.id.express_id);
        //  inputLayout.setHint("请输入姓名");

        EditText text=inputLayout1.getEditText();
        inputLayout1.setErrorEnabled(true);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=8){
                    inputLayout1.setError("快递单号应为8位");
                }else{
                    inputLayout1.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final TextInputLayout inputLayout= (TextInputLayout) findViewById(R.id.input_rec_phone);
        //  inputLayout.setHint("请输入姓名");

        EditText text1=inputLayout.getEditText();
        inputLayout.setErrorEnabled(true);
        text1.addTextChangedListener(new TextWatcher() {
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





         exp_num=(EditText)findViewById(R.id.cae_edit_express_num);
       // exp_send_tel=(EditText)findViewById(R.id.cae_edit_sender_tel);
      //  exp_send_tel.setText(self_tel);
       // exp_send_name=(EditText)findViewById(R.id.cae_edit_sender_name);
        exp_rec_tel=(EditText)findViewById(R.id.cae_edit_rec_tel);
       exp_rec_name=(EditText)findViewById(R.id.cae_edit_rec_name);
         exp_add=(EditText)findViewById(R.id.cae_edit_rec_add);
       btn_add=(Button)findViewById(R.id.btn_add_express_sure);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=exp_num.getText().toString();
//                send_tel=exp_send_tel.getText().toString();
          //      send_name=exp_send_name.getText().toString();
                rec_tel=exp_rec_tel.getText().toString();
                rec_name=exp_rec_name.getText().toString();
                address=exp_add.getText().toString();
             //   add(num,send_tel,send_name,rec_tel,rec_name,address);
                new GetThread().start();//用get方法发送
            }
        });
    }

    class GetThread extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = baseURL+"?id="+num+"&send_tel="+self_tel+"&rec_tel="+rec_tel+"&rec_name="+rec_name+"&address="+address+"&flag=未分配";
            Log.d("addcourier",urlStr);
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
               //     System.out.print("get方法取回内容：" + resultData);
                    Log.d("get方法取回内容：",resultData);
                    if(resultData.equals("insert_successful")){
                        //Toast.makeText(UserRegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Log.d("r","相等");
                        Looper.prepare();
                        showRes("添加成功");
                    }
                    else{
                        Looper.prepare();
                        showRes("添加失败");
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
            Toast.makeText(AddExpressActivity.this,bundle.get("res").toString(),Toast.LENGTH_SHORT).show();
            Log.d("register","toast");
            Looper.loop();
        }
    }
}
