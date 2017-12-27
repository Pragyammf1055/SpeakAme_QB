package com.speakame.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.quickblox.core.helper.StringifyArrayList;
import com.speakame.Beans.AllBeans;
import com.speakame.Beans.User;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.speakame.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAX on 21-Sep-16.
 */
public class DBDataGet {


    private static final String TAG = "DBDataGet";

    public static ArrayList<ChatMessage> getChat(SQLiteDatabase db, String which, String keyname) {

        ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        try {
            String query;
            if (which.equalsIgnoreCase("chat")) {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' ORDER BY " + DBTable.KEY_ID + " ASC ";
            } else {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_GROUPNAME + " = '" + keyname + "' ORDER BY " + DBTable.KEY_ID + " ASC ";
            }
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return chatMessageList;
            } else if (cursor.getCount() == 0) {
                return chatMessageList;
            }

            if (cursor.moveToFirst()) {

                do {
                    Log.d("CHATLISTSS count 1", cursor.getCount() + "\n" + cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER)));
                    String Sender = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER));
                    String Receiver = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIVER));
                    String SenderName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDERNAME));
                    String ReciverName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECIVERNAME));
                    String RecLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECLANGUAGE));
                    String SendLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDLANGUAGE));
                    String messageString = cursor.getString(cursor.getColumnIndex(DBTable.KEY_BODY));
                    String file = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FILE));
                    String filename = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FILENAME));
                    byte[] fileData = cursor.getBlob(cursor.getColumnIndex(DBTable.KEY_FILEDATA));
                    String DateTime = cursor.getString(cursor.getColumnIndex(DBTable.KEY_TIME));
                    String MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGID));
                    String MSGTYPE = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGTYPE));
                    String GroupName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPNAME));
                    String msgStatus = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGSTATUS));
                    String receiptID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIPTID));
                    String userStatus = cursor.getString(cursor.getColumnIndex(DBTable.KEY_USERSTATUS));
                    int isOtherMsg = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISOTHERMSG));

                    boolean isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;

                    String readStatus = cursor.getString(cursor.getColumnIndex(DBTable.KEY_READ_STATUS));
                    int receiver_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_RECIVER_ID));
                    int sender_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_SENDER_ID));
                    int friend_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_FRIEND_ID));

                    byte[] qbChatDialogBytes = cursor.getBlob(cursor.getColumnIndex(DBTable.KEY_QBCHATDIALOG_BYTES));
                    String KEY_QB_MESSAGE_ID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_QB_MESSAGE_ID));

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, file, isMINE, sender_qb_id);

                    chatMessage.Time = DateTime;
                    chatMessage.type = MSGTYPE;
                    chatMessage.fileName = filename;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.msgStatus = msgStatus;
                    chatMessage.receiptId = receiptID;
                    chatMessage.fileData = fileData;
                    chatMessage.userStatus = userStatus;
                    chatMessage.isOtherMsg = isOtherMsg;
                    chatMessage.receiver_QB_Id = receiver_qb_id;
                    chatMessage.friend_QB_Id = friend_qb_id;
                    chatMessage.readStatus = readStatus;

                    if (qbChatDialogBytes != null) {

                        chatMessage.qbChatDialogBytes = qbChatDialogBytes;
                    }

                    chatMessageList.add(chatMessage);

                    Log.d(TAG, "Get Sender Details :- " + sender_qb_id);
                    Log.d(TAG, "Get Receiver Details :- " + receiver_qb_id);
                    Log.d(TAG, "Get Friend Details :- " + friend_qb_id);
                    Log.d(TAG, "Get read status getChat :- " + readStatus);
                    Log.d(TAG, "Get qbChatDialogBytes 1 :- " + qbChatDialogBytes);
//                    Log.d(TAG, "Get full QBChatDialog 1 :- " + SerializationUtils.deserialize(qbChatDialogBytes));

                    Log.d("CHATLISTSS count 2", chatMessage.toString() + "\n>>>>>>>>>>>>>>>>");

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CHATLISTSS Exception", e.getMessage());
        }

        //closeConnecion();
        return chatMessageList;
    }

    public static List<ChatMessage> getReciever(SQLiteDatabase db) {

//dscccccccccccccccccccccc

        Log.v(TAG, "Inside Get Receiver details .... ");

        List<ChatMessage> arrayList = new ArrayList<ChatMessage>();
        try {
            String query = "select * from " + DBTable.TBL_CHAT + " WHERE 1 AND " + DBTable.KEY_GROUPNAME + " IS NULL OR " + DBTable.KEY_GROUPNAME + " = '' GROUP BY " + DBTable.KEY_RECEIVER + " ORDER BY " + DBTable.KEY_ID + " DESC";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return arrayList;
            } else if (cursor.getCount() == 0) {
                return arrayList;
            }

            if (cursor.moveToFirst()) {
                do {
                    String Sender = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER));
                    String Receiver = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIVER));
                    String SenderName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDERNAME));
                    String ReciverName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECIVERNAME));
                    String RecLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECLANGUAGE));
                    String SendLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDLANGUAGE));
                    String messageString = cursor.getString(cursor.getColumnIndex(DBTable.KEY_BODY));
                    String MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGID));
                    String time = cursor.getString(cursor.getColumnIndex(DBTable.KEY_TIME));
                    String date = cursor.getString(cursor.getColumnIndex(DBTable.KEY_DATE));
                    String MSGTYPE = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGTYPE));
                    String GroupName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPNAME));
                    String FriendImg = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FRIENDIMAGE));
                    String msgStatus = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGSTATUS));
                    String receiptId = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIPTID));
                    String userStatus = cursor.getString(cursor.getColumnIndex(DBTable.KEY_USERSTATUS));

                    boolean isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;

                    int receiver_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_RECIVER_ID));
                    int sender_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_SENDER_ID));
//                    int friend_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_SENDER_ID));
                    int friend_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_FRIEND_ID));
                    String dialog_id = cursor.getString(cursor.getColumnIndex(DBTable.KEY_QB_DIALOG_ID));

                    String readStatus = cursor.getString(cursor.getColumnIndex(DBTable.KEY_READ_STATUS));

                    byte[] qbChatDialogBytes = cursor.getBlob(cursor.getColumnIndex(DBTable.KEY_QBCHATDIALOG_BYTES));

                    Log.d(TAG, "Get Sender Details :- " + sender_qb_id);
                    Log.d(TAG, "Get Receiver Details :- " + receiver_qb_id);
                    Log.d(TAG, "Get Friend Details :- " + friend_qb_id);
                    Log.d(TAG, "Get dialog Details :- " + dialog_id);
                    Log.d(TAG, "Get read status getReciever :- " + readStatus);
                    Log.d(TAG, "Get qbChatDialogBytes 1 :- " + qbChatDialogBytes);
//                    Log.d(TAG, "Get full QBChatDialog 1 :- " + SerializationUtils.deserialize(qbChatDialogBytes));

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, "", isMINE, sender_qb_id);
                    chatMessage.Time = time;
                    chatMessage.Date = date;
                    chatMessage.type = MSGTYPE;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.ReciverFriendImage = FriendImg;
                    chatMessage.msgStatus = msgStatus;
                    chatMessage.receiptId = receiptId;
                    chatMessage.userStatus = userStatus;
                    chatMessage.receiver_QB_Id = receiver_qb_id;
                    chatMessage.friend_QB_Id = friend_qb_id;
                    chatMessage.dialog_id = dialog_id;
                    chatMessage.readStatus = readStatus;

                    if (qbChatDialogBytes != null) {

                        chatMessage.qbChatDialogBytes = qbChatDialogBytes;
                    }


                    arrayList.add(chatMessage);

                    Log.v(TAG, "get Receiver Details :- " + chatMessage.toString());

                    System.out.println("query : recivername" + Receiver + " Gropname" + GroupName);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("query : recivername" + e.getMessage());
        }

        //  closeConnecion();
        return arrayList;
    }


    public static String getLastSeen(SQLiteDatabase db, String keyname) {

        String LastSeen = "offline";
        try {
            String query;
            query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "'";

            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return LastSeen;
            } else if (cursor.getCount() == 0) {
                return LastSeen;
            }

            if (cursor.moveToLast()) {
                LastSeen = cursor.getString(cursor.getColumnIndex(DBTable.KEY_LASTSEEN));
                System.out.println("dummyDateDB" + LastSeen);
               /* do {


                } while (cursor.moveToNext());*/
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //closeConnecion();
        return LastSeen;
    }

    public static ArrayList<ChatMessage> getMedia(SQLiteDatabase db, String which, String keyname) {

        ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        try {
            String query;
            if (which.equalsIgnoreCase("chat")) {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' AND " + DBTable.KEY_FILENAME + " !=''";
            } else {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_GROUPNAME + " = '" + keyname + "' AND " + DBTable.KEY_FILENAME + " !=''";
            }
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return chatMessageList;
            } else if (cursor.getCount() == 0) {
                return chatMessageList;
            }

            if (cursor.moveToFirst()) {

                do {

                    String Sender = "";
                    String Receiver = "";
                    String SenderName = "";
                    String ReciverName = "";
                    String RecLanguage = "";
                    String SendLanguage = "";
                    String file = "";
                    String DateTime = "";
                    String MID = "";
                    String MSGTYPE = "";
                    String GroupName = "";
                    byte[] fileData = new byte[0];
                    boolean isMINE = false;

                    String filename = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FILE));
                    String messageString = cursor.getString(cursor.getColumnIndex(DBTable.KEY_BODY));

                    String fileName = MimeTypeMap.getFileExtensionFromUrl(filename);

                    String msg = messageString;
                    Log.d("Filename", ">>" + fileName);

                    /*>>>>>>>>>>*/

                    if (!filename.equalsIgnoreCase("") && msg.contains(AppConstants.KEY_CONTACT)) {
                    } else if (filename.equalsIgnoreCase("")) {
                    } else {

                        Sender = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER));
                        Receiver = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIVER));
                        SenderName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDERNAME));
                        ReciverName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECIVERNAME));
                        RecLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECLANGUAGE));
                        SendLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDLANGUAGE));
                        file = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FILE));
                        fileData = cursor.getBlob(cursor.getColumnIndex(DBTable.KEY_FILEDATA));
                        DateTime = cursor.getString(cursor.getColumnIndex(DBTable.KEY_TIME));
                        MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGID));
                        MSGTYPE = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGTYPE));
                        GroupName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPNAME));
                        isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;
                    }
                    /*>>>>>>>>>>*/
                    /*if ((fileName.equalsIgnoreCase("png") || fileName.equalsIgnoreCase("jpg") || fileName.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {

                    } else if (fileName.equalsIgnoreCase("png") || fileName.equalsIgnoreCase("jpg") || fileName.equalsIgnoreCase("jpeg")
                            || fileName.equalsIgnoreCase("mp4") || fileName.equalsIgnoreCase("3gp")) {
                        Sender = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER));
                        Receiver = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIVER));
                        SenderName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDERNAME));
                        ReciverName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECIVERNAME));
                        RecLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECLANGUAGE));
                        SendLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDLANGUAGE));
                        file = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FILE));
                        fileData = cursor.getBlob(cursor.getColumnIndex(DBTable.KEY_FILEDATA));
                        DateTime = cursor.getString(cursor.getColumnIndex(DBTable.KEY_TIME));
                        MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGID));
                        MSGTYPE = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGTYPE));
                        GroupName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPNAME));
                        isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;
                    }*/


                    int receiver_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_RECIVER_ID));
                    int sender_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_SENDER_ID));
                    int friend_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_FRIEND_ID));

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, "", isMINE, sender_qb_id);

                    chatMessage.Time = DateTime;
                    chatMessage.type = MSGTYPE;
                    chatMessage.fileName = filename;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.fileData = fileData;
                    chatMessage.receiver_QB_Id = receiver_qb_id;
                    chatMessage.friend_QB_Id = friend_qb_id;
                    chatMessageList.add(chatMessage);


                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //closeConnecion();
        return chatMessageList;
    }

    public static ArrayList<ChatMessage> getDateWiseChat(SQLiteDatabase db, String which, String keyname, String date) {
//fvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvd

        ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        try {
            String query;
            if (which.equalsIgnoreCase("chat")) {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'";
            } else {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_GROUPNAME + " = '" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'";
            }
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return chatMessageList;
            } else if (cursor.getCount() == 0) {
                return chatMessageList;
            }

            if (cursor.moveToFirst()) {

                do {

                    String Sender = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER));
                    String Receiver = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIVER));
                    String SenderName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDERNAME));
                    String ReciverName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECIVERNAME));
                    String RecLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECLANGUAGE));
                    String SendLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDLANGUAGE));
                    String messageString = cursor.getString(cursor.getColumnIndex(DBTable.KEY_BODY));
                    String file = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FILE));
                    String filename = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FILENAME));
                    byte[] fileData = cursor.getBlob(cursor.getColumnIndex(DBTable.KEY_FILEDATA));
                    String DateTime = cursor.getString(cursor.getColumnIndex(DBTable.KEY_TIME));
                    String MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGID));
                    String MSGTYPE = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGTYPE));
                    String GroupName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPNAME));
                    String Date = cursor.getString(cursor.getColumnIndex(DBTable.KEY_DATE));

                    boolean isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;

                    int receiver_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_RECIVER_ID));
                    int sender_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_SENDER_ID));
                    int friend_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_FRIEND_ID));

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, "", isMINE, sender_qb_id);

                    chatMessage.Time = DateTime;
                    chatMessage.type = MSGTYPE;
                    chatMessage.Date = Date;
                    chatMessage.fileName = filename;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.fileData = fileData;
                    chatMessage.receiver_QB_Id = receiver_qb_id;
                    chatMessage.friend_QB_Id = friend_qb_id;
                    chatMessageList.add(chatMessage);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //closeConnecion();
        return chatMessageList;
    }

    public static String getMsgId(SQLiteDatabase db, String messageid) {

        String Msgid = "";
        try {
            String query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_MSGID + " = '" + messageid + "'";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {

                return Msgid;
            } /*else if (cursor.getCount() == 0) {
                return Msgid;
            }
*/
            if (cursor.moveToFirst()) {

                do {

                    String MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGID));
                    Msgid = (MID);


                    System.out.println("query : " + MID);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        // closeConnecion();
        return Msgid;
    }

    public static String getUSerStatus(SQLiteDatabase db, String reciver) {

        String status = "";
        try {
            String query = "select " + DBTable.KEY_USERSTATUS + " from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + reciver + "'";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {

                return status;
            } /*else if (cursor.getCount() == 0) {
                return Msgid;
            }
*/
            if (cursor.moveToFirst()) {

                do {

                    status = cursor.getString(cursor.getColumnIndex(DBTable.KEY_USERSTATUS));
                    System.out.println("query : " + status);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        // closeConnecion();
        return status;
    }

    public static boolean getIsBlock(SQLiteDatabase db, String reciver) {

        boolean IsBlock = false;
        try {
            String query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_READ + " = '400' AND " + DBTable.KEY_RECEIVER + " ='" + reciver + "'";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {

                return IsBlock;
            } /*else if (cursor.getCount() == 0) {
                return Msgid;
            }
*/
            if (cursor.moveToFirst()) {


                IsBlock = true;

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        // closeConnecion();
        return IsBlock;
    }

    public static String getmsgCount(SQLiteDatabase db, String which, String keyname) {

        String count = "";
        try {
            String query;
            if (which.equalsIgnoreCase("chat")) {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' and " + DBTable.KEY_READ + " ='" + "0" + "'";
                //query = "select "+DBTable.KEY_MSGSTATUS+" from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' ORDER BY " + DBTable.KEY_ID + " ASC ";
            } else {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' and " + DBTable.KEY_READ + " ='" + "0" + "'";
                //query = "select "+DBTable.KEY_MSGSTATUS+" from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_GROUPNAME + " = '" + keyname + "' ORDER BY " + DBTable.KEY_ID + " ASC ";
            }

            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {

                return count;
            } /*else if (cursor.getCount() == 0) {
                return Msgid;
            }
*/
            if (cursor.moveToFirst()) {
                int num = 0;
                do {
                    //correctAnswer=cursor.getInt(3);
                    String MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_READ));
                    if (MID.equalsIgnoreCase("0")) {
                        num = num + 1;
                    }

                    System.out.println("query : count >" + MID);

                } while (cursor.moveToNext());

                count = String.valueOf(num);

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        System.out.println("query : count " + count);
        // closeConnecion();
        return count;
    }

    public static User getUserDetails(SQLiteDatabase db, String mob) {

        User user = new User();
        try {
            String query = "select * from " + DBTable.TBL_USER + " where " + DBTable.KEY_MOBILE + " = '" + mob + "'";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return user;
            } else if (cursor.getCount() == 0) {
                return user;
            }

            if (cursor.moveToFirst()) {

                do {
                    user.setName(cursor.getString(cursor.getColumnIndex(DBTable.KEY_USERNAME)));
                    user.setMobile(cursor.getString(cursor.getColumnIndex(DBTable.KEY_MOBILE)));
                    user.setPassword(cursor.getString(cursor.getColumnIndex(DBTable.KEY_PASSWORD)));

                    System.out.println("query : " + user.getMobile());

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // closeConnecion();
        return user;
    }

    public static String getMobile(SQLiteDatabase db, String mob) {

        String mobNo = "";
        try {
            String query = "select * from " + DBTable.TBL_USER + " where " + DBTable.KEY_MOBILE + " = '" + mob + "'";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return mobNo;
            } else if (cursor.getCount() == 0) {
                return mobNo;
            }

            if (cursor.moveToFirst()) {

                do {

                    String mobno = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MOBILE));
                    mobNo = (mobno);


                    System.out.println("query : " + mobNo);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // closeConnecion();
        return mobNo;
    }


    public static String getFriendMobile(SQLiteDatabase db, String mob) {
//dsccccccccccccccccccccccccccccccccccccccccccccc
        String mobNo = "";
        try {
            String query = "select * from " + DBTable.TBL_CONTACTIMPORT + " where " + DBTable.FRIENDNUMBER + " = '" + mob + "'";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return mobNo;
            } else if (cursor.getCount() == 0) {
                return mobNo;
            }

            if (cursor.moveToFirst()) {

                do {

                    String mobno = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDNUMBER));
                    mobNo = (mobno);


                    System.out.println("query : " + mobNo);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // closeConnecion();
        return mobNo;
    }

/////////////////importcontact//////////////////////////////

    public static ArrayList<AllBeans> getContactList(SQLiteDatabase db) {
//dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        ArrayList<AllBeans> allbeansList = new ArrayList<AllBeans>();
        try {
            String query;
            query = "select * from " + DBTable.TBL_CONTACTIMPORT + " ORDER BY " + DBTable.FRIENDNAME + " ASC ";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return allbeansList;
            } else if (cursor.getCount() == 0) {
                return allbeansList;
            }

            if (cursor.moveToFirst()) {

                do {

                    String Friendid = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDID));
                    String FriendName = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDNAME));
                    String FriendNumber = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDNUMBER));
                    String FriendImage = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDIMAGE));
                    String FriendProfStatus = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDPROFILESTATUS));
                    String FriendfavStatus = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDFAVIOURATESTATUS));
                    String FriendLanguage = cursor.getString(cursor.getColumnIndex(DBTable.FRIENDLANGUAGE));
                    String BlockedStatus = cursor.getString(cursor.getColumnIndex(DBTable.BLOCKED_STATUS));

                    AllBeans allBeans = new AllBeans();
                    allBeans.setFriendid(Friendid);
                    allBeans.setFriendname(FriendName);
                    allBeans.setFriendmobile(FriendNumber);
                    allBeans.setFriendimage(FriendImage);
                    allBeans.setFriendStatus(FriendProfStatus);
                    allBeans.setFavriouteFriend(FriendfavStatus);
                    allBeans.setLanguages(FriendLanguage);
                    allBeans.setBlockedStatus(BlockedStatus);
                    allBeans.setGroupName("");

                    allbeansList.add(allBeans);


                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //closeConnecion();
        return allbeansList;
    }


    /////////////////////////////////


    ///////////////////>>>>>>>>>>>>>>>>///////////////////////////////////


    public static List<ChatMessage> getGroup(SQLiteDatabase db) {

        List<ChatMessage> arrayList = new ArrayList<ChatMessage>();
        try {
            // String query = "select * from " + DBTable.TBL_CHAT + " WHERE 1 AND " +DBTable.KEY_GROUPID +" != 'null'"+" AND " + DBTable.KEY_GROUPNAME + " IS NOT NULL AND " + DBTable.KEY_GROUPNAME + " != '' GROUP BY " + DBTable.KEY_GROUPNAME + " ORDER BY " + DBTable.KEY_ID + " DESC";
            String query = "select * from " + DBTable.TBL_CHAT + " WHERE 1 AND " + DBTable.KEY_GROUPID + " != 'null'" + " AND " + DBTable.KEY_GROUPNAME + " IS NOT NULL AND " + DBTable.KEY_GROUPNAME + " != '' GROUP BY " + DBTable.KEY_GROUPID + " ORDER BY " + DBTable.KEY_ID + " DESC";
            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return arrayList;
            } else if (cursor.getCount() == 0) {
                return arrayList;
            }

            if (cursor.moveToFirst()) {

                do {

                    String Sender = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER));
                    String Receiver = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECEIVER));
                    String SenderName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDERNAME));
                    String ReciverName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECIVERNAME));
                    String RecLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_RECLANGUAGE));
                    String SendLanguage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDLANGUAGE));
                    String messageString = cursor.getString(cursor.getColumnIndex(DBTable.KEY_BODY));
                    String MID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGID));
                    String time = cursor.getString(cursor.getColumnIndex(DBTable.KEY_TIME));
                    String date = cursor.getString(cursor.getColumnIndex(DBTable.KEY_DATE));
                    String MSGTYPE = cursor.getString(cursor.getColumnIndex(DBTable.KEY_MSGTYPE));
                    String GroupName = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPNAME));
                    String GroupID = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPID));
                    String GroupDpImage = cursor.getString(cursor.getColumnIndex(DBTable.KEY_GROUPIMAGE));
                    String FriendImg = cursor.getString(cursor.getColumnIndex(DBTable.KEY_FRIENDIMAGE));
                    String userStatus = cursor.getString(cursor.getColumnIndex(DBTable.KEY_USERSTATUS));
                    boolean isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;

                    System.out.println("imageeee y2k eeeeeeeee" + GroupDpImage);


                    int receiver_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_RECIVER_ID));
                    int sender_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_SENDER_ID));
                    int friend_qb_id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_FRIEND_ID));
                    String dialog_id = cursor.getString(cursor.getColumnIndex(DBTable.KEY_QB_DIALOG_ID));

                    byte[] qbChatDialogBytes = cursor.getBlob(cursor.getColumnIndex(DBTable.KEY_QBCHATDIALOG_BYTES));

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, "", isMINE, sender_qb_id);
                    chatMessage.Time = time;
                    chatMessage.Date = date;
                    chatMessage.type = MSGTYPE;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.groupid = GroupID;
                    chatMessage.ReciverFriendImage = FriendImg;
                    chatMessage.Groupimage = GroupDpImage;
                    chatMessage.userStatus = userStatus;
                    chatMessage.receiver_QB_Id = receiver_qb_id;
                    chatMessage.friend_QB_Id = friend_qb_id;
                    chatMessage.dialog_id = dialog_id;

                    if (qbChatDialogBytes != null) {
                        chatMessage.qbChatDialogBytes = qbChatDialogBytes;
                    }

                    arrayList.add(chatMessage);


                    System.out.println("query : recivername" + Receiver + " Gropname" + GroupName);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //closeConnecion();
        return arrayList;
    }

    public static String getQBLastSeen(SQLiteDatabase db, int friend_id) {

        String LastSeen = "offline";
        try {
            String query;
            query = "select * from " + DBTable.TBL_STATUS + " where " + DBTable.KEY_QB_FRIEND_ID + " = '" + friend_id + "'";

            System.out.println("query : " + query);
            Cursor cursor = db.rawQuery(query, null);

            if (cursor == null) {
                return LastSeen;
            } else if (cursor.getCount() == 0) {
                return LastSeen;
            }

            if (cursor.moveToLast()) {
                LastSeen = cursor.getString(cursor.getColumnIndex(DBTable.KEY_STATUS));
                int id = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_QB_FRIEND_ID));
                System.out.println("dummyDateDB" + LastSeen);
//                Log.v(TAG, "lAST SEEN OF USER " + id + " :-" + LastSeen);

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error while getting data from :-" + e.getMessage());
        }

//closeConnecion();
        return LastSeen;
    }

    public static StringifyArrayList<String> getUpdateMessageStatus(SQLiteDatabase db, String dialogid, String read_status) {

        Log.v(TAG, "DIALOG ID :- 12:40" + dialogid);

        StringifyArrayList<String> messageIds = new StringifyArrayList<>();

        String qbMessage_id = "";
        try {

            String query;
            query = "select " + DBTable.KEY_QB_MESSAGE_ID + " from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_QB_DIALOG_ID + " = '" + dialogid + "' AND " + DBTable.KEY_READ_STATUS + " = '" + read_status + "'";
            Log.v(TAG, "Get message status of messages 1 :- " + query);
            Log.v(TAG, "Get message status of messages 2 :- " + query);

            Cursor cursor = db.rawQuery(query, null);
            Log.v(TAG, "Cursor data getting message id :- " + cursor);

            if (cursor == null) {
                return messageIds;
            } else if (cursor.getCount() == 0) {
                return messageIds;
            }

            if (cursor.moveToLast()) {

                qbMessage_id = cursor.getString(cursor.getColumnIndex(DBTable.KEY_QB_MESSAGE_ID));
//dffffffffffffffffffffffffffffffffffffffffffffffffv
                Log.v(TAG, "qbMessage_id ID :- 12:40 -- " + qbMessage_id);
                System.out.println("DBDataGet 12:40 :-" + qbMessage_id);

            }

            if (cursor.moveToFirst()) {
                Log.v(TAG, "entering dowhile loop qbMessage_id ID :- 22 dec -- " + qbMessage_id);
                do {

                    qbMessage_id = cursor.getString(cursor.getColumnIndex(DBTable.KEY_QB_MESSAGE_ID));

                    Log.v(TAG, " qbMessage_id ID :- 22 dec 8:40 am -- " + qbMessage_id);
                    String key_body = cursor.getString(cursor.getColumnIndex(DBTable.KEY_BODY));
                    Log.v(TAG, " message text -- key_body ID :- 22 dec 8:40 am -- " + key_body);

                    messageIds.add(qbMessage_id);

                } while (cursor.moveToLast());

            }


            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//closeConnecion();
        return messageIds;
    }
}
