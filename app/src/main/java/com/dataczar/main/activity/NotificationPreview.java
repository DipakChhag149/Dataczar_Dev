/*
 * Copyright (c)  to Samrt Sense . Ai on 2022.
 */

package com.dataczar.main.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.model.NotificationModel;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.material.badge.BadgeDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationPreview extends AppCompatActivity
{

    Context context;
    WebView myWebView;
    String title = "";
    ClsCommon clsCommon;
    Toolbar toolbar;
    NotificationModel mynotification;
    RequestQueue requestQueue;
    String NotificationId, NotificationStstus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_read_detail);
        context = NotificationPreview.this;

        toolbar = findViewById(R.id.toolbar);
        ImageView imgBack = findViewById(R.id.imgBack);

        TextView tvNotiHeader = findViewById(R.id.tvNotiHeader);
        TextView tvDesc = findViewById(R.id.tvDesc);
        TextView tvTime = findViewById(R.id.tvTime);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        requestQueue = Volley.newRequestQueue(context);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        if(intent != null)
        {
            mynotification = (NotificationModel) intent.getSerializableExtra("NotificationData");
            tvNotiHeader.setText(mynotification.getTitle());
            tvDesc.setText(mynotification.getMsg());
            tvTime.setText(mynotification.getTime());
            NotificationId = mynotification.getId();
            NotificationStstus = mynotification.getIsStatus();
        } else {
            Toast.makeText(context, "No notification detail found", Toast.LENGTH_SHORT).show();
        }

        if(NotificationStstus.equals("unread")) {
            new readNotification(context).execute();
        }

    }

    class readNotification extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public readNotification(Context context)
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

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.NOTIFICATION_READ + NotificationId,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(pd.isShowing())
                                pd.dismiss();

                            if(response!= null && !response.isEmpty())
                            {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else
                            {
                                Toast.makeText(context," Can't Connect to server.", Toast.LENGTH_LONG).show();
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
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getCookie()
    {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return  Cookie;
    }

}

