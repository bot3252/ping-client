package com.bot.ping.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.bot.ping.R;
import com.bot.ping.manager.DataManager;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.manager.PermissionManager;
import com.bot.ping.model.User;
import com.bot.ping.ui.UIManagerActivity;
import com.bot.ping.ui.create_group.CreateGroupFragment;
import com.bot.ping.ui.message.MessageMenuFragment;
import com.bot.ping.ui.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    UIManagerActivity uiManagerActivity;
    DataManager dataManager;
    User myUser;
    Context context;
    Bundle arguments;
    DownloadManager downloadManager;
    ArrayList<User> allContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        checkLogin();
        uiManagerActivity=new UIManagerActivity(this,myUser);
        setAvatar();
        checkContacts();
    }

    private void checkContacts() {
        new Thread(new Runnable() {
            public void run() {

                allContacts = downloadManager.downloadAllContacts(myUser.getEmail(), myUser.getPassword());
                if(allContacts!=null){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            uiManagerActivity.setAllContacts(allContacts);
                        }
                    });
                }
            }
        }).start();
    }

    private void checkLogin() {
        if (arguments!=null){
            myUser.setUuid(arguments.get("uuid").toString());
            myUser.setEmail(arguments.get("email").toString());
            myUser.setName(arguments.get("name").toString());
            myUser.setPassword(arguments.get("password").toString());
        }
        else {
            if (myUser.getUuid() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
            }
        }
    }

    void init(){
        context = this;
        dataManager = new DataManager(this);
        myUser=dataManager.getUser();
        arguments = getIntent().getExtras();
        downloadManager = new DownloadManager(context);
        new PermissionManager(this);
        getSupportFragmentManager().findFragmentById(R.id.nav_message_menu);
    }

    void setAvatar(){
        new Thread(new Runnable() {
            public void run() {
                Bitmap avatar=null;
                try {
                    avatar = dataManager.getImage("avatar.jpeg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (avatar==null){
                    avatar = downloadManager.downloadAvatar(myUser.getUuid());
                    dataManager.saveImage(avatar, "avatar.jpeg");
                }
                //                    dataManager.getImage();
                myUser.setAvatar(avatar);
                if(avatar!=null){
                    Bitmap finalAvatar = avatar;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            uiManagerActivity.setNavAvatar(finalAvatar);
                        }
                    });
                }
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
        dataManager.deleteData();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}