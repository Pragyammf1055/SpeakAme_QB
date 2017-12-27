package com.speakame.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.speakame.Activity.ChatActivity.chatAdapter;
import static com.speakame.Activity.ChatActivity.chatlist;
import static com.speakame.Activity.ChatActivity.mRecyclerView;
import static com.speakame.Activity.ForwardActivity.footer;
import static com.speakame.Activity.ForwardActivity.footerHide;
import static com.speakame.Activity.ForwardActivity.footerShow;
import static com.speakame.Activity.ForwardActivity.forwardedName;

/**
 * Created by MMFA-YOGESH on 9/12/2016.
 */
public class ForwardAdapter extends RecyclerView.Adapter<ForwardAdapter.MyViewHolder> {

    private static final String TAG = "ForwardAdapter";
    Context context;
    String name = "";
    private List<ChatMessage> chatMessageList;
    private SparseBooleanArray mSelectedItemsIds;
    private Random random;

    public ForwardAdapter(Context context, List<ChatMessage> chatMessageList) {
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
        ChatMessage chatMessage  = chatMessageList.get(position);
        holder.txtname.setText(chatMessage.reciverName);
        String image = chatMessage.ReciverFriendImage;
        if(image != null){
            if(!image.equalsIgnoreCase("")){
                Picasso.with(context).load(image).error(R.drawable.user_icon)
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

    public void messageForward(JSONArray forwardMsg){
        for(int i = 0; i < mSelectedItemsIds.size(); i++){
            int id = mSelectedItemsIds.keyAt(i);
            ChatMessage chatMessage = chatMessageList.get(id);

            for(int k=0; k<forwardMsg.length(); k++){

                String user1 = AppPreferences.getMobileuser(context);
                String userName = AppPreferences.getFirstUsername(context);
                String user2 = chatMessage.receiver;
                String FriendName = chatMessage.reciverName;
                String groupName = chatMessage.groupName;
                String message = "",file = "",fileName = "";
                try {
                    message = forwardMsg.getJSONObject(k).getString("msg");
                    Log.d("fileeeee",forwardMsg.getJSONObject(k).getString("file"));
                    file = forwardMsg.getJSONObject(k).getString("file");
                    File file1 = new File(forwardMsg.getJSONObject(k).getString("fileName"));
                    fileName = file1.getName();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String reciverlanguages = chatMessage.reciverlanguages;
                String FriendImage = chatMessage.ReciverFriendImage;
                sendTextMessage(user1, userName, user2, FriendName, groupName, message,
                        file, fileName, reciverlanguages, FriendImage);
            }

        }

    }

    public void sendTextMessage(String user1, String userName, String user2, String FriendName, String groupName, String message,
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

        if(fileName == null
                ) {
        }else if (!fileName.equalsIgnoreCase("")) {

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

