package com.example.phamngoctuan.miniproject2_chatapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by phamngoctuan on 29/05/2016.
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SpojMainFragment("http://www.spoj.com");
            case 1:
                ListPersonFragment _followFragment = new ListPersonFragment(MyConstant._followList, MyConstant.FOLLOW_TAB);
                MyConstant._followFragment = new WeakReference<ListPersonFragment>(_followFragment);
                return _followFragment;
            case 2:
                ListPersonFragment _chatFragment = new ListPersonFragment(MyConstant._chatList, MyConstant.CHAT_TAB);
                MyConstant._chatFragment = new WeakReference<ListPersonFragment>(_chatFragment);
                return _chatFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Spoj";
            case 1:
                return "Follow";
            case 2:
                return "chat";
            default:
                return "Tab";
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
