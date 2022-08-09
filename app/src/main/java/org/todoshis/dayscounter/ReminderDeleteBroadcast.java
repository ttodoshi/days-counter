package org.todoshis.dayscounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.core.app.NotificationCompat;

public class ReminderDeleteBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("ID", 0);
        CounterDatabaseHelper db = new CounterDatabaseHelper(context);
        if (db.counterExists(id)){
            db.deleteById(id);
            NotificationUtils notificationUtils = new NotificationUtils(context);
            NotificationCompat.Builder builder = notificationUtils.setNotification(context.getString(R.string.was_deleted), context.getString(R.string.notification_today));
            notificationUtils.getManager().notify(101, builder.build());
        }
    }
}