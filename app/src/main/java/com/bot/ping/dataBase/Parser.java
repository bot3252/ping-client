package com.bot.ping.dataBase;

import com.bot.ping.model.Message;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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
            User user = new User(response.getString("uuid"), response.getString("name"), response.getString("nickname"), response.getString("description"));
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
    public static Message parseMessage(String responseString, String myUuid){
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateString = dateFormat.format(Date.from(instant));

                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String timeString = timeFormat.format(Date.from(instant));

                String msgContent=response.getString("message")
                        .replace("/n","\n");

                Message message = new Message(response.getString("uuid"), response.getString("from"), response.getString("to"), msgContent, timeString, dateString);
                if (Objects.equals(myUuid, message.getFrom())){
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

    public static ArrayList<Message> parseMessages(JSONArray jsonArray, String myUuid) {
        ArrayList<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject element = (JSONObject) jsonArray.get(i);
                messages.add(parseMessage(element.toString(), myUuid));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }

    public static ArrayList<User> addStatuses(ArrayList<User> allContacts, ArrayList<String> onlineUsers, String uuid){
        for (int i = 0; i <= allContacts.size() - 1; i++) {
            for (int b = 0; b <= onlineUsers.size() - 1; b++) {
                if (onlineUsers.get(b).equals(allContacts.get(i).getUuid())) {
                    allContacts.get(i).setStatus(User.TYPE_STATUS_ONLINE);
                }else {
                    allContacts.get(i).setStatus(User.TYPE_STATUS_OFFLINE);
                    if(allContacts.get(i).getUuid().equals(uuid)){
                        allContacts.get(i).setStatus(MyUser.TYPE_STATUS_ONLINE);
                    }
                }
            }
        }
        return allContacts;
    }
}
