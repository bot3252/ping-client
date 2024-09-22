package com.bot.ping.manager.download;

import static org.chromium.base.ContextUtils.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.model.MyUser;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginManager {
    Context context;
    OkHttpClient client;
    String domain;
    MyUser myUser;

    public LoginManager(OkHttpClient okHttpClient, Context context){
        this.client = okHttpClient;
        this.context = context;
        domain=Adress.address(context);
    }

    public MyUser login(String email, String password, String firebaseToken, String deviceId) throws IOException {

        Request request = new Request.Builder()
                .url(domain+"/register/login?email="+email+"&password="+password+"&token="+firebaseToken+"&id_devise="+deviceId)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            if (response.isSuccessful()) {
                myUser = Parser.parseMyUser(stringResponse);
                myUser.setFirebaseToken(firebaseToken);
                DataManager.saveUser(myUser, context);
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

    public boolean vereficateCode(String email, String code, String password, String firebaseToken, String deviceId) {
        Request request = new Request.Builder()
                .url(domain+"/register/verificateAccount?email="+email+"&verificationCode="+code)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()){
                if (response.body() != null && !response.body().string().equals("null")) {
                    login(email, password, firebaseToken, deviceId);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return false;
    }
    public boolean forgetPassword(String email) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"email\":\""+email+"\"    \n" +
                "}");

        Request request = new Request.Builder()
                .url(domain+"/register/forgetPassword")
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
    public MyUser checkVereficateCodeForgetPassword(String email, String code, String password, String firebaseToken) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\n" +
                "}");
        Request request = new Request.Builder()
                .url(domain+"/register/checkVereficateCodeForgetPassword?email="+email+"&verificationCode="+code+"&newPassword="+password)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()){
                return  login(email, password, firebaseToken, DataManager.getIdDevice(context));
            }else {
                return null;
            }
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
        return null;
    }
}
