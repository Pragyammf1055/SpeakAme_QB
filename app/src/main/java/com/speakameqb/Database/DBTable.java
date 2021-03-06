package com.speakameqb.Database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.speakameqb.utils.AppPreferences;

/**
 * Created by MAX on 21-Sep-16.
 */
public class DBTable {

    public static final String TBL_CHAT = "tbl_chat";
    public static final String TBL_GROUPCHAT = "tbl_gpchat";
    public static final String TBL_USER = "tbl_user";
    public static final String TBL_CONTACTIMPORT = "tbl_contact";
    public static final String KEY_ID = "id";
    public static final String KEY_BODY = "key_body";
    public static final String KEY_SENDER = "key_sender";
    public static final String KEY_RECEIVER = "key_receiver";
    public static final String KEY_SENDERNAME = "key_sendername";
    public static final String KEY_RECIVERNAME = "key_recivername";
    public static final String KEY_RECLANGUAGE = "key_reclanguage";
    public static final String KEY_FRIENDIMAGE = "key_frndimage";
    public static final String KEY_SENDLANGUAGE = "key_sendlanguage";
    public static final String KEY_GROUPNAME = "key_groupname";
    public static final String KEY_GROUPID = "key_groupid";
    public static final String KEY_GROUPIMAGE = "key_groupimage";
    public static final String KEY_MSGSTATUS = "key_msgstatus";
    public static final String KEY_RECEIPTID = "key_receiptid";
    public static final String KEY_DATE = "key_date";
    public static final String KEY_TIME = "key_time";
    public static final String KEY_MSGID = "key_msgid";
    public static final String KEY_READ = "key_read";
    public static final String KEY_FILE = "key_file";
    public static final String KEY_FILENAME = "key_filename";
    public static final String KEY_FILEDATA = "key_filedata";
    public static final String KEY_SENDIMAGE = "key_img";
    public static final String KEY_ISMINE = "key_ismine";
    public static final String KEY_MSGTYPE = "key_msgtype";
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_MOBILE = "key_mobile";
    public static final String KEY_PASSWORD = "key_password";
    public static final String KEY_LASTSEEN = "key_lastseen";
    public static final String FRIENDID = "frnd_id";
    public static final String FRIENDNAME = "frnd_name";
    public static final String FRIENDNUMBER = "frnd_number";
    public static final String FRIENDIMAGE = "frnd_image";
    public static final String FRIENDPROFILESTATUS = "frnd_pro_stats";
    public static final String FRIENDFAVIOURATESTATUS = "frnd_fav_status";
    public static final String FRIENDLANGUAGE = "frnd_language";
    public static final String BLOCKED_STATUS = "block_status";
    public static final String KEY_USERSTATUS = "user_status";
    public static final String KEY_ISOTHERMSG = "isOtherMsg";
    public static final String KEY_QB_RECIVER_ID = "receiver_QB_Id";
    public static final String KEY_QB_SENDER_ID = "sender_QB_Id";
    public static final String KEY_QB_FRIEND_ID = "friend_qb_id";
    public static final String KEY_QB_FILE_UPLOAD_ID = "qbFileUploadId";
    public static final String KEY_QB_FILE_U_ID = "qbFileUid";
    public static final String KEY_QB_DIALOG_ID = "qbdialogid";
    public static final String KEY_QB_MESSAGE_ID = "qbmessageid";
    public static final String KEY_QBCHATDIALOG = "qbchatdialog";

    public static final String KEY_STATUS = "status";
    public static final String KEY_READ_STATUS = "read_unread_status";
    public static final String KEY_QBCHATDIALOG_BYTES = "qbchatdialog_bytes";
    public static final String TBL_STATUS = "tbl_status";
    public static final String KEY_RESPONSE = "response";
    public static final String TBL_QB_CHAT_DIALOG = "tbl_qb_chat_dialog";
    public static final String KEY_QB_CHAT_TYPE = "key_qb_chat_type";


    public static final String CREATE_TBL_USER_LAST_SEEN = "create table "
            + TBL_STATUS + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_QB_FRIEND_ID + " integer null, "
            + KEY_STATUS + " text null "
            + ");";

    public static final String CREATE_TBL_QBCHATDIALOG = "create table "
            + TBL_STATUS + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_QB_FRIEND_ID + " integer null, "
            + KEY_QBCHATDIALOG + " text null "
            + ");";

    private static final String CREATE_TBL_CHAT = "create table "
            + TBL_CHAT + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_BODY + " text null, "
            + KEY_SENDER + " text null, "
            + KEY_RECEIVER + " text null, "
            + KEY_SENDLANGUAGE + " text null, "
            + KEY_RECLANGUAGE + " text null, "
            + KEY_SENDERNAME + " text null, "
            + KEY_RECIVERNAME + " text null, "
            + KEY_GROUPNAME + " text null, "
            + KEY_GROUPID + " text null, "
            + KEY_GROUPIMAGE + " text null, "
            + KEY_DATE + " date null, "
            + KEY_TIME + " text null, "
            + KEY_MSGID + " text null, "
            + KEY_READ + " integer null, "
            + KEY_FILE + " text null, "
            + KEY_FILENAME + " text null, "
            + KEY_FILEDATA + " blob null, "
            + KEY_SENDIMAGE + " text null, "
            + KEY_ISMINE + " integer null, "
            + KEY_LASTSEEN + " text null, "
            + KEY_MSGTYPE + " text null, "
            + KEY_MSGSTATUS + " text null, "
            + KEY_ISOTHERMSG + " integer null, "
            + KEY_RECEIPTID + " text null, "
            + KEY_USERSTATUS + " text null, "
            + KEY_QB_MESSAGE_ID + " text null, "
            + KEY_QB_DIALOG_ID + " text null, "
            + KEY_QB_RECIVER_ID + " integer null, "
            + KEY_QB_SENDER_ID + " integer null, "
            + KEY_QB_FRIEND_ID + " integer null, "
            + KEY_QB_FILE_UPLOAD_ID + " integer null, "
            + KEY_QB_FILE_U_ID + " text null, "
            + KEY_READ_STATUS + " text null, "
            + KEY_QBCHATDIALOG_BYTES + " blob null, "
            + KEY_FRIENDIMAGE + " text null"
            + ");";
    private static final String CREATE_TBL_USER = "create table "
            + TBL_USER + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_QB_RECIVER_ID + " integer null, "
            + KEY_USERNAME + " text null, "
            + KEY_MOBILE + " text null, "
            + KEY_PASSWORD + " text null"
            + ");";

    private static final String CREATE_TBL_CONTACTIMPORT_ = "create table "
            + TBL_CONTACTIMPORT + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + FRIENDID + " text null, "
            + KEY_QB_RECIVER_ID + " integer null, "
            + KEY_QB_SENDER_ID + " integer null, "
            + KEY_QB_FRIEND_ID + " integer null, "
            + FRIENDNAME + " text null, "
            + FRIENDNUMBER + " text null, "
            + FRIENDIMAGE + " text null, "
            + FRIENDPROFILESTATUS + " text null, "
            + FRIENDFAVIOURATESTATUS + " text null, "
            + BLOCKED_STATUS + " text null, "
            + FRIENDLANGUAGE + " text null"
            + ");";

    private static final String CREATE_TBL_CONTACTIMPORT = "create table "
            + TBL_CONTACTIMPORT + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + AppPreferences.LOGINID + " text, "
            + KEY_RESPONSE + " text"
            + ");";

    private static final String CREATE_TBL_QB_DIALOG = "create table "
            + TBL_QB_CHAT_DIALOG + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_QB_FRIEND_ID + " integer null, "
            + KEY_QB_DIALOG_ID + " text not null, "
            + KEY_QB_CHAT_TYPE + " text null, "
            + KEY_QBCHATDIALOG_BYTES + " blob null"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TBL_CHAT);
        database.execSQL(CREATE_TBL_USER);
        database.execSQL(CREATE_TBL_CONTACTIMPORT);
        database.execSQL(CREATE_TBL_USER_LAST_SEEN);
        database.execSQL(CREATE_TBL_QB_DIALOG);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(DBTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TBL_CHAT);
        database.execSQL("DROP TABLE IF EXISTS " + TBL_USER);
        database.execSQL("DROP TABLE IF EXISTS " + TBL_CONTACTIMPORT);
        database.execSQL("DROP TABLE IF EXISTS " + TBL_STATUS);

        database.execSQL("DROP TABLE IF EXISTS " + TBL_QB_CHAT_DIALOG);

        onCreate(database);
    }

}
