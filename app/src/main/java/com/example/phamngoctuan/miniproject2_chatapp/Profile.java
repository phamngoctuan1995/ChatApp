package com.example.phamngoctuan.miniproject2_chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    CircleImage _imAvatar;
    TextView _tvName, _tvNickname, _tvPlace, _tvJoin, _tvRank;

    void initView()
    {
        _imAvatar = (CircleImage) findViewById(R.id.imv_avatar);
        _tvName = (TextView) findViewById(R.id.tv_nameProfile);
        _tvNickname = (TextView) findViewById(R.id.tv_nicknameProfile);
        _tvPlace = (TextView) findViewById(R.id.tv_place);
        _tvJoin = (TextView) findViewById(R.id.tv_time);
        _tvRank = (TextView) findViewById(R.id.tv_rank);

        PersonInfo info = MyConstant.myAccount._info;

        Picasso.with(this).load(info._avatar).error(R.drawable.ic_notification).into(_imAvatar);
        _tvName.setText(info._name);
        _tvNickname.setText(info._nickname);
        _tvPlace.setText(MyConstant.myAccount._place);
        _tvJoin.setText(MyConstant.myAccount._datejoin);
        _tvRank.setText(MyConstant.myAccount._rank);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
    }
}
