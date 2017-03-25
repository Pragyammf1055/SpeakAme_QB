package com.speakame.Database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.speakame.Xmpp.CommonMethods;

/**
 * Created by MMFA-YOGESH on 05-Jan-17.
 */

public class DBDelete {

    public static void deleteContact(SQLiteDatabase db) {

        long id = db.delete(DBTable.TBL_CONTACTIMPORT, null, null);
        Log.d("DeleteContact", ":::" + id);
        db.close();
    }

    public static boolean deleteGroup(SQLiteDatabase db, String groupName) {

        boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_GROUPNAME + "='" + groupName + "'", null) > 0;
        Log.d("deleteGroup", ":::" + isDeleted);
        db.close();
        return isDeleted;
    }

    public static boolean deleteChat(SQLiteDatabase db, String msgId) {

        boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_MSGID + "='" + msgId + "'", null) > 0;
        Log.d("deleteGroup", ":::" + isDeleted);
        db.close();
        return isDeleted;
    }

    public static void deleteUserInfo(SQLiteDatabase db) {

        long id = db.delete(DBTable.TBL_USER, null, null);
        Log.d("DeleteContact", ":::" + id);
        db.close();
    }


    public static boolean deleteChatData(SQLiteDatabase db, String key_receiver) {

        Log.d("deleteChat", "Key Receiver :- " + key_receiver);
        boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECEIVER + "='" + key_receiver + "'", null) > 0;


        Log.d("deleteChat", ":::" + isDeleted);
        db.close();
        return isDeleted;
    }
    public static void deleteChatDateWise (SQLiteDatabase db, String keyname, String which, String date) {

        Log.v("DBDelete", "~~~~~~~~~~~~~~ Inside DB Delete class ~~~~~~~~~~~~~~");

        if (which.equalsIgnoreCase("chat")) {

            boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'", null) > 0;
            Log.d("deleteChat", "Delete Query :--" + DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'");
            Log.d("deleteChat", ":::" + isDeleted);

        } else {

            boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'", null) > 0;
            Log.d("deleteChat", ":::" + isDeleted);

        }

        Log.v("DB_Delete", "Delete Query ...");
    }


}
