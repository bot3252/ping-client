package com.bot.ping.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
public class User {
    private String uuid;
    private String name;
    private String email;
    private Bitmap avatar;
    private String password;
    public User(){
    }
    public User(String uuid, String email, String name){
        this.setEmail(email);
        this.setUuid(uuid);
        this.setName(name);
    }
    @NonNull
    public String getUuid() { return uuid;}
    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAvatar(Bitmap avatar) { this.avatar = avatar;}
    public Bitmap getAvatar() { return avatar; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
