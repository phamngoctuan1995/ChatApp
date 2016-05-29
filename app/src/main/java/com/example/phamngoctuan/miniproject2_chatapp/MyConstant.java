package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by phamngoctuan on 28/05/2016.
 */
public class MyConstant {
    public static final int REQUEST_SIGNUP = 0;
    public static final String SETTING_REF = "setting_ref";
    public static final String ACCOUNT_REF = "account_ref";

    static boolean checkInternetAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }
}
