package com.example.gaosach.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.gaosach.R;

import androidx.annotation.RequiresApi;

public class NotificationHelper extends ContextWrapper {

    private static final String GAOVIET_CHANEL_ID="com.example.gaosach";
    private static final String GAOVIET_CHANEL_NAME="GẠO VIỆT";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O); //chi chay tren api 26 or cao hon
        createChanel();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChanel() {
        NotificationChannel gaovietChanel= new NotificationChannel(GAOVIET_CHANEL_ID,
                GAOVIET_CHANEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        gaovietChanel.enableLights(false);
        gaovietChanel.enableVibration(true);
        gaovietChanel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(gaovietChanel);


    }

    public NotificationManager getManager() {
        if(manager== null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder getGaoVietChannelNotification(String title, String body, PendingIntent contenIntent,

                                                                          Uri soundUri)
    {
        return new android.app.Notification.Builder(getApplicationContext(),GAOVIET_CHANEL_ID)
                .setContentIntent(contenIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

}
