package com.example.liguopeng;

import android.content.Context;
import android.util.Log;

import com.example.liguopeng.CAE.Express;

import java.util.ArrayList;
import java.util.List;

public class ExpressLab {

    private static ExpressLab sExpressLab;
    private List<Express> mExpresses;
    public static  ExpressLab get(Context context){
       // if (sExpressLab==null){
        Log.d("AdminloginActivity","null");
           return sExpressLab=new ExpressLab(context);

      //  }
      //  Log.d("AdminloginActivity","zhijie ");
      //  return sExpressLab;

    }
    private ExpressLab(Context context){
        mExpresses=new ArrayList<>();
        for (int i=0;i<10;i++){
            Express express=new Express();
            express.setAddress("啊啊啊");
            express.setId(String.valueOf(i));
            express.setFlag("未分配");
            mExpresses.add(express);
        }
    }

    public List<Express> getExpresses() {
        return mExpresses;
    }
}
