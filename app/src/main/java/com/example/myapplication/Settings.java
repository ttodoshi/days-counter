package com.example.myapplication;

import static android.view.View.INVISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

public class Settings extends BaseActivity {

    private CalendarView calendar;
    private Button dateButton, changeDaysTextButton, submitDate, closeCalendar;
    private RelativeLayout settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = findViewById(R.id.settings);
        changeDaysTextButton = findViewById(R.id.changeDaysText);
        dateButton = findViewById(R.id.changeStartDate);
        calendar = findViewById(R.id.calendar);
        calendar.setVisibility(INVISIBLE);
        submitDate = findViewById(R.id.submit);
        closeCalendar = findViewById(R.id.close);

        // вызов алерта с изменением фразы
        changeDaysTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
                builder.setView(customLayout);
                builder.setTitle("Выберите новую надпись")
                        .setCancelable(true)
                        .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText editText = customLayout.findViewById(R.id.editText);
                                String newPhrase = editText.getText().toString();
                                if (newPhrase.trim().length() > 0 && newPhrase.length() < 100){
                                    SharedPreferences.Editor ed = sPref.edit();
                                    ed.putString("DAYS_TEXT", newPhrase);
                                    ed.commit();
                                }
                                else{
                                    Toast.makeText(Settings.this, "Хуйню написал", Toast.LENGTH_LONG).show();
                                }
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // отображение календаря
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendar.getVisibility() == VISIBLE){
                    calendar.setVisibility(INVISIBLE);
                }
                else if (calendar.getVisibility() == INVISIBLE){
                    calendar.setVisibility(VISIBLE);
                }
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
                        saveStartDate(sdf, selectedDate, today);
                        calendar.setVisibility(INVISIBLE);
                    }
                });
            }
        });

        // закрыть календарь
        closeCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.setVisibility(INVISIBLE);
            }
        });

        // жест
        settings.setOnTouchListener(new OnSwipeTouchListener(Settings.this) {
            @Override
            public void onSwipeRight() {
                finish();
            }
        });
    }

    // сохраняем дату начала
    private void saveStartDate(SimpleDateFormat pattern, Date selectedDate, Date currentDate){
        long difference = currentDate.getTime() - selectedDate.getTime();
        if (difference < 0){
            Toast.makeText(Settings.this, "Ты че из будущего блять?", Toast.LENGTH_LONG).show();
        }
        else{
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("START_DAY", pattern.format(selectedDate));
            ed.commit();
        }
    }
}