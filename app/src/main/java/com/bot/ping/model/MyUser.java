package com.bot.ping.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
public class MyUser implements Parcelable {
    private String uuid;
    private String name;
    private String email;
    private Bitmap avatar;
    private String password;
    private String nickname;
    private String description;
    private String firebaseToken;
    public MyUser(){
    }
    public MyUser(String uuid, String email, String name, String nickName, String description){
        this.setEmail(email);
        this.setUuid(uuid);
        this.setName(name);
        this.setNickname(nickName);
        this.setDescription(description);
    }

    protected MyUser(Parcel in) {
        uuid = in.readString();
        email = in.readString();
        name = in.readString();
        nickname = in.readString();
        description = in.readString();
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        password = in.readString();
    }

    public static final Creator<MyUser> CREATOR = new Creator<MyUser>() {
        @Override
        public MyUser createFromParcel(Parcel in) {
            return new MyUser(in);
        }

        @Override
        public MyUser[] newArray(int size) {
            return new MyUser[size];
        }
    };

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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFirebaseToken(){
        return firebaseToken;
    }
    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(nickname);
        parcel.writeString(description);
        parcel.writeParcelable(avatar, i);
        parcel.writeString(password);
    }
}
