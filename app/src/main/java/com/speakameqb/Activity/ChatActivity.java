package com.speakameqb.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.listeners.QBPrivacyListListener;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.users.model.QBUser;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Beans.User;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.Database.BlockUserDataBaseHelper;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.QuickBlox.DialogsManager;
import com.speakameqb.QuickBlox.QbChatDialogMessageListenerImp;
import com.speakameqb.QuickBlox.VerboseQbChatConnectionListener;
import com.speakameqb.R;
import com.speakameqb.Services.QBService;
import com.speakameqb.Xmpp.ChatAdapter;
import com.speakameqb.Xmpp.ChatMessage;
import com.speakameqb.Xmpp.CommonMethods;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.GetFilePath;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.ListCountry;
import com.speakameqb.utils.TextTranslater;
import com.speakameqb.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.DiscussionHistory;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class ChatActivity extends AnimRootActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, DialogsManager.ManagingDialogsCallbacks,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener, RecyclerView.OnItemTouchListener, ActionMode.Callback, ChatAdapter.OnLongClickPressListener {
    public static final String EXTRA_DIALOG_ID = "dialogId";
    public static final String NEWGROUPNAMEBACK = "newGroupNameback";
    private static final String TAG = "ChatActivity";
    private static final int PICK_CONTACT = 1000;
    private static final int TypingInterval = 1000;
    private static final int NO_OF_EMOTICONS = 54;
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    public static ChatActivity instance = null;
    public static String FriendMobileTWO;
    public static LinearLayoutManager mLayoutManager;
    public static TextView toolbartext;
    public static ImageView conversationimage;
    public static String groupName = "";
    public static RecyclerView mRecyclerView;
    //keep track of camera capture intent
    final int REQUEST_TAKE_GALLERY_VIDEO = 23;
    final int PICKFILE_REQUEST_CODE = 1;
    final int SENDIMAGE = 22;
    final int ADDCONTACT = 24;
    final int GALLARY_CAPTURE = 101;

    //keep track of cropping intent
    final int PIC_CROP = 2;
    final Handler handler = new Handler();
    public TextView status;
    public String FriendMobile, MobileWithCountryCode;
    public int qbFileId = 0;
    public String qbFileUid = "";
    public QBSystemMessagesManager systemMessagesManager;
    Toolbar toolbar;
    ImageView img_eye, smily_img;
    AllBeans allBeans;
    String FriendStatus, reciverlanguages = "", FriendName, FriendImage, FriendId, senderName, groupId;
    Dialog markerDialog;
    LinearLayout mRevealView;
    boolean hidden = true;
    ImageButton btn1, btn2, btn3, btn4;
    FloatingActionButton sendButton;
    //    ImageButton sendButton;
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
    String onlineStatus = "";
    CoordinatorLayout coordinateLayout;
    RelativeLayout relative_layout;
    String QB_Name, QB_Mobile, QB_LoginId;
    Integer QB_Friend_Id;
    QBChatService chatService;
    String message = "";
    // String file = "";
    String fileName = "";
    String filePath = "";
    Gson gson;
    QBService qbService;
    String lastSeen;
    String groupDialodId;
    QBRosterListener rosterListener;
    String GroupName = "";
    QBMessageStatusListener messageStatusListener;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    QBPrivacyListsManager privacyListsManager;
    QBPrivacyListListener privacyListListener = new QBPrivacyListListener() {
        @Override
        public void setPrivacyList(String listName, List<QBPrivacyListItem> listItem) {
            Log.d(TAG, " Set PrivacyList method");
        }

        @Override
        public void updatedPrivacyList(String listName) {
            Log.d(TAG, "updatedPrivacyList method");

        }
    };
    TwoTab_Activity twoTab_activity = new TwoTab_Activity();
    TimeZone timezone;
    Calendar calander;
    boolean isUserBlocked;
    LinearLayout backLinearLayout, nameToolbarTitle;
    MenuItem menuItem22;
    BlockUserDataBaseHelper blockUserDataBaseHelper;
    private String fileUrl = "";
    private EmojiconEditText msg_edittext;
    private String user1 = "", user2 = "", GroupImage;
    private Random random;
    private ActionMode mActionMode;
    private boolean currentTypingState = false;
    private QBChatDialog privateChatDialog, groupChatDialog;
    private ChatMessageListener chatMessageListener;
    private ConnectionListener chatConnectionListener;
    private QBIncomingMessagesManager incomingMessagesManager;
    private QBChatDialogTypingListener privateChatDialogTypingListener, groupChatDialogTypingListener;
    private QBChatDialogMessageSentListener privateChatDialogMessageSentListener, groupChatDialogMessageSentListener;
    private QBChatDialogMessageListener globalChatDialogMessageListener;
    private QBChatDialogMessageListener privateChatDialogMessageListener, groupChatDialogMessageListener;
    private QBMessageStatusesManager messageStatusesManager;
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: send stop typing status here and display it on status
                // ............
                // ............
                sendStopTypingInPrivateChat();
                if (isUserBlocked) {
                    status.setText("");
                } else {
                    if (!groupName.equalsIgnoreCase("")) {
                        status.setText("");
                    } else {
                        if (lastSeen.equalsIgnoreCase("offline") || lastSeen.contains("offline")) {
                            status.setText("");
                        } else {
                            status.setText(lastSeen);
                        }
                    }
                }
            }
        }
    };
    //
    ///////////////////////////////////////////// 1-1 Chat /////////////////////////////////////////////
    //
    private Date date;
    private byte[] qbChatDialogBytes;
    private String filePathFromGallaryN = "";

    public static void startForResult(Activity activity, int code, QBChatDialog dialogId) {
        Toast.makeText(activity, "new code 2", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(activity, ChatActivity.class);
        intent.setAction("");
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: onCreate() method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_);

        setToolbar();

        if (!Function.checkPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Function.requestPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        }

        initViews();

//        getLastSeen();

        // startService(new Intent(ChatActivity.this, HomeService.class));

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getSystemService(ns);
        nMgr.cancel(0);

//        Function.callPermisstion(ChatActivity.this, 1);
//        Function.cameraPermisstion(ChatActivity.this, 1);

        getDataFromIntent();
//ddddddddddddddd
        ArrayList<String> getDateFromDb = DatabaseHelper.getInstance(ChatActivity.this).getDateFromGroup("chat", FriendMobile);
        Log.d(TAG, " DateGetFromDb first : ---- " + getDateFromDb.toString());
//        startServiceForStatus();)
        //      fdsssssssssss
        ifEnterIsSend();

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

        callAdapter();

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

        if (getIntent().getAction().equalsIgnoreCase("Contact_Import")) {
            chatLogin();
            registerQbChatListeners();

            Log.v(TAG, "QB_Friend_Id from contact activity 123 :- " + QB_Friend_Id);
            byte[] qbChatDialogByte = DatabaseHelper.getInstance(ChatActivity.this).getChatDialogUsingQBId(QB_Friend_Id);
            Log.v(TAG, "QbChatDialogByte from contact activity 123 :- " + qbChatDialogByte);

            if (qbChatDialogByte == null) {

                createDialogQuickBlox(QB_Friend_Id, allBeans);

            } else {

                privateChatDialog = SerializationUtils.deserialize(qbChatDialogByte);
                Log.v(TAG, "privateChatDialog  from contact is not null :- " + privateChatDialog);
                QBInit(groupName, privateChatDialog);
            }

        } else {

            privateChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
            QBInit(groupName, privateChatDialog);

        }
//        initConnectionListener();
//        markMessageAsRead(privateChatDialog.getDialogId());

        Log.v(TAG, "TEST 1 UNIVERSAL privateChatDialog :- " + privateChatDialog);

        setListener();

        keyboardVisible();
//        getUserLastSeen();

        if (!filePathFromGallaryN.equalsIgnoreCase("")) {
            Log.d(TAG, "file Sharing form gallary intent : -- " + filePathFromGallaryN);
            filePath = filePathFromGallaryN;
            File qbFile = new File(filePathFromGallaryN);
            fileName = qbFile.getName();
            sharingFilesFromMethod(qbFile);
        }
    }

    public void chatLogin() {
        chatService = QBChatService.getInstance();
        final QBUser user = new QBUser(AppPreferences.getQB_LoginId(ChatActivity.this), "12345678");
        user.setId(AppPreferences.getQBUserId(ChatActivity.this));

        Log.v(TAG, "loggginnn  Login to chat service done :- " + user);

        chatService.login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {

                Log.v(TAG, "Login to chat service done ");

                registerQbChatListeners();
                initMessageSentListener();
                initGlobalMessageListener();
                initPrivateChatMessageListener();
//
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error while login to chat service ");
                Log.v(TAG, "Error chat serviev :-  " + e.getMessage());

            }
        });

    }

    public void registerQbChatListeners() {

        chatService = QBChatService.getInstance();
        incomingMessagesManager = chatService.getIncomingMessagesManager();
        systemMessagesManager = chatService.getSystemMessagesManager();

    }

    private void createDialogQuickBlox(final int qb_id, final AllBeans allBeans) {

        Log.v(TAG, "All beans data 1 :- " + allBeans.getFriendname());
        Log.v(TAG, "All beans data 2 :- " + allBeans.getFriendmobile());
        Log.v(TAG, "All beans data 3 :- " + allBeans.getFriendStatus());

        Log.v(TAG, "QB friend id insdide chat activity :- " + qb_id);
        List<Integer> occupants_id = new ArrayList<>();
        occupants_id.add(qb_id);

//        final QBChatDialog privateDialog = DialogUtils.buildPrivateDialog(qb_id);

        QBChatDialog privateDialog = new QBChatDialog();
        privateDialog.setType(QBDialogType.PRIVATE);
        privateDialog.setOccupantsIds(occupants_id);
        Log.v(TAG, "privateDialog 1:-  " + privateDialog);

//vdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss

        QBRestChatService.createChatDialog(privateDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {

                Log.v(TAG, "deserialized dialog 1 = " + result);
//                qbChatDialog = (QBChatDialog) result;
//                Log.v(TAG, "deserialized dialog 2 = " + qbChatDialog);
//                qbChatDialog.initForChat(QBChatService.getInstance());
                privateChatDialog = result;

                byte[] data = SerializationUtils.serialize(privateChatDialog);
                Log.v(TAG, "QB Serialize dialog to data :- " + data);

                DatabaseHelper.getInstance(ChatActivity.this).insertQbIdQbChatPrivateDialoge(qb_id, privateChatDialog.getDialogId(), data, "Private");
//sacccccccccccccccccccccccccccccccccc
                QBChatDialog yourObject = SerializationUtils.deserialize(data);
                Log.v(TAG, "QB Serialize data to dialog :- " + yourObject);

                Log.v(TAG, "User is logged in or not :- " + QBChatService.getInstance().isLoggedIn());
//                TwoTab_Activity.chatService.isLoggedIn();

                if (QBChatService.getInstance().isLoggedIn()) {

//                    chatLo
                } else {
                    twoTab_activity.createSession(AppPreferences.getMobileuser(ChatActivity.this), "12345678");
                }
//                twoTab_activity.createSession(AppPreferences.getMobileuser(ChatActivity.this), "12345678");

                if (Function.isConnectingToInternet(ChatActivity.this)) {

                    initMessageSentListener();
                    registerQbChatListeners();
                    QBInit(groupName, result);
                    systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
//                    sendSystemMessageAboutCreatingDialog(systemMessagesManager, result);
                } else {
                    Toast.makeText(getApplicationContext(), "Check internet connection.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(QBResponseException responseException) {

                Log.v(TAG, "Error Occures sendChatMessage()");
                Log.v(TAG, "send ChatMessage Error :- " + responseException.getMessage());

            }
        });
    }

    public void sendSystemMessageAboutCreatingDialog(QBSystemMessagesManager systemMessagesManager, QBChatDialog dialog) {

        QBChatMessage systemMessageCreatingDialog = TwoTab_Activity.buildSystemMessageAboutCreatingGroupDialog(dialog);
//        systemMessageCreatingDialog.setRecipientId(dialog.getOccupants().get(0));
        Log.v(TAG, "Two tab Activity  self id:- " + AppPreferences.getQBUserId(ChatActivity.this));
        Log.v(TAG, "Two tab Activity  freind id 1 :- " + dialog.getOccupants());
        Log.v(TAG, "Two tab Activity  freind id 2 :- " + dialog.getRecipientId());
        Log.v(TAG, "Two tab Activity  Chat Dialog 20 jan:- " + dialog);

        for (Integer recipientId : dialog.getOccupants()) {

            Log.v(TAG, "Two tab Activity  friend id inside loop:- " + recipientId);
            try {
                if (!recipientId.equals(AppPreferences.getQBUserId(ChatActivity.this))) {
//sdffffffffffffffffffffffffffffffffffffffffffff\
                    systemMessageCreatingDialog.setRecipientId(recipientId);

                    Log.v(TAG, "Two tab Activity  freind id 20 jan 2018 :- " + dialog.getRecipientId());

//                    dialog.initForChat(dialog.getDialogId(), dialog.getType(), QBChatService.getInstance());

                    systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

                    Log.v(TAG, "Creating Dialog to QuickBlox systemMessagesManager :- " + systemMessagesManager);
                    Log.v(TAG, "Creating Dialog to QuickBlox systemMessageCreatingDialog :- " + systemMessageCreatingDialog);
                    systemMessagesManager.sendSystemMessage(systemMessageCreatingDialog);
                    QBInit(groupName, dialog);
                }
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.e(TAG, "Two tab Activity Error :- " + e.getMessage());
            }
        }
    }

    private void getLastSeen() {

       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                Log.v(TAG, "QB status online from database QB_Friend_Id 9 dec :- " + QB_Friend_Id);
                lastSeen = DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(QB_Friend_Id);
                Log.v(TAG, "QB status online from database new 9 dec :- " + lastSeen);
                status.setText(lastSeen);
            }
        }, 100);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //add your code here

                        Log.v(TAG, "QB status online from database QB_Friend_Id 9 dec 2:- " + QB_Friend_Id);
                        lastSeen = DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(QB_Friend_Id);
                        Log.v(TAG, "QB status online from database new 9 dec 2:- " + lastSeen);
                        status.setText(lastSeen);
                    }
                }, 1000);
            }
        });
*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO : Code for get last seen after every 1 secs
                while (true) {
                    try {
                        Thread.sleep(1000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Log.v(TAG, "QB status online from database QB_Friend_Id 9 dec 3:- " + QB_Friend_Id);
                                lastSeen = DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(QB_Friend_Id);


                                Log.v(TAG, "QB status online from database new 9 dec 4:- " + lastSeen);
                                if (isUserBlocked) {
                                    status.setText("");
                                } else {
                                    if (lastSeen.equalsIgnoreCase("offline") || lastSeen.contains("offline")) {
                                        status.setText("");
                                    } else {
                                        status.setText(lastSeen);
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    public void callAdapter() {

        if (groupName.equalsIgnoreCase("")) {
            chatlist = DatabaseHelper.getInstance(ChatActivity.this).getChat("chat", FriendMobile);

        } else {
            chatlist = DatabaseHelper.getInstance(ChatActivity.this).getChat("group", groupName);
        }
        Log.v(TAG, "CHATLISTSS :- " + chatlist.toString());
        chatAdapter = new ChatAdapter(ChatActivity.this, chatlist, this);
        mRecyclerView.setAdapter(chatAdapter);
    }

    private void ifEnterIsSend() {

        if (AppPreferences.getEnetrSend(ChatActivity.this).equalsIgnoreCase("1")) {

            msg_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String message = msg_edittext.getEditableText().toString();
                    if (!message.equalsIgnoreCase("")) {
                        Log.d("ChatListData", chatlist.get(0).toString());
                        Log.d("PrivacyItem", ">>>");

                        if (groupName.equalsIgnoreCase("")) {

//                            if (dvssssssssssssssssssaefaaaaaaaaaaa

//                            if (privacy == true){
//                                unblockpopup();
//                            } else {

                            sendTextMessage(message, "", "", 0, "0", "", null);
                            sendStopTypingInPrivateChat();

//                            }

                        } else {
                            sendGroupMessage(message, "", "", 0, "", "", null);
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, "Please enter message", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
        }
    }

    public void keyboardVisible() {

//        Toast.makeText(getApplicationContext(), "Inside Keyboard visible ...", Toast.LENGTH_SHORT).show();

        final View activityRootView = findViewById(R.id.list_parent);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > Function.dpToPx(ChatActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    // ... do something here

//                    Toast.makeText(getApplicationContext(), "Keyboard is visible ...", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getApplicationContext(), "Keyboard is not visible ...", Toast.LENGTH_SHORT).show();
                    sendStopTypingInPrivateChat();
                }
            }
        });

    }

    /*

        private void QBInit1(String groupName) {

            chatService = QBChatService.getInstance();

            initGlobalMessageListener();

            Log.v(TAG, "onCreate ChatActivity on Thread ID = " + Thread.currentThread().getId());

            QBChatMessage qbChatMessage = null;

            if (getIntent().getAction().equalsIgnoreCase("PrivateChatClick")) {

              */
/*  privateChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);

            Log.v(TAG, "QB Chat Dialog otuside :- 1233   --- " + privateChatDialog);


            List<Integer> occupants_id = new ArrayList<>();
            occupants_id.add(QB_Friend_Id);

//            onPresenceChanged();
//            readDeliverdStatus();

            privateChatDialog.setOccupantsIds(occupants_id);*//*



            Toast.makeText(getApplicationContext(), "Inside Private chat click", Toast.LENGTH_SHORT).show();

            String dialog_id = getIntent().getStringExtra(EXTRA_DIALOG_ID);
            Log.v(TAG, "ChatActivity dialog _id while click on ONE-TO-ONE chat :- " + dialog_id);

            getQBChatDialogByDialogID(dialog_id, "ONE-TO-ONE chat");

//            privateChatDialog = SerializationUtils.deserialize(qbChatDialogBytes);

            Log.v(TAG, "QB Chat Dialog otuside :- 1233 deserialized  lunch --- " + privateChatDialog);

//            privateChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
//            qbChatMessage = (QBChatMessage) getIntent().getExtras().getSerializable("EXTRA_CHAT_MESSAGE");

        } else if (getIntent().getAction().equalsIgnoreCase("GroupChatClick")) {

            Toast.makeText(getApplicationContext(), "Inside Group chat click", Toast.LENGTH_SHORT).show();

            String dialog_id = getIntent().getStringExtra(EXTRA_DIALOG_ID);
            Log.v(TAG, "ChatActivity dialog _id while click on GROUP chat :- " + dialog_id);

            getQBChatDialogByDialogID(dialog_id, "GROUP chat");

        }

        */
/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *//*

        */
/*   Created by me if two tab send dialdg id then this code will be used *//*


        else if (getIntent().getAction().equalsIgnoreCase("fromContact")) {

            Toast.makeText(getApplicationContext(), "Inside create dialog from contact click ...", Toast.LENGTH_SHORT).show();
            Log.v(TAG, " INSIDE from contact creating dialog ");

            privateChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);

            Log.v(TAG, "TEST 2 QB Chat Dialog from :- " + privateChatDialog);

            if (privateChatDialog.getType().getCode() == 3) {

                Log.v(TAG, " INSIDE CHAT ACTIVITY FOR PRIVATE CHAT ");

                List<Integer> occupants_id = new ArrayList<>();
                occupants_id.add(QB_Friend_Id);

                onPresenceChanged();
//                readDeliverdStatus();

                privateChatDialog.setOccupantsIds(occupants_id);

            } else if (privateChatDialog.getType().getCode() == 2) {

                Log.v(TAG, "INSIDE CHAT ACTIVITY FOR GROUP CHAT");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinGroupChat(privateChatDialog);
                    }
                });

//            groupDialodId = privateChatDialog.getDialogId();

//            getQBChatDialogByDialogID(groupDialodId, "", null);

                Log.v(TAG, "TEST 3  Group dialog ID 1:- " + privateChatDialog.getId());
                Log.v(TAG, " Group dialog ID 2:- " + groupDialodId);
            }

            Log.v(TAG, "TEST 4 deserialized dialog = " + privateChatDialog);

            privateChatDialog.initForChat(QBChatService.getInstance());

            chatMessageListener = new ChatMessageListener();
//
            privateChatDialog.addMessageListener(chatMessageListener);
        }
//        initChatConnectionListener();
    }

    private void getQBChatDialogByDialogID(String dialog_id, final String from) {

        Log.v(TAG, "inside creating QBChatDialog in 1234 from:- " + from);

        QBRestChatService.getChatDialogById(dialog_id).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(final QBChatDialog qbChatDialog, Bundle bundle) {

                Log.v(TAG, "QB group Chat dialog in Chat Activity 1234:- " + qbChatDialog);

                qbChatDialogBytes = SerializationUtils.serialize(qbChatDialog);

                Log.v(TAG, "QBChatdialog TYPE in Chat Activity 1234:- " + privateChatDialog.getType());

                Log.v(TAG, "TEST 5 privateChatDialog " + privateChatDialog);
//                Log.v(TAG, "QB ChatMessage PrivateChat :- " + qbChatMessage);

                if (privateChatDialog.getType().getCode() == 3) {

                    Log.v(TAG, " INSIDE CHAT ACTIVITY FOR PRIVATE CHAT 1234 ");

                    List<Integer> occupants_id = new ArrayList<>();
                    occupants_id.add(QB_Friend_Id);

                    privateChatDialog.setOccupantsIds(occupants_id);

                    onPresenceChanged();

                } else if (privateChatDialog.getType().getCode() == 2) {

                    Log.v(TAG, "INSIDE CHAT ACTIVITY FOR GROUP CHAT");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            joinGroupChat(privateChatDialog);
                        }
                    });

                    Log.v(TAG, "TEST 6  Group dialog ID 1:- " + privateChatDialog.getDialogId());
                    Log.v(TAG, " Group dialog ID 2:- " + groupDialodId);
                }

                Log.v(TAG, "TEST 7 deserialized dialog 1234 :- " + privateChatDialog);

                privateChatDialog.initForChat(QBChatService.getInstance());

                chatMessageListener = new ChatMessageListener();
//
                privateChatDialog.addMessageListener(chatMessageListener);
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error in getting QBChatDialog from dialod id " + e.getMessage());

            }
        });

        initIsTypingListener();
        initPrivateChatMessageListener();

    }
*/
    private void QBInit(String groupName, final QBChatDialog privateChatDialog) {

        // TODO: QBInit() iniitalize Quick blox data
        chatService = QBChatService.getInstance();
        privacyListsManager = chatService.getPrivacyListsManager();
        initIsTypingListener();
        Log.d("TRhisljsdjf ...  ", "  kkkkk   " + groupName);
        if (groupName.equalsIgnoreCase("")) {
            initMessageSentListener();
        } else {
//            groupMessageSend();
        }
        initPrivateChatMessageListener();
        initGlobalMessageListener();

        Log.v(TAG, "onCreate ChatActivity on Thread ID = " + Thread.currentThread().getId());

//        privateChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);

        Log.v(TAG, "QB Chat Dialog from :- " + privateChatDialog);


        if (privateChatDialog.getType().getCode() == 3) {

            Log.v(TAG, " INSIDE CHAT ACTIVITY FOR PRIVATE CHAT ");

            Log.v(TAG, "QB_Friend_Id in QBInit Method :-  " + QB_Friend_Id);

            List<Integer> occupants_id = new ArrayList<>();
            occupants_id.add(QB_Friend_Id);

            if (QBChatService.getInstance().isLoggedIn()) {
                try {
                    privateChatDialog.initForChat(QBChatService.getInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v(TAG, "Exception in initForCHat() :- " + e.getMessage());
                }
            } else {

//                QBUser user = new QBUser(AppPreferences.getMobileuser(ChatActivity.this), "12345678");
//                TwoTab_Activity.instance.chatLogin(user);

            }
//            onPresenceChanged();
//            readDeliverdStatus();
            initMessageStatusManagerAndListener();

            privateChatDialog.setOccupantsIds(occupants_id);
/*

            try {
                privateChatDialog.readMessage(qbChatMessage);
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
*/

//            markMessageAsRead(privateChatDialog.getDialogId());

        } else if (privateChatDialog.getType().getCode() == 2) {

            Log.v(TAG, "INSIDE CHAT ACTIVITY FOR GROUP CHAT");
            privateChatDialog.initForChat(QBChatService.getInstance());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    joinGroupChat(privateChatDialog);
                }
            });

//            groupDialodId = privateChatDialog.getDialogId();

//            getQBChatDialogByDialogID(groupDialodId, "", null);

            Log.v(TAG, " Group dialog ID 1:- " + privateChatDialog.getId());
            Log.v(TAG, " Group dialog ID 2:- " + groupDialodId);
        }

        Log.v(TAG, "deserialized dialog = " + privateChatDialog);

//        privateChatDialog.initForChat(QBChatService.getInstance());

        chatMessageListener = new ChatMessageListener();
//
//        privateChatDialog.addMessageListener(chatMessageListener);

//        initChatConnectionListener();
    }

    private void joinGroupChat(final QBChatDialog qbChatDialog) {

        DiscussionHistory discussionHistory = new DiscussionHistory();
        discussionHistory.setMaxStanzas(0);

        final QBEntityCallback clbck = new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {

                Log.v(TAG, "Joinning grp done");
                Log.v(TAG, "Group joining completed ...");
                Toast.makeText(getApplicationContext(), "Group join done", Toast.LENGTH_SHORT).show();

                // add listeners
                qbChatDialog.addMessageListener(groupChatDialogMessageListener);
//                qbChatDialog.addParticipantListener(participantListener);
                qbChatDialog.addMessageSentListener(groupChatDialogMessageSentListener);
                qbChatDialog.addIsTypingListener(groupChatDialogTypingListener);
            }

            @Override
            public void onError(final QBResponseException list) {
                Log.e(TAG, "Group joining Error :- " + list.getMessage());
                Snackbar.make(findViewById(android.R.id.content), R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                qbChatDialog.initForChat(QBChatService.getInstance());
            }
        };

        qbChatDialog.join(discussionHistory, clbck);

       /* qbChatDialog.join(discussionHistory, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {

                Log.v(TAG, "Joinning grp done" );
                Log.v(TAG , "Group joining completed ...");
                Toast.makeText(getApplicationContext(), "Group join done", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG , "Group joining Error :- " + e.getMessage());
                Snackbar.make(findViewById(android.R.id.content), R.string.connection_error, Snackbar.LENGTH_SHORT).show();

            }
        });*/


    }

    private void setToolbar() {


        // TODO: setToolbar() method

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.profile_default);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    private void getDataFromIntent() {

        /* 24 jan 2018 Code Added for image sharing from gallery to my application froom Ravi's  */

        Intent intent = getIntent();
        filePathFromGallaryN = intent.getStringExtra("requestFilePath");
        Log.d(TAG, " requestCode New : -- " + filePathFromGallaryN + " requestFilePath : -- " + filePathFromGallaryN);

        /* 24 jan 2018 Code Added for image sharing from gallery to my application froom Ravi's  */

/*
        if (getIntent().getAction().equalsIgnoreCase("ChatAct")) {

            Toast.makeText(getApplicationContext(), "calling inside chat activity", Toast.LENGTH_SHORT).show();
            allBeans = getIntent().getParcelableExtra("value");
        } else if (getIntent().getAction().equalsIgnoreCase("PrivateChat")) {
            allBeans = getIntent().getParcelableExtra("value");
        } else if (getIntent().getAction().equalsIgnoreCase("GroupChat")) {
            allBeans = getIntent().getParcelableExtra("value");
        } else if (getIntent().getAction().equalsIgnoreCase("FavoriteAdapter")) {
        }
        */

        // TODO: getDataFromIntent() get data from bundle

        allBeans = getIntent().getParcelableExtra("value");

        FriendStatus = allBeans.getFriendStatus();
        FriendName = allBeans.getFriendname();
        FriendMobile = allBeans.getFriendmobile().replace(" ", "").replace("+", "");
        MobileWithCountryCode = allBeans.getFriendmobile().replace(" ", "");
        Log.v(TAG, "friend MobileWithCountryCode :- " + MobileWithCountryCode + " ---- " + FriendMobile);

        FriendImage = allBeans.getFriendimage();
        FriendId = allBeans.getFriendid();
        reciverlanguages = allBeans.getLanguages();
        groupName = allBeans.getGroupName();
        if (!groupName.equalsIgnoreCase("")) {
            status.setVisibility(View.GONE);
        }
        groupId = allBeans.getGroupid();

        System.out.println("grpiddddd" + groupId);
        GroupImage = allBeans.getGroupImage();
        QB_Friend_Id = allBeans.getFriendQB_id();
/*
        QBPrivacyListItem qbPrivacyListItem = new QBPrivacyListItem();
        qbPrivacyListItem.setAllow(false);
        qbPrivacyListItem.setType(QBPrivacyListItem.Type.USER_ID);
        qbPrivacyListItem.setValueForType(QB_Friend_Id.toString());
*/
        if (FriendName.matches("[0-9]+") && FriendName.length() > 9) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }

        Log.v(TAG, "Recipient Id :- " + QB_Friend_Id);

        Log.v(TAG, "UserList :- " + FriendMobile + ">>>mob>>>" + FriendName + "/n" + FriendStatus + "/n" + groupName + "/n QB ID of user :- " + QB_Friend_Id);

        if (groupName.equalsIgnoreCase("")) {
            img_eye.setVisibility(View.VISIBLE);
            if (Function.isStringInt(FriendName)) {
                toolbartext.setText("+" + FriendName);
            } else {
                toolbartext.setText(FriendName);
            }

            if (FriendImage == null) {

            } else if (!FriendImage.equalsIgnoreCase("")) {
                Picasso.with(ChatActivity.this).load(FriendImage).error(R.drawable.profile_default)
                        .placeholder(R.drawable.profile_pic_default)
                        .resize(200, 200)
                        .into(conversationimage);
            }
        } else {
            img_eye.setVisibility(View.GONE);
            toolbartext.setText(groupName);
            if (GroupImage == null) {

            } else if (!GroupImage.equalsIgnoreCase("")) {
                Picasso.with(ChatActivity.this).load(GroupImage).error(R.drawable.profile_default)
                        .placeholder(R.drawable.profile_pic_default)
                        .resize(200, 200)
                        .into(conversationimage);
            }
        }

        user1 = AppPreferences.getMobileuser(getApplicationContext());
        user2 = FriendMobile;
        FriendMobileTWO = user2;
        Log.d("frndnumber", user2);
        senderName = AppPreferences.getFirstUsername(getApplicationContext());

        lastSeen = DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(QB_Friend_Id);
        Log.v(TAG, "QB status online from database 4444444444444 :- " + lastSeen);

        if (!groupName.equalsIgnoreCase("")) {
            status.setText("");
        } else {
            if (lastSeen.equalsIgnoreCase("offline") || lastSeen.contains("offline")) {
                status.setText("");
            } else {
                status.setText(lastSeen);
            }
        }

        isUserBlocked = blockUserDataBaseHelper.getBlockedUser(QB_Friend_Id);
        if (isUserBlocked) {

            status.setText("");

        } else {
            if (lastSeen.equalsIgnoreCase("offline") || lastSeen.contains("offline")) {
                status.setText("");
            } else {
                status.setText(lastSeen);
            }
        }
    }

    private void initGlobalMessageListener() {

        Log.v(TAG, " Inside initGlobalMessageListener()  -----" + " downloaded");

        globalChatDialogMessageListener = new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogId, QBChatMessage qbChatMessage, Integer senderId) {

                QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog chatDialog, Bundle bundle) {
                        Log.v(TAG, chatDialog.getType().toString() + " chat with id " + chatDialog.getDialogId() + " downloaded");

                        Log.v(TAG, "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n\n" +
                                "GLOBAL MESSAGE LISTENER :--" + chatDialog
                                + "\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                        if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {

                            privateChatDialog = chatDialog;
                            privateChatDialog.addMessageListener(privateChatDialogMessageListener);
                            privateChatDialog.addMessageSentListener(privateChatDialogMessageSentListener);
                            privateChatDialog.addIsTypingListener(privateChatDialogTypingListener);

                        } else {

                            groupChatDialog = chatDialog;
                            groupChatDialog.addMessageListener(groupChatDialogMessageListener);
                            groupChatDialog.addMessageSentListener(groupChatDialogMessageSentListener);
                            groupChatDialog.addIsTypingListener(groupChatDialogTypingListener);

                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.v(TAG, "Error loading dialog from global message listener: " + e);
                    }
                });
            }

            @Override
            public void processError(String dialogId, QBChatException e, QBChatMessage qbChatMessage, Integer senderId) {
                Log.v(TAG, "Received error message to message listener :- " + e.getMessage());
            }
        };
//        QBChatService.getInstance().getIncomingMessagesManager().addDialogMessageListener(globalChatDialogMessageListener);
    }

    private void sendStopTypingInPrivateChat() {

//        initIsTypingListener();
        Log.v(TAG, "inside typing status and");
        if (privateChatDialog == null) {
            Log.v(TAG, "Please create private dialog first");
            return;
        }

//        privateChatDialog.addIsTypingListener(privateChatDialogTypingListener);

        try {
            privateChatDialog.sendStopTypingNotification();
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.v(TAG, "send stop typing error: " + e.getClass().getSimpleName());
        }
    }

    private void initChatConnectionListener() {


        chatConnectionListener = new VerboseQbChatConnectionListener(findViewById(android.R.id.content)) {
            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                /*
                switch (privateChatDialog.getType()) {
                    case GROUP:
                        chatAdapter = null;
                        // Join active room if we're in Group Chat
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                joinGroupChat();
                            }
                        });
                        break;
                }*/
            }
        };
    }

    private void sendDialogId() {

        Log.v(TAG, "TEST 9 CHAT Dialog sending result to Two Tab activity :- " + privateChatDialog);

        Intent result = new Intent();
        result.putExtra(EXTRA_DIALOG_ID, privateChatDialog);
        setResult(RESULT_OK, result);

    }

    private void sendNotification(List<Integer> occupants, String normal_message, String senderNameNew, String sender) {

        Log.v(TAG, "send Notification occupants :- " + occupants);
        Log.v(TAG, "send Notification senderNameNew :- " + senderNameNew);
        Log.v(TAG, "send Notification senderNo :- " + sender);

        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();

        userIds.addAll(occupants);

        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
        qbEvent.setUserIds(userIds);

        JSONObject json = new JSONObject();
        try {

            json.put("message_new", normal_message);
            json.put("qb_sender", senderNameNew);
            json.put("qb_sender_no", sender); // sender no

        } catch (Exception e) {
            e.printStackTrace();
        }
//        qbEvent.setMessage(json.toString());
        Log.v(TAG, "send Notification qbEvent :- " + qbEvent);
        Log.v(TAG, "send Notification Json  :- " + json.toString());
        qbEvent.setMessage(json.toString());

        QBPushNotifications.createEvent(qbEvent).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                Log.v(TAG, "onSuccess.............");
                Log.v(TAG, "onSuccess.............qbEvent" + qbEvent);
                Log.v(TAG, "onSuccess.............bundle" + bundle);
                QBSettings.getInstance().setEnablePushNotification(true);
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "onError.............");
                Log.v(TAG, "onError.............e" + e);
            }
        });
    }

    private void sendChatMessage(final String text, final ChatMessage chatMessage, final String messageType, QBChatMessage qbChatMessage) {
        // TODO: send chatMessage() method for sending message to QuickBlox

        Log.d(TAG, "ChatMessage save inside QB message send :- " + chatMessage.toString());
        Log.d(TAG, "chatMessage.body  :- " + chatMessage.body);
        Log.v(TAG, "QB Serialize sending dialog bytes data  :- " + chatMessage.qbChatDialogBytes);
        Log.v(TAG, "QB Message n Dialog Id :- \n" + "1. " + chatMessage.qbMessageId + "\n2. " + chatMessage.dialog_id);
        Log.d(TAG, "ChatMessage save inside QB message send 1:- " + chatMessage.toString());
        Log.d(TAG, "qbChatMessage 2:- " + qbChatMessage);
        //--------------------------------------------------------------------------------//
        Log.d(TAG, "ChatMessage save inside QB message send 2222 :- " + chatMessage.toString());
        //ddddd
        //--------------------------------------------------------------------------------//

        if (!QBDialogType.PRIVATE.equals(privateChatDialog.getType()) && !privateChatDialog.isJoined()) {
            Toast.makeText(getApplicationContext(), "You're still joining a group chat, please wait a bit", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chatMessage.groupName.equalsIgnoreCase("")) {
            privateChatDialog.addMessageSentListener(privateChatDialogMessageSentListener);
        } else {
            privateChatDialog.addMessageSentListener(groupChatDialogMessageSentListener);
        }

        try {
            Log.e(TAG, "privateChatDialog while sending message to prsonal chat :- " + privateChatDialog);
            Log.e(TAG, "Messsage body sendQB CHAT Message :- " + qbChatMessage);
            privateChatDialog.sendMessage(qbChatMessage);  // msg send to all online / offline users

            if (chatMessage.groupName.equalsIgnoreCase("")) {

                byte[] qbChatDialogBytes = SerializationUtils.serialize(privateChatDialog);
                Log.v(TAG, "QB Serialize dialog to data 1 :- " + chatMessage.qbChatDialogBytes);
                Log.v(TAG, "QB Serialize dialog to data 2 :- " + qbChatDialogBytes);

//                QBChatDialog yourObject = SerializationUtils.deserialize(chatMessage.qbChatDialogBytes);
                QBChatDialog yourObject = SerializationUtils.deserialize(qbChatDialogBytes);
                Log.v(TAG, "QB Serialize data to dialog 2 :- " + yourObject);

                if (messageType.equalsIgnoreCase("FileSharing")) {
                    DatabaseHelper.getInstance(ChatActivity.this).UpdateQbFileIdAndqbFileUid(chatMessage.qbFileUploadId, chatMessage.qbFileUid, chatMessage.dialog_id, chatMessage.qbMessageId);
                } else if (messageType.equalsIgnoreCase("Normal_Message")) {
                    Log.d(TAG, " messageInFiConditon : -- " + chatMessage);

                    DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
                    DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", chatMessage.receiver);
                    chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
                    mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }

            } else {
                Log.d(TAG, " messageInElseiConditon : -- " + chatMessage);

//                Toast.makeText(getApplicationContext(), "inside sending message to Groups chat ...", Toast.LENGTH_SHORT).show();
                if (messageType.equalsIgnoreCase("FileSharing")) {
                    DatabaseHelper.getInstance(ChatActivity.this).UpdateQbFileIdAndqbFileUid(chatMessage.qbFileUploadId, chatMessage.qbFileUid, chatMessage.dialog_id, chatMessage.qbMessageId);
                } else if (messageType.equalsIgnoreCase("Normal_Message")) {
                    DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
                    DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", chatMessage.receiver);
                    chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
                    mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }

            if (ChatActivity.instance == null && ChatActivity.instance == null) {

                Log.e(TAG, "send notification ");
//                        generateNofification(chatMessage, context);
            }
//fbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
            sendNotification(privateChatDialog.getOccupants(), chatMessage.body, chatMessage.senderName, chatMessage.sender);

            MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.tick);
            if (AppPreferences.getConvertTone(ChatActivity.this).equalsIgnoreCase("false")) {
                mp.stop();
            } else {
                mp.start();
                chatAdapter.notifyDataSetChanged();
            }

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in sendMessage(chatMessage) to QuickBlox 2:- " + e.getMessage());
        }
    }

    public void sendGroupMessage(String message, String file, String fileName, int qbFileId, String qbFileUid, String fileUrl, QBChatMessage qbChatMessage_share) {

        // TODO: sendGroupMessage() method for sending group message to QuickBlox

        Log.v(TAG, "Inside text message sendTextMessage \n File :- " + file);
        Log.v(TAG, "Filename :- " + fileName);
        Log.v(TAG, "QB file id :- " + qbFileId);
        Log.v(TAG, "QB qbFileUid :- " + qbFileUid);
        Log.v(TAG, "User QB id  i.e. Sender id :- " + AppPreferences.getQBUserId(ChatActivity.this));

        // final ChatMessage chatMessage = new ChatMessage(FriendName, FriendName, FriendName, FriendName,
        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(ChatActivity.this), user2, FriendName,
                groupName, message, "" + random.nextInt(1000), file, true);
        chatMessage.setMsgID();
        chatMessage.body = message;
        chatMessage.fileName = fileName;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
//dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        /* calander.getTimeInMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        String formattedDate = df.format(calander.getTime());
        Log.d(TAG, " formatteDateAndTime : ----" + formattedDate);
        String data[] = formattedDate.split(" ");

        Log.v(TAG, "data[0]..........." + data[0]);
        Log.v(TAG, "data[1]..........." + data[1]);

        chatMessage.Date = data[0];
        chatMessage.Time = data[1] + " " + data[2];*/

        long date_in_long = Calendar.getInstance().getTimeInMillis();
        Date date_cal = new Date(date_in_long);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        String dateText = sdf.format(date_cal);
        Log.v(TAG, "21 January date 12345 :- " + dateText);
        String data[] = dateText.split(" ");

        Log.v(TAG, "data[0]..........." + data[0]);
        Log.v(TAG, "data[1]..........." + data[1]);

        chatMessage.Date = data[0];
        chatMessage.Time = data[1] + " " + data[2];


        Log.v(TAG, "chatMessage.Date .......... :- " + chatMessage.Date);
        Log.v(TAG, "chatMessage.Time .......... :- " + chatMessage.Time);

        chatMessage.type = Message.Type.groupchat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ChatActivity.this));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ChatActivity.this);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.groupid = groupId;
        chatMessage.Groupimage = GroupImage;
        chatMessage.dialog_id = groupDialodId;
        chatMessage.lastseen = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
        onlineStatus = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
        chatMessage.sender_QB_Id = AppPreferences.getQBUserId(ChatActivity.this);
        msg_edittext.setText("");

        chatMessage.msgStatus = "0";

        BlockUserDataBaseHelper blockUserDataBaseHelper = new BlockUserDataBaseHelper(ChatActivity.this);
        Log.d(TAG, " FriendNameMessa : " + FriendName + " FriendMobileMessa : " + FriendMobile + " FriendOcupantIdMessa : " + QB_Friend_Id);
        ArrayList<Integer> allBlockedUsers = blockUserDataBaseHelper.getAllBlockedUsers();
        Log.d(TAG, " GetAllBlokedUsersMessa .. " + allBlockedUsers.toString());

        if (!fileName.equalsIgnoreCase("")) {

            String fileExte = Function.getFileExtention(fileName);
            String folderType;

            String msg = chatMessage.body;
            if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
                folderType = "SpeakAme Contact";
                chatMessage.Contact = msg;
                String contact[] = msg.split(",");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", contact[0]);
                    jsonObject.put("number", contact[1]);
                    jsonObject.put("image", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                chatMessage.Contact = jsonObject.toString();

            } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "SpeakAme Image";
                chatMessage.Image = file;
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                folderType = "SpeakAme Video";
                chatMessage.Video = file;
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "SpeakAme Document";
                chatMessage.Document = file;
            } else {
                folderType = "SpeakAme Test";
            }

            File SpeakAmeDirectory = Function.createFolder(folderType);
            chatMessage.fileName = Function.generateNewFileName(fileExte);
            chatMessage.files = Function.copyFile(file, SpeakAmeDirectory + "/" + chatMessage.fileName);

            chatMessage.qbFileUploadId = qbFileId;
            chatMessage.qbFileUid = qbFileUid;

            Log.v(TAG, "Image upload 1 :- " + SpeakAmeDirectory + "\n" + chatMessage.fileName + "\n" + chatMessage.files);
            Log.v(TAG, "Image upload 2 :- " + SpeakAmeDirectory + "\n" + chatMessage.fileName + "\n" + chatMessage.qbFileUploadId);

            chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
            chatMessage.timeZone = timezone.getID();

            String body = gson.toJson(chatMessage);
            qbChatMessage_share.setProperty("custom_body", body);
            sendChatMessage(body, chatMessage, "FileSharing", qbChatMessage_share);

        } else {

            Log.v(TAG, "ChatMessage save group message :- " + chatMessage.toString());
            Log.v(TAG, "ChatMessage Save 1:- " + chatMessage.toString());

            chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
            chatMessage.timeZone = timezone.getID();

            String body = gson.toJson(chatMessage);

            QBChatMessage qbChatMessage = new QBChatMessage();
            qbChatMessage.setBody(chatMessage.body);
            qbChatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
            qbChatMessage.setProperty("custom_body", body); // hii;
            qbChatMessage.setProperty("self_destructive", "1");
            qbChatMessage.setDateSent(date.getTime());

            qbChatMessage.setMarkable(true);
            qbChatMessage.setSenderId(chatMessage.sender_QB_Id);

            chatMessage.qbMessageId = qbChatMessage.getId();
            chatMessage.dialog_id = privateChatDialog.getDialogId();
            chatMessage.qbChatDialogBytes = SerializationUtils.serialize(privateChatDialog);
            if (chatMessage.groupName.equalsIgnoreCase("")) {
                qbChatMessage.setRecipientId(chatMessage.receiver_QB_Id);
            }

            Log.v(TAG, "QB Message n Dialog Id :- \n" + "1. " + chatMessage.qbMessageId + "\n2. " + chatMessage.dialog_id);

            sendChatMessage(body, chatMessage, "Normal_Message", qbChatMessage);

        }

//        chatMessage.qbChatDialogBytes = SerializationUtils.serialize(privateChatDialog);


//        sendChatMessage(chatMessage.toString(), chatMessage);
//        sendChatMessage(body, chatMessage, "", qbChatMessage);

/*        DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
        DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", chatMessage.receiver);

        mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);*/
    }

    public void sendTextMessage(String message, String file, String fileName, int qbFileId, String qbFileUid, String fileUrl, QBChatMessage qbChatMessage_file) {

        // TODO: sendTextMessage() method for sending private chat message to QuickBlox

        Log.v(TAG, "Inside text message sendTextMessage \n File :- " + file);
        Log.v(TAG, "Filename :- " + fileName);
        Log.v(TAG, "QB file id :- " + qbFileId);
        Log.v(TAG, "QB qbFileUid :- " + qbFileUid);
        Log.v(TAG, "User QB id  i.e. Sender id :- " + AppPreferences.getQBUserId(ChatActivity.this));
        String MyImage = "";
        String MyStatus = "";

        if (AppPreferences.getPicprivacy(ChatActivity.this).equalsIgnoreCase(AppConstants.EVERYONE)) {
            MyImage = AppPreferences.getUserprofile(ChatActivity.this);
            MyStatus = AppPreferences.getUserstatus(ChatActivity.this);
        } else if (AppPreferences.getPicprivacy(ChatActivity.this).equalsIgnoreCase(AppConstants.MYFRIENDS)) {
            if (!Function.isStringInt(FriendName)) {
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

        Log.v(TAG, "chatMessage.Date ..........12341435 :- " + chatMessage.Date);
        Log.v(TAG, "chatMessage.Time ..........12341435 :- " + chatMessage.Time);

        /* calander.getTimeInMillis();
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
String formattedDate = df.format(calander.getTime());
Log.d(TAG, " formatteDateAndTime : ----" + formattedDate);
String data[] = formattedDate.split(" ");

Log.v(TAG, "data[0]..........." + data[0]);
Log.v(TAG, "data[1]..........." + data[1]);
*/


        long date_in_long = Calendar.getInstance().getTimeInMillis();
        Date date_cal = new Date(date_in_long);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        String dateText = sdf.format(date_cal);
        Log.v(TAG, "21 January date 12345 :- " + dateText);
        String data[] = dateText.split(" ");

        Log.v(TAG, "data[0]..........." + data[0]);
        Log.v(TAG, "data[1]..........." + data[1]);

        chatMessage.Date = data[0];
        chatMessage.Time = data[1] + " " + data[2];

        Log.v(TAG, "chatMessage.Date .......... :- " + chatMessage.Date);
        Log.v(TAG, "chatMessage.Time .......... :- " + chatMessage.Time);

        //        chatMessage.Time = CommonMethods.getCurrentTime();

//        chatMessage.Date = CommonMethods.getCurrentDate();
//        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.type = Message.Type.chat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ChatActivity.this));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ChatActivity.this);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.MyImage = MyImage;
        chatMessage.userStatus = MyStatus;
        chatMessage.lastseen = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
        chatMessage.receiver_QB_Id = QB_Friend_Id;
        chatMessage.sender_QB_Id = AppPreferences.getQBUserId(ChatActivity.this);
        chatMessage.friend_QB_Id = QB_Friend_Id;
        chatMessage.readStatus = "0";

        onlineStatus = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
        //chatMessage.fileData = fileData;
        msg_edittext.setText("");
        fm.setVisibility(View.GONE);
        //ChatActivity activity = new ChatActivity();

        chatMessage.ReciverFriendImage = FriendImage;
        chatMessage.msgStatus = "0";

        Log.v(TAG, "TOTF MESSAGE Testing message files before :- " + chatMessage.files);
        chatMessage.files = "";
        Log.v(TAG, "TOTF MESSAGE Testing message files after :- " + chatMessage.files);


        BlockUserDataBaseHelper blockUserDataBaseHelper = new BlockUserDataBaseHelper(ChatActivity.this);
        Log.d(TAG, " FriendNameMessa : " + FriendName + " FriendMobileMessa : " + FriendMobile + " FriendOcupantIdMessa : " + QB_Friend_Id);
        ArrayList<Integer> allBlockedUsers = blockUserDataBaseHelper.getAllBlockedUsers();
        Log.d(TAG, " GetAllBlokedUsersMessa .. " + allBlockedUsers.toString());

        if (!fileName.equalsIgnoreCase("")) {

            String fileExte = Function.getFileExtention(fileName);
            String folderType;

            String msg = chatMessage.body;
            if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
                folderType = "SpeakAme Contact";
                Log.v(TAG, " Contact Sending :- " + msg);
                chatMessage.Contact = msg;
                String contact[] = msg.split(",");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", contact[0]);
                    jsonObject.put("number", contact[1]);
                    jsonObject.put("image", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                chatMessage.Contact = jsonObject.toString();

            } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "SpeakAme Image";
                chatMessage.Image = qbFileUid;
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                folderType = "SpeakAme Video";
                chatMessage.Video = qbFileUid;
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "SpeakAme Document";
                chatMessage.Document = qbFileUid;
            } else {
                folderType = "SpeakAme Test";
            }

            File SpeakAmeDirectory = Function.createFolder(folderType);
            chatMessage.fileName = Function.generateNewFileName(fileExte);
            chatMessage.files = Function.copyFile(file, SpeakAmeDirectory + "/" + chatMessage.fileName);

            chatMessage.qbFileUploadId = qbFileId;
            chatMessage.qbFileUid = qbFileUid;

            Log.d(TAG, "Files name : while sending data :- " + SpeakAmeDirectory + "\n -- " + chatMessage.fileName + "\n -- " + chatMessage.files);
            Log.d(TAG, SpeakAmeDirectory + "\n" + chatMessage.fileName + "\n" + chatMessage.qbFileUploadId);

            if (allBlockedUsers.contains(QB_Friend_Id)) {
                Log.d(TAG, " That user is blocked");
                unblockpopup();
            } else {
                chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
                chatMessage.timeZone = timezone.getID();
                String body = gson.toJson(chatMessage);
                qbChatMessage_file.setProperty("custom_body", body);
                sendChatMessage(body, chatMessage, "FileSharing", qbChatMessage_file);
//              dvsvsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsd
                Log.d(TAG, "That user is Unblocked");
            }

        } else {

            Log.v(TAG, "ChatMessage Save 1:- " + chatMessage.toString());

            if (allBlockedUsers.contains(QB_Friend_Id)) {

                Log.d(TAG, " That user is blocked");
                unblockpopup();

            } else {

//            String body = gson.toJson(chatMessage);
                chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
                chatMessage.timeZone = timezone.getID();
                Log.v(TAG, "Sender Time Zone :- " + chatMessage.timeZone);

                String body = gson.toJson(chatMessage);

                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(chatMessage.body);
                qbChatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                qbChatMessage.setProperty("custom_body", body);
                qbChatMessage.setDateSent(date.getTime());
                qbChatMessage.setMarkable(true);
                qbChatMessage.setSenderId(chatMessage.sender_QB_Id);
//vdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
                chatMessage.qbMessageId = qbChatMessage.getId();
//csaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                chatMessage.dialog_id = privateChatDialog.getDialogId();

//                fdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddb
                chatMessage.qbChatDialogBytes = SerializationUtils.serialize(privateChatDialog);

                if (chatMessage.groupName.equalsIgnoreCase("")) {

                    qbChatMessage.setRecipientId(chatMessage.receiver_QB_Id);

                }
//                vsddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd

                Log.v(TAG, "QB Message n Dialog Id :- \n" + "1. " + chatMessage.qbMessageId + "\n2. " + chatMessage.dialog_id);
                sendChatMessage(body, chatMessage, "Normal_Message", qbChatMessage);
                Log.d(TAG, "That user is Unblocked");
            }
        }

    }

    private void initConnectionListener() {

        chatConnectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection xmppConnection) {
                Log.i(TAG, "connected()");
            }

            @Override
            public void authenticated(XMPPConnection xmppConnection, boolean b) {
                Log.i(TAG, "authenticated()");
            }

            @Override
            public void connectionClosed() {
                Log.i(TAG, "connectionClosed()");
            }

            @Override
            public void connectionClosedOnError(Exception e) {

            }

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectingIn(int i) {

            }

            @Override
            public void reconnectionFailed(Exception e) {

            }
        };

        chatService.addConnectionListener(chatConnectionListener);
    }

    private void initViews() {

        // TODO: initViews() views initialization

        instance = this;
        qbService = new QBService();
//        registerQbChatListeners();
        gson = new Gson();
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        status = (TextView) findViewById(R.id.status);

        status.setSelected(true);
        status.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        status.setSingleLine(true);

        conversationimage = (ImageView) findViewById(R.id.conversation_contact_photo);

        toolbartext.setText("Chat list");
        toolbartext.setSingleLine();
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        img_eye = (ImageView) findViewById(R.id.iv_chat_eye);
        smily_img = (ImageView) findViewById(R.id.iv_smily);
        msg_edittext = (EmojiconEditText) findViewById(R.id.messageEditText);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        sendButton = (FloatingActionButton) findViewById(R.id.fab_sendMessageButton);
//        sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        btn1 = (ImageButton) findViewById(R.id.pic_id);
        btn2 = (ImageButton) findViewById(R.id.vidoid);
        btn3 = (ImageButton) findViewById(R.id.docid);
        btn4 = (ImageButton) findViewById(R.id.contactid);
        fm = (FrameLayout) findViewById(R.id.emojicons);

        linearLayout = (LinearLayout) findViewById(R.id.linear);
        mbtnadd = (Button) findViewById(R.id.btn2);
        mbtnblock = (Button) findViewById(R.id.btn1);

        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);
        relative_layout = (RelativeLayout) findViewById(R.id.relative_layout);

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        backLinearLayout = (LinearLayout) findViewById(R.id.backLinearLayout);
        nameToolbarTitle = (LinearLayout) findViewById(R.id.nameToolbarTitle);

        random = new Random();
        blockUserDataBaseHelper = new BlockUserDataBaseHelper(ChatActivity.this);

        timezone = TimeZone.getDefault();
        calander = Calendar.getInstance(timezone);
        date = calander.getTime();
    }

    private void setListener() {

        msg_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() >= 1 || s.toString().trim().length() >= 1) {
                    sendButton.setVisibility(View.VISIBLE);
                    handler.postDelayed(input_finish_checker, delay);
                } else {
                    sendButton.setVisibility(View.GONE);
                }
//                sendIsTypingInPrivateChat();
//                initGlobalMessageListener();
                if (groupName.equalsIgnoreCase("")) {
//                    qbService.sendIsTypingInPrivateChat(privateChatDialog, privateChatDialogTypingListener);

                    try {
/*                        if (!blocked) {
                            sdvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                        } else {
                        }*/


                        Log.v(TAG, "user blocked :- " + QB_Friend_Id);
                        Log.v(TAG, "user blocked :- " + isUserBlocked);

                        if (isUserBlocked) {
                            sendStopTypingInPrivateChat();
                            Log.v(TAG, "user is blocked " + isUserBlocked);

                        } else {

                            sendIsTypingInPrivateChat();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (count <= 0) {
                    Log.v(TAG, "edit text is blank ....:- " + QB_Friend_Id);
                    sendStopTypingInPrivateChat();
                    if (isUserBlocked) {
                        status.setText("");
                    } else {
                        if (!groupName.equalsIgnoreCase("")) {
                            status.setText("");
                        } else {
                            if (lastSeen.equalsIgnoreCase("offline") || lastSeen.contains("offline")) {
                                status.setText("");
                            } else {
                                status.setText(lastSeen);
                            }
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                sendStopTypingInPrivateChat();

                handler.removeCallbacks(input_finish_checker);
                Log.v(TAG, "Count after text changed edit text....:- " + count);
                Log.v(TAG, "start after text changed edit text....:- " + start);
                Log.v(TAG, "before after text changed edit text....:- " + before);
                Log.v(TAG, "s after text changed edit text....:- " + s);

                if (start == 0) {
                    sendStopTypingInPrivateChat();
                }
            }

        });

        nameToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Click toolbar ", Toast.LENGTH_SHORT).show();

                if (groupName.equalsIgnoreCase("")) {
                    Intent intent = new Intent(ChatActivity.this, CollapsingToolbarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("name", FriendName);
                    intent.putExtra("number", FriendMobile);
                    intent.putExtra("status", FriendStatus);
                    intent.putExtra("image", FriendImage);
                    intent.putExtra("value", "");
                    startActivity(intent);
                } else {
           /* Intent intent = new Intent(ChatActivity.this,CollapsingToolbarActivity.class);
            intent.putExtra("GroupId", groupId);
            intent.putExtra("GroupName", groupName);
            intent.putExtra("GroupDate", "");
            intent.putExtra("GroupTime", "");
            intent.putExtra("GroupImage", GroupImage);
            intent.putExtra("value", "");
            intent.putExtra("groupJid", FriendMobile);
            intent.putExtra("reciverlanguages", reciverlanguages);
            startActivity(intent);*/
                }

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " btn one clicked : -- " + " button");
               /* Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType("image*//*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLARY_CAPTURE);
*/
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

//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                //intent.setType("application/pdf");
//                String[] mimetypes = {"application|text"};
//                intent.setType("application|text");
//                //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivityForResult(Intent.createChooser(intent, "Select file"), PICKFILE_REQUEST_CODE);

/*
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("application*/
/*|text*/
/*");
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + "/Documents/");
//                intent.setData(uri);
//                intent.setType("text/csv");
                startActivityForResult(Intent.createChooser(intent, "Open with ..."), PICKFILE_REQUEST_CODE);
//                startActivityForResult(Intent.createChooser(intent, "Select file"), PICKFILE_REQUEST_CODE);
*/
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + "/Documents/");
                Uri uri1 = Uri.parse(Environment.getDownloadCacheDirectory().getPath().toString() + "/Documents/");
                Log.v(TAG, "Uri :- " + uri);
                Log.v(TAG, "Uri 1 :- " + uri1);

                Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                chooser.addCategory(Intent.CATEGORY_OPENABLE);
                chooser.setDataAndType(uri, "application/pdf");
//                chooser.addCategory(Intent.CATEGORY_DEFAULT);
//                chooser.setDataAndType(uri, "resource/folder");
//                chooser.setDataAndType(uri, "text/csv");
                startActivityForResult(Intent.createChooser(chooser, "Select File"), PICKFILE_REQUEST_CODE);

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
                blockpopup(menuItem22);
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

        mRevealView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        coordinateLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mRevealView.getVisibility() == View.VISIBLE) {
                    mRevealView.setVisibility(View.INVISIBLE);
                    hidden = true;
                }
                return true;
            }
        });

        relative_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mRevealView.getVisibility() == View.VISIBLE) {
                    mRevealView.setVisibility(View.INVISIBLE);
                    hidden = true;
                }
                return true;
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
        sendButton.setOnClickListener(this);
        backLinearLayout.setOnClickListener(this);
    }

    private void sendIsTypingInPrivateChat() {

        Log.v(TAG, "inside typing status and");
        if (privateChatDialog == null) {
            Log.v(TAG, "Please create private dialog first");
            return;
        }

        privateChatDialog.addIsTypingListener(privateChatDialogTypingListener);

        try {
            privateChatDialog.sendIsTypingNotification();
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.v(TAG, "send typing error: " + e.getClass().getSimpleName());
        }

//        qbService.sendIsTypingInPrivateChat(privateChatDialog, privateChatDialogTypingListener);
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

        }
        */
        View menuItemView = findViewById(R.id.overflow);
        final PopupMenu popup = new PopupMenu(this, menuItemView);
        menuItem22 = popup.getMenu().findItem(R.id.block);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {

            supportFinishAfterTransition();
            if (groupName.equalsIgnoreCase("")) {
                Intent intent = new Intent(ChatActivity.this, TwoTab_Activity.class);
                intent.setAction("");
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra(NEWGROUPNAMEBACK, groupName);
                if (privateChatDialog != null) {
                    intent.putExtra(EXTRA_DIALOG_ID, privateChatDialog);
                }
                setResult(RESULT_OK, intent);
            }
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

            menuItem22 = popup.getMenu().findItem(R.id.block);

            BlockUserDataBaseHelper blockUserDataBaseHelper = new BlockUserDataBaseHelper(ChatActivity.this);
            ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();
            ArrayList<Integer> allBlockedUsersNew = blockUserDataBaseHelper.getAllBlockedUsers();
            Log.d(TAG, " all users list how got bloced " + allBlockedUsersNew.toString());
            menuItem22.setTitle("Block");
            for (int i = 0; i < allBlockedUsersNew.size(); i++) {
                Log.d(TAG, " Loop lo s " + allBlockedUsersNew.get(i) + " : " + QB_Friend_Id);

                if (allBlockedUsersNew.get(i).toString().equalsIgnoreCase(String.valueOf(QB_Friend_Id))) {
                    menuItem22.setTitle("UnBlock");
                } else {
                    menuItem22.setTitle("Block");
                }
            }

            if (groupId != null && groupName != null) {
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
                                intent.putExtra("dialogeIdSend", privateChatDialog.getDialogId());
                                intent.putExtra("reciverlanguages", privateChatDialog.getDialogId());
                                startActivity(intent);
                            }

                            break;

                        case R.id.chathistory:
//                            Intent intent1 = new Intent(ChatActivity.this, SocialContactActivity.class);
//                            startActivity(intent1);

                            chathistoryDialog();
                            break;
                        case R.id.mediahistory:
//                            Intent intent2 = new Intent(ChatActivity.this, CreateGroupChatActivity.class);
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
                                intent.putExtra("dialogeIdSend", privateChatDialog.getDialogId());
                                intent.putExtra("reciverlanguages", reciverlanguages);
                                startActivity(intent);
                            }
                            break;
                        case R.id.block:
//                            Intent intent3 = new Intent(ChatActivity.this, CreateGroupChatActivity.class);
//                            startActivity(intent2);
//                            blockpopup();

                            if (item.getTitle().toString().equalsIgnoreCase("Block")) {
                                Log.d(TAG, " menu item text get IF " + item.getTitle());
                                blockpopup(menuItem22);

                            } else if (item.getTitle().toString().equalsIgnoreCase("UnBlock")) {
                                Log.d(TAG, " menu item text get ELSE" + item.getTitle());
                                unblockpopup();
                                menuItem22.setTitle("Block");
                            }

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
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy-MM-dd", 5);
                        Toast.makeText(getApplicationContext(), "5 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd2.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy-MM-dd", 10);
                        Toast.makeText(getApplicationContext(), "10 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd3.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy-MM-dd", 100);
                        Toast.makeText(getApplicationContext(), "100 day chat is selected", Toast.LENGTH_LONG).show();
                    }

                    if (groupName.equalsIgnoreCase("")) {
                        dateWiseChatList = DatabaseHelper.getInstance(ChatActivity.this).getDateWiseChat("chat", FriendMobile, date);

                        for (int i = 0; i < dateWiseChatList.size(); i++) {
                            Log.d("ChatList>>>", dateWiseChatList.get(i).toString());
                            Log.d(TAG, "Get Chat history :- " + dateWiseChatList.get(i).toString());

                            String Sender = dateWiseChatList.get(i).sender;
                            String Sendername = dateWiseChatList.get(i).senderName;
                            String Reciver = dateWiseChatList.get(i).receiver;
                            String Recivername = dateWiseChatList.get(i).reciverName;
                            String Time = dateWiseChatList.get(i).Time;
                            String Date = dateWiseChatList.get(i).Date;
                            String Message = dateWiseChatList.get(i).body;
                            Log.v(TAG, "Sender :- " + Sender);
                            Log.v(TAG, "Sendername :- " + Sendername);
                            Log.v(TAG, "Reciver :- " + Reciver);
                            Log.v(TAG, "Recivername :- " + Recivername);
                            Log.v(TAG, "DateFromGet :- " + Date);

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
                            Log.d(TAG, "ChatListGroup :- " + dateWiseChatList.get(i).toString());

                            String Sender = dateWiseChatList.get(i).sender;
                            String Sendername = dateWiseChatList.get(i).senderName;
                            String Reciver = dateWiseChatList.get(i).receiver;
                            String Recivername = dateWiseChatList.get(i).reciverName;
                            String Time = dateWiseChatList.get(i).Time;
                            String Date = dateWiseChatList.get(i).Date;
                            String Message = dateWiseChatList.get(i).body;

                            Log.v(TAG, "~~~~~~~ Group CHAT ~~~~~~~~\nSender Name :- " + Sendername + "\n Message :- " + Message + "\nDate & Time :- " + Date + " at " + Time);

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
                        Random r = new Random();

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{AppPreferences.getEmail(ChatActivity.this)});

                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                "Speakame chat");
//                        emailIntent.putExtra(Intent.EXTRA_TEXT,
//                                "Chat history is attached as /' Speakame Chat with "+  +"/'with ");

                        file = getChatHistFile(Finaldata.toString());
                        Log.d("DBFILE", file.toString());
                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(emailIntent, "Export database"));
                        // DrawerDialog();
                    }
                } else if (img_on1.getVisibility() == View.GONE && img_on2.getVisibility() == View.VISIBLE) {
                    String date = "";
                    if (rd1.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy-MM-dd", 5);
                        Log.v(TAG, "Date :-" + date);
                        Toast.makeText(getApplicationContext(), "5 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd2.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy-MM-dd", 10);
                        Log.v(TAG, "Date :-" + date);
                        Toast.makeText(getApplicationContext(), "10 day chat is selected", Toast.LENGTH_LONG).show();
                    } else if (rd3.isChecked()) {
                        date = CommonMethods.getCalculatedDate(CommonMethods.getCurrentDate(), "yyyy-MM-dd", 100);
                        Log.v(TAG, "Date :-" + date);
                        Toast.makeText(getApplicationContext(), "100 day chat is selected", Toast.LENGTH_LONG).show();
                    }

                    if (groupName.equalsIgnoreCase("")) {
//                        DatabaseHelper.getInstance(ChatActivity.this).ChatDelete_ByDate("chat", FriendMobile, date);
                        Toast.makeText(getApplicationContext(), "Deleted chat", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "dialog id-- " + privateChatDialog.getDialogId(), Toast.LENGTH_LONG).show();
/*if(chatlist.contains(date)){
chatlist.remove();
}*/

                        String chatDaloge = privateChatDialog.getDialogId();
                        DatabaseHelper.getInstance(ChatActivity.this).ChatDelete_ByDate("chat", FriendMobile, date);
                        Toast.makeText(getApplicationContext(), "Deleted chat", Toast.LENGTH_LONG).show();

                        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(ChatActivity.this), user2, FriendName,
                                groupName, message, "" + random.nextInt(1000), "", true);
                        chatMessage.reciverName = FriendName;
                        chatMessage.userStatus = FriendStatus;
                        chatMessage.receiver = FriendMobile;
                        chatMessage.groupName = groupName;
                        chatMessage.groupid = groupId;
                        chatMessage.reciverlanguages = reciverlanguages;
                        chatMessage.ReciverFriendImage = FriendImage;
                        chatMessage.Groupimage = "";
                        chatMessage.friend_QB_Id = QB_Friend_Id;
                        chatMessage.isRead = 3;
                        chatMessage.Date = CommonMethods.getCurrentDateNewFormat();
                        //   chatMessage.Date = "15-01-2018";
                        chatMessage.dialog_id = chatDaloge;
                        chatMessage.qbChatDialogBytes = SerializationUtils.serialize(privateChatDialog);
                        Log.d(TAG, " latest chatMessage object for save in DB : - " + chatMessage.toString());

                        DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
                        callAdapter();

                        chatAdapter.notifyDataSetChanged();
                        chatAdapter.notifyItemRangeChanged(0, chatlist.size());
                        mRecyclerView.invalidate();
                    } else {
                        DatabaseHelper.getInstance(ChatActivity.this).ChatDelete_ByDate("group", groupName, date);
                        chatAdapter.notifyDataSetChanged();
                        callAdapter();
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

        File cacheDir = new File(cacheDirectory.getPath() + "/SpeakAme");

        if (!cacheDir.exists())
            cacheDir.mkdirs();
        Log.e("dir", cacheDir.toString());

        FileOutputStream fos;
        try {
            String a = cacheDir + "/SpeakAmeChatHistory" + ".txt";
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
        String sorcountrycode = country.getCode(ChatActivity.this, sorcountry.trim());
        if (sorcountrycode.equalsIgnoreCase("")) {
            sorcountrycode = "en";
        }
        String descountrycode = country.getCode(ChatActivity.this, descountry.trim());
        if (descountrycode.equalsIgnoreCase("")) {
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

        TextTranslater.getInstance().translate(ChatActivity.this, sorcountrycode, descountrycode, message, new VolleyCallback() {
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
                String message = msg_edittext.getEditableText().toString();
                if (!message.equalsIgnoreCase("")) {

                    if (groupName.equalsIgnoreCase("")) {
                        sendTextMessage(message, "", "", 0, "0", "", null);
                    } else {
                        sendGroupMessage(message, "", "", 0, "", "", null);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.sendMessageButton:
            case R.id.fab_sendMessageButton:

                sendStopTypingInPrivateChat();

                Log.v(TAG, "Recipient Id :- " + QB_Friend_Id);
                String message = msg_edittext.getEditableText().toString();

                if (!message.equalsIgnoreCase("")) {

                    if (Function.isConnectingToInternet(ChatActivity.this)) {

                        Log.v(TAG, "Recipient Id :- " + QB_Friend_Id);

                        if (groupName.equalsIgnoreCase("")) {

                            sendTextMessage(message, "", "", 0, "0", "", null);

                        } else {
                            // mLayoutManager.scrollToPosition(chatlist.size());
                            sendGroupMessage(message, "", "", 0, "", "", null);
                        }

//                        sendChatMessage(message, QB_Friend_Id);
//                        sendMessageToQuickBlox(message, "", "", 0);

                    } else {
                        Toast.makeText(ChatActivity.this, "Internet not Connected.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(ChatActivity.this, "Please enter message", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.bubble_layout_parent:

                int idx = mRecyclerView.getChildPosition(v);

                ChatMessage data = chatAdapter.getItem(idx);
                View innerContainer = v.findViewById(R.id.bubble_layout);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    innerContainer.setTransitionName("innerContainer" + "_" + data.msgid);
                }

                if (actionMode != null) {
                    myToggleSelection(idx);
                    return;
                }

                Intent startIntent = new Intent(this, ChatActivity.class);
                startIntent.setAction("");
                startIntent.putExtra("value", allBeans);
                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions
                            .makeSceneTransitionAnimation(this, innerContainer, "innerContainer");
                }
                this.startActivity(startIntent, options.toBundle());
                break;

            case R.id.backLinearLayout:

                supportFinishAfterTransition();
                if (groupName.equalsIgnoreCase("")) {
                    Intent intent = new Intent(ChatActivity.this, TwoTab_Activity.class);
                    intent.setAction("");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("");
                    intent.putExtra(NEWGROUPNAMEBACK, groupName);
                    if (privateChatDialog != null) {
                        intent.putExtra(EXTRA_DIALOG_ID, privateChatDialog);
                    }
                    setResult(RESULT_OK, intent);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        sendDialogId();
        sendStopTypingInPrivateChat();

        supportFinishAfterTransition();
        if (groupName.equalsIgnoreCase("")) {

            Intent intent = new Intent(ChatActivity.this, TwoTab_Activity.class);
            intent.setAction("");
            startActivity(intent);
        } else {

            Intent intent = new Intent();
            intent.putExtra(NEWGROUPNAMEBACK, groupName);
            if (privateChatDialog != null) {

                intent.putExtra(EXTRA_DIALOG_ID, privateChatDialog);

            }
            setResult(RESULT_OK, intent);
            finish();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult" + requestCode + "::" + resultCode);

        File qbFile = null;


        if (resultCode == RESULT_OK) {

            if (requestCode == ADDCONTACT) {

                chatAdapter.onActivityResult(requestCode, resultCode, data);

            } else if (requestCode == SENDIMAGE) {

                message = data.getStringExtra("msg");
                String filepath = data.getStringExtra("file");
                Log.d(TAG, "file>>" + filepath);
                filePath = filepath;
                File file1 = new File(filepath);
                qbFile = file1;
                fileName = file1.getName();
                Log.d(TAG, "file>1>" + fileName);
                filepath = filepath.replace(" ", "");

                // file=filePath;

                //  file = filePath;
                //file = Function.encodeFileToBase64Binary(filepath);
//                sendImageToQB(fileName);
//                bkjbjbjb

            } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {

                Uri selectedImageUri = data.getData();
                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                //selectedImagePath = selectedImagePath.replace(" ","");
                if (selectedImagePath != null) {
                    message = "";
                    filePath = selectedImagePath;
                    File file1 = new File(selectedImagePath);
                    qbFile = file1;
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
                            Log.i(TAG, "The phone number is " + contactNumber + contactName + " picUrl" + picUrl);

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
                            File SpeakAmeDirectory = Function.createFolder("SpeakAme Contact");

                            // Temporary file to store the contact files
                            File tmpFile = new File(SpeakAmeDirectory.getPath() + "/contact.png");

                            qbFile = tmpFile;

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
                            Log.d(TAG, "contactImage" + tmpFile.getPath());
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
                            File tmpFile = new File(cacheDirectory.getPath() + "/SpeakAme" + new Random().nextInt(100) + "img.png");

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

                        } else {

                            Toast.makeText(ChatActivity.this, "Contact number is not available!", Toast.LENGTH_LONG).show();
                            return;

                        }
                    }
                    //  super.onActivityResult(requestCode, resultCode, data);
                }
            } else if (requestCode == PICKFILE_REQUEST_CODE) {

                Uri selectedImageUri = data.getData();
                Log.v(TAG, "Document sending path URI :- " + selectedImageUri.toString());
                // MEDIA GALLERY
                //String selectedImagePath = getPathFile(ChatActivity.this, selectedImageUri);


                String selectedImagePath = GetFilePath.getPath(ChatActivity.this, selectedImageUri);
                //selectedImagePath = selectedImagePath.replace(" ","");
                Log.v(TAG, "Document sending path in ofpj :- " + selectedImagePath);

                if (selectedImagePath != null) {
                    File file1 = new File(selectedImagePath);
                    qbFile = file1;
                    message = file1.getName();
//                    message = "";
                    filePath = selectedImagePath;
                    fileName = file1.getName();
                    // file = selectedImagePath;
                    //file = Function.encodeFileToBase64Binary(selectedImagePath);
                }
            }/*else if (requestCode == GALLARY_CAPTURE){
//                sssssssssssssssss
                Log.d(TAG, " requestCode latest : --- " + GALLARY_CAPTURE);
                       Uri picUri = data.getData();
                //  doCrop();
                try {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    File file1 = new File(filePath);
                    qbFile = file1;
                    fileName = file1.getName();

                 //   imageView.setImageDrawable(Drawable.createFromPath(filePath));
                    cursor.close();
                } catch (Exception e) {
                }

            }*/
            sharingFilesFromMethod(qbFile);
        }
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

    private void sendBlockstatus(final MenuItem menuItem22) {

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ BLock user from QuickBlox if response status is 200 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  */

        final AlertDialog mProgressDialog = new SpotsDialog(ChatActivity.this);
        mProgressDialog.setTitle("Please wait a moment...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

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

                mProgressDialog.dismiss();
                Log.d(TAG, "response>>>>>" + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            blockUsersCode();
                            menuItem22.setTitle("UnBlock");
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setBlockList(ChatActivity.this, topObject.getString("block_users"));
                            }

                            QBPrivacyList list = new QBPrivacyList();
                            list.setName("Speakame");

                            ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

                            QBPrivacyListItem item1 = new QBPrivacyListItem();
                            item1.setAllow(false);
                            item1.setType(QBPrivacyListItem.Type.USER_ID);
                            item1.setValueForType(String.valueOf(QB_Friend_Id));
                            item1.setMutualBlock(true);

                            items.add(item1);

                            list.setItems(items);

                            try {
                                privacyListsManager.setPrivacyList(list);
                                privacyListsManager.setPrivacyListAsDefault("public");
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                                Log.e(TAG, "SmackException.NotConnectedException while setting privacy list :- " + e.getMessage());
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                                Log.e(TAG, "XMPPException.XMPPErrorException while setting privacy list :- " + e.getMessage());
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                                Log.e(TAG, "SmackException.NoResponseException while setting privacy list :- " + e.getMessage());
                            }

                            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ BLock user from QuickBlox if response status is 200 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  */

                            Toast.makeText(getApplicationContext(), "Block successfully", Toast.LENGTH_LONG).show();
                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mProgressDialog.dismiss();
                    }
                }
            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    private void sendUnBlockstatus() {

        final AlertDialog mProgressDialog = new SpotsDialog(ChatActivity.this);
        mProgressDialog.setTitle("Please wait a moment...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

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
                mProgressDialog.dismiss();
                Log.d(TAG, "response>>>>>" + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        unBlockFromQuickBlox();

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
                        mProgressDialog.dismiss();
                    }
                }
            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    private void unBlockFromQuickBlox() {


        QBPrivacyList list = new QBPrivacyList();
        list.setName("public");
        QBPrivacyListsManager privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();

        ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

        ArrayList<Integer> allBlockedUsersNew = blockUserDataBaseHelper.getAllBlockedUsers();
        for (int i = 0; i < allBlockedUsersNew.size(); i++) {
            Log.d(TAG, " Loop lo s " + allBlockedUsersNew.get(i) + " : " + QB_Friend_Id);

            if (allBlockedUsersNew.get(i).toString().equalsIgnoreCase(String.valueOf(QB_Friend_Id))) {
                blockUserDataBaseHelper.deleteBlockedUserById(allBlockedUsersNew.get(i));
                isUserBlocked = blockUserDataBaseHelper.getBlockedUser(QB_Friend_Id);
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
//sendUnBlockstatus();

            } else {
                Log.d(TAG, " imapondl else conditons " + " jlfsdjkl ");

            }

        }


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

       /*
       XmppConneceted activity = new XmppConneceted();
       MyXMPP.Composing composing = MyXMPP.Composing.gone;
       activity.getmService().xmpp.sendIsComposing(composing,user2);
       */

//        stopService(new Intent(ChatActivity.this, LastSeenService.class));
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

    private void myToggleSelection(int idx) {
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

    public void blockpopup(final MenuItem menuItem22) {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Block " + FriendName + "? Blocked contacts will no longer be able to call you or send you messages")
                .setCancelText("Cancel")
                .setConfirmText("Block")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

//                        XmppConneceted activity = new XmppConneceted();
//                        activity.getmService().xmpp.blockedUser(user2);
                        sendBlockstatus(menuItem22);
                        sDialog.cancel();
//dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

                        QBPrivacyListsManager qbPrivacyListsManager = chatService.getPrivacyListsManager();


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
//                        XmppConneceted activity = new XmppConneceted();
//                        activity.getmService().xmpp.unBlockedUser(user2);
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
/*
    private void sendChatMessage(String text, QBAttachment attachment, final String file, final String fileName) {

        QBChatMessage chatMessage = new QBChatMessage();
        *//*if (attachment != null) {
            chatMessage.addAttachment(attachment);
        } else {
        }*//*
        chatMessage.setBody(text);
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setMarkable(true);

        if (!QBDialogType.PRIVATE.equals(privateChatDialog.getType()) && !privateChatDialog.isJoined()) {
            Toast.makeText(this, "You're still joining a group chat, please wait a bit", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            privateChatDialog.sendMessage(chatMessage);
            if (QBDialogType.PRIVATE.equals(privateChatDialog.getType())) {
//                showMessage(chatMessage);
                sendTextMessage(text, file, fileName);
            }

            if (attachment != null) {
//                attachmentPreviewAdapter.remove(attachment);
            } else {
                msg_edittext.setText("");
            }

        } catch (SmackException.NotConnectedException e) {
            Log.w(TAG, e);
            Toast.makeText(this, "Can't send a message, You are not connected to chat", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendMessageToQuickBlox(final String message, final String file, final String fileName, Integer friend_id) {


        final HashMap<String, QBChatDialog> opponentsDialogMap = new HashMap<>();

        Log.v(TAG, "Inside send message method ");
        Log.v(TAG, "QB_Friend_Id :- " + friend_id);
        Log.v(TAG, "QB_Friend_Id 1 :- " + QB_Friend_Id);

        QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();

        final QBMessageListener<QBPrivateChat> privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
            @Override
            public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
                Log.v(TAG, "Inside process message 1");
            }

            @Override
            public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {

                Log.v(TAG, "Inside process message 2");
                Log.v(TAG, "originMessage :- "+ originMessage.getBody());
                Log.v(TAG, "Error :-  " + error.getMessage());
                Log.e(TAG, "Error :-  " + error.getMessage());
            }
        };

        QBPrivateChatManagerListener privateChatManagerListener = new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(final QBPrivateChat privateChat, final boolean createdLocally) {
                if (!createdLocally) {
                    privateChat.addMessageListener(privateChatMessageListener);
                }
            }
        };
        privateChatManager.addPrivateChatManagerListener(privateChatManagerListener);

        Integer opponentId = QB_Friend_Id;
        Log.v(TAG, "QB_Friend_Id 1 :- " + QB_Friend_Id);
//        try {
        final QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(message);
        chatMessage.setProperty("save_to_history", "1"); // Save a message to history

        QBPrivateChat privateChat = privateChatManager.getChat(QB_Friend_Id);
        if (privateChat == null) {
            privateChat = privateChatManager.createChat(QB_Friend_Id, privateChatMessageListener);
        }

        privateChat.sendMessage(chatMessage, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                sendTextMessage(message, file, fileName);

                Log.v(TAG, "Message body Send :- " + chatMessage.getBody());
                msg_edittext.setText("");
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ChatActivity.this, "Message not sent", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Error in sending message :-" + e.getMessage());
            }
        });
       *//* } catch (Exception e) {
            Log.e(TAG, "Error in sending message 3 :- " + e.getMessage());
        }*//*



            incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogId, QBChatMessage qbChatMessage, Integer senderId) {

                Log.v(TAG, "Inside message incoming listener");

                Log.v(TAG, "Message body Receive :- " + qbChatMessage.getBody());
                Log.v(TAG, "2. :-" + qbChatMessage.getDialogId());
                Log.v(TAG, "3. :-" + qbChatMessage.getRecipientId());
                Log.v(TAG, "4. :-" + qbChatMessage.getSenderId());
                Log.v(TAG, "5. :-" + qbChatMessage.getSmackMessage());

                if (!opponentsDialogMap.containsKey(dialogId)) {
                    QBChatDialog opponentDialog = new QBChatDialog();
                    ArrayList<Integer> occupantIds = new ArrayList<>();
                    occupantIds.add(qbChatMessage.getSenderId());
                    opponentDialog.setOccupantsIds(occupantIds);

                    //init Dialog for chatting
                    opponentDialog.initForChat(dialogId, QBDialogType.PRIVATE, chatService);

                    //add message listener on this Dialog
//                    opponentDialog.addMessageListener(opponentDialogMsgListener);

                    //put Dialog in cache
                    opponentsDialogMap.put(dialogId, opponentDialog);

//                    ChatMessage chatMessage = qbChatMessage.getBody().toString();

//                    DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
//                    DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", chatMessage.receiver);
//                    chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
//                    mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

                }
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer senderId) {
                Log.e(TAG, "Error in getting message 2:- " + e.getMessage());
            }
        });

    }

    private ArrayList<QBUser> getUserDetailsByPhoneNumber(String mobileWithCountryCode) {

        final ArrayList<QBUser> selectedUsers = new ArrayList<>();
        QBRoster roster = QBChatService.getInstance().getRoster();

        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(10);

        ArrayList<String> phones = new ArrayList<String>();
        phones.add(mobileWithCountryCode);

        QBUsers.getUsersByPhoneNumbers(phones, pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
//                mDialog.dismiss();

//                userModelArrayList = new ArrayList<UserModel>();
                for (QBUser user : qbUsers) {

                    Log.v(TAG, "User id :- " + user.getId().toString());
                    Log.v(TAG, "User namne :- " + user.getFullName());
                    Log.v(TAG, "User number:- " + user.getPhone());
                    Log.v(TAG, "User Login Id:- " + user.getLogin());

                    QB_Friend_Id = user.getId();
                    QB_Name = user.getFullName().toString();
                    QB_Mobile = user.getPhone().toString();
                    QB_LoginId = user.getLogin().toString();
                    selectedUsers.add(user);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "Error in getting contact :- " + e.getMessage());
            }
        });
        return selectedUsers;
    }

    private Integer getUserIdByPhoneNumber(String mobileWithCountryCode) {

        Log.v(TAG, "Inside getting user details by phone no ");
        final Integer selectedUsers = 0;
        QBRoster roster = QBChatService.getInstance().getRoster();

        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(10);

        ArrayList<String> phones = new ArrayList<String>();
        phones.add(mobileWithCountryCode);

        QBUsers.getUsersByPhoneNumbers(phones, pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
//                mDialog.dismiss();
//                userModelArrayList = new ArrayList<UserModel>();

                for (QBUser user : qbUsers) {

                    Log.v(TAG, "User id :- " + user.getId().toString());
                    Log.v(TAG, "User namne :- " + user.getFullName());
                    Log.v(TAG, "User number:- " + user.getPhone());
                    Log.v(TAG, "User Login Id:- " + user.getLogin());

                    QB_Friend_Id = user.getId();
                    QB_Name = user.getFullName().toString();
                    QB_Mobile = user.getPhone().toString();
                    QB_LoginId = user.getLogin().toString();
                }
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error occured");
                Log.v(TAG, "Error getting contact details :- " + e.getMessage());

            }
        });
        return QB_Friend_Id;
    }



    private void initChatDialogs() {
        try {
            QBChatService.getInstance().enableCarbons();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }*/

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

    @Override
    protected void onResume() {
        super.onResume();
        chatAdapter.notifyDataSetChanged();
//        startServiceForStatus();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof LinearLayout) {

            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {

                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
            }
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        float touchPointX = event.getX();
        float touchPointY = event.getY();
        int[] coordinates = new int[2];
        mRevealView.getLocationOnScreen(coordinates);
        if (touchPointX < coordinates[0] || touchPointX > coordinates[0] + mRevealView.getWidth() || touchPointY < coordinates[1] || touchPointY > coordinates[1] + mRevealView.getHeight()) {
            mRevealView.setVisibility(View.INVISIBLE);
            hidden = true;
        }
        return false;
    }

    private void initIsTypingListener() {
//
//        // Create 'is typing' listener
//        //

        privateChatDialogTypingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer senderId) {

                Log.v(TAG, "user " + senderId + " is typing. Private dialog id: " + dialogId);
                status.setVisibility(View.VISIBLE);
/*
                boolean isUserBlocked = blockUserDataBaseHelper.getBlockedUser(senderId);
                Log.v(TAG, "user blocked :- " + isUserBlocked);

                if (isUserBlocked) {
                    status.setText("");

                } else {
                }*/
                if (isUserBlocked) {
                    status.setText("");
                } else {
                    status.setText("is typing...");
                }

            }

            @Override
            public void processUserStopTyping(String dialogId, Integer senderId) {
                Log.v(TAG, "user " + senderId + " stop typing. Private dialog id: " + dialogId);
                if (isUserBlocked) {
                    status.setText("");
                } else {
                    status.setText(lastSeen);
                }
            }
        };

        groupChatDialogTypingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer senderId) {
                Log.v(TAG, "user " + senderId + " is typing. Group dialog id: " + dialogId);
            }

            @Override
            public void processUserStopTyping(String dialogId, Integer senderId) {
                Log.v(TAG, "user " + senderId + " stop typing. Group dialog id: " + dialogId);
            }
        };

//        privateChatDialog.addIsTypingListener(privateChatDialogTypingListener);
//        groupChatDialog.addIsTypingListener(groupChatDialogTypingListener);

    }

    private void initPrivateChatMessageListener() {
        // Create 1-1 chat is message listener
        //
        privateChatDialogMessageListener = new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                Log.v(TAG, "received message: " + qbChatMessage.getId());

                if (qbChatMessage.getSenderId().equals(chatService.getUser().getId())) {
                    Log.v(TAG, "Message comes here from carbons");
                }
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.v(TAG, "processError: " + e.getLocalizedMessage());
            }
        };


        groupChatDialogMessageListener = new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        };
    }

    private void onPresenceChanged() {

      /*  lastSeen = DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(QB_Friend_Id);
        Log.v(TAG, "QB presence status Last seen :- " + lastseen);
        status.setText(lastSeen);
*/
        rosterListener = new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {

            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {

            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {

            }

            @Override
            public void presenceChanged(QBPresence presence) {

                Log.v(TAG, "presence :- QB presence status presence chages smvsdopm:- " + presence.getType());

                String statusOnline = presence.getType().toString();

                if (statusOnline.equalsIgnoreCase("online")) {

                    Log.v(TAG, "QB status online :- " + statusOnline);

                    User user = new User();
                    user.setFriend_id(QB_Friend_Id);
                    user.setStatus("Online");
                    DatabaseHelper.getInstance(ChatActivity.this).InsertStatus(user);
                    lastSeen = DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(user.getFriend_id());
                    Log.v(TAG, "QB status online from database 111111111 :- " + DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(user.getFriend_id()));
                    if (isUserBlocked) {
                        status.setText("");
                    } else {
                        status.setText(lastSeen);
                    }

                } else if (statusOnline.equalsIgnoreCase("offline")) {

                    Log.v(TAG, "QB status offline :- " + statusOnline);

                    User user = new User();
                    user.setFriend_id(QB_Friend_Id);

                    Log.v(TAG, "QB status offile time 1:- " + Function.getCurrentDateTime());
                    String currentTime = Function.getCurrentDateTime();
                    String time = "";
                    try {
                        time = Function.formatToYesterdayOrToday(currentTime);
                        Log.v(TAG, "QB status offile time 2 :- " + " last seen at " + time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.v(TAG, "QB Error status offile time :- " + e.getMessage());
                    }
                    user.setStatus("last seen at " + time);
                    DatabaseHelper.getInstance(ChatActivity.this).InsertStatus(user);

                    Log.v(TAG, "QB status online from database 2222222222222222 :- " + DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(user.getFriend_id()));

                    lastSeen = DatabaseHelper.getInstance(ChatActivity.this).getLastSeenQB(user.getFriend_id());
                    if (isUserBlocked) {
                        status.setText("");
                    } else {
                        if (!groupName.equalsIgnoreCase("")) {
                            status.setText("");
                        } else {
                            if (lastSeen.equalsIgnoreCase("offline") || lastSeen.contains("offline")) {
                                status.setText("");
                            } else {
                                status.setText(lastSeen);
                            }
                        }
                    }
                }
            }
        };
    }

    //dsgggggggggggggggggggggggggggggggg
    private void initMessageSentListener() {

        privateChatDialogMessageSentListener = new QBChatDialogMessageSentListener() {
            @Override
            public void processMessageSent(String dialogId, QBChatMessage qbChatMessage) {

                Log.v(TAG, " QB Sent CHat Message :- " + qbChatMessage);
                Log.v(TAG, "message " + qbChatMessage.getId() + " sent to dialog " + dialogId);

                String qbMessageId = qbChatMessage.getId();
                String qbDialogId = dialogId;
                String qbRecipientId = qbChatMessage.getRecipientId().toString();

                DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", FriendMobile);
                DatabaseHelper.getInstance(ChatActivity.this).UpdateReadStatus("1", qbDialogId, qbMessageId, qbRecipientId);
                chatAdapter.notifyDataSetChanged();

                callAdapter();
            }

            @Override
            public void processMessageFailed(String dialogId, QBChatMessage qbChatMessage) {
                Log.v(TAG, "send message " + qbChatMessage.getId() + " has failed to dialog " + dialogId);
//                DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("0", FriendMobile);


                String qbMessageId = qbChatMessage.getId();
                String qbDialogId = dialogId;
                String qbRecipientId = qbChatMessage.getRecipientId().toString();

                DatabaseHelper.getInstance(ChatActivity.this).UpdateReadStatus("0", qbDialogId, qbMessageId, qbRecipientId);
                chatAdapter.notifyDataSetChanged();

                callAdapter();
            }
        };

        groupChatDialogMessageSentListener = new QBChatDialogMessageSentListener() {
            @Override
            public void processMessageSent(String dialogId, QBChatMessage qbChatMessage) {
                Log.v(TAG, "message " + qbChatMessage.getId() + " sent to group dialog " + dialogId);
            }

            @Override
            public void processMessageFailed(String dialogId, QBChatMessage qbChatMessage) {
                Log.v(TAG, "send message " + qbChatMessage.getId() + " has failed to dialog " + dialogId);
            }
        };
    }

    private void initMessageStatusManagerAndListener() {
       /* try {
            QBChatService.getInstance().enableCarbons();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }*/
        messageStatusesManager = chatService.getMessageStatusesManager();

        messageStatusListener = new QBMessageStatusListener() {
            @Override
            public void processMessageDelivered(String messageId, String dialogId, Integer userId) {

//                DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("2", FriendMobile);

                String qbMessageId = messageId;
                String qbDialogId = dialogId;
                String qbRecipientId = userId.toString();

                DatabaseHelper.getInstance(ChatActivity.this).UpdateReadStatus("2", qbDialogId, qbMessageId, qbRecipientId);
                chatAdapter.notifyDataSetChanged();

                callAdapter();

// User user = new User();
// user.setFriend_id(QB_Friend_Id);
// user.setMessage_status(message_status);
                Log.v(TAG, "messageStatusesManager...............1..................messagedelivered");
                Log.v(TAG, " ~~~~~~~~~~~~ inside message deliver messageStatusesManager ~~~~~~~~~~~~");
                Log.v(TAG, "Message id for deliver message :- " + messageId);
                Log.v(TAG, "dialogId id for deliver message :- " + dialogId);
                Log.v(TAG, "User id :- " + userId);
                Log.v(TAG, "processMessageRead1" + messageId);
                Log.v(TAG, "processMessageRead1" + dialogId);
                Log.v(TAG, "processMessageRead1" + userId);
                Log.v("NorrisTestQB", "delivered messageid " + messageId + " dialogid " + dialogId + " userid " + userId);

            }

            @Override
            public void processMessageRead(String messageId, String dialogId, Integer userId) {

                Log.v(TAG, " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Inside Read Message Status ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                Log.v(TAG, "messageId processMessageRead2 :- " + messageId);
                Log.v(TAG, "dialogId processMessageRead2 :- " + dialogId);
                Log.v(TAG, "userId processMessageRead2 :- " + userId);
                Log.v(TAG, "read messageid " + messageId + " dialogid " + dialogId + " userid " + userId);

                String qbMessageId = messageId;
                String qbDialogId = dialogId;
                String qbRecipientId = userId.toString();

                DatabaseHelper.getInstance(ChatActivity.this).UpdateReadStatus("3", qbDialogId, qbMessageId, qbRecipientId);
//                chatAdapter.notifyDataSetChanged();
                callAdapter();

                if (chatlist != null && !chatlist.isEmpty()) {
                    if (chatlist.get(chatlist.size() - 1).readStatus.equalsIgnoreCase("3")) {
                        Log.d(TAG, " chatListFromLastIndexstautus : --- " + " " + " :: " + chatlist.get(chatlist.size() - 1).body);
                        DatabaseHelper.getInstance(ChatActivity.this).UpdateReadStatusForAllMessage("3", qbDialogId, "");
                        callAdapter();
//                    chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        if (messageStatusesManager != null) {
            messageStatusesManager.addMessageStatusListener(messageStatusListener);
        }
//dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
    }

    public void blockUsersCode() {
        Toast.makeText(getApplicationContext(), "Block clicked .  " + QB_Friend_Id, Toast.LENGTH_SHORT).show();
        BlockUserDataBaseHelper blockUserDataBaseHelper = new BlockUserDataBaseHelper(ChatActivity.this);
        String s = BlockUserDataBaseHelper.CREATE_TBL_BLOCK;
        Log.d(TAG, " FriendName : " + FriendName + " FriendMobile : " + FriendMobile + " FriendOcupantId : " + QB_Friend_Id + " Query for block : " + s);
        ArrayList<Integer> allBlockedUsers = blockUserDataBaseHelper.getAllBlockedUsers();
        Log.d(TAG, " GetAllBlokedUsers .. " + allBlockedUsers.toString());
        if (allBlockedUsers.contains(QB_Friend_Id)) {
            Log.d(TAG, " user all ready blocked");
        } else {
            blockUserDataBaseHelper.saveBlockedUsers(FriendName, FriendMobile, QB_Friend_Id);

        }
        QBPrivacyListsManager privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();
        privacyListsManager.addPrivacyListListener(privacyListListener);

//..........................................
        List<QBPrivacyList> lists = null;
        try {
            lists = privacyListsManager.getPrivacyLists();
            Log.d(TAG, " My new Log ..." + lists.toString());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d(TAG, " SmackException  method " + e.getMessage());

        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d(TAG, " XMPPException  method " + e.getMessage());

        } catch (SmackException.NoResponseException e) {
            Log.d(TAG, " NoResponseException  method " + e.getMessage());

            e.printStackTrace();
        }
//.............................................
        QBPrivacyList list = new QBPrivacyList();
        list.setName("public");

        ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

        ArrayList<Integer> allBlockedUsersNew = blockUserDataBaseHelper.getAllBlockedUsers();
        for (int i = 0; i < allBlockedUsersNew.size(); i++) {
            QBPrivacyListItem item1 = new QBPrivacyListItem();
            item1.setAllow(false);
            item1.setType(QBPrivacyListItem.Type.USER_ID);
            item1.setValueForType(String.valueOf(allBlockedUsersNew.get(i)));
            Log.d(TAG, " This is the new QBFriendId    " + allBlockedUsersNew.get(i));
            // item1.setMutualBlock(true);
            items.add(item1);
        }
        list.setItems(items);

        try {
            Log.d(TAG, "  This is the privecy list " + list.toString());
            privacyListsManager.setPrivacyList(list);
            privacyListsManager.setPrivacyListAsDefault("public");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {

    }

    @Override
    public void onDialogUpdated(String chatDialog) {

    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {
        Log.v(TAG, "Overriden() on onNewDialogLoaded Created :- " + chatDialog);

        byte[] ser_QBByteData = SerializationUtils.serialize(chatDialog);
        List<Integer> occupantsId = chatDialog.getOccupants();
        occupantsId.get(0);
        Log.v(TAG, "Overriden() Other user friend id :- " + occupantsId.get(1));
        Log.v(TAG, "Overriden()  Other user friend id 123 :- " + chatDialog.getUserId());

//        if (chatDialog.getOccupants())
        if (chatDialog.getType().getCode() == 3)
            DatabaseHelper.getInstance(ChatActivity.this).insertQbIdQbChatPrivateDialoge(chatDialog.getUserId(), chatDialog.getDialogId(), ser_QBByteData, "Private");

    }

    public void sharingFilesFromMethod(final File qbFile) {
        int fileSize = Function.getFileSize(filePath);
        Log.d(TAG, "fileSize" + groupName + ">>>" + fileSize);
        //if(fileSize < )
        //*************************************  For shwoing image first to the adapter and add to database then sending image to server  *********************************//
            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code updated on 09 January 2018 by Ravi ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        String MyImage = "";
        String MyStatus = "";
        if (AppPreferences.getPicprivacy(ChatActivity.this).equalsIgnoreCase(AppConstants.EVERYONE)) {
            MyImage = AppPreferences.getUserprofile(ChatActivity.this);
            MyStatus = AppPreferences.getUserstatus(ChatActivity.this);
        } else if (AppPreferences.getPicprivacy(ChatActivity.this).equalsIgnoreCase(AppConstants.MYFRIENDS)) {
            if (!Function.isStringInt(FriendName)) {
                MyImage = AppPreferences.getUserprofile(ChatActivity.this);
                MyStatus = AppPreferences.getUserstatus(ChatActivity.this);
            }
        }
//if(fileSize < ) {
        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(ChatActivity.this), user2, FriendName,
                groupName, message, "" + random.nextInt(1000), filePath, true);
        chatMessage.setMsgID();
        chatMessage.body = message;
        chatMessage.fileName = fileName;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();

        Log.v(TAG, "chatMessage.Date ..........12341435 :- " + chatMessage.Date);
        Log.v(TAG, "chatMessage.Time ..........12341435 :- " + chatMessage.Time);

        calander.getTimeInMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        String formattedDate = df.format(calander.getTime());
        Log.d(TAG, " formatteDateAndTime : ----" + formattedDate);
        String timeZone_date[] = formattedDate.split(" ");

        Log.v(TAG, "data[0]..........." + timeZone_date[0]);
        Log.v(TAG, "data[1]..........." + timeZone_date[1]);

        chatMessage.Date = timeZone_date[0];
        chatMessage.Time = timeZone_date[1] + " " + timeZone_date[2];

        Log.v(TAG, "chatMessage.Date .......... :- " + chatMessage.Date);
        Log.v(TAG, "chatMessage.Time .......... :- " + chatMessage.Time);

        chatMessage.type = Message.Type.chat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(ChatActivity.this));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(ChatActivity.this);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.MyImage = MyImage;
        chatMessage.userStatus = MyStatus;
        chatMessage.lastseen = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
        chatMessage.receiver_QB_Id = QB_Friend_Id;
        chatMessage.sender_QB_Id = AppPreferences.getQBUserId(ChatActivity.this);
        chatMessage.friend_QB_Id = QB_Friend_Id;
        chatMessage.readStatus = "0";
//            chatMessage.Time = "14-01-2018";

        final QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setBody(chatMessage.body);
        qbChatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        qbChatMessage.setDateSent(date.getTime());
        qbChatMessage.setMarkable(true);
        qbChatMessage.setSenderId(chatMessage.sender_QB_Id);

        chatMessage.qbMessageId = qbChatMessage.getId();
        chatMessage.dialog_id = privateChatDialog.getDialogId();
        chatMessage.qbChatDialogBytes = SerializationUtils.serialize(privateChatDialog);

        if (chatMessage.groupName.equalsIgnoreCase("")) {
            qbChatMessage.setRecipientId(chatMessage.receiver_QB_Id);
        }

        Log.d(TAG, " After Adding the QbChatDialog " + chatMessage.qbMessageId + " :....: " + chatMessage.dialog_id);
// onlineStatus = new DatabaseHelper(ChatActivity.this).getLastSeen(user2);
//chatMessage.fileData = fileData;
        msg_edittext.setText("");
        fm.setVisibility(View.GONE);
//TwoTab_Activity activity = new TwoTab_Activity();

        chatMessage.ReciverFriendImage = FriendImage;
        chatMessage.msgStatus = "0";
        //  chatMessage.Date = "13-01-2018";

        chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        Log.d(TAG, " chatMeaafeFile : -- " + chatMessage.toString());
        DatabaseHelper.getInstance(ChatActivity.this).insertChat(chatMessage);
        DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("1", chatMessage.receiver);
//vsddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd
/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ "end" Code updated on 09 January 2018 by Ravi ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        Log.d(TAG, " chatMeaafeFile new : -- " + chatMessage.toString());
        Log.d(TAG, " chatMeaafeFile qbFile : -- " + qbFile);

        Boolean fileIsPublic = false;

        if (chatAdapter.getItemViewType(chatAdapter.getItemCount() - 1) == 1) {
            chatAdapter.getItemViewType(chatAdapter.getItemCount() - 1);
            Toast.makeText(getApplicationContext(), "Image uploading progress bar", Toast.LENGTH_SHORT).show();
        }

        QBContent.uploadFileTask(qbFile, fileIsPublic, null, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int i) {

            }
        }).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
//dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

                Log.i(TAG, ">>> QBFile:" + qbFile.toString());
                Log.v(TAG, "public url:" + qbFile.getPublicUrl());
                Log.v(TAG, "private url:" + qbFile.getPrivateUrl());
                Log.v(TAG, "public url static:" + QBFile.getPublicUrlForUID(qbFile.getUid()));
                Log.v(TAG, "private url static:" + QBFile.getPrivateUrlForUID(qbFile.getUid()));
                Log.v(TAG, "QB file uploaded ID 1 :- " + qbFile.getId());
                Log.v(TAG, "QB file uploaded U_ID 2 :- " + qbFile.getUid());

                qbFileId = qbFile.getId();
                qbFileUid = qbFile.getUid();
                fileUrl = QBFile.getPrivateUrlForUID(qbFile.getUid());

                Log.v(TAG, "QB file upload ID 1 :- " + qbFileId);
                Log.v(TAG, "QB file upload U_ID 2 :- " + qbFileUid);
                Log.v(TAG, "QB file upload U_ID 3 :- " + fileUrl);

                if (groupName.equalsIgnoreCase("")) {

                    Log.v(TAG, "Inside sending private chat message:- " + fileUrl);
                    sendTextMessage(message, filePath, fileName, qbFileId, qbFileUid, fileUrl, qbChatMessage);

                } else {
                    Log.v(TAG, "Inside sending group chat message:- " + fileUrl);
                    sendGroupMessage(message, filePath, fileName, qbFileId, qbFileUid, fileUrl, qbChatMessage);
                }
            }

            @Override
            public void onError(QBResponseException e) {

                Log.e(TAG, "Error in uploading image :- " + e.getMessage());
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

//...................................................................................//

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
            myToggleSelection(idx);
            super.onLongPress(e);
        }
    }

//.....................................................................//

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

    public class ChatMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            Log.v(TAG, "Inside ChatMessageListener QBChatMessage :- " + qbChatMessage);
            Log.v(TAG, "Inside ChatMessageListener message :- " + s);
//            showMessage(qbChatMessage);
        }

        @Override
        public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
            super.processError(s, e, qbChatMessage, integer);

            Log.v(TAG, "Error while sending message to quick blox user :- " + e.getMessage());
            Log.v(TAG, "String after error occured :- " + s);
            Log.v(TAG, "Integer after error occured :- " + integer);
            Log.v(TAG, "QB chat message :- " + qbChatMessage);
        }
    }


}
