package com.dataczar.main.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferenceManager {
    private SharedPreferences mSharedPreferences;

    /**
     * default constructor
     *
     * @param context context
     */
    public AppPreferenceManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * stores string key value pair in app preference
     *
     * @param key value name to be stored
     * @param res value to be stored
     */
    public void saveStringResponse(String key, String res) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, res);
        editor.apply();
    }


    /**
     * fetch string value from preference
     *
     * @param key key of resource to be fetched
     * @return value of key
     */
    public String getStringResponse(String key) {
        return mSharedPreferences.getString(key, "");
    }


    /**
     * stores boolean key value pair in app preference
     *
     * @param key   value name to be stored
     * @param value value to be stored
     */
    public void saveBooleanResponse(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void clearData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * removes all boolean responses from preference
     */
    public void removeBooleanResponse() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * fetch boolean data from given key
     *
     * @param key key of resource
     * @return boolean value
     */
    public boolean getBooleanResponse(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

}
