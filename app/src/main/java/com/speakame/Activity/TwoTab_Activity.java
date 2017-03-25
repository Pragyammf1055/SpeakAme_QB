package com.speakame.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.speakame.Adapter.BroadcastnewgroupAdapter;
import com.speakame.Adapter.GroupListAdapter;
import com.speakame.Beans.AllBeans;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.HomeService;
import com.speakame.Services.XmppConneceted;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.CallBackUi;
import com.speakame.utils.VolleyCallback;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.speakame.Xmpp.MyXMPP.numMessages;

public class TwoTab_Activity extends AnimRootActivity implements VolleyCallback {
    private static final String TAG = "TwoTab_Activity";
    public static BroadcastnewgroupAdapter adapter;
    public static TwoTab_Activity instance = null;
    static ListView chatlist;
    static List<ChatMessage> chatMessageList;
    TextView toolbartext, firsttab, secondtab, warningtext;
    RelativeLayout lay_desc, sec_tab;
    RecyclerView recyclerViewgroup;
    ArrayList<String> reciver = new ArrayList<String>();
    ArrayList<ChatMessage> mchatemessage;
    GroupListAdapter groupListAdapter;
    EditText srch_edit;
    boolean isSerch = true;
    ArrayList<AllBeans> friendlist;
    AllBeans allBeans;
    AlertDialog mProgressDialog;
    ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star, star_blue, user, user_blue;
    public static CallBackUi callBackUi;

    public static void updateList(String groupName) {
        chatMessageList = new ArrayList<ChatMessage>();
        callBackUi.update(groupName);
        if (groupName.equalsIgnoreCase("")) {
            chatMessageList = DatabaseHelper.getInstance(instance).getReciever();
        } else {
            chatMessageList = DatabaseHelper.getInstance(instance).getGroup();
        }
        System.out.println("chatMessageList : " + chatMessageList.toString());
        adapter = new BroadcastnewgroupAdapter(instance, chatMessageList);
        chatlist.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_tab_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("SpeakAme");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        instance = this;
        numMessages = 0;

        /*ListCountry listCountry = new ListCountry();
        String[] countryList = listCountry.run();
        for (String countryCode : countryList) {

            Locale obj = new Locale("", countryCode);

          *//*  System.out.println("Languagesss : Country Code = " + obj.getCountry()
                    + ", Country Name = " + obj.getDisplayCountry());*//*

            final Locale[] availableLocales = obj.getAvailableLocales();
            for(final Locale locale : availableLocales)
                Log.d("Languagesss::",":"+locale.getDisplayName()+":"+locale.getLanguage()+":"
                        +locale.getCountry()+":values-"+locale.toString().replace("_","-r"));

        }*/
       /* ListCountry country = new ListCountry();
        List<String> locales = country.getAllLanguages();
        for(final String locale : locales)
          Log.d("Languagesss:>>:", locale +"::"+country.getCode(locale));*/

        //  if (!Function.isServiceRunning(instance, XmppConneceted.class.getName())) {
        startService(new Intent(instance, XmppConneceted.class));
        startService(new Intent(TwoTab_Activity.this, HomeService.class));
        //  }

        firsttab = (TextView) findViewById(R.id.broadcast);
        secondtab = (TextView) findViewById(R.id.newgroup);
        lay_desc = (RelativeLayout) findViewById(R.id.layout_desc);
        sec_tab = (RelativeLayout) findViewById(R.id.sec_tab);
        chatlist = (ListView) findViewById(R.id.broadcastlist);
        recyclerViewgroup = (RecyclerView) findViewById(R.id.recycler_view_group);
        srch_edit = (EditText) findViewById(R.id.serch_edit);
        language = (ImageView) findViewById(R.id.iv_language);
        language_blue = (ImageView) findViewById(R.id.iv_bluelanguage);
        chat = (ImageView) findViewById(R.id.iv_chat_footer);
        chat_blue = (ImageView) findViewById(R.id.iv_chatbluefooter);
        setting = (ImageView) findViewById(R.id.iv_setting);
        setting_blue = (ImageView) findViewById(R.id.iv_bluesetting);
        star = (ImageView) findViewById(R.id.iv_star);
        star_blue = (ImageView) findViewById(R.id.iv_starblue);
        user = (ImageView) findViewById(R.id.iv_user_footer);
        user_blue = (ImageView) findViewById(R.id.iv_userbluefooter);
        warningtext = (TextView) findViewById(R.id.alerttext);
        chat_blue.setVisibility(View.VISIBLE);
        chat.setVisibility(View.GONE);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "Raleway-SemiBold.ttf");
        firsttab.setTypeface(tf2);
        secondtab.setTypeface(tf2);
        warningtext.setTypeface(tf2);

        recyclerViewgroup.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        friendlist = new ArrayList<AllBeans>();

        if (friendlist != null) {
            friendlist.clear();
        }

        if (AppPreferences.getTotf(TwoTab_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(TwoTab_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);

        }
        System.out.println("totfvalue:-" + AppPreferences.getTotf(TwoTab_Activity.this));

        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(TwoTab_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(TwoTab_Activity.this, "0");
        }

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(TwoTab_Activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(TwoTab_Activity.this, "1");
                user_blue.setVisibility(View.GONE);
                return true;
            }
        });


        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_blue.setVisibility(View.VISIBLE);
                language.setVisibility(View.GONE);
                chat_blue.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
                Intent intent = new Intent(TwoTab_Activity.this, Languagelearn_activity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting.setVisibility(View.GONE);
                setting_blue.setVisibility(View.VISIBLE);
                chat_blue.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
                Intent intent = new Intent(TwoTab_Activity.this, Setting_Activity.class);
                startActivity(intent);
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star.setVisibility(View.GONE);
                star_blue.setVisibility(View.VISIBLE);
                chat_blue.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
                Intent intent = new Intent(TwoTab_Activity.this, Favoirite_Activity.class);
                startActivity(intent);
            }
        });
//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                user.setVisibility(View.GONE);
//                user_blue.setVisibility(View.VISIBLE);
//                chat_blue.setVisibility(View.GONE);
//                chat.setVisibility(View.VISIBLE);
//                Intent intent = new Intent(TwoTab_Activity.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });


        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("1", chatMessageList.get(position).receiver);
                AllBeans allBeans = new AllBeans();
                allBeans.setFriendname(chatMessageList.get(position).reciverName);
                allBeans.setFriendStatus("");
                allBeans.setFriendmobile(chatMessageList.get(position).receiver);
                allBeans.setGroupName(chatMessageList.get(position).groupName);
                allBeans.setGroupid(chatMessageList.get(position).groupid);
                allBeans.setLanguages(chatMessageList.get(position).reciverlanguages);
                allBeans.setFriendimage(chatMessageList.get(position).ReciverFriendImage);
                allBeans.setGroupImage(chatMessageList.get(position).Groupimage);

                ArrayList<AllBeans> beansArrayList = new ArrayList<AllBeans>();
                beansArrayList.add(allBeans);

                Intent intent = new Intent(TwoTab_Activity.this, ChatActivity.class);
                intent.putExtra("value", beansArrayList.get(0));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //  intent.putExtra("message", chatMessageList.get(position).body);
                startActivity(intent);
            }
        });


        chatlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                Log.v("BroadcastAdapter ", " position of list item :- " + chatMessageList.get(position));
                Log.v("BroadcastAdapter ", " Name of the friend in list :- " + chatMessageList.get(position).reciverName);
                Log.v("BroadcastAdapter ", " Number of the friend in list :- " + chatMessageList.get(position).receiver);

                AlertDialog.Builder builder = new AlertDialog.Builder(TwoTab_Activity.this);
                builder.setMessage("Delete Chat with" + " \"" + chatMessageList.get(position).reciverName + "\" ? ")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                String grpName = chatMessageList.get(position).reciverName;
                                String key_receiver = chatMessageList.get(position).receiver;
                                String key_id = chatMessageList.get(position).msgid;

                                Log.d("deleteChat", "Group name :- " + grpName);
                                Log.d("deleteChat", "key_receiver :- " + key_receiver);
                                Log.d("deleteChat", "key_id :- " + key_id);

                                new DatabaseHelper(TwoTab_Activity.this).deleteFriendFromList(key_receiver);
                                chatMessageList.remove(chatMessageList.get(position));
                                Log.w("TWO_TAB", "Size after delete item :- " + chatMessageList.size());
                                adapter.notifyDataSetChanged();
                                chatlist.invalidateViews();
                                adapter.notifyDataSetInvalidated();

                                Log.w("TWO_TAB", "Size after delete item :- " + chatMessageList.size());
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });


        firsttab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningtext.setVisibility(View.GONE);
                firsttab.setTextColor(getResources().getColor(R.color.colorwhite));
                secondtab.setTextColor(getResources().getColor(R.color.colorPrimary));
                firsttab.setBackgroundResource(R.drawable.left_rounded_corner_bg_layout);
                secondtab.setBackgroundResource(R.drawable.rounded_corner_bg_layout);


                if (chatMessageList != null) {
                    chatMessageList.clear();
                }
                    chatMessageList = DatabaseHelper.getInstance(TwoTab_Activity.this).getReciever();
                    adapter = new BroadcastnewgroupAdapter(instance, chatMessageList);
                    chatlist.setAdapter(adapter);

                    Log.d("query>>>>", chatMessageList.toString());

            }
        });

        secondtab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lay_desc.setVisibility(View.GONE);
                // sec_tab.setVisibility(View.VISIBLE);
                firsttab.setTextColor(getResources().getColor(R.color.colorPrimary));
                secondtab.setTextColor(getResources().getColor(R.color.colorwhite));
                firsttab.setBackgroundResource(R.drawable.rounded_corner_bg_layout);
                secondtab.setBackgroundResource(R.drawable.left_rounded_corner_bg_layout);


                if (chatMessageList != null) {
                    chatMessageList.clear();
                }
                    chatMessageList = DatabaseHelper.getInstance(TwoTab_Activity.this).getGroup();
                    adapter = new BroadcastnewgroupAdapter(instance, chatMessageList);
                    chatlist.setAdapter(adapter);



            }
        });

        callBackUi = new CallBackUi() {
            @Override
            public void update(String s) {
                if(s.equalsIgnoreCase("")){
                    firsttab.setTextColor(getResources().getColor(R.color.colorwhite));
                    secondtab.setTextColor(getResources().getColor(R.color.colorPrimary));
                    firsttab.setBackgroundResource(R.drawable.left_rounded_corner_bg_layout);
                    secondtab.setBackgroundResource(R.drawable.rounded_corner_bg_layout);
                }else{
                    firsttab.setTextColor(getResources().getColor(R.color.colorPrimary));
                    secondtab.setTextColor(getResources().getColor(R.color.colorwhite));
                    firsttab.setBackgroundResource(R.drawable.rounded_corner_bg_layout);
                    secondtab.setBackgroundResource(R.drawable.left_rounded_corner_bg_layout);
                }
            }
        };

        searchFriend();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            if (isSerch) {
                isSerch = false;
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.cross);
                srch_edit.setVisibility(View.VISIBLE);
            } else {
                isSerch = true;
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);
                srch_edit.setVisibility(View.GONE);
            }

            return true;
        }
        if (id == R.id.action_contact) {

            Intent inten = new Intent(TwoTab_Activity.this, ContactImport_Activity.class);
            startActivity(inten);

            return true;
        }
        if (id == R.id.action_menu) {
            View menuItemView = findViewById(R.id.action_contact);
            PopupMenu popup = new PopupMenu(this, menuItemView);
            popup.getMenuInflater().inflate(R.menu.menu_tabitem, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.helpfedback:

                            Intent intent = new Intent(TwoTab_Activity.this, HelpFeedback_Activity.class);
                            startActivity(intent);
                            break;

                        case R.id.addcontact:
                            Intent intent1 = new Intent(TwoTab_Activity.this, SocialContactActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.creategroup:
                            Intent intent2 = new Intent(TwoTab_Activity.this, CreateGroupChatActivity.class);
                            startActivity(intent2);
                            break;

                    }
                    return true;
                }
            });
            popup.show();
            return true;
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
                        allBeans.setGroupid(topObject.getString("group_id"));
                        allBeans.setGroupName(topObject.getString("group_name"));
                        allBeans.setGroupImage(topObject.getString("group_image"));
                        allBeans.setGroupCreateDate(topObject.getString("group_create_date"));
                        allBeans.setGroupCreateTime(topObject.getString("group_create_time"));

                        friendlist.add(0, allBeans);
                    }
                    if (friendlist != null) {
                        groupListAdapter = new GroupListAdapter(getApplicationContext(), friendlist);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TwoTab_Activity.this);
                        recyclerViewgroup.setLayoutManager(mLayoutManager);
                        recyclerViewgroup.setHasFixedSize(true);
                        recyclerViewgroup.addItemDecoration(new VerticalSpaceItemDecoration(5));
                        recyclerViewgroup.setItemAnimator(new DefaultItemAnimator());
                        recyclerViewgroup.setAdapter(groupListAdapter);
                        groupListAdapter.notifyItemInserted(friendlist.size() - 1);
                        groupListAdapter.notifyDataSetChanged();


                    }
                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {


                    allBeans = new AllBeans();
                    allBeans.setGroupid("rtrtm");
                    allBeans.setGroupName("rtrtm");
                    allBeans.setGroupImage("");
                    allBeans.setGroupCreateDate("");
                    allBeans.setGroupCreateTime("");

                    friendlist.add(allBeans);


                    groupListAdapter = new GroupListAdapter(getApplicationContext(), friendlist);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TwoTab_Activity.this);
                    recyclerViewgroup.setLayoutManager(mLayoutManager);
                    recyclerViewgroup.setHasFixedSize(true);
                    recyclerViewgroup.addItemDecoration(new VerticalSpaceItemDecoration(5));
                    recyclerViewgroup.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewgroup.setAdapter(groupListAdapter);
                    groupListAdapter.notifyItemInserted(friendlist.size() - 1);
                    groupListAdapter.notifyDataSetChanged();

                } else if (mainObject.getString("status").equalsIgnoreCase("100")) {

                    warningtext.setVisibility(View.VISIBLE);
                    warningtext.setText("no internet connection");
                    recyclerViewgroup.setVisibility(View.GONE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);

        warningtext.setVisibility(View.GONE);
        firsttab.setTextColor(getResources().getColor(R.color.colorwhite));
        secondtab.setTextColor(getResources().getColor(R.color.colorPrimary));
        firsttab.setBackgroundResource(R.drawable.left_rounded_corner_bg_layout);
        secondtab.setBackgroundResource(R.drawable.rounded_corner_bg_layout);


        srch_edit.setVisibility(View.GONE);
        chat.setVisibility(View.GONE);
        chat_blue.setVisibility(View.VISIBLE);
        star_blue.setVisibility(View.GONE);
        star.setVisibility(View.VISIBLE);
        language_blue.setVisibility(View.GONE);
        language.setVisibility(View.VISIBLE);
        setting.setVisibility(View.VISIBLE);
        setting_blue.setVisibility(View.GONE);

        chatMessageList = DatabaseHelper.getInstance(TwoTab_Activity.this).getReciever();

        Log.d("chatMessageList", chatMessageList.toString());
        adapter = new BroadcastnewgroupAdapter(TwoTab_Activity.this, chatMessageList);
        chatlist.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    private void searchFriend() {

        srch_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String value = srch_edit.getText().toString().toLowerCase(Locale.getDefault());
                Log.v(TAG, "Search txt :- " + value);
                if(adapter != null) {
                    adapter.filter(value.toLowerCase());
                }

            }
        });
    }
}
