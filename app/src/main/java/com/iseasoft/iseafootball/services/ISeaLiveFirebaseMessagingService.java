package com.iseasoft.iseafootball.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iseasoft.iseafootball.ISeaLiveConstants;
import com.iseasoft.iseafootball.R;
import com.iseasoft.iseafootball.SplashActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ISeaLiveFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        try {
            Map<String, String> data = remoteMessage.getData();
            Intent intent = new Intent(this, SplashActivity.class);

            String url = data.get(ISeaLiveConstants.PUSH_URL_KEY);
            String message = remoteMessage.getNotification().getBody();

            if (!TextUtils.isEmpty(url)) {
                intent.putExtra(ISeaLiveConstants.PUSH_URL_KEY, url);
                intent.putExtra(ISeaLiveConstants.PUSH_MESSAGE, message);
            }
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            String title = getString(R.string.app_name);
            if (!TextUtils.isEmpty(remoteMessage.getNotification().getTitle())) {
                title = remoteMessage.getNotification().getTitle();
            }

            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_push_notification))
                    .setSmallIcon(R.drawable.ic_push_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true).build();

            Date now = new Date();
            int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.notify(id, notification);
        } catch (Exception e) {

        }
    }
}
