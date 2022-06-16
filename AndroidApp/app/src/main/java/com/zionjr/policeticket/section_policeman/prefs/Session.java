package com.zionjr.policeticket.section_policeman.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.zionjr.policeticket.model.users.Policeman;

public class Session {

    private static final String PREFS_NAME = "policeman";
    private static final String EMAIL = "saved_email";
    public static Policeman user;

    public static void setEmail(Context context, String email) {

        SharedPreferences sharedPreferences = context
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public static String getEmail(Context context) {

        SharedPreferences sharedPreferences = context
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(EMAIL, null);
    }

    public static void destroy(Context context) {

        SharedPreferences sharedPreferences = context
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
    }
}
