package com.speakame.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconTextView;
import com.speakame.Classes.TimeAgo;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.utils.Function;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by MMFA-YOGESH on 6/13/2016.
 */
public class BroadcastnewgroupAdapter extends BaseAdapter {

    private static final String TAG = "BroadcastAdapter";
    List<ChatMessage> chatMessageList;
    List<ChatMessage> objects;
    int privateChat_count = 0;
    TextView messageCountTextView;
    private Activity context;

    public BroadcastnewgroupAdapter(Activity context, List<ChatMessage> chatMessageList, TextView messageCountTextView) {
        //super(context, R.layout.custom_broadcast);
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.objects = new ArrayList<ChatMessage>();
        this.objects.addAll(chatMessageList);
        this.messageCountTextView = messageCountTextView;
        privateChat_count = 0;
//        messageCountTextView.setVisibility(View.VISIBLE);
    }

    public static String formatToYesterdayOrToday(String date, String time) throws ParseException {


        String old_date = date + ", " + time;
        Date dateTime = new SimpleDateFormat("yyyy MM dd, hh:mm a").parse(old_date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return /*"Today " +*/ timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday "/* + timeFormatter.format(dateTime)*/;
        } else {
            return date;
        }
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
        EmojiconTextView txtmsg = (EmojiconTextView) rowView.findViewById(R.id.messagetext);
        TextView txtminute = (TextView) rowView.findViewById(R.id.minutetext);
        TextView msgCount = (TextView) rowView.findViewById(R.id.msgCount);

        String count = "";

        Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        txtname.setTypeface(tf1);
        txtmsg.setTypeface(tf1);
        txtminute.setTypeface(tf1);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageprofilepic);
        String image = "";
        if (chatMessage.groupName.equalsIgnoreCase("")) {

            if (Function.isStringInt(chatMessage.reciverName)) {
                txtname.setText("+" + chatMessage.reciverName);
            } else {
                txtname.setText(chatMessage.reciverName);
            }
            image = chatMessage.ReciverFriendImage;
            count = DatabaseHelper.getInstance(context).getmsgCount("chat", chatMessage.receiver);
            Log.v(TAG, "count from db inside 213:- " + count);
            Log.v(TAG, "privateChat_count from db before:- " + privateChat_count);

        } else {
            txtname.setText(chatMessage.groupName);
            image = chatMessage.Groupimage;
            count = DatabaseHelper.getInstance(context).getmsgCount("group", chatMessage.receiver);
        }

        if (count.equalsIgnoreCase("")) {
            msgCount.setVisibility(View.GONE);
        } else {
            msgCount.setVisibility(View.VISIBLE);
            msgCount.setText(count);
            txtminute.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            txtminute.setTypeface(null, Typeface.BOLD);
        }

        if (image != null) {

            if (!image.equalsIgnoreCase("")) {
                Log.v(TAG, "USERIMAGE :- " + image);
                Picasso.with(context).load(image).error(R.drawable.user_icon)
                        .resize(200, 200)
                        .into(imageView);
            }

        }

        System.out.println("BroadCast revname :- " + chatMessage.groupid);
        System.out.println("BroadCast grpname :- " + chatMessage.groupName);
        System.out.println("BroadCast receiver :- " + chatMessage.receiver);
        System.out.println("BroadCast reciverName :- " + chatMessage.reciverName);
        txtmsg.setText(chatMessage.body);

        //txtminute.setText(CommonMethods.getCurrentTime());

        Log.v("BroadCastAdapter", "Time :- " + chatMessage.Time);
        Log.v("BroadCastAdapter", "Date :- " + chatMessage.Date);

        String sDate1 = chatMessage.Date + " , " + chatMessage.Time;
        Date date1 = null;

        try {
            date1 = new SimpleDateFormat("yyyy MM dd , hh:mm a").parse(sDate1);
            Log.v("BroadCastAdapter", "Date after conv :- " + date1);

            String lastseen = new TimeAgo(context).timeAgo(date1);

            Log.v("BroadCastAdapter", "Date after conv 1:- " + lastseen);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(sDate1 + "\t" + date1);
        String date = "";
        try {
            String oldDate = chatMessage.Date + ", " + chatMessage.Time;
            date = Function.formatToYesterdayOrToday1(chatMessage.Date, chatMessage.Time);
            date = Function.formatToYesterdayOrToday(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.v("BroadCastAdapter", "Date after conversion :-  " + date);
        txtminute.setText(date);

        ///////////// dont delete this code /////////////

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

                if (wp.groupName.equalsIgnoreCase("")) {
                    Log.d("BroadCast", "Inside if condition");

                    if (wp.reciverName.toLowerCase(Locale.getDefault()).contains(charText)) {
                        chatMessageList.add(wp);
                    }
                } else {
                    Log.d("BroadCast", "Inside else condition");
                    if (wp.groupName.toLowerCase(Locale.getDefault()).contains(charText)) {
//                    if (wp.reciverName.toLowerCase(Locale.getDefault()).contains(charText)) {
                        chatMessageList.add(wp);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}