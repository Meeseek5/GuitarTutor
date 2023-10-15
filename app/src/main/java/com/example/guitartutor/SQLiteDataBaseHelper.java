package com.example.guitartutor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class SQLiteDataBaseHelper extends SQLiteOpenHelper {

    String TableName;

    // 建構子最後一個參數設定資料表名稱
    public SQLiteDataBaseHelper(@Nullable Context context, @Nullable String name,
                                @Nullable SQLiteDatabase.CursorFactory factory, int version, String TableName) {
        super(context, name, factory, version);
        this.TableName = TableName;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQLTable = "CREATE TABLE IF NOT EXISTS " + TableName + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Date TEXT, " +
                "Second TEXT" +
                ");";

        sqLiteDatabase.execSQL(SQLTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // 檢查資料表狀態，若無指定資料表則新增
    public void chickTable(){
        Cursor cursor = getWritableDatabase().rawQuery(
                "select DISTINCT tbl_name from sqlite_master where" +
                        " tbl_name = '" +
                        TableName + "'", null);

        if (cursor != null) {
            if (cursor.getCount() == 0)
                getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + TableName + "( " +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Date TEXT, " +
                        "Second TEXT" +
                        ");");
            cursor.close();
        }
    }

    // 查詢日期是否存在
    public boolean checkDate(String date) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TableName, null);
        cursor.moveToFirst();

        // 確認SQLite裡面有資料
        if(cursor.getCount() > 0) {
            // 從第一列資料就開始判斷
            if(date.equals(cursor.getString(1))) {
                cursor.close();
                return true;
            }

            while(cursor.moveToNext()) {
                if(date.equals(cursor.getString(1))) {
                    cursor.close();
                    return true;
                }
            }
        }
        cursor.close();
        return false;
    }

    // 用日期尋找當天最短秒數
    public String getSecondFromDate(String date) {
        String second = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TableName, null);
        cursor.moveToFirst();

        // 確認SQLite裡面有資料
        if(cursor.getCount() > 0) {
            // 從第一列資料就開始判斷
            if(date.equals(cursor.getString(1))) {
                second = cursor.getString(2);
            }

            while(cursor.moveToNext()) {
                if(date.equals(cursor.getString(1))) {
                    second = cursor.getString(2);
                }
            }
        }
        db.close();
        cursor.close();
        return second;
    }


    // 新增
    public void addData(String date, String second) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Date", date);
        values.put("Second", second);
        db.insert(TableName, null, values);
    }

    // 修改
    public void updateData(String date, String second) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Date", date);
        values.put("Second", second);
        db.update(TableName, values, "Date = ?", new String[]{date});
    }

    // 刪除全部資料
    public boolean deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+TableName);
        return true;
    }

    // 以id刪除資料
    public void deleteByIdEZ(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TableName,"_id = " + id,null);
    }

    // 以date刪除資料
    public void deleteByDate(String date){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TableName,"Date = " + date,null);
    }
}

