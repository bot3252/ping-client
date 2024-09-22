package com.bot.ping.manager.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseTokenManager {
    public String token;
    Context context;
    public FirebaseTokenManager(Context context){
        this.context = context;
    }
    public void updateToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        token = task.getResult();
                        DataManager.saveAndSendToken(token, context);
                    }
                });
    }
    public String getToken(){
        return token;
    }
}
