package com.speakame.Database;

/**
 * Created by Peter on 16-Dec-17.
 * <p>
 * Created by MMFA-YOGESH on 16-Dec-17.
 * <p>
 * Created by MMFA-YOGESH on 16-Dec-17.
 * <p>
 * Created by MMFA-YOGESH on 16-Dec-17.
 */

/**
 * Created by MMFA-YOGESH on 16-Dec-17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockUserDataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BlockUserName.db";
    public static final String BLOCK_TABLE_NAME = "userblock";
    public static final String BLOCK_COLUMN_ID = "id";
    public static final String BLOCK_COLUMN_NAME = "name";
    public static final String BLOCK_COLUMN_PHONE = "phone";
    public static final String BLOCK_COLUMN_OCUPANT_ID = "ocupantid";


    public static final String CREATE_TBL_BLOCK = "create table "
            + BLOCK_TABLE_NAME + " ("
            + BLOCK_COLUMN_ID + " integer primary key autoincrement, "
            + BLOCK_COLUMN_NAME + " text null, "
            + BLOCK_COLUMN_PHONE + " text null, "
            + BLOCK_COLUMN_OCUPANT_ID + " text null"
            + ");";
    /*  public static final String CONTACTS_COLUMN_CITY = "place";
      public static final String CONTACTS_COLUMN_PHONE = "phone";*/
    private HashMap hp;

    public BlockUserDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_TBL_BLOCK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME);
        onCreate(db);
    }

    public boolean saveBlockedUsers(String name, String phone, int ocupantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BLOCK_COLUMN_NAME, name);
        contentValues.put(BLOCK_COLUMN_PHONE, phone);
        contentValues.put(BLOCK_COLUMN_OCUPANT_ID, ocupantId);

        db.insert(BLOCK_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getBlockUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where id=" + id + "", null);
        return res;
    }


    public boolean updateContact(Integer id, String name, String phone, String ocupaintId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BLOCK_COLUMN_NAME, name);
        contentValues.put(BLOCK_COLUMN_PHONE, phone);
        contentValues.put(BLOCK_COLUMN_OCUPANT_ID, ocupaintId);

        db.update(BLOCK_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteBlockedUserById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(" deleteBlockedUserById", " kdjkljflj fdjk ");
        return db.delete(BLOCK_TABLE_NAME,
                BLOCK_COLUMN_OCUPANT_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<Integer> getAllBlockedUsers() {
        ArrayList<Integer> array_list = new ArrayList<Integer>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + BLOCK_TABLE_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getInt(res.getColumnIndex(BLOCK_COLUMN_OCUPANT_ID)));
            // array_list.add(res.getString(res.getColumnIndex(BLOCK_COLUMN_PHONE)));
            // array_list.add(res.getString(res.getColumnIndex(BLOCK_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
