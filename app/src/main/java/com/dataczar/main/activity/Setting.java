/*
 * Copyright (c)  to Samrt Sense . Ai on 2022.
 */

package com.dataczar.main.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    ListView lvSwProfile;
    HashMap<String, String> myteam = new HashMap<>();
    ArrayList<HashMap<String, String>> teammap = new ArrayList<>();
    Context context;
    RequestQueue requestQueue;
    LinearLayout llUserName, llLegal, llBilling, llLogout, llManageAccount, llChangePass, llNotification;
    ImageView img_bedge_billing;
    Switch notificationSwitch;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);
        context = Setting.this;
        requestQueue = Volley.newRequestQueue(context);
        sharedPref = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgHelp = findViewById(R.id.imgHelp);

        llUserName = findViewById(R.id.llUserName);
        llLegal = findViewById(R.id.llLegal);
        llNotification = findViewById(R.id.llNotification);
        llBilling = findViewById(R.id.llBilling);
        llManageAccount = findViewById(R.id.llManageAccount);
        llChangePass = findViewById(R.id.llChangePass);
        llUserName = findViewById(R.id.llUserName);
        llLogout = findViewById(R.id.llLogout);
        notificationSwitch = findViewById(R.id.notificationSwitch);

        img_bedge_billing = findViewById(R.id.img_bedge_billing);

        if (sharedPref.getBoolean(ClsCommon.NOTIFICATION_STATUS,true)){
            notificationSwitch.setChecked(true);
        }else {
            notificationSwitch.setChecked(false);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new getHomeData(context).execute();

        imgHelp.setOnClickListener(this);
        llUserName.setOnClickListener(this);
        llLegal.setOnClickListener(this);
        llNotification.setOnClickListener(this);
        llBilling.setOnClickListener(this);
        llManageAccount.setOnClickListener(this);
        llChangePass.setOnClickListener(this);


        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);

                // Set a title for alert dialog
                //builder.setTitle("");

                // Show a message on alert dialog
                builder.setMessage("Are you sure you want to logout?");

                // Set the positive button
                builder.setPositiveButton("Yes", null);

                // Set the negative button
                builder.setNegativeButton("No", null);

                // Create the alert dialog
                AlertDialog dialog = builder.create();

                // Finally, display the alert dialog
                if (!dialog.isShowing())
                    dialog.show();

                // Get the alert dialog buttons reference
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                // Change the alert dialog buttons text and background color
                positiveButton.setTextColor(Color.parseColor("#FF0400"));
                //positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

                negativeButton.setTextColor(Color.parseColor("#0000FF"));
                //negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog.isShowing())
                            dialog.dismiss();

                        new Logout(context).execute();
                    }
                });


            }
        });


        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String token=sharedPref.getString(ClsCommon.FCM_TOKEN,"");
                if (isChecked) {
                    editor.putBoolean(ClsCommon.NOTIFICATION_STATUS,true).apply();
                    new AddNotificationToken(Setting.this,token).execute();
                } else {
                    editor.putBoolean(ClsCommon.NOTIFICATION_STATUS,false).apply();
                    new DeleteNotification(Setting.this,token).execute();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        Intent iv = new Intent(context, WebviewLP.class);

        switch (id) {
            case R.id.imgHelp:
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.HELP);
                break;
            case R.id.llUserName:
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.USERSETTINGS);
                break;
            case R.id.llNotification:
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.NOTIFICATION);
                break;
            case R.id.llChangePass:
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.CHANGEPASS);
                break;
            case R.id.llManageAccount:
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.MANAGEACCOUNT);
                break;
            case R.id.llBilling:
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.BILLINGS);
                break;
            case R.id.llLegal:
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.LEGAL);
                break;
        }

        startActivity(iv);
    }

    public String getCookie() {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return Cookie;
    }

    class getHomeData extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;

        public getHomeData(Context context) {
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

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GETUSERPROFILE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (pd.isShowing())
                                pd.dismiss();

                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (jsonObject.has("data")) {
                                        JSONObject uDatas = jsonObject.getJSONObject("data");

                                        if (uDatas.has("team")) {
                                            JSONObject userdata = uDatas.getJSONObject("team");

                                            String status = userdata.get("status").toString();

                                            if (!status.equals("active")) {
                                                img_bedge_billing.setVisibility(View.VISIBLE);
                                            } else {
                                                img_bedge_billing.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(context, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(context, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    class Logout extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;

        public Logout(Context context) {
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

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.LOGOUT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (pd.isShowing() && !Setting.this.isFinishing())
                                pd.dismiss();

                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);


                                    if (jsonObject.has("response")) {
                                        JSONObject responsejson = jsonObject.getJSONObject("response");

                                        if (responsejson.has("success")) {
                                            String issuccess = responsejson.getString("success");

                                            if (issuccess.equals("true")) {
                                                try {
                                                    while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                                                        getSupportFragmentManager().popBackStackImmediate();
                                                    }
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }

                                                Intent iv = new Intent(context, LoginActivity.class);
                                                iv.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(iv);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(context, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(context, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    class DeleteNotification extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;
        String token;

        public DeleteNotification(Context context, String token) {
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

            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, WSMethods.DELETE_NOTIFICATION,
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

                                    } else {
                                        //Toast.makeText(context, "Login success - Dashboard", Toast.LENGTH_SHORT).show();

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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WSMethods.DELETE_NOTIFICATION,
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

                                    } else {
                                        //Toast.makeText(context, "Login success - Dashboard", Toast.LENGTH_SHORT).show();

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
