package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

public class MainActivity extends BaseActivity {

    private TextView days, daysPhrase;
    private ImageView round, rectangle;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        days = findViewById(R.id.days);
        daysPhrase = findViewById(R.id.phrase);
        round = findViewById(R.id.roundOnBackground);
        rectangle = findViewById(R.id.rectangleOnBackground);
        RelativeLayout mainActivity = findViewById(R.id.background);

        // жесты
        mainActivity.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft(){
                goToSettings();
            }
            @Override
            public void onSwipeRight() {
                counter.setDaysShowMode();
                recreate();
            }
            @Override
            public void onSwipeUp() {
                goToNextCounter();
            }
            @Override
            public void onSwipeDown() {
                goToPreviousCounter();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadMainScreen(getDaysFromMillis(getDifference(counter.getStartDate())), counter.getPhrase());
    }

    // переход на activity настроек
    private void goToSettings(){
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // отображение дней, надписи и картинок на главном экране
    private void uploadMainScreen(int allDays, String phrase){
        picsShowMode();
        days.setText(daysShowMode(allDays));
        daysPhrase.setText(phraseShow(allDays, phrase));
    }

    // перевести из миллисекунд в прошедшие дни
    private int getDaysFromMillis(long millis){
        return (int)(millis / (24 * 60 * 60 * 1000));
    }

    private String phraseShow(int days, String phrase){
        StringBuilder text = new StringBuilder("");
        if(counter.getDaysShowMode()){
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
        else if ((value % 10 == 2 || value % 10 == 3 || value % 10 == 4) && exceptions){
            return secondWord;
        }
        else{
            return thirdWord;
        }
    }

    private String daysShowMode(int days){
        StringBuilder daysString = new StringBuilder("");
        if (counter.getDaysShowMode()){
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
        if (counter.getDaysShowMode()){
            rectangle.setVisibility(INVISIBLE);
            round.setVisibility(VISIBLE);
        }
        else{
            rectangle.setVisibility(VISIBLE);
            round.setVisibility(INVISIBLE);
        }
    }

    private void goToPreviousCounter(){
        if (currentCounter > 1){
            editor.putInt(storedData.CURRENT_COUNTER.name(), currentCounter - 1);
            editor.apply();
            recreate();
        }
    }
    private void goToNextCounter(){
        if (currentCounter < sPref.getInt(storedData.NUMBER_OF_COUNTERS.name(), 1)){
            editor.putInt(storedData.CURRENT_COUNTER.name(), currentCounter + 1);
            editor.apply();
            recreate();
        }
    }
}