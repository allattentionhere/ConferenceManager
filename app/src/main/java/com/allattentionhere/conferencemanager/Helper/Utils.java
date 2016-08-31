package com.allattentionhere.conferencemanager.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by krupenghetiya on 8/30/16.
 */
public class Utils {
    public static String getUserType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.SHAREDPREF_USERTYPE, Constants.USERTYPE_DOCTOR);
    }

    public static long getUserEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(Constants.SHAREDPREF_USEREMAIL, 0);
    }

    public static void setUserType(Context context, String type) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Constants.SHAREDPREF_USERTYPE, type);
        editor.commit();
    }
    public static void setUserId(Context context, int id) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(Constants.SHAREDPREF_USERID, id);
        editor.commit();
    }

    public static Long getUserId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(Constants.SHAREDPREF_USERID,0);
    }

    public static void signOut(Context c){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.SHAREDPREF_USERID).apply();
    }


}
