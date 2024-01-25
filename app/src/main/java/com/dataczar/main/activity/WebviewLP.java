/*
 * Copyright (c)  to Samrt Sense . Ai on 2022.
 */

package com.dataczar.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dataczar.R;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.viewmodel.ClsCommon;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class WebviewLP extends AppCompatActivity
{

    Context context;
    WebView myWebView;
    String title = "";
    ClsCommon clsCommon;
    CustomHorizontalProgressBar horizontalProgress;
    TextView tvTitle;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview);
        context = WebviewLP.this;

        ImageView imgBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        horizontalProgress = findViewById(R.id.horizontalProgress);

        if(getIntent()!= null)
        {
            title = getIntent().getStringExtra(ClsCommon.WEBSITE).toLowerCase();
            title = String.valueOf(title.charAt(0)).toUpperCase() + title.substring(1, title.length());
        }

        if(title.equals(ClsCommon.USERSETTINGS))
        {
            title = "User Profile";
        }
        else if(title.equals(ClsCommon.CHANGEPASS))
        {
            title = "Change Password";
        }
        else if(title.equals(ClsCommon.NOTIFICATION))
        {
            title = "Notification Settings";
        }
        else if(title.equals(ClsCommon.MANAGEACCOUNT))
        {
            title = "Manage Account";
        }
        else  if(title.equals(ClsCommon.BILLINGS))
        {
            title = "Billing";
        }
        else  if(title.equals(ClsCommon.LEGAL))
        {
            title = "Legal";
        }

        tvTitle.setText(title);


        // Reset the variable

        if(getIntent()!= null)
        {
            title = getIntent().getStringExtra(ClsCommon.WEBSITE);
        }

        myWebView = (WebView) findViewById(R.id.webview);

        initWebview();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        myWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if(!WebviewLP.this.isFinishing())
                {
                  horizontalProgress.setVisibility(View.VISIBLE);
                }else
                {

                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(!WebviewLP.this.isFinishing())
                    horizontalProgress.setVisibility(View.GONE);
            }

        });

    }

    private void initWebview()
    {
        myWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
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

        if (title.equals(ClsCommon.WEBSITE))
            myWebView.loadUrl(WSMethods.Website, map);

        else if (title.equals(ClsCommon.EMAIL))
            myWebView.loadUrl(WSMethods.Email, map);

        else if (title.equals(ClsCommon.LIST))
            myWebView.loadUrl(WSMethods.List, map);

        else if (title.equals(ClsCommon.DOMAIN))
            myWebView.loadUrl(WSMethods.Domain, map);

        else if (title.equals(ClsCommon.CONTENT))
            myWebView.loadUrl(WSMethods.Content, map);

        else if (title.equals(ClsCommon.HELP))
            myWebView.loadUrl(WSMethods.Help, map);

        else if (title.equals(ClsCommon.USERSETTINGS))
            myWebView.loadUrl(WSMethods.UserProfile, map);

        else if (title.equals(ClsCommon.CHANGEPASS))
            myWebView.loadUrl(WSMethods.ChangePassword, map);

        else if (title.equals(ClsCommon.NOTIFICATION))
            myWebView.loadUrl(WSMethods.NotificationSettings, map);

        else if (title.equals(ClsCommon.MANAGEACCOUNT))
            myWebView.loadUrl(WSMethods.ManageAccount, map);

        else if (title.equals(ClsCommon.LEGAL))
            myWebView.loadUrl(WSMethods.Legal, map);

        else if (title.equals(ClsCommon.BILLINGS))
            myWebView.loadUrl(WSMethods.Billings, map);

        else if(title.equals(ClsCommon.FORGOTPASS))
        {
            myWebView.loadUrl(WSMethods.URL_FORGOTPASS);
            tvTitle.setText("Forgot Password");
        }

        else if(title.equals(ClsCommon.SIGNUP)) {
            myWebView.loadUrl(WSMethods.URL_REGISTER);
            tvTitle.setText("Sign up");
        }


        myWebView.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);

                WebviewLP.this.runOnUiThread(new Runnable(){
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
                if(mFilePathCallback != null)
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
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data == null) {
                // If there is not data, then we may have taken a photo
                if(mCameraPhotoPath != null) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getCookie() {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return Cookie;
    }


}

