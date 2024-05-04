package com.bot.ping.model;

import androidx.annotation.NonNull;
public class User {
    private String uuid;
    private String name;
    private String email;

    public User(){
    }
    public User(String uuid, String email, String name){
        this.setEmail(email);
        this.setUuid(uuid);
        this.setName(name);
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

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
}
