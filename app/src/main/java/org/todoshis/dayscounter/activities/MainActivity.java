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

import org.todoshis.dayscounter.controllers.CounterController;
import org.todoshis.dayscounter.activities.gestures.OnSwipeTouchListener;
import org.todoshis.dayscounter.R;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class MainActivity extends AppCompatActivity {
    CounterController counterController;
    ShowMode showMode;
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
            showElements();
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

    private void showElements() {
        if (counterController.getDaysShowMode() == 1)
            showMode = new ShowModeOnlyDays();
        else
            showMode = new ShowModeWithYearsAndMonths();
    }

    private void reloadMainPage() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    abstract class ShowMode {
        protected long pastDays = ChronoUnit.DAYS.between(LocalDate.from(counterController.getDate()), LocalDate.now());

        public ShowMode() {
            show();
        }

        protected void show() {
            showDays();
            showPicture();
            showPhrase();
        }

        abstract void showDays();

        abstract void showPicture();

        protected void showPhrase() {
            String phrase = counterController.getPhrase();
            if (pastDays < 0)
                showFutureDatePhraseView(phrase);
            daysPhrase.append(phrase);
        }

        private void showFutureDatePhraseView(String phrase) {
            daysPhrase.append(getString(R.string.days_left));
            if (!phrase.isEmpty()) daysPhrase.append(" ");
        }
    }

    class ShowModeOnlyDays extends ShowMode {
        @Override
        public void showDays() {
            days.setText(String.valueOf(Math.abs(pastDays)));
        }

        @Override
        public void showPicture() {
            rectangle.setVisibility(INVISIBLE);
            round.setVisibility(VISIBLE);
        }

        protected void showPhrase() {
            if (counterController.getDaysShowMode() == 1)
                daysPhrase.append("days ");
            super.showPhrase();
        }
    }

    class ShowModeWithYearsAndMonths extends ShowMode {
        @SuppressLint("DefaultLocale")
        @Override
        public void showDays() {
            Period pastTime = Period.between(LocalDate.from(counterController.getDate()), LocalDate.now());
            days.setTextSize(20);
            days.setText(String.format("%d years, %d months, %d days", Math.abs(pastTime.getYears()), Math.abs(pastTime.getMonths()), Math.abs(pastTime.getDays())));
        }

        @Override
        public void showPicture() {
            rectangle.setVisibility(VISIBLE);
            round.setVisibility(INVISIBLE);
        }
    }
}