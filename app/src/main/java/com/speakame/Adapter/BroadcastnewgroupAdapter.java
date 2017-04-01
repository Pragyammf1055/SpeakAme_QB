package com.speakame.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by MMFA-YOGESH on 6/13/2016.
 */
public class BroadcastnewgroupAdapter extends BaseAdapter {

    private final Activity context;
    List<ChatMessage> chatMessageList;

    List<ChatMessage> objects;

    public BroadcastnewgroupAdapter(Activity context, List<ChatMessage> chatMessageList) {
        //super(context, R.layout.custom_broadcast);
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.objects = new ArrayList<ChatMessage>();
        this.objects.addAll(chatMessageList);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ChatMessage chatMessage = chatMessageList.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_broadcast, null, true);
        TextView txtname = (TextView) rowView.findViewById(R.id.nametext);
        TextView txtmsg = (TextView) rowView.findViewById(R.id.messagetext);
        TextView txtminute = (TextView) rowView.findViewById(R.id.minutetext);
        TextView msgCount = (TextView) rowView.findViewById(R.id.msgCount);

        String count = "";


        Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        txtname.setTypeface(tf1);
        txtmsg.setTypeface(tf1);
        txtminute.setTypeface(tf1);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageprofilepic);
        String image="";
        if(chatMessage.groupName.equalsIgnoreCase("")){
            txtname.setText(chatMessage.reciverName);
             image = chatMessage.ReciverFriendImage;
           count = DatabaseHelper.getInstance(context).getmsgCount("chat", chatMessage.receiver);
        }else{
            txtname.setText(chatMessage.groupName);
             image = chatMessage.Groupimage;
           count = DatabaseHelper.getInstance(context).getmsgCount("group", chatMessage.receiver);
        }
        if(count.equalsIgnoreCase("")){
            msgCount.setVisibility(View.GONE);
        }else{
            msgCount.setVisibility(View.VISIBLE);
            msgCount.setText(count);
            txtminute.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            txtminute.setTypeface(null, Typeface.BOLD);
            if (count.length() == 2)
            {

                msgCount.setPadding(16,12,16,12);
            } else if (count.length() >= 3){

                msgCount.setPadding(16,18,16,18);
                msgCount.setTextSize(10);
            }
        }


        if(image != null){
            if(!image.equalsIgnoreCase("")){
                Log.d("USERIMAGE", image);
                Picasso.with(context).load(image).error(R.drawable.user_icon)
                        .resize(200, 200)
                        .into(imageView);
            }
        }
        System.out.println("revname:-" + chatMessage.groupid);
        System.out.println("grpname:-" + chatMessage.groupName);
        txtmsg.setText(chatMessage.body);

       //txtminute.setText(CommonMethods.getCurrentTime());

        txtminute.setText(chatMessage.Time);


        /////////////dont delete this code/////////////

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd");
//        Date date = null;
//        try {
//            date = formatter.parse(chatMessage.Date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
//            String setDate = sdf.format(date.getTime());
//            txtminute.setText(setDate);
//        } catch (Exception e) {
//        }
       // txtminute.setText(chatMessage.Date);
        /////////////dont delete this code/////////////
        return rowView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        chatMessageList.clear();
        if (charText.length() == 0) {
            chatMessageList.addAll(objects);
        } else {
            for (ChatMessage wp : objects) {
                if (wp.reciverName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    chatMessageList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}