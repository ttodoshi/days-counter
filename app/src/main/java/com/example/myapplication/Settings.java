package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.util.Date;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

public class Settings extends BaseActivity {

    private CalendarView calendar;
    private Button submitDate;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RelativeLayout settings = findViewById(R.id.settings);
        Button changePhraseButton = findViewById(R.id.changePhrase);
        Button dateButton = findViewById(R.id.changeStartDate);
        calendar = findViewById(R.id.calendar);
        calendar.setVisibility(INVISIBLE);
        Button dateFromText = findViewById(R.id.dateFromText);
        Button closeCalendar = findViewById(R.id.close);
        submitDate = findViewById(R.id.submit);
        Button addCounter = findViewById(R.id.addCounter);
        Button delLastCounter = findViewById(R.id.delLastCounter);

        // вызов алерта с изменением фразы
        changePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhraseAlert();
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
                showDateAlert();
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
                        saveStartDate(new Date(year-1900, month, dayOfMonth));
                    }
                });
            }
        });

        addCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCounter();
            }
        });

        delLastCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delLastCounter();
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

    // alert для изменения фразы
    private void showPhraseAlert(){
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
        AlertDialog.Builder builder = createAlertDialogWithEditText("Выберите новую надпись", customLayout);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = customLayout.findViewById(R.id.editText);
                String newPhrase = editText.getText().toString();
                counter.setPhrase(newPhrase);
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // alert для изменения даты начала отсчёта текстом
    private void showDateAlert(){
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
        AlertDialog.Builder builder = createAlertDialogWithEditText("Введите дату в формате дд.мм.гггг", customLayout);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = customLayout.findViewById(R.id.editText);
                String startDate = editText.getText().toString();
                try {
                    saveStartDate(sdf.parse(startDate));
                } catch (ParseException e) {
                    showMessage("Неверный формат");
                }
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveStartDate(Date selectedDate){
        if (getDifference(selectedDate) < 0){
            showMessage("Из будущего?");
        }
        else{
            counter.setStartDate(sdf.format(selectedDate));
        }
        calendar.setVisibility(INVISIBLE);
    }

    private void addCounter(){
        int numOfCounters = sPref.getInt(StoredData.NUMBER_OF_COUNTERS.name(), 1);
        editor.putInt(StoredData.NUMBER_OF_COUNTERS.name(), numOfCounters + 1);
        editor.apply();
        showMessage("Новый счётчик создан");
    }
    private void delLastCounter(){
        int numOfCounters = sPref.getInt(StoredData.NUMBER_OF_COUNTERS.name(), 1);
        if (numOfCounters > 1){
            editor.putInt(StoredData.NUMBER_OF_COUNTERS.name(), numOfCounters - 1);
            if (currentCounter == numOfCounters){
                editor.putInt(StoredData.CURRENT_COUNTER.name(), numOfCounters - 1);
            }
            editor.remove(new StringBuilder(StoredData.START_DAY.name()).append(numOfCounters).toString());
            editor.remove(new StringBuilder(StoredData.DAYS_SHOW_MODE.name()).append(numOfCounters).toString());
            editor.remove(new StringBuilder(StoredData.PHRASE.name()).append(numOfCounters).toString());
            editor.apply();
            showMessage("Последний счётчик удалён");
        }
        else{
            showMessage("У вас только 1 счётчик");
        }
    }
}