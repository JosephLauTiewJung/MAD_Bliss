package com.example.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.bliss.R;
import com.example.main.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "bliss_notifications";
    public static final String CHANNEL_NAME = "Bliss Reminders";
    public static final String EXTRA_NOTIFICATION_TYPE = "notification_type";
    public static final String TYPE_MOOD = "mood";
    public static final String TYPE_JOURNAL = "journal";

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE);
        if (type == null) return;

        createNotificationChannel(context);

        String title = "";
        String message = "";
        int notificationId = 0;
        String navigateTo = "";

        if (TYPE_MOOD.equals(type)) {
            title = "Daily Mood Check-in";
            message = "How are you feeling today? Take a moment to check in.";
            notificationId = 1001;
            navigateTo = "mood";
        } else if (TYPE_JOURNAL.equals(type)) {
            title = "Journaling Time";
            message = "What was the highlight of your day? Write it down.";
            notificationId = 1002;
            navigateTo = "journal";
        }

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.putExtra("NAVIGATE_TO", navigateTo);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bliss_logo) // Changed to bliss_logo.png
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Daily reminders for mood tracking and journaling");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
