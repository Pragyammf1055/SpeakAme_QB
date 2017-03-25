package com.speakame.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Beans.AllBeans;
import com.speakame.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by MMFA-YOGESH on 7/1/2016.
 */
public class GroupMemberList_Adapter extends RecyclerView.Adapter<GroupMemberList_Adapter.MyViewHolder> {


    Context context;
    private ArrayList<AllBeans> contactList;

    public GroupMemberList_Adapter(Context context, ArrayList<AllBeans> contactList) {
        this.contactList = contactList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_memberlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllBeans allBeans = contactList.get(position);
        holder.name.setText(allBeans.getFriendname());

        if (allBeans.getFriendStatus().equalsIgnoreCase("")) {
            holder.status.setText("Can't talk speakame only");
        } else {
            holder.status.setText(allBeans.getFriendStatus());
        }
        if(!allBeans.getGroupAdminStatus().equalsIgnoreCase("")){
            holder.adminTextView.setVisibility(View.VISIBLE);
        }

        if (allBeans.getFriendimage().equalsIgnoreCase("")) {
            holder.imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(context).load(allBeans.getFriendimage()).error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status, adminTextView;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            adminTextView = (TextView) view.findViewById(R.id.adminTextView);

            name = (TextView) view.findViewById(R.id.nametext);
            status = (TextView) view.findViewById(R.id.statustext);
            imageView = (ImageView) view.findViewById(R.id.meal_image_order);
            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
            name.setTypeface(tf1);
            status.setTypeface(tf1);


        }
    }
}


