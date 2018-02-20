package com.speakameqb.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Beans.User;
import com.speakameqb.Xmpp.ChatMessage;
import com.speakameqb.utils.AppPreferences;

/**
 * Created by MAX on 21-Sep-16.
 */
public class DataInsert {

    private static final String TAG = "DataInsert";

    public static void InsertChat(SQLiteDatabase db, ChatMessage message) {

        Log.v(TAG, "Data insertion while receiving messages :- " + message.sender);
        Log.v(TAG, "4 :- " + message.senderName);
        Log.v(TAG, "5 :- " + message.receiver);
        Log.v(TAG, "6 :- " + message.reciverName);
        Log.v(TAG, "7 :- " + message.Date);

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_BODY, message.body);
        initialValues.put(DBTable.KEY_SENDER, message.sender);
        initialValues.put(DBTable.KEY_RECEIVER, message.receiver);
        initialValues.put(DBTable.KEY_SENDERNAME, message.senderName);
        initialValues.put(DBTable.KEY_RECIVERNAME, message.reciverName);
        initialValues.put(DBTable.KEY_RECLANGUAGE, message.reciverlanguages);
        initialValues.put(DBTable.KEY_SENDLANGUAGE, message.senderlanguages);
        initialValues.put(DBTable.KEY_GROUPNAME, message.groupName);
        initialValues.put(DBTable.KEY_GROUPID, message.groupid);
        initialValues.put(DBTable.KEY_DATE, message.Date);
        initialValues.put(DBTable.KEY_TIME, message.Time);
        initialValues.put(DBTable.KEY_MSGID, message.msgid);
        initialValues.put(DBTable.KEY_FILE, message.files);
        initialValues.put(DBTable.KEY_FILENAME, message.fileName);
        initialValues.put(DBTable.KEY_FILEDATA, message.fileData);
        initialValues.put(DBTable.KEY_ISMINE, message.isMine);
        initialValues.put(DBTable.KEY_MSGTYPE, message.type);
        initialValues.put(DBTable.KEY_LASTSEEN, message.lastseen);
        initialValues.put(DBTable.KEY_FRIENDIMAGE, message.ReciverFriendImage);
        initialValues.put(DBTable.KEY_GROUPIMAGE, message.Groupimage);
        initialValues.put(DBTable.KEY_MSGSTATUS, message.msgStatus);
        initialValues.put(DBTable.KEY_RECEIPTID, message.receiptId);
        initialValues.put(DBTable.KEY_READ, message.isRead);
        initialValues.put(DBTable.KEY_USERSTATUS, message.userStatus);
        initialValues.put(DBTable.KEY_ISOTHERMSG, message.isOtherMsg);
        initialValues.put(DBTable.KEY_QB_RECIVER_ID, message.receiver_QB_Id);
        initialValues.put(DBTable.KEY_QB_SENDER_ID, message.sender_QB_Id);
        initialValues.put(DBTable.KEY_QB_FRIEND_ID, message.friend_QB_Id);
        initialValues.put(DBTable.KEY_QB_FILE_UPLOAD_ID, message.qbFileUploadId);
        initialValues.put(DBTable.KEY_QB_FILE_U_ID, message.qbFileUid);
        initialValues.put(DBTable.KEY_QB_DIALOG_ID, message.dialog_id);
        initialValues.put(DBTable.KEY_QB_MESSAGE_ID, message.qbMessageId);
        initialValues.put(DBTable.KEY_READ_STATUS, message.readStatus);

        Log.v(TAG, "QB Serialize inserting to Db :- " + message.qbChatDialogBytes);
        initialValues.put(DBTable.KEY_QBCHATDIALOG_BYTES, message.qbChatDialogBytes);

        //  long id = db.insert(DBTable.TBL_CHAT, null, initialValues);

        try {
            Log.d("insert", initialValues.toString());
            long id = db.insert(DBTable.TBL_CHAT, null, initialValues);
            Log.d("insertid", id + "");
            db.close(); // Closing database connection
        } catch (Throwable t) {
            Log.i("Database", "Exception caught: " + t.getMessage(), t);
        }

    }

    public static void InsertUser(SQLiteDatabase db, User user) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_USERNAME, user.getName());
        initialValues.put(DBTable.KEY_MOBILE, user.getMobile());
        initialValues.put(DBTable.KEY_PASSWORD, user.getPassword());

        //  long id = db.insert(DBTable.TBL_CHAT, null, initialValues);
        try {

            Log.d("insert", initialValues.toString());
            long id = db.insert(DBTable.TBL_USER, null, initialValues);
            Log.d("insertid", id + "");
            db.close(); // Closing database connection
        } catch (Throwable t) {
            Log.i("Database", "Exception caught: " + t.getMessage(), t);
        }
    }

    public static void InsertImportcontact(SQLiteDatabase db, AllBeans allBeans) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.FRIENDID, allBeans.getFriendid());
        initialValues.put(DBTable.FRIENDNAME, allBeans.getFriendname());
        initialValues.put(DBTable.FRIENDNUMBER, allBeans.getFriendmobile());
        initialValues.put(DBTable.FRIENDIMAGE, allBeans.getFriendimage());
        initialValues.put(DBTable.FRIENDPROFILESTATUS, allBeans.getFriendStatus());
        initialValues.put(DBTable.FRIENDFAVIOURATESTATUS, allBeans.getFriendStatus());
        initialValues.put(DBTable.FRIENDLANGUAGE, allBeans.getLanguages());
        initialValues.put(DBTable.BLOCKED_STATUS, allBeans.getBlockedStatus());
        //  long id = db.insert(DBTable.TBL_CHAT, null, initialValues);
        try {

            Log.d("insert", initialValues.toString());
            long id = db.insert(DBTable.TBL_CONTACTIMPORT, null, initialValues);
            Log.d("insertid", id + "");
            db.close(); // Closing database connection
        } catch (Throwable t) {
            Log.i("Database", "Exception caught: " + t.getMessage(), t);
        }
    }

    public static void InsertUserStatus(SQLiteDatabase db, User user) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_QB_FRIEND_ID, user.getFriend_id());
        initialValues.put(DBTable.KEY_STATUS, user.getStatus());
        Log.v(TAG, "user.getFriend_id() :- " + user.getFriend_id());
        Log.v(TAG, "user.getStatus() :- " + user.getStatus());
        //  long id = db.insert(DBTable.TBL_CHAT, null, initialValues);
        try {

            Log.d(TAG, "insertTodayNew" + initialValues.toString());

            if (initialValues.toString().equals("-1")) {
                Log.v(TAG, "insert...............");
            } else {
                long id = db.insert(DBTable.TBL_STATUS, null, initialValues);
                Log.d("insertidToday", id + "");
                db.close(); // Closing database connection
            }
        } catch (Throwable t) {
            Log.i("Database", "Exception caught: " + t.getMessage(), t);
        }
    }

    public static void insertDataContact(SQLiteDatabase db, String response, Context context) {

        try {
            ContentValues values = new ContentValues();
            values.put(AppPreferences.LOGINID, AppPreferences.getLoginId(context)); // Contact Phone Number
            values.put(DBTable.KEY_RESPONSE, response); // Contact Phone Number
            db.insert(DBTable.TBL_CONTACTIMPORT, null, values);
            db.close(); // Closing database connection
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void insertQbIdQbChatPrivateDialoge(SQLiteDatabase db, int qb_id, String dialogId, byte[] data, String chatType) {

        Log.v(TAG, "insert qb_id :- " + qb_id);
        Log.v(TAG, "insert dialogId :- " + dialogId);
        Log.v(TAG, "insert data. :- " + data.toString());

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_QB_FRIEND_ID, qb_id);
        initialValues.put(DBTable.KEY_QBCHATDIALOG_BYTES, data);
        initialValues.put(DBTable.KEY_QB_DIALOG_ID, dialogId);
        initialValues.put(DBTable.KEY_QB_CHAT_TYPE, chatType);

        try {
            Log.v(TAG, "insert data into database : - " + initialValues.toString());
            long id = db.insert(DBTable.TBL_QB_CHAT_DIALOG, null, initialValues);
            Log.v("insertid", id + "");
            db.close(); // Closing database connection
        } catch (Throwable t) {
            Log.e(TAG, "Exception caught: " + t.getMessage(), t);
        }
    }


}
