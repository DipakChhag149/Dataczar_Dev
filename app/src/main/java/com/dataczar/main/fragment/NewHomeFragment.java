package com.dataczar.main.fragment;

import static com.dataczar.main.utils.AppUtils.getCookie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import com.dataczar.databinding.FragmentEducationBinding;
import com.dataczar.databinding.FragmentNewHomeBinding;
import com.dataczar.main.activity.EBookDetailsActivity;
import com.dataczar.main.activity.SessionExpiredActivity;
import com.dataczar.main.activity.WSMethods;
import com.dataczar.main.activity.WebviewActivity;
import com.dataczar.main.adapter.HomePageListAdapter;
import com.dataczar.main.adapter.QuickLinksAdapter;
import com.dataczar.main.adapter.ViewPagerAdapter;
import com.dataczar.main.model.GetHomePageResponse;
import com.dataczar.main.model.GetPostListResponse;
import com.dataczar.main.model.LearnModel;
import com.dataczar.main.model.QuickLinkData;
import com.dataczar.main.utils.AppUtils;
import com.dataczar.main.utils.Logger;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.nikartm.support.ImageBadgeView;

public class NewHomeFragment extends Fragment {

    private FragmentNewHomeBinding mBinding;
    private String strTitle;
    ClsCommon clsCommon;
    BottomNavigationView bottomNavigationView;
    private ImageView ivExpand;
    private boolean isQuickLinks=false;
    RequestQueue requestQueue;
    private QuickLinksAdapter quickLinksAdapter;
    private ViewPagerAdapter mViewPagerAdapter;

    private ArrayList<QuickLinkData> quickLinkDataList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = FragmentNewHomeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        new getHomeData(requireContext()).execute();

        mBinding.clGettingStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(requireContext(), WebviewActivity.class);
                intent.putExtra(ClsCommon.URL, "https://www.youtube.com/embed/y4d86GCJMGs");
                intent.putExtra(ClsCommon.WEBSITE, "Getting Started with Dataczar");
                startActivity(intent);
            }
        });


    }

    public NewHomeFragment(Context context, String Title, BottomNavigationView bottomNavigationView, ImageBadgeView imgSettingMenu, ConstraintLayout llNotificationIcon, ConstraintLayout llLinks, ImageView ivExpand, boolean isQuickLinks) {
        this.strTitle = Title;
        clsCommon = new ClsCommon(context);
        this.bottomNavigationView = bottomNavigationView;
        this.ivExpand = ivExpand;
        this.isQuickLinks=isQuickLinks;
        llLinks.setVisibility(View.INVISIBLE);
        imgSettingMenu.setVisibility(View.GONE);
        llNotificationIcon.setVisibility(View.GONE);
    }



    class getHomeData extends AsyncTask<String, Void, Boolean> {


        public getHomeData(Context context) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.horizontalProgress.setVisibility(View.VISIBLE);

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
                            mBinding.horizontalProgress.setVisibility(View.INVISIBLE);
                            Logger.Log("Response",""+response);
                            if (response != null && !response.isEmpty()) {
                                if (response.contains("<!DOCTYPE html>")){
                                    Intent intent=new Intent(requireContext(), SessionExpiredActivity.class);
                                    startActivity(intent);
                                }else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        if (jsonObject.has("data")) {
                                            JSONObject uDatas = jsonObject.getJSONObject("data");

                                            if (uDatas.has("website")){
                                                JSONObject website = uDatas.getJSONObject("website");
                                                Logger.Log("WEBSITE",""+ new Gson().toJson(website));
                                                String strId=website.getString("id");
                                                String account_id=website.getString("account_id");
                                                String name=website.getString("name");
                                                AppUtils.saveStringValue(requireContext(),ClsCommon.WEBSITE_ID, strId);
                                                AppUtils.saveStringValue(requireContext(),ClsCommon.WEBSITE_NAME, name);
                                                AppUtils.saveStringValue(requireContext(),ClsCommon.ACCOUNT_ID, account_id);
                                            }
                                            if (uDatas.has("links")) {
                                                JSONObject linkData = uDatas.getJSONObject("links");
                                                if (linkData.length()!=0){
                                                    Iterator iter = linkData.keys();
                                                    while(iter.hasNext()) {
                                                        String key = (String)iter.next();
                                                        JSONObject valueData = linkData.getJSONObject(key);
                                                        QuickLinkData quickLinkData = new QuickLinkData();
                                                        quickLinkData.setName(valueData.getString("name"));
                                                        quickLinkData.setText(valueData.getString("text"));
                                                        quickLinkData.setUrl(valueData.getString("url"));
                                                        quickLinkData.setFa(valueData.getString("fa"));
                                                        quickLinkData.setIcon(valueData.getString("icon"));
                                                        quickLinkData.setIconright(valueData.getString("icon-right"));
                                                        quickLinkDataList.add(quickLinkData);
                                                    }
                                                }



                                                if (quickLinkDataList.size() != 0 && isQuickLinks) {
                                                    mBinding.clQuickLink.setVisibility(View.VISIBLE);
                                                    quickLinksAdapter = new QuickLinksAdapter(quickLinkDataList);

                                                    LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
                                                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                                                    mBinding.rvQuickLinks.setLayoutManager(layoutManager);
                                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( mBinding.rvQuickLinks.getContext(), layoutManager.getOrientation());
                                                    mBinding.rvQuickLinks.addItemDecoration(dividerItemDecoration);
                                                    mBinding.rvQuickLinks.setAdapter(quickLinksAdapter);
                                                }else {
                                                    mBinding.clQuickLink.setVisibility(View.GONE);
                                                }
                                            }

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                Intent intent=new Intent(requireContext(), SessionExpiredActivity.class);
                                startActivity(intent);
                                // Toast.makeText(context, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }

                            new getNotificatioCount(requireContext()).execute();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBinding.horizontalProgress.setVisibility(View.INVISIBLE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireActivity()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    class getNotificatioCount extends AsyncTask<String, Void, Boolean> {


        public getNotificatioCount(Context context) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.horizontalProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d("Method Call", "getHomeData");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.NOTIFICATION_COUNT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mBinding.horizontalProgress.setVisibility(View.INVISIBLE);


                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (jsonObject.has("count")) {
                                        String count = jsonObject.getString("count");
                                        String unreadcount = jsonObject.getString("unread_count");
                                        if (unreadcount != null && unreadcount.trim().length() > 0 && !unreadcount.equals("0")) {
                                            BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_notification);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(Color.parseColor("#f1592a"));
                                        } else {
                                            BadgeDrawable NotifiationBadge = bottomNavigationView.getOrCreateBadge(R.id.ic_notification);
                                            NotifiationBadge.setNumber(Integer.parseInt(unreadcount));
                                            NotifiationBadge.setBackgroundColor(Color.parseColor("#00000000"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(requireContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                            new getSliderImages(requireContext()).execute();
                            new getHomePageData(requireContext()).execute();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    if (pd != null && pd.isShowing() && !getActivity().isFinishing())
//                        pd.dismiss();
                    mBinding.horizontalProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(requireContext(), "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireActivity()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }


    class getSliderImages extends AsyncTask<String, Void, Boolean> {


        public getSliderImages(Context context) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.horizontalProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GET_SLIDER_DATA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mBinding.horizontalProgress.setVisibility(View.INVISIBLE);

                            if (response != null && !response.isEmpty() && !response.contains("<!DOCTYPE html>")) {
                                ArrayList<LearnModel> learnModels = new Gson().fromJson(response, new TypeToken<ArrayList<LearnModel>>() {}.getType());
                                if (learnModels != null) {
                                    if (learnModels.get(0).getSettings()!=null && !learnModels.get(0).getSettings().getContent().isEmpty()){
                                        mBinding.clSlider.setVisibility(View.VISIBLE);
                                        mViewPagerAdapter= new ViewPagerAdapter(requireContext(),learnModels.get(0).getSettings().getContent());

                                        mBinding.viewPagerMain.setAdapter(mViewPagerAdapter);

                                        mBinding.ivPrevious.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int currentItem = mBinding.viewPagerMain.getCurrentItem();
                                                if (currentItem > 0){
                                                    currentItem--;
                                                }
                                                mBinding.viewPagerMain.setCurrentItem(currentItem);
                                            }
                                        });

                                        mBinding.ivNext.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int currentItem = mBinding.viewPagerMain.getCurrentItem();
                                                if (currentItem <= learnModels.get(0).getSettings().getContent().size()){
                                                    currentItem++;
                                                }
                                                mBinding.viewPagerMain.setCurrentItem(currentItem);
                                            }
                                        });

                                        mBinding.btnLearnMore.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent=new Intent(requireContext(), WebviewActivity.class);
                                                String id =learnModels.get(0).getSettings().getContent().get( mBinding.viewPagerMain.getCurrentItem()).getId().toString();
                                                String title =learnModels.get(0).getSettings().getContent().get( mBinding.viewPagerMain.getCurrentItem()).getTitle().toString();
                                                String strUrl = "https://connect.dataczar.com/blog/+"+id+"+/+"+title+"+";
                                                intent.putExtra(ClsCommon.URL, strUrl);
                                                intent.putExtra(ClsCommon.WEBSITE, title);
                                                startActivity(intent);
                                            }
                                        });
                                    }else {
                                        mBinding.clSlider.setVisibility(View.GONE);
                                    }
                                } else {
                                    mBinding.clSlider.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                mBinding.clSlider.setVisibility(View.GONE);
                                Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    if (pd != null && pd.isShowing() && !getActivity().isFinishing())
//                        pd.dismiss();
                    mBinding.horizontalProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(requireContext(), "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireActivity()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }



    class getHomePageData extends AsyncTask<String, Void, Boolean> {


        public getHomePageData(Context context) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.horizontalProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GET_HOME_PAGE_DATA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mBinding.horizontalProgress.setVisibility(View.INVISIBLE);

                            if (response != null && !response.isEmpty() && !response.contains("<!DOCTYPE html>")) {
                                ArrayList<GetHomePageResponse> homePageResponses = new Gson().fromJson(response, new TypeToken<ArrayList<GetHomePageResponse>>() {}.getType());
                                if (homePageResponses != null && !homePageResponses.isEmpty()) {
                                    HomePageListAdapter homePageListAdapter=new HomePageListAdapter(homePageResponses);
                                    mBinding.rvHomePage.setAdapter(homePageListAdapter);
                                } else {
                                    mBinding.rvHomePage.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                mBinding.rvHomePage.setVisibility(View.GONE);
                                Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    if (pd != null && pd.isShowing() && !getActivity().isFinishing())
//                        pd.dismiss();
                    mBinding.horizontalProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(requireContext(), "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireActivity()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }



}
