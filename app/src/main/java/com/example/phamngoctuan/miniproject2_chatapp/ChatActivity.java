package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity{

    TextView _tvFriendName, _tvStatus;
    EditText _edtMessage;
    Button _btnSend;
    RecyclerView _messageContainer;
    PersonInfo _friendInfo;
    MessageChatAdapter _adapter;
    ArrayList<ChatRecord> _message = null;
    ChatPrivate _chatInfo;

    void getChatMessage()
    {
        if (_message == null)
            _message = new ArrayList<>();
        else _message.clear();

        _message.add(new ChatRecord("Nguoi gui", 1));
        _message.add(new ChatRecord("Nguoi nhan", 0));

        _chatInfo = new ChatPrivate("lala", 0);
    }

    void getIntentData(Intent intent)
    {
        _friendInfo = new PersonInfo(intent.getStringExtra("name"), intent.getStringExtra("nickname")
                , intent.getStringExtra("avatar"), intent.getIntExtra("follower", 0)
                , intent.getIntExtra("status", PersonInfo.OFFLINE));
    }

    void InitView()
    {
        _tvFriendName = (TextView) findViewById(R.id.tv_friendnameChat);
        _tvStatus = (TextView) findViewById(R.id.tv_status);
        _edtMessage = (EditText) findViewById(R.id.edt_user_message);
        _btnSend = (Button) findViewById(R.id.btn_send_message);
        _messageContainer = (RecyclerView) findViewById(R.id.chat_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        _messageContainer.setLayoutManager(llm);

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
                if (text == "")
                    return;
                _adapter.refillAdapter(new ChatRecord(text, _chatInfo._position));
                _edtMessage.setText("");
                _messageContainer.scrollToPosition(_adapter.getItemCount() - 1);
            }
        });

        _adapter = new MessageChatAdapter(_message, _chatInfo._position);
        _messageContainer.setAdapter(_adapter);
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
        getChatMessage();
        InitView();
    }
}
