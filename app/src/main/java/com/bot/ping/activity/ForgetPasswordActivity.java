package com.bot.ping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.model.ReCAPTCHA;

public class ForgetPasswordActivity extends AppCompatActivity {
    Button buttonCheckCode, buttonSetLoginActivity, buttonSetRegisterActivity;
    CheckBox captchaCheckBox;
    ReCAPTCHA reCAPTCHA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
    }
    void init(){
        reCAPTCHA = new ReCAPTCHA(this);
        captchaCheckBox = findViewById(R.id.captchaCheckBox);

        buttonCheckCode = findViewById(R.id.buttonCheckCode);
        buttonSetLoginActivity = findViewById(R.id.buttonSetLoginActivity);
        buttonSetRegisterActivity = findViewById(R.id.buttonSetRegisterActivity);

        buttonCheckCode.setOnClickListener(forgetPasswordListener);
        buttonSetLoginActivity.setOnClickListener(setLoginActivityListener);
        buttonSetRegisterActivity.setOnClickListener(setRegisterActivityListener);
        captchaCheckBox.setOnClickListener(captchaListener);

        reCAPTCHA = new ReCAPTCHA(this);
    }

    View.OnClickListener forgetPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        }
    };


    View.OnClickListener setLoginActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
        }
    };

    View.OnClickListener setRegisterActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
        }
    };

    View.OnClickListener captchaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (captchaCheckBox.isChecked()){
                reCAPTCHA.startCaptcha(captchaCheckBox);
            }
        }
    };

    @Override
    protected void onDestroy() {
        overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
        super.onDestroy();
    }
}