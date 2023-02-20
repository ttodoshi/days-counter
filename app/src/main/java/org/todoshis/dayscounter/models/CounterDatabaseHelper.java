package org.todoshis.dayscounter.models;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class CounterDatabaseHelper extends SQLiteOpenHelper {

    @SuppressLint("StaticFieldLeak")
    private static CounterDatabaseHelper counterDatabaseHelper;
    private static final String DATABASE_NAME = "Counters.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "counters";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_CURRENT = "current";
    private static final String COLUMN_START = "start_date";
    private static final String COLUMN_SHOW_MODE = "days_show_mode";
    private static final String COLUMN_PHRASE = "phrase";

    public static CounterDatabaseHelper getInstance(Context context) {
        if (counterDatabaseHelper == null) {
            counterDatabaseHelper = new CounterDatabaseHelper(context.getApplicationContext());
        }
        return counterDatabaseHelper;
    }


    private CounterDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Counter returnCurrentCounter() {
        Cursor cursor = counterDatabaseHelper.readAllData();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getInt(1) == 1) {
                    break;
                }
            } while (cursor.moveToNext());
            return new Counter(cursor.getString(2), cursor.getInt(3), cursor.getString(4));
        }
        cursor.close();
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CURRENT + " INTEGER, " +
                COLUMN_START + " TEXT, " +
                COLUMN_SHOW_MODE + " INTEGER, " +
                COLUMN_PHRASE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void editCounter(String startDate, int daysShowMode, String phrase) {
        SQLiteDatabase readableDB = counterDatabaseHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CURRENT, 1);
        cv.put(COLUMN_START, startDate);
        cv.put(COLUMN_SHOW_MODE, daysShowMode);
        cv.put(COLUMN_PHRASE, phrase);
        readableDB.update(TABLE_NAME, cv, "current=?", new String[]{"1"});
        readableDB.close();
    }

    // position = -1 or 1
    public boolean changeCurrent(int position) {
        SQLiteDatabase writeableDB = counterDatabaseHelper.getWritableDatabase();
        Cursor cursor = counterDatabaseHelper.readAllData();
        int nextId, currentId, firstOrLastId;
        if (position == -1) {
            cursor.moveToFirst();
            firstOrLastId = cursor.getInt(0);
            do {
                if (cursor.getInt(1) == 1)
                    break;
            } while (cursor.moveToNext());
        } else if (position == 1) {
            cursor.moveToLast();
            firstOrLastId = cursor.getInt(0);
            do {
                if (cursor.getInt(1) == 1)
                    break;
            } while (cursor.moveToPrevious());
        } else {
            return false;
        }
        currentId = cursor.getInt(0);
        if (currentId != firstOrLastId) {
            cursor.move(position);
            nextId = cursor.getInt(0);
        } else {
            return false;
        }
        String setCurrentTo0 = "UPDATE " + TABLE_NAME +" SET " + COLUMN_CURRENT+ " = '" + 0 + "' WHERE " + COLUMN_ID + " = " + currentId + ";";
        String setNextCurrent = "UPDATE " + TABLE_NAME + " SET " + COLUMN_CURRENT + " = '" + 1 + "' WHERE " + COLUMN_ID + " = " + nextId + ";";
        writeableDB.execSQL(setCurrentTo0);
        writeableDB.execSQL(setNextCurrent);
        return true;
    }

    public boolean addCounter(String startDate, Integer daysShowMode, String phrase) {
        SQLiteDatabase writeableDB = counterDatabaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CURRENT, counterDatabaseHelper.isEmpty() ? 1 : 0);
        cv.put(COLUMN_START, startDate);
        cv.put(COLUMN_SHOW_MODE, daysShowMode);
        cv.put(COLUMN_PHRASE, phrase);
        long res = writeableDB.insert(TABLE_NAME, null, cv);
        if (res == -1) {
            return false;
        }
        writeableDB.close();
        return true;
    }

    public int deleteLastCounter() {
        SQLiteDatabase writeableDB = counterDatabaseHelper.getWritableDatabase();
        Cursor cursor = counterDatabaseHelper.readAllData();
        // 0 - zero counters, 1 - only one counter, -1 - last counter was deleted
        int status;
        if (cursor.getCount() == 0) {
            status = 0;
        } else if (cursor.getCount() == 1) {
            status = 1;
        } else {
            cursor.moveToLast();
            if (cursor.getInt(1) == 1) {
                changeCurrent(-1);
            }
            writeableDB.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
            status = -1;
        }
        cursor.close();
        writeableDB.close();
        return status;
    }

    public boolean isEmpty() {
        Cursor cursor = counterDatabaseHelper.readAllData();
        boolean res = cursor.getCount() == 0;
        cursor.close();
        return res;
    }

    private Cursor readAllData() {
        SQLiteDatabase readableDB = counterDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = null;
        if (readableDB != null) {
            cursor = readableDB.rawQuery(query, null);
        }
        return cursor;
    }
}
