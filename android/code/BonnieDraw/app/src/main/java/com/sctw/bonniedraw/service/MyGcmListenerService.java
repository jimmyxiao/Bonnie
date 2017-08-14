package com.sctw.bonniedraw.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.sctw.bonniedraw.Logger;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.MainActivity;

public class MyGcmListenerService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Logger.d(data.toString());
        if (!data.isEmpty()) {
            if (getString(R.string.gcm_sender_id).equals(from)) {
                try {
                    sendNotification(data.getString("message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendNotification(String message) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).
                        setContentText(message).
                        setContentTitle(getString(R.string.app_name)).
                        setPriority(NotificationCompat.PRIORITY_DEFAULT).
                        setSmallIcon(R.mipmap.ic_launcher).
                        setStyle(new NotificationCompat.BigTextStyle().bigText(message)).
                        setTicker(getString(R.string.app_name));
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}