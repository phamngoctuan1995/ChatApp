package com.example.phamngoctuan.miniproject2_chatapp;

import java.util.Map;

/**
 * Created by phamngoctuan on 29/05/2016.
 */
public class AccountInfo {
    String _password;
    PersonInfo _info;
    Map<String, Follow> _follow;
    Map<String, ChatPrivate> _chatPrivate;
}

class PersonInfo
{
    String _name, _nickname, _avatar;
    int _follower, _status;

    static int ONLINE = 1;
    static int OFFLINE = 0;

    PersonInfo(String name, String nn, String ava, int fl, int stt)
    {
        _name = name;
        _nickname = nn;
        _avatar = ava;
        _follower = fl;
        _status = stt;
    }
}

class Follow
{
    String _nickname;

    Follow(String nn)
    {
        _nickname = nn;
    }
}

class ChatPrivate
{
    String _nickname;
    int _position;

    ChatPrivate(String nn, int pos)
    {
        _nickname = nn;
        _position = pos;
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
}