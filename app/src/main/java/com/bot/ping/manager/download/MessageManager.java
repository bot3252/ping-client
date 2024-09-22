package com.bot.ping.manager.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.bot.ping.dataBase.DataBase;
import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.model.Message;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageManager {
    OkHttpClient client;
    String domain;
    public MessageManager(OkHttpClient okHttpClient, Context context){
        this.client = okHttpClient;
        domain=Adress.address(context);
    }


    public Message sendMessage(String from, String to, String password, String messageString, String id_device) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        now.setTimeZone(TimeZone.getTimeZone("UTC"));

        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"to\":\""+to+"\",\n" +
                "    \"from\":\""+from+"\",\n" +
                "    \"message\":\""+messageString+"\",\n" +
                    "    \"date\":\""+now.format(new java.util.Date())+ "\"    \n"+
                "}");


        Request request = new Request.Builder()
                .url(domain+"/message/sendMessage?password="+password+"&id_device="+id_device)
                .post(body)
                .build();
        Message message = null;
        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if(response.isSuccessful())
                if(!stringResponse.isEmpty()) {
                    message = Parser.parseMessage(stringResponse, from);
                }
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return message;
    }
    public ArrayList<Message> getAllNewMessages(String myUuid, String chatUserUuid, String password, String startDate)  {
        ArrayList<Message> messages = new ArrayList<>();

        Instant instant = null;
        String dateString = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            instant = Instant.from(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneId.systemDefault())
                            .parse(startDate));
            Date myDate = Date.from(instant);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateString = formatter.format(myDate);
        }

        Request request = new Request.Builder()
                .url(domain+"/message/getMessageSortByDate?uuid="+myUuid+"&chatUserUuid="+chatUserUuid+"&password="+password+"&startData="+dateString)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if(!stringResponse.isEmpty()) {
                messages = Parser.parseMessages(new JSONArray(stringResponse), myUuid);
            }
            return messages;
        } catch (IOException | JSONException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return new ArrayList<>();
    }
    public ArrayList<Message> getAllMessage(String to, String from, String password)  {
        ArrayList<Message> messages = new ArrayList<>();

        Request request = new Request.Builder()
                .url(domain+"/message/getAllMessage?to="+to+"&from="+from+"&password="+password)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if(!stringResponse.isEmpty()) {
                messages = Parser.parseMessages(new JSONArray(stringResponse), from);
            }
            return messages;
        } catch (IOException | JSONException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return messages;
    }
}
