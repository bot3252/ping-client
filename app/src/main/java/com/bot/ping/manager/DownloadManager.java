package com.bot.ping.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bot.ping.DataBase.Parser;
import com.bot.ping.model.User;
import com.bot.ping.ui.DialogLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadManager {
    OkHttpClient client;
    DataManager dataManager;
    DialogLoading dialogLoading;
    public DownloadManager(Context context){
        client = new OkHttpClient();
        dataManager = new DataManager(context);
        dialogLoading = new DialogLoading(context);
    }
    public User login(String email, String password) throws IOException {
        dialogLoading.showDialog();
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://ping.com:8080/login?email="+email+"&password="+password)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            User user = Parser.parseUser(stringResponse);
            dataManager.saveUser(user);
            dialogLoading.closeDialog();
            return user;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        dialogLoading.closeDialog();
        return null;
    }
    public boolean register(String email, String name,String password) throws IOException, JSONException {
        dialogLoading.showDialog();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"name\":\""+name+"\",\n" +
                "    \"email\":\""+email+"\",\n" +
                "    \"password\":\""+password+"\"    \n" +
                "}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://ping.com:8080/register")
                .post(body)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (response.body().string().equals("successful")){
                return true;
            }
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        dialogLoading.closeDialog();
        return false;
    }

    public boolean vereficateCode(String email, String password,String code) {
        dialogLoading.showDialog();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://ping.com:8080/verificateAccount?email="+email+"&verificationCode="+code)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return true;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        dialogLoading.closeDialog();
        return false;
    }

    public Bitmap downloadAvatar(String uuid) {
        dialogLoading.showDialog();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                    .url("http://ping.com:8080/getAvatar?uuid="+uuid)
                .build();


        try (Response response = client.newCall(request).execute()) {
            InputStream inputStream = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            dialogLoading.closeDialog();
            return bitmap;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        dialogLoading.closeDialog();
        return null;
    }

    public ArrayList<User> downloadAllContacts(String email, String password) {
        dialogLoading.showDialog();
        ArrayList<User> users = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://ping.com:8080/getAllContacts?email="+email+"&password="+password)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            dialogLoading.closeDialog();
            users=Parser.parseUsers(new JSONArray(stringResponse));
            for (int i = 0; i<users.size(); i++){
                User user = users.get(i);
                user.setAvatar(downloadAvatar(user.getUuid()));
            }
            return users;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        dialogLoading.closeDialog();
        return null;
    }


    public void sendAvatar(Bitmap bitmap, String email, String uuid, String password) throws IOException {
        dialogLoading.showDialog();
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("avatar", "filename.jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, DataManager.bitmapToBytes(bitmap)))
                .addFormDataPart("uuid", "419683a1-276f-43d7-b467-1b543d85ca03")
                .addFormDataPart("password", "password")
                .addFormDataPart("email", "ping.chat.noreply@gmail.com")
                .build();
        Request request = new Request.Builder().url("http://ping.com:8080/uploadAvatar").post(body).build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        dialogLoading.closeDialog();
    }
}