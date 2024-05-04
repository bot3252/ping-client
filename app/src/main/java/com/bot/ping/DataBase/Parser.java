package com.bot.ping.DataBase;

import com.bot.ping.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class Parser{
    public static User parseUser(String responseString){
        JSONObject response = null;
        try {
            response = new JSONObject(responseString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            return new User(response.getString("uuid"), response.getString("email"), response.getString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
