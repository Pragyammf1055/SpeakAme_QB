package com.speakame.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Activity.GroupMemberList_Activity;
import com.speakame.Beans.AllBeans;
import com.speakame.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMFA-YOGESH on 7/21/2016.
 */
public class NewGroupMember_Adapter extends RecyclerView.Adapter<NewGroupMember_Adapter.MyViewHolder> {

    public static List<Integer> stringArrayList;
    public static List<AllBeans> contactArrayList;
    public static ArrayList<Integer> friendQB_IdArrayList;
    // public static String stringArrayList;
    Context context;
    boolean isCheckeds = false;
    private ArrayList<AllBeans> contactList;


    public NewGroupMember_Adapter(Context context, ArrayList<AllBeans> contactList) {
        this.contactList = contactList;
        this.context = context;
        stringArrayList = new ArrayList<Integer>();
        contactArrayList = new ArrayList<AllBeans>();
        friendQB_IdArrayList = new ArrayList<>();
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
                        .inflate(R.layout.row_contact_display, parent, false);

                return new MyViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        AllBeans allBeans = contactList.get(position);
        holder.name.setText(allBeans.getFriendname());
        holder.number.setText(""+allBeans.getFriendmobile());

        if (allBeans.getFriendimage().equalsIgnoreCase("")) {
            holder.imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(context).load(allBeans.getFriendimage()).error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(holder.imageView);
        }

        if (isCheckeds) {
            holder.checkBox.setChecked(true);

        } else {
            holder.checkBox.setChecked(false);
        }

        if(allBeans.isSelected()){
            holder.checkBox.setChecked(true);
            stringArrayList.add(Integer.valueOf(contactList.get(position).getFriendid()));
            contactArrayList.add(contactList.get(position));
            System.out.println("contactPass" + "::" + contactList.size() + "::" + stringArrayList.toString());
        }else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.checkBox.setTag(position);

                if (isCheckeds) {

                } else if (isChecked) {
                    System.out.println("contactPass Im" + Integer.valueOf(contactList.get(position).getFriendid()));
                    stringArrayList.add(Integer.valueOf(contactList.get(position).getFriendid()));
                    contactArrayList.add(contactList.get(position));
                    friendQB_IdArrayList.add(contactList.get(position).getFriendQB_id());
                    System.out.println("contactPass" + "::" + contactList.size() + "::" + stringArrayList.toString());
                } else {
                    System.out.println("contactPass Rm" + contactList.get(position).getFriendid());
                    stringArrayList.remove(Integer.valueOf(contactList.get(position).getFriendid()));
                    contactArrayList.remove(contactList.get(position));
                    friendQB_IdArrayList.remove(contactList.get(position).getFriendQB_id());
                    System.out.println("contactPass" + stringArrayList.toString());
                }

                GroupMemberList_Activity.updatemember(stringArrayList.size());

            }
        });
    }

    public ArrayList<Integer> getFriendQbIdList() {

        return friendQB_IdArrayList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, number;
        public ImageView imageView;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            number = (TextView) view.findViewById(R.id.number);
            imageView = (ImageView) view.findViewById(R.id.userpic);
            checkBox = (CheckBox) view.findViewById(R.id.chk_member);

            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
            name.setTypeface(tf1);
            number.setTypeface(tf1);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            M.log("chatvalue", getPosition() + "::" + contactList.get(getPosition()));
//            Intent intent = new Intent(context, ChatActivity.class);
//            intent.putExtra("value", contactList.get(getPosition()));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        }
    }
}

