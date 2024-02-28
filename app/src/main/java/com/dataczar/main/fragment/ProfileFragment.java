package com.dataczar.main.fragment;

import static com.dataczar.main.utils.AppUtils.getCookie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.activity.Setting;
import com.dataczar.main.activity.SwitchProfileList;
import com.dataczar.main.activity.WSMethods;
import com.dataczar.main.activity.WebviewLP;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.nikartm.support.ImageBadgeView;

public class ProfileFragment extends Fragment
{
    TextView tvName, tvEmail, tvPhone, tvUserName, llblank;
    ImageView imgSwitchProf, imgVerifyEmail, imgHelp, imgVerifyPhone,ivSettingBadge;
    ArrayList<Map<String, String>> userprofile;
    Context context;
    RequestQueue requestQueue;

    HashMap<String, String> myteam = new HashMap<>();
    ArrayList<HashMap<String, String>> teammap = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    ImageBadgeView imgSettingMenu;
    ConstraintLayout llNotificationIcon;

    LinearLayout llswitfprof,llSetting;
    CustomHorizontalProgressBar horizontalProgress;

    public ProfileFragment()
    {
        this.context = getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        userprofile = new ArrayList<>();
    }

    public ProfileFragment(Context context, BottomNavigationView bottomNavigationView, ImageBadgeView imgSettingMenu,ConstraintLayout llNotificationIcon) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        userprofile = new ArrayList<>();
        this.bottomNavigationView = bottomNavigationView;
        this.imgSettingMenu = imgSettingMenu;
        this.llNotificationIcon = llNotificationIcon;
    }

    @Override
    public void onStart() {
        super.onStart();

        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.ic_profile);
        item.setChecked(true);

        imgSettingMenu.setVisibility(View.VISIBLE);
        llNotificationIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_user_profile, container, false);

        ivSettingBadge     = view.findViewById(R.id.ivSettingBadge);
        tvName     = view.findViewById(R.id.tvName);
        tvEmail    = view.findViewById(R.id.tvEmail);
        tvPhone    = view.findViewById(R.id.tvPhone);
        tvUserName = view.findViewById(R.id.tvUserName);
        imgSwitchProf = view.findViewById(R.id.imgSwitchProf);
        imgVerifyEmail = view.findViewById(R.id.imgVerifyEmail);
        imgVerifyPhone = view.findViewById(R.id.imgVerifyPhone);
        imgHelp = view.findViewById(R.id.imgHelp);
        llswitfprof = view.findViewById(R.id.llswitfprof);
        horizontalProgress = view.findViewById(R.id.horizontalProgress);
        llSetting = view.findViewById(R.id.llSetting);

        if(NetworkUtil.isNetworkConnected(getContext()))
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new getUserProfile(getContext()).execute();
                }
            });
        }
        else
            Toast.makeText(getContext(), "Please check network connection", Toast.LENGTH_SHORT).show();

        llswitfprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tvUserName.getText().toString().isEmpty())
                {
                    Intent iv = new Intent(context, SwitchProfileList.class);
                    iv.putExtra("TeamData", teammap);
                    startActivity(iv);
                }
            }
        });

        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebviewLP.class);
                intent.putExtra(ClsCommon.WEBSITE, ClsCommon.HELP);
                startActivity(intent);
            }
        });

        llSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(),Setting.class));
            }
        });

        new getNotificatioCount(context).execute();

        return view;
    }

    class getNotificatioCount extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public getNotificatioCount(Context context)
        {
//            pd = new ProgressDialog(context, R.style.ProgressDialog);
//            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd.setCancelable(false);
//            if(!pd.isShowing())
//                pd.show();
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
            Log.d("Method Call", "getHomeData");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.NOTIFICATION_COUNT,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
//                            if(pd.isShowing())
//                                pd.dismiss();

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
                                            imgSettingMenu.setBadgeValue(Integer.parseInt(unreadcount));
                                           /* BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.imgSettingMenu);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(Color.parseColor("#f1592a"));*/
                                        }else
                                        {
                                            imgSettingMenu.setBadgeValue(0);
                                            /*BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.imgSettingMenu);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(Color.parseColor("#00000000"));*/
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
//                    if(pd != null && pd.isShowing())
//                        pd.dismiss();
                    horizontalProgress.setVisibility(View.INVISIBLE);

                    Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireContext()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    class getUserProfile extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public getUserProfile(Context context)
        {
//            pd = new ProgressDialog(context, R.style.ProgressDialog);
//            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd.setCancelable(false);
//            if(!pd.isShowing())
//                pd.show();
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

            //ArrLiveVideoTitles.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GETUSERPROFILE,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            horizontalProgress.setVisibility(View.INVISIBLE);


                            if(response!= null && !response.isEmpty())
                            {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if(jsonObject.has("data"))
                                    {
                                        JSONObject uDatas =  jsonObject.getJSONObject("data");

                                        if(uDatas.has("user"))
                                        {
                                            JSONObject userdata = uDatas.getJSONObject("user");

                                            String id               = userdata.get("id").toString();
                                            String name             = userdata.get("name").toString();
                                            String email            = !userdata.get("email").equals(null) ? userdata.get("email").toString() : "";
                                            String phone            = !userdata.get("phone").equals(null) ? userdata.get("phone").toString() : "";
                                            String email_confirmed  = !userdata.get("email_confirmed").equals(null) ? userdata.get("email_confirmed").toString() : "";
                                            String phone_confirmed  = !userdata.get("phone_confirmed").equals(null) ? userdata.get("phone_confirmed").toString() : "";
                                            String current_team_id  = userdata.get("current_team_id").toString();

                                            tvName.setText(name);
                                            tvEmail.setText(email);
                                            tvPhone.setText(phone);

                                            if(uDatas.has("teams"))
                                            {
                                                JSONArray teamsjson = uDatas.getJSONArray("teams");

                                                //Map<String, HashMap<String, String>> teamMap = new HashMap();


                                                for(int i=0; i<teamsjson.length(); i++)
                                                {
                                                    myteam = new HashMap<>();

                                                    JSONObject teamdata = teamsjson.getJSONObject(i);
                                                    String account_id = teamdata.getString("account_id");
                                                    String account_name = teamdata.getString("account_name");

                                                    myteam.put("account_id", account_id);
                                                    myteam.put("account_name", account_name);

                                                    teammap.add(i, myteam);

                                                    if(account_id.equals(current_team_id))
                                                    {
                                                        tvUserName.setText(account_name);
                                                    }
                                                }
                                            }

                                            if(email != null && !email.equals("null") && !email.isEmpty())
                                            {
                                                imgVerifyEmail.setVisibility(View.VISIBLE);

                                                if(email_confirmed != null && email_confirmed.equals("1"))
                                                    imgVerifyEmail.setImageResource(R.drawable.ic_approved);
                                                else
                                                    imgVerifyEmail.setImageResource(R.drawable.ic_non_verified);
                                            }else{
                                                imgVerifyEmail.setVisibility(View.INVISIBLE);
                                            }



                                            if(phone != null && !phone.equals("null") && !phone.isEmpty())
                                            {
                                                imgVerifyPhone.setVisibility(View.VISIBLE);

                                                if(phone_confirmed != null && phone_confirmed.equals("1"))
                                                    imgVerifyPhone.setImageResource(R.drawable.ic_approved);
                                                else
                                                    imgVerifyPhone.setImageResource(R.drawable.ic_non_verified);

                                            }else{
                                                imgVerifyPhone.setVisibility(View.INVISIBLE);
                                            }



                                        }

                                        if (uDatas.has("team")) {
                                            JSONObject userdata = uDatas.getJSONObject("team");

                                            String status = userdata.get("status").toString();

                                            if (!status.equals("active")) {
                                                ivSettingBadge.setVisibility(View.VISIBLE);
                                            } else {
                                                ivSettingBadge.setVisibility(View.INVISIBLE);
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

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
//                    if(pd != null && pd.isShowing())
//                        pd.dismiss();
                    horizontalProgress.setVisibility(View.INVISIBLE);

                    Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireContext()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }



}