package com.example.liguopeng.zxing.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liguopeng.AdminloginActivity2;
import com.example.liguopeng.CourierRegisterActivity;
import com.example.liguopeng.R;
import com.example.liguopeng.zxing.decode.DecodeThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultActivity extends AppCompatActivity {

	private String TAG="ResultActivity";
	private ImageView mResultImage;
	private TextView mResultText;
    private String baseURL="http://192.168.43.251/dashboard/sql/query_flag.php";
    private String CHandID;
    private String id;
    private String self_tel;
    private String en;
    private MyHandler handler;
    private Button mButton;
    private MyHandler2 handler2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Bundle extras = getIntent().getExtras();

		mResultImage = (ImageView) findViewById(R.id.result_image);
		mResultText = (TextView) findViewById(R.id.result_text);
        mButton=(Button)findViewById(R.id.btn_recd);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                besure();
            }
        });
		mResultText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent sendIntent =new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,mResultText.getText());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"您可以选择将快递信息发送给如下应用"));
            }
        });


        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针
        handler2=new MyHandler2();
		if (null != extras) {
			int width = extras.getInt("width");
			int height = extras.getInt("height");
            self_tel=extras.getString("selftel");
            Log.d("获得res",self_tel);
			LayoutParams lps = new LayoutParams(width, height);
			lps.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
			lps.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
			lps.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
			
			mResultImage.setLayoutParams(lps);

			String result = extras.getString("result");
		//	mResultText.setText(result);
			if(result.length()>=8) {
                en = result.substring(13);
                CHandID = result.substring(0, 13);
                id = CHandID.substring(5);
                new GetThread().start();//用get方法发送
            }
			//Log.d(TAG,CHandID);
			//Log.d(TAG,id);
            //Log.d(TAG,en);
			Bitmap barcode = null;
			byte[] compressedBitmap = extras.getByteArray(DecodeThread.BARCODE_BITMAP);
			if (compressedBitmap != null) {
				barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
				// Mutable copy:
				barcode = barcode.copy(Bitmap.Config.RGB_565, true);
			}

			mResultImage.setImageBitmap(barcode);
		}
	}
    class GetThread extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = baseURL+ "?id="+id+"&self_tel="+self_tel+"&en="+en;
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
                   // System.out.print("get方法取回内容：" + resultData);
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
            Bundle bundle=msg.getData();
          //  Toast.makeText(CourierRegisterActivity.this,bundle.get("res").toString(),Toast.LENGTH_SHORT).show();
            String huodede=bundle.get("res").toString();
            String huanhang=huodede.replace('啊','\n');//添加换行
            mResultText.setText(huanhang);
            Log.d(TAG,bundle.get("res").toString());
          //  Looper.loop();
        }
    }

    class GetThread2 extends Thread {
        public void run() {
            HttpURLConnection conn = null;//声明连接对象
            String urlStr = "http://192.168.43.251/dashboard/sql/besureforcourier.php?id="+id;
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
                    // System.out.print("get方法取回内容：" + resultData);
                    if(resultData.equals("insert_successful")){
                        //Toast.makeText(UserRegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Log.d("r","相等");
                        Looper.prepare();
                        showRes2("记录成功");
                    }
                    else{
                        Looper.prepare();
                        showRes2("记录失败请联系管理员");
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void showRes2(String res){
        Bundle bundle=new Bundle();
        bundle.putString("res2",res);//bundle中也可以放序列化或包裹化的类对象数据

        Message msg=handler2.obtainMessage();//每发送一次都要重新获取
        msg.setData(bundle);
        handler2.sendMessage(msg);//用handler向主线程发送信息
    }
    class MyHandler2 extends Handler {
        @Override
        //接收别的线程的信息并处理
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            Toast.makeText(ResultActivity.this,bundle.get("res2").toString(),Toast.LENGTH_SHORT).show();
           // Log.d("register","toast");
            Looper.loop();
        }
    }

    private void besure(){

        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ResultActivity.this);
        normalDialog.setIcon(R.mipmap.cae_icon);
        normalDialog.setTitle("确认派送");
        normalDialog.setMessage("您确认已将快递单号为"+id+"的快递派出了嘛？");
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
}
