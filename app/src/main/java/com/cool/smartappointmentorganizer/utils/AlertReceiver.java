package com.cool.smartappointmentorganizer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        NotificationHelper notificationHelper;
        notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notificationCompat = notificationHelper.getNotificationChannel(title, message);
        notificationHelper.getManager().notify(1, notificationCompat.build());
    }
}
