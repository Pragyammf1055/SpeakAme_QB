package com.speakameqb.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.speakameqb.Activity.ChatActivity;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Database.BlockUserDataBaseHelper;
import com.speakameqb.R;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by MMFA-YOGESH on 8/1/2016.
 */
public class ImportcontactAdapter extends RecyclerView.Adapter<ImportcontactAdapter.MyViewHolder> {

    public static final String EXTRA_QB_USERS = "qb_users";
    private static final String TAG = "ImportcontactAdapter";
    private static final int REQUEST_SELECT_PEOPLE = 174;
    Activity context = null;
    ArrayList<AllBeans> objects;
    AllBeans allBeans;
    QBUser qbUser;
    String requestFilePath;
    OnItemClickListener onItemClickListener;
    private ArrayList<AllBeans> contactList;
    private List<QBUser> selectedUsers;
    private int QB_UID;

    public ImportcontactAdapter(Activity context) {
        this.context = context;
    }

    public ImportcontactAdapter(Activity context, ArrayList<AllBeans> contactList, String requestFilePath, OnItemClickListener onItemClickListener) {
        this.contactList = contactList;
        this.requestFilePath = requestFilePath;
        this.context = context;
        this.objects = new ArrayList<AllBeans>();
        this.objects.addAll(contactList);
        this.selectedUsers = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        allBeans = contactList.get(position);
       /* holder.name.setText(allBeans.getFriendname());
        holder.number.setText(allBeans.getFriendmobile());*/
        Log.v(TAG, "QB ID of user " + position + " :- " + allBeans.getFriendQB_id());
        if (allBeans.getBlockedStatus().equalsIgnoreCase("1")) {

            Log.i("ContactAdapter", " INside status 1 ");
            final String friendName = allBeans.getFriendname();
            final String friendNumber = allBeans.getFriendmobile().replace(" ", "").replace("+", "");
            Log.i("ImportContact", " Blocked Name :-" + allBeans.getFriendmobile());

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
//                                    XmppConneceted activity = new XmppConneceted();
//                                    activity.getmService().xmpp.unBlockedUser(friendNumber);
                                    //   sendUnBlockstatus(friendNumber);

                                    BlockUserDataBaseHelper blockUserDataBaseHelper = new BlockUserDataBaseHelper(context);

                                    QBPrivacyList list = new QBPrivacyList();
                                    list.setName("public");
                                    QBPrivacyListsManager privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();

                                    ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

                                    ArrayList<Integer> allBlockedUsersNew = blockUserDataBaseHelper.getAllBlockedUsers();
                                    for (int i = 0; i < allBlockedUsersNew.size(); i++) {
                                        Log.d(TAG, " Loop lo s " + allBlockedUsersNew.get(i) + " : " + contactList.get(position).getFriendQB_id());

                                        if (allBlockedUsersNew.get(i).toString().equalsIgnoreCase(contactList.get(position).getFriendQB_id().toString())) {
                                            blockUserDataBaseHelper.deleteBlockedUserById(allBlockedUsersNew.get(i));
                                            QBPrivacyListItem item1 = new QBPrivacyListItem();
                                            item1.setAllow(true);
                                            item1.setType(QBPrivacyListItem.Type.USER_ID);
                                            item1.setValueForType(String.valueOf(allBlockedUsersNew.get(i)));
                                            Log.d(TAG, " This is the new QBFriendId unblo " + allBlockedUsersNew.get(i));
// item1.setMutualBlock(true);
                                            items.add(item1);
                                            list.setItems(items);

                                            try {
                                                Log.d(TAG, " This is the privecy list hhh" + list.toString());
                                                privacyListsManager.setPrivacyList(list);
                                                privacyListsManager.setPrivacyListAsDefault("public");
                                            } catch (SmackException.NotConnectedException e) {
                                                e.printStackTrace();
                                            } catch (XMPPException.XMPPErrorException e) {
                                                e.printStackTrace();
                                            } catch (SmackException.NoResponseException e) {
                                                e.printStackTrace();
                                            }
                                            sendUnBlockstatus(friendNumber);

                                        } else {
                                            Log.d(TAG, " imapondl else conditons " + " jlfsdjkl ");

                                        }

                                    }
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
            holder.myimageView.setBackgroundResource(R.drawable.profile_default);

        } else {
            Picasso.with(context).load(allBeans.getFriendimage())
                    .error(R.drawable.profile_default)
                    .placeholder(R.drawable.profile_default)
                    .resize(200, 200)
                    .into(holder.myimageView);
        }

        if (allBeans.getFavriouteFriend().equalsIgnoreCase("0") || allBeans.getFavriouteFriend().equalsIgnoreCase("")) {
            holder.unfav_img.setBackgroundResource(R.drawable.ic_star_grey_600_24dp);

        } else if (allBeans.getFavriouteFriend().equalsIgnoreCase("1")) {
            holder.unfav_img.setVisibility(View.GONE);
            holder.fav_img.setVisibility(View.VISIBLE);
            holder.fav_img.setBackgroundResource(R.drawable.ic_star_blue_24dp);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = contactList.get(position).getFriendQB_id();
                Log.v(TAG, "QB_UID :- " + QB_UID);
                Log.v(TAG, "QB_UID 123:- " + id);

                AllBeans allBeans = contactList.get(position);

                Log.v(TAG, "click on the contact from 123 :- " + allBeans);
                Log.v(TAG, "click on the contact from 456 :- " + contactList.get(position));

                subscribeUser(id);

                passResultToCallerActivity(id, contactList.get(position));
            }
        });

        holder.unfav_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.unfav_img.setVisibility(View.GONE);
                holder.fav_img.setVisibility(View.VISIBLE);
//                addfaviroute(contactList.get(position).getFriendmobile());
                onItemClickListener.onItemClick(v, position);

            }
        });

        holder.fav_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fav_img.setVisibility(View.GONE);
                holder.unfav_img.setVisibility(View.VISIBLE);
                onItemClickListener.onItemClick(v, position);
            }
        });

    }

    public void subscribeUser(int qbUserId) {
        Log.d(TAG, " subScribeUser ID : - " + qbUserId);

        QBRoster сhatRoster = QBChatService.getInstance().getRoster();

/*
try {
сhatRoster.unsubscribe(qbUserId);
} catch (SmackException.NotConnectedException e) {
}
*/
        Log.d(TAG, " QBRoster сhatRoster :- " + сhatRoster);
        if (сhatRoster != null) {
//            Toast.makeText(context.getApplicationContext(), "inside subscription ... of user id :- " + qbUserId, Toast.LENGTH_SHORT).show();
            if (сhatRoster.contains(qbUserId)) {
                try {
                    сhatRoster.subscribe(qbUserId);
                } catch (SmackException.NotConnectedException e) {
                    Log.v(TAG, "error: 123 " + e.getClass().getSimpleName());
                }
            } else {
                try {
                    сhatRoster.createEntry(qbUserId, null);
                } catch (XMPPException e) {
                    Log.v(TAG, "error 456 : " + e.getLocalizedMessage());
                } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | SmackException.NoResponseException e) {
                    Log.v(TAG, "error chatroaster : " + e.getClass().getSimpleName());
                }
            }
        }
    }

    private void passResultToCallerActivity(int id, AllBeans allBeans) {

        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction("Contact_Import");
        intent.putExtra("value", allBeans);
        intent.putExtra("groupName", "");
        intent.putExtra("recipient_qb_id", id);
        intent.putExtra("requestFilePath", requestFilePath);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        context.setResult(RESULT_OK, intent);
        context.startActivity(intent);
//        fddddddddddddddddddddddddddddddddddddddddddddddddddddddddb
        context.finish();
    }

    private QBUser getUserIdByIds(int qb_id) {


        Log.v(TAG, "Inside getting user details by phone no ");
        final Integer selectedUsers = 0;
        QBRoster roster = QBChatService.getInstance().getRoster();

        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(10);

        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(qb_id);

        QBUsers.getUsersByIDs(ids, pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
//                mDialog.dismiss();
//                userModelArrayList = new ArrayList<UserModel>();

                for (QBUser user : qbUsers) {


                    Log.v(TAG, "User id :- " + user.getId().toString());
                    Log.v(TAG, "User namne :- " + user.getFullName());
                    Log.v(TAG, "User number:- " + user.getPhone());
                    Log.v(TAG, "User Login Id:- " + user.getLogin());

//                    QB_FriendId = user.getId();
//                    QB_Name = user.getFullName().toString();
//                    QB_Mobile = user.getPhone().toString();
//                    QB_LoginId = user.getLogin().toString();
                    qbUser = user;
                }
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error occured");
                Log.v(TAG, "Error getting contact details :- " + e.getMessage());

            }
        });
        return qbUser;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public int getSelectedUsers() {
        return QB_UID;
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
        } else {
            Toast.makeText(context, "No Contact Available.", Toast.LENGTH_SHORT).show();
        }
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
            Picasso.with(context).load(contactList.get(position).getFriendimage()).error(R.drawable.profile_default)
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

                Log.v("response>>>>>", response);
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

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, number;
        public ImageView myimageView, fav_img, unfav_img;
        public LinearLayout list_row;
        View view;


        public MyViewHolder(final View view) {
            super(view);
            this.view = view;
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
//            view.setOnClickListener(this);

           /* unfav_img.setOnClickListener(new View.OnClickListener() {
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
            });*/

            myimageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageDialog(getPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
         /*   Log.d("chatvalue", getPosition() + "::" + contactList.get(getPosition()));
/*
            XmppConneceted activity = new XmppConneceted();
            try {
                activity.getmService().xmpp.createRoster(contactList.get(getPosition()).getFriendmobile().replace(" ","").replace("+","") + "@" + context.getString(R.string.server),
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
            if (activity.getmService().xmpp.checkUserBlock(contactList.get(getPosition()).getFriendmobile().replace(" ","").replace("+",""))) {
                activity.getmService().xmpp.unBlockedUser(contactList.get(getPosition()).getFriendmobile().replace(" ","").replace("+",""));
            }*//*

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("value", contactList.get(getPosition()));
            intent.putExtra("groupName", "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            context.finish();*/
        }
    }


}

