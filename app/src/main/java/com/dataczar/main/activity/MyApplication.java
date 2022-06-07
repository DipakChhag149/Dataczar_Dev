/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.activity;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.dataczar.main.viewmodel.ClsCommon;

import java.util.ArrayList;
import java.util.Map;

public class MyApplication extends Application
{
    static ArrayList<Map<String, String>> userprofile = new ArrayList<>();

    private MyApplication()
    {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //AppThemeSelector.checkAndShowTheme(getApplicationContext());
    }
}
