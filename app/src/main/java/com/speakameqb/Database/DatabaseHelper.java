package com.speakameqb.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.quickblox.core.helper.StringifyArrayList;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Beans.User;
import com.speakameqb.Xmpp.ChatMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAX on 21-Sep-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "speakameqb.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "DatabaseHelper";
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

    public static String getUsetImage(int qbId) {
        String userImage = "";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        userImage = DBDataGet.getUserUpdatedUserImage(db, qbId);
        return userImage;
    }

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
        String currentDBPath = "//data//" + "com.speakameqb" + "//databases//" + "speakameqb.db" + "";
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

    public void UpdateUserImage(String userImageUrl, String qbFriendId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DataUpdate.UpdateUserImage(db, userImageUrl, qbFriendId);
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

    public void UpdateFriendPro(String image, String userStatus, String reciver) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DataUpdate.UpdateFriendPro(db, image, userStatus, reciver);
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

    public void insertContact_(AllBeans allBeans) {
        String mob = DBDataGet.getFriendMobile(dbHelper.getReadableDatabase(), allBeans.getFriendmobile());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (mob.equalsIgnoreCase("")) {
            DataInsert.InsertImportcontact(db, allBeans);
        } else {
            DataUpdate.UpdateContact(db, allBeans);
        }
        //  closeConnecion();
    }

    public String getContactResponseFromDataBase(Context context) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String response = DBDataGet.getResponseFromdataBase(db, context);
// closeConnecion();
        return response;
    }

    public void insertContact(String response, Context context) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DataInsert.insertDataContact(db, response, context);
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

    public ArrayList<String> getDateFromGroup(String which, String reciver) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, " getDatafor :  -- " + " in method");
        ArrayList<String> getdateFromDB = DBDataGet.getDateByGroup(db, which, reciver);
        // closeConnecion();
        Log.d(TAG, " getDatafor 11:  -- " + " in method");

        return getdateFromDB;
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
//        fdddddddddddddddddddddd
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

    public String getUSerStatus(String reciver) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String msgID = DBDataGet.getUSerStatus(db, reciver);
        //closeConnecion();
        return msgID;
    }

    public boolean getIsBlock(String reciver) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean msgID = DBDataGet.getIsBlock(db, reciver);
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
        boolean isDeleted = DBDelete.deleteGroup(db, groupName);
        // closeConnecion();
        return isDeleted;
    }

    public boolean deleteChat(String msgId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean isDeleted = DBDelete.deleteChat(db, msgId);
        // closeConnecion();
        return isDeleted;
    }

    public boolean deleteFriendFromList(String key_receiver) {

        Log.v("DB helper", "Inside deleteFriendFromList() helper method !!!!!!!!!!!!!!!!!!!! ");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean isDeleted = DBDelete.deleteChatData(db, key_receiver);
// closeConnecion();
        return isDeleted;
    }

    public void ChatDelete_ByDate(String which, String reciver, String date) {
//fvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        Log.v("DataBase Helper", " ~~~~~~~~~~~~~~ Inside getDateWiseChatDelete() ~~~~~~~~~~~~~~");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DBDelete.deleteChatDateWise(db, reciver, which, date);
// closeConnecion();
    }


    public void UpdateLastSeen(ChatMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DataUpdate.UpdateLastSeen(db, message);
    }

    public String getLastSeenQB(int reciver) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String list = DBDataGet.getQBLastSeen(db, reciver);
        String date = null;

      /*  try {
            date = Function.formatToYesterdayOrToday(list);
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
        Log.v(TAG, "Date 1 :- " + list);
        Log.v(TAG, "Date 2 :- " + date);
// closeConnecion();
        return list;
    }

    public void UpdateReadStatus(String status, String qbDialogId, String qbMessageId, String qbRecipientId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//dcsssssssssssssssssssssssssssss
        DataUpdate.UpdateReadStatus(db, status, qbDialogId, qbMessageId, qbRecipientId);
        db.close();
    }

    public StringifyArrayList<String> getMessageUpdateStatus(String dialogid) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringifyArrayList<String> list = DBDataGet.getUpdateMessageStatus(db, dialogid, "");
// closeConnecion();
        return list;
    }

    public void InsertStatus(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DataInsert.InsertUserStatus(db, user);
// closeConnecion();
    }

    public void UpdateMsgStatus(String status, String receiptId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DataUpdate.UpdateMsgStatus(db, status, receiptId);
    }


    public void UpdateQBChatDialog(String qbDialogId, byte[] ser_QBByteData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//dcsssssssssssssssssssssssssssss
        DataUpdate.UpdateChatDialog(db, qbDialogId, ser_QBByteData);
    }


    public void UpdateReadStatusForAllMessage(String status, String qbDialogId, String receiver) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//dcsssssssssssssssssssssssssssss
        DataUpdate.UpdateReadStatusForAll(db, status, qbDialogId, receiver);
    }

    public void insertQbIdQbChatPrivateDialoge(int qb_id, String dialogeId, byte[] data, String chatType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DataInsert.insertQbIdQbChatPrivateDialoge(db, qb_id, dialogeId, data, chatType);
    }

    public void updateQBChatDialog(int qb_id, String dialogeId, byte[] data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DataUpdate.updateQBChatDialog(db, qb_id, dialogeId, data);
    }

    public byte[] getChatDialogUsingQBId(int qb_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
// closeConnecion();
        return DBDataGet.getChatDialogUsingQBId(db, qb_id);
    }


    public byte[] getChatDialogUsingDialogID(String dialog_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
// closeConnecion();
        return DBDataGet.getChatDialogUsingDialogID(db, dialog_id);
    }

    public boolean ifChatDialogExists(int qb_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
// closeConnecion();
        Log.v(TAG, "check whether qb dialog exists :- " + DBDataGet.ifChatDialogExists(db, qb_id));
        return DBDataGet.ifChatDialogExists(db, qb_id);
    }


    public void UpdateQbFileIdAndqbFileUid(int qbFileId, String qbFileUid, String dialogid, String messageid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DataUpdate.updateqbFileIdAndqbFileUid(db, qbFileId, qbFileUid, dialogid, messageid);
    }

}

