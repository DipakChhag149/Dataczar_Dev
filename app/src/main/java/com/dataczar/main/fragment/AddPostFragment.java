package com.dataczar.main.fragment;

import static com.dataczar.main.utils.AppUtils.getCookie;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.databinding.FragmentAddPostBinding;
import com.dataczar.main.activity.WSMethods;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddPostFragment extends Fragment {

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    Context context;
    ProgressDialog pd;
    RequestQueue requestQueue;
    BottomNavigationView bottomNavigationView;
    ImageView imgSettingMenu;
    private FragmentAddPostBinding mBinding;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    CustomHorizontalProgressBar horizontalProgress;
    public AddPostFragment(Context context, BottomNavigationView bottomNavigationView, ImageView imgSettingMenu) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.bottomNavigationView = bottomNavigationView;
        this.imgSettingMenu = imgSettingMenu;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_post, container, false);
        init();
        return mBinding.getRoot();
    }

    private void init() {
        if (context == null)
            this.context = getActivity().getApplicationContext();

        requestQueue = Volley.newRequestQueue(context);
        imgSettingMenu.setVisibility(View.INVISIBLE);
        horizontalProgress = mBinding.getRoot().findViewById(R.id.horizontalProgress);
        pd = new ProgressDialog(context, R.style.ProgressDialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        mBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                pd.setCancelable(true);
//                if (pd != null && !pd.isShowing()) //&& !getActivity().isFinishing())
//                    pd.show();
                horizontalProgress.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                horizontalProgress.setVisibility(View.INVISIBLE);
//                if (pd.isShowing()) //&& !getActivity().isFinishing())
//                    pd.dismiss();
            }
        });


        initwebview();
        new getNotificatioCount(getContext()).execute();
    }

    private void initwebview() {
        mBinding.webview.setWebViewClient(new WebViewClient());

        WebSettings webSettings = mBinding.webview.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("Chrome/56.0.0.0 Mobile");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(mBinding.webview, true);
        cookieManager.acceptCookie();

        String[] cookies = getCookie(requireContext()).split(";");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(WSMethods.WSURL, getCookie(requireContext()));

        for (String cookiekist : cookies) {
            cookieManager.setCookie(WSMethods.WSURL, cookiekist);
        }

        String cookie = cookieManager.getCookie(WSMethods.WSURL);
        mBinding.webview.loadUrl(WSMethods.AddPostPageUrl, map);


        mBinding.webview.setWebChromeClient(new WebChromeClient() {

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

    class getNotificatioCount extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;

        public getNotificatioCount(Context context) {
//            pd = new ProgressDialog(context, R.style.ProgressDialog);
//            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            horizontalProgress.setVisibility(View.VISIBLE);
//            pd.setCancelable(false);
//            if (pd != null && !pd.isShowing() && !getActivity().isFinishing()) {
//                pd.show();
//            }

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
                            horizontalProgress.setVisibility(View.INVISIBLE);

//                            if (pd != null && pd.isShowing() && !getActivity().isFinishing())
//                                pd.dismiss();

                            if (response != null && !response.isEmpty() && !response.contains("<!DOCTYPE html>")) {
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
                                Toast.makeText(context, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    horizontalProgress.setVisibility(View.INVISIBLE);

                    Toast.makeText(context, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireContext()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }
}
