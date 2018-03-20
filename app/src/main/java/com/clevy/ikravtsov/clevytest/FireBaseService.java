package com.clevy.ikravtsov.clevytest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ikravtsov on 20/03/2018.
 */

public class FireBaseService extends FirebaseMessagingService {

    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
    }

    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, EducationActivity.class);
        intent.putExtra("title",  messageTitle);
        intent.putExtra("body",  messageBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.income_message);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.clevy_logo_2);

        Notification noti = new Notification.Builder(this)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.clevy_logo_on_push)
                .setLargeIcon(largeIcon)
                .setSound(sound)
                .setContentIntent(pendingIntent).getNotification();

        noti.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, noti);
        }
        else {
            Log.println(Log.ERROR, "null", "can not push notify");
        }
    }

}
