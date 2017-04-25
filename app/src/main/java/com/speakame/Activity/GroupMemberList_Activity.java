package com.speakame.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.speakame.Adapter.NewGroupMember_Adapter;
import com.speakame.Beans.AllBeans;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.Services.XmppConneceted;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dmax.dialog.SpotsDialog;


public class GroupMemberList_Activity extends AnimRootActivity implements VolleyCallback {
    public static TextView title_content;
    public TextView nocontenttext, title_name;
    RecyclerView recyclerView;
    ArrayList<AllBeans> friendlist;
    AlertDialog mProgressDialog;
    NewGroupMember_Adapter newGroupMember_adapter;
    AllBeans allBeans;
    String GroupName, GroupId, GroupImagePicture;
    //JSONArray listvalue = new JSONArray();

    ArrayList<Integer> memberIdList;

    public static void updatemember(int count) {
        title_content.setText("Add member(" + count + " member)");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        title_name = (TextView) findViewById(R.id.title_name);
        title_content = (TextView) findViewById(R.id.content_name);
        nocontenttext = (TextView) findViewById(R.id.novaluetext);

        memberIdList = new ArrayList<Integer>();

        title_name.setText("New Group");
        title_content.setText("Add member(0 member)");
        Intent intent = getIntent();
        GroupName = intent.getStringExtra("groupname");
        GroupId = intent.getStringExtra("groupid");
        GroupImagePicture = intent.getStringExtra("groupimage");
        System.out.println("imggrp" + GroupImagePicture);
        memberIdList = intent.getIntegerArrayListExtra("groupmemberid");
        title_name.setText(GroupName);

        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        title_name.setTypeface(tf1);
        title_content.setTypeface(tf1);
        nocontenttext.setTypeface(tf1);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());


        friendlist = new ArrayList<AllBeans>();
        mProgressDialog = new SpotsDialog(GroupMemberList_Activity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.done:
//                Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
//                startActivity(intent);

                registergroupmember();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void backResponse(String response) {
        Log.d("response", response);
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


            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();
        }


    }

    private void registergroupmember() {
        //listvalue.put(newGroupMember_adapter.stringArrayList);

        mProgressDialog = new SpotsDialog(GroupMemberList_Activity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            newGroupMember_adapter.stringArrayList.addAll(memberIdList);
        }catch (NullPointerException e){}

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


                Log.d("response>>>>>", response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            XmppConneceted activity = new XmppConneceted();

                            if (memberIdList == null) {
                                activity.getmService().xmpp.createGroupChat(GroupName, GroupId, AppPreferences.getMobileuser(GroupMemberList_Activity.this), NewGroupMember_Adapter.contactArrayList, GroupImagePicture);
                            } else {
                                activity.getmService().xmpp.addNewMemberInGroup(GroupName, GroupId, AppPreferences.getMobileuser(GroupMemberList_Activity.this), NewGroupMember_Adapter.contactArrayList);
                            }
                            Intent intent = new Intent(GroupMemberList_Activity.this, TwoTab_Activity.class);
                            intent.setAction("");
                            intent.putExtra("groupimage", GroupImagePicture);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            // Toast.makeText(getApplicationContext(), "Alerady accepted", Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content), "Not Accepted", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            // Toast.makeText(getApplicationContext(), "Alerady accepted", Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content), "No network connection", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }

            }
        });
        System.out.println("AppConstants.URL.MENU_DETAIL" + AppConstants.COMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
