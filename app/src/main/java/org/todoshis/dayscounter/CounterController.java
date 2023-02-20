package org.todoshis.dayscounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CounterController {
    @SuppressLint("StaticFieldLeak")
    private static CounterController counterController = null;
    private final CounterDatabaseHelper db;
    private final Context context;
    private Counter currentCounter;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
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
        if (db.addCounter(sdf.format(new Date()), 1, "")) {
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

    public Date getDate() {
        try {
            return sdf.parse(currentCounter.getDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDate(String date) {
        currentCounter.setDate(date);
        db.editCounter(date, currentCounter.getDaysShowMode(), currentCounter.getPhrase());
    }

    public void setDate(Date date) {
        String dateToString = sdf.format(date);
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
        switch (currentCounter.setPhrase(newPhrase)) {
            case 0:
                Toast.makeText(context, context.getString(R.string.invalid_format), Toast.LENGTH_SHORT).show();
                break;
            case 100:
                Toast.makeText(context, context.getString(R.string.too_long), Toast.LENGTH_SHORT).show();
                break;
            default:
                db.editCounter(currentCounter.getDate(), currentCounter.getDaysShowMode(), newPhrase);
        }
    }
}
