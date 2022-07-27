package com.example.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

public class MainActivity extends AppCompatActivity {

    private Button submitDate;
    private ImageButton settings;
    private TextView aloneDays, daysText;
    private CalendarView calendar;
    private SharedPreferences sPref;
    private RelativeLayout backgroundMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aloneDays = findViewById(R.id.mainText);
        daysText = findViewById(R.id.daysText);
        calendar = findViewById(R.id.calendar);
        calendar.setVisibility(INVISIBLE);
        submitDate = findViewById(R.id.submit);
        settings = findViewById(R.id.settings);
        backgroundMainActivity = findViewById(R.id.background);

        // подгружаем данные о днях
        sPref = getPreferences(MODE_PRIVATE);
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String todayStr = sdf.format(today);
        String daysText = sPref.getString("DAYS_TEXT", " в одиночестве");
        try {
            uploadDays(sdf.parse(sPref.getString("START_DAY", todayStr)), today, daysText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // жесты
        backgroundMainActivity.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                calendar.setVisibility(VISIBLE);
            }
            @Override
            public void onSwipeRight(){
                calendar.setVisibility(INVISIBLE);
            }
        });

        // сохранение даты, выбранной в календаре по нажатию кнопки
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                submitDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date selectedDate = new Date(year-1900, month, dayOfMonth);
                        saveStartDate(sdf, selectedDate, today, daysText);
                        calendar.setVisibility(INVISIBLE);
                    }
                });
            }
        });

        //кнопка настроек (изменение надписи)
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
                builder.setView(customLayout);
                builder.setTitle("Выберите новую надпись")
                        .setCancelable(true)
                        .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText editText = customLayout.findViewById(R.id.editText);
                                SharedPreferences.Editor ed = sPref.edit();
                                String newPhrase = editText.getText().toString();
                                ed.putString("DAYS_TEXT", newPhrase);
                                ed.commit();
                                dialogInterface.cancel();
                                try {
                                    uploadDays(sdf.parse(sPref.getString("START_DAY", todayStr)), today, newPhrase);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void saveStartDate(SimpleDateFormat pattern, Date selectedDate, Date currentDate, String daysText){
        long difference = currentDate.getTime() - selectedDate.getTime();
        if (difference < 0){
            Toast.makeText(MainActivity.this, "Ты че из будущего блять?", Toast.LENGTH_LONG).show();
        }
        else{
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("START_DAY", pattern.format(selectedDate));
            ed.commit();
            uploadDays(selectedDate, currentDate, daysText);
        }
    }
    private void uploadDays(Date selectedDate, Date currentDate, String phrase){
        long difference = currentDate.getTime() - selectedDate.getTime();
        int days =  (int)(difference / (24 * 60 * 60 * 1000));
        changeDaysText(days, phrase);
        String daysString = String.valueOf(days);
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
}