package com.speakame.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.gson.Gson;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.speakame.Adapter.BroadcastnewgroupAdapter;
import com.speakame.Adapter.GroupListAdapter;
import com.speakame.AppController;
import com.speakame.Beans.AllBeans;
import com.speakame.Beans.User;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.QuickBlox.ChatHelper;
import com.speakame.QuickBlox.DialogsManager;
import com.speakame.QuickBlox.QbChatDialogMessageListenerImp;
import com.speakame.QuickBlox.QbDialogHolder;
import com.speakame.QuickBlox.QbDialogUtils;
import com.speakame.QuickBlox.QbEntityCallbackImpl;
import com.speakame.R;
import com.speakame.Services.ContactImportService;
import com.speakame.Services.HomeService;
import com.speakame.Services.QBService;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.CallBackUi;
import com.speakame.utils.Contactloader.Contact;
import com.speakame.utils.Contactloader.ContactFetcher;
import com.speakame.utils.Contactloader.ContactPhone;
import com.speakame.utils.DownloadFile;
import com.speakame.utils.Function;
import com.speakame.utils.ListCountry;
import com.speakame.utils.TextTranslater;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.commons.lang3.SerializationUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.speakame.Activity.ChatActivity.chatAdapter;
import static com.speakame.Activity.ChatActivity.conversationimage;
import static com.speakame.Activity.ChatActivity.groupName;
import static com.speakame.utils.Function.mediaScanner;


public class TwoTab_Activity extends AnimRootActivity implements VolleyCallback, DialogsManager.ManagingDialogsCallbacks {

    public static final String EXTRA_DIALOG_ID = "dialogId";
    private static final String TAG = "TwoTab_Activity";
    private static final int REQUEST_SELECT_PEOPLE = 174;
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;
    public static BroadcastnewgroupAdapter adapter;
    public static TwoTab_Activity instance = null;
    public static CallBackUi callBackUi;
    public static QBChatService chatService;
    public static QBRoster сhatRoster;
    public static QBRosterListener rosterListener;
    public static QBSubscriptionListener subscriptionListener;
    public static TextView messageCountTextView, messageCountTextView_group;
    static ListView chatlist;
    static List<ChatMessage> chatMessageList;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

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
    RelativeLayout linearLayout;
    ActionBar actionBar;
    TapTargetSequence sequence;
    MenuItem contact, overflow;
    ProgressDialog dialog;
    DialogsManager dialogsManager;
    Gson gson;
    QBService qbService;
    QBChatDialog mQbChatDialog;
    QBChatMessage mQbChatMessage;
    QBChatDialog qbChatDialog;
    QBMessageStatusListener messageStatusListener;
    private QBIncomingMessagesManager incomingMessagesManager;
    private QBSystemMessagesManager systemMessagesManager;
    private SystemMessagesListener systemMessagesListener;
    private QBChatDialogMessageListener allDialogsMessagesListener;
    private QBChatDialogTypingListener privateChatDialogTypingListener, groupChatDialogTypingListener;
    private QBChatDialogMessageSentListener privateChatDialogMessageSentListener, groupChatDialogMessageSentListener;
    //
    ///////////////////////////////////////////// 1-1 Chat /////////////////////////////////////////////
    //
    private QBChatDialogMessageListener privateChatDialogMessageListener;
    private QBChatDialogMessageListener groupChatDialogMessageListener;
    private QBMessageStatusesManager messageStatusesManager;
    private Handler handler = new Handler();

    public static void updateList(String groupName) {
        chatMessageList = new ArrayList<ChatMessage>();
        callBackUi.update(groupName);
        if (groupName.equalsIgnoreCase("")) {
            chatMessageList = DatabaseHelper.getInstance(instance).getReciever();
            updateMessageCount(chatMessageList, "");

        } else {
            chatMessageList = DatabaseHelper.getInstance(instance).getGroup();
            updateMessageCount(chatMessageList, "for_group");
        }
        Log.v(TAG, "Data from Chatlist :- " + chatMessageList.toString());
        Log.v(TAG, "Data from Chatlist :- " + chatMessageList.toString());
        System.out.println("chatMessageList : " + chatMessageList.toString());

        adapter = new BroadcastnewgroupAdapter(instance, chatMessageList, messageCountTextView);
//        adapter.notifyDataSetChanged();
        chatlist.setAdapter(adapter);
    }


    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    private static void updateMessageCount(List<ChatMessage> chatMessageList, String groupName) {

        int privatecount = 0;
        int privatecount_group = 0;

        for (int i = 0; i < chatMessageList.size(); i++) {

            String count_again = DatabaseHelper.getInstance(instance).getmsgCount("chat", chatMessageList.get(i).receiver);

            if (groupName.equalsIgnoreCase("")) {
                if (!count_again.equalsIgnoreCase("")) {
                    privatecount = Integer.parseInt(count_again) + privatecount;
                }
            } else {
                if (!count_again.equalsIgnoreCase("")) {
                    privatecount_group = Integer.parseInt(count_again) + privatecount_group;
                }
            }
        }

        messageCountTextView.setText(String.valueOf(privatecount));
        messageCountTextView_group.setText(String.valueOf(privatecount_group));

        if (privatecount <= 0) {
            messageCountTextView.setVisibility(View.GONE);
        } else {
            messageCountTextView.setVisibility(View.VISIBLE);
        }

        if (privatecount_group <= 0) {
            messageCountTextView_group.setVisibility(View.GONE);
        } else {
            messageCountTextView_group.setVisibility(View.VISIBLE);
        }

        String countString = String.valueOf(privatecount);
        String countString_Grp = String.valueOf(privatecount_group);

//        Toast.makeText(instance.getApplicationContext(), " private count in updateList() :- " + privatecount, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_tab_);
        setAppWalkThrough();
        setToolbar();
        instance = this;
        checkMarshmallowPermission();

        startService(new Intent(TwoTab_Activity.this, HomeService.class));
        //  }
        initViews();

        initChatService();

        setListener();

        callBackUi = new CallBackUi() {
            @Override
            public void update(String s) {

                if (s.equalsIgnoreCase("")) {

                    firsttab.setTextColor(getResources().getColor(R.color.colorwhite));
                    secondtab.setTextColor(getResources().getColor(R.color.colorPrimary));
                    firsttab.setBackgroundResource(R.drawable.left_rounded_corner_bg_layout);
                    secondtab.setBackgroundResource(R.drawable.rounded_corner_bg_layout);
                } else {
                    firsttab.setTextColor(getResources().getColor(R.color.colorPrimary));
                    secondtab.setTextColor(getResources().getColor(R.color.colorwhite));
                    firsttab.setBackgroundResource(R.drawable.rounded_corner_bg_layout);
                    secondtab.setBackgroundResource(R.drawable.left_rounded_corner_bg_layout);
                }
            }
        };

        searchFriend();

        Log.v(TAG, "Mobile no :- " + AppPreferences.getMobileuser(TwoTab_Activity.this));

        allDialogsMessagesListener = new AllDialogsMessageListener();
        systemMessagesListener = new SystemMessagesListener();
        dialogsManager = new DialogsManager();

        Log.v(TAG, "user is logged in Quick BloX or not :- " + chatService.isLoggedIn());

        initRosterListener();
        initSubscriptionListener();

        if (chatService.isLoggedIn()) {
            Log.v(TAG, "USER IS LOGGED IN TO QUICKBLOX ANDROID :- " + chatService.isLoggedIn());
        } else {
            loginUserToQuickBlox(AppPreferences.getQB_LoginId(TwoTab_Activity.this), "12345678");
        }
        loginUserToQuickBlox(AppPreferences.getQB_LoginId(TwoTab_Activity.this), "12345678");
//        registerQbChatListeners();
//        createSessionForReg("+" + AppPreferences.getMobileuser(TwoTab_Activity.this), "12345678");
        registerQbChatListeners();
        updateChatAdapter("onResume 2");

        // Init 1-1 listeners
        initPrivateChatMessageListener();
        initIsTypingListener();
//        initRoster();
//        initMessageSentListener();
        startService(new Intent(getBaseContext(), ContactImportService.class));

        Log.d(TAG, " AppController.privateMessageCountravi  " + AppController.privateMessageCount);

//        getting_UserImage();

    }

    private void getting_UserImage() {

        for (int i = 0; i < chatMessageList.size(); i++) {

            int QbUserID = chatMessageList.get(i).friend_QB_Id;
            Log.v(TAG, "QBUSerId :19 dec : - " + QbUserID);

            checkUserPresence(QbUserID);

        }

    }

    private void getIntentVal() {

        if (getIntent() != null) {
            if (getIntent().getAction().equalsIgnoreCase("GroupMember")) {

                QBChatDialog privateChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);

                Log.v(TAG, "Inside Two Tab Activtiy :- " + privateChatDialog);
                Intent intent = new Intent(TwoTab_Activity.this, ChatActivity.class);
                intent.putExtra("value", "");
                intent.putExtra("groupName", "");
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, privateChatDialog);
//                startActivityForResult(intent, REQUEST_DIALOG_ID_FOR_UPDATE);
            }
        }
    }

    private void incomingMessage() {

        gson = new Gson();
        final HashMap<String, QBChatDialog> opponentsDialogMap = new HashMap<>();

        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {

                Log.v(TAG, "Inside  incomming message listener");


                Log.v(TAG, "Message body Receive :- " + qbChatMessage.getBody());
                Log.v(TAG, "2. Dialog id:-" + qbChatMessage.getDialogId());
                Log.v(TAG, "2.1 Message id . :-" + qbChatMessage.getId());
                Log.v(TAG, "3. :-" + qbChatMessage.getRecipientId());
                Log.v(TAG, "4. :-" + qbChatMessage.getSenderId());
                Log.v(TAG, "5. :-" + qbChatMessage.getSmackMessage());

                final String mDialogId = qbChatMessage.getDialogId();
                final String mMessageId = qbChatMessage.getId();

                final ChatMessage chatMessage = gson.fromJson(qbChatMessage.getBody(), ChatMessage.class);

                Log.v(TAG, "Dialog in bytes from receiving message after cinversion :- " + chatMessage.qbChatDialogBytes);

                if (chatMessage.groupName.equalsIgnoreCase("")) {

                    Log.v(TAG, "for private message listener");
                    mQbChatMessage = qbChatMessage;
//                  asccccccccccccccccccccccccccccccccccccccc
                    Log.v(TAG, "mQbChatMessage is not null 1:- " + mQbChatMessage);

                    final String sender_lang = chatMessage.senderlanguages;
                    final String my_language = AppPreferences.getUSERLANGUAGE(TwoTab_Activity.this);
                    ListCountry country = new ListCountry();
                    String sorcountrycode = country.getCode(TwoTab_Activity.this, sender_lang.trim());
                    if (sorcountrycode.equalsIgnoreCase("")) {
                        sorcountrycode = "en";
                    }
                    String descountrycode = country.getCode(TwoTab_Activity.this, my_language.trim());
                    if (descountrycode.equalsIgnoreCase("")) {
                        descountrycode = "en";
                    }

                    if (sender_lang.equalsIgnoreCase("no translate")) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Log.d("Languagesss", "inside  no traslate !!!!!!");
//                                    processMessages(chatMessage);
                                processMessages(chatMessage, qbChatMessage.getId(), qbChatMessage.getDialogId(), qbChatMessage);
                            }
                        });

                    } else {
                        try {

                            Log.d(TAG, "Inside  Traslate else !!!!!!");
                            TextTranslater.getInstance().translate(TwoTab_Activity.this, sorcountrycode, descountrycode, chatMessage.body, new VolleyCallback() {
                                @Override
                                public void backResponse(String response) {
                                    if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(TwoTab_Activity.this).equalsIgnoreCase("1")) {
                                        // if (!langu.equalsIgnoreCase(Mylangu)) {
                                        chatMessage.body = (chatMessage.body + "~" + sender_lang + "~" + response);

                                        Log.d(TAG, "TOTF MESSAGE Mesasage after translation 1:-  " + chatMessage.body);
                                        Log.d(TAG, "TOTF MESSAGE Inside TOTF enabled.. ");
                                        //}
                                    } else if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(TwoTab_Activity.this).equalsIgnoreCase("0")) {

                                        Log.d(TAG, "TOTF MESSAGEInside TOTF disabled.. ");

                                        chatMessage.body = response;
                                        Log.d(TAG, "TOTF MESSAGE Mesasage after translation 0:-  " + chatMessage.body);
                                        // chatMessage.body = (chatMessage.body + "\n" + Mylangu + ":\n" + response);
                                    }
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                        @Override
                                        public void run() {

//                                            Log.d(TAG,  "TOTF MESSAGE Inside process disabled.. ");
                                            Log.d(TAG, "TOTF MESSAGE Mesasage send meesgae:-  " + chatMessage.body);

                                            processMessages(chatMessage, qbChatMessage.getId(), qbChatMessage.getDialogId(), qbChatMessage);

                                        }
                                    });

                                }

                            });
                        } catch (Exception e) {
                        }
                    }

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

                } else {

                    Log.v(TAG, "for group message listener");
                    Log.v(TAG, "Chat Message for group message listener :-=  " + chatMessage);

                    processGroupMessage(chatMessage, qbChatMessage.getId(), qbChatMessage.getDialogId());

                }
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer senderId) {
                Log.e(TAG, "Error in getting message 2:- " + e.getMessage());
            }
        });
    }

    private void processGroupMessage(final ChatMessage message, String qbMsgId, String dialogId) {

        String fileUrl = "";

        Log.v(TAG, "inside incoming mesage processMessage() chatMessage :- " + message);
        Log.v(TAG, "inside incoming mesage processMessage() files id :- " + message.files);
        Log.v(TAG, "inside incoming mesage processMessage() files Uid :- " + message.qbFileUid);
        Log.v(TAG, "inside incoming mesage processMessage() files content Id :- " + message.qbFileUploadId);
        Log.v(TAG, "inside incoming mesage processMessage() files QB user Id :- " + message.receiver_QB_Id);
        Log.v(TAG, "inside incoming mesage processMessage() files QB readStatus :- " + message.readStatus);
        Log.v(TAG, "inside incoming mesage processMessage() dialogId :- " + dialogId);

        if (!message.qbFileUid.equalsIgnoreCase("")) {
            fileUrl = QBFile.getPrivateUrlForUID(message.qbFileUid);
        }
        Log.v(TAG, "inside incoming mesage processMessage() File Url :- " + fileUrl);
        Log.v(TAG, "inside IsmInestatus 11 :- " + message.isMine);

        message.isMine = false;

        message.files = fileUrl;

        Log.v(TAG, " Group Name test 4 :- " + ChatActivity.groupName);
        Log.v(TAG, " Group Name test 5 :- " + message.senderName);
        Log.v(TAG, " Group Name test 6 :- " + message.groupName);
        Log.v(TAG, " Group Name test 7 :- " + message.receiver);
        Log.v(TAG, " Group Name test 8 :- " + message.reciverName);
        Log.v(TAG, " Group Name test 9 :- " + message.sender);
        Log.v(TAG, "QB Serialize Chat dialog in Bytes Group :- " + message.qbChatDialogBytes);

//        fdbbbbbbbbbbbbbbbbbb
      /*  final ChatMessage chatMessage = new ChatMessage(message.receiver, message.reciverName,
                message.sender, getContactName(message.sender), message.groupName, message.body, message.msgid, message.files, message.isMine);
*/
        // groupNotification(chatMessage.groupName, chatMessage.body);

        message.dialog_id = dialogId;
        message.qbMessageId = qbMsgId;
        message.senderName = getContactName(message.sender);
        message.isMine = false;
        message.Date = CommonMethods.getCurrentDate();
        message.Time = CommonMethods.getCurrentTime();
        message.msgStatus = "10";
        DatabaseHelper helper = new DatabaseHelper(TwoTab_Activity.this);
        String msgId = helper.getMsgId(message.msgid);

        Log.v(TAG, "inside IsmInestatus 22 :- " + message.isMine);
        //DatabaseHelper.getInstance(context).getMsgId(chatMessage.msgid);
        if (AppPreferences.getMobileuser(TwoTab_Activity.this).equalsIgnoreCase(message.sender)) {
            Log.v(TAG, " getMobileuser 11:- " + AppPreferences.getMobileuser(TwoTab_Activity.this) + " and " + message.sender);

        } else {
            Log.v(TAG, "QB Serialize Chat dialog in Bytes Group 1 :- " + message.qbChatDialogBytes);
            DatabaseHelper.getInstance(TwoTab_Activity.this).insertChat(message);
            Log.v(TAG, " getMobileuser 22:- " + AppPreferences.getMobileuser(TwoTab_Activity.this) + " and " + message.sender);

            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {

                    int chatMessageCount = 0;
                    if (ChatActivity.instance != null) {

                        Log.d(TAG, "QB file uploaded Content ID GROUP :- " + message.qbFileUploadId);
                        Log.d(TAG, "QB file uploaded UID GROUP :- " + message.qbFileUid);

                      /*  if (!message.qbFileUid.equalsIgnoreCase("") || message.qbFileUid != null) {
                            getFileUrlFromQBContentID(message.qbFileUploadId, message.qbFileUid);
                        }
*/

                        chatMessageCount++;
                        Log.v(TAG, "is chat activity instance != null then chatMessageCount :- " + chatMessageCount);
                        Toast.makeText(getApplicationContext(), "Total Message Count chatinstance :- " + chatMessageCount, Toast.LENGTH_SHORT).show();

                        Log.d(TAG, " Group Messaggg :- " + message.sender);
                        Log.v(TAG, " Group message Name :- " + ChatActivity.FriendMobileTWO);
                        Log.v(TAG, " Group message Name  vvvsvs :- " + ChatActivity.groupName);

                        if (message.body.contains("Image changed by")) {
                            Picasso.with(TwoTab_Activity.this).load(message.Groupimage).error(R.drawable.user_icon)
                                    .resize(200, 200)
                                    .into(conversationimage);
                        } else if (message.body.contains("name changed by")) {
                            toolbartext.setText(message.groupName);
                            groupName = message.groupName;
                        }
                        Log.v(TAG, " getMobileuser :- " + AppPreferences.getMobileuser(TwoTab_Activity.this) + " and " + message.sender);
                        Log.v(TAG, " getMobileUserWithoutCountry :- " + AppPreferences.getMobileUserWithoutCountry(TwoTab_Activity.this) + " and " + message.sender);

                        DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("1", message.sender);

                        Log.v(TAG, " Group Name test 4 :- " + ChatActivity.groupName);
                        Log.v(TAG, " Group Name test 5 :- " + message.senderName);
                        Log.v(TAG, " Group Name test 6 :- " + message.groupName);
                        Log.v(TAG, " Group Name test 7 :- " + message.receiver);
                        Log.v(TAG, " Group Name test 8 :- " + message.reciverName);
                        Log.v(TAG, " Group Name test 9 :- " + message.sender);

                        Log.v(TAG, " Group Name test 1 :- " + ChatActivity.groupName);
                        Log.v(TAG, " Group Name test 2 :- " + message.senderName);
                        Log.v(TAG, " Group Name test 3:- " + message.groupName);
                        Log.v(TAG, " Group Name test 10 :- " + message.reciverName);
                        Log.v(TAG, " Group Name test 119 :- " + message.sender);

                        Log.v(TAG, " Group Name test 12 :- " + message.receiver);

                        if (ChatActivity.groupName.equalsIgnoreCase(message.groupName)) {

                            Log.v("MyXMPP", " Inside M1 ");
                            Log.v(TAG, " Inside M1 ");
                            Log.v(TAG, "inside IsmInestatus 33 :- " + message.isMine);
                            Log.v(TAG, "inside IsmInestatus GroupChat :- " + message.toString());

                            ChatActivity.chatlist.add(message);
                            Log.v(TAG, "ChatArraylOst... :- " + ChatActivity.chatlist.toString());

                           /* if (!message.files.equalsIgnoreCase("")) {
                                Log.v(TAG, " Inside message.files ChatActivity " + message.files);

                                downLoadFile(message);
                            }*/
                            ChatActivity.mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                            // ChatActivity.mLayoutManager.scrollToPosition(ChatActivity.chatlist.size());
                            MediaPlayer mp = MediaPlayer.create(TwoTab_Activity.this, R.raw.steamchat);
                            if (AppPreferences.getConvertTone(TwoTab_Activity.this).equalsIgnoreCase("false")) {
                                mp.stop();
                            } else {
                                mp.start();
                            }
                        } else {
//                            generateNofification(chatMessage);
                            Log.v(TAG, " Inside message.files ChatActivity BAhar 00" + message.files);

                            if (!message.files.equalsIgnoreCase("")) {
                                downLoadFile(message);
                                Log.v(TAG, " Inside message.files ChatActivity BAhar  11" + message.files);
                            }
                        }


                    } else {
//                        generateNofification(chatMessage);
                        Log.v(TAG, " Inside message.files ChatActivity BAhar  22  " + message.files);

                        if (!message.files.equalsIgnoreCase("")) {
                            downLoadFile(message);
                            Log.v(TAG, " Inside message.files ChatActivity BAhar  33  " + message.files);

                        }
                    }

                    if (TwoTab_Activity.instance != null) {
                        Log.d(TAG, "check instance...");
//                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("3", chatMessage.sender);152626262626262626262626262626
                        TwoTab_Activity.updateList(message.groupName);
                        Log.d(TAG, "IFTwoTab_Activity....Mess Group   " + message.groupName + "  : " + message);
                        //  ChatActivity.chatlist.add(chatMessage);
                    }

                   /* if (TwoTab_Activity.instance == null && ChatActivity.instance == null) {
                        generateNofification(chatMessage);
                    }
*/

                    //subscribtion(message.receiver, message.reciverName);
                }
            });

        }

        /*  if (DatabaseHelper.getInstance(TwoTab_Activity.this).getIsBlock(chatMessage.sender)) {

        } else {

        }*/

    }

    private void processGroupMessage1(final ChatMessage message, String qbMsgId, String dialogId) {

        String fileUrl = "";

        Log.v(TAG, "inside incoming mesage processMessage() chatMessage :- " + message);
        Log.v(TAG, "inside incoming mesage processMessage() files id :- " + message.files);
        Log.v(TAG, "inside incoming mesage processMessage() files Uid :- " + message.qbFileUid);
        Log.v(TAG, "inside incoming mesage processMessage() files content Id :- " + message.qbFileUploadId);
        Log.v(TAG, "inside incoming mesage processMessage() files QB user Id :- " + message.receiver_QB_Id);
        Log.v(TAG, "inside incoming mesage processMessage() dialogId :- " + dialogId);

        if (!message.qbFileUid.equalsIgnoreCase("")) {
            fileUrl = QBFile.getPrivateUrlForUID(message.qbFileUid);
        }
        Log.v(TAG, "inside incoming mesage processMessage() File Url :- " + fileUrl);

        message.isMine = false;

        message.files = fileUrl;

        // groupNotification(chatMessage.groupName, chatMessage.body);
        message.dialog_id = dialogId;
        message.qbMessageId = qbMsgId;
        message.senderName = getContactName(message.sender);
        message.isMine = false;
        message.Date = CommonMethods.getCurrentDate();
        message.Time = CommonMethods.getCurrentTime();
        message.msgStatus = "10";
        DatabaseHelper helper = new DatabaseHelper(TwoTab_Activity.this);
        String msgId = helper.getMsgId(message.msgid);
        //DatabaseHelper.getInstance(context).getMsgId(chatMessage.msgid);

        Log.v(TAG, "Inside Process messasge from Group chat :- " + message);
//        DatabaseHelper.getInstance(TwoTab_Activity.this).insertChat(chatMessage);
 /*       if (msgId.equalsIgnoreCase("")) {
          *//*  final ChatMessage message = new ChatMessage(chatMessage.sender,getContactName(chatMessage.sender), chatMessage.receiver, chatMessage.reciverName, chatMessage.groupName, chatMessage.body, chatMessage.msgid,chatMessage.files, chatMessage.isMine);
            message.Date = chatMessage.Date;
            message.Time = chatMessage.Time;
*//*
            if (chatMessage.fileName == null) {

            } else if (!chatMessage.fileName.equalsIgnoreCase("")) {

            }
            Log.d("processGroupMessage", chatMessage.toString());
            DatabaseHelper.getInstance(TwoTab_Activity.this).insertChat(chatMessage);

            if (chatMessage.body.contains("removeFromGroup")) {

            }
        }
*/
           /* if(chatMessage.body.contains("Remove by :")) {
                userExitFromGroup(chatMessage.receiver);
            }else if(chatMessage.body.contains("Subadmin :")) {

            }*/

        if (AppPreferences.getMobileuser(TwoTab_Activity.this).equalsIgnoreCase(message.sender)) {
            Log.v(TAG, " getMobileuser 11:- " + AppPreferences.getMobileuser(TwoTab_Activity.this) + " and " + message.sender);

        } else {
            DatabaseHelper.getInstance(TwoTab_Activity.this).insertChat(message);
            Log.v(TAG, " getMobileuser 22:- " + AppPreferences.getMobileuser(TwoTab_Activity.this) + " and " + message.sender);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    if (ChatActivity.instance != null) {

                        Log.d(TAG, "QB file uploaded Content ID GROUP :- " + message.qbFileUploadId);
                        Log.d(TAG, "QB file uploaded UID GROUP :- " + message.qbFileUid);

                        Log.d(TAG, "Group Messaggg :- " + message.sender);
                        Log.v(TAG, " Group message Name :- " + ChatActivity.FriendMobileTWO);
                        Log.v(TAG, " Group message Name  vvvsvs :- " + ChatActivity.groupName);

                        if (message.body.contains("Image changed by")) {
                            Picasso.with(TwoTab_Activity.this).load(message.Groupimage).error(R.drawable.user_icon)
                                    .resize(200, 200)
                                    .into(conversationimage);
                        } else if (message.body.contains("name changed by")) {
                            toolbartext.setText(message.groupName);
                            groupName = message.groupName;
                        }

                        DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("1", message.sender);

                        if (ChatActivity.groupName.equalsIgnoreCase(message.groupName)) {
                            Log.v("MyXMPP", " Inside M1 ");

                            if (!message.files.equalsIgnoreCase("")) {
                                downLoadFile(message);
                            }

                            ChatActivity.chatlist.add(message);

                            ChatActivity.mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                            // ChatActivity.mLayoutManager.scrollToPosition(ChatActivity.chatlist.size());
                            MediaPlayer mp = MediaPlayer.create(TwoTab_Activity.this, R.raw.steamchat);
                            if (AppPreferences.getConvertTone(TwoTab_Activity.this).equalsIgnoreCase("false")) {
                                mp.stop();
                            } else {
                                mp.start();
                            }
                        } else {
//                            generateNofification(chatMessage);

                            if (!message.files.equalsIgnoreCase("")) {
                                downLoadFile(message);
                            }
                        }


                    } else {
//                        generateNofification(chatMessage);

                        if (!message.files.equalsIgnoreCase("")) {
                            downLoadFile(message);
                        }
                    }

                    if (TwoTab_Activity.instance != null) {

                        DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("3", message.sender);
                        TwoTab_Activity.updateList(message.groupName);
                    }

                   /* if (TwoTab_Activity.instance == null && ChatActivity.instance == null) {
                        generateNofification(chatMessage);
                    }
*/

                    //subscribtion(message.receiver, message.reciverName);
                }
            });
        }

        if (DatabaseHelper.getInstance(TwoTab_Activity.this).getIsBlock(message.sender)) {

        } else {

        }

    }

    private void processMessages(final ChatMessage chatMessage, String msgId, final String dialogId, final QBChatMessage qbChatMessage) {

        Log.v(TAG, "SQLITE Data insertion while receiving messages :- " + chatMessage.sender);          // 9074900690
        Log.v(TAG, "7 :- " + chatMessage.senderName);                                                   // Pr
        Log.v(TAG, "8 :- " + chatMessage.receiver);                                                     // 919630588122
        Log.v(TAG, "9 :- " + chatMessage.reciverName);                                                  // Sheetal

        Log.v(TAG, "inside incoming mesage processMessage() chatMessage :- " + chatMessage);
        Log.v(TAG, "inside incoming mesage processMessage() files id :- " + chatMessage.files);
        Log.v(TAG, "inside incoming mesage processMessage() files Uid :- " + chatMessage.qbFileUid);
        Log.v(TAG, "inside incoming mesage processMessage() files content Id :- " + chatMessage.qbFileUploadId);
        Log.v(TAG, "inside incoming mesage processMessage() files QB user Id :- " + chatMessage.receiver_QB_Id);
        Log.v(TAG, "inside incoming mesage processMessage() dialogId :- " + dialogId);
        Log.v(TAG, "inside incoming mesage processMessage() msgId :- " + msgId);

        Log.v(TAG, "TOTF MESSAGE  msgId :- " + chatMessage.body);
        String fileUrl = "";
        if (!chatMessage.qbFileUid.equalsIgnoreCase("")) {
            fileUrl = QBFile.getPrivateUrlForUID(chatMessage.qbFileUid);
            Log.v(TAG, "inside incoming mesage processMessage() File Url 1:- " + fileUrl);
            chatMessage.files = fileUrl;
        } /*else if (!chatMessage.qbFileUid.equalsIgnoreCase("null")) {
            fileUrl = QBFile.getPrivateUrlForUID(chatMessage.qbFileUid);
            Log.v(TAG, "inside incoming mesage processMessage() File Url 2:- " + fileUrl);
        } else if (chatMessage.qbFileUid != null) {
            fileUrl = QBFile.getPrivateUrlForUID(chatMessage.qbFileUid);
            Log.v(TAG, "inside incoming mesage processMessage() File Url 3:- " + fileUrl);
        } */ else {
//            fileUrl = QBFile.getPrivateUrlForUID(chatMessage.qbFileUid);         // need to comment this line for normal message
            Log.v(TAG, "inside incoming mesage processMessage() File Url 54:- " + fileUrl);
            chatMessage.files = fileUrl;
        }
        chatMessage.isMine = false;
        // String msgId = DatabaseHelper.getInstance(context).getMs gId(chatMessage.msgid);

        final ChatMessage message = new ChatMessage(chatMessage.receiver, chatMessage.reciverName,
                chatMessage.sender, getContactName(chatMessage.sender), chatMessage.groupName, chatMessage.body, chatMessage.msgid, chatMessage.files, chatMessage.isMine);
        //message.Date = chatMessage.Date;
        message.fileName = chatMessage.fileName;
        message.Date = CommonMethods.getCurrentDate();
        message.Time = CommonMethods.getCurrentTime();
        message.type = Message.Type.chat.name();
        message.senderlanguages = chatMessage.reciverlanguages;
        message.reciverlanguages = chatMessage.senderlanguages;
        message.ReciverFriendImage = chatMessage.MyImage;
        message.userStatus = chatMessage.userStatus;
        message.msgStatus = "10";                       // need to un comment this line for normal and TOTF message
        message.sender_QB_Id = chatMessage.sender_QB_Id;
        message.receiver_QB_Id = chatMessage.receiver_QB_Id;
        message.friend_QB_Id = chatMessage.sender_QB_Id;
        message.dialog_id = dialogId;
        message.readStatus = "";
        message.qbMessageId = msgId;
//        message.qbChatDialogBytes =  chatMessage.qbChatDialogBytes;

        Log.v(TAG, "Inside Process messasge from personal chat :- " + message);

        Log.v(TAG, "SQLITE Data insertion while receiving messages :- " + message.sender);               // 919630588122
        Log.v(TAG, "1 :- " + message.senderName);               // Sheetal
        Log.v(TAG, "2 :- " + message.receiver);               // 919074900690
        Log.v(TAG, "3 :- " + message.reciverName);               // Pra

        getQBChatDialogByDialogID(dialogId, chatMessage.sender_QB_Id, message);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (ChatActivity.instance != null) {
//gfffffffffffffffffd
                    Log.d(TAG, "QB file uploaded Content ID :- " + chatMessage.qbFileUploadId);
                    Log.d(TAG, "QB file uploaded UID :- " + chatMessage.qbFileUid);

                    Log.d("msggg", chatMessage.sender);
                    Log.v(TAG, "One to One Message :- " + chatMessage.sender);
                    Log.v(TAG, "One to One Message :- " + message);
                    Log.v(TAG, "One to One Message 12324232 :- " + qbChatDialog);

//                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("1", chatMessage.sender);
                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("3", chatMessage.sender);
                    if (ChatActivity.FriendMobileTWO.equalsIgnoreCase(chatMessage.sender)) {

//                        markMessageAsRead(dialogId, qbChatDialog);
//dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                        if (mQbChatMessage != null) {
                            Log.v(TAG, "mQbChatMessage :- " + mQbChatMessage);
                            try {
                                qbChatDialog.initForChat(QBChatService.getInstance());
                                qbChatDialog.readMessage(mQbChatMessage);
                            } catch (XMPPException e) {
                                e.printStackTrace();
                                Log.e(TAG, "mQbChatMessage error at when Chat activity is open 1:- " + e.getMessage());
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                                Log.e(TAG, "SmackException.NotConnectedException while sending read status :- " + e.getMessage());
                            }
                        }
                        if (!message.files.equalsIgnoreCase("")) {
                            downLoadFile(message);
                        }

                        ChatActivity.chatlist.add(message);

                        ChatActivity.mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.steamchat);

                        if (AppPreferences.getConvertTone(getApplicationContext()).equalsIgnoreCase("false")) {
                            mp.stop();
                        } else {
                            mp.start();
                        }
                           /* final MediaPlayer mp = MediaPlayer.create(context, R.raw.steamchat);
                            mp.start();*/
                    } else {
//                        generateNofification(message);
                        if (!message.files.equalsIgnoreCase("")) {
                            downLoadFile(message);
                        }
                        TwoTab_Activity.updateList(chatMessage.groupName);
                    }

                } else {
//                    generateNofification(message);
                    if (!message.files.equalsIgnoreCase("")) {
                        downLoadFile(message);
                    }
                }

                if (TwoTab_Activity.instance != null) {
                    TwoTab_Activity.updateList(chatMessage.groupName);
                    Log.d(TAG, " AppController.privateMessageCountravi  teo   " + AppController.privateMessageCount);
                    Log.d(TAG, " AppController chatMessage.groupName :- " + chatMessage.groupName);
                }

                Log.d(TAG, "Main handler count :- " + AppController.privateMessageCount);
//subscribtion(message.receiver, message.reciverName);
            }
        });
    }

    private void downLoadFile(final ChatMessage message) {
        /*ObjectAnimator animation = ObjectAnimator.ofInt (vh1.progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration (5000); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();*/

        new DownloadFile(TwoTab_Activity.this, new ImageView(TwoTab_Activity.this), new ProgressBar(TwoTab_Activity.this), new VolleyCallback() {
            @Override
            public void backResponse(String response) {
//dcsssssssssss
                Log.v(TAG, "Downloaded file path / response :- " + response);
//                message.files =response;
                if (response.equalsIgnoreCase("11")) {
//                    message.files =response;
                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgStatus(response, message.msgid);
                } else {
//                    message.files =response;
                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgStatus("12", message.msgid);
                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateFileName(response, message.msgid);
                }
                /*
                    DatabaseHelper.getInstance(context).UpdateMsgStatus(response,message.msgid);
                File SpeakaMe = Environment.getExternalStorageDirectory();
                File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/image/recive");
                String file = SpeakaMeDirectory+"/"+message.fileName;
                DatabaseHelper.getInstance(context).UpdateFileName(file,message.msgid);*/

                mediaScanner(response);
                Bitmap bitmap = BitmapFactory.decodeFile((message.files));
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, message.fileName, null);
//csaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
            }
        }).execute(message.files, message.fileName);

    }

    public String getContactName(String number) {
        String name = number;
        ArrayList<Contact> listContacts = new ContactFetcher(TwoTab_Activity.this).fetchAll();
        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.v(TAG, "ContactFetch :- " + contact.name + "::" + phone.number + " ------ " + number);
                Log.v(TAG, "ContactFetch 123:- " + contact.name + "::" + phone.number.toString().replace("-", "").replace(" ", "") + " ------ " + number);
                if (number.contains(phone.number.toString().replace("-", "").replace(" ", "")) && phone.number.length() > 9) {
                    Log.v(TAG, "getting contact name  :- " + contact.name);
                    return contact.name;
                }
            }
        }

        Log.v(TAG, "" + name);
        return name;
    }

    private void createSession(String login, String pwd) {

        final QBUser user = new QBUser(login, pwd);

        QBAuth.createSession(user).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                user.setId(qbSession.getUserId());

                chatLogin(user);

                /*chatService.login(user, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        Log.v(TAG, "Login to chat service done ");
                        registerQbChatListeners();
                        incomingMessage();
                        initRoster();
                        messageStatusesManager = chatService.getMessageStatusesManager();
                        subscribeUser(AppPreferences.getQBUserId(TwoTab_Activity.this));
                        initMessageStatusManagerAndListener();
//                        SDCCCCCCCCCCCCCCCCCCCCCC
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.v(TAG, "Error while login to chat service ");
                        Log.v(TAG, "Error chat serviev :-  " + e.getMessage());
                    }
                });*/
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error in auth :-  " + e.getMessage());
            }
        });

    }

    private void chatLogin(QBUser user) {

        chatService.login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                Log.v(TAG, "Login to chat service done ");
                registerQbChatListeners();
                incomingMessage();
                initRoster(chatService);
//                getting_UserImage();
                messageStatusesManager = QBChatService.getInstance().getMessageStatusesManager();
                subscribeUser(AppPreferences.getQBUserId(TwoTab_Activity.this));
//                initMessageStatusManagerAndListener();
//                        SDCCCCCCCCCCCCCCCCCCCCCC
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error while login to chat service ");
                Log.v(TAG, "Error chat serviev :-  " + e.getMessage());
            }
        });

    }

    private void subscribeUser(int qbUserId) {

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

    private void checkUserPresence(int qbUserId) {

//        sdvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

        QBPresence qbPresence = сhatRoster.getPresence(qbUserId);


        Log.v(TAG, "qbPresence getting User status :-  " + qbPresence);

        if (qbPresence.getType() == QBPresence.Type.online) {
            Toast.makeText(getApplicationContext(), "User " + qbUserId + " is online", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "User " + qbUserId + " is online");
            User user = new User();
            user.setFriend_id(qbUserId);
            user.setStatus("Online");
            DatabaseHelper.getInstance(TwoTab_Activity.this).InsertStatus(user);
            Log.v(TAG, "QB status online from database 111111111 :- " + DatabaseHelper.getInstance(TwoTab_Activity.this).getLastSeenQB(user.getFriend_id()));

        } else {
            Log.v(TAG, "User " + qbUserId + " is offline");
//            Toast.makeText(getApplicationContext(), "User " + qbUserId + " is offline", Toast.LENGTH_SHORT).show();
        }

    }

    private void registerQbChatListeners() {

        Log.v(TAG, "Inside registerQbChatListeners");

        incomingMessagesManager = chatService.getIncomingMessagesManager();
        systemMessagesManager = chatService.getSystemMessagesManager();

        if (incomingMessagesManager != null) {
            incomingMessagesManager.addDialogMessageListener(allDialogsMessagesListener != null
                    ? allDialogsMessagesListener : new AllDialogsMessageListener());
        }

        if (systemMessagesManager != null) {

            Log.v(TAG, "Inside systemMessagesManager ");
//            systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
//                    ? systemMessagesListener : new SystemMessagesListener());
            systemMessagesManager.addSystemMessageListener(systemMessagesListener = new SystemMessagesListener());
        }

        dialogsManager.addManagingDialogsCallbackListener(this);
    }

    private void unregisterQbChatListeners() {

        if (incomingMessagesManager != null) {
            incomingMessagesManager.removeDialogMessageListrener(allDialogsMessagesListener);
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.removeSystemMessageListener(systemMessagesListener);
        }

        dialogsManager.removeManagingDialogsCallbackListener(this);
    }

    private void loginUserToQuickBlox(String mobile_no, String pwd) {

//        QBSettings.getInstance().fastConfigInit(MyApplication.APP_ID, MyApplication.AUTH_KEY, MyApplication.AUTH_SECRET);


        Log.v(TAG, " ~~~~~~~~~~~~ Inside Login Button ~~~~~~~~~~~~ ");
        Log.v(TAG, "Login :-  " + mobile_no);
        Log.v(TAG, "Pwd :-  " + pwd);

        final QBUser user = new QBUser();
        user.setLogin(mobile_no);
        user.setPassword(pwd);

        dialog = new ProgressDialog(TwoTab_Activity.this);
        dialog.setMessage("Please wait...");
//        dialog.show();

        Log.v(TAG, "before login :- " + AppPreferences.getQBUserId(TwoTab_Activity.this));
        loginAsync(user, dialog); // Asynchronous way:

    }

    private void loginAsync(QBUser user, final ProgressDialog dialog) {

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                dialog.dismiss();

                Log.v(TAG, "After login:- " + AppPreferences.getQBUserId(TwoTab_Activity.this));
                Log.v(TAG, "after login 1 :- " + qbUser.getId());
                AppPreferences.setQBUserId(TwoTab_Activity.this, qbUser.getId());
                Log.v(TAG, "after settting Qb user id to pref :- " + AppPreferences.getQBUserId(TwoTab_Activity.this));

                Log.v(TAG, "Login Sucessfully");
                Log.v(TAG, "Bundle data :- " + bundle.toString());

//                subscribeUserForStatus(qbUser.getId());
//                Snackbar.make(findViewById(android.R.id.content), "User Login Sucessfully.", Snackbar.LENGTH_SHORT).show();

                if (chatService.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(), "ALready logged in", Toast.LENGTH_SHORT).show();
                } else {
                    createSession(qbUser.getLogin(), "12345678");
                }
                createSession(qbUser.getLogin(), "12345678");

            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Login failed .." + e.getMessage());
                dialog.dismiss();
                String message = e.getMessage();
                Snackbar.make(findViewById(android.R.id.content), "Login failed due to " + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void initChatService() {


        chatService = QBChatService.getInstance();

        QBChatService.setDebugEnabled(true); // enable chat logging
        QBChatService.setDefaultPacketReplyTimeout(10000);

        QBChatService.getInstance().setUseStreamManagement(true);

        QBChatService.ConfigurationBuilder builder = new QBChatService.ConfigurationBuilder();
        builder.setAutojoinEnabled(true);
        builder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
        builder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        builder.setUseTls(true);
        QBChatService.setConfigurationBuilder(builder);

//        chatService.setUseStreamManagement(true);
        chatService.startAutoSendPresence(10);

    }

    private void createSessionForReg(String username, String pwd) {

//        mDialog = new ProgressDialog(TwoTab_Activity.this);
//        mDialog.setMessage("Please wait...");
//        mDialog.setTitle("Creating session");
//        mDialog.show();

        final QBUser qbUser = new QBUser(username, pwd);

        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());

                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                chatService.login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
//                        mDialog.dismiss();
                        Log.v(TAG, "session created !" + "");
//                        getAllUserList();
                    }

                    @Override
                    public void onError(QBResponseException e) {
//                        mDialog.dismiss();
                        Log.e(TAG, "Error :- " + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
//                mDialog.dismiss();
                Log.d(TAG, "Error :- " + e.getMessage());
            }
        });
    }

    private void setMaterialTapTargetPromptContact() {

        Log.v("svnvnvnv", "inside setMaterialTapTargetPrompt()");

        new MaterialTapTargetPrompt.Builder(this)
                .setPrimaryText("Search your conatcts")
                .setSecondaryText("SpeakAme Contact list and how add contacts, new groups and designated favorites")
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen._200sdp)
                .setIcon(R.drawable.cuser)
                .setBackgroundColour(getResources().getColor(R.color.colorPrimary))
                .setTarget(R.id.action_contact)
                .setAutoDismiss(true)
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

//                        setOverflowprompt();
                    }

                    @Override
                    public void onHidePromptComplete() {
                        setOverflowprompt();
                    }
                })
                .show();
    }

    private void setOverflowprompt() {

        new MaterialTapTargetPrompt.Builder(this)
                .setPrimaryText("More Action")
                .setSecondaryText("Tap the 3 vertical dots to see more options")
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen._200sdp)
                .setIcon(R.drawable.right_menu_icon)
                .setTarget(R.id.action_menu)
                .setAutoDismiss(true)
                .setBackgroundColour(getResources().getColor(R.color.colorPrimary))
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                    }

                    @Override
                    public void onHidePromptComplete() {

                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        contact = menu.findItem(R.id.action_contact);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            if (isSerch) {
                isSerch = false;
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
                srch_edit.setVisibility(View.VISIBLE);
                srch_edit.setEnabled(true);
                srch_edit.setActivated(true);
                srch_edit.setFocusable(true);
                srch_edit.setFocusableInTouchMode(true);
                srch_edit.requestFocus();
                srch_edit.requestFocusFromTouch();
            } else {
                isSerch = true;
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);
                srch_edit.setVisibility(View.GONE);
            }
            return true;
        }

        if (id == R.id.action_contact) {

            Intent intent = new Intent(TwoTab_Activity.this, ContactImport_Activity.class);
            startActivityForResult(intent, REQUEST_SELECT_PEOPLE);
//            startActivity(intent);
//
//            ContactImport_Activity.startForResult(this, REQUEST_SELECT_PEOPLE);
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

                        case R.id.status:
                            Intent intent1 = new Intent(TwoTab_Activity.this, EditProfile_Activity.class);
                            startActivity(intent1);
                            break;

                        case R.id.refresh:
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

        registerQbChatListeners();

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
        updateChatAdapter("OnCreate 1");

        final QBUser user = new QBUser(AppPreferences.getMobileuser(TwoTab_Activity.this), "12345678");
        user.setId(AppPreferences.getQBUserId(TwoTab_Activity.this));
//        chatLogin(user);

    }

    private void updateChatAdapter(String text) {

        chatMessageList = DatabaseHelper.getInstance(TwoTab_Activity.this).getReciever();

        updateMessageCount(chatMessageList, "");

        Log.d(TAG, "chatMessageList inside " + text + " :-  " + chatMessageList.toString());
        AppController.privateMessageCount = 0;
        adapter = new BroadcastnewgroupAdapter(TwoTab_Activity.this, chatMessageList, messageCountTextView);
        chatlist.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
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
                if (adapter != null) {
                    adapter.filter(value.toLowerCase());
                }
            }
        });

        srch_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
//                    Function.hideSoftKeyboard(TwoTab_Activity.this);
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "inside onActivity Result 1:- " + data);
        Log.v(TAG, "inside onActivity Result 2:- " + resultCode);
        Log.v(TAG, "inside onActivity Result 3:- " + requestCode);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_SELECT_PEOPLE) {

                int qb_id1 = data.getExtras().getInt("recipient_qb_id");
                int qb_id = data.getIntExtra("recipient_qb_id", 0);
                Log.v(TAG, "Recipient User id :- " + qb_id1);
                Log.v(TAG, "Recipient User id :- " + qb_id);

                checkUserPresence(qb_id);
//                uiThread(qb_id);

                AllBeans allBeans = data.getParcelableExtra("value");
                AllBeans allBeans1 = data.getExtras().getParcelable("value");
                Log.v(TAG, "get Parcable data :- " + allBeans);
                Log.v(TAG, "get Parcable data 1 :- " + allBeans1);

                /*getUserIdByIds(qb_id, allBeans);*/

                createDialogQuickBlox(qb_id, allBeans);
                registerQbChatListeners();

                /*

                if (isPrivateDialogExist(selectedUsers)) {
                    selectedUsers.remove(ChatHelper.getCurrentUser());
                    QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                    isProcessingResultInProgress = false;
                    ChatActivity.startForResult(DialogsActivity.this, REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
                } else {
                    ProgressDialogFragment.show(getSupportFragmentManager(), R.string.create_chat);
                    createDialog(selectedUsers);
                }
                */

            } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {
                if (data != null) {

                    qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
                    Log.v(TAG, "onActivity Result 123 :- " + qbChatDialog);
                    loadUpdatedDialog(qbChatDialog.getDialogId());

                } else {
//                    isProcessingResultInProgress = false;

                }
            }
        }
    }

    private void uiThread(final int qb_id) {

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


                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    private void loadUpdatedDialog(String dialogId) {
        ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle bundle) {
//                isProcessingResultInProgress = false;
                QbDialogHolder.getInstance().addDialog(result);
                updateDialogsAdapter();
            }

            @Override
            public void onError(QBResponseException e) {
//                isProcessingResultInProgress = false;
            }

        });
    }

    private void updateDialogsAdapter() {
//        updateList("");
    }

    private void getUserIdByIds(int qb_id, final AllBeans allBeans) {


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
                }

                if (isPrivateDialogExist(qbUsers)) {

                    Toast.makeText(getApplicationContext(), "new code 1", Toast.LENGTH_SHORT).show();

//                    selectedUsers.remove(ChatHelper.getCurrentUser());
                    QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(qbUsers.get(0));

                    ChatActivity.startForResult(TwoTab_Activity.this, REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);

                } else {
//                    ProgressDialogFragment.show(getSupportFragmentManager(), R.string.create_chat);
//                    createDialog(selectedUsers);
                    createDialogQuickBlox(qbUsers.get(0).getId(), allBeans);

                }

            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error occured");
                Log.v(TAG, "Error getting contact details :- " + e.getMessage());

            }
        });

    }

    private boolean isPrivateDialogExist(ArrayList<QBUser> allSelectedUsers) {

        ArrayList<QBUser> selectedUsers = new ArrayList<>();
        selectedUsers.addAll(allSelectedUsers);
        selectedUsers.remove(ChatHelper.getCurrentUser());
        return selectedUsers.size() == 1 && QbDialogHolder.getInstance().hasPrivateDialogWithUser(selectedUsers.get(0));

    }

    private void createDialogQuickBlox(int qb_id, final AllBeans allBeans) {

        Log.v(TAG, "All beans data 1 :- " + allBeans.getFriendname());
        Log.v(TAG, "All beans data 2 :- " + allBeans.getFriendmobile());
        Log.v(TAG, "All beans data 3 :- " + allBeans.getFriendStatus());

        Log.v(TAG, "Message sent :- " + qb_id);
        List<Integer> occupants_id = new ArrayList<>();
        occupants_id.add(qb_id);

        QBChatDialog privateDialog = DialogUtils.buildPrivateDialog(qb_id);
        privateDialog.setOccupantsIds(occupants_id);
        Log.v(TAG, "privateDialog 1:-  " + privateDialog);

        QBRestChatService.createChatDialog(privateDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {

                Log.v(TAG, "deserialized dialog 1 = " + result);
//                qbChatDialog = (QBChatDialog) result;
//                Log.v(TAG, "deserialized dialog 2 = " + qbChatDialog);
//                qbChatDialog.initForChat(QBChatService.getInstance());
                qbChatDialog = result;

                byte[] data = SerializationUtils.serialize(qbChatDialog);
                Log.v(TAG, "QB Serialize dialog to data :- " + data);
                QBChatDialog yourObject = SerializationUtils.deserialize(data);
                Log.v(TAG, "QB Serialize data to dialog :- " + yourObject);

                Log.v(TAG, "User is logged in or not :- " + chatService.isLoggedIn());
                chatService.isLoggedIn();

                if (chatService.isLoggedIn()) {

                } else {
                    createSession(AppPreferences.getMobileuser(TwoTab_Activity.this), "12345678");
                }
                createSession(AppPreferences.getMobileuser(TwoTab_Activity.this), "12345678");

                if (Function.isConnectingToInternet(TwoTab_Activity.this)) {
                    sendSystemMessageAboutCreatingDialog(systemMessagesManager, result);
                } else {
                    Toast.makeText(getApplicationContext(), "Check internet connection.", Toast.LENGTH_SHORT).show();
                }

                /*
                String qbDialogString = qbChatDialog.toString();
                Class c = object.getClass();
                String cn = c.toString();*/
/*

                QBChatDialog qbChat = qbChatDialog;
                Class QBChatDialog = qbChat.getClass();
                String cn = QBChatDialog.toString();

                Class abc  = Class.forName(cn);
*/

/*
                Gson gson = new Gson();
                String jsonString = gson.toJson(qbChatDialog);
                String jsonString123 = qbChatDialog.toString();

                Log.v(TAG, "Json Conversion String from QbChatDialog pragya 1 :- " + jsonString);
                Gson gson1 = new Gson();
                QBChatDialog qbObject = gson1.fromJson(jsonString123 , QBChatDialog.class);
*/


                Bundle bundle = new Bundle();
                bundle.putSerializable("EXTRA_CHAT_MESSAGE", mQbChatMessage);

                Intent intent = new Intent(TwoTab_Activity.this, ChatActivity.class);
                intent.setAction("fromContact");
                intent.putExtra("value", allBeans);
                intent.putExtra("groupName", "");
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, result);
                startActivityForResult(intent, REQUEST_DIALOG_ID_FOR_UPDATE);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }

            @Override
            public void onError(QBResponseException responseException) {

                Log.v(TAG, "Error Occures sendChatMessage()");
                Log.v(TAG, "send ChatMessage Error :- " + responseException.getMessage());

            }
        });
    }

    //Let's notify occupants
    public void sendSystemMessageAboutCreatingDialog(QBSystemMessagesManager systemMessagesManager, QBChatDialog dialog) {

        registerQbChatListeners();
        QBChatMessage systemMessageCreatingDialog = buildSystemMessageAboutCreatingGroupDialog(dialog);

        Log.v(TAG, "Two tab Activity  self id:- " + AppPreferences.getQBUserId(TwoTab_Activity.this));
        Log.v(TAG, "Two tab Activity  freind id 1 :- " + dialog.getOccupants());
        Log.v(TAG, "Two tab Activity  freind id 2 :- " + dialog.getRecipientId());

        for (Integer recipientId : dialog.getOccupants()) {

            Log.v(TAG, "Two tab Activity  friend id inside loop:- " + recipientId);
            try {
                if (!recipientId.equals(AppPreferences.getQBUserId(TwoTab_Activity.this))) {

                    systemMessageCreatingDialog.setRecipientId(recipientId);
                    systemMessagesManager.sendSystemMessage(systemMessageCreatingDialog);

                }
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.e(TAG, "Two tab Activity Error :- " + e.getMessage());
            }
        }
    }

    private QBChatMessage buildSystemMessageAboutCreatingGroupDialog(QBChatDialog dialog) {

        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setDialogId(dialog.getDialogId());
        qbChatMessage.setProperty(AppConstants.PROPERTY_OCCUPANTS_IDS, QbDialogUtils.getOccupantsIdsStringFromList(dialog.getOccupants()));
        qbChatMessage.setProperty(AppConstants.PROPERTY_DIALOG_TYPE, String.valueOf(dialog.getType().getCode()));
        qbChatMessage.setProperty(AppConstants.PROPERTY_DIALOG_NAME, String.valueOf(dialog.getName()));
        qbChatMessage.setProperty(AppConstants.PROPERTY_NOTIFICATION_TYPE, AppConstants.CREATING_DIALOG);
        return qbChatMessage;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterQbChatListeners();
    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {
        Log.v(TAG, "on Dialog Created :- " + chatDialog);
    }

    @Override
    public void onDialogUpdated(String chatDialog) {

        Log.v(TAG, "on Dialog Updated :- " + chatDialog);
    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {

        Log.v(TAG, "on onNewDialogLoaded Created :- " + chatDialog);

    }

    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("SpeakAme");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

    }

    private void initViews() {
        qbService = new QBService();

        messageCountTextView = (TextView) findViewById(R.id.messageCountTextView);
        messageCountTextView.setVisibility(View.GONE);
        messageCountTextView_group = (TextView) findViewById(R.id.messageCountTextView_grp);
        messageCountTextView_group.setVisibility(View.GONE);


        linearLayout = (RelativeLayout) findViewById(R.id.linearLayout);
        firsttab = (TextView) findViewById(R.id.broadcast);
        firsttab.setBackground(getResources().getDrawable(R.drawable.left_rounded_corner_bg_layout));
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

    }

    private void checkMarshmallowPermission() {

        if (ContextCompat.checkSelfPermission(TwoTab_Activity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(TwoTab_Activity.this,
                    Manifest.permission.READ_CONTACTS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.READ_CONTACTS}
                            , 1);
                }
            } else {
                ActivityCompat.requestPermissions(TwoTab_Activity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            }
        }

        if (ContextCompat.checkSelfPermission(TwoTab_Activity.this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(TwoTab_Activity.this,
                    Manifest.permission.WRITE_CONTACTS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.WRITE_CONTACTS}
                            , 1);
                }

            } else {

                ActivityCompat.requestPermissions(TwoTab_Activity.this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        1);

            }
        }
    }

    private void markMessageAsRead(String dialogId, final QBChatDialog qbChatDialog) {

        // TODO -- mark message as read

        QBRestChatService.markMessagesAsRead(dialogId, null).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                Log.v(TAG, " Read message mark success ... :- " + bundle.toString());
                Log.v(TAG, " Read message mark success ... ");

                if (mQbChatMessage != null) {
                    Log.v(TAG, " mQbChatMessage not null... " + mQbChatMessage);
                    try {
                        qbChatDialog.initForChat(QBChatService.getInstance());
                        qbChatDialog.readMessage(mQbChatMessage);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error while mark read message" + e.getMessage());
            }
        });
    }

    private void setListener() {

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
                startActivityForResult(intent, REQUEST_SELECT_PEOPLE);
            }
        });

//        user.setOnClickListener(new View.OnClickListener() {
//            @Overridein
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
//rfffffffffffffffffffffffffffffffffffeeeeeeer
                registerQbChatListeners();
//sdvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                if (chatMessageList.get(position).groupName.equalsIgnoreCase("")) {

                    Log.v(TAG, "Chat Message full :- " + chatMessageList.get(position));
                    Log.v(TAG, "This is a One to one Chat !!!!!!!!!!!!!!!!!!! ");

//scccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
                    String messageCountString = DatabaseHelper.getInstance(TwoTab_Activity.this).getmsgCount("chat", chatMessageList.get(position).receiver);
                    Log.v(TAG, "messageCountString while click on chat list item :- " + messageCountString);

                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("3", chatMessageList.get(position).receiver);
//                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateReadStatus("3", chatMessageList.get(position).dialog_id, chatMessageList.get(position).qbMessageId, chatMessageList.get(position).receiver);
                    AllBeans allBeans = new AllBeans();
                    allBeans.setFriendname(chatMessageList.get(position).reciverName);
                    allBeans.setFriendStatus(chatMessageList.get(position).userStatus);
                    allBeans.setFriendmobile(chatMessageList.get(position).receiver);
                    allBeans.setGroupName(chatMessageList.get(position).groupName);
                    allBeans.setGroupid(chatMessageList.get(position).groupid);
                    allBeans.setLanguages(chatMessageList.get(position).reciverlanguages);
                    allBeans.setFriendimage(chatMessageList.get(position).ReciverFriendImage);
                    allBeans.setGroupImage(chatMessageList.get(position).Groupimage);
                    allBeans.setFriendQB_id(chatMessageList.get(position).friend_QB_Id);

                    Log.v(TAG, "Quick Blox in chat list item 1 :- " + chatMessageList.get(position).dialog_id);
//                  Log.v(TAG, "Quick Blox in chat list item 2 :- " + allBeans.getFRIEND_Qb_Id());

                    ArrayList<AllBeans> beansArrayList = new ArrayList<AllBeans>();
                    beansArrayList.add(allBeans);

                    Log.v(TAG, "qbChatDialogBytes in bytes data:- " + chatMessageList.get(position).qbChatDialogBytes);

                    byte[] chatbytes = chatMessageList.get(position).qbChatDialogBytes;

                    qbChatDialog = SerializationUtils.deserialize(chatbytes);

                    Log.v(TAG, "qbChatDialog after desierialized data :- " + qbChatDialog);

                    Log.v(TAG, "reciverName :- " + chatMessageList.get(position).reciverName);
                    Log.v(TAG, "receiver :- " + chatMessageList.get(position).receiver);
                    Log.v(TAG, "sender :- " + chatMessageList.get(position).sender);
                    Log.v(TAG, "senderName :- " + chatMessageList.get(position).senderName);

                    /*StringifyArrayList<String> messageIds =  DatabaseHelper.getInstance(TwoTab_Activity.this).getMessageUpdateStatus(chatMessageList.get(position).dialog_id);

                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateReadStatusForAllMessage("3", chatMessageList.get(position).dialog_id, chatMessageList.get(position).receiver);
                    Log.v(TAG, "Message ids :- " + messageIds);

                    for (String messaeg :messageIds) {
                        Log.v(TAG, "Message ids :- " + messaeg);
                    }
*/
                    Log.e(TAG, " mQbChatMessage not null... click on chat :-  " + mQbChatMessage);
                    if (mQbChatMessage != null) {
                        Log.v(TAG, " mQbChatMessage not null... " + mQbChatMessage);
                        try {
                            qbChatDialog.initForChat(QBChatService.getInstance());
                            qbChatDialog.readMessage(mQbChatMessage);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                            Log.v(TAG, "mQbChatMessage error at when Two Tab activity is open 1 :- " + e.getMessage());
                        } catch (SmackException.NotConnectedException e) {
                            Log.v(TAG, "mQbChatMessage error at when Two Tab activity is open 2 :- " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent(TwoTab_Activity.this, ChatActivity.class);
//                    intent.setAction("PrivateChatClick");
                    intent.putExtra("value", allBeans);
                    intent.putExtra("groupName", "");
                    intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, qbChatDialog);
                    startActivityForResult(intent, REQUEST_DIALOG_ID_FOR_UPDATE);

//                    createDialogQuickBlox(chatMessageList.get(position).friend_QB_Id, allBeans);

                } else {

                    Log.v(TAG, "This is a group !!!!!!!!!!!!!!!!!!! ");

                    Log.v(TAG, "Chat Message full :- " + chatMessageList.get(position));

                    Log.v(TAG, "Chat Message full :- " + chatMessageList.get(position));

                    String messageCountString = DatabaseHelper.getInstance(TwoTab_Activity.this).getmsgCount("chat", chatMessageList.get(position).receiver);
                    Log.v(TAG, "messageCountString while click on chat list item :- " + messageCountString);

                    DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateMsgRead("3", chatMessageList.get(position).receiver);
                    AllBeans allBeans = new AllBeans();
                    allBeans.setFriendname(chatMessageList.get(position).reciverName);
                    allBeans.setFriendStatus(chatMessageList.get(position).userStatus);
                    allBeans.setFriendmobile(chatMessageList.get(position).receiver);
                    allBeans.setGroupName(chatMessageList.get(position).groupName);
                    allBeans.setGroupid(chatMessageList.get(position).groupid);
                    allBeans.setLanguages(chatMessageList.get(position).reciverlanguages);
                    allBeans.setFriendimage(chatMessageList.get(position).ReciverFriendImage);
                    allBeans.setGroupImage(chatMessageList.get(position).Groupimage);
                    allBeans.setFriendQB_id(chatMessageList.get(position).friend_QB_Id);

                    Log.v(TAG, "Quick Blox in chat list item 1 :- " + chatMessageList.get(position).friend_QB_Id);
//                Log.v(TAG, "Quick Blox in chat list item 2 :- " + allBeans.getFRIEND_Qb_Id());

                    ArrayList<AllBeans> beansArrayList = new ArrayList<AllBeans>();
                    beansArrayList.add(allBeans);
//                    getQBChatDialogByDialogID(chatMessageList.get(position).dialog_id);
                    Toast.makeText(getApplicationContext(), "Dialog id for group :- " + chatMessageList.get(position).dialog_id, Toast.LENGTH_SHORT).show();

                    Log.v(TAG, "QBChatDialog qbChatDialogBytes in bytes data for GROUP :- " + chatMessageList.get(position).qbChatDialogBytes);

                    byte[] chatbytes = chatMessageList.get(position).qbChatDialogBytes;

                    qbChatDialog = SerializationUtils.deserialize(chatbytes);

                    Log.v(TAG, "QB_CHAT_DIALOG after desierialized data Group :- " + qbChatDialog);

                    Intent intent = new Intent(TwoTab_Activity.this, ChatActivity.class);
//                    intent.setAction("GroupChatClick");
                    intent.putExtra("value", allBeans);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, qbChatDialog);
                    startActivityForResult(intent, REQUEST_DIALOG_ID_FOR_UPDATE);
                }

               /* Intent intent = new Intent(TwoTab_Activity.this, ChatActivity.class);
                intent.putExtra("value", beansArrayList.get(0));
                intent.putExtra("groupName", "");
//                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, "");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //  intent.putExtra("message", chatMessageList.get(position).body);
                startActivity(intent);*/
            }
        });

        chatlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                Log.v("BroadcastAdapter ", " position of list item :- " + chatMessageList.get(position));
                Log.v("BroadcastAdapter ", " Name of the friend in list :- " + chatMessageList.get(position).reciverName);
                Log.v("BroadcastAdapter ", " Number of the friend in list :- " + chatMessageList.get(position).receiver);

                AlertDialog.Builder builder = new AlertDialog.Builder(TwoTab_Activity.this);
                builder.setMessage("Delete chat with" + " \"" + chatMessageList.get(position).reciverName + "\" ? ")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

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
                adapter = new BroadcastnewgroupAdapter(instance, chatMessageList, messageCountTextView);


                chatlist.setAdapter(adapter);

                Log.d("chatMessageList", chatMessageList.toString());
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
                adapter = new BroadcastnewgroupAdapter(instance, chatMessageList, messageCountTextView);

                chatlist.setAdapter(adapter);

                Log.d("chatMessageList", chatMessageList.toString());
            }
        });
    }

    private void getQBChatDialogByDialogID(final String dialog_id, /*, final AllBeans allBeans*/ final int sender_QB_Id, final ChatMessage message) {

        QBRestChatService.getChatDialogById(dialog_id).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(final QBChatDialog res, Bundle bundle) {

                Log.v(TAG, "SQLITE QB group Chat dialog :- " + res);
                byte[] ser_QBByteData = SerializationUtils.serialize(res);

                Log.v(TAG, "Updating dialog into bytes :- " + ser_QBByteData);
//dsvvvvvvvvvvvvvvvvvvvvvvdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
//                DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateQBChatDialog(dialog_id, ser_QBByteData);
                message.qbChatDialogBytes = ser_QBByteData;
                DatabaseHelper.getInstance(TwoTab_Activity.this).insertChat(message);
                if (TwoTab_Activity.instance != null) {
                    TwoTab_Activity.updateList(message.groupName);
                }
/*
                qbChatDialog = res;

                Log.v(TAG, "mQbChatMessage is not null :- " + mQbChatMessage);
                if (mQbChatMessage != null) {
                    Log.v(TAG, "qbChatDialog is not null 1:- " + qbChatDialog);
                    try {
                        qbChatDialog.readMessage(mQbChatMessage);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(TwoTab_Activity.this, ChatActivity.class);
                intent.setAction("PrivateChatClick");
                intent.putExtra("value", allBeans);
                intent.putExtra("groupName", "");
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, qbChatDialog);
                startActivityForResult(intent, REQUEST_DIALOG_ID_FOR_UPDATE);*/
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error in getting QBChatDialog from dialod id " + e.getMessage());

            }
        });
    }

    private void setAppWalkThrough() {

        firsttab = (TextView) findViewById(R.id.broadcast);
        secondtab = (TextView) findViewById(R.id.newgroup);
        firsttab.setBackground(null);

//        secondtab.setTextColor(getResources().getColor(R.color.colorwhite));
//        findViewById(R.id.broadcast).setBackground(null);

        SharedPreferences sharedPref = this.getSharedPreferences("SEQUENCE_TAP_TARGET", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
//        getSupportActionBar().hide();

        try {
            sequence = new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.broadcast), "Chat List", " List of your active conversations")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .transparentTarget(true)
                                    .cancelable(false),

                            TapTarget.forView(findViewById(R.id.newgroup), "Group List", "List of your group conversations")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .transparentTarget(true)
                                    .cancelable(false),

                         /*   TapTarget.forView(findViewById(R.id.footer), "Navigation Bar", "You can access any of them instantly")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .targetCircleColor(R.color.colorPrimary)
                                    .transparentTarget(true)
                                    .cancelable(false),*/

                            TapTarget.forView(findViewById(R.id.iv_star), "Favorites", "List of starred contacts")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .cancelable(false),
/*
                            TapTarget.forView(findViewById(R.id.iv_chat_footer), "Main Chat window", "")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .cancelable(false),*/

                            TapTarget.forView(findViewById(R.id.iv_chatbluefooter), "Main Chat window", "")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .cancelable(false),

                            TapTarget.forView(findViewById(R.id.iv_user_footer), "TOTF", "Translate On the Fly ")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .cancelable(false),

                            /*TapTarget.forView(findViewById(R.id.iv_userbluefooter), "TOTF", "Translate the Fly On/ Off switch")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .cancelable(false),*/

                            TapTarget.forView(findViewById(R.id.iv_language), "Language Learner", "Translate words & phrases between two languages")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .cancelable(false),

                            TapTarget.forView(findViewById(R.id.iv_setting), "Settings", "Access your profile and prefrences")
                                    .outerCircleColor(R.color.colorPrimary)
                                    .cancelable(false)

                    ).listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {

                            findViewById(R.id.broadcast).setBackground(getResources().getDrawable(R.drawable.left_rounded_corner_bg_layout));
                            setMaterialTapTargetPromptContact();
                            editor.putBoolean("finished", true);
                            editor.commit();
                        }

                        @Override
                        public void onSequenceCanceled() {
                            findViewById(R.id.broadcast).setBackground(getResources().getDrawable(R.drawable.left_rounded_corner_bg_layout));
                            setMaterialTapTargetPromptContact();
                            editor.putBoolean("finished", true);
                            editor.commit();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isSequenceFinished = sharedPref.getBoolean("finished", false);

        if (!isSequenceFinished) {
            sequence.start();
        }
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
    }

    private void initIsTypingListener() {
//
//        // Create 'is typing' listener
//        //
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

        privateChatDialogTypingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer senderId) {
                Log.v(TAG, "user " + senderId + " is typing. Private dialog id: " + dialogId);
            }

            @Override
            public void processUserStopTyping(String dialogId, Integer senderId) {
                Log.v(TAG, "user " + senderId + " stop typing. Private dialog id: " + dialogId);
            }
        };
    }

    private void onGroupMessageReceived(ChatMessage chatMessage, String dialog_id) {

        Log.v(TAG, "10 :-" + chatMessage);

        chatMessage.isOtherMsg = 1;
        chatMessage.dialog_id = dialog_id;

        Log.v(TAG, "QB Serialize Group dialog to data " + chatMessage.qbChatDialogBytes);

        //groupNotification(chatMessage.groupName, chatMessage.body);
//        generateNofification(chatMessage);

        getQBChatDialogByDialogID(dialog_id, chatMessage);



/*
        //""+ inviter+" is joined"
        Message message1 = new Message();
        chatMessage.setMsgID();
        chatMessage.body = "loginUser" + " " + "joined this room!!";
        String body = gson.toJson(chatMessage);
        message1.setBody(body);
        message1.setType(Message.Type.groupchat);*/
    }

    private void getQBChatDialogByDialogID(String dialog_id, final ChatMessage chatMessage) {

        Log.v(TAG, " inside creating dialog when receiving group creation dialog :- " + dialog_id);

        QBRestChatService.getChatDialogById(dialog_id).performAsync(new QBEntityCallback<QBChatDialog>() {

            @Override
            public void onSuccess(final QBChatDialog res, Bundle bundle) {

                Log.v(TAG, "SQLITE QB group Chat dialog :- " + res);
                byte[] ser_QBByteData = SerializationUtils.serialize(res);

                Log.v(TAG, "Updating dialog into bytes :- " + ser_QBByteData);
//dsvvvvvvvvvvvvvvvvvvvvvvdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
//                DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateQBChatDialog(dialog_id, ser_QBByteData);

                chatMessage.qbChatDialogBytes = ser_QBByteData;
                qbService.createGroupWindows(chatMessage, TwoTab_Activity.this, " Two Tab while receiving created GROUP notification ...");
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error in getting QBChatDialog from dialod id " + e.getMessage());

            }
        });

    }

    private void initMessageStatusManagerAndListener() {

        try {
            QBChatService.getInstance().enableCarbons();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
//        messageStatusesManager = chatService.getMessageStatusesManager();

        messageStatusListener = new QBMessageStatusListener() {
            @Override
            public void processMessageDelivered(String messageId, String dialogId, Integer userId) {

//                DatabaseHelper.getInstance(ChatActivity.this).UpdateMsgRead("2", FriendMobile);

                String qbMessageId = messageId;
                String qbDialogId = dialogId;
                String qbRecipientId = userId.toString();

                DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateReadStatus("2", qbDialogId, qbMessageId, qbRecipientId);
                chatAdapter.notifyDataSetChanged();

                if (ChatActivity.instance != null) {
                    ChatActivity.instance.callAdapter();
                }

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
                Log.v(TAG, "NorrisTestQB :- " + "delivered messageid " + messageId + " dialogid " + dialogId + " userid :- " + userId);
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

                DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateReadStatus("3", qbDialogId, qbMessageId, qbRecipientId);
//                chatAdapter.notifyDataSetChanged();
                if (ChatActivity.instance != null) {
                    ChatActivity.instance.callAdapter();
                }
            }
        };
        messageStatusesManager.addMessageStatusListener(messageStatusListener);
    }

    private void initRoster(QBChatService chatService) {

        сhatRoster = chatService.getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
        сhatRoster.addRosterListener(rosterListener);
    }


    private void initRosterListener() {

        rosterListener = new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {
                Log.v(TAG, "entriesDeleted: " + userIds);
            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {
                Log.v(TAG, "entriesAdded: " + userIds);
            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {
                Log.v(TAG, "entriesUpdated: " + userIds);
            }

            @Override
            public void presenceChanged(QBPresence presence) {

                String lastSeen;
                Log.v(TAG, "presence :- QB presenceChanged :- " + presence);

                Log.v(TAG, "QB presence status presence chages getType:- " + presence.getType());
                Log.v(TAG, "QB presence status presence chages getUserId:- " + presence.getUserId());

                String statusOnline = presence.getType().toString();

                if (statusOnline.equalsIgnoreCase("online")) {

                    Log.v(TAG, "QB status online :- " + statusOnline);

                    User user = new User();
                    user.setFriend_id(presence.getUserId());
                    user.setStatus("Online");
                    DatabaseHelper.getInstance(TwoTab_Activity.this).InsertStatus(user);
                    lastSeen = DatabaseHelper.getInstance(TwoTab_Activity.this).getLastSeenQB(user.getFriend_id());
                    Log.v(TAG, "QB status online from database 111111111 :- " + DatabaseHelper.getInstance(TwoTab_Activity.this).getLastSeenQB(user.getFriend_id()));
//                    ChatActivity.status.setText(lastSeen);

                } else if (statusOnline.equalsIgnoreCase("offline")) {

                    Log.v(TAG, "QB status offline :- " + statusOnline);

                    User user = new User();
                    user.setFriend_id(presence.getUserId());

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
                    DatabaseHelper.getInstance(TwoTab_Activity.this).InsertStatus(user);

                    Log.v(TAG, "QB status online from database 2222222222222222 :- " + DatabaseHelper.getInstance(TwoTab_Activity.this).getLastSeenQB(user.getFriend_id()));

                    lastSeen = DatabaseHelper.getInstance(TwoTab_Activity.this).getLastSeenQB(user.getFriend_id());
//                    ChatActivity.status.setText(lastSeen);
                }

                // TODO  : get image when any other user changes profile picture
                if (presence.getStatus() == null || presence.getStatus().equalsIgnoreCase("null")) {

                    Log.v(TAG, "If conditon true for the presence in null " + presence.getStatus());

                } else {
                    Log.v(TAG, "Else conditon true for the presence in not null " + presence.getStatus());
                    Log.v(TAG, "ELSE QB Chat Message systemMessageCreatingDialog tis is spp :- " + presence);

                    /* {"chat_Type":"singleChat", "status":"Can't talk SpeakAme Only" ,"profile_name":"Ravi","profile_language":"",
                    "profile_image":"http:\/\/fxpips.co.uk\/SpeakAme\/user\/images\/5a3a4b4712544.png", "sender_id":155}*/

                    int QbUserID = presence.getUserId();

                    try {
                        JSONObject jsonObject = new JSONObject(presence.getStatus());
                        String profile_image = jsonObject.getString("profile_image");
                        String status = jsonObject.getString("status");
                        String profile_name = jsonObject.getString("profile_name");
                        String chat_Type = jsonObject.getString("chat_Type");

                        Log.v(TAG, "PresenceSendService :- " + " profile image for friends" + profile_image);
                        DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateUserImage(presence.getStatus(), String.valueOf(QbUserID));

                        if (TwoTab_Activity.instance != null) {
                            if (chat_Type.equalsIgnoreCase("singleChat"))
                                TwoTab_Activity.updateList("");
                            else if (chat_Type.equalsIgnoreCase("Group")) {
                                TwoTab_Activity.updateList("Group");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void initSubscriptionListener() {

        subscriptionListener = new QBSubscriptionListener() {
            @Override
            public void subscriptionRequested(int userId) {
                Log.v(TAG, "subscriptionRequested: " + userId);
            }
        };
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

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            Log.v(TAG, "Inside systemMessagesManager 1");
            Log.v(TAG, "Inside systemMessagesManager 1 newww " + qbChatMessage);

            Log.v(TAG, "Inside message incoming listener 1");

            Log.v(TAG, "Message body Receive 1:- " + qbChatMessage.getBody());
            Log.v(TAG, "2. :-" + qbChatMessage.getDialogId());
            Log.v(TAG, "3. :-" + qbChatMessage.getRecipientId());
            Log.v(TAG, "4. :-" + qbChatMessage.getSenderId());
            Log.v(TAG, "5. :-" + qbChatMessage.getSmackMessage());
            Log.v(TAG, "6. :-" + qbChatMessage.getProperties());

            String dialog_type = qbChatMessage.getProperties().get("dialog_type");

            Log.v(TAG, "7. :-" + dialog_type);
//            if (dialog_type))
            /*
            *  PRIVATE ---- 3
            *  GROUP   ---- 2
            * */

            if (dialog_type.equalsIgnoreCase("2")) {   // if dialog type not == PRIVATE

                final ChatMessage chatMessage = gson.fromJson(qbChatMessage.getBody(), ChatMessage.class);
                Log.v(TAG, "8. :-" + chatMessage);

                onGroupMessageReceived(chatMessage, qbChatMessage.getDialogId());

                Log.v(TAG, "9. :-" + chatMessage);

            }

            dialogsManager.onSystemMessageReceived(qbChatMessage);
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {

            Log.e(TAG, "Error in getting system group message :-" + e.getMessage());
        }
    }

    private class AllDialogsMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(final String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {
            if (!senderId.equals(ChatHelper.getCurrentUser().getId())) {
                dialogsManager.onGlobalMessageReceived(dialogId, qbChatMessage);
            }
        }
    }

}
