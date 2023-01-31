package com.dataczar.main.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
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
import com.dataczar.main.activity.WSMethods;
import com.dataczar.main.activity.WebviewLP;
import com.dataczar.main.adapter.QuickLinksAdapter;
import com.dataczar.main.model.QuickLinkData;
import com.dataczar.main.utils.ObservableWebView;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = WebviewLP.class.getSimpleName();
    Context context;
    WebView myWebView;
    String Title;
    ClsCommon clsCommon;
    ProgressDialog pd;
    RequestQueue requestQueue;
    BottomNavigationView bottomNavigationView;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private int requestCode;
    private int resultCode;
    private Intent data;
    private ArrayList<QuickLinkData> quickLinkDataList = new ArrayList<>();
    private QuickLinksAdapter quickLinksAdapter;
    private RecyclerView rvQuickLinks;
    private CardView clQuickLink;
    private ConstraintLayout llLinks;
    private NestedScrollView scrollview;
    private ImageView ivExpand;
    private boolean isQuickLinks=false;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;

    public HomeFragment() {
    }

    public HomeFragment(Context context, String Title, BottomNavigationView bottomNavigationView, ImageView imgSettingMenu,ConstraintLayout llLinks,ImageView ivExpand,boolean isQuickLinks) {
        this.context = context;
        this.Title = Title;
        clsCommon = new ClsCommon(context);
        this.bottomNavigationView = bottomNavigationView;
        this.ivExpand = ivExpand;
        this.llLinks=llLinks;
        this.isQuickLinks=isQuickLinks;
        imgSettingMenu.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_dashboard, container, false);

        myWebView =(WebView)view.findViewById(R.id.webview);
        rvQuickLinks = view.findViewById(R.id.rvQuickLinks);
        clQuickLink = view.findViewById(R.id.clQuickLink);
        scrollview = view.findViewById(R.id.scrollview);
        sharedPref = getContext().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (context == null)
            this.context = getActivity().getApplicationContext();

        requestQueue = Volley.newRequestQueue(context);
        initwebview();

        pd = new ProgressDialog(context, R.style.ProgressDialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.setCancelable(true);
                if (pd != null && !pd.isShowing()) //&& !getActivity().isFinishing())
                    pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (pd.isShowing()) //&& !getActivity().isFinishing())
                    pd.dismiss();
            }
        });

        if (isQuickLinks){
            clQuickLink.setVisibility(View.VISIBLE);
        }else {
            clQuickLink.setVisibility(View.GONE);
        }
        new getHomeData(context).execute();

        return view;
    }

    private void initwebview() {
        myWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("Chrome/56.0.0.0 Mobile");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(myWebView, true);
        cookieManager.acceptCookie();

        String[] cookies = getCookie().split(";");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(WSMethods.WSURL, getCookie());

        for (String cookiekist : cookies) {
            cookieManager.setCookie(WSMethods.WSURL, cookiekist);
        }

        String cookie = cookieManager.getCookie(WSMethods.WSURL);


        if (Title == ClsCommon.DASHBOARD)
            myWebView.loadUrl(WSMethods.DashboardPageUrl, map);

        else if (Title == ClsCommon.ADDPOST)
            myWebView.loadUrl(WSMethods.AddPostPageUrl, map);

        else if (Title == ClsCommon.USERSETTINGS)
            myWebView.loadUrl(WSMethods.UserSettings, map);

        else if (Title == ClsCommon.MANAGEACCOUNT)
            myWebView.loadUrl(WSMethods.ManageAccount, map);

        else if (Title == ClsCommon.LEGAL)
            myWebView.loadUrl(WSMethods.Legal, map);

        else if (Title == ClsCommon.HELP)
            myWebView.loadUrl(WSMethods.Help, map);

        else if (Title == ClsCommon.BILLINGS)
            myWebView.loadUrl(WSMethods.Billings, map);

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);

                getActivity().runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        Log.i("Dataczar", "|> onPermissionRequest run");
                        request.grant(request.getResources());
                    }// run
                });
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);

                // Double check that we don't have any existing callbacks
                if (mFilePathCallback != null)
                    mFilePathCallback.onReceiveValue(null);


                mFilePathCallback = filePathCallback;

                // Set up the intent to get an existing image
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                // Set up the intents for the Intent chooser
                Intent[] intentArray = new Intent[0];


                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }
        });

        llLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clQuickLink.getVisibility()==View.VISIBLE){
                    ivExpand.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_expand_black));
                    clQuickLink.setVisibility(View.GONE);
                }else {
                    ivExpand.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_expand_less));
                    clQuickLink.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }

    @Override
    public void onStart() {
        super.onStart();
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.ic_home);

        if (Title == ClsCommon.ADDPOST)
            item = bottomNavigationView.getMenu().findItem(R.id.ic_addpost);

        item.setChecked(true);
    }

    public String getCookie() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return Cookie;
    }

    class getNotificatioCount extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;

        public getNotificatioCount(Context context) {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.setCancelable(false);
            if (pd != null && !pd.isShowing() && !getActivity().isFinishing()) {
                pd.show();
            }

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
                            if (pd != null && pd.isShowing() && !getActivity().isFinishing())
                                pd.dismiss();

                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (jsonObject.has("count")) {
                                        String count = jsonObject.getString("count");
                                        String unreadcount = jsonObject.getString("unread_count");
                                        editor.putString(ClsCommon.NOTIFICATION_COUNT, unreadcount);
                                        editor.apply();
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
                                Toast.makeText(context, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing() && !getActivity().isFinishing())
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

            Log.d("Method Call", "getHomeData");

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

                                            /*if (linkData.has("started")) {

                                                JSONObject startedData = linkData.getJSONObject("started");
                                                QuickLinkData quickLinkData = new QuickLinkData();
                                                quickLinkData.setName(startedData.getString("name"));
                                                quickLinkData.setText(startedData.getString("text"));
                                                quickLinkData.setUrl(startedData.getString("url"));
                                                quickLinkData.setFa(startedData.getString("fa"));
                                                quickLinkData.setIcon(startedData.getString("icon"));
                                                quickLinkData.setIconright(startedData.getString("icon-right"));
                                                quickLinkDataList.add(quickLinkData);
                                            }


                                            if (linkData.has("preview")) {
                                                JSONObject previewData = linkData.getJSONObject("preview");
                                                QuickLinkData quickLinkData = new QuickLinkData();
                                                quickLinkData.setName(previewData.getString("name"));
                                                quickLinkData.setText(previewData.getString("text"));
                                                quickLinkData.setUrl(previewData.getString("url"));
                                                quickLinkData.setFa(previewData.getString("fa"));
                                                quickLinkData.setIcon(previewData.getString("icon"));
                                                quickLinkData.setIconright(previewData.getString("icon-right"));
                                                quickLinkDataList.add(quickLinkData);
                                            }

                                            if (linkData.has("edit")) {
                                                JSONObject editData = linkData.getJSONObject("edit");
                                                QuickLinkData quickLinkData = new QuickLinkData();
                                                quickLinkData.setName(editData.getString("name"));
                                                quickLinkData.setText(editData.getString("text"));
                                                quickLinkData.setUrl(editData.getString("url"));
                                                quickLinkData.setFa(editData.getString("fa"));
                                                quickLinkData.setIcon(editData.getString("icon"));
                                                quickLinkData.setIconright(editData.getString("icon-right"));
                                                quickLinkDataList.add(quickLinkData);
                                            }*/

                                            if (quickLinkDataList.size() != 0 && isQuickLinks) {
                                                clQuickLink.setVisibility(View.VISIBLE);
                                                quickLinksAdapter = new QuickLinksAdapter(quickLinkDataList);

                                                LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
                                                layoutManager.setOrientation(RecyclerView.VERTICAL);
                                                rvQuickLinks.setLayoutManager(layoutManager);
                                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvQuickLinks.getContext(), layoutManager.getOrientation());
                                                rvQuickLinks.addItemDecoration(dividerItemDecoration);
                                                rvQuickLinks.setAdapter(quickLinksAdapter);
                                            }else {
                                                clQuickLink.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(context, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }

                            new getNotificatioCount(context).execute();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

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

}