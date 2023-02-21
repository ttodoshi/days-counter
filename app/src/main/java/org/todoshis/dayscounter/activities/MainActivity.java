package org.todoshis.dayscounter.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.todoshis.dayscounter.controllers.CounterController;
import org.todoshis.dayscounter.activities.gestures.OnSwipeTouchListener;
import org.todoshis.dayscounter.R;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    CounterController counterController;
    private TextView days, daysPhrase;
    private ImageView round, rectangle;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counterController = CounterController.getInstance(getApplicationContext());
        setContentView(R.layout.activity_main);

        days = findViewById(R.id.days);
        daysPhrase = findViewById(R.id.phrase);
        round = findViewById(R.id.roundOnBackground);
        rectangle = findViewById(R.id.rectangleOnBackground);

        round.setVisibility(INVISIBLE);
        rectangle.setVisibility(INVISIBLE);

        RelativeLayout mainActivity = findViewById(R.id.backgroundCounter);

        // gestures
        mainActivity.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                goToSettings();
            }

            @Override
            public void onSwipeRight() {
                changeDaysShowMode();
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
        if (counterController.haveCounters()) {
            // show elements on main screen
            picsShowMode();
            long pastDays = ChronoUnit.DAYS.between(LocalDate.from(counterController.getDate()), LocalDate.now());
            days.setText(daysShowMode(Math.abs(pastDays)));
            daysPhrase.setText(phraseShow(pastDays, counterController.getPhrase()));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    private void goToPreviousCounter() {
        if (counterController.previous() && counterController.haveCounters()) {
            reloadMainPage();
            overridePendingTransition(R.anim.out_to_bottom, R.anim.in_from_top);
        }
    }

    private void goToNextCounter() {
        if (counterController.next() && counterController.haveCounters()) {
            reloadMainPage();
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
        }
    }

    private void reloadMainPage() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // go to settings activity
    private void goToSettings() {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }

    private void changeDaysShowMode() {
        if (counterController.haveCounters()) {
            counterController.setDaysShowMode();
            reloadMainPage();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    private void picsShowMode() {
        if (counterController.getDaysShowMode() == 1) {
            rectangle.setVisibility(INVISIBLE);
            round.setVisibility(VISIBLE);
        } else {
            rectangle.setVisibility(VISIBLE);
            round.setVisibility(INVISIBLE);
        }
    }

    private String daysShowMode(long daysCount) {
        String daysString;
        if (counterController.getDaysShowMode() == 1) {
            daysString = String.valueOf(daysCount);
        } else {
            long years = daysCount / 365;
            long weeks = (daysCount % 365) / 7;
            long remainingDays = (daysCount % 365) % 7;

            daysString = years + " " + checkEnding(years, getString(R.string.year),
                    getString(R.string.year_different_ending), getString(R.string.years)) + " " +
                    weeks + " " + checkEnding(weeks, getString(R.string.week),
                    getString(R.string.week_different_ending), getString(R.string.weeks)) + " " +
                    remainingDays + " " + checkEnding(remainingDays, getString(R.string.day),
                    getString(R.string.day_different_ending), getString(R.string.days));
            days.setTextSize(20);
        }
        return daysString;
    }

    private String phraseShow(long days, String phrase) {
        StringBuilder text = new StringBuilder();
        if (counterController.getDaysShowMode() == 1) {
            text.append(checkEnding(Math.abs(days), getString(R.string.day), getString(R.string.day_different_ending), getString(R.string.days)));
            text.append(" ");
        }
        text.append(phrase);
        if (days < 0) {
            if (!phrase.isEmpty()) {
                text.append(" ");
            }
            text.append(getString(R.string.days_left));
        }
        return text.toString();
    }

    // TODO remove from this class
    private String checkEnding(long value, String firstWord, String secondWord, String thirdWord) {
        boolean exceptions = !(value % 100 == 11 || value % 100 == 12 || value % 100 == 13 || value % 100 == 14);
        if (value % 10 == 1 && exceptions) {
            return firstWord;
        } else if ((value % 10 == 2 || value % 10 == 3 || value % 10 == 4) && exceptions) {
            return secondWord;
        } else {
            return thirdWord;
        }
    }
}