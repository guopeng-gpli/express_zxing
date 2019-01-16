package com.example.liguopeng;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
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


import com.example.liguopeng.CAE.Express;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class AdminloginActivity extends AppCompatActivity {
    private RecyclerView mItemRecyclerView;
    final String baseURL="http://192.168.43.251/dashboard/sql/obtainforadmin.php";

    private final String TAG="AdminloginActivity";
    private MyHandler handler;
    private ItemAdpter mAdapter;
    private List<Express> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);
        mItemRecyclerView=(RecyclerView)findViewById(R.id.list_all_item);
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       new GetThread().start();//用get方法发送
        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针
      //  updateUI();
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
         //   Toast.makeText(AdminloginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

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
        mAdapter=new ItemAdpter(mItems);
        mItemRecyclerView.setAdapter(mAdapter);
    }
    /*
     * 列表部分
     * RecyclerView的任务仅限于回收和定位屏幕上的TextView
     * TextView能够显示数据离不开Adapter子类和ViewHolder子类
     * ViewHolder子类作用：容纳View视图
     * RecyclerView自身不会创建视图，它创建的是ViewHolder，ViewHolder引用这一个个itemView
     * */
    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mExpress_id;
        private TextView mExpress_region;
        private TextView mFlag;
        private Express mExpress;
        public ItemHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mExpress_id=(TextView)itemView.findViewById(R.id.cae_all_express_num);
            mExpress_region=(TextView)itemView.findViewById(R.id.cae_all_region);
            mFlag=(TextView)itemView.findViewById(R.id.cae_all_flag);
        }

        public void bindItem(Express express){
            mExpress=express;
            mExpress_id.setText(mExpress.getId());
            mExpress_region.setText(mExpress.getAddress());


            mFlag.setText(mExpress.getFlag());


        }
        @Override
        public void onClick(View v) {

            //这里不想对已经分配的进行分配，当点击已经分配的，不让再分配，，，，到快递员页面，要显示当前是在对谁进行分配，提示
            if(mExpress.getFlag().equals("未分配")) {
                //  Toast.makeText(AdminloginActivity.this,mExpress.getId(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AdminloginActivity.this, AdminloginActivity2.class);
                i.putExtra(TAG, mExpress.getId());
                startActivity(i);
            }else{
                besure(mExpress.getId());
            }
        }

    }
    /*
     * RecyclerView自身不会创建视图，它创建的是ViewHolder：这个实际任务是Adapter来完成的。
     * adapter是个控制器对象，从模型层获取数据，然后提供给RecyclerView显示
     * adapter负责：创建必要的ViewHolder、绑定ViewHolder至模型层数据
     * RecyclerView需要显示视图对象时，就会去找它的adapter
     * 首先调用adapter的getItemCount()方法，RecyclerView询问数组列表有多少个对象
     *2.RecyclerView调用adapter的createViewHolder方法创建ViewHolder以及ViewHolder要显示的视图。
     *3.RecyclerView会传入ViewHolder及其位置，调用onBindViewHolder方法。adapter会找到目标位置的数据并绑定到ViewHolder的视图上。绑定：使用模型数据填充视图。
     */

    private class ItemAdpter extends RecyclerView.Adapter<ItemHolder>{
        private List<Express> mExpresses;
        public  ItemAdpter(List<Express> expresses){
            mExpresses=expresses;
        }


        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(AdminloginActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_express_all, parent, false);
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


    private void besure(final  String express_id ){

        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AdminloginActivity.this);
        normalDialog.setIcon(R.mipmap.cae_icon);
        normalDialog.setTitle("确认修改分配");
        normalDialog.setMessage("您确定要将更改"+express_id+"的分配情况嘛？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(AdminloginActivity.this, AdminloginActivity2.class);
                        i.putExtra(TAG,express_id);
                        startActivity(i);
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

    @Override
    protected void onResume() {
        super.onResume();
        new GetThread().start();//用get方法发送
    }
}
