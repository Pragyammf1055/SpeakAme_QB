package com.speakame.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.speakame.Beans.AllBeans;
import com.speakame.Beans.User;
import com.speakame.Xmpp.ChatMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAX on 21-Sep-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "speakame.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper dbHelper;
    //private static SQLiteDatabase db = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbHelper = this;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
            //openConnecion();
        }

        return dbHelper;
    }



   /* public static void openConnecion() {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
    }*/


    @Override
    public void onCreate(SQLiteDatabase database) {
        DBTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        DBTable.onUpgrade(database, oldVersion, newVersion);
    }

    public void deleteDB() {
        File data = Environment.getDataDirectory();
        String currentDBPath = "//data//" + "com.speakame" + "//databases//" + "speakame.db" + "";
        File currentDB = new File(data, currentDBPath);

        SQLiteDatabase.deleteDatabase(currentDB);

    }
    public void insertChat(ChatMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DataInsert.InsertChat(db, message);
        //closeConnecion();

    }

    public void insertUser(User user) {
        String mob = DBDataGet.getMobile(dbHelper.getReadableDatabase(), user.getMobile());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (mob.equalsIgnoreCase("")) {
            DataInsert.InsertUser(db, user);
        } else {
            DataUpdate.UpdateUser(db, user);
        }
        //  closeConnecion();

    }
    public void UpdateLastSeen(ChatMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateLastSeen(db, message);
    }
    public void UpdateMsgStatus(String status, String receiptId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateMsgStatus(db, status, receiptId);
    }
    public void UpdateMsgRead(String status, String reciver) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateMsgRead(db, status, reciver);
    }
    public void UpdateContactName(String recivername, String reciver) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateContactName(db, recivername, reciver);
    }
    public void UpdateGroupImage(String image, String groupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateGroupImage(db, image, groupId);
    }
    public void UpdateFriendPro(String image, String reciver) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateFriendPro(db, image, reciver);
    }
    public void UpdateGroupName(String name, String groupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateGroupName(db, name, groupId);
    }
    public void UpdateFileName(String filename, String msgId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateFileName(db, filename, msgId);
    }
    public void UpdateReceiptID(String msgId, String receiptId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            DataUpdate.UpdateReceiptID(db, msgId, receiptId);
    }

    public void insertContact(AllBeans allBeans) {
        String mob = DBDataGet.getFriendMobile(dbHelper.getReadableDatabase(), allBeans.getFriendmobile());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (mob.equalsIgnoreCase("")) {
            DataInsert.InsertImportcontact(db, allBeans);
        } else {
            DataUpdate.UpdateContact(db, allBeans);
        }
        //  closeConnecion();

    }


    public List<ChatMessage> getReciever() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // closeConnecion();
        return DBDataGet.getReciever(db);
    }

    public List<ChatMessage> getGroup() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<ChatMessage> list = DBDataGet.getGroup(db);
        //closeConnecion();

        return list;
    }

    public ArrayList<ChatMessage> getChat(String which, String reciver) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<ChatMessage> list = DBDataGet.getChat(db, which, reciver);
        // closeConnecion();
        return list;
    }
    public String getLastSeen(String reciver) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String list = DBDataGet.getLastSeen(db, reciver);
        // closeConnecion();
        return list;
    }
    public ArrayList<AllBeans> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<AllBeans> list = DBDataGet.getContactList(db);
        // closeConnecion();
        return list;
    }

    public ArrayList<ChatMessage> getMedia(String which, String reciver) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<ChatMessage> list = DBDataGet.getMedia(db, which, reciver);
        // closeConnecion();
        return list;
    }

    public ArrayList<ChatMessage> getDateWiseChat(String which, String reciver, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<ChatMessage> list = DBDataGet.getDateWiseChat(db, which, reciver, date);
        // closeConnecion();
        return list;
    }

    public String getMsgId(String messageId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String msgID = DBDataGet.getMsgId(db, messageId);
        //closeConnecion();
        return msgID;
    }
    public String getmsgCount(String which, String reciver) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String msgID = DBDataGet.getmsgCount(db, which, reciver);
        //closeConnecion();
        return msgID;
    }

    public String getMobileNO(String mob) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String mobiNo = DBDataGet.getMobile(db, mob);
        // closeConnecion();
        return mobiNo;
    }

    public User getUser(String mob) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = DBDataGet.getUserDetails(db, mob);
        // closeConnecion();
        return user;
    }



    public boolean deleteGroup(String groupName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean isDeleted = DBDelete.deleteGroup(db,groupName);
        // closeConnecion();
        return isDeleted;
    }
    public boolean deleteChat(String msgId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean isDeleted = DBDelete.deleteChat(db,msgId);
        // closeConnecion();
        return isDeleted;
    }

    public boolean deleteFriendFromList ( String key_receiver){

        Log.v("DB helper","Inside deleteFriendFromList() helper method !!!!!!!!!!!!!!!!!!!! " );

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean isDeleted = DBDelete.deleteChatData(db, key_receiver );
// closeConnecion();
        return isDeleted;
    }
    public void ChatDelete_ByDate(String which, String reciver, String date) {

        Log.v("DataBase Helper", " ~~~~~~~~~~~~~~ Inside getDateWiseChatDelete() ~~~~~~~~~~~~~~");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DBDelete.deleteChatDateWise(db, reciver, which, date);
// closeConnecion();
    }
}

