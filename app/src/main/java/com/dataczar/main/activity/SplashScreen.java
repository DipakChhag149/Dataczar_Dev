/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.viewmodel.ClsCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class SplashScreen extends AppCompatActivity
{
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

        if(clsCommon.checkConnection())
            checkUserLoginStatus();
        else
            clsCommon.showSnackBar(false, view);

    }


    class GetLoginStatus extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public GetLoginStatus(Context context)
        {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            if(!pd.isShowing())
                pd.show();

        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,  WSMethods.GETLOGINSTATUS,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(pd.isShowing())
                                pd.dismiss();

                            if(response!= null && !response.isEmpty())
                            {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONObject data_response = jsonResponse.getJSONObject("response");
                                    if(!data_response.getBoolean("success"))
                                    {
                                        //Toast.makeText(context, "Login failed - Move to Login", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                        SplashScreen.this.finish();
                                    }else
                                    {

                                        //Toast.makeText(context, "Login success - Dashboard", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(context, Dashboard.class).putExtra("NeedNavigate", ClsCommon.LOGIN).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                        SplashScreen.this.finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    if(pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }
            )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(clsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    private void checkUserLoginStatus()
    {
        if(getCookie().toString() != null && !getCookie().toString().isEmpty())
            new GetLoginStatus(context).execute();
        else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            SplashScreen.this.finish();
        }
    }

    public String getCookie()
    {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(clsCommon.COOKIE, "");
        return  Cookie;
    }
}
