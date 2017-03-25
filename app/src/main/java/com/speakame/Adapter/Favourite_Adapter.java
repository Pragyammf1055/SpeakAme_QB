package com.speakame.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Activity.ChatActivity;
import com.speakame.Beans.AllBeans;
import com.speakame.R;
import com.speakame.Services.XmppConneceted;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by MMFA-YOGESH on 6/28/2016.
 */
public class Favourite_Adapter extends RecyclerView.Adapter<Favourite_Adapter.MyViewHolder> {

    Context context;
    ArrayList<AllBeans> objects;
    AllBeans allBeans;
    private ArrayList<AllBeans> contactList;


    public Favourite_Adapter(Context context, ArrayList<AllBeans> contactList) {
        this.contactList = contactList;
        this.context = context;
        this.objects = new ArrayList<AllBeans>();
        this.objects.addAll(contactList);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case 0: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_favrite, parent, false);

                return new MyViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        allBeans = contactList.get(position);
        holder.name.setText(allBeans.getFriendname());
        holder.number.setText(allBeans.getFriendmobile());


        if (allBeans.getFriendimage().equalsIgnoreCase("0")) {
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

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contactList.clear();
        if (charText.length() == 0) {
            contactList.addAll(objects);
        } else {
            for (AllBeans wp : objects) {
                if (wp.getFriendname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    contactList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, number;
        public ImageView imageView;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
            number = (TextView) view.findViewById(R.id.tv_number);
            imageView = (ImageView) view.findViewById(R.id.iv_photo);
            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");

            name.setTypeface(tf1);
            number.setTypeface(tf1);

            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Log.d("chatvalue", getPosition() + "::" + contactList.get(getPosition()));
            XmppConneceted activity = new XmppConneceted();
            if (activity.getmService().xmpp.checkUserBlock(contactList.get(getPosition()).getFriendmobile())){
                activity.getmService().xmpp.unBlockedUser(contactList.get(getPosition()).getFriendmobile());
            }

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("value", contactList.get(getPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}

