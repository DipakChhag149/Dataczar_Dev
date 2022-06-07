package com.dataczar.main.fragment;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.activity.NotificationPreview;
import com.dataczar.main.activity.SwitchProfileList;
import com.dataczar.main.activity.WSMethods;
import com.dataczar.main.model.NotificationModel;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class NotificationFragment extends Fragment
{

    Context context;
    ListView listview;
    RequestQueue requestQueue;
    SwipeRefreshLayout swipeLayout;
    Boolean isLoaded;
    BottomNavigationView bottomNavigationView;

    public NotificationFragment(Context context, BottomNavigationView bottomNavigationView, ImageView imgSettingMenu)
    {
        this.context= context;
        this.bottomNavigationView = bottomNavigationView;
        imgSettingMenu.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_notification, container, false);

        requestQueue = Volley.newRequestQueue(context);

        listview = view.findViewById(R.id.list);
        swipeLayout = view.findViewById(R.id.swiperefresh);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getAllNotification(context).execute();
                Toast.makeText(context, "Please Wait...", Toast.LENGTH_SHORT).show();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000); // Delay in millis
            }
        });

       /* if (savedInstanceState != null) {
            isLoaded = savedInstanceState.getBoolean("ISLOADED", false);
        }

        if(isLoaded!= null && !isLoaded)
        {

        }*/

        new getNotificatioCount(context).execute();

        Log.e("Tag", "onCreateView");

        return view;
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
                                            BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_notification);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(Color.parseColor("#00000000"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else
                            {
                                Toast.makeText(context," Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }

                            new getAllNotification(context).execute();

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.e("Tag", "onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        //new getAllNotification(context).execute();

        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.ic_notification);
        item.setChecked(true);

        new getNotificatioCount(context).execute();

        Log.e("Tag", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.e("Tag", "OnDestroid");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("Tag", "onViewCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Tag", "OnDestroid");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ISLOADED", isLoaded);
    }

    public class getAllNotification extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public getAllNotification(Context context)
        {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            Log.d("Method Call", "getAllNotification");
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
            Log.d("Method Call", "getAllNotification");
            //ArrLiveVideoTitles.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.NOTIFICATION_ALLLIST,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            isLoaded = true;

                            if(pd.isShowing())
                                pd.dismiss();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Tag", "getAllNotification");
                                }
                            });


                          List<NotificationModel> notificationModelRead = new ArrayList<>();
                          List<NotificationModel> notificationModelUnread = new ArrayList<>();
                          List<NotificationModel> notificationModelArchived = new ArrayList<>();
                          List<NotificationModel> notificationModelAll = new ArrayList<>();

                            if(response!= null && !response.isEmpty())
                            {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);
                                    MyNotificationAdapter myNotificationAdapter = new MyNotificationAdapter();

                                    if(jsonObject.has("notifications"))
                                    {
                                        JSONArray notifications = jsonObject.getJSONArray("notifications");
                                        //List<NotificationModel> arrTest = new ArrayList<>();
                                        for (int i = 0; i < notifications.length(); i++)
                                        {

                                            JSONObject jsonobj = notifications.getJSONObject(i);
                                            NotificationModel notificationModelPojo = new NotificationModel();

                                            JSONObject jsondata = jsonobj.getJSONObject("data");

                                            String id = jsonobj.getString("id");
                                            String isStatus = jsonobj.getString("isStatus");
                                            String time     = jsonobj.getString("human");
                                            String notifiable_id     = jsonobj.getString("notifiable_id");
                                            String title    = jsondata.getString("title");
                                            String msg      = jsondata.getString("msg");

                                            notificationModelPojo.setId(id);
                                            notificationModelPojo.setNotifiable_id(notifiable_id);
                                            notificationModelPojo.setMsg(msg);
                                            notificationModelPojo.setTitle(title);
                                            notificationModelPojo.setTime(time);
                                            notificationModelPojo.setIsStatus(isStatus);

                                            if(isStatus.equals("archived"))
                                            {
                                                notificationModelArchived.add(notificationModelPojo);
                                            }
                                            else if(isStatus.equals("unread") || isStatus.equals("read"))
                                            {
                                                notificationModelUnread.add(notificationModelPojo);
                                            }/* else if(isStatus.equals("read"))
                                            {
                                                notificationModelRead.add(notificationModelPojo);
                                            }*/

                                        }
                                    }

                                    if(notificationModelUnread.size() > 0)
                                    {
                                        for(int i = 0; i < notificationModelUnread.size(); i++)
                                        {
                                            notificationModelAll.add(notificationModelUnread.get(i));
                                            myNotificationAdapter.addItem(notificationModelUnread.get(i));
                                        }
                                    }

                                    if(notificationModelRead.size() > 0)
                                    {
                                        for(int i = 0; i < notificationModelRead.size(); i++)
                                        {
                                            notificationModelAll.add(notificationModelRead.get(i));
                                            myNotificationAdapter.addItem(notificationModelRead.get(i));
                                        }
                                    }


                                    if(notificationModelArchived.size() > 0)
                                    {
                                        for(int i = 0; i < notificationModelArchived.size(); i++)
                                        {
                                            if(i==0)
                                            {
                                                if(notificationModelUnread.size() > 0 || notificationModelRead.size() > 0)
                                                    myNotificationAdapter.addSeparatorItem(notificationModelArchived.get(i));
                                            }

                                            notificationModelAll.add(notificationModelArchived.get(i));
                                            myNotificationAdapter.addItem(notificationModelArchived.get(i));
                                        }
                                    }

                                    listview.setAdapter(myNotificationAdapter);

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


    public class setArchivedNotification extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        String NotificationId;
        public setArchivedNotification(Context context, String NotificationId)
        {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.NotificationId = NotificationId;
        }

        @Override
        protected void onPreExecute()
        {
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

            //ArrLiveVideoTitles.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.NOTIFICATION_ARCHIVED + NotificationId,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(pd.isShowing())
                                pd.dismiss();

                            new getAllNotification(context).execute();

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
        SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return  Cookie;
    }

    //Adapter Class
    private class MyNotificationAdapter extends BaseAdapter
    {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        //private ArrayList<String> mData = new ArrayList<String>();
        private ArrayList<NotificationModel> mData = new ArrayList<NotificationModel>();

        private LayoutInflater mInflater;

        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

        public MyNotificationAdapter() {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final NotificationModel notoficationpojo)
        {
            mData.add(notoficationpojo);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final NotificationModel notoficationpojo)
        {
            mData.add(notoficationpojo);
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        public int getCount() {
            return mData.size();
        }

        public NotificationModel getItem(int position) {
            return mData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder = new ViewHolder();
            int type = getItemViewType(position);

                switch (type)
                {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.notification_item, null);

                        holder.tvTitle = (TextView)convertView.findViewById(R.id.tvNotiHeader);
                        holder.tvDesc = (TextView)convertView.findViewById(R.id.tvNotiDtl);
                        holder.tvTime = (TextView)convertView.findViewById(R.id.tvNotiTime);
                        holder.llbackground= (LinearLayout) convertView.findViewById(R.id.llbackground);
                        holder.imgArchived = (ImageView) convertView.findViewById(R.id.imgArchived);
                        holder.llblank = (LinearLayout) convertView.findViewById(R.id.llblank);

                        NotificationModel notificationDetails = mData.get(position);

                        String isStatus = mData.get(position).isStatus;

                        holder.tvTitle.setTag(isStatus);
                        holder.tvDesc.setTag(notificationDetails.id);
                        holder.imgArchived.setTag(notificationDetails.id);

                        String msg = notificationDetails.msg;

                        if(msg.contains("."))
                        {
                            try {
                                msg = msg.split(".")[0];
                            }catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }

                        }

                        holder.tvTitle.setText(notificationDetails.title);
                        holder.tvDesc.setText(notificationDetails.msg);
                        holder.tvTime.setText(notificationDetails.time);

                        holder.tvDesc.setTag(notificationDetails);

                        if(holder.tvTitle.getTag().equals("read"))
                        {
                            holder.llbackground.setBackgroundColor(Color.parseColor("#f0e0e0"));
                            holder.imgArchived.setVisibility(View.VISIBLE);
                        }else if(holder.tvTitle.getTag().equals("unread"))
                        {
                            holder.llbackground.setBackgroundColor(Color.parseColor("#e0f0e0"));
                            holder.imgArchived.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            holder.llbackground.setBackgroundColor(Color.parseColor("#00000000"));
                            holder.imgArchived.setVisibility(View.INVISIBLE);
                        }

                        holder.imgArchived.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {
                                new setArchivedNotification(context,view.getTag().toString()).execute();
                            }
                        });

                        holder.tvDesc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent iv = new Intent(context, NotificationPreview.class);
                                iv.putExtra("NotificationData", (Serializable) holder.tvDesc.getTag());
                                startActivity(iv);
                            }
                        });

                        holder.tvTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent iv = new Intent(context, NotificationPreview.class);
                                iv.putExtra("NotificationData", (Serializable) holder.tvDesc.getTag());
                                startActivity(iv);
                            }
                        });

                        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent iv = new Intent(context, NotificationPreview.class);
                                iv.putExtra("NotificationData", (Serializable) holder.tvDesc.getTag());
                                startActivity(iv);
                            }
                        });

                        holder.llblank.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent iv = new Intent(context, NotificationPreview.class);
                                iv.putExtra("NotificationData", (Serializable) holder.tvDesc.getTag());
                                startActivity(iv);
                            }
                        });

                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.notification_section, null);
                        holder.tvTitle = (TextView)convertView.findViewById(R.id.text);
                        holder.tvTitle.setText("Archived Notifications");

                        break;
                }

                convertView.setTag(holder);


            return convertView;
        }

    }

    public static class ViewHolder
    {
        public TextView tvTitle, tvDesc, tvTime;
        LinearLayout llbackground;
        ImageView imgArchived;
        LinearLayout llblank;
    }

    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


}