package org.todoshis.dayscounter.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

import androidx.appcompat.app.AppCompatActivity;

import org.todoshis.dayscounter.activities.spell_checker.SpellChecker;
import org.todoshis.dayscounter.controllers.CounterController;
import org.todoshis.dayscounter.activities.gestures.OnSwipeTouchListener;
import org.todoshis.dayscounter.R;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        RelativeLayout mainActivity = findViewById(R.id.backgroundCounter);

        // gestures
        OnSwipeTouchListener swipeListener = new SwipeListener(this);
        mainActivity.setOnTouchListener(swipeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (counterController.haveCounters()) {
            // show elements on main screen
            if (counterController.getDaysShowMode() == 1) {
                rectangle.setVisibility(INVISIBLE);
                round.setVisibility(VISIBLE);
            } else {
                rectangle.setVisibility(VISIBLE);
                round.setVisibility(INVISIBLE);
            }
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
    class SwipeListener extends OnSwipeTouchListener {
        public SwipeListener(Context context) {
            super(context);
        }
        public void onSwipeLeft() {
            // go to settings
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        }
        public void onSwipeRight() {
            // change days show mode
            if (counterController.haveCounters()) {
                counterController.setDaysShowMode();
                reloadMainPage();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        }
        public void onSwipeUp() {
            // go to next counter
            if (counterController.next() && counterController.haveCounters()) {
                reloadMainPage();
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            }
        }
        public void onSwipeDown() {
            // go to previous counter
            if (counterController.previous() && counterController.haveCounters()) {
                reloadMainPage();
                overridePendingTransition(R.anim.out_to_bottom, R.anim.in_from_top);
            }
        }
    }

    private void reloadMainPage() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("DefaultLocale")
    private String daysShowMode(long daysCount) {
        String daysString;
        if (counterController.getDaysShowMode() == 1) {
            daysString = String.valueOf(daysCount);
        } else {
            long years = daysCount / 365;
            long weeks = (daysCount % 365) / 7;
            long remainingDays = (daysCount % 365) % 7;
            daysString = String.format("%d %s %d %s %d %s", years, SpellChecker.checkEndingDependingOnQuantity(years, getString(R.string.year), getString(R.string.year_different_ending), getString(R.string.years)), weeks, SpellChecker.checkEndingDependingOnQuantity(weeks, getString(R.string.week),
                    getString(R.string.week_different_ending), getString(R.string.weeks)), remainingDays, SpellChecker.checkEndingDependingOnQuantity(remainingDays, getString(R.string.day),
                    getString(R.string.day_different_ending), getString(R.string.days)));
            days.setTextSize(20);
        }

        return daysString;
    }

    private String phraseShow(long days, String phrase) {
        StringBuilder text = new StringBuilder();
        if (counterController.getDaysShowMode() == 1)
            text.append(SpellChecker.checkEndingDependingOnQuantity(Math.abs(days), getString(R.string.day), getString(R.string.day_different_ending), getString(R.string.days))).append(" ");
        text.append(phrase);
        if (days < 0) {
            if (!phrase.isEmpty())
                text.append(" ");
            text.append(getString(R.string.days_left));
        }
        return text.toString();
    }
}