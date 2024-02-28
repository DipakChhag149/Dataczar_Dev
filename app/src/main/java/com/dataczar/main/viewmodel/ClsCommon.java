/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dataczar.R;
import com.dataczar.main.activity.Setting;
import com.google.android.material.snackbar.Snackbar;

public class ClsCommon
{


    public static final String FORGOTPASS       = "Forgotpassword";
    public static final String SIGNUP           = "Signup";
    public static final String LOGIN            = "Login";
    public static final String PREFDATA         = "Pref_User";
    public static final String COOKIE           = "Cookie";
    public static final String HEADER           = "Header";
    public static final String DASHBOARD        = "Dashboard";
    public static final String ADDPOST          = "Addpost";
    public static final String USERSETTINGS     = "Usersetting";
    public static final String CHANGEPASS       = "Changepassword";
    public static final String NOTIFICATION     = "Notification";
    public static final String MANAGEACCOUNT    = "Manageaccount";
    public static final String LEGAL            = "Legal";
    public static final String BILLINGS         = "Billing";
    public static final String WEBSITE          = "Website";
    public static final String URL          = "url";
    public static final String EMAIL            = "Emails";
    public static final String LIST             = "Lists";
    public static final String DOMAIN           = "Domains";
    public static final String CONTENT          = "Content";
    public static final String HELP             = "Help";
    public static final String FCM_TOKEN             = "fcm_token";
    public static final String NOTIFICATION_STATUS             = "notification_status";
    public static final String NOTIFICATION_OPEN             = "notification_open";
    public static final String NOTIFICATION_COUNT             = "notification_count";
    public static final String WEBSITE_ID             = "website_id";
    public static final String WEBSITE_NAME             = "website_name";
    public static final String ACCOUNT_ID             = "account_id";

    public static final String PROFILE = "Profile";
    public boolean isNotificationRead = false;

    Context context;

    public ClsCommon(Context context)
    {
        this.context = context;
    }

    public boolean checkConnection()
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return  networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void showSnackBar(boolean isConnected, View uiview)
    {
        String message = "";
        int color = Color.RED;

        // check condition
        if (!isConnected) {
            message = "Not Connected to Internet, Please check you connection and try again.";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar.make(uiview, message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();

        TextView textView = view.findViewById(R.id.snackbar_text);

        textView.setTextColor(color);
        snackbar.show();
    }

}
