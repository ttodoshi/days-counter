package org.todoshis.dayscounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CounterController {
    private static CounterController counterController = null;
    private static Counter counter;
    private static Context context;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private CounterController(Context applicationContext) {
        context = applicationContext;
        counter = returnCurrentCounter(context);
    }
    public static CounterController getInstance(Context context) {
        if (counterController == null) {
            counterController = new CounterController(context);
        }
        return counterController;
    }

    public static Counter returnCurrentCounter(Context context) {
        return Counter.returnCurrentCounter(context);
    }

    public static boolean haveCounters() {
        return Counter.haveCounters();
    }

    public static void addCounter() {
        if (Counter.addCounter(sdf.format(new Date()))) {
            Toast.makeText(context, context.getString(R.string.new_counter), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.counter_didnt_add), Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteLastCounter() {
        int resultStatus = Counter.deleteLastCounter();
        switch (resultStatus) {
            case -1:
                Toast.makeText(context, context.getString(R.string.last_counter_was_deleted), Toast.LENGTH_SHORT).show();
                return;
            case 1:
                Toast.makeText(context, context.getString(R.string.last_counter_message), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, context.getString(R.string.no_counters), Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public static boolean next() {
        return Counter.next();
    }
    public static boolean previous() {
        return Counter.previous();
    }
    public static Date getDate() {
        try {
            return sdf.parse(counter.getDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setDate(String date) {
        counter.setDate(date);
    }
    public static void setDate(Date date) {
        counter.setDate(sdf.format(date));
    }
    public static int getDaysShowMode() {
        return counter.getDaysShowMode();
    }
    public static void setDaysShowMode() {
        counter.setDaysShowMode();
    }
    public static String getPhrase() {
        return counter.getDate();
    }
    public static void setPhrase(String newPhrase) {
        counter.setPhrase(newPhrase);
    }
}
