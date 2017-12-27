package com.speakame.Xmpp;

import com.speakame.utils.VolleyCallback;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by MAX on 21-Sep-16.
 */
public class ChatMessage {
    public int receiver_QB_Id = 0;
    public int sender_QB_Id = 0;
    public int friend_QB_Id = 0;
    public String dialog_id = " ";
    public String qbMessageId = " ";
    public String readStatus = "0";
    public String body, sender, receiver, senderName, reciverName, files, fileName, qbFileUid = "";
    public int qbFileUploadId;
    public String Date, Time;
    public String msgid;
    public String type;
    public String groupName;
    public String groupid;
    public String formID;
    public boolean isMine;// Did I send the message.
    public String senderlanguages;
    public String reciverlanguages;
    public String lastseen;
    public String ReciverFriendImage;
    public String MyImage;
    public String Groupimage = "";
    public String userStatus = "";
    public String msgStatus = "0";
    public String receiptId = "0";
    public String Video = "";
    public String Document = "";
    public String Contact = "";
    public String Image = "";
    public int isRead = 0;
    public int isOtherMsg = 0;
    public byte[] fileData;
    public VolleyCallback callback;
    public byte[] qbChatDialogBytes = null;
    private boolean isSelected = false;

    public ChatMessage(String Sender, String SenderName, String Receiver, String ReciverName, String groupname, String messageString,
                       String ID, String file, boolean isMINE, int sender_QB_Id) {
        body = messageString;
        isMine = isMINE;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        senderName = SenderName;
        reciverName = ReciverName;
        files = file;
        groupName = groupname;
        this.sender_QB_Id = sender_QB_Id;
    }

    public ChatMessage(String Sender, String SenderName, String Receiver, String ReciverName, String groupname, String messageString,
                       String ID, String file, boolean isMINE) {
        body = messageString;
        isMine = isMINE;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        senderName = SenderName;
        reciverName = ReciverName;
        files = file;
        groupName = groupname;
    }

    public ChatMessage() {

    }

    public void setMsgID() {

        msgid += "-" + String.format("%02d", new Random().nextInt(100));
        ;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public String toString() {

        String old_String = "{" +
                "body='" + body + '\'' +
                ", msgid='" + msgid + '\'' +
                ", readStatus='" + readStatus + '\'' +
                ", receiver_QB_Id='" + receiver_QB_Id + '\'' +
                ", sender_QB_Id='" + sender_QB_Id + '\'' +
                ", friend_QB_Id='" + friend_QB_Id + '\'' +
                ", qb_dialog_id='" + dialog_id + '\'' +
                ", qbFileUploadId='" + qbFileUploadId + '\'' +
                ", qbFileUid='" + qbFileUid + '\'' +
//                ", qbChatDialogBytes='" + qbChatDialogBytes + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", senderName='" + senderName + '\'' +
                ", reciverName='" + reciverName + '\'' +
                ", files='" + files + '\'' +
                ", fileName='" + fileName + '\'' +
                ", Date='" + Date + '\'' +
                ", Time='" + Time + '\'' +
                ", type='" + type + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupid='" + groupid + '\'' +
                ", formID='" + formID + '\'' +
                ", isMine=" + isMine +
                ", senderlanguages='" + senderlanguages + '\'' +
                ", reciverlanguages='" + reciverlanguages + '\'' +
                ", lastseen='" + lastseen + '\'' +
                ", ReciverFriendImage='" + ReciverFriendImage + '\'' +
                ", MyImage='" + MyImage + '\'' +
                ", Groupimage='" + Groupimage + '\'' +
                ", msgStatus='" + msgStatus + '\'' +
                ", receiptId='" + receiptId + '\'' +
                ", isSelected=" + isSelected +
                ", fileData=" + Arrays.toString(fileData) +
                ", callback=" + callback +
                '}';

        String new_String = "{" +
                "\"" + "msgid" + "\"" + ":" + "\"" + msgid + "\"" + "," +
                "\"" + "receiver_QB_Id" + "\"" + ":" + "\"" + receiver_QB_Id + "\"" + "," +
                "\"" + "sender_QB_Id" + "\"" + ":" + "\"" + sender_QB_Id + "\"" + "," +
                "\"" + "body" + "\"" + ":" + "\"" + body + "\"" + "," +
                "\"" + "qbFileUploadId" + "\"" + ":" + "\"" + qbFileUploadId + "\"" + "," +
                "\"" + "qbFileUid" + "\"" + ":" + "\"" + qbFileUid + "\"" + "," +
                "\"" + "sender" + "\"" + ":" + "\"" + sender + "\"" + "," +
                "\"" + "receiver" + "\"" + ":" + "\"" + receiver + "\"" + "," +
                "\"" + "senderName" + "\"" + ":" + "\"" + senderName + "\"" + "," +
                "\"" + "reciverName" + "\"" + ":" + "\"" + reciverName + "\"" + "," +
                "\"" + "files" + "\"" + ":" + "\"" + files + "\"" + "," +
                "\"" + "fileName" + "\"" + ":" + "\"" + fileName + "\"" + "," +
                "\"" + "Date" + "\"" + ":" + "\"" + Date + "\"" + "," +
                "\"" + "Time" + "\"" + ":" + "\"" + Time + "\"" + "," +
                "\"" + "type" + "\"" + ":" + "\"" + type + "\"" + "," +
                "\"" + "groupName" + "\"" + ":" + "\"" + groupName + "\"" + "," +
                "\"" + "groupid" + "\"" + ":" + "\"" + groupid + "\"" + "," +
                "\"" + "formID" + "\"" + ":" + "\"" + formID + "\"" + "," +
                "\"" + "isMine" + "\"" + ":" + "\"" + isMine + "\"" + "," +
                "\"" + "senderlanguages" + "\"" + ":" + "\"" + senderlanguages + "\"" + "," +
                "\"" + "reciverlanguages" + "\"" + ":" + "\"" + reciverlanguages + "\"" + "," +
                "\"" + "lastseen" + "\"" + ":" + "\"" + lastseen + "\"" + "," +
                "\"" + "ReciverFriendImage" + "\"" + ":" + "\"" + ReciverFriendImage + "\"" + "," +
                "\"" + "MyImage" + "\"" + ":" + "\"" + MyImage + "\"" + "," +
                "\"" + "Groupimage" + "\"" + ":" + "\"" + Groupimage + "\"" + "," +
                "\"" + "msgStatus" + "\"" + ":" + "\"" + msgStatus + "\"" + "," +
                "\"" + "receiptId" + "\"" + ":" + "\"" + receiptId + "\"" + "," +
                "\"" + "isSelected" + "\"" + ":" + "\"" + isSelected + "\"" + "," +
                "\"" + "fileData" + "\"" + ":" + "\"" + fileData + "\"" + "," +
                "\"" + "callback" + "\"" + ":" + "\"" + callback + "\"" +
                '}';

        return old_String;
    }
}
