package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 29/05/2016.
 */
public class ListPersonAdapter extends RecyclerView.Adapter<ListPersonAdapter.PersonViewHolder> implements onItemClickInterface{

    ArrayList<PersonInfo> _data;
    WeakReference<Context> _contextRef;
    int _type;

    ListPersonAdapter(Context ct, ArrayList<PersonInfo> dt, int t)
    {
        _data = dt;
        _contextRef = new WeakReference<Context>(ct);
        _type = t;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cardview, parent, false);

        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, final int position) {
        final Context context = _contextRef.get();
        if (context == null)
            return;

        PersonInfo info = _data.get(position);
        holder._name.setText(info._name);
        holder._nickname.setText(info._nickname);
        holder._numfollow.setText("" + info._follower);

        if (MyConstant._followSet.contains(info._nickname))
        {
            Picasso.with(context).load(R.drawable.ic_follow).into(holder._follow);
        }
        else
        {
            Picasso.with(context).load(R.drawable.ic_unfollow).into(holder._follow);
        }

        if (info._status == PersonInfo.ONLINE)
        {
            holder._status.setText("Online");
            holder._status.setTextColor(Color.argb(255, 0, 255, 0));
        }
        else
        {
            holder._status.setText("Offline");
            holder._status.setTextColor(Color.argb(255, 255, 0, 0));
        }

        Picasso.with(context).load(info._avatar).error(R.drawable.ic_notification).into(holder._avatar);

        holder._follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context1 = _contextRef.get();
                if (context == null)
                    return;

                PersonInfo info = _data.get(position);
                if (MyConstant._followSet.contains(info._nickname))
                {
                    Picasso.with(context).load(R.drawable.ic_unfollow).into((ImageView) v);
                    MyConstant._followSet.remove(info._nickname);
                    MyConstant._followList.remove(position);

                    if (_type == MyConstant.FOLLOW_TAB) {
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, _data.size());
                    }
                }
                else
                {
                    Picasso.with(context).load(R.drawable.ic_follow).into((ImageView) v);
                    MyConstant._followList.add(info);
                    MyConstant._followSet.add(info._nickname);

                    Toast.makeText(context, "Add " + info._name + " to follow list successfully!", Toast.LENGTH_SHORT);
                }
            }
        });

        holder._cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    @Override
    public void OnItemClickListener(View v, int position) {
        Context context = _contextRef.get();
        if (context == null)
            return;

        PersonInfo info = _data.get(position);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("name", info._name);
        intent.putExtra("nickname", info._nickname);
        intent.putExtra("status", info._status);
        intent.putExtra("follower", info._follower);
        intent.putExtra("avatar", info._avatar);
        context.startActivity(intent);
    }


    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView _cv;
        TextView _name;
        TextView _nickname;
        ImageView _avatar;
        TextView _numfollow;
        ImageButton _follow;
        TextView _status;

        PersonViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _name = (TextView) itemView.findViewById(R.id.tv_card_name);
            _nickname = (TextView) itemView.findViewById(R.id.tv_card_nickname);
            _avatar = (ImageView) itemView.findViewById(R.id.imv_card_avatar);
            _numfollow = (TextView) itemView.findViewById(R.id.tv_card_numfollow);
            _follow = (ImageButton) itemView.findViewById(R.id.imb_card_follow);
            _status = (TextView) itemView.findViewById(R.id.tv_card_status);

        }
    }

}

interface onItemClickInterface
{
    public void OnItemClickListener(View v, int position);
}
