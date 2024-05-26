package com.bot.ping.DataBase;

import android.graphics.BitmapFactory;

import com.bot.ping.manager.DataManager;
import com.bot.ping.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
            User user = new User(response.getString("uuid"), response.getString("email"), response.getString("name"));
            String password = response.getString("password");
            if(password!="null"){
                user.setPassword(password);
            }
            return user;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<User> parseUsers(JSONArray jsonArray){
        ArrayList<User> users = new ArrayList<User>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject element = (JSONObject) jsonArray.get(i);
                users.add(parseUser(element.toString()));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return users;
    }
}
