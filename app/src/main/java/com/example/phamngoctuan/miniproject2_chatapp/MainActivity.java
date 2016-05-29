package com.example.phamngoctuan.miniproject2_chatapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ViewPager _viewPager;
    private TabLayout tabLayout;

    private void initViewPager()
    {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Spoj"));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorite"));
        tabLayout.addTab(tabLayout.newTab().setText("Old chat"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        _pagerAdapter = new PagerAdapter(getSupportFragmentManager(), 3);
//
//        // Set up the ViewPager with the sections adapter.
//        _viewPager = (ViewPager) findViewById(R.id.container);
//        _viewPager.setAdapter(_pagerAdapter);
//
//        _viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                _viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPager();
    }
}
