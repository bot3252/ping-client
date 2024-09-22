package com.bot.ping.activity.login;

import static org.chromium.base.ContextUtils.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.data.FirebaseTokenManager;
import com.bot.ping.manager.download.LoginManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.Timer;
import com.bot.ping.ui.dialog.DialogLoading;

import okhttp3.OkHttpClient;

public class CheckCodeActivity  extends AppCompatActivity {
    TextView timerTextView, editTextEntryCode;
    Button buttonCheckCode;
    LoginManager loginManager;
    DataManager dataManager;
    Bundle arguments;
    Activity activity;
    MyUser user;
    FirebaseTokenManager firebaseTokenManager;
    DialogLoading dialogLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vereficate_code);

        activity = this;
        init();
    }
    @SuppressLint("HardwareIds")
    public void init(){
        arguments = getIntent().getExtras();
        user = (MyUser) arguments.get("myUser");

        dialogLoading=new DialogLoading(this);

        dataManager = new DataManager(this);
        loginManager = new LoginManager(new OkHttpClient(), this);

        firebaseTokenManager = new FirebaseTokenManager(this);
        firebaseTokenManager.updateToken();

        timerTextView = findViewById(R.id.timerTextView);
        buttonCheckCode = findViewById(R.id.buttonCheckCode);
        editTextEntryCode = findViewById(R.id.editTextEntryCode);
        buttonCheckCode.setOnClickListener(checkCodeListener);
        Timer.start(timerTextView, 60 * 5);
    }

    View.OnClickListener checkCodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialogLoading.showDialog();
            new Thread(new Runnable() {
                public void run() {
                    if(loginManager.vereficateCode(user.getEmail(), editTextEntryCode.getText().toString(), user.getPassword(), firebaseTokenManager.getToken(),  DataManager.getIdDevice(activity))) {
                        Intent intent = new Intent(activity.getApplicationContext(), SelectAvatarActivity.class);
                        intent.putExtra("myUser", user);
                        activity.startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                    }
                    dialogLoading.closeDialog();
                }
            }).start();

        }
    };
}
