package org.todoshis.dayscounter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Settings extends AppCompatActivity {

    AlertDialog alert;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RelativeLayout settings = findViewById(R.id.settings);
        Button changePhraseButton = findViewById(R.id.changePhrase);
        Button dateButton = findViewById(R.id.changeStartDate);
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
                if (CounterController.haveCounters()) {
                    showCalendarAlert();
                } else {
                    Toast.makeText(Settings.this, getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
                }
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
                goBackToMainScreen();
            }
        });
        LinearLayout voidBetweenButtons = findViewById(R.id.linearLayoutForButtons);
        voidBetweenButtons.setOnTouchListener(new OnSwipeTouchListener(Settings.this) {
            @Override
            public void onSwipeRight() {
                goBackToMainScreen();
            }
        });
    }

    private void goBackToMainScreen() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    private void showCalendarAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        View view = getLayoutInflater().inflate(R.layout.calendar_layour, null);
        CalendarView calendar = view.findViewById(R.id.calendar);
        Button dateFromText = view.findViewById(R.id.dateFromText);
        Button closeCalendar = view.findViewById(R.id.close);
        Button submitDate = view.findViewById(R.id.submit);

        closeCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
        dateFromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
                showDateAlert();
            }
        });
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                submitDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date selectedDate = new Date(year - 1900, month, dayOfMonth);
                        CounterController.setDate(selectedDate);
                        alert.cancel();
                    }
                });
            }
        });

        builder.setView(view)
                .setCancelable(false);

        alert = builder.create();
        alert.show();
    }

    // создание AlertDialog с вводом текста
    private AlertDialog.Builder createAlertDialogWithEditText(String title, View customLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setView(customLayout)
                .setTitle(title)
                .setCancelable(true);
        return builder;
    }

    // alert для изменения фразы
    private void showPhraseAlert() {
        if (CounterController.haveCounters()) {
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
            AlertDialog.Builder builder = createAlertDialogWithEditText(getString(R.string.new_phrase), customLayout);
            builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText editText = customLayout.findViewById(R.id.editText);
                    String newPhrase = editText.getText().toString();
                    CounterController.setPhrase(newPhrase);
                    dialogInterface.cancel();
                }
            });
            alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(Settings.this, getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
        }
    }

    // alert для изменения даты начала отсчёта текстом
    private void showDateAlert() {
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
        AlertDialog.Builder builder = createAlertDialogWithEditText(getString(R.string.new_date_text), customLayout);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = customLayout.findViewById(R.id.editText);
                String startDate = editText.getText().toString();
                CounterController.setDate(startDate);
                dialogInterface.cancel();
            }
        });
        alert = builder.create();
        alert.show();
    }

    private void addCounter() {
        CounterController.addCounter();
    }

    private void delLastCounter() {
        CounterController.deleteLastCounter();
    }
}