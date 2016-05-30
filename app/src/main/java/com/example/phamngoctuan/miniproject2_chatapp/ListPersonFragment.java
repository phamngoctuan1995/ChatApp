package com.example.phamngoctuan.miniproject2_chatapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * Created by phamngoctuan on 29/05/2016.
 */
public class ListPersonFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView _rcv;
    SwipeRefreshLayout _refreshLayout;
    ArrayList<PersonInfo> _data;
    int _tabType;
    ListPersonAdapter _adapter;

    ListPersonFragment(ArrayList<PersonInfo> dt, int tabType)
    {
        _data = dt;
        _tabType = tabType;
    }

    void setAdapter()
    {
        _adapter = new ListPersonAdapter(getContext(), _data, _tabType);
        if (_tabType == MyConstant.CHAT_TAB)
            MyConstant._chatAdapter = new WeakReference<ListPersonAdapter>(_adapter);
        if (_tabType == MyConstant.FOLLOW_TAB)
            MyConstant._followAdapter = new WeakReference<ListPersonAdapter>(_adapter);
        _rcv.setAdapter(_adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listfriend_fragent, container, false);

        _rcv = (RecyclerView) rootView.findViewById(R.id.rcv_listfriend);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        _rcv.setLayoutManager(llm);


        setAdapter();

        _refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        _refreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        _refreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onRefresh() {
        doOnRefresh();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _refreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    public void doOnRefresh()
    {
        _adapter = new ListPersonAdapter(getContext(), _data, _tabType);
        _rcv.setAdapter(_adapter);
    }
}
