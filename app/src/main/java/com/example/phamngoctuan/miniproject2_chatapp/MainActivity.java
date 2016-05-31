package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity{

    private ViewPager _viewPager;
    private TabLayout _tabLayout;
    MainViewPagerAdapter _pagerAdapter;
    SearchView _searchView;;


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
        _viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ListPersonAdapter adapter = null;
                if (position == 1)
                    if (MyConstant._followAdapter != null)
                        adapter = MyConstant._followAdapter.get();
                if (position == 2)
                    if (MyConstant._chatAdapter != null)
                        adapter = MyConstant._chatAdapter.get();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

        account_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    new registerAccountAsyctask(username, password).execute();
                }
                else
                {
                    Log.d("debug", "Key: " + dataSnapshot.getKey() + "     value: " + dataSnapshot.getValue().toString());
                    new getAccountAsynctask(dataSnapshot, username, password).execute();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null)
            getIntentInfo(intent);

        initViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search_item = menu.findItem(R.id.action_search);
        _searchView = (SearchView) search_item.getActionView();
        final WeakReference<MainActivity> activityWeakReference = new WeakReference<MainActivity>(this);

        _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivity activity = activityWeakReference.get();
                _searchView.clearFocus();

                if (activity != null) {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    intent.putExtra("query", query.toUpperCase());
                    startActivity(intent);
                }
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
            MyConstant.doLogout(this);
            return true;
        }
        if (id == R.id.action_search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        MyConstant.doLogout(this);
    }
}

class registerAccountAsyctask extends AsyncTask<Void, Void, Void>
{
    String _username, _password;

    registerAccountAsyctask(String user, String pass)
    {
        _username = user;
        _password = pass;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Document document = Jsoup.connect("http://www.spoj.com/users/" + _username).get();

            Element div = document.getElementById("user-profile-left");
            String avatar = div.child(0).attr("src");
            String name = div.child(1).text();
            String nickname = _username;
            String place = div.child(3).text();
            String join = div.child(4).text();
            String rank = div.child(5).text();

            document = Jsoup.connect("http://vn.spoj.com/users/" + _username).get();
            Element table = document.getElementsByClass("col-md-9").first();
            Elements td = table.getElementsByTag("table").first().getElementsByTag("td");
            HashMap<String, String> problem = new HashMap<>();
            for (Element e : td) {
                String text = e.text();
                if (text != "")
                    problem.put(text, text);
            }

            if (problem.size() < 1)
                problem = null;

            if (MyConstant.myAccount == null) {
                PersonInfo personInfo = new PersonInfo(name, nickname, avatar, 0, PersonInfo.ONLINE);
                MyConstant.myAccount = new AccountInfo(_password, place, join, rank,
                        personInfo, null, null, problem);
            }
            else
            {
                MyConstant.myAccount._rank = rank;
                MyConstant.myAccount._datejoin = join;
                MyConstant.myAccount._password = _password;
                MyConstant.myAccount._place = place;
                MyConstant.myAccount._problem = problem;
                MyConstant.myAccount._info._avatar = avatar;
                MyConstant.myAccount._info._name = name;
                MyConstant.myAccount._info._status = PersonInfo.ONLINE;
            }

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
        MyConstant.doOnPostExecute(PersonInfo.ONLINE);
    }
}

class getAccountAsynctask extends AsyncTask<Void, Void, Void>
{
    DataSnapshot _snapshot;
    String _username, _password;

    getAccountAsynctask(DataSnapshot snapshot, String usr, String pw)
    {
        _snapshot = snapshot;
        _username = usr;
        _password = pw;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (MyConstant.myAccount == null)
            MyConstant.myAccount = new AccountInfo();

        if (MyConstant.myAccount._info == null)
            MyConstant.myAccount._info = new PersonInfo();
        MyConstant.myAccount._info = _snapshot.child("_info").getValue(PersonInfo.class);
        MyConstant.myAccount._privateChat = (HashMap<String, Long>) _snapshot.child("_privateChat").getValue();
        MyConstant.myAccount._follow = (HashMap<String, String>) _snapshot.child("_follow").getValue();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        new registerAccountAsyctask(_username, _password).execute();
    }
}
