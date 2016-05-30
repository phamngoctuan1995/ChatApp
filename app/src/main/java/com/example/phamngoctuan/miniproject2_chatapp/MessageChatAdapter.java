package com.example.phamngoctuan.miniproject2_chatapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by phamngoctuan on 29/05/2016.
 */
public class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<ChatRecord> _chatMessage;
    private int _type;

    public MessageChatAdapter(ArrayList<ChatRecord> listOfFireChats, int t) {
        _chatMessage = listOfFireChats;
        _type = t;
    }

    @Override
    public int getItemViewType(int position) {
        if(_chatMessage.get(position)._position == _type){
            Log.e("Adapter", " sender");
            return MyConstant.SENDER;
        }else {
            return MyConstant.RECIPIENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        switch (viewType) {
            case MyConstant.SENDER:
                view = inflater.inflate(R.layout.sender_message, viewGroup, false);
                viewHolder= new ViewHolderSender(view);
                break;
            case MyConstant.RECIPIENT:
                view = inflater.inflate(R.layout.recipient_message, viewGroup, false);
                viewHolder= new ViewHolderRecipient(view);
                break;
            default:
                view = inflater.inflate(R.layout.sender_message, viewGroup, false);
                viewHolder= new ViewHolderSender(view);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()){
            case MyConstant.SENDER:
                ViewHolderSender viewHolderSender = (ViewHolderSender)viewHolder;
                configureSenderView(viewHolderSender, position);
                break;
            case MyConstant.RECIPIENT:
                ViewHolderRecipient viewHolderRecipient = (ViewHolderRecipient)viewHolder;
                configureRecipientView(viewHolderRecipient, position);
                break;
        }
    }

    private void configureSenderView(ViewHolderSender viewHolderSender, int position) {
        ChatRecord message = _chatMessage.get(position);
        viewHolderSender.getSenderMessageTextView().setText(message._message);
    }

    private void configureRecipientView(ViewHolderRecipient viewHolderRecipient, int position) {
        ChatRecord recipientFireMessage = _chatMessage.get(position);
        viewHolderRecipient.getRecipientMessageTextView().setText(recipientFireMessage._message);
    }

    @Override
    public int getItemCount() {
        return _chatMessage.size();
    }


    public void refillAdapter(ChatRecord message){

        /*add new message chat to list*/
        _chatMessage.add(message);

        /*refresh view*/
        notifyItemInserted(getItemCount() - 1);
    }

    public void refillFirsTimeAdapter(ArrayList<ChatRecord> newFireChatMessage){

        _chatMessage.clear();
        _chatMessage.addAll(newFireChatMessage);
        notifyItemInserted(getItemCount()-1);
    }

    public void cleanUp() {
        _chatMessage.clear();
    }


    /*==============ViewHolder===========*/

    /*ViewHolder for Sender*/

    public class ViewHolderSender extends RecyclerView.ViewHolder {

        private TextView mSenderMessageTextView;

        public ViewHolderSender(View itemView) {
            super(itemView);
            mSenderMessageTextView = (TextView) itemView.findViewById(R.id.senderMessage);
        }

        public TextView getSenderMessageTextView() {
            return mSenderMessageTextView;
        }

        public void setSenderMessageTextView(TextView senderMessage) {
            mSenderMessageTextView = senderMessage;
        }
    }


    /*ViewHolder for Recipient*/
    public class ViewHolderRecipient extends RecyclerView.ViewHolder {

        private TextView mRecipientMessageTextView;

        public ViewHolderRecipient(View itemView) {
            super(itemView);
            mRecipientMessageTextView = (TextView) itemView.findViewById(R.id.recipientMessage);
        }

        public TextView getRecipientMessageTextView() {
            return mRecipientMessageTextView;
        }

        public void setRecipientMessageTextView(TextView recipientMessage) {
            mRecipientMessageTextView = recipientMessage;
        }
    }
}
