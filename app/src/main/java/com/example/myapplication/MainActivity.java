package com.example.myapplication;

import java.text.ParseException;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private TextView aloneDays, daysText;
    private RelativeLayout mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aloneDays = findViewById(R.id.mainText);
        daysText = findViewById(R.id.daysText);
        mainActivity = findViewById(R.id.background);

        // жест
        mainActivity.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft(){
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    private void uploadDays(Date selectedDate, Date currentDate, String phrase){
        long difference = currentDate.getTime() - selectedDate.getTime();
        int days =  (int)(difference / (24 * 60 * 60 * 1000));
        String daysString = String.valueOf(days);
        changeDaysText(days, phrase);
        aloneDays.setText(daysString);
    }
    private void changeDaysText(int days, String phrase){
        StringBuilder text = new StringBuilder("");
        switch (days){
            case 11: case 12: case 13: case 14:
                text.append(getString(R.string.days));
                break;
            default:
                if (days % 10 == 1){
                    text.append(getString(R.string.day));
                }
                else if (days % 10 == 2 || days % 10 == 3 || days % 10 == 4){
                    text.append(getString(R.string.day_different_ending));
                }
                else{
                    text.append(getString(R.string.days));
                }
                break;
        }
        text.append(" ");
        text.append(phrase);
        daysText.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // подгружаем данные о днях
        String todayStr = sdf.format(today);
        try {
            uploadDays(sdf.parse(sPref.getString("START_DAY", todayStr)), today, sPref.getString("DAYS_TEXT", " в одиночестве"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}