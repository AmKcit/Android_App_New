package com.example.android_app_new;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "AQI_ALERTS";
    private static final String CHANNEL_NAME = "Air Quality Alerts";
    private static final int NOTIFICATION_ID = 1001;

    /**
     * Creates notification channel (required for Android 8.0+)
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription("Alerts for unhealthy air quality levels");
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Sends notification for unhealthy AQI levels
     *
     * @param context Application context
     * @param city City name
     * @param aqi AQI value
     * @param status AQI status
     */
    public static void sendAqiAlert(Context context, String city, int aqi, String status) {
        // Create intent to open Dashboard when notification is clicked
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Determine title and message based on AQI level
        String title = "⚠️ Air Quality Alert";
        String message;
        int priority;

        if (aqi > 300) {
            // Hazardous
            title = "☠️ HAZARDOUS Air Quality";
            message = city + " AQI is " + aqi + " (Hazardous)!\nStay indoors and avoid outdoor activities.";
            priority = NotificationCompat.PRIORITY_MAX;
        } else if (aqi > 200) {
            // Very Unhealthy
            title = "💀 Very Unhealthy Air Quality";
            message = city + " AQI is " + aqi + " (Very Unhealthy)!\nAvoid outdoor activities.";
            priority = NotificationCompat.PRIORITY_HIGH;
        } else if (aqi > 150) {
            // Unhealthy
            title = "🔴 Unhealthy Air Quality";
            message = city + " AQI is " + aqi + " (Unhealthy).\nSensitive groups should limit outdoor activities.";
            priority = NotificationCompat.PRIORITY_HIGH;
        } else if (aqi > 100) {
            // Unhealthy for Sensitive Groups
            title = "🟠 Moderate-High Air Quality";
            message = city + " AQI is " + aqi + " (Unhealthy for Sensitive Groups).\nSensitive individuals should be cautious.";
            priority = NotificationCompat.PRIORITY_DEFAULT;
        } else {
            // Good or Moderate - no alert needed
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(priority)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 250, 500})
                .setLights(0xFF0000, 1000, 1000);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Sends a general notification
     */
    public static void sendNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Cancels all notifications
     */
    public static void cancelNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}

