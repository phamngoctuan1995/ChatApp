package com.example.phamngoctuan.miniproject2_chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by phamngoctuan on 28/05/2016.
 */
public class MyConstant {
    public static final String SETTING_REF = "setting_ref";
    public static final String ACCOUNT_REF = "account_ref";
    public static final int FOLLOW_TAB = 1;
    public static final int CHAT_TAB = 2;
    public static final int SEARCH_TAB = 0;
    public static Firebase fb_root = null;
    public static Firebase fb_users = null;
    public static Firebase fb_problems = null;
    public static Firebase fb_chats = null;
    public static Firebase fb_myaccount = null;
    public static AccountInfo myAccount = null;
    public static final int SENDER = 0;
    public static final int RECIPIENT = 1;
    public static ProgressDialog _progressDialog;
    public static ChildEventListener _listener = null;

    public static ArrayList<PersonInfo> _searchList = new ArrayList<>();
    public static ArrayList<PersonInfo> _followList = new ArrayList<>();
    public static ArrayList<PersonInfo> _chatList = new ArrayList<>();
    public static HashSet<String> _followSet = new HashSet<>();

    public static WeakReference<ListPersonAdapter> _followAdapter, _chatAdapter;

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

    static void makeFollowSet()
    {
        _followSet.clear();
        for (String nickname: myAccount._follow.keySet())
            _followSet.add(nickname);
    }

    static void initFollow()
    {
        _followList.clear();
        if (myAccount._follow != null)
        {
            for (String key : myAccount._follow.keySet())
                addPersonList(key, _followList, _followAdapter);
            makeFollowSet();
        }
    }

    static void initChat()
    {
        _chatList.clear();
        if (myAccount._privateChat != null)
            for (String key : myAccount._privateChat.keySet())
                addPersonList(key, _chatList, _chatAdapter);
    }

    static void initData() {
        initFollow();
        initChat();

        _listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (myAccount._privateChat == null)
                    myAccount._privateChat = new HashMap<String, Long>();
                String key = dataSnapshot.getKey();

                if (!myAccount._privateChat.containsKey(key)) {
                    Long position = (Long)dataSnapshot.getValue();
                    myAccount._privateChat.put(key, position);
                    addPersonList(key, _chatList, _chatAdapter);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        fb_myaccount.child("_privateChat").addChildEventListener(_listener);
    }

    static public void addPersonList(String key, final ArrayList<PersonInfo> _list, final WeakReference<ListPersonAdapter> adapterWeakReference)
    {
        fb_users.child(key).child("_info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    PersonInfo info = new PersonInfo();
                    info = dataSnapshot.getValue(PersonInfo.class);

                    ListPersonAdapter adapter = null;
                    if (adapterWeakReference != null)
                        adapter = adapterWeakReference.get();

                    if (adapter != null)
                        adapter.addPerson(info);
                    else
                        _list.add(info);
                }
                catch (Exception e)
                {
                    Log.d("debug", "Exception ondatachange");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    static public void doLogout(Activity activity)
    {
        Intent intent = new Intent(activity, LoginActivity.class);
        final WeakReference<Intent> intentWeakReference = new WeakReference<Intent>(intent);
        final WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Activity activity = weakReference.get();
                Intent intent = intentWeakReference.get();
                if (activity == null)
                    return;

                doOnPostExecute(PersonInfo.OFFLINE);

                resetConstant();

                if (intent != null)
                   activity.startActivity(intent);

                dialog.dismiss();
                activity.finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static void resetConstant()
    {
        fb_myaccount.child("_privateChat").removeEventListener(_listener);
        _listener = null;
        myAccount = null;
        _followList.clear();
        _chatList.clear();
        _searchList.clear();
        _followSet.clear();
        fb_root = null;
        fb_users = null;
        fb_problems = null;
        fb_chats = null;
        fb_myaccount = null;
        _followAdapter = _chatAdapter = null;
        _progressDialog = null;
    }

    static public void doOnPostExecute(int status)
    {
        MyConstant.fb_myaccount = MyConstant.fb_users.child(MyConstant.myAccount._info._nickname);

        HashMap<String, String> problem = MyConstant.myAccount._problem;
        MyConstant.myAccount._info._status = status;
        PersonInfo info = MyConstant.myAccount._info;

        MyConstant.fb_myaccount.child("_info").child("_status").setValue(info._status);

        if (problem != null) {
            Map<String, Object> update = new HashMap<>();
            for (String key : problem.keySet())
                update.put(key + "/" + info._nickname, info._status);

            MyConstant.fb_problems.updateChildren(update);
        }

        if (status == PersonInfo.ONLINE)
            initData();
    }
}
