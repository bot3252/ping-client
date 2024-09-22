package com.bot.ping.callbacks;

public interface  WebSocketServiceCallbacks {
    void onNewMessage(String type,String messageString);
}
