package com.bot.ping.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bot.ping.R;
import com.bot.ping.manager.DataManager;
import com.bot.ping.manager.LoginManager;
import com.bot.ping.model.Timer;

import java.io.IOException;

public class CheckCodeActivity extends Activity {
    TextView timerTextView, editTextEntryCode;
    Button buttonCheckCode;
    LoginManager loginManager;
    Bundle arguments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vereficate_code);
        init();
    }

    public void init(){
        timerTextView = findViewById(R.id.timerTextView);
        buttonCheckCode = findViewById(R.id.buttonCheckCode);
        editTextEntryCode = findViewById(R.id.editTextEntryCode);
        buttonCheckCode.setOnClickListener(checkCodeListener);
        Timer.start(timerTextView, 60 * 5);

        loginManager = new LoginManager(this);
    }

    View.OnClickListener checkCodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            arguments = getIntent().getExtras();
            loginManager.vereficateCode(arguments.get("email").toString(), arguments.get("password").toString(),editTextEntryCode.getText().toString());
        }
    };
}
