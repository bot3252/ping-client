package com.bot.ping.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bot.ping.DataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.manager.DataManager;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.manager.PermissionManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.ui.DialogLoading;
import com.bot.ping.ui.UIManagerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    UIManagerActivity uiManagerActivity;
    DataManager dataManager;
    MyUser myUser;
    Context context;
    Bundle arguments;
    DownloadManager downloadManager;
    ArrayList<User> allContacts;
    DataBase dataBase;
    DialogLoading dialogLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        if (arguments!=null){
            myUser = (MyUser) arguments.get("myUser");
        }
        else {
            myUser = dataManager.getMyUser();
            if (myUser.getUuid()==null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }else {
                dataBase = new DataBase(this, myUser.getUuid());
                uiManagerActivity = new UIManagerActivity(this, myUser);
                setAvatar();
                new Thread(new Runnable() {
                    public void run() {
                        checkContacts();
                        try {
                            checkUpdate();
                        } catch (PackageManager.NameNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        }
        dialogLoading.closeDialog();
    }

    private void checkUpdate() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        String version = pInfo.versionName;
        if(downloadManager.checkUpdate(version)){
            Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
            startActivity(intent);
        }
    }

    private void checkContacts() {
        new Thread(new Runnable() {
            public void run() {
                allContacts=dataBase.getContacts();
                if(allContacts.isEmpty()){
                    allContacts = downloadManager.downloadAllContacts(myUser.getUuid(), myUser.getPassword());
                    if(!(allContacts == null)){
                        dataBase.saveContacts(allContacts);
                    }
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        uiManagerActivity.setAllContacts(allContacts);
                    }
                });
            }
        }).start();
    }

    void init(){
        dialogLoading = new DialogLoading(this);
        dialogLoading.showDialog();
        context = this;
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
        dataManager = new DataManager(this);
        arguments = getIntent().getExtras();
        downloadManager = new DownloadManager(context);
        new PermissionManager(this);
    }

    void setAvatar(){
        new Thread(new Runnable() {
            public void run() {
                Bitmap avatar=null;
                try {
                    avatar = dataManager.getImage(myUser.getUuid(), context);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (avatar==null){
                    avatar = downloadManager.downloadAvatar(myUser.getUuid());
                    if(avatar==null){
                        avatar= BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.avatar1);
                    }
                }
                Bitmap finalAvatar = avatar;
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        uiManagerActivity.setNavAvatar(finalAvatar);
                    }
                });
                myUser.setAvatar(avatar);
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return uiManagerActivity.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return uiManagerActivity.onSupportNavigateUp();
    }
    public void exit_account(MenuItem item) {
        new Thread(new Runnable() {
            public void run() {
                downloadManager.logout(myUser);
            }
        }).start();
        dataManager.deleteData();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}