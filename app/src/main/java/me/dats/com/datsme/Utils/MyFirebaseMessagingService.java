package me.dats.com.datsme.Utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.dats.com.datsme.Activities.ChatActivity;
import me.dats.com.datsme.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";
    private static final String actionLiked = "liked";
    private static final int NOTIFICATION_ID = 1593;
    private final String GROUP_KEY = "GROUP_KEY_RANDOM_NAME";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle, notificationBody, user_id, username;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody() + remoteMessage.getData());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
            user_id = remoteMessage.getData().get("from_user_id");
            username = remoteMessage.getData().get("userName");
            Intent intent = new Intent(this, ChatActivity.class);
            if (notificationTitle.equals("New Message"))
                sendNotification(notificationTitle, notificationBody, user_id, username, intent);
            else
                sendNotification(notificationTitle, notificationBody, user_id, username, intent);

        }

    }


    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
//
//    private void sendNotificationChat(String notificationTitle, String notificationBody, String notficationData, String notficationData2) {
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.putExtra("from_user_id", notficationData);
//        intent.putExtra("userName", notficationData2);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)   //Automatically delete the notification
//                .setSmallIcon(R.drawable.logo) //Notification icon
//                .setContentIntent(pendingIntent)
//                .setBadgeIconType(R.drawable.logo)
//                .setContentTitle(notificationTitle)
//                .setContentText(notificationBody)
//                .setSound(defaultSoundUri);
//
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//
//
//    private void sendNotification(String notificationTitle, String notificationBody, String notficationData, String notficationData2) {
//        Intent intent = new Intent(this, Others_profile.class);
//        intent.putExtra("from_user_id", notficationData);
//        intent.putExtra("userName", notficationData2);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)   //Automatically delete the notification
//                .setSmallIcon(R.drawable.logo) //Notification icon
//                .setContentIntent(pendingIntent)
//                .setBadgeIconType(R.drawable.logo)
//                .setContentTitle(notificationTitle)
//                .setContentText(notificationBody)
//                .setSound(defaultSoundUri);
//
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }

    @TargetApi(Build.VERSION_CODES.M)
    private void sendNotification(String notificationTitle, String messageBody, String notficationData, String notficationData2, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Intent onCancelNotificationReceiver = new Intent(this, CancelNotificationReceiver.class);
        PendingIntent onCancelNotificationReceiverPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0,
                onCancelNotificationReceiver, 0);
        String notificationHeader = this.getResources().getString(R.string.app_name);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = manager.getActiveNotifications();
        for (int i = 0; i < notifications.length; i++) {
            if (notifications[i].getPackageName().equals(getApplicationContext().getPackageName())) {
                Log.d("Notification", notifications[i].toString());
                Intent startNotificationActivity = new Intent(this, ChatActivity.class);
                startNotificationActivity.putExtra("from_user_id", notficationData);
                startNotificationActivity.putExtra("userName", notficationData2);
                startNotificationActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startNotificationActivity,
                        PendingIntent.FLAG_ONE_SHOT);
                Notification notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                        .setContentTitle(notificationHeader)
                        .setContentText("Tap to open")
                        .setAutoCancel(true)
                        .setStyle(getStyleForNotification(messageBody))
                        .setGroupSummary(true)
                        .setGroup(GROUP_KEY)
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(onCancelNotificationReceiverPendingIntent)
                        .build();
                SharedPreferences sharedPreferences = getSharedPreferences("NotificationData", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(String.valueOf(new Random(NOTIFICATION_ID)), messageBody);
                editor.apply();
                notificationManager.notify(NOTIFICATION_ID, notification);
                return;
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Notification notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setContentTitle(notificationHeader)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(onCancelNotificationReceiverPendingIntent)
                .build();
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationData", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(new Random(NOTIFICATION_ID)), messageBody);
        editor.apply();
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder);
    }

    private NotificationCompat.InboxStyle getStyleForNotification(String messageBody) {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        SharedPreferences sharedPref = getSharedPreferences("NotificationData", 0);
        Map<String, String> notificationMessages = (Map<String, String>) sharedPref.getAll();
        Map<String, String> myNewHashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : notificationMessages.entrySet()) {
            myNewHashMap.put(entry.getKey(), entry.getValue());
        }
        inbox.addLine(messageBody);
        for (Map.Entry<String, String> message : myNewHashMap.entrySet()) {
            inbox.addLine(message.getValue());
        }
        inbox.setBigContentTitle(this.getResources().getString(R.string.app_name))
                .setSummaryText("Tap to open");
        return inbox;
    }
}
