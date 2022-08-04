package org.todoshis.dayscounter;

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

        round.setVisibility(INVISIBLE);
        rectangle.setVisibility(INVISIBLE);

        RelativeLayout mainActivity = findViewById(R.id.backgroundCounter);

        // жесты
        mainActivity.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft(){
                goToSettings();
            }
            @Override
            public void onSwipeRight() {
                if(cursor.getCount() != 0){
                    counter.setDaysShowMode();
                    recreate();
                }
            }
            @Override
            public void onSwipeUp() {
                if(cursor.getCount() != 0){
                    goToNextCounter();
                }
            }
            @Override
            public void onSwipeDown() {
                if(cursor.getCount() != 0){
                    goToPreviousCounter();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cursor.getCount() != 0){
            uploadMainScreen(getDaysFromMillis(getDifference(counter.getStartDate())), counter.getPhrase());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
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
        if(counter.getDaysShowMode() == 1){
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

    private String daysShowMode(int daysCount){
        String daysString;;
        if (counter.getDaysShowMode() == 1){
            daysString = String.valueOf(daysCount);
        }
        else{
            int years = daysCount / 365;
            int weeks = (daysCount % 365) / 7;
            int remainingDays = (daysCount % 365) % 7;

            daysString = years + " " + checkEnding(years, getString(R.string.year),
                    getString(R.string.year_different_ending), getString(R.string.years)) +  " " +
                    weeks + " " + checkEnding(weeks, getString(R.string.week),
                    getString(R.string.week_different_ending), getString(R.string.weeks)) + " " +
                    remainingDays + " " + checkEnding(remainingDays, getString(R.string.day),
                    getString(R.string.day_different_ending), getString(R.string.days));
            days.setTextSize(20);
        }
        return daysString;
    }

    private void picsShowMode(){
        if (counter.getDaysShowMode() == 1){
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
            cursor.moveToFirst();
            do { if (cursor.getInt(0) == currentCounter){
                break;
            }} while (cursor.moveToNext());
            cursor.move(-1);
            editor.putInt("CURRENT_COUNTER", cursor.getInt(0));
            editor.apply();
            recreate();
        }
    }
    private void goToNextCounter(){
        cursor.moveToLast();
        if (cursor.getInt(0) != currentCounter){
            do { if (cursor.getInt(0) == currentCounter){
                break;
            }} while (cursor.move(-1));
            cursor.move(1);
            editor.putInt("CURRENT_COUNTER", cursor.getInt(0));
            editor.apply();
            recreate();
        }
    }
}