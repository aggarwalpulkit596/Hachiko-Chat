package me.dats.com.datsme.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.dats.com.datsme.Activities.ChatActivity;
import me.dats.com.datsme.Activities.Others_profile;
import me.dats.com.datsme.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle, notificationBody, user_id, username;
        ;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody() + remoteMessage.getData());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
            user_id = remoteMessage.getData().get("from_user_id");
            username = remoteMessage.getData().get("userName");
            if (username != null) {
                sendNotificationChat(notificationTitle, notificationBody, user_id, username);
            } else {
                sendNotification(notificationTitle, notificationBody, user_id, username);
            }

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    private void sendNotificationChat(String notificationTitle, String notificationBody, String notficationData, String notficationData2) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user_id", notficationData);
        intent.putExtra("name", notficationData2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "testchannelid")
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendNotification(String notificationTitle, String notificationBody, String notficationData, String username) {
        Intent intent = new Intent(this, Others_profile.class);
        intent.putExtra("user_id", notficationData);
        intent.putExtra("name", username);
        Log.i("Notification", "PagerViewAdapter: " + notficationData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "testchannelid")
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}