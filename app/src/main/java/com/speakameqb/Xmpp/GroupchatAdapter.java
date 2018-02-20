package com.speakameqb.Xmpp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.speakameqb.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by MMFA-YOGESH on 11/7/2016.
 */

public class GroupchatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<ChatMessage> chatMessageList;

    public GroupchatAdapter(Context activity, ArrayList<ChatMessage> list) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.chatbubble, null);

        TextView msg = (TextView) vi.findViewById(R.id.message_text);
        TextView timetext = (TextView) vi.findViewById(R.id.time_text);
        ImageView mimageview = (ImageView) vi.findViewById(R.id.image);


        System.out.println("decodeimage" + message.files);

        if (message.body.equalsIgnoreCase("")) {
            msg.setVisibility(View.GONE);
        } else {
            msg.setVisibility(View.VISIBLE);
        }

        if (message.files.equalsIgnoreCase("")) {
            mimageview.setVisibility(View.GONE);
        } else {
            mimageview.setVisibility(View.VISIBLE);

            File file = new File(message.files);
            Uri uri = Uri.fromFile(file);
            mimageview.setImageURI(uri);
            Bitmap bmImg = BitmapFactory.decodeFile(String.valueOf(file));
            System.out.println("decodeimage" + bmImg);
            /*if (bmImg != null) {

                mimageview.setImageBitmap(bmImg);
            }*/
        }


        msg.setText(message.body);
        timetext.setText(message.Time);
        LinearLayout layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout);
        RelativeLayout parent_layout = (RelativeLayout) vi
                .findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
        if (message.isMine) {
            layout.setBackgroundResource(R.drawable.yyk);
            parent_layout.setGravity(Gravity.RIGHT);
            msg.setTextColor(Color.WHITE);
            timetext.setTextColor(Color.BLACK);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.bubble1);
            parent_layout.setGravity(Gravity.LEFT);
            msg.setTextColor(Color.BLACK);
            timetext.setTextColor(Color.BLACK);

        }

        return vi;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }
}
