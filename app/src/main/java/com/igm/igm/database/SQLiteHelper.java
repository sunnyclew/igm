package com.igm.igm.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.igm.igm.model.DeviceInfo;

import java.util.ArrayList;
/**
 * 안드로이드에 내장된 SQLite DB 의 사용을 위한 클래스
 * Device 의 저장 정보를 기록하기 위함이므로 table 과 column 을 간단하게 함
 * */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper sqLiteHelper = null;
    private static final String DATABASE_NAME = "IGM.db";
    private static final String TABLE_NAME = "DEVICES";
    //2018. 05. 14. DB_VERSION = 1
    private static final int DB_VERSION = 1;
    private static final String IDX = "IDX";
    private static final String COL_0 = "D_NAME";
    private static final String COL_1 = "D_PLACE";
    private static final String COL_2 = "D_USER";

    private SQLiteDatabase database = this.getWritableDatabase();

    //singleton pattern, 메모리 관리를 용이하게 하기 위해
    public static SQLiteHelper getInstance(Context context) {
        if (sqLiteHelper == null) {
            sqLiteHelper = new SQLiteHelper(context);
        }
        return sqLiteHelper;
    }

    //private constructor
    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    // 앱 설치 후, 최초 1회만 실행된다
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_0 + " TEXT, "
                + COL_1 + " TEXT, "
                + COL_2 + " TEXT "
                + ")"
        );
    }

    // DB_VERSION 이 높아지면 실행됨
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //select all
    public ArrayList<DeviceInfo> selectAll() {
        ArrayList<DeviceInfo> list = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor result = database.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + IDX + " ASC", null);

        if (result.moveToFirst()) {
            do {
                list.add(new DeviceInfo(result.getInt(0),
                        result.getString(1),
                        result.getString(2),
                        result.getString(3)));
            } while (result.moveToNext());
        }
        return list;
    }
    //특정 row select
    @SuppressWarnings("unused")
    public DeviceInfo selectData(int idx) {
        DeviceInfo deviceInfo = null;
        @SuppressLint("Recycle")
        Cursor result = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + IDX + " = " + idx, null);
        if (result.moveToFirst()) {
            deviceInfo = new DeviceInfo(result.getInt(0),
                    result.getString(1),
                    result.getString(2),
                    result.getString(3));
        }
        return deviceInfo;
    }
    @SuppressWarnings("unused")
    public DeviceInfo selectData(String name) {
        DeviceInfo deviceInfo = null;
        @SuppressLint("Recycle")
        Cursor result = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_0+ " =? ", new String[]{name});
        if (result.moveToFirst()) {
            deviceInfo = new DeviceInfo(result.getInt(0),
                    result.getString(1),
                    result.getString(2),
                    result.getString(3));
        }
        return deviceInfo;
    }
    // 0 = 중복아님 = false
    // 1 이상 = 중복 = true
    public boolean isDuplicate(String name, String place) {
        @SuppressLint("Recycle")
        Cursor result = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_0 + " =? " + " OR " + COL_1 + " =? ", new String[]{name, place});
        return result.getCount() != 0;
    }
    public boolean isDuplicate(String place) {
        @SuppressLint("Recycle")
        Cursor result = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_1 + " =? ", new String[]{place});
        return result.getCount() != 0;
    }
    //insert 1
    @SuppressWarnings("unused")
    public boolean insertData(DeviceInfo info) {
        return database.insert(TABLE_NAME, null, setData(info.getDName(), info.getDPlace(), info.getDUser())) != -1;
    }

    public boolean insertData(String name, String place, String user) {
        return database.insert(TABLE_NAME, null, setData(name, place, user)) != -1;
    }

    //update
    public boolean updateData(String name, String place, String user) {
        return database.update(TABLE_NAME, setData(null, place, user), COL_0+"=?", new String[]{name}) >= 1;
    }

    //delete
    public void deleteData(String name) {
        database.delete(TABLE_NAME, COL_0 + "=?", new String[]{name});
    }

    private ContentValues setData(String name, String place, String user) {
        ContentValues values = new ContentValues();
        if (name != null) values.put(COL_0, name);
        if (place != null) values.put(COL_1, place);
        if (user != null) values.put(COL_2, user);
        return values;
    }
}
