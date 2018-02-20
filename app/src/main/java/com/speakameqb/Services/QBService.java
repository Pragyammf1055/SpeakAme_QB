package com.speakameqb.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.model.QBUser;
import com.speakameqb.Activity.ChatActivity;
import com.speakameqb.Activity.TwoTab_Activity;
import com.speakameqb.Adapter.BroadcastnewgroupAdapter;
import com.speakameqb.AppController;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Beans.User;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.QuickBlox.ChatHelper;
import com.speakameqb.QuickBlox.DialogsManager;
import com.speakameqb.QuickBlox.QbChatDialogMessageListenerImp;
import com.speakameqb.R;
import com.speakameqb.Xmpp.ChatMessage;
import com.speakameqb.Xmpp.CommonMethods;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Contactloader.Contact;
import com.speakameqb.utils.Contactloader.ContactFetcher;
import com.speakameqb.utils.Contactloader.ContactPhone;
import com.speakameqb.utils.DownloadFile;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.ListCountry;
import com.speakameqb.utils.TextTranslater;
import com.speakameqb.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.speakameqb.Activity.ChatActivity.chatAdapter;
import static com.speakameqb.Activity.ChatActivity.conversationimage;
import static com.speakameqb.Activity.ChatActivity.groupName;
import static com.speakameqb.utils.Function.mediaScanner;

/**
 * Created by Peter on 03-Nov-17.
 */

public class QBService extends Service implements DialogsManager.ManagingDialogsCallbacks, QBSubscriptionListener, QBRosterListener, QBChatDialogTypingListener
        , QBChatDialogMessageSentListener, QBChatDialogMessageListener {

    public static final String PROPERTY_OCCUPANTS_IDS = "occupants_ids";
    public static final String PROPERTY_DIALOG_TYPE = "dialog_type";
    public static final String PROPERTY_DIALOG_NAME = "dialog_name";
    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    private static final String TAG = "QBService_";
    public static int numMessages = 0;
    public static Random random;
    public static QBRoster сhatRoster;
    public static BroadcastnewgroupAdapter adapter;
    QBChatService chatService;
    DialogsManager dialogsManager;
    int Count = 0;
    boolean groupCreated = false;
    List<HashMap<String, String>> userMsgList = new ArrayList<HashMap<String, String>>();
    String grpReceiver;
    Bitmap bitmap;
    Gson gson;
    StringifyArrayList<Integer> userIds;
    QBChatMessage mQbChatMessage;
    QBChatDialog qbChatDialog;
    TimeZone timezone;
    private QBChatDialog privateChatDialog, groupChatDialog;
    private QBChatDialogTypingListener privateChatDialogTypingListener, groupChatDialogTypingListener;
    private QBChatDialogMessageSentListener privateChatDialogMessageSentListener, groupChatDialogMessageSentListener;
    private QBChatDialogMessageListener privateChatDialogMessageListener;
    private QBChatDialogMessageListener groupChatDialogMessageListener;
    private QBIncomingMessagesManager incomingMessagesManager;
    /*
        @Override
        protected void onHandleIntent(@Nullable Intent intent) {

        }*/
    private QBSystemMessagesManager systemMessagesManager;
    private SystemMessagesListener systemMessagesListener;
    private QBChatDialogMessageListener allDialogsMessagesListener;
    private String ImageStringUrl = "";


    public QBService() {
    }

    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public static void start(Context context, QBUser qbUser, PendingIntent pendingIntent) {
        Intent intent = new Intent(context, QBService.class);
/*
        intent.putExtra(Consts.EXTRA_COMMAND_TO_SERVICE, Consts.COMMAND_LOGIN);
        intent.putExtra(Consts.EXTRA_QB_USER, qbUser);
        intent.putExtra(Consts.EXTRA_PENDING_INTENT, pendingIntent);*/

        context.startService(intent);
    }

    public static void start(Context context, Intent intent1) {

        Log.v(TAG, "ONstart service Class 123");
        Intent intent = new Intent(context, QBService.class);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.w(TAG, "~~~~~~~~~~~~~~~~~~~~~ ON CREATE ~~~~~~~~~~~~~~~~~~~~~ ");
        Count++;

        dialogsManager = new DialogsManager();
        Log.v(TAG, "Inside on create QB Service !!!!!!!! ------ " + Count + " ````````");
        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code started on 15 February 2018 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

        initChatService();
        chatLogin();

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code started on 15 February 2018 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    }

    private void initListener() {

        allDialogsMessagesListener = new AllDialogsMessageListener();
        systemMessagesListener = new SystemMessagesListener();
        dialogsManager = new DialogsManager();
        gson = new Gson();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "Inside onStartCommand !!!!!!!!");

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code started on 15 February 2018 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        initChatService();
        Log.v(TAG, "Service chatService.isLoggedIn() :- " + chatService.isLoggedIn());
        Log.v(TAG, "Service ! chatService.isLoggedIn() :- " + !chatService.isLoggedIn());
        if (!chatService.isLoggedIn()) {
            chatLogin();
        }

        adapter = new BroadcastnewgroupAdapter();
        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code started on 15 February 2018 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

        return Service.START_STICKY;
    }

    public void initChatService() {

        Log.w(TAG, "~~~~~~~~~~~~~~~~~~~~~ inside initChatService() ~~~~~~~~~~~~~~~~~~~~~ ");

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
//        chatService.startAutoSendPresence(10);

    }

    private void processGroupMessage(final ChatMessage message, String qbMsgId, String dialogId) {

        TimeZone timezone = TimeZone.getDefault();
        TimeZone timeZomeSender = TimeZone.getTimeZone(message.timeZone);
        SimpleDateFormat dateFormat_sender = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        dateFormat_sender.setTimeZone(timeZomeSender);
        Calendar cal_sender = Calendar.getInstance(TimeZone.getTimeZone(message.timeZone));
        cal_sender.setTimeInMillis(message.dateInLong);
        Date date_sender = cal_sender.getTime();

        Log.v(TAG, "TimeZONE Sender :- " + dateFormat_sender.format(date_sender));
        Log.v(TAG, "Time 2 :- " + date_sender.getTime());

        Calendar calander_receiver = Calendar.getInstance(timezone);
        SimpleDateFormat sdf_receiver = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        sdf_receiver.setTimeZone(timezone);
        Date date_receiver = calander_receiver.getTime();
        Log.v(TAG, "TimeZONE Receiver current:- " + sdf_receiver.format(date_receiver));

        calander_receiver.setTimeInMillis(message.dateInLong);

        Date date_receiver_send = calander_receiver.getTime();
        Log.v(TAG, "TimeZONE Receiver sender :- " + sdf_receiver.format(date_receiver_send));

        String current_time = sdf_receiver.format(date_receiver_send);
        String time[] = current_time.toString().split(" ");
        String send_time = time[1];

        Log.v(TAG, "Sender time :- " + send_time + time[2]);

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

        message.Date = time[0];
        message.Time = send_time + " " + time[2];

        message.msgStatus = "10";
        DatabaseHelper helper = new DatabaseHelper(QBService.this);
        String msgId = helper.getMsgId(message.msgid);

        Log.v(TAG, "inside IsmInestatus 22 :- " + message.isMine);
        //DatabaseHelper.getInstance(context).getMsgId(chatMessage.msgid);

        message.qbChatDialogBytes = DatabaseHelper.getInstance(QBService.this).getChatDialogUsingDialogID(dialogId);

        if (AppPreferences.getMobileuser(QBService.this).equalsIgnoreCase(message.sender)) {
            Log.v(TAG, " getMobileuser 11:- " + AppPreferences.getMobileuser(QBService.this) + " and " + message.sender);

        } else {
            Log.v(TAG, "QB Serialize Chat dialog in Bytes Group 1 :- " + message.qbChatDialogBytes);
            DatabaseHelper.getInstance(QBService.this).insertChat(message);
            Log.v(TAG, " getMobileuser 22:- " + AppPreferences.getMobileuser(QBService.this) + " and " + message.sender);
//vfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffv
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
//                        Toast.makeText(getApplicationContext(), "Total Message Count chatinstance :- " + chatMessageCount, Toast.LENGTH_SHORT).show();

                        Log.d(TAG, " Group Messaggg :- " + message.sender);
                        Log.v(TAG, " Group message Name :- " + ChatActivity.FriendMobileTWO);
                        Log.v(TAG, " Group message Name  vvvsvs :- " + ChatActivity.groupName);

                        if (message.body.contains("Image changed by")) {
                            Picasso.with(QBService.this).load(message.Groupimage).error(R.drawable.profile_default)
                                    .resize(200, 200)
                                    .into(conversationimage);
                        } else if (message.body.contains("name changed by")) {
                            TwoTab_Activity.toolbartext.setText(message.groupName);
                            groupName = message.groupName;
                        }
                        Log.v(TAG, " getMobileuser :- " + AppPreferences.getMobileuser(QBService.this) + " and " + message.sender);
                        Log.v(TAG, " getMobileUserWithoutCountry :- " + AppPreferences.getMobileUserWithoutCountry(QBService.this) + " and " + message.sender);

                        DatabaseHelper.getInstance(QBService.this).UpdateMsgRead("1", message.sender);

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
                            MediaPlayer mp = MediaPlayer.create(QBService.this, R.raw.steamchat);
                            if (AppPreferences.getConvertTone(QBService.this).equalsIgnoreCase("false")) {
                                mp.stop();
                            } else {
                                mp.start();
                            }
                        } else {
//                            generateNofification(chatMessage);
                            Log.v(TAG, " Inside message.files ChatActivity BAhar 00" + message.files);

                            if (!message.files.equalsIgnoreCase("")) {
                                downLoadFile(message);
//                                Log.v(TAG, " Inside message.files ChatActivity BAhar  11" + message.files);
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
                        Log.d(TAG, "IFTwoTab_Activity.... Mess Group   " + message.groupName + "  : " + message);
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

    private void processMessages(final ChatMessage chatMessage, String msgId, final String dialogId, final QBChatMessage qbChatMessage) {


        TimeZone timeZomeSender = TimeZone.getTimeZone(chatMessage.timeZone);
        SimpleDateFormat dateFormat_sender = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        dateFormat_sender.setTimeZone(timeZomeSender);
        Calendar cal_sender = Calendar.getInstance(TimeZone.getTimeZone(chatMessage.timeZone));
        cal_sender.setTimeInMillis(chatMessage.dateInLong);
        Date date_sender = cal_sender.getTime();
        Log.v(TAG, "TimeZONE Sender :- " + dateFormat_sender.format(date_sender));
        Log.v(TAG, "Time 2 :- " + date_sender.getTime());

        TimeZone timezone = TimeZone.getDefault();
        Log.v(TAG, "Receiver's Time Zone :- " + chatMessage.timeZone);

        Calendar calander_receiver = Calendar.getInstance(timezone);
        SimpleDateFormat sdf_receiver = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        sdf_receiver.setTimeZone(timezone);
        Date date_receiver = calander_receiver.getTime();
        Log.v(TAG, "TimeZONE Receiver current :- " + sdf_receiver.format(date_receiver));

        calander_receiver.setTimeInMillis(chatMessage.dateInLong);

        Date date_receiver_send = calander_receiver.getTime();
        Log.v(TAG, "TimeZONE Receiver sender :- " + sdf_receiver.format(date_receiver_send));

        String current_time = sdf_receiver.format(date_receiver_send);
        String time[] = current_time.toString().split(" ");
        String send_time = time[1];

        Log.v(TAG, "Sender time :- " + send_time + time[2]);

        Log.v(TAG, "Getting Difference in timezone 1 (Date sent) :- date in long :- 123 --- " + calander_receiver.getTime());

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
//          fileUrl = QBFile.getPrivateUrlForUID(chatMessage.qbFileUid);         // need to comment this line for normal message
            Log.v(TAG, "inside incoming mesage processMessage() File Url 54:- " + fileUrl);
            chatMessage.files = fileUrl;

        }

        chatMessage.isMine = false;
        // String msgId = DatabaseHelper.getInstance(context).getMs gId(chatMessage.msgid);

        final ChatMessage message = new ChatMessage(chatMessage.receiver, chatMessage.reciverName,
                chatMessage.sender, getContactName(chatMessage.sender), chatMessage.groupName, chatMessage.body, chatMessage.msgid, chatMessage.files, chatMessage.isMine);
        //message.Date = chatMessage.Date;
        message.fileName = chatMessage.fileName;

        message.Date = time[0];
        message.Time = send_time + " " + time[2];
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

        // message.isOtherMsg = 1;
//      message.qbChatDialogBytes =  chatMessage.qbChatDialogBytes;

        Log.v(TAG, "Inside Process messasge from personal chat :- " + message);

        Log.v(TAG, "SQLITE Data insertion while receiving messages :- " + message.sender);               // 919630588122
        Log.v(TAG, "1 :- " + message.senderName);               // Sheetal
        Log.v(TAG, "2 :- " + message.receiver);               // 919074900690
        Log.v(TAG, "3 :- " + message.reciverName);               // Pra

//        getQBChatDialogByDialogID(dialogId, chatMessage.sender_QB_Id, message, chatMessage.sender);

        if (ChatActivity.instance != null && ChatActivity.FriendMobileTWO.equalsIgnoreCase(message.receiver)) {
            message.isRead = 3;
            Log.d(TAG, " chat isns  fjfj   " + " conditono is true ");
        }
        message.qbChatDialogBytes = DatabaseHelper.getInstance(QBService.this).getChatDialogUsingDialogID(dialogId);
        //--------------------------------------------------------------------------------//
        Log.d(TAG, "ChatMessage save inside QB message send 2222New :- " + message.toString());
        DatabaseHelper.getInstance(QBService.this).insertChat(message);
      /*  if (TwoTab_Activity.instance != null) {
            TwoTab_Activity.updateList(message.groupName);
        }*/

      /*  if (ChatActivity.instance != null) {
            if (ChatActivity.FriendMobileTWO.equalsIgnoreCase(sender)) {

            }
        }
        */

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

                    if (ChatActivity.FriendMobileTWO.equalsIgnoreCase(chatMessage.sender)) {

                        if (mQbChatMessage != null && qbChatDialog != null) {
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

                            // This condition is called when a user sends meesage to another user and his chat window is not opened..

                        /* Suppose user A is sending meesage or image to User B and B has not opened chat wondow of A.. then
                        * this condition is working.
                        * Here image will be downloaded.
                        * */

//                            Toast.makeText(getApplicationContext(), "Toast where 6 bookmark is defined...", Toast.LENGTH_SHORT).show();
//                            downLoadFile(message);
                        }

                        byte[] chatbytes = DatabaseHelper.getInstance(QBService.this).getChatDialogUsingDialogID(dialogId);

                        qbChatDialog = SerializationUtils.deserialize(chatbytes);
                        Log.v(TAG, "18 JAN 2018 :- " + qbChatDialog);

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

//                            Toast.makeText(getApplicationContext(), "Toast where 7 bookmark is defined...", Toast.LENGTH_SHORT).show();
                            downLoadFile(message);
                        }
                        TwoTab_Activity.updateList(chatMessage.groupName);
                    }

                } else {
//                    generateNofification(message);
                    if (!message.files.equalsIgnoreCase("")) {
                        // This condition is called when a user sends meesage to another user and his chat window is not opened..

                        /* Suppose user A is sending meesage or image to User B and B has not opened chat wondow of A.. then
                        * this condition is working.
                        * Here image will be downloaded.
                        * */

//                        Toast.makeText(getApplicationContext(), "Toast where 8 bookmark is defined...", Toast.LENGTH_SHORT).show();
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

        new DownloadFile(QBService.this, new ImageView(QBService.this), new ProgressBar(QBService.this), new VolleyCallback() {
            @Override
            public void backResponse(String response) {
//dcsssssssssss
                Log.v(TAG, "Downloaded file path / response :- " + response);
//                message.files =response;
                if (response.equalsIgnoreCase("11")) {
//                    message.files =response;
                    DatabaseHelper.getInstance(QBService.this).UpdateMsgStatus(response, message.msgid);
                } else {
//                    message.files =response;
                    DatabaseHelper.getInstance(QBService.this).UpdateMsgStatus("12", message.msgid);
                    DatabaseHelper.getInstance(QBService.this).UpdateFileName(response, message.msgid);
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

    private void getQBChatDialogByDialogID(final String dialog_id, final int sender_QB_Id /*, final ChatMessage message, final String sender*/) {

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Created while receiving message from Personal Cshat ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        QBRestChatService.getChatDialogById(dialog_id).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(final QBChatDialog res, Bundle bundle) {

                Log.v(TAG, "SQLITE QB group Chat dialog :- " + res);
                byte[] ser_QBByteData = SerializationUtils.serialize(res);

                Log.v(TAG, "Updating dialog into bytes :- " + ser_QBByteData);
                Log.d(TAG, " conditono is true " + ChatActivity.FriendMobileTWO + " : " /* + message.receiver*/);

                DatabaseHelper.getInstance(QBService.this).insertQbIdQbChatPrivateDialoge(sender_QB_Id, dialog_id, ser_QBByteData, "Private");

             /* List<ChatMessage> chatMessageList = DatabaseHelper.getInstance(instance).getReciever();
                for (ChatMessage chatMessage : chatMessageList){
                    if (chatMessage.sender.equalsIgnoreCase(message.sender) && chatMessage.Time.equalsIgnoreCase())
                }
                Date='16/01/2018', Time='04:24 pm'
                message.Time = 1 ;
xxxxxxxxxxxx*/
/*
                DatabaseHelper.getInstance(TwoTab_Activity.this).insertQbIdQbChatPrivateDialoge(sender_QB_Id, dialog_id, ser_QBByteData, "Private");

                if (ChatActivity.instance != null && ChatActivity.FriendMobileTWO.equalsIgnoreCase(message.receiver)) {
                    message.isRead = 3;
                    Log.d(TAG, " chat isns  fjfj   " + " conditono is true ");
                }
                message.qbChatDialogBytes = ser_QBByteData;
                //--------------------------------------------------------------------------------//
                Log.d(TAG, "ChatMessage save inside QB message send 2222New :- " + message.toString());
                DatabaseHelper.getInstance(TwoTab_Activity.this).insertChat(message);
                if (TwoTab_Activity.instance != null) {
                    TwoTab_Activity.updateList(message.groupName);
                }

                if (ChatActivity.instance != null) {
                    if (ChatActivity.FriendMobileTWO.equalsIgnoreCase(sender)) {
                        if (mQbChatMessage != null && qbChatDialog != null  ) {
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
                    }
                }*/
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error in getting QBChatDialog from dialod id " + e.getMessage());

            }
        });
    }

    private void incomingMessage() {
        Log.v(TAG, "incomingMessage method :- " + "incomingMessage");

        final HashMap<String, QBChatDialog> opponentsDialogMap = new HashMap<>();

        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {

                getQBChatDialogByDialogID(dialogId, senderId /*, message, chatMessage.sender*/);

                TimeZone timezone = TimeZone.getDefault();

                String TimeZoneName = timezone.getDisplayName();

                int TimeZoneOffset = timezone.getRawOffset() / (60 * 60 * 1000);

                Log.v(TAG, "TimeZoneName :- " + TimeZoneName);
                Log.v(TAG, "TimeZoneOffset :- " + TimeZoneOffset);

                Log.v(TAG, "Inside incomming message listener");

                Log.v(TAG, "Message body Receive :- " + qbChatMessage.getBody());
                Log.v(TAG, "2 Dialog id :- " + qbChatMessage.getDialogId());
                Log.v(TAG, "2-1 Message id :- " + qbChatMessage.getId());
                Log.v(TAG, "3. :- " + qbChatMessage.getRecipientId());
                Log.v(TAG, "4. :- " + qbChatMessage.getSenderId());
                Log.v(TAG, "5. :- " + qbChatMessage.getSmackMessage());
                Log.v(TAG, "6. :- " + qbChatMessage.getDateSent());

                final String mDialogId = qbChatMessage.getDialogId();
                final String mMessageId = qbChatMessage.getId();

                final ChatMessage chatMessage = gson.fromJson(qbChatMessage.getProperties().get("custom_body"), ChatMessage.class);

                Log.v(TAG, "Dialog in bytes from receiving message after conversation :- " + chatMessage.qbChatDialogBytes);
                Log.v(TAG, "Getting Difference in timezone 1 (Date sent) :- " + chatMessage.dateInLong);
                Log.v(TAG, "Getting timeZone in timezone :- " + chatMessage.timeZone);

                if (chatMessage.groupName.equalsIgnoreCase("")) {

                    Log.v(TAG, "for private message listener");
                    mQbChatMessage = qbChatMessage;
//                  asccccccccccccccccccccccccccccccccccccccc
                    Log.v(TAG, "mQbChatMessage is not null 1:- " + mQbChatMessage);

                    final String sender_lang = chatMessage.senderlanguages;
                    final String my_language = AppPreferences.getUSERLANGUAGE(TwoTab_Activity.instance);
                    ListCountry country = new ListCountry();
                    String sorcountrycode = country.getCode(QBService.this, sender_lang.trim());
                    if (sorcountrycode.equalsIgnoreCase("")) {
                        sorcountrycode = "en";
                    }
                    String descountrycode = country.getCode(QBService.this, my_language.trim());
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
                            TextTranslater.getInstance().translate(QBService.this, sorcountrycode, descountrycode, chatMessage.body, new VolleyCallback() {
                                @Override
                                public void backResponse(String response) {
                                    if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(QBService.this).equalsIgnoreCase("1")) {
                                        // if (!langu.equalsIgnoreCase(Mylangu)) {

                                        if (chatMessage.Contact.equalsIgnoreCase("")) {
                                            chatMessage.body = (chatMessage.body + "~" + sender_lang + "~" + response);
                                        }

                                        Log.d(TAG, "TOTF MESSAGE Mesasage after translation 1:-  " + chatMessage.body);
                                        Log.d(TAG, "TOTF MESSAGE Inside TOTF enabled.. ");
                                        //}
                                    } else if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(QBService.this).equalsIgnoreCase("0")) {

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

    private void initRoster(QBChatService chatService) {

        сhatRoster = chatService.getRoster(QBRoster.SubscriptionMode.mutual, new QBSubscriptionListener() {
            @Override
            public void subscriptionRequested(int userId) {

                Log.v(TAG, "inside initRoster subscriptionRequested :- " + userId);
                confirmSubscription(userId);
            }
        });

        сhatRoster.addRosterListener(new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> collection) {

            }

            @Override
            public void entriesAdded(Collection<Integer> collection) {

            }

            @Override
            public void entriesUpdated(Collection<Integer> collection) {

            }

            @Override
            public void presenceChanged(QBPresence presence) {

                Log.v(TAG, "presence :- QB presenceChanged :- " + presence);

                Log.v(TAG, "QB presence status presence chages getType:- " + presence.getType());
                Log.v(TAG, "QB presence status presence chages getUserId:- " + presence.getUserId());

                String statusOnline = presence.getType().toString();

                if (statusOnline.equalsIgnoreCase("online")) {

                    Log.v(TAG, "QB status online :- " + statusOnline);

                    User user = new User();
                    user.setFriend_id(presence.getUserId());
                    user.setStatus("Online");
                    DatabaseHelper.getInstance(QBService.this).InsertStatus(user);
//            lastSeen = DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id());
                    Log.v(TAG, "QB status online from database 111111111 :- " + DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id()));
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
                    DatabaseHelper.getInstance(QBService.this).InsertStatus(user);

                    Log.v(TAG, "QB status online from database 2222222222222222 :- " + DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id()));

//            lastSeen = DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id());
                }

                // TODO  : get image when any other user changes profile picture
                if (presence.getStatus() == null || presence.getStatus().equalsIgnoreCase("null")) {
//DVSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
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
                        //   DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateUserImage(presence.getStatus(), String.valueOf(QbUserID));
                        //**************************************************************************************************//
                        DatabaseHelper.getInstance(QBService.this).UpdateFriendPro(profile_image, status, String.valueOf(QbUserID));
                        String userImage = DatabaseHelper.getUsetImage(QbUserID);

                        if (!userImage.equalsIgnoreCase("")) {
                            Log.d(TAG, " Friend Image get from DataBase : - " + userImage);
                            adapter.notifyDataSetChanged();
                            if (ChatActivity.instance != null) {
                                Picasso.with(QBService.this).load(userImage).error(R.drawable.profile_default)
                                        .resize(200, 200)
                                        .into(conversationimage);
                            }

                        }
                        //**************************************************************************************************//

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
        });
    }

    public void chatLogin() {

        Log.w(TAG, "~~~~~~~~~~~~~~~~~~~~~ inside chatLogin() ~~~~~~~~~~~~~~~~~~~~~ ");

        final QBUser user = new QBUser(AppPreferences.getQB_LoginId(this), "12345678");
        user.setId(AppPreferences.getQBUserId(this));

        Log.v(TAG, "loggginnn Login to chat service done :- " + user);

        chatService.login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                Log.v(TAG, "Login to chat service done ");

                incomingMessage();
                initRoster(chatService);
                QBSubscriptiondevice();
                initListener();
                registerQbChatListeners();
                initPrivateChatMessageListener();
                initGroupChatMessageListener();
                initIsTypingListener();
                initMessageSentListener();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error while login to chat service ");
                Log.v(TAG, "Error chat serviev :-  " + e.getMessage());

            }
        });
    }

    private void initGroupChatMessageListener() {


        allDialogsMessagesListener = new AllDialogsMessageListener();
        systemMessagesListener = new SystemMessagesListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Service Stopped !!! ");
        Log.v(TAG, "on destroy called :- TwoTab ");
        Log.v(TAG, "User is loggin to chat or not :- " + QBChatService.getInstance().isLoggedIn());
        Log.v(TAG, "User is loggin to chat or not :- " + chatService.isLoggedIn());

//        unregisterQbChatListeners();
        if (QBChatService.getInstance().isLoggedIn()) {
            Log.v(TAG, "on destroy called :- TwoTab ");
            chatLogout(chatService);
        }
    }

    private void chatLogout(QBChatService chatService) {

        chatService.logout(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                Log.v(TAG, "Logged out from chat success fully !!!");

            }

            @Override
            public void onError(QBResponseException e) {

                Log.e(TAG, "Error while logging out from s:- " + e.getMessage());

            }
        });

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public void sendIsTypingInPrivateChat(QBChatDialog privateChatDialog, QBChatDialogTypingListener privateChatDialogTypingListener) {
        Log.v(TAG, "Inside QB Service send is typing !!!!!!!!");
        Log.v(TAG, "Inside QB Service privateChatDialog " + privateChatDialog);

        privateChatDialog.addIsTypingListener(privateChatDialogTypingListener);

        try {
            privateChatDialog.sendIsTypingNotification();
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.v(TAG, "send typing error: " + e.getClass().getSimpleName());
        }
    }

    public void initIsTypingListener() {
//        // Create 'is typing' listener

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
    }

    public void initPrivateChatMessageListener() {
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

    public void initMessageSentListener() {

        privateChatDialogMessageSentListener = new QBChatDialogMessageSentListener() {
            @Override
            public void processMessageSent(String dialogId, QBChatMessage qbChatMessage) {

                Log.v(TAG, " QB CHat Message :- " + qbChatMessage);
                Log.v(TAG, "message " + qbChatMessage.getId() + " sent to dialog " + dialogId);

            }

            @Override
            public void processMessageFailed(String dialogId, QBChatMessage qbChatMessage) {
                Log.v(TAG, "send message " + qbChatMessage.getId() + " has failed to dialog " + dialogId);
            }
        };

        groupChatDialogMessageSentListener = new QBChatDialogMessageSentListener() {
            @Override
            public void processMessageSent(String dialogId, QBChatMessage qbChatMessage) {
                Log.v(TAG, "message " + qbChatMessage.getId() + " sent to dialog " + dialogId);
            }

            @Override
            public void processMessageFailed(String dialogId, QBChatMessage qbChatMessage) {
                Log.v(TAG, "send message " + qbChatMessage.getId() + " has failed to dialog " + dialogId);
            }
        };
    }

    public void registerQbChatListeners() {

        initListener();
        Log.v(TAG, "Inside registerQbChatListeners");
        dialogsManager = new DialogsManager();
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        if (incomingMessagesManager != null) {
            incomingMessagesManager.addDialogMessageListener(allDialogsMessagesListener != null
                    ? allDialogsMessagesListener : new AllDialogsMessageListener());
        }

        if (systemMessagesManager != null) {

            Log.v(TAG, "Inside systemMessagesManager ");
            systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
                    ? systemMessagesListener : new SystemMessagesListener());
        }

        dialogsManager.addManagingDialogsCallbackListener(this);
    }

    public void initChatMessageDatabase(Context context, String groupName, String groupId, String groupImagePicture, QBChatDialog groupChatDialog) {

        timezone = TimeZone.getDefault();
        List<Integer> occupantIdsList = groupChatDialog.getOccupants();

        userIds = new StringifyArrayList<>();
        userIds.add(QBSessionManager.getInstance().getSessionParameters().getUserId());
        Log.v(TAG, "QBSessionManager......................" + QBSessionManager.getInstance().getSessionParameters().getUserId());
        userIds.addAll(occupantIdsList);

        registerQbChatListeners();

        random = new Random();
        String chatRoomId1 = getOnlyStrings(groupName.trim());
        // Get a MultiUserChat using MultiUserChatManager

        String chatRoomId = chatRoomId1.replace(" ", "_");
        Log.d("groupchatName  service", ">>" + chatRoomId1 + " ::::" + chatRoomId);

        ChatMessage chatMessage = new ChatMessage(AppPreferences.getMobileuser(context),
                AppPreferences.getFirstUsername(context),
                chatRoomId, chatRoomId,
                groupName, "Hi welcome to " + groupName + " group", "" + random.nextInt(1000), "", true);
        chatMessage.setMsgID();
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.type = Message.Type.groupchat.name();
        chatMessage.groupid = groupId;
        chatMessage.Groupimage = groupImagePicture;
        chatMessage.isOtherMsg = 1;
        chatMessage.dialog_id = groupChatDialog.getDialogId();
        chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
        chatMessage.timeZone = timezone.getID();
        Log.d(TAG, "Groupimage  :- " + groupImagePicture);
        Log.d(TAG, "creating group dialog Chat Message Body  :- " + chatMessage.toString());
        String body = gson.toJson(chatMessage);
        Log.d(TAG, "creating group dialog Chat Message Body gson convertor  :- " + body);

        byte[] data = SerializationUtils.serialize(groupChatDialog);
        Log.v(TAG, "QB Serialize dialog to data :- " + data);
        QBChatDialog yourObject = SerializationUtils.deserialize(data);
        Log.v(TAG, "QB Serialize data to dialog :- " + yourObject);
        chatMessage.qbChatDialogBytes = data;
        chatMessage.qbChatDialogBytes = SerializationUtils.serialize(privateChatDialog);

        DatabaseHelper.getInstance(this).insertQbIdQbChatPrivateDialoge(0, groupChatDialog.getDialogId(), data, "Group");

//      vsdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd

        try {
            dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, groupChatDialog, context, body, chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception while creating dialog android :- " + e.getMessage());
        }
        createGroupWindows(chatMessage, context, " QBService while sending grp");
    }

    public void createGroupWindows(final ChatMessage chatMessage, final Context context, String msg) {

//dvsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssfdddddddddddddddddddddddddddddb

        Log.v(TAG, "Inside Create Group Window from :- " + msg);
        Log.v(TAG, "Chat message received :- " + chatMessage);
        Log.v(TAG, "QB Serialize data in bytes while creating Group :- " + chatMessage.qbChatDialogBytes);

        DatabaseHelper helper = new DatabaseHelper(context);
        String msgId = helper.getMsgId(chatMessage.msgid);

        if (msgId.equalsIgnoreCase("")) {

            /*
            ~~~~~~~~~~~~~~~
            final ChatMessage message = new ChatMessage(chatMessage.receiver, chatMessage.reciverName, chatMessage.sender, getContactName(chatMessage.sender),chatMessage.groupName, chatMessage.body, chatMessage.msgid,"", chatMessage.isMine);
            message.Date = chatMessage.Date;
            message.Time = chatMessage.Time;
            message.type =  Message.Type.groupchat.name();
            DatabaseHelper.getInstance(context).insertChat(message);
            ~~~~~~~~~~~~~~~
            */

            /*
//          bddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffffffffffffffffffffffffffffffffffffffff
            */

            DatabaseHelper.getInstance(context).insertChat(chatMessage);

            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    if (ChatActivity.instance != null) {
                        ChatActivity.chatlist.add(chatMessage);
                        ChatActivity.chatAdapter.notifyDataSetChanged();
                    }

                    if (TwoTab_Activity.instance != null) {
                        TwoTab_Activity.updateList(chatMessage.groupName);
                    }

                    if (TwoTab_Activity.instance == null && ChatActivity.instance == null) {
//                        generateNofification(chatMessage, context);
//                        generateNotificationToQB(userIds, chatMessage.groupName);
                    }

                }
            });
        }
    }

    private void generateNotificationToQB(StringifyArrayList<Integer> userIds, String groupName) {

        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
        qbEvent.setPushType(QBPushType.APNS_VOIP);
        qbEvent.setUserIds(userIds);

        JSONObject json = new JSONObject();
        try {
//             custom parameters
            json.put("status", "200");
            json.put("message", "New group " + "\"" + groupName + "\" " + "created");
        } catch (Exception e) {
            e.printStackTrace();
        }
        qbEvent.setMessage(json.toString());

        QBPushNotifications.createEvent(qbEvent).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                Log.v(TAG, "Push notification sent Successfully !!!!!!");
                Log.v(TAG, "onSuccess.............qbEvent" + qbEvent);
                Log.v(TAG, "onSuccess.............bundle" + bundle);
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "onError.............");
                Log.v(TAG, "onError sending Push notification :- " + e.getMessage());
            }
        });
    }

    public void generateNofification(ChatMessage message, Context context) {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put("user", message.reciverName);

        String msg;
        if (message.files.endsWith(".png") || message.files.endsWith(".jpg") || message.files.endsWith(".jpeg")) {
            msg = "Image";
        } else if (message.files.endsWith(".mp4") || message.files.endsWith(".3gp")) {
            msg = "Video";
        } else if (message.files.endsWith(".pdf")) {
            msg = "Document";
        } else {
            msg = message.body;
        }
        user.put("body", msg);
        userMsgList.add(user);

        String ringtoneName = AppPreferences.getNotificationRingtone(context);

        String ringtoneURI = AppPreferences.getNotificationRingtoneUri(context);

        Uri sound_notification = Uri.parse(ringtoneURI);

        String vibrationType = AppPreferences.getVibrationType(context);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        Log.v("MyXmpp", "Image Icon Bitmap urlll ImageStringUrl 1 :- " + ImageStringUrl);
        ImageStringUrl = message.ReciverFriendImage;
        Log.v("MyXmpp", "Image Icon Bitmap ImageStringUrl 2 :- " + ImageStringUrl);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

//        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
//bbdbfdbdb

        try {
            bitmap = new AsyncTask<String, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(String... params) {
                    InputStream in;

                    try {

                        URL url = new URL(ImageStringUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        in = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(in);
                        Log.v("MyXmpp", "Bitmap :- " + myBitmap);

                        Bitmap circleBitmap = Function.getCircleBitmap(myBitmap);

                        Log.v("MyXmpp", "Bitmap :- " + circleBitmap);
//                        return myBitmap;
                        return circleBitmap;

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            }.execute().get(1500, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

        } catch (TimeoutException e) {

            e.printStackTrace();
        }

        if (bitmap != null) {

            notificationBuilder.setSmallIcon(R.mipmap.app_icon);
            notificationBuilder.setLargeIcon(bitmap);
//            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            notificationBuilder.setColor(context.getResources().getColor(R.color.colorAccent));

        } else {

            notificationBuilder.setSmallIcon(R.mipmap.app_icon);
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            notificationBuilder.setColor(context.getResources().getColor(R.color.colorAccent));
        }
        Log.v("MyXmpp", "Image Icon Bitmap :- " + bitmap);

        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setLights(0xff493C7C, 1000, 1000);

        if (vibrationType.equalsIgnoreCase("Off")) {
            notificationBuilder.setVibrate(null);
        } else if (vibrationType.equalsIgnoreCase("Short")) {

            notificationBuilder.setVibrate(new long[]{0, 1000, 500, 1000, 500, 1000});
        } else if (vibrationType.equalsIgnoreCase("Long")) {

            notificationBuilder.setVibrate(new long[]{0, 3000, 500, 3000, 500, 3000});
        }

        if (!ringtoneName.equals("") && !ringtoneName.isEmpty()) {

            notificationBuilder.setSound(sound_notification);

            if (vibrationType.equalsIgnoreCase("Default")) {

                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            }
        } else {
//            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            if (vibrationType.equalsIgnoreCase("Default")) {

                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            } else {
                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            }
        }

        System.out.println("value" + message.reciverName);
        Log.v("MyXMPP", "Value insde MyXMpp :- " + message.reciverName);
        grpReceiver = message.reciverName;

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        boolean isSameUser = false;
        if (numMessages == 0) {
            System.out.println("value else" + message.reciverName);
            notificationBuilder.setContentText(msg)
                    .setContentTitle(message.reciverName);
        } else {
            for (int j = 0; j < userMsgList.size(); j++) {
                if (userMsgList.get(j).get("user").equalsIgnoreCase(message.reciverName)) {
                    isSameUser = true;
                } else {
                    isSameUser = false;
                    break;
                }
            }
            if (isSameUser) {
                notificationBuilder.setContentText(numMessages + 1 + " messages")
                        .setContentTitle(message.reciverName);

                inboxStyle.setBigContentTitle(message.reciverName);

                for (int i = 0; i < userMsgList.size(); i++) {

                    inboxStyle.addLine(userMsgList.get(i).get("body"));

                }
            } else {
                notificationBuilder.setContentText(numMessages + 1 + " messages")
                        .setContentTitle("Speakame");
                inboxStyle.setBigContentTitle("Speakame");

                for (int i = 0; i < userMsgList.size(); i++) {
                    inboxStyle.addLine(userMsgList.get(i).get("user") + ": " + userMsgList.get(i).get("body"));
                }
            }
            inboxStyle.setSummaryText(numMessages + 1 + " messages");

            System.out.println("value if" + message.reciverName);

        }
        ++numMessages;

// Moves events into the expanded layout
// Moves the expanded layout object into the notification object.

        notificationBuilder.setStyle(inboxStyle);
        AllBeans allBeans = new AllBeans();
        allBeans.setFriendname(message.reciverName);
        allBeans.setFriendmobile(message.receiver);
        allBeans.setGroupName(message.groupName);
        allBeans.setLanguages(message.senderlanguages);

        Intent resultIntent;
        if (isSameUser) {
            resultIntent = new Intent(context, ChatActivity.class);
            resultIntent.putExtra("value", allBeans);

        } else {
            resultIntent = new Intent(context, TwoTab_Activity.class);
            resultIntent.setAction("");
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(ChatActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {

    }

    @Override
    public void onDialogUpdated(String dialogId) {

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
            DatabaseHelper.getInstance(QBService.this).insertQbIdQbChatPrivateDialoge(chatDialog.getUserId(), chatDialog.getDialogId(), ser_QBByteData, "Private");

    }

    public QBChatDialog getDialog() {
        return groupChatDialog;
    }

    public QBChatDialog getGrpDialog() {
        return groupChatDialog;
    }

    public void setGrpDialog(QBChatDialog grpDialog) {
        this.groupChatDialog = grpDialog;
    }

    public boolean isGroupCreated() {
        return groupCreated;
    }

    public void setGroupCreated(boolean groupCreated) {
        this.groupCreated = groupCreated;
    }

    @Override
    public void subscriptionRequested(int userId) {
        Log.v(TAG, "over ridden method subscriptionRequested :- " + userId);
        confirmSubscription(userId);

    }

    private void confirmSubscription(int userId) {

        try {
            сhatRoster.confirmSubscription(userId);
        } catch (SmackException.NotConnectedException e) {
            Log.v(TAG, "subscriptionRequested: Error 1 :- " + e.getMessage());
        } catch (SmackException.NotLoggedInException e) {
            Log.v(TAG, "subscriptionRequested: Error 2 :- " + e.getMessage());
        } catch (XMPPException e) {
            Log.v(TAG, "subscriptionRequested: Error 3 :- " + e.getMessage());
        } catch (SmackException.NoResponseException e) {
            Log.v(TAG, "subscriptionRequested: Error 4 :- " + e.getMessage());
        }
    }

    @Override
    public void entriesDeleted(Collection<Integer> collection) {

    }

    @Override
    public void entriesAdded(Collection<Integer> collection) {

    }

    @Override
    public void entriesUpdated(Collection<Integer> collection) {

    }

    @Override
    public void presenceChanged(QBPresence presence) {

        Log.v(TAG, "presence :- QB presenceChanged :- " + presence);

        Log.v(TAG, "QB presence status presence chages getType:- " + presence.getType());
        Log.v(TAG, "QB presence status presence chages getUserId:- " + presence.getUserId());

        String statusOnline = presence.getType().toString();

        if (statusOnline.equalsIgnoreCase("online")) {

            Log.v(TAG, "QB status online :- " + statusOnline);

            User user = new User();
            user.setFriend_id(presence.getUserId());
            user.setStatus("Online");
            DatabaseHelper.getInstance(QBService.this).InsertStatus(user);
//            lastSeen = DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id());
            Log.v(TAG, "QB status online from database 111111111 :- " + DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id()));
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
            DatabaseHelper.getInstance(QBService.this).InsertStatus(user);

            Log.v(TAG, "QB status online from database 2222222222222222 :- " + DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id()));

//            lastSeen = DatabaseHelper.getInstance(QBService.this).getLastSeenQB(user.getFriend_id());
        }

        // TODO  : get image when any other user changes profile picture
        if (presence.getStatus() == null || presence.getStatus().equalsIgnoreCase("null")) {
//DVSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
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
                //   DatabaseHelper.getInstance(TwoTab_Activity.this).UpdateUserImage(presence.getStatus(), String.valueOf(QbUserID));
                //**************************************************************************************************//
                DatabaseHelper.getInstance(QBService.this).UpdateFriendPro(profile_image, status, String.valueOf(QbUserID));
                String userImage = DatabaseHelper.getUsetImage(QbUserID);

                if (!userImage.equalsIgnoreCase("")) {
                    Log.d(TAG, " Friend Image get from DataBase : - " + userImage);
                    adapter.notifyDataSetChanged();
                    if (ChatActivity.instance != null) {
                        Picasso.with(QBService.this).load(userImage).error(R.drawable.profile_default)
                                .resize(200, 200)
                                .into(conversationimage);
                    }

                }
                //**************************************************************************************************//

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

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processMessageSent(String s, QBChatMessage qbChatMessage) {

    }

    @Override
    public void processMessageFailed(String s, QBChatMessage qbChatMessage) {

    }

    @Override
    public void processUserIsTyping(String s, Integer integer) {

    }

    @Override
    public void processUserStopTyping(String s, Integer integer) {

    }

    private void QBSubscriptiondevice() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "TIME Running in 1:- " + Function.datetime());
        Log.v(TAG, "Firebase refreshedToken :- " + refreshedToken);
        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
//        checkPermission();

        String deviceId;
        final TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
            Log.v(TAG, "Device id :- .............." + deviceId);
        } else {
            deviceId = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
        subscription.setDeviceUdid(deviceId);
//
        subscription.setRegistrationID(refreshedToken);

        Log.v(TAG, "Firebase Refreshed Token Splash :- " + refreshedToken);

        Log.v(TAG, "Device id :- .............." + deviceId);

        QBPushNotifications.createSubscription(subscription).performAsync(
                new QBEntityCallback<ArrayList<QBSubscription>>() {

                    @Override
                    public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {

                        Log.i(TAG, ">>> subscription created :- " + subscriptions.toString());

                        QBSubscription qbSubscription = subscriptions.get(0);
                        Log.i(TAG, ">>> subscription created 1:- " + qbSubscription);

                        int subscriptionId = qbSubscription.getId();
                        Log.i(TAG, ">>> subscription id :- " + subscriptionId);

                        AppPreferences.setQBFcmSubscribeId(QBService.this, subscriptionId);
                        Log.i(TAG, "Two Tab Activity subscription id 123 pref :- " + AppPreferences.getQBFcmSubscribeId(QBService.this));

                        deleteSubscription(AppPreferences.getQBFcmSubscribeId(QBService.this));
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e(TAG, "onSubscriptionError :- " + errors.getMessage());
                    }
                });
    }

    private void deleteSubscription(final int subscriptionId) {

        QBPushNotifications.getSubscriptions().performAsync(new QBEntityCallback<ArrayList<QBSubscription>>() {
            @Override
            public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {

                Log.v(TAG, "QBPushNotificatio arraylist :== " + qbSubscriptions);

                Log.v(TAG, "My Subscription id :--  " + subscriptionId);

                for (QBSubscription qbSubscription : qbSubscriptions) {

                    Log.v(TAG, "All Subscription id :--  " + qbSubscription.getId());

                    if (qbSubscription.getId() != subscriptionId) {

                        Log.v(TAG, "Subscription id :== " + qbSubscription.getId());

                        QBPushNotifications.deleteSubscription(qbSubscription.getId()).performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                Log.v(TAG, "push on sucess deleted");
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e(TAG, "push on on ERROR");
                                Log.e(TAG, "Error :- " + e.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {

                Log.e(TAG, "Error :- " + e.getMessage());
            }
        });
    }

    public String getContactName(String number) {

        String name = number;
        ArrayList<Contact> listContacts = new ContactFetcher(QBService.this).fetchAll();
        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.v(TAG, "ContactFetch :- " + contact.name + "::" + phone.number + " ------ " + number);
                Log.v(TAG, "ContactFetch 123:- " + contact.name + "::" + phone.number.toString().replace("-", "").replace(" ", "") + " ------ " + number);
                if (number.contains(phone.number.toString().replace("+", "").replace("-", "").replace(" ", "")) && phone.number.length() > 9) {
                    Log.v(TAG, "getting contact name  :- " + contact.name);
                    return contact.name;
                }
            }
        }
        Log.v(TAG, "" + name);

        return name;
    }

    public void ChangeProfilePic() {
        Log.v(TAG, "Change Profile pic in Services !!!!!!!!!!! ");

    }

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            Log.v(TAG, "Inside systemMessagesManager 1");

            dialogsManager.onSystemMessageReceived(qbChatMessage);
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {

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
