package com.example.phamngoctuan.miniproject2_chatapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
                return new ListPersonFragment(MyConstant._followList, MyConstant.FOLLOW_TAB);
//            case 2:
//                return new FragmentGridBook(constant.FAVORITE_DATA);
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
            default:
                return "Tab";
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
