package com.example.phamngoctuan.miniproject2_chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

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
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cardview, parent, false);

        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, final int position)
    {
        final Context context = _contextRef.get();
        if (context == null)
            return;

        PersonInfo info = _data.get(position);
        holder._name.setText(info._name);
        holder._nickname.setText(info._nickname);
        holder._numfollow.setText("" + info._follower);

        if (MyConstant._followSet.contains(info._nickname))
            Picasso.with(context).load(R.drawable.ic_follow).into(holder._follow);
        else {
            Picasso.with(context).load(R.drawable.ic_unfollow).into(holder._follow);
        }


        if (info._status == PersonInfo.ONLINE)
        {
            holder._status.setText("Online");
            holder._status.setTextColor(Color.parseColor("#00A388"));
        }
        else
        {
            holder._status.setText("Offline");
            holder._status.setTextColor(Color.parseColor("#FF6138"));
        }

        Picasso.with(context).load(info._avatar).error(R.drawable.ic_notification).into(holder._avatar);

        holder._follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context1 = _contextRef.get();
                if (context == null)
                    return;

                final PersonInfo info = _data.get(position);

                if (MyConstant._followSet.contains(info._nickname))
                {
                    Picasso.with(context).load(R.drawable.ic_unfollow).into((ImageView) v);

                    MyConstant._followSet.remove(info._nickname);

                    MyConstant.fb_myaccount.child("_follow").child(info._nickname).removeValue();
                    MyConstant.myAccount._follow.remove(info._nickname);

                    MyConstant.fb_users.child(info._nickname).child("_info").child("_follower")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long follower = (Long) dataSnapshot.getValue();
                            follower = follower > 0 ? follower - 1 : 0;
                            MyConstant.fb_users.child(info._nickname).child("_info").child("_follower").setValue(follower);
                            info._follower = follower.intValue();

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    if (_type == MyConstant.FOLLOW_TAB) {
                        MyConstant._followList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, _data.size());
                    }
                    else
                    {
                        int i = 0;
                        for (PersonInfo person : MyConstant._followList)
                            if (person._nickname.equals(info._nickname))
                                break;
                            else
                                i++;

                        ListPersonAdapter _followAdapter = null;
                        if (MyConstant._followAdapter != null)
                            _followAdapter = MyConstant._followAdapter.get();
                        if (_followAdapter != null)
                            _followAdapter.deletePerson(i);
                    }
                }
                else
                {
                    Picasso.with(context).load(R.drawable.ic_follow).into((ImageView) v);
                    MyConstant._followList.add(info);

                    MyConstant._followSet.add(info._nickname);
                    MyConstant.fb_myaccount.child("_follow").child(info._nickname).setValue(info._name);

                    if (MyConstant.myAccount._follow == null)
                        MyConstant.myAccount._follow = new HashMap<String, String>();
                    MyConstant.myAccount._follow.put(info._nickname, info._name);

                    MyConstant.fb_users.child(info._nickname).child("_info").child("_follower")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            PersonInfo info = _data.get(position);
                            Long follower = (Long) dataSnapshot.getValue();
                            follower += 1;
                            MyConstant.fb_users.child(info._nickname).child("_info").child("_follower").setValue(follower);
                            info._follower = follower.intValue();

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
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

    void addPerson(PersonInfo info)
    {
        _data.add(info);
        notifyItemInserted(getItemCount() - 1);
    }

    void deletePerson(int position)
    {
        if (position >= getItemCount())
            return;

//        PersonInfo info = _data.get(position);
        _data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
//
//        if (_type == MyConstant.FOLLOW_TAB)
//        {
//            MyConstant._followSet.remove(info._nickname);
//            MyConstant.fb_myaccount.child("_follow").child(info._nickname).removeValue();
//        }
    }

    @Override
    public int getItemCount()
    {
        return _data.size();
    }

    @Override
    public void OnItemClickListener(View v, int position)
    {
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

    public static class PersonViewHolder extends RecyclerView.ViewHolder
    {
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
