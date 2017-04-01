package com.speakame.Xmpp;

import com.speakame.utils.VolleyCallback;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by MAX on 21-Sep-16.
 */
public class ChatMessage {
    public String body, sender, receiver, senderName, reciverName, files, fileName;
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
    public String Groupimage="";
    public String msgStatus="0";
    public String receiptId="0";
    public int isRead = 0;
    private boolean isSelected = false;
    public byte[] fileData;
    public VolleyCallback callback;

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


    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "body='" + body + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", senderName='" + senderName + '\'' +
                ", reciverName='" + reciverName + '\'' +
                ", files='" + files + '\'' +
                ", fileName='" + fileName + '\'' +
                ", Date='" + Date + '\'' +
                ", Time='" + Time + '\'' +
                ", msgid='" + msgid + '\'' +
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
    }
}
