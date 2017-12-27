package com.speakame.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.gson.Gson;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.speakame.Activity.ChatActivity;
import com.speakame.Activity.TwoTab_Activity;
import com.speakame.Beans.AllBeans;
import com.speakame.Database.DatabaseHelper;
import com.speakame.QuickBlox.ChatHelper;
import com.speakame.QuickBlox.DialogsManager;
import com.speakame.QuickBlox.QbChatDialogMessageListenerImp;
import com.speakame.R;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;

import org.apache.commons.lang3.SerializationUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Peter on 03-Nov-17.
 */

public class QBService extends Service implements DialogsManager.ManagingDialogsCallbacks {

    public static final String PROPERTY_OCCUPANTS_IDS = "occupants_ids";
    public static final String PROPERTY_DIALOG_TYPE = "dialog_type";
    public static final String PROPERTY_DIALOG_NAME = "dialog_name";
    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    private static final String TAG = "QBService_";
    public static int numMessages = 0;
    public static Random random;
    QBChatService chatService;
    DialogsManager dialogsManager;
    int Count = 0;
    boolean groupCreated = false;
    List<HashMap<String, String>> userMsgList = new ArrayList<HashMap<String, String>>();
    String grpReceiver;
    Bitmap bitmap;
    Gson gson;
    StringifyArrayList<Integer> userIds;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        initPrivateChatMessageListener();
        initIsTypingListener();
        initMessageSentListener();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Count++;

        dialogsManager = new DialogsManager();
        Log.v(TAG, "Inside on create QB Service !!!!!!!! ------ " + Count + " ````````");
        chatService = QBChatService.getInstance();

        initListener();
        registerQbChatListeners();
        initPrivateChatMessageListener();
        initIsTypingListener();
        initMessageSentListener();

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
        initListener();
        registerQbChatListeners();
        initPrivateChatMessageListener();
        initIsTypingListener();
        initMessageSentListener();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        Log.d(TAG, "Groupimage  :- " + groupImagePicture);
        Log.d(TAG, "creating group dialog Chat Message Body  :- " + chatMessage.toString());
        String body = gson.toJson(chatMessage);
        Log.d(TAG, "creating group dialog Chat Message Body gson convertor  :- " + body);

        byte[] data = SerializationUtils.serialize(groupChatDialog);
        Log.v(TAG, "QB Serialize dialog to data :- " + data);
        QBChatDialog yourObject = SerializationUtils.deserialize(data);
        Log.v(TAG, "QB Serialize data to dialog :- " + yourObject);
        chatMessage.qbChatDialogBytes = data;

//      vsdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd
        dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, groupChatDialog, context, body);
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

    public void generateNofificationUsingQB(ChatMessage message, Context context) {

    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {

    }

    @Override
    public void onDialogUpdated(String chatDialog) {

    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {

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
