package com.example.Diary2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.security.PublicKey;

public class PreferenceUtils {

    static PublicKey key;

    PreferenceUtils () {

    }

    public static boolean savePassword (String password, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(String.valueOf(key), true);
        editor.apply();
        return true;

    }
    
    public static String getPassword(Context context) {
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(String.valueOf(key), null);
        
    }

}
