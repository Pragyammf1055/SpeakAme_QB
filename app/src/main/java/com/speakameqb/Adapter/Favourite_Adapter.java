package com.speakameqb.Adapter;

import android.app.Activity;
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

import com.quickblox.chat.model.QBChatDialog;
import com.speakameqb.Activity.ChatActivity;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.R;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by MMFA-YOGESH on 6/28/2016.
 */
public class Favourite_Adapter extends RecyclerView.Adapter<Favourite_Adapter.MyViewHolder> {

    private static final String TAG = "Favourite_Adapter";
    Activity context;
    ArrayList<AllBeans> objects;
    AllBeans allBeans;
    String requestFilePath;
    private ArrayList<AllBeans> contactList;

    public Favourite_Adapter() {
    }

    public Favourite_Adapter(Activity context, ArrayList<AllBeans> contactList, String requestFilePath) {
        this.requestFilePath = requestFilePath;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        allBeans = contactList.get(position);
        holder.name.setText(allBeans.getFriendname());
        holder.number.setText(allBeans.getFriendmobile());
        Log.v(TAG, "Qb id of user in favorite Adapter :- " + allBeans.getFriendQB_id());

        if (allBeans.getFriendimage().equalsIgnoreCase("0")) {
            holder.imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(context).load(allBeans.getFriendimage()).error(R.drawable.profile_default)
                    .resize(200, 200)
                    .into(holder.imageView);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int QB_UID = contactList.get(position).getFriendQB_id();

                Log.v(TAG, "QB id of user :- " + QB_UID);
                byte[] chatbytes = DatabaseHelper.getInstance(context).getChatDialogUsingQBId(QB_UID);
                QBChatDialog qbChatDialog = SerializationUtils.deserialize(chatbytes);
                Log.v(TAG, "qbChatDialog :- " + qbChatDialog);

                Intent intent = new Intent(context, ChatActivity.class);
                intent.setAction("");
                intent.putExtra("value", contactList.get(position));
                intent.putExtra("groupName", "");
                intent.putExtra("requestFilePath", requestFilePath);
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, qbChatDialog);
                context.startActivity(intent);
                requestFilePath = "";
                /*
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
//                context.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (contactList != null && !contactList.isEmpty()) {
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
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, number;
        public ImageView imageView;
        View view;

        public MyViewHolder(final View view) {
            super(view);
            this.view = view;
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
       /*
            XmppConneceted activity = new XmppConneceted();
            if (activity.getmService().xmpp.checkUserBlock(contactList.get(getPosition()).getFriendmobile())){
                activity.getmService().xmpp.unBlockedUser(contactList.get(getPosition()).getFriendmobile());
            }*/

            Intent intent = new Intent(context, ChatActivity.class);
            intent.setAction("");
            intent.putExtra("value", contactList.get(getPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        }
    }
}

