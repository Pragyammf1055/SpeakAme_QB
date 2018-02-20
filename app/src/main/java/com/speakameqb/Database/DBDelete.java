package com.speakameqb.Database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.speakameqb.Xmpp.CommonMethods;

/**
 * Created by MMFA-YOGESH on 05-Jan-17.
 */

public class DBDelete {

    private static final String TAG = "DBdelete";

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


    public static void deleteChatDateWise(SQLiteDatabase db, String keyname, String which, String date) {
//fvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        Log.v("DBDelete", "~~~~~~~~~~~~~~ Inside DB Delete class ~~~~~~~~~~~~~~");
/*

        if (which.equalsIgnoreCase("chat")) {
            boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECEIVER + "=" + keyname + " AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'", null) > 0;
            Log.d("deleteChat", "Delete Query :--" + DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'");
            Log.d("deleteChat", ":::" + isDeleted);

        } else {
            boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'", null) > 0;
            Log.d("deleteChat", ":::" + isDeleted);
        }
*/

        if (which.equalsIgnoreCase("chat")) {

            String DEL_DATA = "DELETE from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDateNewFormat() + "'";

//            String DEL_DATA = "DELETE "+ DBTable.KEY_BODY + "from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'";
            db.execSQL(DEL_DATA);

            boolean isDeleted = true;
//            boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'", null) > 0;

            Log.d(TAG, "Delete Query :- " + DEL_DATA);
            Log.d(TAG, "Delete Query :- " + DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDateNewFormat() + "'");
            Log.d(TAG, "data deleted :: :-" + isDeleted);

        } else {
            boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECEIVER + "='" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDateNewFormat() + "'", null) > 0;
            Log.d("deleteChat", ":::" + isDeleted);


            String DEL_DATA = "DELETE from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDateNewFormat() + "'";
            Log.d(TAG, "Delete Query Group :- " + DEL_DATA);
//            String DEL_DATA = "DELETE "+ DBTable.KEY_BODY + "from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' AND " + DBTable.KEY_DATE + " BETWEEN '" + date + "' AND '" + CommonMethods.getCurrentDate() + "'";
            db.execSQL(DEL_DATA);

        }

        Log.v("DB_Delete", "Delete Query ...");
    }

    public static void deleteContactName(SQLiteDatabase db, String name, String key_receiver) {

        Log.v("DBDelete", "~~~~~~~~~~~~~~ Inside DB Delete class ~~~~~~~~~~~~~~");
        boolean isDeleted = db.delete(DBTable.TBL_CHAT, DBTable.KEY_RECIVERNAME + "='" + name + " ' WHERE " + DBTable.KEY_RECEIVER + " = '" + key_receiver + "'", null) > 0;
        Log.d("deleteChat", "Delete Query :--" + DBTable.KEY_RECIVERNAME + "='" + name + " ' WHERE " + DBTable.KEY_RECEIVER + " = '" + key_receiver + "'");
        Log.d("deleteChat", ":::" + isDeleted);
    }

}
