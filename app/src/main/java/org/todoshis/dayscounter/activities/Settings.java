package org.todoshis.dayscounter.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.todoshis.dayscounter.controllers.CounterController;
import org.todoshis.dayscounter.activities.gestures.OnSwipeTouchListener;
import org.todoshis.dayscounter.R;

import java.time.LocalDate;

public class Settings extends AppCompatActivity {
    CounterController counterController;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counterController = CounterController.getInstance(getApplicationContext());
        setContentView(R.layout.activity_settings);
        ScrollView settings = findViewById(R.id.settings);

        // // gestures
        OnSwipeTouchListener swipeListener = new SwipeListener(this);
        settings.setOnTouchListener(swipeListener);
        LinearLayout voidBetweenButtons = findViewById(R.id.linearLayoutForButtons);
        voidBetweenButtons.setOnTouchListener(swipeListener);
    }

    class SwipeListener extends OnSwipeTouchListener {
        public SwipeListener(Context context) {
            super(context);
        }

        public void onSwipeRight() {
            goToMainScreen();
        }
    }

    private void goToMainScreen() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    public void addCounter(View view) {
        counterController.addCounter();
    }

    public void deleteLastCounter(View view) {
        counterController.deleteLastCounter();
    }

    public void showCalendarAlert(View view) {
        if (counterController.haveCounters()) {
            View calendarView = getLayoutInflater().inflate(R.layout.calendar_layour, null);
            AlertDialog alert = new AlertDialog.Builder(Settings.this)
                    .setView(calendarView)
                    .setCancelable(false)
                    .create();
            CalendarView calendar = calendarView.findViewById(R.id.calendar);
            Button dateFromText = calendarView.findViewById(R.id.dateFromText);
            Button closeCalendar = calendarView.findViewById(R.id.close);
            Button submitDate = calendarView.findViewById(R.id.submit);

            closeCalendar.setOnClickListener(view1 -> alert.cancel());
            dateFromText.setOnClickListener(view1 -> {
                alert.cancel();
                showDateFromTextAlert();
            });
            calendar.setOnDateChangeListener((view1, year, month, dayOfMonth) -> submitDate.setOnClickListener(view2 -> {
                counterController.setDate(LocalDate.of(year, month + 1, dayOfMonth));
                alert.cancel();
            }));
            alert.show();
        } else
            Toast.makeText(Settings.this, getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
    }

    // alert for change phrase
    public void showPhraseAlert(View view) {
        if (counterController.haveCounters()) {
            createAlertDialogWithEditText(getString(R.string.new_phrase),
                    (text) -> counterController.setPhrase(text)).show();
        } else
            Toast.makeText(Settings.this, getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
    }

    // alert for change date from text input
    private void showDateFromTextAlert() {
        createAlertDialogWithEditText(getString(R.string.new_date_text),
                (text) -> counterController.setDate(text)).show();
    }

    private interface CounterChanger {
        void execute(String text);
    }

    // AlertDialog with text input
    @SuppressLint("InflateParams")
    private AlertDialog createAlertDialogWithEditText(String title, CounterChanger counterChanger) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        View view = getLayoutInflater().inflate(R.layout.alert_layout, null);
        builder.setView(view)
                .setTitle(title)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.save), (dialogInterface, i) -> {
                    EditText editText = view.findViewById(R.id.editText);
                    String text = editText.getText().toString();
                    counterChanger.execute(text);
                    dialogInterface.cancel();
                });
        return builder.create();
    }
}