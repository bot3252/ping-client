package com.bot.ping.manager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.bot.ping.model.User;

public class DataManager{
    Context context;
    SharedPreferences sharedPreferencesUserData;
    public DataManager(Context context){
        this.context = context;
        sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
    }

    public void saveUser(User user) {
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        prefEditor.putString("name", user.getName());
        prefEditor.putString("email", user.getEmail());
        prefEditor.putString("uuid", user.getUuid());
        prefEditor.apply();
    }

    public User getUser() {
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        User user = new User();
        user.setName(sharedPreferencesUserData.getString("name",null));
        user.setEmail(sharedPreferencesUserData.getString("email",null));
        user.setUuid(sharedPreferencesUserData.getString("uuid",null));
        return user;
    }

    public void deleteData(){
        SharedPreferences settings = context.getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }
}
