package com.example.phamngoctuan.miniproject2_chatapp;

import java.util.HashMap;

/**
 * Created by phamngoctuan on 29/05/2016.
 */
public class AccountInfo
{
    String _password, _place, _datejoin, _rank;
    PersonInfo _info = null;
    HashMap<String, String> _follow = null;
    HashMap<String, Long> _privateChat = null;
    HashMap<String, String> _problem = null;

    AccountInfo()
    {
    }

    AccountInfo(String pass, String place, String date, String rank
            , PersonInfo info, HashMap<String, String> fl, HashMap<String, Long> cp, HashMap<String, String> pro)
    {
        _place = place;
        _datejoin = date;
        _rank = rank;
        _password = pass;
        _info = info;
        _follow = fl;
        _privateChat = cp;
        _problem = pro;
    }
}

class PersonInfo
{
    String _name, _nickname, _avatar;
    int _follower, _status;

    static int ONLINE = 1;
    static int OFFLINE = 0;

    PersonInfo() {}
    PersonInfo(String name, String nn, String ava, int fl, int stt)
    {
        _name = name;
        _nickname = nn;
        _avatar = ava;
        _follower = fl;
        _status = stt;
    }
}

class ChatRecord
{
    String _message;
    int _position;

    ChatRecord(String mess, int pos)
    {
        _message = mess;
        _position = pos;
    }

    public ChatRecord() {

    }
}