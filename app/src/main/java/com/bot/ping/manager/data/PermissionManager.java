package com.bot.ping.manager.data;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.app.ActivityCompat.requestPermissions;
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
import androidx.core.content.ContextCompat;

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
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.POST_NOTIFICATIONS)) {
        } else {
            // You can directly ask for the permission.
            requestPermissions(activity,
                    new String[] { Manifest.permission.POST_NOTIFICATIONS },
                    POST_NOTIFICATIONS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(activity,new String[]{Manifest.permission.POST_NOTIFICATIONS},POST_NOTIFICATIONS);
            }
        }
        requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE);
    }
}