package com.example.phamngoctuan.miniproject2_chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.ref.WeakReference;

public class SearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView _rcv;
    SwipeRefreshLayout _refreshLayout;
    ListPersonAdapter _adapter;
    String _query;
    ValueEventListener _listener;
    Firebase _searchRef;

    void initView()
    {
        _rcv = (RecyclerView) findViewById(R.id.rcv_listsearch);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        _rcv.setLayoutManager(llm);

        _refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        _refreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        _refreshLayout.setOnRefreshListener(this);

        setAdapter();
    }

    void setAdapter()
    {
        _adapter = new ListPersonAdapter(this, MyConstant._searchList, MyConstant.SEARCH_TAB);
        _rcv.setAdapter(_adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        initView();

        Intent intent = getIntent();
        _query = intent.getStringExtra("query");
        MyConstant._searchList.clear();

        _listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot person : dataSnapshot.getChildren())
                    {
                        try {
                            if (person.getKey().equals(MyConstant.myAccount._info._nickname))
                                continue;
                            MyConstant.addPersonList(person.getKey(), MyConstant._searchList, new WeakReference<ListPersonAdapter>(_adapter));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Log.d("debug", "Exception Searchacti");
                        }
                    }
                else
                    Toast.makeText(getApplicationContext(), "Sorry, can't find anyone solve this problem...", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        _searchRef = MyConstant.fb_problems.child(_query);
        _searchRef.addValueEventListener(_listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_logout) {
            MyConstant.doLogout(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (!MyConstant.checkInternetAvailable(getApplicationContext()))
        {
            Toast.makeText(this, "No internet connection!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        MyConstant._searchList.clear();
        _adapter.notifyDataSetChanged();

        _searchRef.removeEventListener(_listener);
        _searchRef.addValueEventListener(_listener);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _refreshLayout.setRefreshing(false);
            }
        }, 1400);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _searchRef.removeEventListener(_listener);
    }
}
