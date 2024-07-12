package com.bot.ping.DataBase;

import com.bot.ping.model.Message;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Parser{
    public static User parseUser(String responseString){
        JSONObject response = null;
        try {
            response = new JSONObject(responseString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            User user = new User(response.getString("uuid"), response.getString("email"), response.getString("name"), response.getString("nickname"), response.getString("description"));
            return user;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public static MyUser parseMyUser(String responseString){
        JSONObject response = null;
        try {
            response = new JSONObject(responseString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            MyUser myUser = new MyUser(response.getString("uuid"), response.getString("email"), response.getString("name"), response.getString("nickname"), response.getString("description"));
            String password = response.getString("password");
            if(password!="null"){
                myUser.setPassword(password);
            }
            return myUser;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public static Message parseMessage(String responseString, MyUser myUser){
        JSONObject response = null;
        try {
            response = new JSONObject(responseString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            Instant instant = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                instant = Instant.from(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                .withZone(ZoneId.of("UTC"))
                                .parse(response.getString("date")));
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String dateString = dateFormat.format(Date.from(instant));

                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String timeString = timeFormat.format(Date.from(instant));

                Message message = new Message(response.getString("uuid"), response.getString("from"), response.getString("message"), timeString, dateString);
                if (Objects.equals(myUser.getUuid(), message.getFrom())){
                    message.setMsgType(Message.MSG_TYPE_SENT);
                }else {
                    message.setMsgType(Message.MSG_TYPE_RECEIVED);
                }
                return message;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
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

    public static ArrayList<Message> parseMessages(JSONArray jsonArray, MyUser myUser) {
        ArrayList<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject element = (JSONObject) jsonArray.get(i);
                messages.add(parseMessage(element.toString(), myUser));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }
}
