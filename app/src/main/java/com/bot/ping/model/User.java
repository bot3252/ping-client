package com.bot.ping.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
public class User{
    private String uuid;
    private String name;
    private String email;
    private Bitmap avatar;
    private String nickname;
    private String description;
    public User(){
    }
    public User(String uuid, String email, String name, String nickName, String description){
        this.setEmail(email);
        this.setUuid(uuid);
        this.setName(name);
        this.setNickname(nickName);
        this.setDescription(description);
    }


    @NonNull
    public String getUuid() { return uuid;}
    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
    public String getDescription(){
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
