package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class SearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView _rcv;
    SwipeRefreshLayout _refreshLayout;
    ListPersonAdapter _adapter;

    void initView()
    {
        _rcv = (RecyclerView) findViewById(R.id.rcv_listsearch);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        _rcv.setLayoutManager(llm);

        _refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        _refreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        _refreshLayout.setOnRefreshListener(this);
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
            return true;
        }
        if (id == R.id.action_profile) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

    }
}
