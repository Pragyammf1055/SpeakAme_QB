package com.speakame.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Activity.ChatActivity;
import com.speakame.Beans.AllBeans;
import com.speakame.R;
import com.speakame.Services.XmppConneceted;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by MMFA-YOGESH on 8/1/2016.
 */
public class ImportcontactAdapter extends RecyclerView.Adapter<ImportcontactAdapter.MyViewHolder> {

    Activity context = null;
    ArrayList<AllBeans> objects;
    AllBeans allBeans;
    private ArrayList<AllBeans> contactList;


    public ImportcontactAdapter(Activity context, ArrayList<AllBeans> contactList) {
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
                        .inflate(R.layout.custom_contactlist, parent, false);

                return new MyViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        allBeans = contactList.get(position);
       /* holder.name.setText(allBeans.getFriendname());
        holder.number.setText(allBeans.getFriendmobile());*/

        if (allBeans.getBlockedStatus().equalsIgnoreCase("1")) {

            Log.i("ContactAdapter", " INside status 1 ");
            final String friendName = allBeans.getFriendname();
            final String friendNumber = allBeans.getFriendmobile();
            Log.i("ImportContact", " Blocked Name :-" + friendName);

            holder.name.setTextColor(context.getResources().getColor(R.color.fadelistitem));
            holder.number.setTextColor(context.getResources().getColor(R.color.fadelistitem));
            holder.name.setText(allBeans.getFriendname());
            holder.number.setText(allBeans.getFriendmobile());

/* ~~~~~~~~~ Setting onClick to null for blocked user ~~~~~~~~~ */
// holder.list_row.setBackgroundColor(Color.parseColor("#d1d0d0"));
            holder.list_row.setOnClickListener(null);
            holder.myimageView.setClickable(false);
            holder.fav_img.setClickable(false);
            holder.unfav_img.setClickable(false);

            holder.list_row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true)
                            .setNeutralButton("Unblock " + friendName, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//do things
                                    XmppConneceted activity = new XmppConneceted();
                                    activity.getmService().xmpp.unBlockedUser(friendNumber);
                                    sendUnBlockstatus(friendNumber);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    return false;
                }
            });

        } else {
            holder.name.setText(allBeans.getFriendname());
            holder.number.setText(allBeans.getFriendmobile());
        }


        if (allBeans.getFriendimage().equalsIgnoreCase("")) {
            holder.myimageView.setBackgroundResource(R.drawable.ic_account_circle_black_24dp);

        } else {
            Picasso.with(context).load(allBeans.getFriendimage()).error(R.drawable.ic_account_circle_black_24dp)
                    .resize(200, 200)
                    .into(holder.myimageView);


        }

        if (allBeans.getFavriouteFriend().equalsIgnoreCase("0") || allBeans.getFavriouteFriend().equalsIgnoreCase("")) {
            holder.unfav_img.setBackgroundResource(R.drawable.star);

        } else if (allBeans.getFavriouteFriend().equalsIgnoreCase("1")) {

            holder.unfav_img.setVisibility(View.GONE);
            holder.fav_img.setVisibility(View.VISIBLE);
            holder.fav_img.setBackgroundResource(R.drawable.star_blue);

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

    public void addfaviroute(int position) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "addfavirioute");
            jsonObject.put("contactnumber", contactList.get(position).getFriendmobile());
            jsonObject.put("userid", AppPreferences.getLoginId(context));
            jsonObject.put("faviroute", "1");

            jsonArray.put(jsonObject);
            System.out.println("sentjson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(context);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {


                Log.d("response>>>>>", response);
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");
                            // senduseriD();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    private void addunfaviroute(int position) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "addfavirioute");
            jsonObject.put("contactnumber", contactList.get(position).getFriendmobile());
            jsonObject.put("userid", AppPreferences.getLoginId(context));
            jsonObject.put("faviroute", "0");

            jsonArray.put(jsonObject);
            System.out.println("sentjson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(context);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {


                Log.d("response>>>>>", response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }


    private void imageDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_popup);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


        ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);

        if (contactList.get(position).getFriendimage().equalsIgnoreCase("")) {
            imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(context).load(contactList.get(position).getFriendimage()).error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(imageView);


        }

        window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_corner_dialog));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

    }

    private void sendUnBlockstatus(String FriendNumber) {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "blockstatus");
            jsonObject.put("blockunblockstaus", "0");
            jsonObject.put("friend_mobile", FriendNumber);
            jsonObject.put("user_id", AppPreferences.getLoginId(context));
// jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(context);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {


                Log.d("response>>>>>", response);
// mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setBlockList(context, topObject.getString("block_users"));

                            }

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(context.getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, number;
        public ImageView myimageView, fav_img, unfav_img;
        public LinearLayout list_row;


        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
            number = (TextView) view.findViewById(R.id.tv_number);
            myimageView = (ImageView) view.findViewById(R.id.iv_photo);
            fav_img = (ImageView) view.findViewById(R.id.favimage);
            unfav_img = (ImageView) view.findViewById(R.id.unfavimage);
            list_row = (LinearLayout) view.findViewById(R.id.list_row);
            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");

            name.setTypeface(tf1);
            number.setTypeface(tf1);

            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            view.setOnClickListener(this);
            unfav_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unfav_img.setVisibility(View.GONE);
                    fav_img.setVisibility(View.VISIBLE);
                    addfaviroute(getPosition());

                }
            });
            fav_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fav_img.setVisibility(View.GONE);
                    unfav_img.setVisibility(View.VISIBLE);
                    addunfaviroute(getPosition());

                }
            });

            myimageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageDialog(getPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.d("chatvalue", getPosition() + "::" + contactList.get(getPosition()));

            XmppConneceted activity = new XmppConneceted();
            try {
                activity.getmService().xmpp.createRoster(contactList.get(getPosition()).getFriendmobile() + "@" + context.getString(R.string.server),
                        contactList.get(getPosition()).getFriendname(), new String[]{"Friends"});
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }
            if (activity.getmService().xmpp.checkUserBlock(contactList.get(getPosition()).getFriendmobile())) {
                activity.getmService().xmpp.unBlockedUser(contactList.get(getPosition()).getFriendmobile());
            }
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("value", contactList.get(getPosition()));
            intent.putExtra("groupName", "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            context.finish();
        }
    }


}

