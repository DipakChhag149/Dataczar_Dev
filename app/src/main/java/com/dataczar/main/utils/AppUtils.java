package com.dataczar.main.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.dataczar.R;
import com.dataczar.main.activity.RegistrationActivity;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;

public class AppUtils {
    public static boolean isInternetAvailable(Context context){
        if (NetworkUtil.isNetworkConnected(context)){
            return true;
        }else {
            Toast.makeText(context, "Please check internet connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static String getCookie(Context context) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        return appPreferenceManager.getStringResponse(ClsCommon.COOKIE);
    }

    public static void saveCookie(Context context,String cookie) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        appPreferenceManager.saveStringResponse(ClsCommon.COOKIE,cookie);
    }

    public static void saveFMCToken(Context context,String token) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        appPreferenceManager.saveStringResponse(ClsCommon.FCM_TOKEN,token);
    }

    public static void saveStringValue(Context context,String key,String value) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        appPreferenceManager.saveStringResponse(key,value);
    }

    public static void saveBoolValue(Context context,String key,boolean value) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        appPreferenceManager.saveBooleanResponse(key,value);
    }
    public static String getStringValue(Context context,String key) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        return appPreferenceManager.getStringResponse(key);
    }

    public static boolean getBoolValue(Context context,String key) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        return appPreferenceManager.getBooleanResponse(key);
    }

    public static void clearData(Context context) {
        AppPreferenceManager appPreferenceManager=new AppPreferenceManager(context);
        appPreferenceManager.clearData();
    }

    public static void showDialog(Context context,String title,String message,String btnText) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.success_dialog);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        AppCompatTextView tvMessage = dialog.findViewById(R.id.tvMessage);
        AppCompatTextView tvTitle = dialog.findViewById(R.id.tvTitle);

        tvTitle.setText(title);
        tvMessage.setText(message);
        AppCompatTextView tvOk = dialog.findViewById(R.id.tvOk);
        tvOk.setText(btnText);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
