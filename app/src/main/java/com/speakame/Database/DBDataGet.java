package com.speakame.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.MimeTypeMap;

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
                    Log.d("CHATLISTSS count",cursor.getCount()+"\n"+cursor.getString(cursor.getColumnIndex(DBTable.KEY_SENDER)));
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

                    boolean isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, file, isMINE);
                    chatMessage.Time = DateTime;
                    chatMessage.type = MSGTYPE;
                    chatMessage.fileName = filename;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.msgStatus = msgStatus;
                    chatMessage.receiptId = receiptID;
                    chatMessage.fileData = fileData;
                    chatMessageList.add(chatMessage);
                    Log.d("CHATLISTSS count",chatMessage.toString()+"\n>>>>>>>>>>>>>>>>");

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CHATLISTSS Exception",e.getMessage());
        }

        //closeConnecion();
        return chatMessageList;
    }

    public static String getLastSeen(SQLiteDatabase db, String keyname) {

        String LastSeen = "";
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
                    }else if (filename.equalsIgnoreCase("")) {
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


                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, file, isMINE);
                    chatMessage.Time = DateTime;
                    chatMessage.type = MSGTYPE;
                    chatMessage.fileName = filename;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.fileData = fileData;
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

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, file, isMINE);
                    chatMessage.Time = DateTime;
                    chatMessage.type = MSGTYPE;
                    chatMessage.Date = Date;
                    chatMessage.fileName = filename;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.fileData = fileData;
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

    public static String getmsgCount(SQLiteDatabase db, String which, String keyname) {

        String count = "";
        try {
            String query;
            if (which.equalsIgnoreCase("chat")) {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' and "+DBTable.KEY_READ+" ='" + "0" +"'";
                //query = "select "+DBTable.KEY_MSGSTATUS+" from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' ORDER BY " + DBTable.KEY_ID + " ASC ";
            } else {
                query = "select * from " + DBTable.TBL_CHAT + " where " + DBTable.KEY_RECEIVER + " = '" + keyname + "' and "+DBTable.KEY_READ+" ='" + "0" +"'";
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
                    if(MID.equalsIgnoreCase("0")){
                        num = num+1;
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

    public static List<ChatMessage> getReciever(SQLiteDatabase db) {

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

                    boolean isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;


                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, "", isMINE);
                    chatMessage.Time = time;
                    chatMessage.Date = date;
                    chatMessage.type = MSGTYPE;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.ReciverFriendImage = FriendImg;
                    chatMessage.msgStatus = msgStatus;
                    chatMessage.receiptId = receiptId;


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
            System.out.println("query : recivername" + e.getMessage());
        }

        //  closeConnecion();
        return arrayList;
    }

    public static List<ChatMessage> getGroup(SQLiteDatabase db) {

        List<ChatMessage> arrayList = new ArrayList<ChatMessage>();
        try {
           // String query = "select * from " + DBTable.TBL_CHAT + " WHERE 1 AND " +DBTable.KEY_GROUPID +" != 'null'"+" AND " + DBTable.KEY_GROUPNAME + " IS NOT NULL AND " + DBTable.KEY_GROUPNAME + " != '' GROUP BY " + DBTable.KEY_GROUPNAME + " ORDER BY " + DBTable.KEY_ID + " DESC";
            String query = "select * from " + DBTable.TBL_CHAT + " WHERE 1 AND " +DBTable.KEY_GROUPID +" != 'null'"+" AND " + DBTable.KEY_GROUPNAME + " IS NOT NULL AND " + DBTable.KEY_GROUPNAME + " != '' GROUP BY " + DBTable.KEY_GROUPID + " ORDER BY " + DBTable.KEY_ID + " DESC";
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
                    boolean isMINE = cursor.getInt(cursor.getColumnIndex(DBTable.KEY_ISMINE)) > 0;

                    System.out.println("imageeeey2keeeeeeeee" + GroupDpImage);

                    ChatMessage chatMessage = new ChatMessage(Sender, SenderName, Receiver, ReciverName, GroupName, messageString, MID, "", isMINE);
                    chatMessage.Time = time;
                    chatMessage.Date = date;
                    chatMessage.type = MSGTYPE;
                    chatMessage.senderlanguages = SendLanguage;
                    chatMessage.reciverlanguages = RecLanguage;
                    chatMessage.groupid = GroupID;
                    chatMessage.ReciverFriendImage = FriendImg;
                    chatMessage.Groupimage = GroupDpImage;


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

}
