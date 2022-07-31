package com.example.myapplication;

import java.text.ParseException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

public class MainActivity extends BaseActivity {

    private TextView aloneDays, daysText;
    private RelativeLayout mainActivity;
    private ImageView round, rectangle;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aloneDays = findViewById(R.id.mainText);
        daysText = findViewById(R.id.daysText);
        round = findViewById(R.id.roundOnBackground);
        rectangle = findViewById(R.id.rectangleOnBackground);
        mainActivity = findViewById(R.id.background);

        // жесты
        mainActivity.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft(){
                goToSettings();
            }
            @Override
            public void onSwipeRight() {
                saveDaysShowMode();
                recreate();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            uploadMainScreen(sdf.parse(getStartDay()), getDaysText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // переход на activity настроек
    private void goToSettings(){
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // отображение дней и надписи на главном экране
    private void uploadMainScreen(Date selectedDate, String phrase){
        int days = getDaysFromMillis(getDifference(selectedDate));
        picsShowMode();
        aloneDays.setText(daysShowMode(days));
        daysText.setText(daysTextShow(days, phrase));
    }

    private String getStartDay(){
        return sPref.getString("START_DAY", sdf.format(today));
    }
    private String getDaysShowMode(){
        return sPref.getString("DAYS_SHOW_MODE", "true");
    }
    private String getDaysText(){
        return sPref.getString("DAYS_TEXT", "");
    }

    // перевести из миллисекунд в прошедшие дни
    private int getDaysFromMillis(long millis){
        return (int)(millis / (24 * 60 * 60 * 1000));
    }

    private String daysTextShow(int days, String phrase){
        StringBuilder text = new StringBuilder("");
        if(getDaysShowMode().equals("true")){
            text.append(checkEnding(days, getString(R.string.day), getString(R.string.day_different_ending), getString(R.string.days)));
            text.append(" ");
        }
        text.append(phrase);
        return text.toString();
    }

    private String checkEnding(int value, String firstWord, String secondWord, String thirdWord){
        boolean exceptions = !(value % 100 == 11 || value % 100 == 12 || value % 100 == 13 || value % 100 == 14);
        if (value % 10 == 1 && exceptions){
            return firstWord;
        }
        else if (value % 10 == 2 || value % 10 == 3 || value % 10 == 4 && exceptions){
            return secondWord;
        }
        else{
            return thirdWord;
        }
    }

    // true - по умолчанию, только количество дней
    // false - количество лет, недель и дней
    private void saveDaysShowMode(){
        if(getDaysShowMode().equals("true")){
            editor.putString("DAYS_SHOW_MODE", "false");
        }
        else if(getDaysShowMode().equals("false")){
            editor.putString("DAYS_SHOW_MODE", "true");
        }
        editor.apply();
    }

    private String daysShowMode(int days){
        StringBuilder daysString = new StringBuilder("");
        if (getDaysShowMode().equals("true")){
            daysString.append(days);
        }
        else{
            int years = days / 365;
            int weeks = (days % 365) / 7;
            int remainingDays = (days % 365) % 7;

            daysString.append(years);
            daysString.append(" ");
            daysString.append(checkEnding(years, getString(R.string.year), getString(R.string.year_different_ending), getString(R.string.years)));
            daysString.append(" ");
            daysString.append(weeks);
            daysString.append(" ");
            daysString.append(checkEnding(weeks, getString(R.string.week), getString(R.string.week_different_ending), getString(R.string.weeks)));
            daysString.append(" ");
            daysString.append(remainingDays);
            daysString.append(" ");
            daysString.append(checkEnding(remainingDays, getString(R.string.day), getString(R.string.day_different_ending), getString(R.string.days)));
        }
        return daysString.toString();
    }

    private void picsShowMode(){
        if (getDaysShowMode().equals("true")){
            rectangle.setVisibility(INVISIBLE);
            round.setVisibility(VISIBLE);
        }
        else{
            rectangle.setVisibility(VISIBLE);
            round.setVisibility(INVISIBLE);
        }
    }
}