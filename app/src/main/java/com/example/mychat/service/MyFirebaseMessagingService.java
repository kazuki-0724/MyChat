package com.example.mychat.service;

import static com.example.mychat.constant.CommonConst.FROM_OTHERS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.mychat.MainActivity;
import com.example.mychat.R;
import com.example.mychat.db.AppDatabase;
import com.example.mychat.db.AppDatabaseSingleton;
import com.example.mychat.task.DataStoreInsertAsyncTask;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if(message.getData() != null){
            Map<String, String> data = message.getData();
            String title = data.get("title");
            String body = data.get("text");
            Log.i("MyChat", "title: " + title + ", body: " + body);
            showNotification(title, body);
        }
    }

    /**
     * 通知を表示する
     * @param title
     * @param body
     */
    private void showNotification(String title, String body) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // バージョンによりチャンネルを変える
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "FCM Notifications";
            String channelId = "fcm_channel";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }


        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "fcm_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());

        // 処理をブロードキャストする
        Intent broadcast = new Intent();
        broadcast.putExtra("message", body);
        broadcast.setAction("DO_ACTION");
        getBaseContext().sendBroadcast(broadcast);

        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        new DataStoreInsertAsyncTask(body, FROM_OTHERS, db).execute();
        
    }

    // FCMトークンの更新を受け取る
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM", "Refreshed token: " + token);

        // トークンが新しくなったらSharedPreferencesに保存する
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myChat_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("myToken",token);
        editor.commit();
    }
}
