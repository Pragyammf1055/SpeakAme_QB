package com.speakameqb.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.speakameqb.Beans.AllBeans;
import com.speakameqb.R;

import java.util.ArrayList;

/**
 * Created by Peter on 08-Jun-17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    Activity context = null;
    ArrayList<AllBeans> objects;
    AllBeans allBeans;
    private ArrayList<AllBeans> contactList;

    public ContactListAdapter(Activity context, ArrayList<AllBeans> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case 0: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_phone_contactlist, parent, false);

                return new MyViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        AllBeans allBeans = contactList.get(position);

        holder.name.setText(allBeans.getFriendname());
        holder.number.setText(allBeans.getFriendmobile());
        final String MobileNo = contactList.get(position).getFriendmobile();
        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("Contact_List_Adapter", "Mobile no :- " + MobileNo);
                Uri phoneNoUri = Uri.parse("sms:" + MobileNo);
                String shareBody = "Download SpeakAme messenger to chat with me in all languages.\n\n http://speakame.com/dl/";

                Intent sharingIntent = new Intent(Intent.ACTION_SENDTO);
                sharingIntent.putExtra("sms_body", shareBody);
                sharingIntent.setData(phoneNoUri);
                context.startActivity(sharingIntent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, number;
        public ImageView myimageView, fav_img, unfav_img;
        public LinearLayout list_row;
        Button inviteButton;


        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
            number = (TextView) view.findViewById(R.id.tv_number);
            myimageView = (ImageView) view.findViewById(R.id.iv_photo);
            inviteButton = (Button) view.findViewById(R.id.inviteButton);
            list_row = (LinearLayout) view.findViewById(R.id.list_row);
            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");

            name.setTypeface(tf1);
            number.setTypeface(tf1);

            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
        }
    }
}
