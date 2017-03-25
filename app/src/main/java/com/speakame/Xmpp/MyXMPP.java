package com.speakame.Xmpp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.speakame.Activity.ChatActivity;
import com.speakame.Activity.TwoTab_Activity;
import com.speakame.Activity.ViewGroupDetail_Activity;
import com.speakame.Beans.AllBeans;
import com.speakame.Classes.TimeAgo;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.CallBackUi;
import com.speakame.utils.DownloadFile;
import com.speakame.utils.ListCountry;
import com.speakame.utils.TextTranslater;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.attention.packet.AttentionExtension;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.nick.packet.Nick;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.privacy.PrivacyList;
import org.jivesoftware.smackx.privacy.PrivacyListManager;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider;
import org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.FormNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.RetractEventProvider;
import org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.shim.provider.HeaderProvider;
import org.jivesoftware.smackx.shim.provider.HeadersProvider;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.speakame.Activity.ChatActivity.conversationimage;
import static com.speakame.Activity.ChatActivity.toolbartext;


/**
 * Created by MAX on 21-Sep-16.
 */
public class MyXMPP extends Service {

    public static boolean connected = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    public static XMPPTCPConnection connection;
    public static String loginUser;
    public static String passwordUser;
    public static MyXMPP instance = null;
    public static boolean instanceCreated = false;
    public static String serverAddress;
    public static int numMessages = 0;
    CallBackUi callBackUi;
    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
        }
    }

    public boolean loggedin = false;
    public org.jivesoftware.smack.chat.Chat Mychat;
    public org.jivesoftware.smack.chat.Chat MychatCompose;
    Gson gson;
    MyService context;
    Roster roster;
    List<HashMap<String, String>> userMsgList = new ArrayList<HashMap<String, String>>();
    ChatManagerListenerImpl mChatManagerListener;

    MMessageListener mMessageListener;
    MGMessageListener mGMessageListener;
    FileManagerListener fileManagerListener;

    MyInvitationListener myInvitationListener;
    MyReceiptReceivedListener myReceiptReceivedListener;

    String text = "";
    String mMessage = "", mReceiver = "";
    private Random random;
    private boolean chat_created = false;
    private TypingModified typingChangedListener;

    public MyXMPP() {
    }

    public MyXMPP(MyService context, String serverAdress, String logiUser,
                  String passwordser) {
        this.serverAddress = serverAdress;
        this.loginUser = logiUser;
        this.passwordUser = logiUser;
        this.context = context;
        init();

    }

    public static MyXMPP getInstance(MyService context, String server,
                                     String user, String pass) {

        if (instance == null) {
            instance = new MyXMPP(context, server, user, pass);
            instanceCreated = true;
        }
        return instance;

    }


    public static void deleteUserr() {

        try {
            System.out.println("Delete user.");
            AccountManager.getInstance(connection).deleteAccount();
            System.out.println("User is deleted.");
        } catch (Exception ex) {
            System.out.println("Exception in deleteUser: " + ex.toString());
        }

        connection.isSocketClosed();
    }

    public static String getLastSeen(Context context, String user) {

        LastActivityManager lastActivityManager = LastActivityManager.getInstanceFor(connection);
        lastActivityManager.enable();
        try {
            LastActivity lastActivity = lastActivityManager.getLastActivity(user + "@" + context.getString(R.string.server));

            Date date = new Date();
            date.setSeconds((int) (lastActivity.getIdleTime()));
            Log.d("LastActivity", lastActivity.message + "\n" + lastActivity.lastActivity + "\n" + lastActivity.getStatusMessage() + "\n" +
                    lastActivity.getIdleTime() * 1000 + "\n" + new TimeAgo(context).timeAgo(date));
            return new TimeAgo(context).timeAgo(date);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }

    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }
    public MultiUserChat createGroupChat(String chatRoom, String groupid, String user, List<AllBeans> mobileList, String GroupPicture) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        String service = "conference.ip-172-31-30-231";//services.get(0);
        String chatRoomId1 = getOnlyStrings(chatRoom.trim());
        Log.d("groupchatName  service", chatRoom.trim() +">>"+ chatRoom + " ::::" + service);
        // Get a MultiUserChat using MultiUserChatManager

        String chatRoomId = chatRoomId1.replace(" ","_");
        Log.d("groupchatName  service",">>"+ chatRoomId1 + " ::::" + chatRoomId);

        String chatroomServerId = chatRoomId + "@" + service;

        MultiUserChat muc = manager.getMultiUserChat(chatroomServerId);


        try {
            // Create the room
            muc.create(chatRoomId);
            Log.d("groupchat  create room", chatRoomId);
            // muc.sendConfigurationForm(new Form(DataForm.Type.submit));

            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            for (Iterator<FormField> fields = form.getFields().iterator(); fields.hasNext(); ) {
                FormField field = fields.next();
                if (!FormField.Type.hidden.equals(field.getType()) && field.getVariable() != null) {
                    submitForm.setDefaultAnswer(field.getVariable());
                    Log.d("groupchat  submit form", "sub");
                }
            }
            List owners = new ArrayList();
            owners.add(user + "@" + context.getString(R.string.server));
            // submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            submitForm.setAnswer("muc#roomconfig_publicroom", true);
            submitForm.setAnswer("muc#roomconfig_roomname", chatRoomId);
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // Send the completed form (with default values) to the server to configure the room
            muc.sendConfigurationForm(submitForm);

            muc.join(user);
            Log.d("groupchat join self", user);
            muc.addInvitationRejectionListener(new InvitationRejectionListener() {
                public void invitationDeclined(String invitee, String reason) {
                    // Do whatever you need here...
                    Log.d("groupchat  >>", invitee);
                }
            });

            ChatMessage chatMessage = new ChatMessage(AppPreferences.getMobileuser(context), AppPreferences.getFirstUsername(context),
                    chatRoomId, chatRoomId,
                    chatRoom, "Hi welcome to " + chatRoom + " group", "" + random.nextInt(1000), "", true);
            chatMessage.setMsgID();
            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.Time = CommonMethods.getCurrentTime();
            chatMessage.type = Message.Type.groupchat.name();
            chatMessage.groupid = groupid;
            chatMessage.Groupimage = GroupPicture;
            Log.d("Groupimage  >>", GroupPicture);

            createGroupWindows(chatMessage);


            //""+ inviter+" is joined"
            Message message1 = new Message();
            chatMessage.setMsgID();
            chatMessage.body = "Hi welcome to " + chatRoom + " group";
            chatMessage.groupid=groupid;
            String body = gson.toJson(chatMessage);
            message1.setBody(body);
            message1.setStanzaId(chatMessage.msgid);
            message1.setType(Message.Type.groupchat);

            //muc.sendMessage(message1);


            for (int i = 0; i < mobileList.size(); i++) {

                Message message = new Message();
                message.setBody(body);
                // message.setType(Message.Type.groupchat);
                try {
                    muc.invite(mobileList.get(i).getFriendmobile() + "@" + context.getString(R.string.server), message.getBody());

                    Log.d("groupchat  invite>>", mobileList.get(i).getFriendmobile() + "@" + context.getString(R.string.server));
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                }
                muc.addMessageListener(mGMessageListener);

            }

        } catch (SmackException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(context, " Creation failed - User already joined the room.", Toast.LENGTH_LONG).show();
        }
        return muc;
    }

    public void init() {
        gson = new Gson();
        Log.d("xmppconnection", "init");
        random = new Random();
        mChatManagerListener = new ChatManagerListenerImpl();

        mMessageListener = new MMessageListener(context);
        mGMessageListener = new MGMessageListener();


        fileManagerListener = new FileManagerListener();
        myInvitationListener = new MyInvitationListener();
        myReceiptReceivedListener = new MyReceiptReceivedListener();
        initialiseConnection();

    }

    private void initialiseConnection() {

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setUsernameAndPassword(loginUser, passwordUser);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(serverAddress);
        config.setHost(serverAddress);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        connection.setPacketReplyTimeout(10000);
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    public void connect(final String caller) {

        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (connection.isConnected())
                    return false;
                isconnecting = true;
                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {

                           /* Toast.makeText(context,
                                    caller + "=>connecting....",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    });
                Log.d("Connect() Function", caller + "=>connecting....");

                try {
                    connection.connect();
                    configureProviderManager(connection);
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid,
                                                      final String toid, final String msgid,
                                                      final Stanza packet) {

                        }
                    });
                    connected = true;

                    roster = Roster.getInstanceFor(MyXMPP.connection);
                    roster.addRosterListener(new RosterListener() {
                        @Override
                        public void entriesAdded(Collection<String> addresses) {

                        }

                        @Override
                        public void entriesUpdated(Collection<String> addresses) {

                        }

                        @Override
                        public void entriesDeleted(Collection<String> addresses) {

                        }

                        @Override
                        public void presenceChanged(final Presence presence) {

                            Log.d("presenceChanged", "" + presence.getStatus() + ":" + presence.getType() + ":getFrom:" + presence.getFrom());
                            new Handler(Looper.getMainLooper())
                                    .post(new Runnable() {

                                        @Override
                                        public void run() {

                                            updateLastSeen(presence.getType(), presence.getFrom(), presence.getStatus());
                                        }
                                    });

                        }
                    });


                } catch (IOException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(
                                                context,
                                                "(" + caller + ")"
                                                        + "IOException: ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "(" + caller + ")" + "SMACKException: ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("(" + caller + ")",
                            "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)

                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(
                                                context,
                                                "(" + caller + ")"
                                                        + "XMPPException: ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    Log.e("connect(" + caller + ")",
                            "XMPPException: " + e.getMessage());

                }
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    public void login() {

        try {
            Log.i("LOGIN", "Yey! We're connected to the Xmpp server>>>!" + loginUser + ":" + passwordUser);
            if (connection.isConnected()) {
                connection.login(loginUser, passwordUser);

            } else {
                connect("");

            }


            Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            //login();
        } catch (Exception e) {
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendMessage(final ChatMessage chatMessage, final CallBackUi callBack) {
        String body = gson.toJson(chatMessage);
        this.callBackUi = callBack;
        // if (!chat_created) {
        Mychat = ChatManager.getInstanceFor(connection).createChat(
                chatMessage.receiver + "@"
                        + context.getString(R.string.server),
                mMessageListener);
        //    chat_created = true;
        // }
        final Message message = new Message();
        message.setBody(body);
        message.setStanzaId(chatMessage.msgid);
        message.setType(Message.Type.chat);
        final String deliveryReceiptId = DeliveryReceiptRequest.addTo(message);

        try {
            if (connection.isAuthenticated()) {

                Mychat.sendMessage(message);
                Log.e("xmpp.SendMessage()", String.valueOf(message));

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        chatMessage.receiptId = deliveryReceiptId;
                        chatMessage.msgStatus = "1";

                        Log.d("onReceiptReceived", chatMessage.msgStatus+":"+ chatMessage.receiptId);

                        new DatabaseHelper(context).UpdateReceiptID(chatMessage.msgid, chatMessage.receiptId);
                        new DatabaseHelper(context).UpdateMsgStatus(chatMessage.msgStatus, chatMessage.receiptId);

                        callBackUi.update("1");
                        //ChatActivity.chatAdapter.updateStatus(chatMessage.msgStatus, chatMessage.receiptId);

                    }
                });

            } else {

                login();
            }
        } catch (NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.SendMessage()-Exc",
                    "msg Not sent!" + e.getMessage());
        }

    }

    public void sendGroupMessage(final ChatMessage chatMessage, final CallBackUi callBack) {
        String body = gson.toJson(chatMessage);
        this.callBackUi = callBack;
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        Log.d("groupChatName", chatMessage.receiver + "@conference."
                + context.getString(R.string.server));
        MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(chatMessage.receiver + "@conference."
                + context.getString(R.string.server));
           /* if (!chat_created) {
                Mychat = ChatManager.getInstanceFor(connection).createChat(
                        chatMessage.receiver + "@"
                                + context.getString(R.string.server),
                        mMessageListener);
                chat_created = true;
            }*/
        Log.d("fileName", "XMP send>>>" + chatMessage.fileName);
        final Message message = new Message();
        message.setBody(body);
        try {
            if (connection.isAuthenticated()) {
                multiUserChat.join(AppPreferences.getMobileuser(context));
                multiUserChat.sendMessage(message);
                final String deliveryReceiptId = DeliveryReceiptRequest.addTo(message);
                Log.e("xmpp.SendMessage()", String.valueOf(message));

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        chatMessage.receiptId = deliveryReceiptId;
                        chatMessage.msgStatus = "1";

                        Log.d("onReceiptReceived", chatMessage.msgStatus+":"+ chatMessage.receiptId);

                        new DatabaseHelper(context).UpdateReceiptID(chatMessage.msgid, chatMessage.receiptId);
                        new DatabaseHelper(context).UpdateMsgStatus(chatMessage.msgStatus, chatMessage.receiptId);

                        callBackUi.update("1");
                        //ChatActivity.chatAdapter.updateStatus(chatMessage.msgStatus, chatMessage.receiptId);

                    }
                });

            } else {

                login();
            }
        } catch (NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.SendMessage()-Exc",
                    "msg Not sent!" + e.getMessage());
        }

    }

    public void generateNofification(ChatMessage message) {
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

        String vibrationType =  AppPreferences.getVibrationType(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setLights(0xff493C7C, 1000, 1000);

        if (vibrationType.equalsIgnoreCase("Off")) {

            notificationBuilder.setVibrate(null);
        } else if (vibrationType.equalsIgnoreCase("Short")) {

            notificationBuilder.setVibrate(new long[]{0, 1000, 500, 1000, 500, 1000});
        } else if (vibrationType.equalsIgnoreCase("Long")) {

            notificationBuilder.setVibrate(new long[]{0,3000, 500, 3000, 500, 3000});
        }

        if (! ringtoneName.equals("") && !ringtoneName.isEmpty()) {

            notificationBuilder.setSound(sound_notification);

            if (vibrationType.equalsIgnoreCase("Default")) {

                notificationBuilder.setDefaults( Notification.DEFAULT_VIBRATE);
            }
        } else {

//            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            if (vibrationType.equalsIgnoreCase("Default")) {

                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            } else  {
                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            }
        }

        System.out.println("value" + message.reciverName);
        Log.v("MyXMPP", "Value insde MyXMpp :- " +  message.reciverName);

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


    public void createRoster(String jid, String name, String[] group) throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, NotConnectedException, SmackException.NoResponseException {
        Roster roster = Roster.getInstanceFor(connection);
        roster.createEntry(jid, name, group);
        Presence subscribed = new Presence(Presence.Type.subscribed);
        subscribed.setTo(jid);
        connection.sendPacket(subscribed);
    }

    public void rosterss() {
        Roster roster = Roster.getInstanceFor(connection);
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            System.out.println("NAME>>>" + entry);
        }


    }

    public void subscribtion(String user, String name, String group) {

        Roster roster = Roster.getInstanceFor(connection);
        try {
            roster.createEntry(user, name, new String[]{group});
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
        rosterss();

    }

    public String getContactName(String number) {
        String name = number;

        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        // jsonContacts = new JSONArray();
        if (cursor.moveToFirst()) {

            //jsonContacts = new JSONObject();
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        if (contactNumber.equalsIgnoreCase(number)) {
                            name = (contactname);
                        }

                        break;

                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        return name;
    }



    public void GroupChatInvitation() {
        StanzaFilter filter = new StanzaExtensionFilter("x", "jabber:x:conference");
        connection.createPacketCollector(filter);
    }

    public void JoinedGroup() {
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);

        List<ChatMessage> chatMessageList = DatabaseHelper.getInstance(context).getGroup();
        for (int i = 0; i < chatMessageList.size(); i++) {
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(chatMessageList.get(i).groupName + "@conference." + context.getString(R.string.server));
            try {
                multiUserChat.join(loginUser);
                Log.d("multiUserChat joined", chatMessageList.get(i).groupName + "@conference." + context.getString(R.string.server));
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }
            multiUserChat.addMessageListener(mGMessageListener);
        /*    multiUserChat.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {

                }
            });*/
        }

    }

    private void createGroupWindows(final ChatMessage chatMessage) {
        DatabaseHelper helper = new DatabaseHelper(context);
        String msgId = helper.getMsgId(chatMessage.msgid);
        if (msgId.equalsIgnoreCase("")) {

            /*final ChatMessage message = new ChatMessage(chatMessage.receiver, chatMessage.reciverName, chatMessage.sender, getContactName(chatMessage.sender),chatMessage.groupName, chatMessage.body, chatMessage.msgid,"", chatMessage.isMine);
            message.Date = chatMessage.Date;
            message.Time = chatMessage.Time;
            message.type =  Message.Type.groupchat.name();
            DatabaseHelper.getInstance(context).insertChat(message);*/
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
                        generateNofification(chatMessage);
                    }

                    subscribtion(chatMessage.receiver, chatMessage.reciverName, chatMessage.sender);
                }
            });
        }
    }

    public void setStatus(boolean available, String status) throws XMPPException {
        Presence.Type type = available ? Presence.Type.available : Presence.Type.unavailable;
        Presence presence = new Presence(type);
        presence.setStatus(status);
        try {
            connection.sendPacket(presence);
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void configureProviderManager(XMPPConnection connection) {


        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());


        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);

        sdm.addFeature("http://jabber.org/protocol/disco#info");
        sdm.addFeature("http://jabber.org/protocol/disco#item");
        sdm.addFeature("jabber:iq:privacy");


        // The order is the same as in the smack.providers file

        //  Private Data Storage
        ProviderManager.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
        //  Time
        try {
            ProviderManager.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            System.err.println("Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        //  Roster Exchange
        //   ProviderManager.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
        //  Message Events
        //  ProviderManager.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());
        //  Chat State
        ProviderManager.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        //  XHTML
        ProviderManager.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        //  Group Chat Invitations
        ProviderManager.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
        //  Service Discovery # Items
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        //  Service Discovery # Info
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        //  Data Forms
        ProviderManager.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        //  MUC User
        ProviderManager.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        //  MUC Admin
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        //  MUC Owner
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        //  Delayed Delivery
        ProviderManager.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
        ProviderManager.addExtensionProvider("delay", "urn:xmpp:delay", new DelayInformationProvider());
        //  Version
        try {
            ProviderManager.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            System.err.println("Can't load class for org.jivesoftware.smackx.packet.Version");
        }
        //  VCard
        ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        //  Offline Message Requests
        ProviderManager.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        //  Offline Message Indicator
        ProviderManager.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        //  Last Activity
        ProviderManager.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        //  User Search
        ProviderManager.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        //  SharedGroupsInfo
        ProviderManager.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        //  JEP-33: Extended Stanza Addressing
        ProviderManager.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

        //   FileTransfer
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        ProviderManager.addIQProvider("open", "http://jabber.org/protocol/ibb", new OpenIQProvider());

        ProviderManager.addIQProvider("close", "http://jabber.org/protocol/ibb", new CloseIQProvider());
        //  ProviderManager.addExtensionProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());

        //  Privacy
        ProviderManager.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());

        // SHIM
        ProviderManager.addExtensionProvider("headers", "http://jabber.org/protocol/shim", new HeadersProvider());
        ProviderManager.addExtensionProvider("header", "http://jabber.org/protocol/shim", new HeaderProvider());

        // PubSub
        ProviderManager.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub", new PubSubProvider());
        ProviderManager.addExtensionProvider("create", "http://jabber.org/protocol/pubsub", new SimpleNodeProvider());
        ProviderManager.addExtensionProvider("items", "http://jabber.org/protocol/pubsub", new ItemsProvider());
        ProviderManager.addExtensionProvider("item", "http://jabber.org/protocol/pubsub", new ItemProvider());
        ProviderManager.addExtensionProvider("subscriptions", "http://jabber.org/protocol/pubsub", new SubscriptionsProvider());
        ProviderManager.addExtensionProvider("subscription", "http://jabber.org/protocol/pubsub", new SubscriptionProvider());
        ProviderManager.addExtensionProvider("affiliations", "http://jabber.org/protocol/pubsub", new AffiliationsProvider());
        ProviderManager.addExtensionProvider("affiliation", "http://jabber.org/protocol/pubsub", new AffiliationProvider());
        ProviderManager.addExtensionProvider("options", "http://jabber.org/protocol/pubsub", new FormNodeProvider());
        // PubSub owner
        ProviderManager.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub#owner", new PubSubProvider());
        ProviderManager.addExtensionProvider("configure", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
        ProviderManager.addExtensionProvider("default", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
        // PubSub event
        ProviderManager.addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", new EventProvider());
        ProviderManager.addExtensionProvider("configuration", "http://jabber.org/protocol/pubsub#event", new ConfigEventProvider());
        ProviderManager.addExtensionProvider("delete", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());
        ProviderManager.addExtensionProvider("options", "http://jabber.org/protocol/pubsub#event", new FormNodeProvider());
        ProviderManager.addExtensionProvider("items", "http://jabber.org/protocol/pubsub#event", new ItemsProvider());
        ProviderManager.addExtensionProvider("item", "http://jabber.org/protocol/pubsub#event", new ItemProvider());
        ProviderManager.addExtensionProvider("retract", "http://jabber.org/protocol/pubsub#event", new RetractEventProvider());
        ProviderManager.addExtensionProvider("purge", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());

        // Nick Exchange
        ProviderManager.addExtensionProvider("nick", "http://jabber.org/protocol/nick", new Nick.Provider());

        // Attention
        ProviderManager.addExtensionProvider("attention", "urn:xmpp:attention:0", new AttentionExtension.Provider());

        //input
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        ProviderManager.addIQProvider("open", "http://jabber.org/protocol/ibb",
                new OpenIQProvider());
        ProviderManager.addIQProvider("close", "http://jabber.org/protocol/ibb",
                new CloseIQProvider());
        // ProviderManager.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
        //         new DataPacketProvider());

    }

    public void unBlockedUser(String userName) {

        String listName = userName;
        List<PrivacyItem> privacyItems = new Vector<PrivacyItem>();

        PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid,
                userName, true, 1l);
        privacyItems.add(item);
        // Create the new list.

        try {
            PrivacyListManager privacyManager;
            privacyManager = PrivacyListManager
                    .getInstanceFor(connection);
            privacyManager.createPrivacyList(listName, privacyItems);

        } catch (XMPPException e) {
            System.out.println("PRIVACY_ERROR: " + e);
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void blockedUser(String userName) {

        String listName = userName;
        List<PrivacyItem> privacyItems = new Vector<PrivacyItem>();

        PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid,
                userName, false, 1l);
        privacyItems.add(item);
        // Create the new list.

        try {
            PrivacyListManager privacyManager;
            privacyManager = PrivacyListManager
                    .getInstanceFor(connection);
            privacyManager.createPrivacyList(listName, privacyItems);

        } catch (XMPPException e) {
            System.out.println("PRIVACY_ERROR: " + e);
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUserBlock(String userName) {
        boolean isAllow = true;
        String listName = userName;

        PrivacyListManager privacyManager;
        privacyManager = PrivacyListManager
                .getInstanceFor(connection);

        try {
            PrivacyList privacyList = privacyManager.getPrivacyList(listName);
            List<PrivacyItem> privacyItem = privacyList.getItems();
            for (PrivacyItem item : privacyItem) {
                Log.d("privacyList", item.getValue());
            }

            isAllow = privacyItem.get(0).isAllow();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }


        return isAllow;
    }

    public void setStatusModified(TypingModified typingChangedListener) {
        this.typingChangedListener = typingChangedListener;
    }

    public void addNewMemberInGroup(String chatRoom, String groupid, String user, List<AllBeans> mobileList) {

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        String service = "conference.ip-172-31-30-231";//services.get(0);
        Log.d("groupchatName  service", chatRoom + " ::::" + service);
        // Get a MultiUserChat using MultiUserChatManager
        String chatRoomId1 = getOnlyStrings(chatRoom.trim());
        String chatRoomId = chatRoomId1.replace(" ","_");

        String chatroomServerId = chatRoomId + "@" + service;

        MultiUserChat muc = manager.getMultiUserChat(chatroomServerId);

        for (int i = 0; i < mobileList.size(); i++) {

            Message message = new Message();
            message.setBody("Hi welcome to" + chatRoom + "group");
            // message.setType(Message.Type.groupchat);
            try {
                muc.invite(mobileList.get(i).getFriendmobile() + "@" + context.getString(R.string.server), message.getBody());

                Log.d("groupchat  invite>>", mobileList.get(i).getFriendmobile() + "@" + context.getString(R.string.server));
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }
            muc.addMessageListener(mGMessageListener);

        }
    }

    public boolean sendIsComposing(Composing composing, String user) {
        try {
            Message msg = new Message();

            //other stuff...

            msg.setBody(null);
            msg.addExtension(new ChatStateExtension(composing.getState()));

            //other stuff...

            try {
                if (connection.isAuthenticated()) {
                    //  if (!chat_created) {
                    MychatCompose = ChatManager.getInstanceFor(connection).createChat(
                            user + "@"
                                    + context.getString(R.string.server),
                            mMessageListener);
                    //chat_created = true;
                    //  }
                    MychatCompose.sendMessage(msg);
                    Log.e("xmpp.sendIsComposing()", String.valueOf(msg));

                } else {

                    login();
                }
            } catch (NotConnectedException e) {
                Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

            } catch (Exception e) {
                Log.e("xmpp.SendMessage()-Exc",
                        "msg Not sent!" + e.getMessage());
                e.printStackTrace();
            }
            return (true);
        } catch (Exception e) {
            e.printStackTrace();
            return (false);
        }
    }

    private void updateLastSeen(Presence.Type type, String from, String status) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.receiver = from.replace(context.getResources().getString(R.string.serverandresorces), "");
        Log.d("dummyDatesasta", chatMessage + ">>>");
        if(status == null){
            chatMessage.lastseen = String.valueOf(new Date().getTime());
            DatabaseHelper.getInstance(context).UpdateLastSeen(chatMessage);
            try {
                typingChangedListener.onIsTypingModified("offline", chatMessage.receiver);
            } catch (NullPointerException e) {
            }
        }else
        if(status.equalsIgnoreCase("online")){
            chatMessage.lastseen = "online";
            DatabaseHelper.getInstance(context).UpdateLastSeen(chatMessage);
            try {
                typingChangedListener.onIsTypingModified("online", chatMessage.receiver);
            } catch (NullPointerException e) {
            }
        }else{
            chatMessage.lastseen = String.valueOf(new Date().getTime());
            DatabaseHelper.getInstance(context).UpdateLastSeen(chatMessage);
            try {
                typingChangedListener.onIsTypingModified("offline", chatMessage.receiver);
            } catch (NullPointerException e) {
            }
        }


        /*if (type.equals(Presence.Type.available)) {
            chatMessage.lastseen = "";
            DatabaseHelper.getInstance(context).UpdateLastSeen(chatMessage);
            try {
                typingChangedListener.onIsTypingModified("online", chatMessage.receiver);
            } catch (NullPointerException e) {
            }
        } else {
            chatMessage.lastseen = String.valueOf(new Date().getTime());
            DatabaseHelper.getInstance(context).UpdateLastSeen(chatMessage);
            try {
                typingChangedListener.onIsTypingModified("offline", chatMessage.receiver);
            } catch (NullPointerException e) {
            }
        }*/


    }

    public boolean deleteChatRoom(String roomId) throws NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(roomId + "@conference." + context.getString(R.string.server));

        multiUserChat.join(loginUser);

        for (Affiliate affiliate : multiUserChat.getOwners()) {
            if (affiliate.getJid().equalsIgnoreCase(loginUser + "@" + context.getResources().getString(R.string.server))) {
                multiUserChat.destroy("remove", loginUser + "@" + context.getResources().getString(R.string.server));
                return true;
            }
        }
        return false;
    }

    private void groupNotification(String romm, String meg) {

        Intent notificationIntent = new Intent(context, TwoTab_Activity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(romm)
                        .setContentText(meg)

                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setTicker("Speakame")
                        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND| Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent);
                        //.setContentInfo("Info");


        //builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }



    public enum Composing {
        typing(ChatState.composing),
        active(ChatState.active),
        pause(ChatState.paused),
        gone(ChatState.gone),
        inactive(ChatState.inactive);

        private ChatState state;

        Composing(ChatState state) {
            this.state = state;
        }

        public ChatState getState() {
            return (state);
        }

    }

    public interface TypingModified {
        public void onIsTypingModified(String status, String reciver);
    }

    private class FileManagerListener implements FileTransferListener {

        @Override
        public void fileTransferRequest(final FileTransferRequest request) {
            new Thread() {
                @Override
                public void run() {
                    IncomingFileTransfer transfer = request.accept();
                    Log.d("FileManager: tra", transfer.getFileName());

                    // File saveFile = Function.getOutputMediaFile();

                    File SpeakaMe = Environment.getExternalStorageDirectory();
                    File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/image/");
                    if (!SpeakaMeDirectory.exists()) {
                        SpeakaMeDirectory.mkdirs();
                    }
                    File file = new File(SpeakaMeDirectory, transfer.getFileName());


                    Log.d("SpeakaMeDirectory", SpeakaMeDirectory + ":::" + file.toString() + "\n" + transfer.toString() + "\n" + request.getDescription());
                    try {
                        transfer.recieveFile(file);
                        while (!transfer.isDone()) {
                            Log.i("XMPPClient", "while..... ");

                            if (transfer.getStatus().equals(
                                    FileTransfer.Status.error)) {
                                Log.e("ERROR!!! ", transfer.getError() + "");
                            }
                            if (transfer.getException() != null) {
                                transfer.getException().printStackTrace();
                            }

                            final double progress = transfer.getProgress();
                            final double progressPercent = progress * 100.0;
                            final String percComplete = String.format("%1$,.2f", progressPercent);
                            Log.d("Transfer status is: ", transfer.getStatus() + "");
                            Log.d("File transfer is ", percComplete + "%");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ChatActivity.instance != null) {
                                        //  ChatAdapter.progressStatus.setVisibility(View.VISIBLE);
                                        // ChatAdapter.progressStatus.setText(percComplete + "%");
                                    }
                                }
                            });

                            Thread.sleep(1000L);
                        }

                        if (transfer.isDone()) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ChatActivity.instance != null) {
                                        // ChatAdapter.progressStatus.setVisibility(View.GONE);
                                        // ChatAdapter.progressStatus.setText("");
                                    }
                                }
                            });

                            final ChatMessage chatMessage = gson.fromJson(request.getDescription(), ChatMessage.class);
                            chatMessage.files = file.getAbsolutePath();

                            processImage(chatMessage);

                        }

                    } catch (Exception e) {
                        Log.e("XMPPClient", e.getMessage());
                    }
                }

                ;
            }.start();
        }

        private void processImage(final ChatMessage chatMessage) {

            chatMessage.isMine = false;
            DatabaseHelper helper = new DatabaseHelper(context);
            String msgId = helper.getMsgId(chatMessage.msgid);
            //String msgId = DatabaseHelper.getInstance(context).getMsgId(chatMessage.msgid);
            if (msgId.equalsIgnoreCase("")) {

                final ChatMessage message = new ChatMessage(chatMessage.receiver, chatMessage.reciverName, chatMessage.sender, getContactName(chatMessage.sender), chatMessage.groupName, chatMessage.body, chatMessage.msgid, chatMessage.files, chatMessage.isMine);
                message.Date = chatMessage.Date;
                message.Time = chatMessage.Time;
                message.type = Message.Type.chat.name();
                DatabaseHelper.getInstance(context).insertChat(message);

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
                            generateNofification(message);
                        }

                        String mobb = DatabaseHelper.getInstance(context).getMobileNO(message.receiver);
                        Log.d("Name>>", mobb);
                        if (mobb.equalsIgnoreCase("")) {

                        }
                        //subscribtion(message.receiver, message.reciverName);
                    }
                });
            }

        }
    }

    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(final org.jivesoftware.smack.chat.Chat chat,
                                final boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(mMessageListener);

        }

    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d("xmpp", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();

            }
        }

        @Override
        public void connectionClosed() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

//                        Toast.makeText(context, "ConnectionCLosed!",
//                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
            //  startService(new Intent(context, XmppConneceted.class));
            connect("");
            /*try {
                connection.connect();

            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }*/
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, "ConnectionClosedOn Error!!",
//                                Toast.LENGTH_SHORT).show();
//
//                    }
//                });
                Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;

            chat_created = false;
            loggedin = false;
            connect("");
            /*try {
                connection.connect();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }*/
        }

        @Override
        public void reconnectingIn(int arg0) {

            Log.d("xmpp", "Reconnectingin " + arg0);

            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)
//
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        Toast.makeText(context, "ReconnectionFailed!",
//                                Toast.LENGTH_SHORT).show();
//
//                    }
//                });
                Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
            connect("");
            /*try {
                connection.connect();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }*/
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                       /* Toast.makeText(context, "REConnected!",
                                Toast.LENGTH_SHORT).show();*/

                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;

            ChatManager.getInstanceFor(connection).addChatListener(
                    mChatManagerListener);
            FileTransferManager.getInstanceFor(connection).addFileTransferListener(fileManagerListener);

            MultiUserChatManager.getInstanceFor(connection).addInvitationListener(myInvitationListener);

            DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(myReceiptReceivedListener);


            GroupChatInvitation();
            JoinedGroup();

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
/*
                        Toast.makeText(context, "Connected!",
                                Toast.LENGTH_SHORT).show();*/

                    }
                });
        }
    }

    private class MMessageListener implements ChatMessageListener {

        public MMessageListener(Context contxt) {
            Log.d("xmppconnection", "MMessageListener");
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat,
                                   final Message message) {


            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);
            Log.d("xmppconnection", "processMessage");
            if (message.getType() == Message.Type.chat
                    && message.getBody() != null) {
                final ChatMessage chatMessage = gson.fromJson(message.getBody(), ChatMessage.class);


                if (checkUserBlock(chatMessage.sender)) {
                    final String langu = chatMessage.senderlanguages;
                    final String Mylangu = AppPreferences.getUSERLANGUAGE(context);
                   // List<String> languages = context.getResources().getStringArray(R.array.country);
                    //String[] languageCode = context.getResources().getStringArray(R.array.countryCode);

                    ListCountry country = new ListCountry();
                    String sorcountrycode = country.getCode(langu.trim());
                    if(sorcountrycode.equalsIgnoreCase("")){
                     sorcountrycode = "en";
                    }
                    /*for (int i = 0; i < languages.length; i++) {
                        if (langu.equalsIgnoreCase(languages[i])) {
                            sorcountrycode = languageCode[i];
                        }
                    }*/
                    String descountrycode = country.getCode(Mylangu.trim());
                    if(descountrycode.equalsIgnoreCase("")){
                        descountrycode = "en";
                    }
                    /*for (int i = 0; i < languages.length; i++) {
                        if (Mylangu.equalsIgnoreCase(languages[i])) {
                            descountrycode = languageCode[i];
                        }
                    }*/

                    String msgId = new DatabaseHelper(context).getMsgId(chatMessage.msgid);
                    if (msgId.equalsIgnoreCase("")) {
                        if (langu.equalsIgnoreCase(Mylangu)) {
                            processMessages(chatMessage);

                        } else {

                            try {

                                TextTranslater.getInstance().translate(context, sorcountrycode, descountrycode, chatMessage.body, new VolleyCallback() {
                                    @Override
                                    public void backResponse(String response) {
                                        if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(context).equalsIgnoreCase("1")) {

                                            chatMessage.body = (chatMessage.body + "~" + Mylangu + "~" + response);
                                            //  chatMessage.body = (chatMessage.body + "\n" + Mylangu + ":\n" + response);
                                        } else if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(context).equalsIgnoreCase("0")) {

                                            chatMessage.body = response;
                                            // chatMessage.body = (chatMessage.body + "\n" + Mylangu + ":\n" + response);
                                        }
                                        processMessages(chatMessage);


                                    }

                                });
                            }catch (Exception e){}
                        }

                    }

                }
            } else {
                String msg_xml = message.toXML().toString();
                Log.d("dummyD", msg_xml);
                String reciver = message.getFrom().replace(context.getResources().getString(R.string.serverandresorces), "");
                try {
                    if (msg_xml.contains(ChatState.composing.toString())) {
                        typingChangedListener.onIsTypingModified("typing...", reciver);

                    } else if (msg_xml.contains(ChatState.paused.toString())) {
                        typingChangedListener.onIsTypingModified("online", reciver);

                    } else if (msg_xml.contains(ChatState.active.toString())) {
                        typingChangedListener.onIsTypingModified("online", reciver);
                    } else if (msg_xml.contains(ChatState.inactive.toString())) {
                        typingChangedListener.onIsTypingModified("offline", reciver);
                    } else if (msg_xml.contains(ChatState.gone.toString())) {
                        typingChangedListener.onIsTypingModified("online", reciver);
                    }
                }catch (NullPointerException e){}

            }


        }


        private void processMessages(final ChatMessage chatMessage) {

            chatMessage.isMine = false;


// String msgId = DatabaseHelper.getInstance(context).getMsgId(chatMessage.msgid);


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
            message.msgStatus = "10";


            // File file1 = new File(SpeakaMeDirectory, transfer.getFileName());

            if (!chatMessage.files.equalsIgnoreCase("")) {

                /*String fileExte = Function.getFileExtention(chatMessage.files);
                String folderType;

                String msg = chatMessage.body;
                if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
                    folderType = "contact";
                } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                    folderType = "image";
                } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                    folderType = "video";
                } else if (fileExte.equalsIgnoreCase("pdf")) {
                    folderType = "document";
                } else {
                    folderType = "test";
                }

                chatMessage.files = chatMessage.files.replace("\"","");

                File SpeakaMe = Function.createFolder(folderType,"recive");
                Log.d("backResponse>>", chatMessage.files);
                String fileName = Function.getFileName(chatMessage.files);

                message.fileName = fileName;*/

               /* try {
                    File file2 = Function.decodeBase64BinaryToFile(SpeakaMeDirectory.toString(), chatMessage.fileName, chatMessage.files);
                    message.fileName = file2.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/


            }else{
               // DatabaseHelper.getInstance(context).insertChat(message);
            }

            DatabaseHelper.getInstance(context).insertChat(message);

            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {


                    if (ChatActivity.instance != null) {
                        Log.d("msggg", chatMessage.sender);
                        DatabaseHelper.getInstance(context).UpdateMsgRead("1", chatMessage.sender);
                        if (ChatActivity.FriendMobileTWO.equalsIgnoreCase(chatMessage.sender)) {

                            ChatActivity.chatlist.add(message);

                            ChatActivity.mRecyclerView.scrollToPosition(ChatActivity.chatAdapter.getItemCount()-1);

                            MediaPlayer mp = MediaPlayer.create(context, R.raw.steamchat);
                            if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
                                mp.stop();
                            } else {
                                mp.start();
                            }

                           /* final MediaPlayer mp = MediaPlayer.create(context, R.raw.steamchat);
                            mp.start();*/
                        } else {
                            generateNofification(message);
                            if(!message.files.equalsIgnoreCase("")) {
                                downLoadFile(message);
                            }
                        }

                    } else {
                        generateNofification(message);

                        if(!message.files.equalsIgnoreCase("")) {
                            downLoadFile(message);
                        }

                    }

                    if (TwoTab_Activity.instance != null) {
                        TwoTab_Activity.updateList(chatMessage.groupName);

                    }


//subscribtion(message.receiver, message.reciverName);
                }
            });


        }


    }

    private void processGroupMessage(final ChatMessage chatMessage) {

        // groupNotification(chatMessage.groupName, chatMessage.body);

        chatMessage.isMine = false;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.msgStatus = "10";
        DatabaseHelper helper = new DatabaseHelper(context);
        String msgId = helper.getMsgId(chatMessage.msgid);
        //DatabaseHelper.getInstance(context).getMsgId(chatMessage.msgid);
        if (msgId.equalsIgnoreCase("")) {
          /*  final ChatMessage message = new ChatMessage(chatMessage.sender,getContactName(chatMessage.sender), chatMessage.receiver, chatMessage.reciverName, chatMessage.groupName, chatMessage.body, chatMessage.msgid,chatMessage.files, chatMessage.isMine);
            message.Date = chatMessage.Date;
            message.Time = chatMessage.Time;
*/
            if (chatMessage.fileName == null) {

            } else if (!chatMessage.fileName.equalsIgnoreCase("")) {

                /*String fileExte = MimeTypeMap.getFileExtensionFromUrl(chatMessage.fileName);
                String folderType;

                String msg = chatMessage.body;
                if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
                    folderType = "contact";
                } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                    folderType = "image";
                } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                    folderType = "video";
                } else if (fileExte.equalsIgnoreCase("pdf")) {
                    folderType = "document";
                } else {
                    folderType = "test";
                }

                File SpeakaMe = Environment.getExternalStorageDirectory();
                File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/" + folderType + "/recive");
                if (!SpeakaMeDirectory.exists()) {
                    SpeakaMeDirectory.mkdirs();
                }

                try {
                    File file2 = Function.decodeBase64BinaryToFile(SpeakaMeDirectory.toString(), chatMessage.fileName, chatMessage.files);
                    chatMessage.fileName = file2.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
            Log.d("processGroupMessage", chatMessage.toString());
            DatabaseHelper.getInstance(context).insertChat(chatMessage);

            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    if (ChatActivity.instance != null) {
                        if(chatMessage.body.contains("Image changed by")){
                            Picasso.with(context).load(chatMessage.Groupimage).error(R.drawable.user_icon)
                                    .resize(200, 200)
                                    .into(conversationimage);
                        }else if(chatMessage.body.contains("name changed by")){
                            toolbartext.setText(chatMessage.groupName);
                        }
                        ChatActivity.chatlist.add(chatMessage);
                        ChatActivity.mRecyclerView.scrollToPosition(ChatActivity.chatAdapter.getItemCount()-1);
                        // ChatActivity.mLayoutManager.scrollToPosition(ChatActivity.chatlist.size());
                        MediaPlayer mp = MediaPlayer.create(context, R.raw.steamchat);
                        if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
                            mp.stop();
                        } else {
                            mp.start();
                        }
                    }else{
                        generateNofification(chatMessage);

                        if(!chatMessage.files.equalsIgnoreCase("")) {
                            downLoadFile(chatMessage);
                        }
                    }

                    if (TwoTab_Activity.instance != null) {
                        TwoTab_Activity.updateList(chatMessage.groupName);
                    }

                   /* if (TwoTab_Activity.instance == null && ChatActivity.instance == null) {
                        generateNofification(chatMessage);
                    }
*/

                    //subscribtion(message.receiver, message.reciverName);
                }
            });
        }

    }
    private void downLoadFile(final ChatMessage message) {
        /*ObjectAnimator animation = ObjectAnimator.ofInt (vh1.progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration (5000); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();*/

        new DownloadFile(context,new ImageView(context),new ProgressBar(context), new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                if(response.equalsIgnoreCase("11")){
                    DatabaseHelper.getInstance(context).UpdateMsgStatus(response,message.msgid);
                }else{
                    DatabaseHelper.getInstance(context).UpdateMsgStatus("12",message.msgid);

                    DatabaseHelper.getInstance(context).UpdateFileName(response,message.msgid);

                }
                /*
                    DatabaseHelper.getInstance(context).UpdateMsgStatus(response,message.msgid);
                File SpeakaMe = Environment.getExternalStorageDirectory();
                File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/image/recive");
                String file = SpeakaMeDirectory+"/"+message.fileName;
                DatabaseHelper.getInstance(context).UpdateFileName(file,message.msgid);*/
            }
        }).execute(message.files, message.fileName);
    }

   /* private void downLoadFile(final ChatMessage message){
        new DownloadFile(new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                DatabaseHelper.getInstance(context).UpdateMsgStatus("5",message.msgid);
            }
        }).execute(message.files, message.fileName);
    }*/

    private class MGMessageListener implements MessageListener {


        @Override
        public void processMessage(Message message) {
            Log.d("processGroupMessage1", message.toString());
            if (message.getType() == Message.Type.groupchat
                    && message.getBody() != null) {
                final ChatMessage chatMessage = gson.fromJson(message.getBody(), ChatMessage.class);
                Log.d("processGroupMessage2", chatMessage.toString());
               // processGroupMessage(chatMessage);

                if (checkUserBlock(chatMessage.sender)) {


                    final String langu = chatMessage.senderlanguages;
                    final String Mylangu = AppPreferences.getUSERLANGUAGE(context);

                    ListCountry country = new ListCountry();
                    String sorcountrycode = country.getCode(langu.trim());
                    if(sorcountrycode.equalsIgnoreCase("")){
                        sorcountrycode = "en";
                    }

                    String descountrycode = country.getCode(Mylangu.trim());
                    if(descountrycode.equalsIgnoreCase("")){
                        descountrycode = "en";
                    }

                  /*  String[] languages = context.getResources().getStringArray(R.array.country);
                    String[] languageCode = context.getResources().getStringArray(R.array.country);
                    String sorcountrycode = "en";
                    for (int i = 0; i < languages.length; i++) {
                        if (langu.equalsIgnoreCase(languages[i])) {
                            sorcountrycode = languageCode[i];
                        }
                    }
                    String descountrycode = "en";
                    for (int i = 0; i < languages.length; i++) {
                        if (Mylangu.equalsIgnoreCase(languages[i])) {
                            descountrycode = languageCode[i];
                        }
                    }
*/
                    String msgId = new DatabaseHelper(context).getMsgId(chatMessage.msgid);
                    if (msgId.equalsIgnoreCase("")) {
                        if (langu.equalsIgnoreCase(Mylangu)) {
                            processGroupMessage(chatMessage);

                        } else {
                            try {
                                TextTranslater.getInstance().translate(context,sorcountrycode, descountrycode, chatMessage.body, new VolleyCallback() {
                                    @Override
                                    public void backResponse(String response) {
                                        if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(context).equalsIgnoreCase("1")) {

                                            chatMessage.body = (chatMessage.body + "~" + Mylangu + "~" + response);
                                            //  chatMessage.body = (chatMessage.body + "\n" + Mylangu + ":\n" + response);
                                        } else if (!response.equalsIgnoreCase("") && AppPreferences.getTotf(context).equalsIgnoreCase("0")) {

                                            chatMessage.body = response;
                                            // chatMessage.body = (chatMessage.body + "\n" + Mylangu + ":\n" + response);
                                        }
                                        processGroupMessage(chatMessage);


                                    }

                                });
                            }catch (Exception e){}
                        }

                    }

                }


            }

        }
    }

    private class MyInvitationListener implements InvitationListener {

        @Override
        public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {

            Log.d("invitationReceived", room.getRoom() + "::" + inviter + ":::" + reason + "::" + password + ":::" + message.toString());
            try {

                   /* MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

                    MultiUserChat muc = manager.getMultiUserChat("tre@conference."+context.getString(R.string.server));*/

                //room.join(AppPreferences.getMobileuser(context));
                try {
                    room.join(loginUser);
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                }
                JSONObject jsonObject=new JSONObject(reason);

                Log.d("groupchat join >>", AppPreferences.getMobileuser(context));
                String chatRoomId = room.getRoom().replace("@conference.ip-172-31-30-231", "");
                String roomName = chatRoomId.replace("_", " ");
                ChatMessage chatMessage = new ChatMessage(inviter, inviter,
                        chatRoomId, chatRoomId,
                        jsonObject.getString("groupName"), jsonObject.getString("body"), "" + message.getStanzaId(), "", false);
                chatMessage.setMsgID();
                chatMessage.Date = CommonMethods.getCurrentDate();
                chatMessage.Time = CommonMethods.getCurrentTime();
                chatMessage.type = Message.Type.groupchat.name();
                chatMessage.groupid=jsonObject.getString("groupid");
                chatMessage.Groupimage = jsonObject.getString("Groupimage");

                //groupNotification(chatMessage.groupName, chatMessage.body);
                generateNofification(chatMessage);
                createGroupWindows(chatMessage);


                //""+ inviter+" is joined"
                Message message1 = new Message();
                chatMessage.setMsgID();
                chatMessage.body = loginUser + " " + "joined this room!!";
                String body = gson.toJson(chatMessage);
                message1.setBody(body);
                message1.setType(Message.Type.groupchat);

                room.sendMessage(message1);//"I joined this room!! Bravo!!");
                room.addMessageListener(mGMessageListener);


            } catch (NotConnectedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void groupUpdate(ChatMessage chatMessage){

        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        Log.d("groupChatName", chatMessage.receiver + "@conference."
                + context.getString(R.string.server));
        MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(chatMessage.receiver + "@conference."
                + context.getString(R.string.server));

        Message message1 = new Message();
        String body = gson.toJson(chatMessage);
        message1.setBody(body);
        message1.setType(Message.Type.groupchat);

        try {
            multiUserChat.sendMessage(message1);
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public class MyReceiptReceivedListener implements ReceiptReceivedListener {

        @Override
        public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
            Log.d("", "onReceiptReceived: from: " +
                    fromJid + " to: " + toJid + " deliveryReceiptId: " + receiptId + " stanza: " + receipt);

            final ChatMessage message = new ChatMessage();
            message.receiptId = receiptId;
            message.msgStatus = "2";

            new DatabaseHelper(context).UpdateMsgStatus(message.msgStatus, message.receiptId);

            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {

                    if (ChatActivity.instance != null) {
                        Log.d("onReceiptReceived", ": a update: " );
                        if(callBackUi != null) {
                            callBackUi.update("2");
                        }
                        Log.d("onReceiptReceived", ": b update: " );
                        //ChatActivity.chatAdapter.updateStatus(message.msgStatus, message.receiptId);

                    }
                }
            });
        }
    }

}
