package com.example.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class NotificationScheduler {

    public static void scheduleDailyReminders(Context context) {
        scheduleNotification(context, NotificationReceiver.TYPE_MOOD, 20, 00, 1001); // 12:30 PM
        scheduleNotification(context, NotificationReceiver.TYPE_JOURNAL, 21, 0, 1002); // 9:00 PM
    }

    private static void scheduleNotification(Context context, String type, int hour, int minute, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_NOTIFICATION_TYPE, type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the time has already passed today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            // Set repeating alarm
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    public static void cancelAllReminders(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // Cancel mood reminder
        Intent moodIntent = new Intent(context, NotificationReceiver.class);
        moodIntent.putExtra(NotificationReceiver.EXTRA_NOTIFICATION_TYPE, NotificationReceiver.TYPE_MOOD);
        PendingIntent moodPendingIntent = PendingIntent.getBroadcast(
                context, 1001, moodIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Cancel journal reminder  
        Intent journalIntent = new Intent(context, NotificationReceiver.class);
        journalIntent.putExtra(NotificationReceiver.EXTRA_NOTIFICATION_TYPE, NotificationReceiver.TYPE_JOURNAL);
        PendingIntent journalPendingIntent = PendingIntent.getBroadcast(
                context, 1002, journalIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        if (alarmManager != null) {
            alarmManager.cancel(moodPendingIntent);
            alarmManager.cancel(journalPendingIntent);
        }
    }
}
