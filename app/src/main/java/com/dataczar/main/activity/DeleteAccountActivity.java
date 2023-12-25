package com.dataczar.main.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.BuildConfig;
import com.dataczar.R;
import com.dataczar.databinding.ActivityDeleteAccountBinding;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeleteAccountActivity extends AppCompatActivity {

    private ActivityDeleteAccountBinding mBinding;
    RequestQueue requestQueue;
    CustomHorizontalProgressBar horizontalProgress;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_delete_account);
        horizontalProgress = mBinding.getRoot().findViewById(R.id.horizontalProgress);
        requestQueue = Volley.newRequestQueue(this);
        mBinding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    private void deleteAccount(){
        if (mBinding.edtDelete.getText().toString().isEmpty()){
            Toast.makeText(this,"Please enter confirmation text",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mBinding.edtDelete.getText().toString().equalsIgnoreCase("Delete")){
            Toast.makeText(this,"Please enter valid confirmation text",Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String accountId = prefs.getString(ClsCommon.ACCOUNT_ID, "");
        String strURL = "https://connect.dataczar.com/api/users/"+accountId+"/delete";
        if (NetworkUtil.isNetworkConnected(DeleteAccountActivity.this)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new deleteAccount(DeleteAccountActivity.this, strURL).execute();
                }
            });
        } else {
            Toast.makeText(DeleteAccountActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
        }
    }

    class deleteAccount extends AsyncTask<String, Void, Boolean> {

        private String url;

        public deleteAccount(Context context, String url) {

            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            horizontalProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (BuildConfig.DEBUG) {
                                Log.e("Response", "" + response);
                            }

                            horizontalProgress.setVisibility(View.GONE);
                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
                                    prefs.edit().clear().apply();
                                    Intent i = new Intent(DeleteAccountActivity.this,LoginActivity.class);
                                    startActivity(i);
                                    finishAffinity();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(DeleteAccountActivity.this, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    horizontalProgress.setVisibility(View.GONE);

                    Toast.makeText(DeleteAccountActivity.this, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
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

        public String getCookie() {
            SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }
}