package com.bot.ping.manager;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.bot.ping.DataBase.Parser;
import com.bot.ping.activity.CheckCodeActivity;
import com.bot.ping.activity.LoginActivity;
import com.bot.ping.activity.MainActivity;
import com.bot.ping.model.User;
import com.bot.ping.ui.DialogLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginManager {
    OkHttpClient client;
    Activity activity;
    public DialogLoading dialogLoading;
    DataManager dataManager;
    public LoginManager(Activity activity){
        this.activity = activity;
        dialogLoading = new DialogLoading(activity);
        client = new OkHttpClient();
        dataManager = new DataManager(activity);
    }
    public void login(String email, String password, Intent intent) throws IOException {
        dialogLoading.showDialog();
        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://ping.com:8080/login?email="+email+"&password="+password)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialogLoading.closeDialog();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String stringResponse = response.body().string();
                User user = Parser.parseUser(stringResponse);
                dataManager.saveUser(user);
                dialogLoading.closeDialog();
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("uuid", user.getUuid());
                activity.startActivity(intent);
            }
        });
    }
    public void register(String email, String name,String password) throws IOException, JSONException {
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


        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                dialogLoading.closeDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String stringResponse = response.body().string();
                if (stringResponse.equals("successful")){
                    Intent intent = new Intent(activity.getApplicationContext(), CheckCodeActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    activity.startActivity(intent);
                }
                dialogLoading.closeDialog();
            }
        });
    }

    public void vereficateCode(String email, String password,String code) {
        dialogLoading.showDialog();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://ping.com:8080/verificateAccount?email="+email+"&verificationCode="+code)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialogLoading.closeDialog();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String stringResponse = response.body().string();
                if(stringResponse!="null"){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                login(email, password, new Intent(activity.getApplicationContext(), MainActivity.class));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }else {
                    Toast.makeText(activity, "Неправильный код или почта", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
