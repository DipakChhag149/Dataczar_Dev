/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.model.CheckLoginStatusResponse;
import com.dataczar.main.utils.AppUtils;
import com.dataczar.main.utils.Logger;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class SplashScreen extends AppCompatActivity {
    RequestQueue requestQueue;
    Context context;
    ClsCommon clsCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        context = SplashScreen.this;
        clsCommon = new ClsCommon(context);

        requestQueue = Volley.newRequestQueue(context);

        View view = findViewById(R.id.imgLogo);


        if (AppUtils.isInternetAvailable(this)) {
            checkUserLoginStatus();
        }

    }

    private void checkUserLoginStatus() {
        if (AppUtils.getCookie(this).toString() != null && !AppUtils.getCookie(this).toString().isEmpty())
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        checkUserIsLoginStatus();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            SplashScreen.this.finish();
        }
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    String action = getIntent().getAction();
                    if (action != null && action.equalsIgnoreCase("com.notification")) {
                        startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.NOTIFICATION).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        SplashScreen.this.finish();
                    } else {
                        startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.LOGIN).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        SplashScreen.this.finish();
                    }

                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                AppUtils.saveFMCToken(SplashScreen.this,token);
                Logger.Log("Device Token", "" + token);
                if (AppUtils.isInternetAvailable(SplashScreen.this)) {
                    try {
                        callAddNotificationTokenAPI(token);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                //new AddNotificationToken(SplashScreen.this, token).execute();
            }
        });
    }

    void checkUserIsLoginStatus() throws IOException {
        ProgressDialog pd;
        pd = new ProgressDialog(this, R.style.ProgressDialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!pd.isShowing()) pd.show();
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url(WSMethods.GETLOGINSTATUS).addHeader(clsCommon.COOKIE, AppUtils.getCookie(this)).get().build();
        Logger.Log("API Request ",""+new Gson().toJson(request));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                Logger.Log("RES Fail", "" + call.toString());
                if (pd.isShowing()) pd.dismiss();

                Toast.makeText(context, "Response Error:  Can't Connect to server.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                if (pd.isShowing()) pd.dismiss();
                final String myResponse = response.body().string();
                Logger.Log("RES Donesss", "" + myResponse);

                SplashScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myResponse != null && !myResponse.equals("")) {
                            CheckLoginStatusResponse checkLoginStatusResponse = new Gson().fromJson(myResponse, CheckLoginStatusResponse.class);
                            if (!checkLoginStatusResponse.getResponse().getSuccess()) {
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                SplashScreen.this.finish();
                            } else {
                                getFirebaseToken();
                            }
                        } else {
                            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            SplashScreen.this.finish();
                        }
                    }
                });

            }
        });
    }

    void callAddNotificationTokenAPI(String token) throws IOException {
        ProgressDialog pd;

        pd = new ProgressDialog(this, R.style.ProgressDialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!pd.isShowing()) pd.show();
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("token", token)
                .add("description", "Android")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(WSMethods.ADD_NOTIFICATION)
                .addHeader(ClsCommon.COOKIE, AppUtils.getCookie(this))
                .put(formBody)
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                Logger.Log("RES Fail", "" + call.toString());
                if (pd.isShowing()) pd.dismiss();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                if (pd.isShowing()) pd.dismiss();
                final String myResponse = response.body().string();
                Logger.Log("RES Donesss", "" + myResponse);

                SplashScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myResponse != null && !myResponse.equals("")) {
                            if (myResponse != null && !myResponse.isEmpty()) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(myResponse);
                                    String action = getIntent().getAction();
                                    if (action != null && action.equalsIgnoreCase("com.notification")) {
                                        startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.NOTIFICATION).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        SplashScreen.this.finish();
                                    } else {
                                        startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.LOGIN).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        SplashScreen.this.finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            Toast.makeText(SplashScreen.this, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
