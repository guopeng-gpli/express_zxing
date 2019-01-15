package com.example.liguopeng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liguopeng.CAE.Courier;
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
import java.util.ArrayList;
import java.util.List;

public class AdminloginActivity2 extends AppCompatActivity {
    private String express_id;
    private RecyclerView mItemRecyclerView;
    final String baseURL="http://192.168.11.103/dashboard/sql/obtainallcourier.php";
    private MyHandler handler;
    private MyHandler2 handler2;
    private ItemAdapter mAdapter;
    private List<Courier> mItms;
    private final String TAG="AdminloginActivity2";
    private String couriertel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin2);
        mItemRecyclerView=(RecyclerView)findViewById(R.id.choose_courier) ;
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        new GetThread().start();//用get方法发送
        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针
        handler2=new MyHandler2();
        express_id=getIntent().getStringExtra("AdminloginActivity");
        Log.d("获得",express_id);
    }

    class GetThread extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = baseURL;
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
            Bundle bundle = msg.getData();
            String obtain_res = bundle.get("res").toString();
            //   login_res应该是数组
           // Toast.makeText(AdminloginActivity2.this, "登录成功", Toast.LENGTH_SHORT).show();

            //   String strByJson = JsonToStringUtil.getStringByJson(this, obtain_res);

            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(obtain_res).getAsJsonArray();

            Gson gson = new Gson();
            mItms=new ArrayList<>();

            //加强for循环遍历JsonArray
            for (JsonElement user : jsonArray) {
                //使用GSON，直接转成Bean对象
                Courier courier = gson.fromJson(user, Courier.class);
                mItms.add(courier);
            }
            Log.d(TAG,String.valueOf(mItms.size()));
            if(mItms.size()>0)
            updateUI();

        }

    }


    private void updateUI(){
        Log.d(TAG,"updateui");

        mAdapter=new ItemAdapter(mItms);
        mItemRecyclerView.setAdapter(mAdapter);
    }


    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mCourier_name;
        private TextView mCourier_region;
        private TextView mCourier_tel;
        private Courier mCourier;
        public ItemHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mCourier_name=(TextView)itemView.findViewById(R.id.courier_name);
            mCourier_region=(TextView)itemView.findViewById(R.id.courier_region);
            mCourier_tel=(TextView)itemView.findViewById(R.id.courier_tel);
        }

        public void bindItem(Courier courier){
            mCourier=courier;
            mCourier_tel.setText(mCourier.getTel());
            mCourier_name.setText(mCourier.getName());
            mCourier_region.setText(mCourier.getRegion());

        }
        @Override
        public void onClick(View v) {
            couriertel=mCourier.getTel();
            besure(mCourier.getName());

           // Toast.makeText(AdminloginActivity.this,mExpress.getId(),Toast.LENGTH_SHORT).show();
           //应该是弹窗，然后问是否确定，然后确定之后进行网络请求
        }

    }
    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder>{
        private List<Courier> mCouriers;
        public ItemAdapter(List<Courier>couriers){
            mCouriers=couriers;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(AdminloginActivity2.this);
            View view = layoutInflater.inflate(R.layout.list_item_courier_info, parent, false);
            return new ItemHolder(view);
        }
        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Courier courier = mCouriers.get(position);
            holder.bindItem(courier);
        }
        public int getItemCount(){
            Log.d(TAG,String.valueOf(mCouriers.size()));
            return mCouriers.size();

        }


    }

    private void besure(final  String courier_name ){

        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AdminloginActivity2.this);
        normalDialog.setIcon(R.mipmap.cae_icon);
        normalDialog.setTitle("确认分配");
        normalDialog.setMessage("您确定要将"+express_id+"分配给"+courier_name+"嘛？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new GetThread2().start();//用get方法发送
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();



    }

    class GetThread2 extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = "http://192.168.11.103/dashboard/sql/besure.php"+"?couriertel="+couriertel+"&expressid="+express_id;
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
                    Looper.prepare();
                    showRes2(resultData);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showRes2(String res){
        Bundle bundle=new Bundle();
        bundle.putString("res",res);//bundle中也可以放序列化或包裹化的类对象数据

        Message msg=handler2.obtainMessage();//每发送一次都要重新获取
        msg.setData(bundle);
        handler2.sendMessage(msg);//用handler向主线程发送信息
    }
    class MyHandler2 extends Handler {
        @Override
        //接收别的线程的信息并处理
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String obtain_res = bundle.get("res").toString();
            if(obtain_res.equals("insert_successful")){
                Toast.makeText(AdminloginActivity2.this,"分配成功",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            else{
                Toast.makeText(AdminloginActivity2.this,"分配失败",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

        }

    }
}
