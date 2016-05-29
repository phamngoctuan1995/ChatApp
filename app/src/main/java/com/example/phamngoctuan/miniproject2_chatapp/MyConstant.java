package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by phamngoctuan on 28/05/2016.
 */
public class MyConstant {
    public static final int REQUEST_SIGNUP = 0;
    public static final String SETTING_REF = "setting_ref";
    public static final String ACCOUNT_REF = "account_ref";
    public static final int FOLLOW_TAB = 1;
    public static final int CHAT_TAB = 2;
    public static final int SEARCH_TAB = 0;

    public static ArrayList<PersonInfo> _searchList = new ArrayList<>();
    public static ArrayList<PersonInfo> _followList = new ArrayList<>();
    public static ArrayList<PersonInfo> _chat = new ArrayList<>();
    public static HashMap<String, PersonInfo> _followMap = new HashMap<String, PersonInfo>();

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

    static void makeFollowMap()
    {
        _followMap.clear();
        for (PersonInfo info : _followList)
            _followMap.put(info._nickname, info);
    }

    static void initStupData()
    {
        _followList.add(new PersonInfo("Pham Ngoc Tuan", "a"
                , "http://www.gravatar.com/avatar/2f8a4520237e064ada5a2b310323e2f7?s=290&20160529", 20, PersonInfo.ONLINE));
        _followList.add(new PersonInfo("Pham Ngoc Tuan", "b"
                , "http://www.gravatar.com/avatar/2f8a4520237e064ada5a2b310323e2f7?s=290&20160529", 20, PersonInfo.OFFLINE));
        _followList.add(new PersonInfo("Pham Ngoc Tuan", "c"
                , "http://www.gravatar.com/avatar/2f8a4520237e064ada5a2b310323e2f7?s=290&20160529", 20, PersonInfo.ONLINE));
        _followList.add(new PersonInfo("PNguyen Hoang Phuong", "nhphuongltv"
                , "http://www.gravatar.com/avatar/2f8a4520237e064ada5a2b310323e2f7?s=290&20160529", 0, PersonInfo.OFFLINE));
        _followList.add(new PersonInfo("Pham Ngoc Tuan", "d"
                , "http://www.gravatar.com/avatar/2f8a4520237e064ada5a2b310323e2f7?s=290&20160529", 100, PersonInfo.ONLINE));
        _followList.add(new PersonInfo("Pham Ngoc Tuan", "e"
                , "http://www.gravatar.com/avatar/2f8a4520237e064ada5a2b310323e2f7?s=290&20160529", 20, PersonInfo.ONLINE));
        makeFollowMap();
    }
}
