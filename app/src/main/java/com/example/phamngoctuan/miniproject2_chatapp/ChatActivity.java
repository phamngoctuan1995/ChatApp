package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity{

    TextView _tvFriendName, _tvStatus;
    EditText _edtMessage;
    Button _btnSend;
    RecyclerView _messageContainer;

    void InitView()
    {
        _tvFriendName = (TextView) findViewById(R.id.tv_friendnameChat);
        _tvStatus = (TextView) findViewById(R.id.tv_status);
        _edtMessage = (EditText) findViewById(R.id.edt_user_message);
        _btnSend = (Button) findViewById(R.id.btn_send_message);
        _messageContainer = (RecyclerView) findViewById(R.id.chat_recycler_view);

        _messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        InitView();
    }
}
