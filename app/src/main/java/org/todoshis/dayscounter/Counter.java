package org.todoshis.dayscounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Counter {
    private final Context context;

    private String startDate;
    private int daysShowMode;
    private String phrase;

    static CounterDatabaseHelper db;
    Cursor cursor;

    public Counter(Context context, String startDate, int daysShowMode, String phrase) {
        this.context = context;
        this.startDate = startDate;
        this.daysShowMode = daysShowMode;
        setPhrase(phrase);
        db = CounterDatabaseHelper.getInstance(context);
        cursor = db.readAllData();
    }

    public void updateValues() {
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getInt(1) == 1) {
                    break;
                }
            } while (cursor.moveToNext());
            this.startDate = cursor.getString(2);
            this.daysShowMode = cursor.getInt(3);
            this.phrase = cursor.getString(4);
        }
    }

    public static Counter returnCurrentCounter(Context context) {
        Cursor cursor = db.readAllData();
        Counter counter = null;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getInt(1) == 1) {
                    break;
                }
            } while (cursor.moveToNext());

            counter = new Counter(context, cursor.getString(2), cursor.getInt(3), cursor.getString(4));
        }
        cursor.close();
        return counter;
    }
    public static boolean addCounter(String startDate) {
        return db.addCounter(startDate, 1, "");
    }

    public static int deleteLastCounter() {
        return db.deleteLastCounter();
    }
    public static boolean next() {
        return db.changeCurrent(1);
    }
    public static boolean previous() {
        return db.changeCurrent(-1);
    }

    public static boolean haveCounters() {
        return !db.isEmpty();
    }

    public String getDate() {
        return startDate;
    }

    public void setDate(String newStartDate) {
        if (!db.isEmpty()) {
            this.startDate = newStartDate;
            db.editCounter(this.startDate, this.daysShowMode, this.phrase);
        }
    }

    // 1 - только количество дней
    // 0 - количество лет, недель и дней
    public int getDaysShowMode() {
        return daysShowMode;
    }

    public void setDaysShowMode() {
        this.daysShowMode = (daysShowMode == 1) ? 0 : 1;
        db.editCounter(this.startDate, this.daysShowMode, this.phrase);
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String newPhrase) {
        if (newPhrase.trim().length() == 0) {
            Toast.makeText(context, context.getString(R.string.invalid_format), Toast.LENGTH_SHORT).show();
        } else if (newPhrase.length() > 100) {
            Toast.makeText(context, context.getString(R.string.too_long), Toast.LENGTH_SHORT).show();
        } else {
            this.phrase = newPhrase;
            db.editCounter(this.startDate, this.daysShowMode, this.phrase);
        }
    }
}
