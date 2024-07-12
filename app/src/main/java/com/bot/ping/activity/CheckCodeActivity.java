package com.bot.ping.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.Timer;
import com.bot.ping.model.User;

public class CheckCodeActivity  extends AppCompatActivity {
    TextView timerTextView, editTextEntryCode;
    Button buttonCheckCode;
    DownloadManager downloadManager;
    Bundle arguments;
    Activity activity;
    MyUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vereficate_code);
        activity = this;
        init();
    }

    public void init(){
        arguments = getIntent().getExtras();
        user = (MyUser) arguments.get("myUser");
        downloadManager = new DownloadManager(this);
        timerTextView = findViewById(R.id.timerTextView);
        buttonCheckCode = findViewById(R.id.buttonCheckCode);
        editTextEntryCode = findViewById(R.id.editTextEntryCode);
        buttonCheckCode.setOnClickListener(checkCodeListener);
        Timer.start(timerTextView, 60 * 5);
    }

    View.OnClickListener checkCodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new Thread(new Runnable() {
                public void run() {
                    if(downloadManager.vereficateCode(user.getEmail(), editTextEntryCode.getText().toString(), user.getPassword())) {
                        Intent intent = new Intent(activity.getApplicationContext(), SelectAvatarActivity.class);
                        intent.putExtra("myUser", user);
                        activity.startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                    }
                }
            }).start();

        }
    };
}
