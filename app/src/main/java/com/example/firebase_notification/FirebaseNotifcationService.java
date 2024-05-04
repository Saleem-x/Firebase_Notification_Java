package com.example.firebase_notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseNotifcationService extends FirebaseMessagingService {



    static void gettoken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.e("token", token);

                        FirebaseHelper firebaseHelper = new FirebaseHelper();
                        firebaseHelper.uploadString(token);
                    } else {
                        Log.e("token", "getToken() failed", task.getException());
                    }
                });
    }
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }



    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("rmsg", "From: " + remoteMessage.getFrom());
        Context context = getApplicationContext();
        // Check if message contains a data payload.

        Log.e("titleee", String.valueOf(remoteMessage.getNotification()));
        String s = String.valueOf(remoteMessage.getNotification().getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showNotification(s,context);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("notibody", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void showNotification(String s,Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "Java Notification",
                    "Java Notifiction",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        Intent notificationIntent = new Intent(context, SecondFragment.class);


//        notificationIntent.putExtra("notification_action", "navigate_to_second_fragment");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(context, "Java Notification")
                .setContentTitle("Firebase Notification test app")
                .setContentText( s)
                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .build();


        NotificationManager notificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
    }

    public static void sendNotification(String title, String message, String token) {
        Map<String, String> data = new HashMap<>();
        data.put("message", message); // Add any additional data you want to send

        RemoteMessage.Builder builder = new RemoteMessage.Builder(token);
        builder.setData(data);
        builder.notify(); ;

        FirebaseMessagingService firebaseMessagingService = new FirebaseMessagingService();
        firebaseMessagingService.onMessageReceived(builder.build());
    }
}
