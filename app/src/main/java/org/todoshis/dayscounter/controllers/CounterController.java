package org.todoshis.dayscounter.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import org.todoshis.dayscounter.models.Counter;
import org.todoshis.dayscounter.db_helpers.CounterDatabaseHelper;
import org.todoshis.dayscounter.R;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class CounterController {
    @SuppressLint("StaticFieldLeak")
    private static CounterController counterController = null;
    private final CounterDatabaseHelper db;
    private final Context context;
    private Counter currentCounter;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private CounterController(Context context) {
        this.context = context;
        db = CounterDatabaseHelper.getInstance(context);
        updateCurrentCounter();
    }

    public static CounterController getInstance(Context context) {
        if (counterController == null)
            counterController = new CounterController(context);
        return counterController;
    }

    public void updateCurrentCounter() {
        currentCounter = db.returnCurrentCounter();
    }

    public boolean haveCounters() {
        return !db.isEmpty();
    }

    public void addCounter() {
        boolean isFirstCounterCreated = db.isEmpty();
        if (db.addCounter(dtf.format(LocalDate.now()), 1, "")) {
            if (isFirstCounterCreated) {
                updateCurrentCounter();
            }
            Toast.makeText(context, context.getString(R.string.new_counter), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.counter_didnt_add), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteLastCounter() {
        int resultStatus = db.deleteLastCounter();
        switch (resultStatus) {
            case -1:
                Toast.makeText(context, context.getString(R.string.last_counter_was_deleted), Toast.LENGTH_SHORT).show();
                updateCurrentCounter();
                return;
            case 1:
                Toast.makeText(context, context.getString(R.string.last_counter_message), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, context.getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public boolean next() {
        boolean res = db.changeCurrent(1);
        updateCurrentCounter();
        return res;
    }

    public boolean previous() {
        boolean res = db.changeCurrent(-1);
        updateCurrentCounter();
        return res;
    }

    public TemporalAccessor getDate() {
        return dtf.parse(currentCounter.getDate());
    }

    public void setDate(String date) {
        try {
            setDate(dtf.parse(date));
        } catch (DateTimeException e) {
            Toast.makeText(context, context.getString(R.string.invalid_date), Toast.LENGTH_SHORT).show();
        }
    }

    public void setDate(TemporalAccessor date) {
        String dateToString = dtf.format(date);
        currentCounter.setDate(dateToString);
        db.editCounter(dateToString, currentCounter.getDaysShowMode(), currentCounter.getPhrase());
    }

    public int getDaysShowMode() {
        return currentCounter.getDaysShowMode();
    }

    public void setDaysShowMode() {
        currentCounter.setDaysShowMode();
        db.editCounter(currentCounter.getDate(), currentCounter.getDaysShowMode(), currentCounter.getPhrase());
    }

    public String getPhrase() {
        return currentCounter.getPhrase();
    }

    public void setPhrase(String newPhrase) {
        if (currentCounter.setPhrase(newPhrase))
            db.editCounter(currentCounter.getDate(), currentCounter.getDaysShowMode(), newPhrase);
        else
            Toast.makeText(context, context.getString(R.string.invalid_phrase), Toast.LENGTH_SHORT).show();
    }
}
