package com.speakameqb.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
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
import com.speakameqb.Activity.ChatActivity;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Database.BlockUserDataBaseHelper;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.R;
import com.speakameqb.Xmpp.ChatMessage;
import com.speakameqb.Xmpp.CommonMethods;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Function;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import static com.speakameqb.Activity.ChatActivity.chatAdapter;
import static com.speakameqb.Activity.ChatActivity.chatlist;
import static com.speakameqb.Activity.ChatActivity.mRecyclerView;
import static com.speakameqb.Activity.ForwardActivity.footer;
import static com.speakameqb.Activity.ForwardActivity.footerHide;
import static com.speakameqb.Activity.ForwardActivity.footerShow;
import static com.speakameqb.Activity.ForwardActivity.forwardedName;


/**
 * Created by MMFA-YOGESH on 9/12/2016.
 */
public class ForwardAdapter extends RecyclerView.Adapter<ForwardAdapter.MyViewHolder> {

    private static final String TAG = "ForwardAdapter";
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    Activity context;
    String name = "";
    int qbFileId = 0;
    String qbFileUid = "";
    Calendar calander;
    TimeZone timezone;
    private List<ChatMessage> chatMessageList;
    private SparseBooleanArray mSelectedItemsIds;
    private Random random;
    private String fileUrl = "";
    private Date date;

    public ForwardAdapter(Activity context, List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
        this.context = context;
        mSelectedItemsIds = new SparseBooleanArray();
        random = new Random();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case 0: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.forward_item, parent, false);

                return new MyViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);
        holder.txtname.setText(chatMessage.reciverName);
        String image = chatMessage.ReciverFriendImage;
        if (image != null) {
            if (!image.equalsIgnoreCase("")) {
                Picasso.with(context).load(image).error(R.drawable.profile_default)
                        .resize(200, 200)
                        .into(holder.imageView);
            }
        }
        holder.txtmsg.setText(chatMessage.body);
        holder.txtminute.setText(chatMessage.Time);

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public void messageForward(JSONArray forwardMsg) {
        for (int i = 0; i < mSelectedItemsIds.size(); i++) {
            int id = mSelectedItemsIds.keyAt(i);
            Log.d(TAG, " idforuser : -- " + id);
            ChatMessage chatMessage = chatMessageList.get(id);

            for (int k = 0; k < forwardMsg.length(); k++) {

                String user1 = AppPreferences.getMobileuser(context);
                String userName = AppPreferences.getFirstUsername(context);
                String user2 = chatMessage.receiver;
                String FriendName = chatMessage.reciverName;
                String groupName = chatMessage.groupName;
                String message = "", file = "", fileName = "";
                File file1 = null;
                try {
                    message = forwardMsg.getJSONObject(k).getString("msg");
                    Log.d("fileeeee", forwardMsg.getJSONObject(k).getString("file"));
                    file = forwardMsg.getJSONObject(k).getString("file");
                    // file1 = new File(forwardMsg.getJSONObject(k).getString("fileName"));
                    file1 = new File(file);
                    Log.d(TAG, " FileAllDetailsss : -- " + file + " :: " + file1);
                    fileName = file1.getName();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String reciverlanguages = chatMessage.reciverlanguages;
                String FriendImage = chatMessage.ReciverFriendImage;
                int qbId = chatMessage.friend_QB_Id;
                String qb_dialog_id = chatMessage.dialog_id;
                Log.d(TAG, " qb_dialog_id :--  " + qb_dialog_id);
                byte[] chatbytes = DatabaseHelper.getInstance(context).getChatDialogUsingDialogID(qb_dialog_id);
//sdvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                QBChatDialog qbChatDialog = SerializationUtils.deserialize(chatbytes);
                Log.d(TAG, " qbChatDialog :--  " + qbChatDialog);

                //  sendTextMessage(user1, userName, user2, FriendName, groupName, message,file, fileName, reciverlanguages, FriendImage);
                sharingFilesFromMethod(file1, file, groupName, FriendName, user1, message, fileName, reciverlanguages, qbId, user2, FriendImage, qbChatDialog);

                if (mSelectedItemsIds.size() == 1) {
                    Log.d(TAG, " Seletected itemd If : -- " + mSelectedItemsIds.size());
                    qbChatDialog.initForChat(QBChatService.getInstance());
                    AllBeans allBeans = new AllBeans();
                    allBeans.setFriendname(chatMessage.reciverName);
                    allBeans.setFriendStatus(chatMessage.userStatus);
                    allBeans.setFriendmobile(chatMessage.receiver);
                    allBeans.setGroupName(chatMessage.groupName);
                    allBeans.setGroupid(chatMessage.groupid);
                    allBeans.setLanguages(chatMessage.reciverlanguages);
                    allBeans.setFriendimage(chatMessage.ReciverFriendImage);
                    allBeans.setGroupImage(chatMessage.Groupimage);
                    allBeans.setFriendQB_id(chatMessage.friend_QB_Id);

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setAction("");
                    intent.putExtra("value", allBeans);
                    intent.putExtra("groupName", "");
                    intent.putExtra("requestFilePath", "");
                    intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, qbChatDialog);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        context.setResult(RESULT_OK, intent);
                    context.startActivity(intent);
                    // context.finish();
                } else {
                    Log.d(TAG, "Selected itemd Else : -- " + mSelectedItemsIds.size());

                    context.finish();
                }
            }
        }
    }

    public void sendTextMessagennn(String user1, String userName, String user2, String FriendName, String groupName, String message,
                                   String file, String fileName, String reciverlanguages, String FriendImage) {
        String MyImage = "";
        String MyStatus = "";
        if (AppPreferences.getPicprivacy(context).equalsIgnoreCase(AppConstants.EVERYONE)) {
            MyImage = AppPreferences.getUserprofile(context);
            MyStatus = AppPreferences.getUserstatus(context);
        } else if (AppPreferences.getPicprivacy(context).equalsIgnoreCase(AppConstants.MYFRIENDS)) {
            if (!Function.isStringInt(FriendName)) {
                MyImage = AppPreferences.getUserprofile(context);
                MyStatus = AppPreferences.getUserstatus(context);
            }
        }

        final ChatMessage chatMessage = new ChatMessage(user1, userName, user2, FriendName,
                groupName, message, "" + random.nextInt(1000), file, true);

        chatMessage.setMsgID();
        chatMessage.body = message;
        chatMessage.fileName = fileName;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.type = Message.Type.chat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(context));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(context);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.MyImage = MyImage;
        chatMessage.userStatus = MyStatus;
        chatMessage.lastseen = new DatabaseHelper(context).getLastSeen(user2);
        //chatMessage.fileData = fileData;
        // msg_edittext.setText("");
        //fm.setVisibility(View.GONE);
        //TwoTab_Activity activity = new TwoTab_Activity();

        chatMessage.ReciverFriendImage = FriendImage;
        chatMessage.msgStatus = "0";

        if (!fileName.equalsIgnoreCase("")) {

            String fileExte = Function.getFileExtention(fileName);
            String folderType;

            String msg = chatMessage.body;
            if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
                folderType = "SpeakAme Contact";
            } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "SpeakAme Image";
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                folderType = "SpeakAme Video";
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "SpeakAme Document";
            } else {
                folderType = "SpeakAme Test";
            }

            File SpeakAmeDirectory = Function.createFolder(folderType);
            chatMessage.fileName = Function.generateNewFileName(fileExte);
            chatMessage.files = Function.copyFile(file, SpeakAmeDirectory + "/" + chatMessage.fileName);

           /* File file2= null;
            try {
                file2 = Function.decodeBase64BinaryToFile(SpeakAmeDirectory.toString(), Function.generateNewFileName(fileExte), file);
                chatMessage.fileName = file2.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            Log.d("IMAGEPATH filename", SpeakAmeDirectory + "\n" + chatMessage.fileName + "\n" + chatMessage.files);

        } else {

            /*XmppConneceted activity = new XmppConneceted();
            activity.getmService().xmpp.sendMessage(chatMessage);*/

        }

        Log.d("ChatMessage save", chatMessage.toString());
       /* if(chatMessage.fileName.contains("mp4") || chatMessage.fileName.contains("jpg")|| chatMessage.fileName.contains("pdf")){
            chatMessage.files = chatMessage.fileName;
        }*/
        DatabaseHelper.getInstance(context).insertChat(chatMessage);
        DatabaseHelper.getInstance(context).UpdateMsgRead("1", chatMessage.receiver);
        chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    public void sendTextMessage1(String user1, String userName, String user2, String FriendName, String groupName, String message,
                                 String file, String fileName, String reciverlanguages, String FriendImage) {
        final ChatMessage chatMessage = new ChatMessage(user1, userName, user2, FriendName,
                groupName, message, "" + random.nextInt(1000), file, true);
        chatMessage.setMsgID();
        chatMessage.body = message;
        chatMessage.fileName = fileName;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.type = Message.Type.chat.name();
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(context));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(context);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.MyImage = AppPreferences.getUserprofile(context);

        //TwoTab_Activity activity = new TwoTab_Activity();

     /*   XmppConneceted activity = new XmppConneceted();
        activity.getmService().xmpp.sendMessage(chatMessage, new CallBackUi() {
            @Override
            public void update(String s) {

            }
        });*/


        chatMessage.ReciverFriendImage = FriendImage;

        if (fileName == null
                ) {
        } else if (!fileName.equalsIgnoreCase("")) {

            String fileExte = MimeTypeMap.getFileExtensionFromUrl(fileName);
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
            File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/" + folderType + "/send");
            if (!SpeakaMeDirectory.exists()) {
                SpeakaMeDirectory.mkdirs();
            }
            // File file1 = new File(SpeakaMeDirectory, transfer.getFileName());
            Log.v(TAG, "File for decode :- " + file);
            try {
                File file2 = Function.decodeBase64BinaryToFile(SpeakaMeDirectory.toString(), fileName, file);
                chatMessage.fileName = file2.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        DatabaseHelper.getInstance(context).insertChat(chatMessage);
        chatlist.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
    }

    public void sharingFilesFromMethod(final File qbFile, final String filePath, final String groupName, final String FriendName, final String user1,
                                       final String message, final String fileName, final String reciverlanguages, final int QB_Friend_Id, final String user2,
                                       final String FriendImage, final QBChatDialog qbChatDialog) {
        int fileSize = Function.getFileSize(filePath);
        Log.d(TAG, "fileSize" + groupName + ">>>" + fileSize);

        timezone = TimeZone.getDefault();
        calander = Calendar.getInstance(timezone);
        date = calander.getTime();
        //if(fileSize < )
        //*************************************  For shwoing image first to the adapter and add to database then sending image to server  *********************************//
            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code updated on 09 January 2018 by Ravi ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        String MyImage = "";
        String MyStatus = "";
        if (AppPreferences.getPicprivacy(context).equalsIgnoreCase(AppConstants.EVERYONE)) {
            MyImage = AppPreferences.getUserprofile(context);
            MyStatus = AppPreferences.getUserstatus(context);
        } else if (AppPreferences.getPicprivacy(context).equalsIgnoreCase(AppConstants.MYFRIENDS)) {
            if (!Function.isStringInt(FriendName)) {
                MyImage = AppPreferences.getUserprofile(context);
                MyStatus = AppPreferences.getUserstatus(context);
            }
        }
//if(fileSize < ) {
        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(context), user2, FriendName,
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
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(context));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(context);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.MyImage = MyImage;
        chatMessage.userStatus = MyStatus;
        chatMessage.lastseen = new DatabaseHelper(context).getLastSeen(user2);
        chatMessage.receiver_QB_Id = QB_Friend_Id;
        chatMessage.sender_QB_Id = AppPreferences.getQBUserId(context);
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
        chatMessage.dialog_id = qbChatDialog.getDialogId();
        chatMessage.qbChatDialogBytes = SerializationUtils.serialize(qbChatDialog);
        if (chatMessage.groupName.equalsIgnoreCase("")) {
            qbChatMessage.setRecipientId(chatMessage.receiver_QB_Id);
        }

        Log.d(TAG, " After Adding the QbChatDialog " + chatMessage.qbMessageId + " :....: " + chatMessage.dialog_id);
       /* msg_edittext.setText("");
        fm.setVisibility(View.GONE);*/

        chatMessage.ReciverFriendImage = FriendImage;
        chatMessage.msgStatus = "0";

       /* chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);*/
        Log.d(TAG, " chatMeaafe chatMessage.qbChatDialogBytes : -- " + chatMessage.qbChatDialogBytes);
        Log.d(TAG, " chatMeaafeFile : -- " + chatMessage.toString());
        DatabaseHelper.getInstance(context).insertChat(chatMessage);
        DatabaseHelper.getInstance(context).UpdateMsgRead("1", chatMessage.receiver);
/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ "end" Code updated on 09 January 2018 by Ravi ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        Log.d(TAG, " chatMeaafeFile new : -- " + chatMessage.toString());
        Log.d(TAG, " chatMeaafeFile qbFile : -- " + qbFile + " : " + filePath);
        if (filePath.equalsIgnoreCase("") || qbFile == null) {
            Log.d(TAG, " sendMessageText If: -- " + chatMessage.body);
            sendTextMessage(chatMessage.body, "", "", 0, "0", "", qbChatMessage, FriendName, user1, user2, groupName, reciverlanguages, QB_Friend_Id, FriendImage, qbChatDialog);
            ///  sendTextMessage(message, "", "", 0, "0", "", null);

        } else {

            Log.d(TAG, " sendMessageImage Else : -- " + chatMessage.body);

            Boolean fileIsPublic = false;

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
                        sendTextMessage(chatMessage.body, filePath, fileName, qbFileId, qbFileUid, fileUrl, qbChatMessage, FriendName, user1, user2, groupName, reciverlanguages, QB_Friend_Id, FriendImage, qbChatDialog);
                    } else {
                        Log.v(TAG, "Inside sending group chat message:- " + fileUrl);
                        //sendGroupMessage(message, filePath, fileName, qbFileId, qbFileUid, fileUrl, qbChatMessage);
                    }
                }

                @Override
                public void onError(QBResponseException e) {

                    Log.e(TAG, "Error in uploading image :- " + e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    //************************************************************************************************************//

    public void sendTextMessage(String message, String file, String fileName, int qbFileId, String qbFileUid, String fileUrl, QBChatMessage qbChatMessage_file,
                                String FriendName, String user1, String user2, String groupName, String reciverlanguages, int QB_Friend_Id, String FriendImage,
                                final QBChatDialog qbChatDialog) {

        // TODO: sendTextMessage() method for sending private chat message to QuickBlox
        Gson gson = new Gson();
        Log.v(TAG, "Inside text message sendTextMessage \n File :- " + file);
        Log.v(TAG, "Filename :- " + fileName);
        Log.v(TAG, "QB file id :- " + qbFileId);
        Log.v(TAG, "QB qbFileUid :- " + qbFileUid);
        Log.v(TAG, "User QB id  i.e. Sender id :- " + AppPreferences.getQBUserId(context));
        String MyImage = "";
        String MyStatus = "";

        if (AppPreferences.getPicprivacy(context).equalsIgnoreCase(AppConstants.EVERYONE)) {
            MyImage = AppPreferences.getUserprofile(context);
            MyStatus = AppPreferences.getUserstatus(context);
        } else if (AppPreferences.getPicprivacy(context).equalsIgnoreCase(AppConstants.MYFRIENDS)) {
            if (!Function.isStringInt(FriendName)) {
                MyImage = AppPreferences.getUserprofile(context);
                MyStatus = AppPreferences.getUserstatus(context);
            }
        }

        final ChatMessage chatMessage = new ChatMessage(user1, AppPreferences.getFirstUsername(context), user2, FriendName,
                groupName, message, "" + random.nextInt(1000), file, true);
        chatMessage.setMsgID();
        chatMessage.body = message;
        chatMessage.fileName = fileName;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();

        Log.v(TAG, "chatMessage.Date ..........12341435 :- " + chatMessage.Date);
        Log.v(TAG, "chatMessage.Time ..........12341435 :- " + chatMessage.Time);

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
        chatMessage.formID = String.valueOf(AppPreferences.getLoginId(context));
        chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(context);
        chatMessage.reciverlanguages = reciverlanguages;
        chatMessage.MyImage = MyImage;
        chatMessage.userStatus = MyStatus;
        chatMessage.lastseen = new DatabaseHelper(context).getLastSeen(user2);
        chatMessage.receiver_QB_Id = QB_Friend_Id;
        chatMessage.sender_QB_Id = AppPreferences.getQBUserId(context);
        chatMessage.friend_QB_Id = QB_Friend_Id;
        chatMessage.readStatus = "0";

       /* onlineStatus = new DatabaseHelper(context).getLastSeen(user2);
        //chatMessage.fileData = fileData;
        msg_edittext.setText("");
        fm.setVisibility(View.GONE);*/
        //ChatActivity activity = new ChatActivity();

        chatMessage.ReciverFriendImage = FriendImage;
        chatMessage.msgStatus = "0";

        Log.v(TAG, "TOTF MESSAGE Testing message files before :- " + chatMessage.files);
        chatMessage.files = "";
        Log.v(TAG, "TOTF MESSAGE Testing message files after :- " + chatMessage.files);


        BlockUserDataBaseHelper blockUserDataBaseHelper = new BlockUserDataBaseHelper(context);
        Log.d(TAG, " FriendNameMessa : " + FriendName + " FriendMobileMessa : " + "" + " FriendOcupantIdMessa : " + QB_Friend_Id);
        ArrayList<Integer> allBlockedUsers = blockUserDataBaseHelper.getAllBlockedUsers();
        Log.d(TAG, " GetAllBlokedUsersMessa .. " + allBlockedUsers.toString());
        Log.d(TAG, " fileName _ 11: " + fileName);
        if (!fileName.equalsIgnoreCase("")) {
            Log.d(TAG, " fileName chatMessage_ : " + chatMessage);
            Log.d(TAG, " fileName22 _ : " + fileName);

            String fileExte = Function.getFileExtention(fileName);
            String folderType;
            Log.d(TAG, " fileNamefileExte _ : " + fileExte);

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
                // unblockpopup();
            } else {
                chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
                chatMessage.timeZone = timezone.getID();
                String body = gson.toJson(chatMessage);
                qbChatMessage_file.setProperty("custom_body", body);
                sendChatMessage(body, chatMessage, "FileSharing", qbChatMessage_file, qbChatDialog);
//              dvsvsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsd
                Log.d(TAG, "That user is Unblocked");
            }

        } else {

            Log.v(TAG, "ChatMessage Save 1:- " + chatMessage.toString());

            if (allBlockedUsers.contains(QB_Friend_Id)) {

                Log.d(TAG, " That user is blocked");
                // unblockpopup();

            } else {

//            String body = gson.toJson(chatMessage);
                chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
                chatMessage.timeZone = timezone.getID();

                chatMessage.dateInLong = Calendar.getInstance().getTimeInMillis();
                chatMessage.timeZone = timezone.getID();
                String body = gson.toJson(chatMessage);
                qbChatMessage_file.setProperty("custom_body", body);

                //   String body = gson.toJson(chatMessage);

              /*  QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(chatMessage.body);
                qbChatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                qbChatMessage.setProperty("custom_body", body);
                qbChatMessage.setDateSent(date.getTime());
                qbChatMessage.setMarkable(true);
                qbChatMessage.setSenderId(chatMessage.sender_QB_Id);*/
//vdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
                // chatMessage.qbMessageId = qbChatMessage.getId();
//csaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                chatMessage.dialog_id = qbChatDialog.getDialogId();

//                fdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddb
                chatMessage.qbChatDialogBytes = SerializationUtils.serialize(qbChatDialog);

                if (chatMessage.groupName.equalsIgnoreCase("")) {

                    qbChatMessage_file.setRecipientId(chatMessage.receiver_QB_Id);

                }
//                vsddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd

                Log.v(TAG, "QB Message n Dialog Id :- \n" + "1. " + chatMessage.qbMessageId + "\n2. " + chatMessage.dialog_id);
                //  sendChatMessage(body, chatMessage, "Normal_Message", qbChatMessage);
                sendChatMessage(body, chatMessage, "Normal_Message", qbChatMessage_file, qbChatDialog);
                Log.d(TAG, "That user is Unblocked");
            }
        }

    }
//--------------------------------------------------------------------------------------------------//

    private void sendChatMessage(final String text, final ChatMessage chatMessage, final String messageType, QBChatMessage qbChatMessage, final QBChatDialog qbChatDialog) {
        // TODO: send chatMessage() method for sending message to QuickBlox
        qbChatDialog.initForChat(QBChatService.getInstance());

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

        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType()) && !qbChatDialog.isJoined()) {
            Toast.makeText(context, "You're still joining a group chat, please wait a bit", Toast.LENGTH_SHORT).show();
            return;
        }

       /* if (chatMessage.groupName.equalsIgnoreCase("")) {
            privateChatDialog.addMessageSentListener(privateChatDialogMessageSentListener);
        } else {
            privateChatDialog.addMessageSentListener(groupChatDialogMessageSentListener);
        }*/
        try {
            Log.e(TAG, "privateChatDialog while sending message to prsonal chat :- " + qbChatDialog);
            Log.e(TAG, "Messsage body sendQB CHAT Message :- " + qbChatMessage);

            qbChatDialog.sendMessage(qbChatMessage);  // msg send to all online / offline users

            if (chatMessage.groupName.equalsIgnoreCase("")) {

                byte[] qbChatDialogBytes = SerializationUtils.serialize(qbChatDialog);
                Log.v(TAG, "QB Serialize dialog to data 1 :- " + chatMessage.qbChatDialogBytes);
                Log.v(TAG, "QB Serialize dialog to data 2 :- " + qbChatDialogBytes);

//                QBChatDialog yourObject = SerializationUtils.deserialize(chatMessage.qbChatDialogBytes);
                QBChatDialog yourObject = SerializationUtils.deserialize(qbChatDialogBytes);
                Log.v(TAG, "QB Serialize data to dialog 2 :- " + yourObject);

                if (messageType.equalsIgnoreCase("FileSharing")) {
                    DatabaseHelper.getInstance(context).UpdateQbFileIdAndqbFileUid(chatMessage.qbFileUploadId, chatMessage.qbFileUid, chatMessage.dialog_id, chatMessage.qbMessageId);
                } else if (messageType.equalsIgnoreCase("Normal_Message")) {
                    Log.d(TAG, " messageInFiConditon : -- " + chatMessage);

                    // DatabaseHelper.getInstance(context).insertChat(chatMessage);
                    //   DatabaseHelper.getInstance(context).UpdateMsgRead("1", chatMessage.receiver);
                    //  chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
                    // mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }

            } else {
                Log.d(TAG, " messageInElseiConditon : -- " + chatMessage);

//                Toast.makeText(getApplicationContext(), "inside sending message to Groups chat ...", Toast.LENGTH_SHORT).show();
                if (messageType.equalsIgnoreCase("FileSharing")) {
                    DatabaseHelper.getInstance(context).UpdateQbFileIdAndqbFileUid(chatMessage.qbFileUploadId, chatMessage.qbFileUid, chatMessage.dialog_id, chatMessage.qbMessageId);
                } else if (messageType.equalsIgnoreCase("Normal_Message")) {
                    DatabaseHelper.getInstance(context).insertChat(chatMessage);
                    DatabaseHelper.getInstance(context).UpdateMsgRead("1", chatMessage.receiver);
                    chatAdapter.add(chatMessage, chatAdapter.getItemCount() - 1);
                    mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }

            if (ChatActivity.instance == null && ChatActivity.instance == null) {

                Log.e(TAG, "send notification ");
//                        generateNofification(chatMessage, context);
            }
//fbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
            sendNotification(qbChatDialog.getOccupants(), chatMessage.body, chatMessage.senderName, chatMessage.sender);

            MediaPlayer mp = MediaPlayer.create(context, R.raw.tick);
            if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
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
//****************************************************************************************************//

    //******************************************************************************************************//
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtname;
        TextView txtmsg;
        TextView txtminute;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            txtname = (TextView) view.findViewById(R.id.nametext);
            txtmsg = (TextView) view.findViewById(R.id.messagetext);
            txtminute = (TextView) view.findViewById(R.id.minutetext);
            imageView = (ImageView) view.findViewById(R.id.image);

            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
            txtname.setTypeface(tf1);
            txtmsg.setTypeface(tf1);
            txtminute.setTypeface(tf1);

        }

        @Override
        public void onClick(View v) {
            ChatMessage chatMessage = chatMessageList.get(getAdapterPosition());
            chatMessage.setSelected(!chatMessage.isSelected());
            View innerContainer = v.findViewById(R.id.innerContainer);
            innerContainer.setBackgroundColor(chatMessage.isSelected() ? Color.GRAY : 0);

            if (mSelectedItemsIds.get(getAdapterPosition(), false)) {
                mSelectedItemsIds.delete(getAdapterPosition());

                String name = forwardedName.getText().toString().replace(chatMessage.reciverName, "");
                forwardedName.setText(name);
            } else {
                mSelectedItemsIds.put(getAdapterPosition(), true);
                if (TextUtils.isEmpty(forwardedName.getText().toString())) {
                    forwardedName.setText(chatMessage.reciverName);
                } else {
                    forwardedName.setText(chatMessage.reciverName + " " + forwardedName.getText().toString());
                }
            }

            if (footer.getVisibility() == View.VISIBLE) {
                if (mSelectedItemsIds.size() == 0) {
                    footerHide();
                }
            } else {
                footerShow();
            }
        }
    }
}

