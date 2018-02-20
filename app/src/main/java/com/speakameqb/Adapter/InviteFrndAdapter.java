package com.speakameqb.Adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakameqb.Beans.AllBeans;
import com.speakameqb.R;

import java.util.ArrayList;

/**
 * Created by abc on 24-01-2018.
 */

public class InviteFrndAdapter extends RecyclerView.Adapter<InviteFrndAdapter.ViewHolder> {
    final String TAG = "InviteFrndAdapter";
    Context context;
    private ArrayList<AllBeans> contactList;

    public InviteFrndAdapter(Context context, ArrayList<AllBeans> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_invitefriend, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AllBeans allBeans = contactList.get(position);

        holder.tv_name.setText(allBeans.getFriendname());
        holder.tv_number.setText(allBeans.getFriendmobile());

        holder.inviteimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MobileNo = contactList.get(position).getFriendmobile();
                Log.v(TAG, "Mobile no on click :- " + MobileNo);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_number;
        ImageView iv_photo, inviteimage;

        ViewHolder(View itemView) {
            super(itemView);
            inviteimage = (ImageView) itemView.findViewById(R.id.inviteimage);
            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
        }
    }

}
