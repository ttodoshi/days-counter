package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    protected Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    protected SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    protected Counter counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sPref.edit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        counter = new Counter(sPref.getString(storedData.START_DAY.name(), sdf.format(today)), sPref.getString(storedData.DAYS_SHOW_MODE.name(), "true"), sPref.getString(storedData.PHRASE.name(), ""));
    }

    protected long getDifference(Date selectedDate){
        return today.getTime() - selectedDate.getTime();
    }

    public class Counter {
        private String startDate;
        private String daysShowMode;
        private String phrase;

        public Counter(String startDate, String daysShowMode, String phrase) {
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
        public void setStartDate(String newStartDate) {
            editor.putString(storedData.START_DAY.name(), newStartDate);
            editor.apply();
            this.startDate = newStartDate;
        }

        // true - по умолчанию, только количество дней
        // false - количество лет, недель и дней
        public String getDaysShowMode() {
            return daysShowMode;
        }
        public void setDaysShowMode() {
            String newDaysShowMode = daysShowMode.equals("true") ? "false" : "true";
            editor.putString(storedData.DAYS_SHOW_MODE.name(), newDaysShowMode);
            editor.apply();
            this.daysShowMode = newDaysShowMode;
        }

        public String getPhrase() {
            return phrase;
        }
        public void setPhrase(String newPhrase) {
            editor.putString(storedData.PHRASE.name(), newPhrase);
            editor.apply();
            this.phrase = newPhrase;
        }
    }
}
