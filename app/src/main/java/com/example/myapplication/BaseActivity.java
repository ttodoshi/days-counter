package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {
    protected SharedPreferences sPref;
    protected SharedPreferences.Editor editor;
    protected Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    protected SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

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

        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            do { if (cursor.getInt(0) == currentCounter){
                break;
            }} while (cursor.moveToNext());
            counter = new Counter(cursor.getString(1), cursor.getInt(2), cursor.getString(3));
        }
    }

    protected long getDifference(Date selectedDate){
        return today.getTime() - selectedDate.getTime();
    }

    public class Counter {
        private String startDate;
        private int daysShowMode;
        private String phrase;

        public Counter(String startDate, int daysShowMode, String phrase) {
            this.startDate = startDate;
            this.daysShowMode = daysShowMode;
            this.phrase = phrase;
        }

        public Date getStartDate() {
            try {
                return sdf.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        public void setStartDate(Date newStartDate) {
            if(cursor.getCount() != 0){
                if (getDifference(newStartDate) > 0){
                    this.startDate = sdf.format(newStartDate);
                    db.editCounter(this.startDate, this.daysShowMode, this.phrase);
                }
                else{
                    ShowMessage.showMessage(BaseActivity.this, "Не удалось сохранить дату");
                }
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
            recreate();
        }

        public String getPhrase() {
            return phrase;
        }
        public void setPhrase(String newPhrase) {
            if (newPhrase.trim().length() == 0){
                ShowMessage.showMessage(BaseActivity.this, "Неверный формат");
            }
            else if (newPhrase.length() > 100){
                ShowMessage.showMessage(BaseActivity.this, "Слишком длинная надпись");
            }
            else {
                if(cursor.getCount() != 0){
                    this.phrase = newPhrase;
                    db.editCounter(this.startDate, this.daysShowMode, this.phrase);
                }
            }
        }
    }
}
