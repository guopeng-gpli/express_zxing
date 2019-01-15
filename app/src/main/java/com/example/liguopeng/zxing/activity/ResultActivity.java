package com.example.liguopeng.zxing.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

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

public class ResultActivity extends Activity {

	private String TAG="ResultActivity";
	private ImageView mResultImage;
	private TextView mResultText;
    private String baseURL="http://192.168.11.103/dashboard/sql/query_flag.php";
    private String CHandID;
    private String id;
    private String self_tel;
    private String en;
    private MyHandler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Bundle extras = getIntent().getExtras();

		mResultImage = (ImageView) findViewById(R.id.result_image);
		mResultText = (TextView) findViewById(R.id.result_text);
        handler=new MyHandler();//别忘了要先实例化这里，要不然就是空指针
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
}
