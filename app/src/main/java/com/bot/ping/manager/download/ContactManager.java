package com.bot.ping.manager.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bot.ping.dataBase.Parser;
import com.bot.ping.model.User;
import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactManager {

    OkHttpClient client;
    static String domain;
    String userUuid;
    public ContactManager(OkHttpClient client, String userUuid, Context context){
        this.client = client;
        this.userUuid = userUuid;
        domain=Adress.address(context);
    }

    public ArrayList<User> findUser(String tag)  {
        ArrayList<User> users = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/user/findUser?tag="+tag)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            JSONArray jsonArray = new JSONArray(stringResponse);
            users= Parser.parseUsers(jsonArray);
            for (int i = 0; i<users.size(); i++){
                User user = users.get(i);
                user.setAvatar(downloadAvatar(user.getUuid()));
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if(jsonObject.getString("status").equals(User.TYPE_STATUS_ONLINE))
                    user.setStatus(User.TYPE_STATUS_ONLINE);
                else
                    user.setStatus(User.TYPE_STATUS_OFFLINE);
            }
            for (int i = 0; i<users.size(); i++) {
                User user = users.get(i);
                if(user.getUuid().equals(userUuid)){
                    users.remove(user);
                }
            }

            return users;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public static ArrayList<User> downloadAllContacts(String uuid, String password, OkHttpClient client) {
        ArrayList<User> users = new ArrayList<>();

        Request request = new Request.Builder()
                .url(domain+"/user/getAllContacts?uuid="+uuid+"&password="+password)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if (response.isSuccessful()&&!stringResponse.isEmpty()) {
                JSONArray jsonArray = new JSONArray(stringResponse);
                users = Parser.parseUsers(jsonArray);
                for (int i = 0; i < jsonArray.length(); i++){
                    User user = users.get(i);
                    Bitmap avatar = stDownloadAvatar(user.getUuid(), client);
                    user.setAvatar(avatar);
                }
            }
            return users;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void sendContact(String uuid1, String password, String uuid2) throws IOException {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        now.setTimeZone(TimeZone.getTimeZone("UTC"));

        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"uuid1\":\""+uuid1+"\",\n" +
                "    \"uuid2\":\""+uuid2+"\"    \n"+
                "}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/user/createContact?password="+password)
                .post(body)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            response.body().string().equals("successful");
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
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

    public static Bitmap stDownloadAvatar(String uuid, OkHttpClient client) {
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
}
