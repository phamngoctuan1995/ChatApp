package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity{

    TextView _tvFriendName, _tvStatus;
    EditText _edtMessage;
    Button _btnSend;
    RecyclerView _messageContainer;
    PersonInfo _friendInfo;
    MessageChatAdapter _adapter;
    ArrayList<ChatRecord> _message;
    int _chatPosition;
    Firebase chatRef;

    void initChatInfo()
    {
        HashMap<String, Long> chatPrivate = MyConstant.myAccount._privateChat;
        if (chatPrivate != null && chatPrivate.containsKey(_friendInfo._nickname)) {
            _chatPosition = chatPrivate.get(_friendInfo._nickname).intValue();
        }
        else {
            _chatPosition = MyConstant.SENDER;
            MyConstant.fb_myaccount.child("_privateChat").child(_friendInfo._nickname).setValue(_chatPosition);
        }
    }
    void initPrivateChat()
    {
        String chatCode;
        if (_chatPosition == MyConstant.SENDER) {
            chatCode = MyConstant.myAccount._info._nickname + "_" + _friendInfo._nickname;
            MyConstant.fb_users.child(_friendInfo._nickname).child("_privateChat")
                    .child(MyConstant.myAccount._info._nickname).setValue(MyConstant.RECIPIENT);
        }
        else {
            chatCode = _friendInfo._nickname + "_" + MyConstant.myAccount._info._nickname;
            MyConstant.fb_users.child(_friendInfo._nickname).child("_privateChat")
                    .child(MyConstant.myAccount._info._nickname).setValue(MyConstant.SENDER);
        }

        chatRef = MyConstant.fb_chats.child(chatCode);
        Query query = chatRef;
        query.limitToLast(20);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatRecord chat = new ChatRecord();
                chat = dataSnapshot.getValue(ChatRecord.class);
                _adapter.refillAdapter(chat);
                _messageContainer.scrollToPosition(_adapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    void getIntentData(Intent intent)
    {
        _friendInfo = new PersonInfo(intent.getStringExtra("name"), intent.getStringExtra("nickname")
                , intent.getStringExtra("avatar"), intent.getIntExtra("follower", 0)
                , intent.getIntExtra("status", PersonInfo.OFFLINE));
    }

    void initView()
    {
        _message = new ArrayList<>();
        _tvFriendName = (TextView) findViewById(R.id.tv_friendnameChat);
        _tvStatus = (TextView) findViewById(R.id.tv_status);
        _edtMessage = (EditText) findViewById(R.id.edt_user_message);
        _btnSend = (Button) findViewById(R.id.btn_send_message);
        _messageContainer = (RecyclerView) findViewById(R.id.chat_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        _messageContainer.setLayoutManager(llm);
        _adapter = new MessageChatAdapter(_message, _chatPosition);
        _messageContainer.setAdapter(_adapter);

        _edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                _messageContainer.scrollToPosition(_adapter.getItemCount() - 1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        _tvFriendName.setText(_friendInfo._name);

        if (_friendInfo._status == PersonInfo.ONLINE)
        {
            _tvStatus.setText("Online");
            _tvStatus.setTextColor(Color.argb(255, 0, 255, 0));
        }
        else
        {
            _tvStatus.setText("Offline");
            _tvStatus.setTextColor(Color.argb(255, 255, 0, 0));
        }

        _btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = _edtMessage.getText().toString();
                if (text.equals(""))
                    return;
                _edtMessage.setText("");
                ChatRecord chat = new ChatRecord(text, _chatPosition);
                chatRef.push().setValue(chat);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        if (intent != null)
            getIntentData(intent);
        else {
            Toast.makeText(this, "Error!!! Please try again", Toast.LENGTH_SHORT).show();
            finish();
        }
        initChatInfo();
        initView();
        initPrivateChat();
    }
}
