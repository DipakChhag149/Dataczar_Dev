package com.dataczar.main.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.dataczar.BuildConfig;

public class Logger {
    public static void Log(String tag,String msg){
        if (BuildConfig.DEBUG){
            Log.e(tag,msg);
        }
    }

    public static void showToastMessage(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
