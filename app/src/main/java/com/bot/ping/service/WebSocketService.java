package com.bot.ping.service;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.bot.ping.callbacks.WebSocketServiceCallbacks;
import com.bot.ping.activity.main.ChatActivity;
import com.bot.ping.dataBase.DataBase;
import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.download.Adress;
import com.bot.ping.manager.download.ConnectCheck;
import com.bot.ping.manager.download.ContactManager;
import com.bot.ping.manager.download.UserManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends Service implements Runnable {
    private final IBinder binder = new LocalBinder();
    private WebSocketServiceCallbacks serviceCallbacks;
    OkHttpClient client;
    public  boolean isCreated=false;
    String uuid, password;
    ArrayList<String> usersOnline = new ArrayList<>();
    ArrayList<User> allContacts = new ArrayList<>();
    DataBase dataBase;
    public WebSocket ws;
    UserManager userManger;
    Context context;

    @Override
    public void run() {

    }

    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
//        updateOnlineUser(this);
        return binder;
    }
    public void create(String uuid, String password, Context context, DataBase dataBase, UserManager userManager)  {
        this.uuid = uuid;
        this.password = password;
        this.dataBase = dataBase;
        this.userManger = userManager;
        this.context = context;

        if(ConnectCheck.check(context)) {
            Request request = new Request.Builder().url(Adress.websocket(context)).build();
            ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();
            if (uuid != null || password != null) {
                client = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(3, TimeUnit.HOURS)
                        .writeTimeout(3, TimeUnit.HOURS)
                        .readTimeout(3, TimeUnit.HOURS)
                        .pingInterval(3, TimeUnit.HOURS)
                        .build();

                ChatWebSocketListener listener = new ChatWebSocketListener();
                ws = client.newWebSocket(request, listener);

                client.dispatcher().executorService().shutdown();
                isCreated = true;

            }
        }
    }

    private final class ChatWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uuid", uuid);
                jsonObject.put("password", password);
                jsonObject.put("id_device", DataManager.getIdDevice(context));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            ws.send(jsonObject.toString());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                JSONObject jsonObject = new JSONObject(text.toString());
                String type = jsonObject.getString("type");
                jsonObject.remove("type");

                switch (type){
                    case "status":
                        String status = jsonObject.getString("status");
                        if (status.equals(User.TYPE_STATUS_ONLINE)){
                            usersOnline.add(jsonObject.getString("uuid"));
                        }else {
                            usersOnline.remove(jsonObject.getString("uuid"));
                        }
                        break;
                    case "new_contact":
                        User user = Parser.parseUser(jsonObject.toString());
                        Bitmap bitmap = userManger.downloadAvatar(user.getUuid());
                        user.setAvatar(bitmap);
                        dataBase.saveContact(user);
                        addOnlineUser(user.getUuid());
                        break;
                }
                if(serviceCallbacks!=null)
                    serviceCallbacks.onNewMessage(type, jsonObject.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
             Log.e(LOG_TAG, "OnFailure: " + response + ", " + t);
             isCreated=false;
//             webSocket.cancel();
             client.connectionPool().evictAll();
        }

    }
    public void setCallbacks(WebSocketServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
        updateOnlineUser(this);
    }
    public ArrayList<String> getUsersOnline() {
        return usersOnline;
    }
    public void close(){
//        setCallbacks(null);

        isCreated = false;
        ws.cancel();
    }
    public void setUsersOnline(ArrayList<String> usersOnline) {
        this.usersOnline = usersOnline;
    }
    public void addOnlineUser(String uuid){
        usersOnline.add(uuid);
    }
    public String getStatusUser(String uuid){
        for(int i = 0; i <= usersOnline.size() - 1; i++){
            if(usersOnline.get(i).equals(uuid)){
                return User.TYPE_STATUS_ONLINE;
            }
        }

        return User.TYPE_STATUS_OFFLINE;
    }
    public ArrayList<User> getAllContacts(){
        return allContacts;
    }
    public void updateOnlineUser(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    allContacts=ContactManager.downloadAllContacts(uuid, password, client);
                    usersOnline=UserManager.getOnlineContacts(uuid, password, client);


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("state", "start");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    serviceCallbacks.onNewMessage("webSocketService", jsonObject.toString());
                }
            }
        };
        t.start();
    }

    class ConnectionChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);



            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                create(uuid, password, context, dataBase, userManger);
            } else {
                ws.cancel();
                isCreated=false;
            }
        }
    }
}
