/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    RequestQueue requestQueue;
    Context context;
    ClsCommon clsCommon;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        sharedPref = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        context = SplashScreen.this;
        clsCommon = new ClsCommon(context);

        requestQueue = Volley.newRequestQueue(context);

        View view = findViewById(R.id.imgLogo);


        if (clsCommon.checkConnection()){
            checkUserLoginStatus();
            /*if(getIntent() != null){
                String action= getIntent().getAction();
                if (action!=null && action.equalsIgnoreCase("com.notification")){
                    startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.NOTIFICATION).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    SplashScreen.this.finish();
                }else {
                    checkUserLoginStatus();
                }
            }else {
                checkUserLoginStatus();
            }*/
        }else {
            clsCommon.showSnackBar(false, view);
        }
    }

    private void checkUserLoginStatus() {
        if (getCookie().toString() != null && !getCookie().toString().isEmpty())
            new GetLoginStatus(context).execute();
        else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            SplashScreen.this.finish();
        }
    }

    public String getCookie() {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(clsCommon.COOKIE, "");
        return Cookie;
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            String action= getIntent().getAction();
                            if (action!=null && action.equalsIgnoreCase("com.notification")){
                                startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.NOTIFICATION).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                SplashScreen.this.finish();
                            }else {
                                startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.LOGIN).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                SplashScreen.this.finish();
                            }

                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        editor.putString(ClsCommon.FCM_TOKEN, token);
                        editor.apply();
                        Log.e("Device Token",""+token);
                        new AddNotificationToken(SplashScreen.this,token).execute();
                    }
                });
    }

    class GetLoginStatus extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;

        public GetLoginStatus(Context context) {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            if (!pd.isShowing())
                pd.show();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GETLOGINSTATUS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (pd.isShowing())
                                pd.dismiss();

                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONObject data_response = jsonResponse.getJSONObject("response");
                                    if (!data_response.getBoolean("success")) {
                                        //Toast.makeText(context, "Login failed - Move to Login", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        SplashScreen.this.finish();
                                    } else {
                                        getFirebaseToken();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(context, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(clsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }


    class AddNotificationToken extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;
        String token;

        public AddNotificationToken(Context context, String token) {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            if (!pd.isShowing())
                pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, WSMethods.ADD_NOTIFICATION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (pd.isShowing())
                                pd.dismiss();

                            if (response != null && !response.isEmpty()) {
                                try {

                                    JSONObject jsonResponse = new JSONObject(response);
                                    String action= getIntent().getAction();
                                    if (action!=null && action.equalsIgnoreCase("com.notification")){
                                        startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.NOTIFICATION).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        SplashScreen.this.finish();
                                    }else {
                                        startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.LOGIN).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        SplashScreen.this.finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

                    startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.LOGIN).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    SplashScreen.this.finish();

                    //Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("description", "Android");
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    try {
                        //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));

                        List<Header> jsonheader = response.allHeaders;

                        String Cookie = "";
                        for (int i = 0; i < jsonheader.size(); i++) {
                            Header header = jsonheader.get(i);
                            if (header.getName().equals("Set-Cookie")) {
                                Cookie += header.getValue().split(";")[0] + ";";
                            }
                        }

                        Cookie = Cookie.substring(0, Cookie.length() - 1);
                        editor.putString(ClsCommon.COOKIE, Cookie);
                        editor.apply();

                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }

                    return super.parseNetworkResponse(response);
                }
            };
            requestQueue.add(stringRequest);
        }
    }
}
