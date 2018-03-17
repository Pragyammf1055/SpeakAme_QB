package com.speakameqb.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.speakameqb.Adapter.NewGroupMember_Adapter;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.QuickBlox.GooglePlayServicesHelper;
import com.speakameqb.R;
import com.speakameqb.Services.QBService;
import com.speakameqb.Xmpp.ChatMessage;
import com.speakameqb.Xmpp.CommonMethods;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Contactloader.Contact;
import com.speakameqb.utils.Contactloader.ContactFetcher;
import com.speakameqb.utils.Contactloader.ContactPhone;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.commons.lang3.SerializationUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class GroupMemberList_Activity extends AnimRootActivity implements VolleyCallback {

    private static final String TAG = "GroupList_Activity";
    public static TextView title_content;
    public TextView nocontenttext, title_name;
    public QBService qbService;
    RecyclerView recyclerView;
    ArrayList<AllBeans> friendlist;
    AlertDialog mProgressDialog;
    NewGroupMember_Adapter newGroupMember_adapter;
    AllBeans allBeans;
    //JSONArray listvalue = new JSONArray();
    String GroupName, GroupId, GroupImagePicture, dialogeIdSend;
    ArrayList<Integer> memberIdList;
    QBChatDialog groupChatDialog;
    GooglePlayServicesHelper googlePlayServicesHelper;

    JSONArray alContactsname = new JSONArray();
    JSONArray alContactsnumber = new JSONArray();
    //**********************************************************************************************//


    public static void updatemember(int count) {
        title_content.setText("Add member(" + count + " member)");
    }

    public static QBChatMessage createChatNotificationForGroupChatUpdate(QBChatDialog chatDialog) {
        Log.d(TAG, " CreatechatNotification : ");
        String dialogId = String.valueOf(chatDialog.getDialogId());
        String roomJid = chatDialog.getRoomJid();
        String occupantsIds = TextUtils.join(",", chatDialog.getOccupants());
        String dialogName = chatDialog.getName();
        String dialogTypeCode = String.valueOf(chatDialog.getType().ordinal());

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody("optional text");
        chatMessage.setDialogId(chatDialog.getDialogId());

        // Add notification_type=2 to extra params when you updated a group chat
        //
        chatMessage.setProperty("notification_type", "1");

        chatMessage.setProperty("_id", dialogId);
        if (!TextUtils.isEmpty(roomJid)) {
            chatMessage.setProperty("room_jid", roomJid);
        }
        chatMessage.setProperty("occupants_ids", occupantsIds);
        if (!TextUtils.isEmpty(dialogName)) {
            chatMessage.setProperty("dialog_name", dialogName);
        }
        chatMessage.setProperty("dialog_type", "2");//2 // dialogTypeCode

        return chatMessage;
    }

    //------------------------------------------------------------------------------------------------------------//
    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);

        setToolbar();

        memberIdList = new ArrayList<Integer>();
        qbService = new QBService();

        title_content.setText("Add member(0 member)");

        Intent intent = getIntent();
        if (intent.getAction().equalsIgnoreCase("CreateGroupChatActivity")) {

            GroupName = intent.getStringExtra("groupName");
            GroupId = intent.getStringExtra("groupId");
            GroupImagePicture = intent.getStringExtra("groupImage");

        } else if (intent.getAction().equalsIgnoreCase("ViewGroupDetail_Activity")) {

            GroupName = intent.getStringExtra("groupName");
            GroupId = intent.getStringExtra("groupId");
            memberIdList = intent.getIntegerArrayListExtra("group_MemberId");
            dialogeIdSend = intent.getStringExtra("dialogeIdSend");
        }

        Log.v(TAG, "Grp 1 :- " + GroupName);
        Log.v(TAG, "Grp 2 :- " + GroupId);
        Log.v(TAG, "Grp 3 :- " + GroupImagePicture);
        Log.v(TAG, "Grp 4 :- " + memberIdList);

        System.out.println("imggrp" + GroupImagePicture);
        title_name.setText(GroupName);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());

        friendlist = new ArrayList<AllBeans>();


        googlePlayServicesHelper = new GooglePlayServicesHelper();
        getConatactList();

    }

    private void callGetCheckListAPi() {

       /* mProgressDialog = new SpotsDialog(GroupMemberList_Activity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();*/

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.GETCHECKLIST);
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(GroupMemberList_Activity.this));
            jsonObject.put("user_id", AppPreferences.getLoginId(GroupMemberList_Activity.this));

            jsonArray.put(jsonObject);
            System.out.println("AddmemberList" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(getApplicationContext());
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, GroupMemberList_Activity.this);
        System.out.println("jsonArray" + jsonObject);
    }

    private void setToolbar() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        title_name = (TextView) findViewById(R.id.title_name);
        title_content = (TextView) findViewById(R.id.content_name);
        nocontenttext = (TextView) findViewById(R.id.novaluetext);

        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        title_name.setTypeface(tf1);
        title_content.setTypeface(tf1);
        nocontenttext.setTypeface(tf1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_display_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
                intent.setAction("");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.done:
//                Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
//                startActivity(intent);
                if (getIntent().getAction().equalsIgnoreCase("CreateGroupChatActivity")) {
                    Log.d(TAG, " CreateGroupChatActivity : -" + " first time ");
                    registergroupmember();
                    QBChatDialog dialog = qbService.getGrpDialog();
                    Log.v(TAG, " Chat Dialog after grp created :- " + dialog);

                } else if (getIntent().getAction().equalsIgnoreCase("ViewGroupDetail_Activity")) {
                    Log.d(TAG, " ViewGroupDetail_Activity : -" + " not first time ");
//dffffffffffffff
                    getAllPrevieusUsersAddInGroup();


                }


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void backResponse(String response) {
        Log.d("backResponseresponse", response);
        //  mProgressDialog.dismiss();
        if (response != null) {
            try {
                JSONObject mainObject = new JSONObject(response);

                if (mainObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray orderArray = mainObject.getJSONArray("result");

                    for (int i = 0; orderArray.length() > i; i++) {
                        JSONObject topObject = orderArray.getJSONObject(i);
                        allBeans = new AllBeans();
                        allBeans.setFriendid(topObject.getString("speaka_id"));
                        allBeans.setFriendname(topObject.getString("person_name"));
                        allBeans.setFriendmobile(topObject.getString("speaka_number"));
                        allBeans.setFriendimage(topObject.getString("user_image"));
                        allBeans.setFriendStatus(topObject.getString("userProfileStatus"));
                        allBeans.setFriendQB_id(Integer.parseInt(topObject.getString("qb_id")));

                        Log.v(TAG, "QB id of freind:-" + allBeans.getFriendQB_id());
                        Log.d(TAG, " get friend user name from get check list : -- " + topObject.getString("person_name"));
                        if (memberIdList != null) {
                            if (memberIdList.contains(Integer.parseInt(topObject.getString("speaka_id")))) {
                                allBeans.setSelected(true);
                            } else {
                                allBeans.setSelected(false);
                                friendlist.add(allBeans);
                            }

                        } else {
                            friendlist.add(allBeans);

                        }
                        //////Sorting name////////
                        Collections.sort(friendlist, new Comparator<AllBeans>() {
                            @Override
                            public int compare(AllBeans lhs, AllBeans rhs) {
                                return lhs.getFriendname().compareTo(rhs.getFriendname());
                            }
                        });
                        //////Sorting name////////

                    }
                    if (friendlist != null) {
                        newGroupMember_adapter = new NewGroupMember_Adapter(getApplicationContext(), friendlist);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(GroupMemberList_Activity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(newGroupMember_adapter);
                    }
                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                    nocontenttext.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    //    Toast.makeText(getApplicationContext(), "No data found in Menu section", Toast.LENGTH_LONG).show();
                }
                // mProgressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //  mProgressDialog.dismiss();

    }

    private void registergroupmember() {
        //listvalue.put(newGroupMember_adapter.stringArrayList);

      /*  mProgressDialog = new SpotsDialog(GroupMemberList_Activity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();*/
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            newGroupMember_adapter.stringArrayList.addAll(memberIdList);
        } catch (NullPointerException e) {
        }

        try {
            jsonObject.put("method", "singleGroupView");
            jsonObject.put("group_id", GroupId);
            jsonObject.put("user_id", AppPreferences.getLoginId(GroupMemberList_Activity.this));
            jsonObject.put("group_frnd_list", newGroupMember_adapter.stringArrayList);

            jsonArray.put(jsonObject);
            System.out.println("memberList" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONParser jsonParser = new JSONParser(GroupMemberList_Activity.this);

        jsonParser.parseVollyJsonArray(AppConstants.USERGROUPURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.d(TAG, "response singleGroupView :- " + response);
                Log.d(TAG, "Memeber list :- " + memberIdList);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {

                            String msg = "";

                            JSONArray orderArray = mainObject.getJSONArray("result");
//
                            ArrayList<Integer> occupantIdsList = newGroupMember_adapter.getFriendQbIdList();
                            Log.v(TAG, "Friend List :- " + occupantIdsList + " GroupId : -- " + GroupId);

//dsvvvvvvvvvvvdsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                            createGroupInQuickBlox(GroupMemberList_Activity.this, occupantIdsList, GroupName, GroupId, GroupImagePicture);

                            Log.v(TAG, " Chat Dialog after grp created  inside :- " + groupChatDialog);

                            if (memberIdList == null) {

                                msg = "Group cannnot be created ";
                            } else {
//                                creategroup = activity.getmService().xmpp.addNewMemberInGroup(GroupName, GroupId, AppPreferences.getMobileuser(GroupMemberList_Activity.this), NewGroupMember_Adapter.contactArrayList);
//                                msg = "member not added";
                            }

/*

                            if (creategroup) {

                                QBChatDialog dialog = qbService.getGrpDialog();

                                Intent intent = new Intent(GroupMemberList_Activity.this, TwoTab_Activity.class);
                                intent.setAction("GroupMember");
                                intent.putExtra("groupimage", GroupImagePicture);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(GroupMemberList_Activity.this, msg, Toast.LENGTH_SHORT).show();
                            }
*/

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            // Toast.makeText(getApplicationContext(), "Alerady accepted", Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content), "Not Accepted", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            // Toast.makeText(getApplicationContext(), "Alerady accepted", Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content), "Server issue", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  mProgressDialog.dismiss();
                }

            }
        });
        System.out.println("AppConstants.URL.MENU_DETAIL" + AppConstants.COMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }


//////////////******************************************************************************************************//

    public void createGroupInQuickBlox(final Context context, final ArrayList<Integer> occupantIdsList, final String GroupName, final String GroupId, final String groupImagePicture) {

//        qbService.registerQbChatListeners();
        Log.v(TAG, "Inside Group create using QB service :- " + occupantIdsList + " -- Name :-  " + GroupName);
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(GroupName);
        dialog.setPhoto(groupImagePicture);
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);
//dvsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog dialog, Bundle args) {
                groupChatDialog = dialog;

                Log.v(TAG, "dialog 1 :- " + groupChatDialog);
                Log.v(TAG, "dialog 2 :- " + dialog.getDialogId());
                Log.v(TAG, "dialog 3 :- " + dialog.getOccupants());
                Log.v(TAG, "dialog 3 :- " + dialog.getName());

                qbService.initChatMessageDatabase(context, GroupName, GroupId, groupImagePicture, groupChatDialog);
//dvssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
                Log.v(TAG, " Chat Dialog after grp created 12334:- " + dialog);
                Intent intent = new Intent(GroupMemberList_Activity.this, TwoTab_Activity.class);
                intent.setAction("GroupMember");
                intent.putExtra("groupimage", GroupImagePicture);
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, groupChatDialog);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e(TAG, "QB Error creating group dialog :- " + errors.getMessage());
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
        intent.setAction("");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //**********************************************************************************************//

    public void getConatactList() {

        ArrayList<Contact> listContacts = new ContactFetcher(GroupMemberList_Activity.this).fetchAll();

        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.d(TAG, "ContactFetch :- " + contact.name + "::" + phone.number);

                String number = phone.number.replace("-", "").replace(" ", "");

                if (number.replace("-", "").replace(" ", "").length() > 10) {

                    Log.v(TAG, "Length_10 if Length is more than 10 :- " + contact.name + "::" + phone.number);

                    number = getLastnCharacters(number, 10);

                    Log.v(TAG, "Length_10 Getting last 10 characters from string  :- " + number);
                } else {
                    number = phone.number;
                }

                Log.v(TAG, "Length_10 Phone number after conditions :- " + number);

                alContactsnumber.put(number);
                alContactsname.put(contact.name);
            }
        }

        sendallcontact();
        //checkPermission();

//        DatabaseHelper databaseHelper = new DatabaseHelper(this);


    }

    //////////////******************************************************************************************************//
    public String getLastnCharacters(String inputString,
                                     int subStringLength) {
        int length = inputString.length();
        if (length <= subStringLength) {
            return inputString;
        }
        int startIndex = length - subStringLength;
        return inputString.substring(startIndex);
    }

    private void sendallcontact() {

//        7313398300
        Log.v(TAG, "Contact Array List Number :- " + alContactsnumber.toString());
        Log.v(TAG, "Contact Array List Name :- " + alContactsname.toString());
        mProgressDialog = new SpotsDialog(GroupMemberList_Activity.this);
        mProgressDialog.setTitle("Your contact is updating...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.CHECKLIST);
            jsonObject.put("user_id", AppPreferences.getLoginId(GroupMemberList_Activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(GroupMemberList_Activity.this));
            jsonObject.put("contactNumber", alContactsnumber);
            jsonObject.put("contactName", alContactsname);
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));
            jsonArray.put(jsonObject);
            Log.d(TAG, "JSON REQUEST CHECKLIST :- " + jsonArray.toString());
            System.out.println("sendallcontact>>>>>" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(GroupMemberList_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {
                Log.d(TAG, "JSON RESPONSE CHECKLIST :- " + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setAckwnoledge(GroupMemberList_Activity.this, topObject.getString("acknoledgeinsert"));
                                System.out.println("valueallcontact" + AppPreferences.getAckwnoledge(GroupMemberList_Activity.this));
                            }
                            callGetCheckListAPi();
                            mProgressDialog.dismiss();
                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            Toast.makeText(getApplicationContext(), "Contact Not Updated", Toast.LENGTH_LONG).show();
                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }
                mProgressDialog.dismiss();
            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonArray);
    }

    public void updateGroupMember(String dialogeIdSend, final ArrayList<Integer> qbIdArrayList, final ArrayList<String> speakaIdFromServerUserAddInGroup, final ArrayList<String> personNameArrayList) {

        Log.d(TAG, " newGroupMember_adapter.stringArrayList 11: -- " + newGroupMember_adapter.stringArrayList);
        Log.d(TAG, " newGroupMember_adapter.stringArrayList 22: -- " + memberIdList);
        Log.d(TAG, " newGroupMember_adapter.stringArrayList 33: -- " + speakaIdFromServerUserAddInGroup);
        Log.d(TAG, " newGroupMember_adapter.stringArrayList 44: -- " + personNameArrayList);
        Log.d(TAG, " newGroupMember_adapter.stringArrayList 55: -- " + newGroupMember_adapter.contactNumberArrayList);
        try {
            newGroupMember_adapter.stringArrayList.addAll(memberIdList);
        } catch (NullPointerException e) {
        }

        Log.d(TAG, " qbIdFromServerUserAddInGroup Method : -- " + qbIdArrayList.toString());

        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
        ArrayList<Integer> occupantIdsList = newGroupMember_adapter.getFriendQbIdList();
        for (final Integer occupantId : occupantIdsList) {
            Log.d(TAG, " Occupant Id from : " + occupantId);
            requestBuilder.addUsers(occupantId); // add another users
        }
        Log.d(TAG, "requestBuilder : -- " + requestBuilder);
        final Gson gson = new Gson();
// requestBuilder.removeUsers(22); // Remove yourself (user with ID 22)
        byte[] chatbytes = DatabaseHelper.getInstance(GroupMemberList_Activity.this).getChatDialogUsingDialogID(dialogeIdSend);

        final QBChatDialog chatDialog = SerializationUtils.deserialize(chatbytes); // dialog should contain dialogId
        Log.d(TAG, " On Succes method 11: -- " + chatDialog);

        chatDialog.setName("Team room");
        chatDialog.setType(QBDialogType.GROUP);

        QBRestChatService.updateGroupChatDialog(chatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                Log.d(TAG, " On Succes method 22: -- " + qbChatDialog);
                Log.d(TAG, " On Succes method 33: -- " + chatDialog);
                Log.d(TAG, " On Succes method mobile no: -- " + AppPreferences.getMobileUserWithoutCountry(getApplicationContext()));

                Log.d(TAG, " getOccupatns idss : -- " + chatDialog.getOccupants() + " :New: " + qbChatDialog.getOccupants());

                for (Integer userID : qbChatDialog.getOccupants()) {

                    Log.d(TAG, " QbSystemMessageManager : -- " + userID);
                    Log.d(TAG, " QbSystemMessageManager Name : -- " + userID + " : " + qbChatDialog.getName());

                    if (!userID.equals(AppPreferences.getQBUserId(getApplicationContext()))) {
                        //ChatMessage chatMessageNew = null;
                        if (qbIdArrayList.contains(userID)) {
                            for (int j = 0; j < newGroupMember_adapter.contactNumberArrayList.size(); j++) {
                                TimeZone timezone = TimeZone.getDefault();
                                QBSystemMessagesManager systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                                QBChatMessage chatMessage = createChatNotificationForGroupChatUpdate(qbChatDialog);

                                Random random = new Random();
                                String chatRoomId1 = getOnlyStrings(GroupName.trim());
                                String chatRoomId = chatRoomId1.replace(" ", "_");
                                String time = CommonMethods.getCurrentTime();
                                chatMessage.setProperty("date_sent", time + "");

                                String[] str = newGroupMember_adapter.contactNumberArrayList.get(j).split(" ");
                                String number = str[1];
                                Log.d(TAG, " Split number : -- " + number + " : " + str[0]);
                                Log.d(TAG, " condition match ... " + qbIdArrayList + " : " + userID);
                                ChatMessage chatMessageNew = new ChatMessage(AppPreferences.getMobileuser(getApplicationContext()),
                                        AppPreferences.getFirstUsername(getApplicationContext()),
                                        chatRoomId, chatRoomId,
                                        GroupName, "" + number + "  added in group by " + AppPreferences.getMobileUserWithoutCountry(getApplicationContext()), "" + random.nextInt(1000), "", true);

                                chatMessageNew.setMsgID();
                                chatMessageNew.Date = CommonMethods.getCurrentDate();
                                chatMessageNew.Time = CommonMethods.getCurrentTime();
                                chatMessageNew.type = Message.Type.groupchat.name();
                                chatMessageNew.groupid = GroupId;
                                chatMessageNew.Groupimage = "";
                                chatMessageNew.isOtherMsg = 1;
                                chatMessageNew.dialog_id = qbChatDialog.getDialogId();
                                chatMessageNew.dateInLong = Calendar.getInstance().getTimeInMillis();
                                chatMessageNew.timeZone = timezone.getID();
                                String body = gson.toJson(chatMessageNew);
                                chatMessage.setProperty("custom_body", body);
                                chatMessage.setRecipientId(userID);
                                try {
                                    systemMessagesManager.sendSystemMessage(chatMessage);
                                    Log.d(TAG, " CreatechatNotificationchatMessageIf try: " + chatMessage);

                                } catch (SmackException.NotConnectedException e) {
                                    Log.d(TAG, " CreatechatNotificationchatMessageNotConnectedException : " + chatMessage);
                                }
                            }
                        } else {//if (!qbIdArrayList.contains(userID))
                            for (int k = 0; k < 2; k++) {
                                if (k == 0) {
                                    TimeZone timezone = TimeZone.getDefault();
                                    QBSystemMessagesManager systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                                    QBChatMessage chatMessage = createChatNotificationForGroupChatUpdate(qbChatDialog);

                                    Random random = new Random();
                                    String chatRoomId1 = getOnlyStrings(GroupName.trim());
                                    String chatRoomId = chatRoomId1.replace(" ", "_");
                                    String time = CommonMethods.getCurrentTime();
                                    chatMessage.setProperty("date_sent", time + "");

                                    Log.d(TAG, " condition not match ... " + qbIdArrayList + " : " + userID);
                                    ChatMessage chatMessageNew = new ChatMessage(AppPreferences.getMobileuser(getApplicationContext()),
                                            AppPreferences.getFirstUsername(getApplicationContext()),
                                            chatRoomId, chatRoomId,
                                            GroupName, "Hi welcome to " + GroupName + " group", "" + random.nextInt(1000), "", true);

                                    chatMessageNew.setMsgID();
                                    chatMessageNew.Date = CommonMethods.getCurrentDate();
                                    chatMessageNew.Time = CommonMethods.getCurrentTime();
                                    chatMessageNew.type = Message.Type.groupchat.name();
                                    chatMessageNew.groupid = GroupId;
                                    chatMessageNew.Groupimage = "";
                                    chatMessageNew.isOtherMsg = 1;
                                    chatMessageNew.dialog_id = qbChatDialog.getDialogId();
                                    chatMessageNew.dateInLong = Calendar.getInstance().getTimeInMillis();
                                    chatMessageNew.timeZone = timezone.getID();
                                    String body = gson.toJson(chatMessageNew);
                                    chatMessage.setProperty("custom_body", body);
                                    chatMessage.setRecipientId(userID);
                                    try {
                                        systemMessagesManager.sendSystemMessage(chatMessage);
                                        Log.d(TAG, " CreatechatNotificationchatMessageElse try: " + chatMessage);

                                    } catch (SmackException.NotConnectedException e) {
                                        Log.d(TAG, " CreatechatNotificationchatMessageNotConnectedException : " + chatMessage);
                                    }
                                } else if (k == 1) {
                                    TimeZone timezone = TimeZone.getDefault();
                                    //-----------------------------------------------------------------------------//
                                    QBSystemMessagesManager systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

                                    QBChatMessage chatMessageNew1 = createChatNotificationForGroupChatUpdate(qbChatDialog);

                                    Random randomNew1 = new Random();
                                    String chatRoomId1New1 = getOnlyStrings(GroupName.trim());
                                    String chatRoomIdNew1 = chatRoomId1New1.replace(" ", "_");
                                    String timeNew1 = CommonMethods.getCurrentTime();
                                    chatMessageNew1.setProperty("date_sent", timeNew1 + "");

                                    Log.d(TAG, " condition not match ... " + qbIdArrayList + " : " + userID);
                                    ChatMessage chatMessageNewNew1 = new ChatMessage(AppPreferences.getMobileuser(getApplicationContext()),
                                            AppPreferences.getFirstUsername(getApplicationContext()),
                                            chatRoomIdNew1, chatRoomIdNew1,
                                            GroupName, AppPreferences.getMobileUserWithoutCountry(getApplicationContext()) + " added you", "" + randomNew1.nextInt(1000), "", true);

                                    chatMessageNewNew1.setMsgID();
                                    chatMessageNewNew1.Date = CommonMethods.getCurrentDate();
                                    chatMessageNewNew1.Time = CommonMethods.getCurrentTime();
                                    chatMessageNewNew1.type = Message.Type.groupchat.name();
                                    chatMessageNewNew1.groupid = GroupId;
                                    chatMessageNewNew1.Groupimage = "";
                                    chatMessageNewNew1.isOtherMsg = 1;
                                    chatMessageNewNew1.dialog_id = qbChatDialog.getDialogId();
                                    chatMessageNewNew1.dateInLong = Calendar.getInstance().getTimeInMillis();
                                    chatMessageNewNew1.timeZone = timezone.getID();
                                    String bodyNew1 = gson.toJson(chatMessageNewNew1);
                                    chatMessageNew1.setProperty("custom_body", bodyNew1);
                                    chatMessageNew1.setRecipientId(userID);
                                    try {
                                        systemMessagesManager.sendSystemMessage(chatMessageNew1);
                                        Log.d(TAG, " CreatechatNotificationchatMessageElse try: " + chatMessageNew1);

                                    } catch (SmackException.NotConnectedException e) {
                                        Log.d(TAG, " CreatechatNotificationchatMessageNotConnectedException : " + chatMessageNew1);
                                    }
                                }
                            }
                        }
                    }
                }

                Log.d(TAG, " newGroupMember_adapter.stringArrayList 44: -- " + newGroupMember_adapter.stringArrayList);

                updateGroupMemberOnServer(newGroupMember_adapter.stringArrayList);

                Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
                intent.setAction("GroupMember");
                intent.putExtra("groupimage", GroupImagePicture);
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, qbChatDialog);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
//        }
    }
    //------------------------------------------------------------------------------------------------------------//

    //..................................................................................................................//
    public void getAllPrevieusUsersAddInGroup() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        final ArrayList<Integer> qbIdFromServerUserAddInGroup = new ArrayList<>();
        final ArrayList<String> speakaIdFromServerUserAddInGroup = new ArrayList<>();
        final ArrayList<String> personNameArrayList = new ArrayList<>();
        try {
            jsonObject.put("method", AppConstants.USERMAKEGROUPDETAIL);
            jsonObject.put("user_id", AppPreferences.getLoginId(getApplicationContext()));
            jsonObject.put("group_id", GroupId);
            jsonArray.put(jsonObject);

            System.out.println("groupmemberlist" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(getApplicationContext());
        jsonParser.parseVollyJsonArray(AppConstants.USERGROUPURL, 1, jsonArray, new VolleyCallback() {

            @Override
            public void backResponse(String response) {
                Log.d(TAG, "response : -- " + response);
                //  mProgressDialog.dismiss();

                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            //  isAdmin = mainObject.getString("isAdmin");
                            // updateAdminUi(isAdmin);
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                allBeans = new AllBeans();
                                allBeans.setFriendid(topObject.getString("speaka_id"));
                                allBeans.setFriendname(topObject.getString("person_name"));
                                allBeans.setFriendmobile(topObject.getString("speaka_number").replace(" ", "").replace("+", ""));
                                allBeans.setFriendimage(topObject.getString("user_image"));
                                allBeans.setFriendStatus(topObject.getString("userProfileStatus"));
                                allBeans.setGroupCreateDate(topObject.getString("group_create"));
                                allBeans.setGroupAdminStatus(topObject.getString("admin_id"));

                                String qbId = topObject.getString("qb_id");
                                String speaka_id = topObject.getString("speaka_id");
                                String personName = topObject.getString("person_name");
                                Log.d(TAG, "qbIdFromServierUserAddInGroup : == " + qbId + " : " + speaka_id + " : " + personName);
                                qbIdFromServerUserAddInGroup.add(Integer.valueOf(qbId));
                                speakaIdFromServerUserAddInGroup.add(speaka_id);
                                personNameArrayList.add(personName);
                                //   friendlist.add(allBeans);
                                //  stringArrayList.add(Integer.valueOf(topObject.getString("speaka_id")));
                            }
                            updateGroupMember(dialogeIdSend, qbIdFromServerUserAddInGroup, speakaIdFromServerUserAddInGroup, personNameArrayList);
                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {


                            //    Toast.makeText(ViewGroupDetail_Activity.this, "No data found in Menu section", Toast.LENGTH_LONG).show();
                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // mProgressDialog.dismiss();
                }

            }
        });


    }

    private void updateGroupMemberOnServer(List<Integer> group_frnd_list) {
        Log.d(TAG, " updateGroupMemberOnServer METHOD : -- " + group_frnd_list);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();


        try {
            jsonObject.put("method", "singleGroupView");
            jsonObject.put("group_id", GroupId);
            jsonObject.put("user_id", AppPreferences.getLoginId(GroupMemberList_Activity.this));
            jsonObject.put("group_frnd_list", group_frnd_list);

            jsonArray.put(jsonObject);
            System.out.println("memberList" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONParser jsonParser = new JSONParser(GroupMemberList_Activity.this);

        jsonParser.parseVollyJsonArray(AppConstants.USERGROUPURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.d(TAG, "response singleGroupView update :- " + response);
                // Log.d(TAG, "Memeber list update:- " + memberIdList);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            // Toast.makeText(getApplicationContext(), "Alerady accepted", Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content), "Not Accepted", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            // Toast.makeText(getApplicationContext(), "Alerady accepted", Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content), "Server issue", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  mProgressDialog.dismiss();
                }

            }
        });

    }

    //----------------------------------------------------------------------------------------------------------//

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }
}