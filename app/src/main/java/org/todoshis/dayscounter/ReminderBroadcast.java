package org.todoshis.dayscounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("ID", 0);
        CounterDatabaseHelper db = new CounterDatabaseHelper(context);
        if (db.counterExists(id)) {
            NotificationUtils notificationUtils = new NotificationUtils(context);
            NotificationCompat.Builder builder = notificationUtils.setNotification(context.getString(R.string.reminder), context.getString(R.string.notification_tomorrow));
            notificationUtils.getManager().notify(101, builder.build());
        }
    }
}