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
    public static Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    protected Counter counter;

    protected CounterDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = new CounterDatabaseHelper(this);
        Cursor cursor = db.readAllData();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getInt(1) == 1) {
                    break;
                }
            } while (cursor.moveToNext());
            counter = new Counter(BaseActivity.this, cursor.getString(2), cursor.getInt(3), cursor.getString(4));
        }
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public static long getDifference(Date selectedDate) {
        return today.getTime() - selectedDate.getTime();
    }
}

