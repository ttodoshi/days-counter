package org.todoshis.dayscounter.activities;

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

import org.todoshis.dayscounter.controllers.CounterController;
import org.todoshis.dayscounter.activities.gestures.OnSwipeTouchListener;
import org.todoshis.dayscounter.R;

import java.time.LocalDate;

public class Settings extends AppCompatActivity {
    CounterController counterController;

    AlertDialog alert;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counterController = CounterController.getInstance(this);

        setContentView(R.layout.activity_settings);

        RelativeLayout settings = findViewById(R.id.settings);

        // right swipe
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

    public void addCounter(View view) {
        counterController.addCounter();
    }

    public void deleteLastCounter(View view) {
        counterController.deleteLastCounter();
    }

    public void showCalendarAlert(View view) {
        if (counterController.haveCounters()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            View calendarView = getLayoutInflater().inflate(R.layout.calendar_layour, null);
            CalendarView calendar = calendarView.findViewById(R.id.calendar);
            Button dateFromText = calendarView.findViewById(R.id.dateFromText);
            Button closeCalendar = calendarView.findViewById(R.id.close);
            Button submitDate = calendarView.findViewById(R.id.submit);

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
                            counterController.setDate(LocalDate.of(year, month + 1, dayOfMonth));
                            alert.cancel();
                        }
                    });
                }
            });

            builder.setView(calendarView).setCancelable(false);

            alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(Settings.this, getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
        }
    }

    public void showPhraseAlert(View view) {
        if (counterController.haveCounters()) {
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
            AlertDialog.Builder builder = createAlertDialogWithEditText(getString(R.string.new_phrase), customLayout);
            builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText editText = customLayout.findViewById(R.id.editText);
                    String newPhrase = editText.getText().toString();
                    counterController.setPhrase(newPhrase);
                    dialogInterface.cancel();
                }
            });
            alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(Settings.this, getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
        }
    }

    // AlertDialog with text input
    private AlertDialog.Builder createAlertDialogWithEditText(String title, View customLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setView(customLayout)
                .setTitle(title)
                .setCancelable(true);
        return builder;
    }


    private void goBackToMainScreen() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    // alert for change date
    private void showDateAlert() {
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
        AlertDialog.Builder builder = createAlertDialogWithEditText(getString(R.string.new_date_text), customLayout);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = customLayout.findViewById(R.id.editText);
                String startDate = editText.getText().toString();
                counterController.setDate(startDate);
                dialogInterface.cancel();
            }
        });
        alert = builder.create();
        alert.show();
    }
}