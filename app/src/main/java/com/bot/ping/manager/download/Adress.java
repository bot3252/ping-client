package com.bot.ping.manager.download;

import android.content.Context;

import com.bot.ping.manager.data.DataManager;

import org.json.JSONException;

public class Adress {
    public static String address(Context context) {
        try {
            return DataManager.getAddress(context).get("address").toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public static String websocket(Context context) {
        try {
            return DataManager.getAddress(context).get("addressWebsocket").toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
