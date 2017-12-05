package com.sctw.bonniedraw.utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sctw.bonniedraw.activity.MainActivity;

/**
 * Created by Fatorin on 2017/12/1.
 */

public class NotificaitonMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "onMessageReceived:" + remoteMessage.getFrom());
    }

    private void sendNotification(String body) {
        NotificationUtil mNotificationUtil = new NotificationUtil(getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder nb = mNotificationUtil.
                getAndroidChannelNotification("Bonnidraw", body);

        mNotificationUtil.getManager().notify(101, nb.build());
    }
}