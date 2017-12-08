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
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d("FCM", "Message Notification Body: " + remoteMessage.getNotification().getTitle());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String title, String body) {
        NotificationUtil mNotificationUtil = new NotificationUtil(getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);
        //event 1= 留言 ， 2 = 點讚
        intent.putExtra("evnet","msg");
        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder nb = mNotificationUtil.
                getAndroidChannelNotification(title, body);
        nb.setContentIntent(pendingIntent);

        mNotificationUtil.getManager().notify(101, nb.build());
    }
}