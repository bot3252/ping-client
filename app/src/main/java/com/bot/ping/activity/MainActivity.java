package com.bot.ping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.manager.DataManager;
import com.bot.ping.model.User;
import com.bot.ping.ui.UiManagerActivity;

public class MainActivity extends AppCompatActivity {
    UiManagerActivity uiManagerActivity;
    DataManager dataManager;
    User myUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager(this);
        myUser=dataManager.getUser();
        Bundle arguments = getIntent().getExtras();
        if (arguments!=null){
            myUser.setUuid(arguments.get("uuid").toString());
            myUser.setEmail(arguments.get("email").toString());
            myUser.setName(arguments.get("name").toString());
        }
        else {
            if (myUser.getUuid() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }

        uiManagerActivity=new UiManagerActivity(this,myUser);
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