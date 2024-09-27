/*
 * Copyright (c)  to Samrt Sense . Ai on 2022.
 */

package com.dataczar.main.activity;

import static com.dataczar.main.utils.AppUtils.getCookie;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dataczar.R;
import com.dataczar.databinding.NewWebviewBinding;
import com.dataczar.main.model.GetHomePageResponse;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.gson.Gson;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NewWebviewActivity extends AppCompatActivity
{

    Context context;
   private NewWebviewBinding mBinding;
   private GetHomePageResponse homePageResponse;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding= DataBindingUtil.setContentView(this,R.layout.new_webview);
        context = NewWebviewActivity.this;

        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String strData = getIntent().getStringExtra("HOME_PAGE");
        if (strData!=null){
            homePageResponse = new Gson().fromJson(strData,GetHomePageResponse.class);


            mBinding.tvTitle.setText(homePageResponse.getTitle());
            mBinding.tvDescription.setHtml(homePageResponse.getContent());

            Glide.with(mBinding.getRoot().getContext())
                    .load(homePageResponse.getImage())
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .centerInside()
                    .into(mBinding.ivImage);

            String date=homePageResponse.getCreatedAt();
            SimpleDateFormat dateFormatprev = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = null;
            try {
                d = dateFormatprev.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String changedDate = dateFormat.format(d);
            mBinding.tvDate.setText("Posted "+changedDate);
        }
    }
}

