package com.speakame.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Activity.GroupChat_Activity;
import com.speakame.Beans.AllBeans;
import com.speakame.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by MMFA-YOGESH on 9/12/2016.
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {

    Context context;
    private ArrayList<AllBeans> contactList;


    public GroupListAdapter(Context context, ArrayList<AllBeans> contactList) {
        this.contactList = contactList;
        this.context = context;
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
                        .inflate(R.layout.custom_grouplist, parent, false);

                return new MyViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllBeans allBeans = contactList.get(position);
        holder.name.setText(allBeans.getGroupName());
        holder.date.setText(allBeans.getGroupCreateDate());
        if (allBeans.getGroupImage().equalsIgnoreCase("")) {
            holder.imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(context).load(allBeans.getGroupImage()).error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, date;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.nametext);
            date = (TextView) view.findViewById(R.id.datetext);
            imageView = (ImageView) view.findViewById(R.id.image);

            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
            name.setTypeface(tf1);

            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           // M.log("chatvalue", getPosition() + "::" + contactList.get(getPosition()));
            Intent intent = new Intent(context, GroupChat_Activity.class);
            intent.putExtra("value", contactList.get(getPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}

