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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.dataczar.main.viewmodel.ClsCommon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SwitchProfileList extends AppCompatActivity
{

    ListView lvSwProfile;
    HashMap<String, String> myteam = new HashMap<>();
    ArrayList<HashMap<String, String>> teammap = new ArrayList<>();
    Context context;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.switch_profile);
        context = SwitchProfileList.this;
        requestQueue = Volley.newRequestQueue(context);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imgBack = findViewById(R.id.imgBack);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        Intent intent = getIntent();
        if(intent != null)
        {
            teammap = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("TeamData");

            lvSwProfile = findViewById(R.id.lvSwProfile);

            ProfileAdapater profileAdapater = new ProfileAdapater();
            lvSwProfile.setAdapter(profileAdapater);

        }else
        {
            Toast.makeText(context, "No Profile Avilavle to Switch", Toast.LENGTH_SHORT).show();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public class ProfileAdapater extends BaseAdapter
    {
        @Override
        public int getCount() {
            return teammap.size();
        }

        @Override
        public Object getItem(int i) {
            return teammap.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            LayoutInflater inflter = (LayoutInflater.from(SwitchProfileList.this));
            view = inflter.inflate(R.layout.switch_profile_item, null);

            TextView tvpostion = (TextView) view.findViewById(R.id.tvpostion);
            TextView tvprofilename = (TextView) view.findViewById(R.id.tvprofilename);

            myteam = teammap.get(i);

            String account_id = myteam.get("account_id");
            String account_name = myteam.get("account_name");

            tvpostion.setText((i+1) + "");

            tvprofilename.setText(account_name);

            tvprofilename.setTag(myteam);
            tvpostion.setTag(myteam);

            tvprofilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String accountId = view.getTag().toString();
                    new SwitchUserProfile(context, (HashMap<String, String>) view.getTag()).execute();

                }
            });

            tvpostion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String accountId = view.getTag().toString();
                    new SwitchUserProfile(context, (HashMap<String, String>) view.getTag()).execute();

                }
            });

            return view;
        }
    }


    class SwitchUserProfile extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        String ProfileId;
        String ProfileName;
        HashMap<String, String> Profile;

        public SwitchUserProfile(Context context, HashMap<String, String> Profile)
        {
            this.Profile = Profile;
            this.ProfileId = Profile.get("account_id").toString();
            this.ProfileName = Profile.get("account_name");
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

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.SWITCHACCOUNT + ProfileId,
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
                                    JSONObject json = new JSONObject(response);

                                    if(json.has("original"))
                                    {
                                        JSONObject json1 = json.getJSONObject("original");
                                        JSONObject json2 = json1.getJSONObject("response");
                                        String issuccess = json2.getString("success");

                                        if(issuccess.equals("true"))
                                        {
                                            Intent iv = new Intent(SwitchProfileList.this, Dashboard.class);
                                            iv.putExtra("NeedNavigate", ClsCommon.PROFILE);
                                            startActivity(iv);

                                            SwitchProfileList.this.finish();

                                            //Toast.makeText(context, "Profile Switch successfully to : " + ProfileName, Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(context, "Switching profile failed, please try again", Toast.LENGTH_SHORT).show();
                                        }

                                    }
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



    public String getCookie()
    {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return  Cookie;
    }
}
