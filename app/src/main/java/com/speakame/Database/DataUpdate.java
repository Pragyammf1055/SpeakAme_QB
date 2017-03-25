package com.speakame.Database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.speakame.Beans.AllBeans;
import com.speakame.Beans.User;
import com.speakame.Xmpp.ChatMessage;

/**
 * Created by MAX on 10-Oct-16.
 */
public class DataUpdate {

    public static void UpdateUser(SQLiteDatabase db,User user) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_USERNAME, user.getName());
        initialValues.put(DBTable.KEY_MOBILE, user.getMobile());
        initialValues.put(DBTable.KEY_PASSWORD, user.getPassword());

        long id = db.update(DBTable.TBL_USER, initialValues, DBTable.KEY_MOBILE + " = ?",
                new String[]{user.getMobile()});
        Log.d("UpdateUser", ":::"+id);
        db.close();
    }


    public static void UpdateContact(SQLiteDatabase db,AllBeans allBeans) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.FRIENDID, allBeans.getFriendid());
        initialValues.put(DBTable.FRIENDNAME, allBeans.getFriendname());
        initialValues.put(DBTable.FRIENDNUMBER, allBeans.getFriendmobile());
        initialValues.put(DBTable.FRIENDIMAGE, allBeans.getFriendimage());
        initialValues.put(DBTable.FRIENDPROFILESTATUS, allBeans.getFriendStatus());
        initialValues.put(DBTable.FRIENDFAVIOURATESTATUS, allBeans.getFriendStatus());
        initialValues.put(DBTable.FRIENDLANGUAGE, allBeans.getLanguages());
        initialValues.put(DBTable.BLOCKED_STATUS, allBeans.getBlockedStatus());

        long id = db.update(DBTable.TBL_CONTACTIMPORT, initialValues, DBTable.FRIENDNUMBER + " = ?",
                new String[]
                        {allBeans.getFriendmobile()});
        Log.d("UpdateContact", ":::"+id);
        db.close();
    }


    public static void UpdateLastSeen(SQLiteDatabase db,ChatMessage message) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_LASTSEEN, message.lastseen);


        long id = db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_RECEIVER + " = ?",
                new String[]
                        {message.receiver});
        Log.d("dummyDat update", ":::"+id);
        //db.close();
    }
    public static void UpdateFileName(SQLiteDatabase db,String filename, String msgId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_FILE, filename);

            db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_MSGID + " = ?",
                    new String[]
                            {msgId});

        db.close();
    }
    public static void UpdateMsgStatus(SQLiteDatabase db,String status, String receiptId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_MSGSTATUS, status);

        if(status.equalsIgnoreCase("5") || status.equalsIgnoreCase("11") || status.equalsIgnoreCase("12")){  //  5 for upload fail
            db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_MSGID + " = ?",
                    new String[]
                            {receiptId});
        }else{
            db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_RECEIPTID + " = ?",
                    new String[]
                            {receiptId});
        }
       /* long id = db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_RECEIPTID + " = ?",
                new String[]
                        {receiptId});*/
        //Log.d("UpdateLastSeen", ":::"+id);
        db.close();
    }
    public static void UpdateMsgRead(SQLiteDatabase db,String status, String reciver) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_READ, status);

        db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_RECEIVER + " = ?",
                new String[]
                        {reciver});
       /* long id = db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_RECEIPTID + " = ?",
                new String[]
                        {receiptId});*/
        //Log.d("UpdateLastSeen", ":::"+id);
        db.close();
    }
    public static void UpdateGroupImage(SQLiteDatabase db,String image, String groupId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_GROUPIMAGE, image);

        db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_GROUPID + " = ?",
                new String[]
                        {groupId});
        db.close();
    }
    public static void UpdateGroupName(SQLiteDatabase db,String name, String groupId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_GROUPNAME, name);

        db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_GROUPID + " = ?",
                new String[]
                        {groupId});
        db.close();
    }
    public static void UpdateContactName(SQLiteDatabase db,String recivername, String reciver) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_RECIVERNAME, recivername);

        db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_RECEIVER + " = ?",
                new String[]
                        {reciver});
       /* long id = db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_RECEIPTID + " = ?",
                new String[]
                        {receiptId});*/
        //Log.d("UpdateLastSeen", ":::"+id);
        db.close();
    }
    public static void UpdateReceiptID(SQLiteDatabase db,String msgId, String receiptId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBTable.KEY_RECEIPTID, receiptId);

        long id = db.update(DBTable.TBL_CHAT, initialValues, DBTable.KEY_MSGID + " = ?",
                new String[]
                        {msgId});
        Log.d("UpdateLastSeen", ":::"+id);
        db.close();
    }


}
