package com.bot.ping.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class User{
    private String uuid;
    private String name;
    private Bitmap avatar;
    private String nickname;
    private String description;
    private String status;
    public static String TYPE_STATUS_ONLINE = "ONLINE";
    public static String TYPE_STATUS_OFFLINE = "OFFLINE";
    public User(){
    }
    public User(String uuid,String name, String nickName, String description){
        this.setUuid(uuid);
        this.setName(name);
        this.setNickname(nickName);
        this.setDescription(description);
    }


    @NonNull
    public String getUuid() {return uuid;}
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
    public void setAvatar(Bitmap avatar) { this.avatar = avatar;}
    public Bitmap getAvatar() { return avatar; }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public static String getTypeStatusOnline() {
        return TYPE_STATUS_ONLINE;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public JSONObject userToJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", uuid);
        jsonObject.put("name", name);
        jsonObject.put("avatar", convertBitmapToByteArray(avatar));
        jsonObject.put("nickname", nickname);
        jsonObject.put("description", description);
        jsonObject.put("status", status);
        return jsonObject;
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
