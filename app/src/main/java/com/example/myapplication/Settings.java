package com.example.myapplication;

import static android.view.View.INVISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

public class Settings extends BaseActivity {

    private CalendarView calendar;
    private Button dateButton, changeDaysTextButton, submitDate, closeCalendar, dateFromText;
    private RelativeLayout settings;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = findViewById(R.id.settings);
        changeDaysTextButton = findViewById(R.id.changeDaysText);
        dateButton = findViewById(R.id.changeStartDate);
        calendar = findViewById(R.id.calendar);
        calendar.setVisibility(INVISIBLE);
        dateFromText = findViewById(R.id.dateFromText);
        closeCalendar = findViewById(R.id.close);
        submitDate = findViewById(R.id.submit);

        // вызов алерта с изменением фразы
        changeDaysTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
                AlertDialog.Builder builder = createAlertDialogWithEditText("Выберите новую надпись", customLayout);
                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText editText = customLayout.findViewById(R.id.editText);
                                String newPhrase = editText.getText().toString();
                                if (newPhrase.trim().length() > 0 && newPhrase.length() < 100){
                                    editor.putString("DAYS_TEXT", newPhrase);
                                    editor.apply();
                                }
                                else{
                                    Toast.makeText(Settings.this, "Неверный формат или слишком длинная надпись", Toast.LENGTH_LONG).show();
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
                calendar.setVisibility(VISIBLE);
            }
        });

        // ввести дату текстом
        dateFromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
                AlertDialog.Builder builder = createAlertDialogWithEditText("Введите дату в формате дд.мм.гггг", customLayout);
                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editText = customLayout.findViewById(R.id.editText);
                        String startDate = editText.getText().toString();
                        try {
                            sdf.parse(startDate);
                            editor.putString("START_DAY", startDate);
                            editor.apply();
                        } catch (ParseException e) {
                            Toast.makeText(Settings.this, "Неверный формат", Toast.LENGTH_LONG).show();
                        }
                        dialogInterface.cancel();
                        calendar.setVisibility(INVISIBLE);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // закрыть календарь
        closeCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        saveStartDate(selectedDate);
                        calendar.setVisibility(INVISIBLE);
                    }
                });
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

    // создание AlertDialog с вводом текста
    private AlertDialog.Builder createAlertDialogWithEditText(String title, View customLayout){
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setView(customLayout)
                .setTitle(title)
                .setCancelable(true);
        return builder;
    }

    // сохраняем дату начала
    private void saveStartDate(Date selectedDate){
        if (getDifference(selectedDate) < 0){
            Toast.makeText(Settings.this, "Из будущего?", Toast.LENGTH_LONG).show();
        }
        else{
            editor.putString("START_DAY", sdf.format(selectedDate));
            editor.apply();
        }
    }
}