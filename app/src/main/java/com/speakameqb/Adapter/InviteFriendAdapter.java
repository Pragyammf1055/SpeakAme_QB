package com.speakameqb.Adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakameqb.R;

/**
 * Created by MMFA-YOGESH on 6/13/2016.
 */
public class InviteFriendAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] name;
    private final String[] number;

    public InviteFriendAdapter(Activity context,
                               String[] name, String[] number) {
        super(context, R.layout.custom_invitefriend, name);
        this.context = context;
        this.name = name;
        this.number = number;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_invitefriend, null, true);
        TextView txtname = (TextView) rowView.findViewById(R.id.nametext);
        TextView txtmobile = (TextView) rowView.findViewById(R.id.tv_number);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        ImageView invite = (ImageView) rowView.findViewById(R.id.inviteimage);
        txtname.setText(name[position]);
        txtmobile.setText(number[position]);
        return rowView;
    }
}