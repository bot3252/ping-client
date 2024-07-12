package com.bot.ping.manager;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.content.ContextCompat.getSystemService;

import static org.chromium.base.ContextUtils.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.bot.ping.R;

public class PermissionManager {
    Activity activity;

    private static int WRITE_EXTERNAL_STORAGE = 100;
    private static int POST_NOTIFICATIONS = 101;
    public PermissionManager(Activity activity){
        this.activity = activity;
        askPermission();
    }
    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.POST_NOTIFICATIONS},POST_NOTIFICATIONS);
            }
        }
        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE);
    }

    public static void sendNotification(){
//        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notify = new Notification.Builder
//                (getApplicationContext()).setContentTitle(tittle).setContentText(body).
//                setContentTitle(subject).setSmallIcon(R.drawable.ps).build();
//
//        notify.flags |= Notification.FLAG_AUTO_CANCEL;
//        notif.notify(0, notify);
    }

}