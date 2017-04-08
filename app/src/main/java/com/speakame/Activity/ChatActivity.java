package com.speakame.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.speakame.Beans.AllBeans;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Classes.TimeAgo;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.XmppConneceted;
import com.speakame.Xmpp.ChatAdapter;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.speakame.Xmpp.MyXMPP;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.speakame.utils.GetFilePath;
import com.speakame.utils.JSONParser;
import com.speakame.utils.ListCountry;
import com.speakame.utils.TextTranslater;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


import static com.speakame.Xmpp.MyXMPP.numMessages;


public class ChatActivity extends AnimRootActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener, RecyclerView.OnItemTouchListener, ActionMode.Callback, ChatAdapter.OnLongClickPressListener {
    private static final String TAG = "ChatActivity";
    private static final int PICK_CONTACT = 1000;
    private static final int TypingInterval = 1000;
    private static final int NO_OF_EMOTICONS = 54;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    public static ChatActivity instance = null;
    public static String FriendMobileTWO;
    public static LinearLayoutManager mLayoutManager;
    //keep track of camera capture intent
    final int REQUEST_TAKE_GALLERY_VIDEO = 23;
    final int PICKFILE_REQUEST_CODE = 1;
    final int SENDIMAGE = 22;
    final int ADDCONTACT = 24;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    public String FriendMobile;
    public static TextView toolbartext;
    TextView status;
    String lastseen = "";
    Toolbar toolbar;
    ImageView img_eye, smily_img;
    public static ImageView conversationimage;
    AllBeans allBeans;
    String FriendStatus, reciverlanguages = "", FriendName, FriendImage, FriendId, senderName, groupName, groupId;
    Dialog markerDialog;
    LinearLayout mRevealView;
    boolean hidden = true;
    ImageButton btn1, btn2, btn3, btn4;
    ImageButton sendButton;
    FrameLayout fm;
    LinearLayout linearLayout;
    Button mbtnblock, mbtnadd;
    int MY_PERMISSIONS_REQUEST_CALL_PHONE = 101;
    /**
     * Checking keyboard height and keyboard visibility
     */
    int previousHeightDiffrence = 0;
    GestureDetectorCompat gestureDetector;
    ActionMode actionMode;
    public static RecyclerView mRecyclerView;
    private EmojiconEditText msg_edittext;
    private String user1 = "", user2 = "", GroupImage;
    private Random random;
    private ActionMode mActionMode;
    private OnTypingModified typingChangedListener;
    private boolean currentTypingState = false;
    private Handler handler = new Handler();
    private Runnable stoppedTypingNotifier = new Runnable() {
        @Override
        public void run() {
            //part A of the magic...
            if (null != typingChangedListener) {
                typingChangedListener.onIsTypingModified(msg_edittext, false);
                currentTypingState = false;
            }

            String dummyDate = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
            Log.d("dummyDat stop", dummyDate +">>"+ user2);
            Date date = new Date();
            if (dummyDate == null  || dummyDate.equalsIgnoreCase("")) {
                status.setVisibility(View.GONE);
            } else if (dummyDate.equalsIgnoreCase("online")) {
                status.setVisibility(View.VISIBLE);
            } else {
                status.setVisibility(View.VISIBLE);
                date.setTime(Long.parseLong(dummyDate));
                lastseen = new TimeAgo(ChatActivity.this).timeAgo(date);

            }
            //status.setText(lastseen);

        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathFile(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.d("FileLoad content", getDataColumn(context, uri, null, null));

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.d("FileLoad file", uri.getPath());

            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void setOnTypingModified(OnTypingModified typingChangedListener) {
        this.typingChangedListener = typingChangedListener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        instance = this;
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        status = (TextView) findViewById(R.id.status);
        conversationimage = (ImageView) findViewById(R.id.conversation_contact_photo);
        status.setSelected(true);
        status.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        status.setSingleLine(true);

        numMessages = 0;

        toolbartext.setText("Chat list");
        toolbartext.setSingleLine();
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        img_eye = (ImageView) findViewById(R.id.iv_chat_eye);
        smily_img = (ImageView) findViewById(R.id.iv_smily);
        msg_edittext = (EmojiconEditText) findViewById(R.id.messageEditText);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        btn1 = (ImageButton) findViewById(R.id.pic_id);
        btn2 = (ImageButton) findViewById(R.id.vidoid);
        btn3 = (ImageButton) findViewById(R.id.docid);
        btn4 = (ImageButton) findViewById(R.id.contactid);
        fm = (FrameLayout) findViewById(R.id.emojicons);

        if(!Function.checkPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Function.requestPermission(ChatActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        }

        linearLayout = (LinearLayout) findViewById(R.id.linear);
        mbtnadd = (Button) findViewById(R.id.btn2);
        mbtnblock = (Button) findViewById(R.id.btn1);


        ImageView imgback = (ImageView) findViewById(R.id.up);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        // startService(new Intent(ChatActivity.this, HomeService.class));


        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getSystemService(ns);
        nMgr.cancel(0);


        Function.callPermisstion(ChatActivity.this, 1);
        Function.cameraPermisstion(ChatActivity.this, 1);

        allBeans = getIntent().getParcelableExtra("value");

        FriendStatus = allBeans.getFriendStatus();
        FriendName = allBeans.getFriendname();
        FriendMobile = allBeans.getFriendmobile().replace(" ","").replace("+","");
        FriendImage = allBeans.getFriendimage();
        FriendId = allBeans.getFriendid();
        reciverlanguages = allBeans.getLanguages();
        groupName = allBeans.getGroupName();
        groupId = allBeans.getGroupid();

        System.out.println("grpiddddd" + groupId);
        GroupImage = allBeans.getGroupImage();

        if (FriendName.matches("[0-9]+") && FriendName.length() > 9) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }

        Log.d("UserList", FriendMobile + ">>>mob>>>" + FriendName + "/n" + FriendStatus + "/n" + groupName + "/n");
        if (groupName.equalsIgnoreCase("")) {
            img_eye.setVisibility(View.VISIBLE);
            if(Function.isStringInt(FriendName)){
                toolbartext.setText("+"+FriendName);
            }else{
                toolbartext.setText(FriendName);
            }

            if(FriendImage == null){

            }else
            if (!FriendImage.equalsIgnoreCase("")) {
                Picasso.with(ChatActivity.this).load(FriendImage).error(R.drawable.user_icon)
                        .resize(200, 200)
                        .into(conversationimage);

            }
        } else {
            img_eye.setVisibility(View.GONE);
            toolbartext.setText(groupName);
            if (GroupImage == null) {

            } else if (!GroupImage.equalsIgnoreCase("")) {
                Picasso.with(ChatActivity.this).load(GroupImage).error(R.drawable.user_icon)
                        .resize(200, 200)
                        .into(conversationimage);

            }
        }


        user1 = AppPreferences.getMobileuser(getApplicationContext());
        user2 = FriendMobile;
        FriendMobileTWO = user2;
        Log.d("frndnumber", user2);
        senderName = AppPreferences.getFirstUsername(getApplicationContext());

        String dummyDate = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);

        if (dummyDate == null || dummyDate.equalsIgnoreCase("")) {
            status.setVisibility(View.GONE);
        } else if (dummyDate.equalsIgnoreCase("online")) {
            status.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.VISIBLE);
            Date date = new Date();
            date.setTime(Long.parseLong(dummyDate));
            lastseen = new TimeAgo(ChatActivity.this).timeAgo(date);
            status.setText(new TimeAgo(ChatActivity.this).timeAgo(date));
        }

        msg_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1 || s.toString().trim().length() >= 1) {
                    sendButton.setVisibility(View.VISIBLE);
                } else {
                    sendButton.setVisibility(View.GONE);
                }
                if (null != typingChangedListener) {
                    if (!currentTypingState) {
                        typingChangedListener.onIsTypingModified(msg_edittext, true);
                        currentTypingState = true;
                    }

                    handler.removeCallbacks(stoppedTypingNotifier);
                    handler.postDelayed(stoppedTypingNotifier, TypingInterval);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });


        setOnTypingModified(new OnTypingModified() {
            @Override
            public void onIsTypingModified(EditText view, boolean isTyping) {
                XmppConneceted activity = new XmppConneceted();

                Log.d("frndnumber", user2);
                MyXMPP.Composing composing = (isTyping) ? MyXMPP.Composing.typing : MyXMPP.Composing.pause;

                activity.getmService().xmpp.sendIsComposing(composing, user2);
            }
        });


        XmppConneceted activity = new XmppConneceted();
        activity.getmService().xmpp.setStatusModified(new MyXMPP.TypingModified() {
            @Override
            public void onIsTypingModified(final String s, final String reciver) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("dummyDat>>>", s + ":::" + reciver + "/" + user2);
                        if (!reciver.equals(user2)) {
                            status.setVisibility(View.GONE);
                        } else if (s.equalsIgnoreCase("offline")) {
                            String dummyDate = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
                            Log.d("dummyDat>>>", s + ":Lastseen::" + dummyDate);
                            if (dummyDate == null || dummyDate.equalsIgnoreCase("")) {
                                status.setVisibility(View.GONE);
                            } else if (dummyDate.equalsIgnoreCase("online")) {
                                status.setVisibility(View.VISIBLE);
                            } else {
                                status.setVisibility(View.VISIBLE);
                                Date date = new Date();
                                date.setTime(Long.parseLong(dummyDate));
                                lastseen = new TimeAgo(ChatActivity.this).timeAgo(date);

                            }
                            status.setText(lastseen);

                        } else {
                            status.setVisibility(View.VISIBLE);
                            status.setText(s);
                        }


                    }
                });

            }
        });

        img_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg_edittext.getEditableText().toString();
                if (!message.equalsIgnoreCase("")) {
                    PriviewmsgDialog(AppPreferences.getUSERLANGUAGE(ChatActivity.this), reciverlanguages, message);
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter message", Toast.LENGTH_LONG).show();
                }
                //PriviewmsgDialog();
            }
        });


        smily_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.setVisibility(View.VISIBLE);
                View view = ChatActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

            }
        });
        msg_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fm.setVisibility(View.GONE);
                return false;
            }
        });
        setEmojiconFragment(false);

        random = new Random();

        sendButton.setOnClickListener(this);

        if (AppPreferences.getEnetrSend(ChatActivity.this).equalsIgnoreCase("1")) {

            msg_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String message = msg_edittext.getEditableText().toString();
                    if (!message.equalsIgnoreCase("")) {
                        Log.d("ChatListData", chatlist.get(0).toString());
                        XmppConneceted activity = new XmppConneceted();


                        Log.d("PrivacyItem", ">>>");
                        if (groupName.equalsIgnoreCase("")) {
                            boolean isAllow = activity.getmService().xmpp.checkUserBlock(user2);
                            if (!isAllow) {
                                unblockpopup();
                            } else {
                                sendTextMessage(message, "", "");
                            }
                        } else {
                            sendGroupMessage(message, "", "");
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, "Please enter message", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });

        }


        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        //mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // ----Set autoscroll of listview when a new message arrives----//
       /* mRecyclerView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mRecyclerView.setStackFromBottom(true);*/

        chatlist = new ArrayList<ChatMessage>();

        if (groupName.equalsIgnoreCase("")) {
            chatlist = DatabaseHelper.getInstance(ChatActivity.this).getChat("chat", FriendMobile);
        } else {
            chatlist = DatabaseHelper.getInstance(ChatActivity.this).getChat("group", groupName);

        }

        Log.d("CHATLISTSS", chatlist.toString());
        chatAdapter = new ChatAdapter(ChatActivity.this, chatlist, this);
        mRecyclerView.setAdapter(chatAdapter);
        isStoragePermissionGranted();

        //mRecyclerView.addOnItemTouchListener(this);

        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewDemoOnGestureListener());

        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(ChatActivity.this), user1, AppPreferences.getFirstUsername(ChatActivity.this),
                groupName, "Start chat with " + FriendName, "" + 1, "", true);
        chatMessage.setMsgID();
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.type = Message.Type.chat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ChatActivity.this));
        msg_edittext.setText("");
        fm.setVisibility(View.GONE);
        if (chatlist.isEmpty()) {
            chatlist.add(0, chatMessage);
            chatAdapter.notifyDataSetChanged();
        }


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, SendImageActivity.class);
                intent.setAction("gallary");
                intent.putExtra("user1", user1);
                intent.putExtra("user2", user2);
                intent.putExtra("FriendName", FriendName);
                intent.putExtra("requestCode", SENDIMAGE);
                startActivityForResult(intent, SENDIMAGE);
                revalmethod();

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType("video/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
                revalmethod();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("application/pdf");
                String[] mimetypes = {"application/*|text/*"};
                intent.setType("application/*|text/*");
                //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Select file"), PICKFILE_REQUEST_CODE);
                revalmethod();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                revalmethod();

            }
        });


        mbtnblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockpopup();
            }
        });
        mbtnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, Addnewcontact_activity.class);
                intent.putExtra("contactnumber", FriendMobileTWO);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chating_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.user);
        menuItem.setVisible(false);//
        /*
        if (groupId  != null &&  groupName  != null){
            MenuItem menuItem = menu.findItem(R.id.user);
            menuItem.setVisible(false);//

        }*/
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            supportFinishAfterTransition();
            startActivity(new Intent(this, TwoTab_Activity.class));
            finish();
            return true;
        }
        if (id == R.id.user) {
            callDialog();

            return true;
        }
        if (id == R.id.overflow) {
            View menuItemView = findViewById(R.id.overflow);
            final PopupMenu popup = new PopupMenu(this, menuItemView);
            popup.getMenuInflater().inflate(R.menu.menu_chatmenu, popup.getMenu());
            if (groupId  != null &&  groupName  != null){
                MenuItem menuItem = popup.getMenu().findItem(R.id.vireprof);
                menuItem.setTitle("Group info");
                MenuItem menuItem2 = popup.getMenu().findItem(R.id.block);
                menuItem2.setVisible(false);//

            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {



                    switch (item.getItemId()) {
                        case R.id.vireprof:
                         if (groupName.equalsIgnoreCase("")) {
                                Intent intent = new Intent(ChatActivity.this, ViewContact_DetailActivity.class);
                                intent.putExtra("name", FriendName);
                                intent.putExtra("number", FriendMobile);
                                intent.putExtra("status", FriendStatus);
                                intent.putExtra("image", FriendImage);
                                intent.putExtra("value", "");

                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ChatActivity.this, ViewGroupDetail_Activity.class);
                                intent.putExtra("GroupId", groupId);
                                intent.putExtra("GroupName", groupName);
                                intent.putExtra("GroupDate", "");
                                intent.putExtra("GroupTime", "");
                                intent.putExtra("GroupImage", GroupImage);
                                intent.putExtra("value", "");
                                intent.putExtra("groupJid", FriendMobile);
                                intent.putExtra("reciverlanguages", reciverlanguages);
                                startActivity(intent);
                            }

                            break;

                        case R.id.chathistory:
//                            Intent intent1 = new Intent(TwoTab_Activity.this, SocialContactActivity.class);
//                            startActivity(intent1);

                            chathistoryDialog();
                            break;
                        case R.id.mediahistory:
//                            Intent intent2 = new Intent(TwoTab_Activity.this, CreateGroupChatActivity.class);
//                            startActivity(intent2);
                            if (groupName.equalsIgnoreCase("")) {
                                Intent intent = new Intent(ChatActivity.this, ViewContact_DetailActivity.class);
                                intent.putExtra("name", FriendName);
                                intent.putExtra("number", FriendMobile);
                                intent.putExtra("status", FriendStatus);
                                intent.putExtra("image", FriendImage);
                                intent.putExtra("value", "1");
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ChatActivity.this, ViewGroupDetail_Activity.class);
                                intent.putExtra("GroupId", "");
                                intent.putExtra("GroupName", groupName);
                                intent.putExtra("GroupDate", "");
                                intent.putExtra("GroupTime", "");
                                intent.putExtra("GroupImage", "");
                                intent.putExtra("value", "1");
                                intent.putExtra("groupJid", FriendMobile);
                                intent.putExtra("reciverlanguages", reciverlanguages);
                                startActivity(intent);
                            }
                            break;
                        case R.id.block:
//                            Intent intent3 = new Intent(TwoTab_Activity.this, CreateGroupChatActivity.class);
//                            startActivity(intent2);

                            blockpopup();
                            break;

                    }
                    return true;
                }
            });
            popup.show();
        }
        if (id == R.id.attach) {
            revalmethod();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void chathistoryDialog() {
        final Dialog markerDialog = new Dialog(this, R.style.RadioDialogTheme);

        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
//        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);

        //  window.setBackgroundDrawableResource(R.drawable.rounded_corner_dialog);
        //window.s
        markerDialog.setContentView(R.layout.chathistory_dialog);
        Button btn_done = (Button) markerDialog.findViewById(R.id.done_btn);
        Button btn_cancel = (Button) markerDialog.findViewById(R.id.cancel_btn);

        final ImageView img_off1 = (ImageView) markerDialog.findViewById(R.id.off_toggel1);
        final ImageView img_off2 = (ImageView) markerDialog.findViewById(R.id.off_toggel2);
        final ImageView img_on1 = (ImageView) markerDialog.findViewById(R.id.on_toggel1);
        final ImageView img_on2 = (ImageView) markerDialog.findViewById(R.id.on_toggel2);

        final RadioButton rd1 = (RadioButton) markerDialog.findViewById(R.id.rd1);
        final RadioButton rd2 = (RadioButton) markerDialog.findViewById(R.id.rd2);
        final RadioButton rd3 = (RadioButton) markerDialog.findViewById(R.id.rd3);


        img_off1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img_on1.setVisibility(View.VISIBLE);
                img_on2.setVisibility(View.GONE);
                img_off1.setVisibility(View.GONE);
                img_off2.setVisibility(View.VISIBLE);
                return true;
            }
        });

        img_off2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img_on2.setVisibility(View.VISIBLE);
                img_on1.setVisibility(View.GONE);
                img_off1.setVisibility(View.VISIBLE);
                img_off2.setVisibility(View.GONE);
                return true;
            }
        });


        img_on1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img_on1.setVisibility(View.GONE);
                img_on2.setVisibility(View.GONE);
                img_off1.setVisibility(View.VISIBLE);
                img_off2.setVisibility(View.VISIBLE);
                return true;
            }
        });

        img_on2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img_on2.setVisibility(View.GONE);
                img_on1.setVisibility(View.GONE);
                img_off2.setVisibility(View.VISIBLE);
                img_off1.setVisibility(View.VISIBLE);

                return true;
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = null;
                ArrayList<ChatMessage> dateWiseChatList;
                StringBuilder Finaldata = new StringBuilder();
                String setDate = null;
                markerDialog.dismiss();

                if (img_on1.getVisibility() == View.GONE && img_on2.getVisibility() == View.GONE) {
                    Toast.makeText(getApplicationContext(), "Please firstly choose an action Email/Delete", Toast.LENGTH_LONG).show();
                } else if (img_on1.getVisibility() == View.VISIBLE && img_on2.getVisibility() == View.GONE) {
                    String date = "";
                    if (rd1.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy MM dd", 5);
                        Toast.makeText(getApplicationContext(), "5 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd2.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy MM dd", 10);
                        Toast.makeText(getApplicationContext(), "10 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd3.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy MM dd", 100);
                        Toast.makeText(getApplicationContext(), "100 day chat is selected", Toast.LENGTH_LONG).show();
                    }

                    if (groupName.equalsIgnoreCase("")) {
                        dateWiseChatList = DatabaseHelper.getInstance(ChatActivity.this).getDateWiseChat("chat", FriendMobile, date);

                        for (int i = 0; i < dateWiseChatList.size(); i++) {
                            Log.d("ChatList>>>", dateWiseChatList.get(i).toString());

                            String Sender = dateWiseChatList.get(i).sender;
                            String Sendername = dateWiseChatList.get(i).senderName;
                            String Reciver = dateWiseChatList.get(i).receiver;
                            String Recivername = dateWiseChatList.get(i).reciverName;
                            String Time = dateWiseChatList.get(i).Time;
                            String Date = dateWiseChatList.get(i).Date;
                            String Message = dateWiseChatList.get(i).body;

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd");
                            Date datee = null;
                            try {
                                datee = formatter.parse(Date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                                setDate = sdf.format(datee.getTime());
                            } catch (Exception e) {
                            }


                            Finaldata.append(setDate);
                            Finaldata.append(", ");
                            Finaldata.append(Time);
                            Finaldata.append(" - ");
                            Finaldata.append(Recivername);
                            Finaldata.append(" : ");
                            Finaldata.append(Message);
                            Finaldata.append(new String("\n"));
                            System.out.println("Data--->>" + Finaldata);

                        }

                    } else {
                        dateWiseChatList = DatabaseHelper.getInstance(ChatActivity.this).getDateWiseChat("group", groupName, date);
                        for (int i = 0; i < dateWiseChatList.size(); i++) {
                            Log.d(TAG, "ChatListGroup :- " +dateWiseChatList.get(i).toString());

                            String Sender = dateWiseChatList.get(i).sender;
                            String Sendername = dateWiseChatList.get(i).senderName;
                            String Reciver = dateWiseChatList.get(i).receiver;
                            String Recivername = dateWiseChatList.get(i).reciverName;
                            String Time = dateWiseChatList.get(i).Time;
                            String Date = dateWiseChatList.get(i).Date;
                            String Message = dateWiseChatList.get(i).body;

                            Log.v(TAG, "~~~~~~~ Group CHAT ~~~~~~~~\nSender Name :- " +Sendername + "\n Message :- " + Message + "\nDate & Time :- " + Date +" at " +Time );

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd");
                            Date datee = null;
                            try {
                                datee = formatter.parse(Date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                                setDate = sdf.format(datee.getTime());
                            } catch (Exception e) {
                            }

                            Finaldata.append(setDate);
                            Finaldata.append(", ");
                            Finaldata.append(Time);
                            Finaldata.append(" - ");
                            Finaldata.append(Sendername);
                            Finaldata.append(" : ");
                            Finaldata.append(Message);
                            Finaldata.append(new String("\n"));
                            System.out.println("Data--->>" + Finaldata);

                        }
                    }

                    if (!dateWiseChatList.isEmpty()) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{AppPreferences.getEmail(ChatActivity.this)});

                        Random r = new Random();
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                "Local db");
                        file = getChatHistFile(Finaldata.toString());
                        Log.d("DBFILE", file.toString());
                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(emailIntent, "Export database"));
                        // DrawerDialog();
                    }
                } else if (img_on1.getVisibility() == View.GONE && img_on2.getVisibility() == View.VISIBLE) {


                    String date = "";
                    if (rd1.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy MM dd", 5);
                        Log.v(TAG, "Date :-" + date);
                        Toast.makeText(getApplicationContext(), "5 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd2.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy MM dd", 10);
                        Log.v(TAG, "Date :-" + date);
                        Toast.makeText(getApplicationContext(), "10 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd3.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy MM dd", 100);
                        Log.v(TAG, "Date :-" + date);
                        Toast.makeText(getApplicationContext(), "100 day chat is selected", Toast.LENGTH_LONG).show();
                    }

                    if (groupName.equalsIgnoreCase("")) {
                        DatabaseHelper.getInstance(ChatActivity.this).ChatDelete_ByDate("chat", FriendMobile, date);
                        Toast.makeText(getApplicationContext(), "Deleted chat", Toast.LENGTH_LONG).show();
/*if(chatlist.contains(date)){
chatlist.remove();
}*/

                        chatAdapter.notifyDataSetChanged();
                        chatAdapter.notifyItemRangeChanged(0, chatlist.size());
                        mRecyclerView.invalidate();
                    } else {
                        DatabaseHelper.getInstance(ChatActivity.this).ChatDelete_ByDate("group", groupName, date);
                        chatAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(), "Deleted chat", Toast.LENGTH_LONG).show();


                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();
            }
        });

        markerDialog.setCanceledOnTouchOutside(false);
        markerDialog.show();
    }

    public File getChatHistFile(String filedata) {

        File cacheDirectory = Environment.getExternalStorageDirectory();

        File cacheDir = new File(cacheDirectory.getPath() + "/Speakame");


        if (!cacheDir.exists())
            cacheDir.mkdirs();

        Log.e("dir", cacheDir.toString());


        FileOutputStream fos;
        try {
            String a = cacheDir + "/SpeakameChatHistory" + ".txt";
            Log.e("files", a);
            File myFile = new File(a);
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fos = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append((filedata != null ? filedata : "I am testing "));
            myOutWriter.close();
            fos.flush();
            fos.close();


            //deleteFile(myFile.toString(),mContext);
            return myFile;
        } catch (Exception e) {
            Log.e("Exception ", "Creating  " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public void PriviewmsgDialog(String sorcountry, String descountry, final String message) {

        ListCountry country = new ListCountry();
        String sorcountrycode = country.getCode(ChatActivity.this,sorcountry.trim());
        if(sorcountrycode.equalsIgnoreCase("")){
            sorcountrycode = "en";
        }
        String descountrycode = country.getCode(ChatActivity.this,descountry.trim());
        if(descountrycode.equalsIgnoreCase("")){
            descountrycode = "en";
        }

        final Dialog markerDialog = new Dialog(this, R.style.RadioDialogTheme);

        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
//        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);
        markerDialog.setContentView(R.layout.priviewmsg_popup);
        Button btn_send = (Button) markerDialog.findViewById(R.id.send_btn);
        Button btn_cancel = (Button) markerDialog.findViewById(R.id.cancel_btn);
        final TextView text = (TextView) markerDialog.findViewById(R.id.texts);

        /*try {
            TranslateText.TranslateText(message, Language.ENGLISH.toString(), Language.SPANISH.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TranslateText", "Connecting to " + e.getMessage());
        }*/

        TextTranslater.getInstance().translate(ChatActivity.this,sorcountrycode, descountrycode, message, new VolleyCallback() {
            @Override
            public void backResponse(final String response) {
                if (response.equalsIgnoreCase("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(message);
                        }
                    });

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(response);
                        }
                    });

                }


            }

        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!message.equalsIgnoreCase("")) {
                    XmppConneceted activity = new XmppConneceted();


                    if (groupName.equalsIgnoreCase("")) {
                        boolean isAllow = activity.getmService().xmpp.checkUserBlock(user2);
                        if (!isAllow) {
                            unblockpopup();
                        } else {
                            sendTextMessage(message, "", "");
                        }
                    } else {
                        sendGroupMessage(message, "", "");
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter message", Toast.LENGTH_LONG).show();
                }
                markerDialog.dismiss();


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();

            }
        });

        markerDialog.setCanceledOnTouchOutside(false);
        markerDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }
        }
    }

    public void sendTextMessage(String message, String file, String fileName) {
        String MyImage = "";
        String MyStatus = "";
        if(AppPreferences.getPicprivacy(ChatActivity.this).equalsIgnoreCase(AppConstants.EVERYONE)){
            MyImage = AppPreferences.getUserprofile(ChatActivity.this);
            MyStatus = AppPreferences.getUserstatus(ChatActivity.this);
        }else if(AppPreferences.getPicprivacy(ChatActivity.this).equalsIgnoreCase(AppConstants.MYFRIENDS)){
            if(!Function.isStringInt(FriendName)){
                MyImage = AppPreferences.getUserprofile(ChatActivity.this);
                MyStatus = AppPreferences.getUserstatus(ChatActivity.this);
            }
        }

        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(ChatActivity.this), user2, FriendName,
                groupName, message, "" + random.nextInt(1000), file, true);
        chatMessage.setMsgID();
        chatMessage.body = message;
        chatMessage.fileName = fileName;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.type = Message.Type.chat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ChatActivity.this));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ChatActivity.this);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.MyImage = MyImage;
        chatMessage.userStatus = MyStatus;
        chatMessage.lastseen = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
        //chatMessage.fileData = fileData;
        msg_edittext.setText("");
        fm.setVisibility(View.GONE);
        //TwoTab_Activity activity = new TwoTab_Activity();


        chatMessage.ReciverFriendImage = FriendImage;
        chatMessage.msgStatus = "0";

        if (!fileName.equalsIgnoreCase("")) {

            String fileExte = Function.getFileExtention(fileName);
            String folderType;

            String msg = chatMessage.body;
            if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
                folderType = "SpeakaMeContact";
            } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "SpeakaMeImage";
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                folderType = "SpeakaMeVideo";
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "SpeakaMeDocument";
            } else {
                folderType = "SpeakaMeTest";
            }


            File SpeakaMeDirectory = Function.createFolder(folderType);
            chatMessage.fileName = Function.generateNewFileName(fileExte);
            chatMessage.files = Function.copyFile(file, SpeakaMeDirectory +"/"+ chatMessage.fileName );


           /* File file2= null;
            try {
                file2 = Function.decodeBase64BinaryToFile(SpeakaMeDirectory.toString(), Function.generateNewFileName(fileExte), file);
                chatMessage.fileName = file2.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            Log.d("IMAGEPATH filename",SpeakaMeDirectory+"\n"+chatMessage.fileName+"\n"+chatMessage.files);

        }else{


            /*XmppConneceted activity = new XmppConneceted();
            activity.getmService().xmpp.sendMessage(chatMessage);*/

        }

        Log.d("ChatMessage save",chatMessage.toString());
       /* if(chatMessage.fileName.contains("mp4") || chatMessage.fileName.contains("jpg")|| chatMessage.fileName.contains("pdf")){
            chatMessage.files = chatMessage.fileName;
        }*/
        DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
        DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", chatMessage.receiver);
        chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount()- 1);

    }

    public void sendGroupMessage(String message, String file, String fileName) {

        // final ChatMessage chatMessage = new ChatMessage(FriendName, FriendName, FriendName, FriendName,
        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(ChatActivity.this), user2, FriendName,
                groupName, message, "" + random.nextInt(1000), file, true);
        chatMessage.setMsgID();
        chatMessage.body = message;
        chatMessage.fileName = fileName;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.type = Message.Type.groupchat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ChatActivity.this));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ChatActivity.this);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.groupid = groupId;
        chatMessage.Groupimage = GroupImage;
        chatMessage.lastseen = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
        msg_edittext.setText("");


        chatMessage.msgStatus = "0";

        if (!fileName.equalsIgnoreCase("")) {

            String fileExte = Function.getFileExtention(fileName);
            String folderType;

            String msg = chatMessage.body;
            if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
                folderType = "SpeakaMeContact";
            } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "SpeakaMeImage";
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                folderType = "SpeakaMeVideo";
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "SpeakaMeDocument";
            } else {
                folderType = "SpeakaMeTest";
            }


            File SpeakaMeDirectory = Function.createFolder(folderType);
            chatMessage.fileName = Function.generateNewFileName(fileExte);
            chatMessage.files = Function.copyFile(file, SpeakaMeDirectory +"/"+ chatMessage.fileName );


           /* File file2= null;
            try {
                file2 = Function.decodeBase64BinaryToFile(SpeakaMeDirectory.toString(), Function.generateNewFileName(fileExte), file);
                chatMessage.fileName = file2.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            Log.d("IMAGEPATH filename",SpeakaMeDirectory+"\n"+chatMessage.fileName+"\n"+chatMessage.files);

        }else{


            /*XmppConneceted activity = new XmppConneceted();
            activity.getmService().xmpp.sendMessage(chatMessage);*/

        }

        Log.d("ChatMessage save",chatMessage.toString());
       /* if(chatMessage.fileName.contains("mp4") || chatMessage.fileName.contains("jpg")|| chatMessage.fileName.contains("pdf")){
            chatMessage.files = chatMessage.fileName;
        }*/
        DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
        DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", chatMessage.receiver);
        chatAdapter.add(chatMessage, chatAdapter.getItemCount() + 1);
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount()- 1);



    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                String message = msg_edittext.getEditableText().toString();
                if (!message.equalsIgnoreCase("")) {
                    Log.d("ChatListData", chatlist.get(0).toString());
                    XmppConneceted activity = new XmppConneceted();


                    Log.d("PrivacyItem", ">>>");
                    if (groupName.equalsIgnoreCase("")) {
                        boolean isAllow = activity.getmService().xmpp.checkUserBlock(user2);
                        if (!isAllow) {
                            unblockpopup();
                        } else {
                           // mLayoutManager.scrollToPosition(chatlist.size());
                            sendTextMessage(message, "", "");
                        }
                    } else {
                       // mLayoutManager.scrollToPosition(chatlist.size());
                        sendGroupMessage(message, "", "");
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter message", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bubble_layout_parent:
                int idx = mRecyclerView.getChildPosition(v);

                ChatMessage data = chatAdapter.getItem(idx);
                View innerContainer = v.findViewById(R.id.bubble_layout);
                innerContainer.setTransitionName("innerContainer" + "_" + data.msgid);

                if (actionMode != null) {
                    myToggleSelection(idx);
                    return;
                }


                Intent startIntent = new Intent(this, ChatActivity.class);
                startIntent.putExtra("value", allBeans);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this, innerContainer, "innerContainer");
                this.startActivity(startIntent, options.toBundle());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TwoTab_Activity.class));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Log.d("onActivityResult", requestCode + "::" + resultCode);

        String message = "";
       // String file = "";
        String fileName = "";
        String filePath = "";


        if (resultCode == RESULT_OK) {

            if (requestCode == ADDCONTACT) {
                chatAdapter.onActivityResult(requestCode, resultCode, data);

            }else if (requestCode == SENDIMAGE) {
                message = data.getStringExtra("msg");
                String filepath = data.getStringExtra("file");
                Log.d("file>>", filepath);
                filePath = filepath;
                File file1 = new File(filepath);
                fileName = file1.getName();
                Log.d("file>1>", fileName);
                filepath = filepath.replace(" ","");
                // file=filePath;

                  //  file = filePath;
                    //file = Function.encodeFileToBase64Binary(filepath);

            } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedImageUri = data.getData();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                //selectedImagePath = selectedImagePath.replace(" ","");
                if (selectedImagePath != null) {
                    message = "";
                    filePath = selectedImagePath;
                    File file1 = new File(selectedImagePath);
                    fileName = file1.getName();

                    //file = selectedImagePath;

                   // Log.d("filebase64", file.toString());
                }
            } else if (requestCode == PICK_CONTACT) {
                if (resultCode == Activity.RESULT_OK) {

                    Uri uri = data.getData();
                    ContentResolver contentResolver = getContentResolver();
                    Cursor contentCursor = contentResolver.query(uri, null, null, null, null);

                    if (contentCursor.moveToFirst()) {
                        String id = contentCursor.getString(contentCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =
                                contentCursor.getString(contentCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            phones.moveToFirst();
                            String contactNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String contactName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String picUrl = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                            // long contactId = phones.getLong(phones.getColumnIndex("_ID"));
                            Log.i("phoneNUmber", "The phone number is " + contactNumber + contactName + " picUrl" + picUrl);

                            message = contactName + AppConstants.KEY_CONTACT + contactNumber;

                            String photoPath = null;
                            Uri uri1 = null;
                            Bitmap bitmap = null;
                            if (picUrl != null) {
                                uri1 = Uri.parse(picUrl);
                                InputStream input = null;
                                try {
                                    input = getContentResolver().openInputStream(uri1);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();

                                }
                                bitmap = BitmapFactory.decodeStream(input);
                            } else {
                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);

                            }

                            // Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                            // Getting Caching directory
                            //File cacheDirectory = getBaseContext().getCacheDir();
                            File SpeakaMeDirectory = Function.createFolder("SpeakaMeContact");

                            // Temporary file to store the contact files
                            File tmpFile = new File(SpeakaMeDirectory.getPath() + "/contact.png");

                            // The FileOutputStream to the temporary file
                            try {
                                FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                                // Writing the bitmap to the temporary file as png file
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);

                                // Flush the FileOutputStream
                                fOutStream.flush();

                                //Close the FileOutputStream
                                fOutStream.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            filePath = tmpFile.getPath();
                            Log.d("contactImage", tmpFile.getPath());
                            fileName = tmpFile.getName();
                            /*String photoPath = null;
                            Uri uri1 = null;
                            Bitmap bitmap = null;
                            if (picUrl != null) {
                                uri1 = Uri.parse(picUrl);
                                InputStream input = null;
                                try {
                                    input = getContentResolver().openInputStream(uri1);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();

                                }
                                bitmap = BitmapFactory.decodeStream(input);
                            } else {
                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);

                            }

                            // Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                            // Getting Caching directory
                            File cacheDirectory = getBaseContext().getCacheDir();

                            // Temporary file to store the contact files
                            File tmpFile = new File(cacheDirectory.getPath() + "/SpeakaMe" + new Random().nextInt(100) + "img.png");

                            // The FileOutputStream to the temporary file
                            try {
                                FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                                // Writing the bitmap to the temporary file as png file
                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fOutStream);

                                // Flush the FileOutputStream
                                fOutStream.flush();

                                //Close the FileOutputStream
                                fOutStream.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            photoPath = tmpFile.getPath();
                            filePath = photoPath;
                            Log.d("contactImage", photoPath);
                            fileName = tmpFile.getName();



                            if (contactNumber != null && contactName != null) {

                                //message = contactName + AppConstants.KEY_CONTACT + contactNumber;
                                System.out.println("numberrrr>>>" + message);

                            }*/


                        }else {
                            Toast.makeText(ChatActivity.this, "Contact number is not available!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    //  super.onActivityResult(requestCode, resultCode, data);
                }
            } else if (requestCode == PICKFILE_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                Log.d("selectedImagePath", selectedImageUri.toString());
                // MEDIA GALLERY
                //String selectedImagePath = getPathFile(ChatActivity.this, selectedImageUri);
                String selectedImagePath = GetFilePath.getPath(ChatActivity.this, selectedImageUri);
                //selectedImagePath = selectedImagePath.replace(" ","");
                Log.d("selectedImagePath", selectedImagePath);
                if (selectedImagePath != null) {
                    File file1 = new File(selectedImagePath);
                    message = file1.getName();
                    filePath = selectedImagePath;
                    fileName = file1.getName();

                   // file = selectedImagePath;
                        //file = Function.encodeFileToBase64Binary(selectedImagePath);

                }
            }

           // byte[] fileData = Function.fileToByte(filePath);

            XmppConneceted activity = new XmppConneceted();

            int fileSize = Function.getFileSize(filePath);
            Log.d("fileSize", groupName+">>>" + fileSize);
            //if(fileSize < ) {
                if (groupName.equalsIgnoreCase("")) {
                    boolean isAllow = true;
                    try {
                        isAllow = activity.getmService().xmpp.checkUserBlock(user2);
                    } catch (Exception e) {
                    }
                    if (!isAllow) {
                        unblockpopup();
                    } else {
                        sendTextMessage(message, filePath, fileName);
                    }
                } else {
                    sendGroupMessage(message, filePath, fileName);
                }
           /* }else {

            }*/

        }


        //  }


    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }

    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    private void sendBlockstatus() {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "blockstatus");
            jsonObject.put("blockunblockstaus", "1");
            jsonObject.put("friend_mobile", user2);
            jsonObject.put("user_id", AppPreferences.getLoginId(ChatActivity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ChatActivity.this);
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

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setBlockList(ChatActivity.this, topObject.getString("block_users"));


                            }

                            Toast.makeText(getApplicationContext(), "Block successfully", Toast.LENGTH_LONG).show();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
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

    private void sendUnBlockstatus() {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "blockstatus");
            jsonObject.put("blockunblockstaus", "0");
            jsonObject.put("friend_mobile", user2);
            jsonObject.put("user_id", AppPreferences.getLoginId(ChatActivity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ChatActivity.this);
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

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setBlockList(ChatActivity.this, topObject.getString("block_users"));


                            }

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
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



    public void revalmethod() {

        int cx = (mRevealView.getLeft() + mRevealView.getRight());
//                int cy = (mRevealView.getTop() + mRevealView.getBottom())/2;
        int cy = mRevealView.getTop();

        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {


            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(800);

            SupportAnimator animator_reverse = animator.reverse();

            if (hidden) {
                mRevealView.setVisibility(View.VISIBLE);
                animator.start();
                hidden = false;
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        mRevealView.setVisibility(View.INVISIBLE);
                        hidden = true;

                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();

            }
        } else {
            if (hidden) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                mRevealView.setVisibility(View.VISIBLE);
                anim.start();
                hidden = false;

            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRevealView.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }
                });
                anim.start();

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
      /*  XmppConneceted activity = new XmppConneceted();

        MyXMPP.Composing composing = MyXMPP.Composing.active;

        activity.getmService().xmpp.sendIsComposing(composing,user2);*/


    }

    @Override
    protected void onPause() {
        super.onPause();

       /* XmppConneceted activity = new XmppConneceted();

        MyXMPP.Composing composing = MyXMPP.Composing.inactive;

        activity.getmService().xmpp.sendIsComposing(composing,user2);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        user2 = "";
       /* XmppConneceted activity = new XmppConneceted();

        MyXMPP.Composing composing = MyXMPP.Composing.gone;

        activity.getmService().xmpp.sendIsComposing(composing,user2);*/
    }


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(msg_edittext, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(msg_edittext);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    private void myToggleSelection( int idx) {
        chatAdapter.toggleSelection(idx);
        @SuppressLint("StringFormatMatches") String title = getString(R.string.selected_count, chatAdapter.getSelectedCount());
        actionMode.setTitle(title);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menutool, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                List<Integer> selectedItemPositions = chatAdapter.getSelectedItems();
                int currPos;
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currPos = selectedItemPositions.get(i);
                    chatAdapter.removeData(currPos);
                }
                actionMode.finish();
                return true;

            case R.id.action_copy:
                List<Integer> selectedItemPosition = chatAdapter.getSelectedItems();
                int currPo;
                String selectedText = "";
                for (int i = selectedItemPosition.size() - 1; i >= 0; i--) {
                    currPo = selectedItemPosition.get(i);
                    if (selectedText.equalsIgnoreCase("")) {
                        selectedText = chatAdapter.copyMsg(currPo);
                    } else {
                        selectedText = selectedText + "\n" + chatAdapter.copyMsg(currPo);
                    }

                }

                int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(selectedText);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("selectedText", selectedText);
                    clipboard.setPrimaryClip(clip);
                }

                actionMode.finish();
                return true;
            case R.id.action_forward:
                List<Integer> selectedItemPos = chatAdapter.getSelectedItems();
                int currPosi;
                JSONArray jsonArray = new JSONArray();
                for (int i = selectedItemPos.size() - 1; i >= 0; i--) {
                    currPosi = selectedItemPos.get(i);
                    jsonArray.put(chatAdapter.copyData(currPosi));
                   /* if(selectedMsg.equalsIgnoreCase("")){

                    }else{
                        selectedMsg = selectedMsg +"\n"+chatAdapter.copyData(currPosi);
                    }*/

                }
                Intent intent = new Intent(ChatActivity.this, ForwardActivity.class);
                intent.putExtra("message", jsonArray.toString());
                startActivity(intent);
                actionMode.finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
        chatAdapter.clearSelections();
    }

    public void blockpopup() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Block " + FriendName + "? Blocked contacts will no longer be able to call you or send you messages")
                .setCancelText("Cancel")
                .setConfirmText("Block")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        XmppConneceted activity = new XmppConneceted();
                        activity.getmService().xmpp.blockedUser(user2);
                        sendBlockstatus();
                        sDialog.cancel();


                    }
                })
                .show();
    }

    public void unblockpopup() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Unblock " + FriendName + "? send you messages")
                .setCancelText("Cancel")
                .setConfirmText("Unblock")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        XmppConneceted activity = new XmppConneceted();
                        activity.getmService().xmpp.unBlockedUser(user2);
                        sendUnBlockstatus();
                        sDialog.cancel();


                    }
                })
                .show();
    }

    private void callDialog() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Do you want to call !")
// .setContentText("This call will make use of your network !!!!")
                .setContentText("Call you make will be charged as per your network !!!!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + FriendMobile));
                        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
// TODO: Consider calling
// public void requestPermissions(@NonNull String[] permissions, int requestCode)
// here to request the missing permissions, and then overriding
// public void onRequestPermissionsResult(int requestCode, String[] permissions,
// int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for Activity#requestPermissions for more details.

                            ActivityCompat.requestPermissions(ChatActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

                            return;
                        }
                        startActivity(callIntent);

                    }
                }).show();


    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            //adapter.removeItem(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startActionMode(this);
        }

        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        chatAdapter.toggleSelection(position);
        int count = chatAdapter.getSelectedCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    public interface OnTypingModified {
        public void onIsTypingModified(EditText view, boolean isTyping);
    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (actionMode != null) {
                return;
            }
            // Start the CAB using the ActionMode.Callback defined above
            actionMode = startActionMode(ChatActivity.this);
            int idx = mRecyclerView.getChildPosition(view);
//            ChatMessage data = chatAdapter.getItem(idx);
            View innerContainer = view.findViewById(R.id.bubble_layout);
            //innerContainer.setTransitionName("innerContainer"+ "_" + data.msgid);
            myToggleSelection( idx);
            super.onLongPress(e);
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


}
