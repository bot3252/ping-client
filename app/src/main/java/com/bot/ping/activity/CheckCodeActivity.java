package com.bot.ping.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bot.ping.R;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.model.Timer;

public class CheckCodeActivity extends Activity {
    TextView timerTextView, editTextEntryCode;
    Button buttonCheckCode;
    DownloadManager downloadManager;
    Bundle arguments;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vereficate_code);
        activity = this;
        init();
    }

    public void init(){
        timerTextView = findViewById(R.id.timerTextView);
        buttonCheckCode = findViewById(R.id.buttonCheckCode);
        editTextEntryCode = findViewById(R.id.editTextEntryCode);
        buttonCheckCode.setOnClickListener(checkCodeListener);
        Timer.start(timerTextView, 60 * 5);
    }

    View.OnClickListener checkCodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            arguments = getIntent().getExtras();
            new Thread(new Runnable() {
                public void run() {
                    String name = arguments.get("name").toString();
                    String email = arguments.get("email").toString();
                    String password = arguments.get("password").toString();
                    downloadManager.vereficateCode(email, password,editTextEntryCode.getText().toString());
                    Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                    activity.startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                }
            }).start();

        }
    };
}
