package com.bot.ping.manager.download;

import android.content.Context;

import com.bot.ping.model.Message;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateManger {
    OkHttpClient client;
    String domain;
    public UpdateManger(OkHttpClient client, Context context){
        this.client = client;
        domain=Adress.address(context);
    }
    public boolean checkUpdate(String version){
        Request request = new Request.Builder()
                .url(domain+"/updates/checkVersionClient?version="+version)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            return stringResponse.equals("true");

        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return true;
    }
}
