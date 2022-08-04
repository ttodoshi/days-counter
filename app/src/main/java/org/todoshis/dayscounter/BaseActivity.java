package org.todoshis.dayscounter;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {
    protected SharedPreferences sPref;
    protected SharedPreferences.Editor editor;
    public static Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public static int currentCounter;
    protected Counter counter;

    CounterDatabaseHelper db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sPref.edit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = new CounterDatabaseHelper(this);
        cursor = db.readAllData();
        currentCounter = sPref.getInt("CURRENT_COUNTER", 1);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getInt(0) == currentCounter) {
                    break;
                }
            } while (cursor.moveToNext());
            counter = new Counter(BaseActivity.this, cursor.getString(1), cursor.getInt(2), cursor.getString(3));
        }
    }

    public static long getDifference(Date selectedDate) {
        return today.getTime() - selectedDate.getTime();
    }
}

