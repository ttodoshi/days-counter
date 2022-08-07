package org.todoshis.dayscounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CounterDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "Counters.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "counters";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_CURRENT = "current";
    private static final String COLUMN_START = "start_date";
    private static final String COLUMN_SHOWMODE = "days_show_mode";
    private static final String COLUMN_PHRASE = "phrase";


    public CounterDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CURRENT + " INTEGER, " +
                COLUMN_START + " TEXT, " +
                COLUMN_SHOWMODE + " INTEGER, " +
                COLUMN_PHRASE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void editCounter(String startDate, int daysShowMode, String phrase){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CURRENT, 1);
        cv.put(COLUMN_START, startDate);
        cv.put(COLUMN_SHOWMODE, daysShowMode);
        cv.put(COLUMN_PHRASE, phrase);
        long res = db.update(TABLE_NAME, cv, "current=?", new String[]{"1"});
        if (res == -1){
            ShowMessage.showMessage(context, context.getString(R.string.couldnt_change_counter));
        }
        db.close();
    }
    // position = -1 or 1
    public boolean changeCurrent(int position){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = this.readAllData();
        int nextId, currentId, firstOrLastId;
        if (position == -1){
            cursor.moveToFirst();
            firstOrLastId = cursor.getInt(0);
            do { if (cursor.getInt(1) == 1){
                break;
            }} while (cursor.moveToNext());
        }
        else if (position == 1){
            cursor.moveToLast();
            firstOrLastId = cursor.getInt(0);
            do { if (cursor.getInt(1) == 1){
                break;
            }} while (cursor.moveToPrevious());
        }
        else {
            return false;
        }
        currentId = cursor.getInt(0);
        if (currentId != firstOrLastId) {
            cursor.move(position);
            nextId = cursor.getInt(0);
        }
        else{
            return false;
        }
        String setCurrentTo0 = "UPDATE " + TABLE_NAME +" SET " + COLUMN_CURRENT+ " = '" + 0 + "' WHERE " + COLUMN_ID + " = " + currentId + ";";
        String setNextCurrent = "UPDATE " + TABLE_NAME + " SET " + COLUMN_CURRENT + " = '" + 1 + "' WHERE " + COLUMN_ID + " = " + nextId + ";";
        db.execSQL(setCurrentTo0);
        db.execSQL(setNextCurrent);
        cursor.close();
        return true;
    }

    public void addCounter(String startDate, Integer daysShowMode, String phrase){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = readAllData();
        cv.put(COLUMN_CURRENT, cursor.getCount() == 0 ? 1 : 0);
        cv.put(COLUMN_START, startDate);
        cv.put(COLUMN_SHOWMODE, daysShowMode);
        cv.put(COLUMN_PHRASE, phrase);
        long res = db.insert(TABLE_NAME, null, cv);
        if (res == -1){
            ShowMessage.showMessage(context, context.getString(R.string.counter_didnt_add));
        }
        else{
            ShowMessage.showMessage(context, context.getString(R.string.new_counter));
        }
        cursor.close();
        db.close();
    }
    public void delLastCounter(){
        Cursor cursor = this.readAllData();
        if (cursor.getCount() == 0){
            ShowMessage.showMessage(context, context.getString(R.string.no_counters));
        }
        else if (cursor.getCount() == 1){
            ShowMessage.showMessage(context, context.getString(R.string.last_counter));
        }
        else{
            SQLiteDatabase db = this.getWritableDatabase();
            cursor.moveToLast();
            if (cursor.getInt(1) == 1){
                changeCurrent(-1);
            }
            db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
            ShowMessage.showMessage(context, context.getString(R.string.del_last_counter_message));
        }
        cursor.close();
    }

    public boolean isEmpty(){
        Cursor cursor = this.readAllData();
        boolean res = cursor.getCount() == 0;
        cursor.close();
        return res;
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
