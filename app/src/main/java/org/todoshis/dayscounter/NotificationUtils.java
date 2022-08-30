package org.todoshis.dayscounter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils extends ContextWrapper
{

    private NotificationManager notificationManager;
    private final Context context;
    private final String CHANNEL_ID = "DaysCounterChannel";

    public NotificationUtils(Context base) {
        super(base);
        context = base;
        createChannel();
    }

    public NotificationCompat.Builder setNotification(String title, String body) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent);
    }

    private void createChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String TIMELINE_CHANNEL_NAME = "Notifications About Counters";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TIMELINE_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if(notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public void setReminder(int id, long timeInMillis) {
        Intent intent = new Intent(context, ReminderBroadcast.class);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }
    public void setDelete(int id, long timeInMillis){
        Intent intent = new Intent(context, ReminderDeleteBroadcast.class);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }
}