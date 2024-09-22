package com.bot.ping.manager.download;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.model.Message;
import com.bot.ping.model.MyUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserManager {
    OkHttpClient client;
    static String domain;
    public UserManager(OkHttpClient client, Context context){
        this.client = client;
        domain=Adress.address(context);
    }

    public void sendAvatar(Bitmap bitmap, String uuid, String password) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("avatar", "filename.jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, DataManager.bitmapToBytes(bitmap)))
                .build();
        Request request = new Request.Builder().url(domain+"/user/uploadAvatar?password="+password+"&uuid="+uuid).post(body).build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
    }


    public void updateUser(){

    }

    public void logout(MyUser myUser)  {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"uuid\":\""+myUser.getUuid()+"\",\n" +
                "    \"password\":\""+myUser.getPassword()+"\",\n" +
                "    \"name\":\""+myUser.getName()+"\",\n" +
                "    \"email\":\""+myUser.getEmail()+"\"    \n"+
                "}");

        Request request = new Request.Builder()
                .url(domain+"/register/logout?firebaseToken="+myUser.getFirebaseToken())
                .post(body)
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAccount(MyUser myUser) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"uuid\":\""+myUser.getUuid()+"\",\n" +
                "    \"name\":\""+myUser.getName()+"\",\n" +
                "    \"password\":\""+myUser.getPassword()+"\",\n" +
                "    \"email\":\""+myUser.getEmail()+"\",\n" +
                "    \"firebaseToken\":\""+myUser.getFirebaseToken()+ "\"    \n"+
                "}");

        Request request = new Request.Builder()
                .url(domain+"/register/logout")
                .post(body)
                .build();

        Call call = client.newCall(request);
        Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Bitmap downloadAvatar(String uuid) {
        Request request = new Request.Builder()
                .url(domain+"/user/getAvatar?uuid="+uuid)
                .build();

        try (Response response = client.newCall(request).execute()) {
            InputStream inputStream = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return null;
    }

    public ArrayList<String> getOnlineContacts(String uuid, String password) {
        ArrayList<String> onlineContacts = new ArrayList<>();

        Request request = new Request.Builder()
                .url(domain+"/user/getOnlineContacts?uuid="+uuid+"&password="+password)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if(!stringResponse.isEmpty()) {
                JSONArray jsonArray=new JSONArray(stringResponse);
                for (int i = 0; i <=  jsonArray.length() - 1; i++) {
                    onlineContacts.add(jsonArray.get(i).toString());
                }
            }
            return onlineContacts;
            } catch (IOException | JSONException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return new ArrayList<>();
    }

    public static ArrayList<String> getOnlineContacts(String uuid, String password, OkHttpClient client) {
        ArrayList<String> onlineContacts = new ArrayList<>();

        Request request = new Request.Builder()
                .url(domain+"/user/getOnlineContacts?uuid="+uuid+"&password="+password)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if(!stringResponse.isEmpty()) {
                JSONArray jsonArray=new JSONArray(stringResponse);
                for (int i = 0; i <=  jsonArray.length() - 1; i++) {
                    onlineContacts.add(jsonArray.get(i).toString());
                }
            }
            return onlineContacts;
        } catch (IOException | JSONException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return new ArrayList<>();
    }
}
