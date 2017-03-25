package com.speakame.Database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.speakame.Beans.AllBeans;
import com.speakame.Beans.User;
import com.speakame.Xmpp.ChatMessage;

/**
 * Created by MAX on 21-Sep-16.
 */
public class DataInsert {

    public static void InsertChat(SQLiteDatabase db, ChatMessage message) {
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







}
