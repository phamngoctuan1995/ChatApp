package com.example.phamngoctuan.miniproject2_chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    CircleImage _imAvatar;
    TextView _tvName, _tvNickname, _tvPlace, _tvJoin, _tvRank;

    void InitView()
    {
        _imAvatar = (CircleImage) findViewById(R.id.imv_avatar);
        _tvName = (TextView) findViewById(R.id.tv_nameProfile);
        _tvNickname = (TextView) findViewById(R.id.tv_nicknameProfile);
        _tvPlace = (TextView) findViewById(R.id.tv_place);
        _tvJoin = (TextView) findViewById(R.id.tv_time);
        _tvRank = (TextView) findViewById(R.id.tv_rank);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
