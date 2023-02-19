package org.todoshis.dayscounter;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;

public class Counter {
    private final Context context;

    private String startDate;
    private int daysShowMode;
    private String phrase;

    CounterDatabaseHelper db;
    Cursor cursor;

    public Counter(Context context, String startDate, int daysShowMode, String phrase) {
        this.context = context;

        this.startDate = startDate;
        this.daysShowMode = daysShowMode;
        this.phrase = phrase;

        db = new CounterDatabaseHelper(context);
        cursor = db.readAllData();
    }

    public Date getDate() {
        try {
            return Settings.sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDate(Date newStartDate) {
        if (!db.isEmpty()) {
            this.startDate = Settings.sdf.format(newStartDate);
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
