package com.bot.ping.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bot.ping.activity.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.PropertyPermission;

public class PermissionManager {
    Activity activity;

    private static int REQUEST_CODE = 100;
    public PermissionManager(Activity activity){
        this.activity = activity;
        askPermission();
    }
    private void askPermission() {

        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
    }

}
