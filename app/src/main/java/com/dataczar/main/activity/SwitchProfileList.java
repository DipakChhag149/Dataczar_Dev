/*
 * Copyright (c)  to Samrt Sense . Ai on 2022.
 */

package com.dataczar.main.activity;

import static com.dataczar.main.utils.AppUtils.getCookie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.adapter.QuickLinksAdapter;
import com.dataczar.main.fragment.HomeFragment;
import com.dataczar.main.model.QuickLinkData;
import com.dataczar.main.utils.AppUtils;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.viewmodel.ClsCommon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SwitchProfileList extends AppCompatActivity
{

    ListView lvSwProfile;
    HashMap<String, String> myteam = new HashMap<>();
    ArrayList<HashMap<String, String>> teammap = new ArrayList<>();
    Context context;
    RequestQueue requestQueue;
    CustomHorizontalProgressBar horizontalProgress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.switch_profile);

        horizontalProgress=findViewById(R.id.horizontalProgress);
        context = SwitchProfileList.this;


        requestQueue = Volley.newRequestQueue(context);


        ImageView imgBack = findViewById(R.id.ivBack);



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
        String ProfileId;
        String ProfileName;
        HashMap<String, String> Profile;

        public SwitchUserProfile(Context context, HashMap<String, String> Profile)
        {
            this.Profile = Profile;
            this.ProfileId = Profile.get("account_id").toString();
            this.ProfileName = Profile.get("account_name");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           horizontalProgress.setVisibility(View.VISIBLE);

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
                            horizontalProgress.setVisibility(View.GONE);
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
                                            new getUserProfile(context).execute();


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
                    horizontalProgress.setVisibility(View.GONE);
                    Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(SwitchProfileList.this));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }





    class getUserProfile extends AsyncTask<String, Void, Boolean> {


        public getUserProfile(Context context) {

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

            Log.d("Method Call", "getHomeData");

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GETUSERPROFILE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            horizontalProgress.setVisibility(View.GONE);

                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (jsonObject.has("data")) {
                                        JSONObject uDatas = jsonObject.getJSONObject("data");

                                        if (uDatas.has("website")){
                                            JSONObject website = uDatas.getJSONObject("website");
                                            String strId=website.getString("id");
                                            String account_id=website.getString("account_id");
                                            AppUtils.saveStringValue(SwitchProfileList.this,ClsCommon.WEBSITE_ID,strId);
                                            AppUtils.saveStringValue(SwitchProfileList.this,ClsCommon.ACCOUNT_ID,account_id);

                                            Intent iv = new Intent(SwitchProfileList.this, Dashboard.class);
                                            iv.putExtra("NeedNavigate", ClsCommon.PROFILE);
                                            startActivity(iv);
                                            finishAffinity();
                                            SwitchProfileList.this.finish();
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
                    horizontalProgress.setVisibility(View.GONE);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(SwitchProfileList.this));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }
}
