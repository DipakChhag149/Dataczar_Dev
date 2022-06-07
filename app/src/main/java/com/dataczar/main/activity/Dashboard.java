/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.activity;

import static com.dataczar.main.activity.MyApplication.userprofile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.fragment.HomeFragment;
import com.dataczar.main.fragment.NotificationFragment;
import com.dataczar.main.fragment.ProfileFragment;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {
    DrawerLayout mDrawerlayout;
    ImageView imgSideMenu;
    ActionBarDrawerToggle mDrawerToggle;
    NavigationView navigationView;
    LinearLayout mDrawerList_Left;
    int Drawer_width;
    Toolbar toolbar;
    private static final long DOUBLE_CLICK_TIME_DELTA = 500;//milliseconds
    long lastClickTime = 0;
    int calendarClick = 0;
    Context context;
    BottomNavigationView bottomNavigationView;
    RequestQueue requestQueue;
    FragmentTransaction fragmentTransaction = null;
    TextView tvActionbartitle;
    ImageView imgActionbarlogo, imgSettingMenu;
    ClsCommon clsCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        toolbar = findViewById(R.id.toolbar);
        tvActionbartitle = findViewById(R.id.tvActionbartitle);
        imgActionbarlogo = findViewById(R.id.imgActionbarlogo);
        imgSettingMenu   = findViewById(R.id.imgSettingMenu);

        imgSettingMenu.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        context = Dashboard.this;
        clsCommon = new ClsCommon(context);

        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        requestQueue = Volley.newRequestQueue(context);

        mDrawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view_up);

        navigationView.setNavigationItemSelectedListener(this);

        mDrawerlayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerlayout, toolbar, R.string.home, R.string.add_post);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_sidemenu);
        toolbar.setOnMenuItemClickListener(this);

        mDrawerToggle.syncState();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(getIntent() != null)
        {
            String moveto = getIntent().getStringExtra("NeedNavigate");

            if(moveto != null && moveto.equals(ClsCommon.PROFILE))
            {
                fragmentTransaction.replace(R.id.llFargment, new ProfileFragment(context, bottomNavigationView, imgSettingMenu));
                bottomNavigationView.setSelectedItemId(R.id.ic_profile);
                bottomNavigationView.setSelected(true);
                fragmentTransaction.commit();
            }
            else {
                fragmentTransaction.add(R.id.llFargment, new HomeFragment(context, ClsCommon.DASHBOARD, bottomNavigationView, imgSettingMenu));
                bottomNavigationView.setSelectedItemId(R.id.ic_home);
                bottomNavigationView.setSelected(true);
                fragmentTransaction.commit();
            }
        }else
        {
            fragmentTransaction.add(R.id.llFargment, new HomeFragment(context, ClsCommon.DASHBOARD, bottomNavigationView,  imgSettingMenu));
            bottomNavigationView.setSelected(true);
            bottomNavigationView.setSelectedItemId(R.id.ic_home);
            fragmentTransaction.commit();
        }

        //fragmentTransaction.commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                if(!clsCommon.checkConnection()) {
                    clsCommon.showSnackBar(false, imgActionbarlogo.getRootView());
                }
                else
                {
                    int id = item.getItemId();

                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.disallowAddToBackStack();

                    switch(id)
                    {
                        case R.id.ic_home:
                            bottomNavigationView.setSelected(true);
                            imgSettingMenu.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.GONE);
                            imgActionbarlogo.setVisibility(View.VISIBLE);
                            fragmentTransaction.replace(R.id.llFargment, new HomeFragment(context, ClsCommon.DASHBOARD, bottomNavigationView, imgSettingMenu));
                            fragmentTransaction.commit();
                            return true;

                        case R.id.ic_addpost:
                            imgSettingMenu.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.VISIBLE);
                            imgActionbarlogo.setVisibility(View.GONE);
                            tvActionbartitle.setText("Add Post");
                            fragmentTransaction.replace(R.id.llFargment, new HomeFragment(context, ClsCommon.ADDPOST, bottomNavigationView, imgSettingMenu));
                            fragmentTransaction.commit();
                            return true;

                        case R.id.ic_notification:
                            imgSettingMenu.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.VISIBLE);
                            imgActionbarlogo.setVisibility(View.GONE);
                            tvActionbartitle.setText("Notifications");
                            fragmentTransaction.replace(R.id.llFargment, new NotificationFragment(context, bottomNavigationView, imgSettingMenu));
                            fragmentTransaction.commit();
                            return true;

                        case R.id.ic_profile:
                            imgSettingMenu.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.VISIBLE);
                            imgActionbarlogo.setVisibility(View.GONE);
                            tvActionbartitle.setText("Profile");
                            fragmentTransaction.replace(R.id.llFargment, new ProfileFragment(context, bottomNavigationView, imgSettingMenu));
                            fragmentTransaction.commit();
                            return true;

                    }
                }




                return false;
            }
        });


        imgSettingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Setting.class));
            }
        });


        ActivityCompat.requestPermissions(Dashboard.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        /*Log.e("O", "Called");

        if(getIntent() != null)
        {
            String moveto = getIntent().getStringExtra("NeedNavigate");

            if(moveto != null && moveto.equals(ClsCommon.PROFILE)) {
                fragmentTransaction.replace(R.id.llFargment, new ProfileFragment(context, bottomNavigationView, imgSettingMenu));
                fragmentTransaction.commit();
            }
            else {
                fragmentTransaction.replace(R.id.llFargment, new HomeFragment(context, ClsCommon.DASHBOARD, bottomNavigationView));
                bottomNavigationView.setSelectedItemId(R.id.ic_home);
                bottomNavigationView.setSelected(true);
                fragmentTransaction.commit();

            }
        }*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Dashboard.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int viewID = item.getItemId();

        if (mDrawerlayout.isDrawerOpen(mDrawerList_Left))
            mDrawerlayout.closeDrawer(mDrawerList_Left);
        else
            mDrawerlayout.openDrawer(mDrawerList_Left);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        int viewID = item.getItemId();
        mDrawerlayout.closeDrawer(Gravity.LEFT);

        if(viewID == R.id.ic_website)
        {
            if (isSingleClick())
            {
                Intent intent = new Intent(context, WebviewLP.class);
                intent.putExtra(ClsCommon.WEBSITE, ClsCommon.WEBSITE);
                startActivity(intent);
            }
        }

        else if(viewID == R.id.ic_email)
        {
            if (isSingleClick())
            {
                Intent intent = new Intent(context, WebviewLP.class);
                intent.putExtra(ClsCommon.WEBSITE, ClsCommon.EMAIL);
                startActivity(intent);
            }
        }

        else if(viewID == R.id.ic_list)
        {
            if (isSingleClick())
            {
                Intent intent = new Intent(context, WebviewLP.class);
                intent.putExtra(ClsCommon.WEBSITE, ClsCommon.LIST);
                startActivity(intent);
            }
        }

        else if(viewID == R.id.ic_domain)
        {
            if (isSingleClick())
            {
                Intent intent = new Intent(context, WebviewLP.class);
                intent.putExtra(ClsCommon.WEBSITE, ClsCommon.DOMAIN);
                startActivity(intent);
            }
        }

        else if(viewID == R.id.ic_content)
        {
            if (isSingleClick())
            {
                Intent intent = new Intent(context, WebviewLP.class);
                intent.putExtra(ClsCommon.WEBSITE, ClsCommon.CONTENT);
                startActivity(intent);
            }
        }



        return true;
    }

    private boolean isSingleClick()
    {
        long clickTime = System.currentTimeMillis();
        long transcureTime = clickTime - lastClickTime;
        lastClickTime = clickTime;
        return transcureTime > DOUBLE_CLICK_TIME_DELTA;
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* if(getIntent() != null)
        {
            String moveto = getIntent().getStringExtra("NeedNavigate");

            if(moveto != null && moveto.equals(ClsCommon.PROFILE)) {
                bottomNavigationView.setSelectedItemId(R.id.ic_profile);
            }
            else {
                bottomNavigationView.setSelectedItemId(R.id.ic_home);
            }
        }else
        {
            bottomNavigationView.setSelectedItemId(R.id.ic_home);
        }*/

        new getHomeData(context).execute();

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.menu_settings:
                startActivity(new Intent(context, Setting.class));
                return true;
        }
        return false;
    }

    class getHomeData extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public getHomeData(Context context)
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

            Log.d("Method Call", "getHomeData");

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GETUSERPROFILE,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(pd.isShowing())
                                pd.dismiss();


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Tag", "getHomeData");
                                }
                            });

                            if(response!= null && !response.isEmpty())
                            {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if(jsonObject.has("data"))
                                    {
                                        JSONObject uDatas =  jsonObject.getJSONObject("data");

                                        if(uDatas.has("team"))
                                        {
                                            JSONObject userdata = uDatas.getJSONObject("team");

                                               String status  = userdata.get("status").toString();

                                            if(!status.equals("active"))
                                            {
                                                BadgeDrawable ProfileBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_profile);
                                                ProfileBadge.setNumber(0);
                                                ProfileBadge.setBadgeTextColor(Color.parseColor("#f1592a"));
                                                ProfileBadge.setBackgroundColor(Color.parseColor("#f1592a"));

                                                imgSettingMenu.setImageDrawable(getResources().getDrawable(R.drawable.ic_setting_bedge));
                                            }
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else
                            {
                                Toast.makeText(context," Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }

                            new getNotificatioCount(context).execute();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    if(pd != null && pd.isShowing())
                        pd.dismiss();

                    //Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
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

    class getNotificatioCount extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public getNotificatioCount(Context context)
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
            Log.d("Method Call", "getHomeData");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.NOTIFICATION_COUNT,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(pd.isShowing())
                                pd.dismiss();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Tag", "getNotificatio - Count");
                                }
                            });


                            if(response!= null && !response.isEmpty())
                            {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if(jsonObject.has("count"))
                                    {
                                        String count =  jsonObject.getString("count");
                                        String unreadcount =  jsonObject.getString("unread_count");

                                        if(unreadcount != null && unreadcount.trim().length()>0 && !unreadcount.equals("0"))
                                        {
                                            BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_notification);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(Color.parseColor("#f1592a"));
                                        }else
                                        {
                                            /*BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_notification);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(getResources().getColor(android.R.color.transparent));*/
                                        }
                                    }
                                } catch (JSONException e) {
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

                    //Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
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
