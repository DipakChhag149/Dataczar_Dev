/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.activity;



import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.fragment.AddPostNewFragment;
import com.dataczar.main.fragment.EducationFragment;
import com.dataczar.main.fragment.HomeFragment;
import com.dataczar.main.fragment.NotificationFragment;
import com.dataczar.main.fragment.ProfileFragment;
import com.dataczar.main.utils.AppPreferenceManager;
import com.dataczar.main.utils.AppUtils;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.nikartm.support.ImageBadgeView;

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
    ImageView imgActionbarlogo ;
    ImageBadgeView imgSettingMenu;
    ConstraintLayout llNotificationIcon;
    ClsCommon clsCommon;
    ConstraintLayout llLinks;
    ImageView ivExpand;
   //public static  CustomHorizontalProgressBar horizontalProgress;
    private FragmentRefreshListener fragmentRefreshListener;
    private int unReadCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
      //  horizontalProgress = findViewById(R.id.horizontalProgress);

        toolbar = findViewById(R.id.toolbar);
        tvActionbartitle = findViewById(R.id.tvActionbartitle);
        imgActionbarlogo = findViewById(R.id.imgActionbarlogo);
        imgSettingMenu   = findViewById(R.id.imgSettingMenu);
        llNotificationIcon   = findViewById(R.id.llNotificationIcon);
        llLinks   = findViewById(R.id.llLinks);
        ivExpand   = findViewById(R.id.ivExpand);

        imgSettingMenu.setVisibility(View.GONE);
        llNotificationIcon.setVisibility(View.GONE);
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
            String action= getIntent().getAction();
            if (action!=null && action.equalsIgnoreCase("com.notification")){
                fragmentTransaction.add(R.id.llFargment, new NotificationFragment(context));
                bottomNavigationView.setSelectedItemId(R.id.ic_profile);
                bottomNavigationView.setSelected(true);
                fragmentTransaction.commit();
            }else {
                String moveto = getIntent().getStringExtra("NeedNavigate");

                if(moveto != null && moveto.equals(ClsCommon.PROFILE))
                {
                    fragmentTransaction.replace(R.id.llFargment, new ProfileFragment(context, bottomNavigationView, imgSettingMenu,llNotificationIcon));
                    bottomNavigationView.setSelectedItemId(R.id.ic_profile);
                    bottomNavigationView.setSelected(true);
                    imgActionbarlogo.setVisibility(View.GONE);
                    tvActionbartitle.setVisibility(View.VISIBLE);
                    tvActionbartitle.setText("Profile");
                    fragmentTransaction.commit();
                }else if(moveto!=null && moveto.equals(ClsCommon.NOTIFICATION)){
                    fragmentTransaction.add(R.id.llFargment, new NotificationFragment(context));
                    bottomNavigationView.setSelectedItemId(R.id.ic_profile);
                    bottomNavigationView.setSelected(true);
                    fragmentTransaction.commit();
                }
                else {
                    llLinks.setVisibility(View.VISIBLE);
                    fragmentTransaction.add(R.id.llFargment, new HomeFragment(context, ClsCommon.DASHBOARD, bottomNavigationView, imgSettingMenu,llNotificationIcon,llLinks,ivExpand,true));
                    bottomNavigationView.setSelectedItemId(R.id.ic_home);
                    bottomNavigationView.setSelected(true);
                    fragmentTransaction.commit();
                }
            }

        }else
        {
            llLinks.setVisibility(View.VISIBLE);
            fragmentTransaction.add(R.id.llFargment, new HomeFragment(context, ClsCommon.DASHBOARD, bottomNavigationView,  imgSettingMenu,llNotificationIcon,llLinks,ivExpand,true));
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
                            llNotificationIcon.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.GONE);
                            imgActionbarlogo.setVisibility(View.VISIBLE);
                            llLinks.setVisibility(View.VISIBLE);
                            fragmentTransaction.replace(R.id.llFargment, new HomeFragment(context, ClsCommon.DASHBOARD, bottomNavigationView, imgSettingMenu,llNotificationIcon,llLinks,ivExpand,true));
                            fragmentTransaction.commit();
                            return true;

                        case R.id.ic_addpost:
                            imgSettingMenu.setVisibility(View.GONE);
                            llNotificationIcon.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.VISIBLE);
                            imgActionbarlogo.setVisibility(View.GONE);
                            llLinks.setVisibility(View.GONE);
                            tvActionbartitle.setText("Add Post");
                            fragmentTransaction.replace(R.id.llFargment, new AddPostNewFragment(context));
                            fragmentTransaction.commit();
                            return true;

                        case R.id.ic_education:
                            imgSettingMenu.setVisibility(View.GONE);
                            llNotificationIcon.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.VISIBLE);
                            imgActionbarlogo.setVisibility(View.GONE);
                            llLinks.setVisibility(View.GONE);
                            tvActionbartitle.setText("Education");
                            fragmentTransaction.replace(R.id.llFargment, new EducationFragment());
                            fragmentTransaction.commit();
                            return true;

                        case R.id.ic_notification:
                            imgSettingMenu.setVisibility(View.GONE);
                            llNotificationIcon.setVisibility(View.GONE);
                            llNotificationIcon.setVisibility(View.GONE);
                            tvActionbartitle.setVisibility(View.VISIBLE);
                            imgActionbarlogo.setVisibility(View.GONE);
                            llLinks.setVisibility(View.GONE);
                            tvActionbartitle.setText("Notifications");
                            fragmentTransaction.replace(R.id.llFargment, new NotificationFragment(context));
                            fragmentTransaction.commit();
                            return true;

                        case R.id.ic_profile:
                            tvActionbartitle.setVisibility(View.VISIBLE);
                            imgActionbarlogo.setVisibility(View.GONE);
                            llLinks.setVisibility(View.GONE);
                            tvActionbartitle.setText("Profile");
                            fragmentTransaction.replace(R.id.llFargment, new ProfileFragment(context, bottomNavigationView, imgSettingMenu,llNotificationIcon));
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
                startActivity(new Intent(context, NotificationListActivity.class));
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    1);
        }else{
            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("create_post_update"));

    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(getFragmentRefreshListener()!=null){
                getFragmentRefreshListener().onRefresh();
            }
        }
    };

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }


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
        context.registerReceiver(broadcastReceiver, new IntentFilter("notification_update"));
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



        new getNotificatioCount(context).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(broadcastReceiver);
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
        public getHomeData(Context context)
        {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //horizontalProgress.setVisibility(View.VISIBLE);

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
                            Log.e("Response",""+response);
                          //  horizontalProgress.setVisibility(View.GONE);


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
                                                if (unReadCount<=0){
                                                    BadgeDrawable ProfileBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_profile);
                                                    ProfileBadge.setVisible(true);
                                                    ProfileBadge.setNumber(0);
                                                    ProfileBadge.setBadgeTextColor(Color.parseColor("#f1592a"));
                                                    ProfileBadge.setBackgroundColor(Color.parseColor("#f1592a"));

                                                }


                                            }
                                        }

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else
                            {
                                Intent intent=new Intent(context, SessionExpiredActivity.class);
                                startActivity(intent);
                                //Toast.makeText(context," Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }

                           // new getNotificatioCount(context).execute();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                   // horizontalProgress.setVisibility(View.GONE);

                    //Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, AppUtils.getCookie(Dashboard.this));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    class getNotificatioCount extends AsyncTask<String, Void, Boolean>
    {
        public getNotificatioCount(Context context)
        {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // horizontalProgress.setVisibility(View.VISIBLE);

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
                        //    horizontalProgress.setVisibility(View.GONE);

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
                                        unReadCount = Integer.parseInt(unreadcount);

                                        if(unreadcount != null && unreadcount.trim().length()>0 && !unreadcount.equals("0"))
                                        {
                                            BadgeDrawable ProfileBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_profile);
                                            ProfileBadge.setVisible(true);
                                            ProfileBadge.setNumber(unReadCount);
                                            ProfileBadge.setBadgeTextColor(Color.parseColor("#fefefe"));
                                            ProfileBadge.setBackgroundColor(Color.parseColor("#f1592a"));

                                            imgSettingMenu.setBadgeValue(unReadCount);
                                            /*BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_profile);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(Color.parseColor("#f1592a"));*/
                                        }else
                                        {
                                            imgSettingMenu.setBadgeValue(0);
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
                            new getHomeData(context).execute();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //horizontalProgress.setVisibility(View.GONE);

                    //Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE,AppUtils.getCookie(Dashboard.this));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        @Override
        public void onReceive(Context context, Intent intent) {

            new getNotificatioCount(context).execute();
        }
    };

    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    public static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("notification_update");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }
}
