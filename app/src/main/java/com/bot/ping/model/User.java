package com.bot.ping.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
public class User implements Parcelable {
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

    protected User(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        email = in.readString();
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        password = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeParcelable(avatar, i);
        parcel.writeString(password);
    }
}
