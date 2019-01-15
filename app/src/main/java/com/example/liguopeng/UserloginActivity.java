package com.example.liguopeng;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liguopeng.CAE.Express;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;

public class UserloginActivity extends AppCompatActivity {
    private Button btn_add;
    private String self_tel;
    private RecyclerView mItemRecyclerView;
    private final String TAG="UserloginActivity";
    private MyHandler handler;
    private ItemAdapter mAdapter;
    private List<Express> mItems;
    final String baseURL="http://192.168.11.103/dashboard/sql/obtainforuser.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);

        self_tel=getIntent().getStringExtra("MainActivity");
        Log.d("获得用户",self_tel);

        mItemRecyclerView=(RecyclerView)findViewById(R.id.list_user_item);
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_add=(Button)findViewById(R.id.btn_add_express);
        btn_add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserloginActivity.this,AddExpressActivity.class);
                startActivity(i);
            }
        });

        new GetThread().start();//用get方法发送
        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针


    }


    class GetThread extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = baseURL+"?user="+self_tel;
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

                    showRes(resultData);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void showRes(String res){
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
            Bundle bundle = msg.getData();
            String obtain_res = bundle.get("res").toString();
            //   login_res应该是数组
            //Toast.makeText(UserloginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

            //   String strByJson = JsonToStringUtil.getStringByJson(this, obtain_res);

            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(obtain_res).getAsJsonArray();

            Gson gson = new Gson();
            mItems=new ArrayList<>();

            //加强for循环遍历JsonArray
            for (JsonElement user : jsonArray) {
                //使用GSON，直接转成Bean对象
                Express express = gson.fromJson(user, Express.class);
                mItems.add(express);
            }

            updateUI();

        }

    }
    private void updateUI(){
        Log.d(TAG,"updateui");
        //  ExpressLab expressLab=ExpressLab.get(AdminloginActivity.this);
        //     List<Express> expresses=expressLab.getExpresses();
        mAdapter=new ItemAdapter(mItems);
        mItemRecyclerView.setAdapter(mAdapter);
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mExpress_id;
        private TextView mRec_name;
        private TextView mRec_tel;
        private TextView mAddress;
        private Express mExpress;
        public ItemHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mExpress_id=(TextView)itemView.findViewById(R.id.cae_item_id);
            mRec_name=(TextView)itemView.findViewById(R.id.cae_item_name);
            mRec_tel=(TextView)itemView.findViewById(R.id.cae_item_phone);
            mAddress=(TextView)itemView.findViewById(R.id.cae_item_add);
        }

        public void bindItem(Express express){
            mExpress=express;
            mExpress_id.setText("快递单号："+mExpress.getId());
            mRec_name.setText("收件人："+mExpress.getRec_name());
            mRec_tel.setText(mExpress.getRec_tel());
            mAddress.setText(mExpress.getAddress());
          //  mFlag.setText(mExpress.getFlag());

        }
        @Override
        public void onClick(View v) {

        }

    }
    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder>{
        private List<Express> mExpresses;
        public  ItemAdapter(List<Express> expresses){
            mExpresses=expresses;
        }


        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(UserloginActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_express, parent, false);
            return new ItemHolder(view);
        }
        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Express express = mExpresses.get(position);
            holder.bindItem(express);
        }
        public int getItemCount(){
            Log.d(TAG,String.valueOf(mExpresses.size()));
            return mExpresses.size();

        }


    }

}
