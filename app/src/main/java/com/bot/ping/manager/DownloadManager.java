package com.bot.ping.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bot.ping.DataBase.Parser;
import com.bot.ping.model.Message;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadManager {
    OkHttpClient client;
    public DataManager dataManager;
    String token;
    MyUser myUser;
    String domain = "";
    public DownloadManager(Context context){
        client = new OkHttpClient();
        dataManager = new DataManager(context);
        myUser=dataManager.getMyUser();
        boolean isLocalServer = true;
        if (isLocalServer){
            domain = "http://ping.com:8080";
        }   else {
            domain = "https://koshmin.ru/ping";
        }

    }
    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        token = task.getResult();
                        dataManager.saveToken(token);
                    }
                });
    }
    public MyUser login(String email, String password) throws IOException {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(domain+"/register/login?email="+email+"&password="+password+"&token="+dataManager.getToken())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if (response.isSuccessful()) {
                myUser = Parser.parseMyUser(stringResponse);
                myUser.setFirebaseToken(token);
                dataManager.saveUser(myUser);
            }
            return myUser;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return null;
    }
    public boolean register(String email, String name,String password) throws IOException, JSONException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"name\":\""+name+"\",\n" +
                "    \"email\":\""+email+"\",\n" +
                "    \"password\":\""+password+"\"    \n" +
                "}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/register/register")
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
        return false;
    }

    public boolean vereficateCode(String email, String code, String password) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/register/verificateAccount?email="+email+"&verificationCode="+code)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()){
                login(email, password);
                return true;
            }
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return false;
    }

    public Bitmap downloadAvatar(String uuid) {
        OkHttpClient client = new OkHttpClient();

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

    public ArrayList<User> downloadAllContacts(String uuid, String password) {
        ArrayList<User> users = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/user/getAllContacts?uuid="+uuid+"&password="+password)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            JSONArray jsonArray = new JSONArray(stringResponse);
            if (response.isSuccessful())
                users=Parser.parseUsers(jsonArray);
            for (int i = 0; i < jsonArray.length(); i++){
                User user = users.get(i);
                Bitmap avatar = downloadAvatar(user.getUuid());
                user.setAvatar(avatar);
            }
            return users;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public void sendAvatar(Bitmap bitmap, String uuid, String password) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("avatar", "filename.jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, DataManager.bitmapToBytes(bitmap)))
                .addFormDataPart("password", password)
                .addFormDataPart("uuid", uuid)
                .build();
        Request request = new Request.Builder().url(domain+"/user/uploadAvatar").post(body).build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
    }
    public void sendContact(String uuid1, String password, String uuid2) throws IOException {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    public ArrayList<User> findUser(String tag)  {
        ArrayList<User> users = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/user/findUser?tag="+tag)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            users=Parser.parseUsers(new JSONArray(stringResponse));
            for (int i = 0; i<users.size(); i++){
                User user = users.get(i);
                user.setAvatar(downloadAvatar(user.getUuid()));
                if(user.getUuid().equals(myUser.getUuid())){
                    users.remove(user);
                }
            }
            return users;
        } catch (IOException e) {
            Toast.makeText(dataManager.context, "Ошибка подключения " + e.toString(), Toast.LENGTH_SHORT).show();
            System.out.println("Ошибка подключения: " + e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void sendMessage(String from, String to, String password, String message) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        now.setTimeZone(TimeZone.getTimeZone("UTC"));

        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"to\":\""+to+"\",\n" +
                "    \"from\":\""+from+"\",\n" +
                "    \"message\":\""+message+"\",\n" +
                "    \"firebaseToken\":\""+token+"\",\n" +
                "    \"date\":\""+now.format(new java.util.Date())+ "\"    \n"+
                "}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/message/sendMessage?password="+password)
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

    public ArrayList<Message> getAllMessage(String to, String from, MyUser myUser)  {
        ArrayList<Message> messages = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(domain+"/message/getMessage?to="+to+"&from="+from+"&password="+myUser.getPassword())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if(!stringResponse.isEmpty()) {
                messages = Parser.parseMessages(new JSONArray(stringResponse), myUser);
            }
            return messages;
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        } catch (JSONException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return new ArrayList<>();
    }

    public void logout(MyUser myUser)  {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"uuid\":\""+myUser.getUuid()+"\",\n" +
                "    \"password\":\""+myUser.getPassword()+"\",\n" +
                "    \"name\":\""+myUser.getName()+"\",\n" +
                "    \"email\":\""+myUser.getEmail()+"\",\n" +
                "    \"firebaseToken\":\""+token+ "\"    \n"+
                "}");

        OkHttpClient client = new OkHttpClient();

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

    public void deleteAccount(MyUser myUser) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"uuid\":\""+myUser.getUuid()+"\",\n" +
                "    \"name\":\""+myUser.getName()+"\",\n" +
                "    \"password\":\""+myUser.getPassword()+"\",\n" +
                "    \"email\":\""+myUser.getEmail()+"\",\n" +
                "    \"firebaseToken\":\""+token+ "\"    \n"+
                "}");

        OkHttpClient client = new OkHttpClient();

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

    public boolean checkUpdate(String version){
        ArrayList<Message> messages = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

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