package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements onPostExecuteCallback{

    private ViewPager _viewPager;
    private TabLayout _tabLayout;
    MainViewPagerAdapter _pagerAdapter;


    private void initFirebase()
    {
        Firebase.setAndroidContext(getApplicationContext());
        if (MyConstant.fb_root == null)
            MyConstant.fb_root = new Firebase("https://algchat.firebaseio.com/");
        if (MyConstant.fb_users == null)
            MyConstant.fb_users = MyConstant.fb_root.child("users");
        if (MyConstant.fb_problems == null)
            MyConstant.fb_problems = MyConstant.fb_root.child("problems");
        if (MyConstant.fb_chats == null)
            MyConstant.fb_chats = MyConstant.fb_root.child("chats");
    }

    private void initViewPager()
    {
        _tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        _tabLayout.addTab(_tabLayout.newTab().setText("Spoj"));
        _tabLayout.addTab(_tabLayout.newTab().setText("Follow"));
        _tabLayout.addTab(_tabLayout.newTab().setText("Chat"));
        _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        _pagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), _tabLayout.getTabCount());

        // Set up the ViewPager with the sections adapter.
        _viewPager = (ViewPager) findViewById(R.id.container);
        _viewPager.setAdapter(_pagerAdapter);

        _viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(_tabLayout));

        _tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                _viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void getIntentInfo(Intent intent)
    {
        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");

        Firebase account_ref = MyConstant.fb_users.child(username);
        final WeakReference<MainActivity> activityWeakReference = new WeakReference<MainActivity>(this);

        account_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity activity = activityWeakReference.get();
                if (activity == null)
                    return;

                if (!dataSnapshot.exists()) {
                    new registerAccountAsyctask(username, password, activity).execute();
                }
                else
                {
                    Log.d("debug", "Key: " + dataSnapshot.getKey() + "     value: " + dataSnapshot.getValue().toString());
                    new getAccountAsynctask(dataSnapshot, activity).execute();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();

        Intent intent = getIntent();

//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra("username", "nhphuongltv");
//        intent.putExtra("password", "12345aaA");

        if (intent != null)
            getIntentInfo(intent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        MyConstant.initStupData();
        initViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView;
        MenuItem search_item = menu.findItem(R.id.action_search);
        searchView = (SearchView) search_item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_logout) {
            doLogout();
            return true;
        }
        if (id == R.id.action_search)
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        doLogout();
    }

    void doLogout()
    {
        final Intent intent = new Intent(this, LoginActivity.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doOnPostExecute(PersonInfo.OFFLINE);
                startActivity(intent);
                dialog.dismiss();
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

    public void doOnPostExecute(int status)
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

    }
}

interface onPostExecuteCallback
{
    void doOnPostExecute(int status);
}

class registerAccountAsyctask extends AsyncTask<Void, Void, Void>
{
    String _username, _password;
    WeakReference<onPostExecuteCallback> _callbackWeakReference;

    registerAccountAsyctask(String user, String pass, onPostExecuteCallback cb)
    {
        _username = user;
        _password = pass;
        _callbackWeakReference = new WeakReference<onPostExecuteCallback>(cb);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Document document = Jsoup.connect("http://www.spoj.com/users/" + _username).get();

            Element div = document.getElementById("user-profile-left");
            String avatar = div.child(0).attr("src");
            String name = div.child(1).text();
//            String nickname = div.child(2).text();
            String nickname = _username;
            String place = div.child(3).text();
            String join = div.child(4).text();
            String rank = div.child(5).text();

            Element table = document.getElementById("user-profile-tables");
            Elements td = table.getElementsByTag("table").first().getElementsByTag("td");
            HashMap<String, String> problem = new HashMap<>();
            for (Element e : td) {
                String text = e.text();
                if (text != "")
                    problem.put(text, text);
            }

            if (problem.size() < 1)
                problem = null;

            PersonInfo personInfo = new PersonInfo(name, nickname, avatar, 0, PersonInfo.ONLINE);
            MyConstant.myAccount = new AccountInfo(_password, place, join, rank,
                    personInfo, null, null, problem);

            Firebase account_ref = MyConstant.fb_users.child(_username);
            account_ref.setValue(MyConstant.myAccount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onPostExecuteCallback callback = _callbackWeakReference.get();
        if (callback != null)
            callback.doOnPostExecute(PersonInfo.ONLINE);
    }
}

class getAccountAsynctask extends AsyncTask<Void, Void, Void>
{
    DataSnapshot _snapshot;
    WeakReference<onPostExecuteCallback> _callbackWeakReference;

    getAccountAsynctask(DataSnapshot snapshot, onPostExecuteCallback cb)
    {
        _snapshot = snapshot;
        _callbackWeakReference = new WeakReference<onPostExecuteCallback>(cb);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (MyConstant.myAccount == null)
            MyConstant.myAccount = new AccountInfo();
        MyConstant.myAccount._password = _snapshot.child("_password").getValue().toString();
        MyConstant.myAccount._rank = _snapshot.child("_rank").getValue().toString();
        MyConstant.myAccount._place = _snapshot.child("_place").getValue().toString();
        MyConstant.myAccount._datejoin = _snapshot.child("_datejoin").getValue().toString();
        if (MyConstant.myAccount._info == null)
            MyConstant.myAccount._info = new PersonInfo();
        MyConstant.myAccount._info = _snapshot.child("_info").getValue(PersonInfo.class);
        MyConstant.myAccount._problem = (HashMap<String, String>) _snapshot.child("_problem").getValue();
        MyConstant.myAccount._chatPrivate = (HashMap<String, ChatPrivate>) _snapshot.child("_chatPrivate").getValue();
        MyConstant.myAccount._follow = (HashMap<String, String>) _snapshot.child("_follow").getValue();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onPostExecuteCallback callback = _callbackWeakReference.get();
        if (callback != null)
            callback.doOnPostExecute(PersonInfo.ONLINE);
    }
}
