package com.speakame.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.speakame.Adapter.GroupMemberList_Adapter;
import com.speakame.Beans.AllBeans;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.XmppConneceted;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.speakame.Xmpp.MediaAdapter;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dmax.dialog.SpotsDialog;

public class ViewGroupDetail_Activity extends AnimRootActivity implements VolleyCallback, View.OnClickListener {
    private static final int SELECTIMAGE = 1;
    private static final int SELECTNAME = 2;
    TextView toolbartitle, text_groupdate, alerttextview, editstatus;
    TextView edit_groupname;
    ImageView image_group;
    Button deleteGroup, exitGroup;
    RecyclerView recyclerView;
    GroupMemberList_Adapter groupMemberList_adapter;
    String Groupdate, Grouptime ;
    public static String groupJid, Groupname,Groupid, GroupImage,reciverlanguages;
    AlertDialog mProgressDialog;
    AllBeans allBeans;
    ArrayList<AllBeans> friendlist;
    private String value = "";
    private MediaAdapter mediaAdapter;
    private ArrayList<ChatMessage> chatlist;
    ArrayList<Integer> stringArrayList;
    private Random random;
    public static String isAdmin = "0";
    private MenuItem addContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_detail_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        initview();

        random = new Random();

        toolbartitle.setText("Group Title");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartitle.setTypeface(tf1);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.BLACK)
                .sizeResId(R.dimen.dividerhight)
                .marginResId(R.dimen.dividerhight, R.dimen.dividerhight)
                .build());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ViewGroupDetail_Activity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        stringArrayList = new ArrayList<Integer>();
        Intent intent = getIntent();
        Groupid = intent.getStringExtra("GroupId");
        Groupname = intent.getStringExtra("GroupName");
        Groupdate = intent.getStringExtra("GroupDate");
        Grouptime = intent.getStringExtra("GroupTime");
        GroupImage = intent.getStringExtra("GroupImage");
        groupJid = intent.getStringExtra("groupJid");
        reciverlanguages = intent.getStringExtra("reciverlanguages");
        toolbartitle.setText(Groupname);
        edit_groupname.setText(Groupname);
        text_groupdate.setText("Group created" + " " + Groupdate + " " + Grouptime);

        System.out.println("grpid" + Groupid);

        if (GroupImage.equalsIgnoreCase("")) {
            image_group.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(ViewGroupDetail_Activity.this).load(GroupImage).error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(image_group);
        }
        friendlist = new ArrayList<AllBeans>();

        value = intent.getStringExtra("value");

        if (value.equalsIgnoreCase("1")) {
            editstatus.setText("Media");
            chatlist = DatabaseHelper.getInstance(ViewGroupDetail_Activity.this).getMedia("group", Groupname);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ViewGroupDetail_Activity.this, 3);
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._5sdp);
            recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
            recyclerView.setLayoutManager(layoutManager);

            mediaAdapter = new MediaAdapter(ViewGroupDetail_Activity.this, chatlist);
            recyclerView.setAdapter(mediaAdapter);


        } else {
            editstatus.setText("Group members");
            mProgressDialog = new SpotsDialog(ViewGroupDetail_Activity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                jsonObject.put("method", "userMakeGroupDetail");
                jsonObject.put("user_id", AppPreferences.getLoginId(ViewGroupDetail_Activity.this));
                jsonObject.put("group_id", Groupid);
                jsonArray.put(jsonObject);

                System.out.println("groupmemberlist" + jsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONParser jsonParser = new JSONParser(ViewGroupDetail_Activity.this);
            jsonParser.parseVollyJsonArray(AppConstants.USERGROUPURL, 1, jsonArray, ViewGroupDetail_Activity.this);
            System.out.println("jsonArray" + jsonObject);

            updateAdminUi(isAdmin);

        }

        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmppConneceted activity = new XmppConneceted();
                try {
                    boolean isDeleted = activity.getmService().xmpp.deleteChatRoom(Groupname);
                    if(isDeleted){
                        DatabaseHelper.getInstance(ViewGroupDetail_Activity.this).deleteGroup(Groupname);
                        Intent intent = new Intent(ViewGroupDetail_Activity.this, TwoTab_Activity.class);
                        intent.setAction("");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                }
            }
        });
        exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmppConneceted activity = new XmppConneceted();
                ChatMessage chatMessage = new ChatMessage(AppPreferences.getMobileuser(ViewGroupDetail_Activity.this), AppPreferences.getFirstUsername(ViewGroupDetail_Activity.this),
                        groupJid, groupJid,
                        Groupname, "",
                        "" + random.nextInt(1000), "", false);
                chatMessage.setMsgID();
                chatMessage.Date = CommonMethods.getCurrentDate();
                chatMessage.Time = CommonMethods.getCurrentTime();
                chatMessage.type = Message.Type.groupchat.name();
                chatMessage.groupid = Groupid;
                chatMessage.Groupimage = GroupImage;
                chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ViewGroupDetail_Activity.this);
                chatMessage.reciverlanguages = reciverlanguages;
                chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ViewGroupDetail_Activity.this));
                chatMessage.lastseen = new DatabaseHelper(ViewGroupDetail_Activity.this).getLastSeen(groupJid);

                chatMessage.body = "removeFromGroup";

                activity.getmService().xmpp.userSelfExit(chatMessage);
                //activity.getmService().xmpp.banUser(chatMessage,AppPreferences.getMobileuser(ViewGroupDetail_Activity.this));
                finish();
            }
        });


    }

    private void initview() {
        toolbartitle = (TextView) findViewById(R.id.toolbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_listmember);
        image_group = (ImageView) findViewById(R.id.image_group);
        edit_groupname = (TextView) findViewById(R.id.edit_title);
        text_groupdate = (TextView) findViewById(R.id.date_text);
        alerttextview = (TextView) findViewById(R.id.alerttext);
        editstatus = (TextView) findViewById(R.id.editstatus);
        deleteGroup = (Button) findViewById(R.id.deleteGroup);
        exitGroup = (Button) findViewById(R.id.exitGroup);

        image_group.setOnClickListener(this);
        edit_groupname.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addcontact, menu);
        addContact = menu.findItem(R.id.addcontact);
        updateAdminUi(isAdmin);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }else if (id == R.id.addcontact) {
            Intent intent = new Intent(ViewGroupDetail_Activity.this, GroupMemberList_Activity.class);
            intent.putExtra("groupname", Groupname);
            intent.putExtra("groupid", Groupid);
            intent.putIntegerArrayListExtra("groupmemberid", stringArrayList);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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
                    isAdmin = mainObject.getString("isAdmin");
                    updateAdminUi(isAdmin);
                    JSONArray orderArray = mainObject.getJSONArray("result");

                    for (int i = 0; orderArray.length() > i; i++) {
                        JSONObject topObject = orderArray.getJSONObject(i);
                        allBeans = new AllBeans();
                        allBeans.setFriendid(topObject.getString("speaka_id"));
                        allBeans.setFriendname(topObject.getString("person_name"));
                        allBeans.setFriendmobile(topObject.getString("speaka_number").replace(" ","").replace("+",""));
                        allBeans.setFriendimage(topObject.getString("user_image"));
                        allBeans.setFriendStatus(topObject.getString("userProfileStatus"));
                        allBeans.setGroupCreateDate(topObject.getString("group_create"));
                        allBeans.setGroupAdminStatus(topObject.getString("admin_id"));


                        friendlist.add(allBeans);
                        stringArrayList.add(Integer.valueOf(topObject.getString("speaka_id")));
                    }
                    if (friendlist != null) {
                        groupMemberList_adapter = new GroupMemberList_Adapter(ViewGroupDetail_Activity.this, friendlist);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewGroupDetail_Activity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(groupMemberList_adapter);
                        text_groupdate.setText("Group created" +"-"+ allBeans.getGroupCreateDate());
                    }
                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                    alerttextview.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    //    Toast.makeText(ViewGroupDetail_Activity.this, "No data found in Menu section", Toast.LENGTH_LONG).show();
                } else if (mainObject.getString("status").equalsIgnoreCase("100")) {

                    alerttextview.setVisibility(View.VISIBLE);
                    alerttextview.setText("Server error");
                    recyclerView.setVisibility(View.GONE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_group:
                changeGImage();
                break;
            case R.id.edit_title:
                Intent intent = new Intent(ViewGroupDetail_Activity.this, EditNameActivity.class);
                intent.putExtra("Groupid", Groupid);
                startActivityForResult(intent, SELECTNAME);
                break;
        }
    }



    private void changeGImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECTIMAGE);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri picUri;
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTIMAGE) {
                picUri = data.getData();

                try {
                    //We get the file path from the media info returned by the content resolver
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    String filePath = cursor.getString(columnIndex);

                    String fileString = Function.encodeFileToBase64Binary(filePath);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("method",AppConstants.GROUP_IMAGE_UPDATE);
                    jsonObject.put("user_id",AppPreferences.getLoginId(ViewGroupDetail_Activity.this));
                    jsonObject.put("group_id",Groupid);
                    jsonObject.put("group_image",fileString);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);

                    final ProgressDialog progressDialog  = ProgressDialog.show(ViewGroupDetail_Activity.this, "", "Group Image updating.....", false);

                    JSONParser jsonParser = new JSONParser(ViewGroupDetail_Activity.this);
                    jsonParser.parseVollyJsonArray(AppConstants.USERGROUPURL, 1, jsonArray, new VolleyCallback() {
                        @Override
                        public void backResponse(String response) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if(object.getString("status").equalsIgnoreCase("200")){
                                    JSONArray array = object.getJSONArray("result");
                                    JSONObject object1 = array.getJSONObject(0);
                                    String group_image = object1.getString("group_image");
                                    GroupImage = group_image;
                                    Picasso.with(ViewGroupDetail_Activity.this).load(group_image).error(R.drawable.user_icon)
                                            .resize(200, 200)
                                            .into(image_group);
                                    DatabaseHelper.getInstance(ViewGroupDetail_Activity.this).UpdateGroupImage(group_image,Groupid);

                                    XmppConneceted activity = new XmppConneceted();
                                    ChatMessage chatMessage = new ChatMessage(AppPreferences.getMobileuser(ViewGroupDetail_Activity.this), AppPreferences.getFirstUsername(ViewGroupDetail_Activity.this),
                                            groupJid, groupJid,
                                            Groupname, "Group Image changed by "+ AppPreferences.getFirstUsername(ViewGroupDetail_Activity.this),
                                            "" + random.nextInt(1000), "", false);
                                    chatMessage.setMsgID();
                                    chatMessage.Date = CommonMethods.getCurrentDate();
                                    chatMessage.Time = CommonMethods.getCurrentTime();
                                    chatMessage.type = Message.Type.groupchat.name();
                                    chatMessage.groupid = Groupid;
                                    chatMessage.Groupimage = group_image;
                                    chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ViewGroupDetail_Activity.this);
                                    chatMessage.reciverlanguages = reciverlanguages;
                                    chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ViewGroupDetail_Activity.this));
                                    chatMessage.lastseen = new DatabaseHelper(ViewGroupDetail_Activity.this).getLastSeen(groupJid);

                                    activity.getmService().xmpp.groupUpdate(chatMessage);

                                }else if(object.getString("status").equalsIgnoreCase("200")){
                                    Toast.makeText(ViewGroupDetail_Activity.this, "Server not respond", Toast.LENGTH_LONG).show();
                                }else{
                                    JSONArray array = object.getJSONArray("result");
                                    JSONObject object1 = array.getJSONObject(0);
                                    Toast.makeText(ViewGroupDetail_Activity.this, object1.getString("msg"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    });

                    //imagePath = decodeFile(filePath, 200, 200);
                    // imagePath = filePath;

                    //  imageView.setImageDrawable(Drawable.createFromPath(filePath));
                    cursor.close();
                } catch (Exception e) {
                }


                //performCrop(picUri);
            }else if(requestCode == SELECTNAME){

                String name = data.getStringExtra("name");
                Groupname = name;
                edit_groupname.setText(name);
                toolbartitle.setText(name);
                DatabaseHelper.getInstance(ViewGroupDetail_Activity.this).UpdateGroupName(name, Groupid);

                XmppConneceted activity = new XmppConneceted();
                ChatMessage chatMessage = new ChatMessage(AppPreferences.getMobileuser(ViewGroupDetail_Activity.this), AppPreferences.getFirstUsername(ViewGroupDetail_Activity.this),
                        groupJid, groupJid,
                        name, "Group name changed by "+ AppPreferences.getFirstUsername(ViewGroupDetail_Activity.this),
                        "" + random.nextInt(1000), "", false);
                chatMessage.setMsgID();
                chatMessage.Date = CommonMethods.getCurrentDate();
                chatMessage.Time = CommonMethods.getCurrentTime();
                chatMessage.type = Message.Type.groupchat.name();
                chatMessage.groupid = Groupid;
                chatMessage.Groupimage = GroupImage;
                chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ViewGroupDetail_Activity.this);
                chatMessage.reciverlanguages = reciverlanguages;
                chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ViewGroupDetail_Activity.this));
                chatMessage.lastseen = new DatabaseHelper(ViewGroupDetail_Activity.this).getLastSeen(groupJid);

                activity.getmService().xmpp.groupUpdate(chatMessage);
            }

        }

    }

    private void updateAdminUi(String isAdmin){
        if(isAdmin.equalsIgnoreCase("2")){
            exitGroup.setVisibility(View.VISIBLE);
            deleteGroup.setVisibility(View.VISIBLE);
            image_group.setClickable(true);
            edit_groupname.setClickable(true);
            if(addContact != null) {
                addContact.setVisible(true);
            }
        }else if(isAdmin.equalsIgnoreCase("1")){
            exitGroup.setVisibility(View.VISIBLE);
            deleteGroup.setVisibility(View.GONE);
            image_group.setClickable(true);
            edit_groupname.setClickable(true);
            if(addContact != null) {
                addContact.setVisible(true);
            }
        }else {
            exitGroup.setVisibility(View.VISIBLE);
            deleteGroup.setVisibility(View.GONE);
            image_group.setClickable(false);
            edit_groupname.setClickable(false);
            if(addContact != null) {
                addContact.setVisible(false);
            }
        }
    }


}
