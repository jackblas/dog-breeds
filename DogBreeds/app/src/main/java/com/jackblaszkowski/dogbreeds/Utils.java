package com.jackblaszkowski.dogbreeds;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class Utils {


    public static final int STATUS_SERVER_OK = 0;
    public static final int STATUS_SERVER_ERROR = 1;
    public static final int STATUS_NO_CONNECTION = 2;
    public static final int STATUS_TIMED_OUT = 3;


    public static boolean isOnline(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }


    public static int getServerStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_server_status_key), STATUS_SERVER_OK);

    }

    public static void setServerStatus(Context context, int status) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(context.getString(R.string.pref_server_status_key), status);
        spe.apply();
    }
}
