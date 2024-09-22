package com.bot.ping.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.manager.download.LoginManager;
import com.bot.ping.model.MailChecker;
import com.bot.ping.model.ReCAPTCHA;
import com.bot.ping.ui.dialog.DialogLoading;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class ForgetPasswordActivity extends AppCompatActivity {
    Button buttonSetLoginActivity, buttonSetRegisterActivity, buttonSendCode;
    CheckBox captchaCheckBox;
    ReCAPTCHA reCAPTCHA;
    LoginManager loginManager;
    EditText editTextEmail;
    EditText passwordTextView;
    DialogLoading dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
    }
    @SuppressLint("WrongViewCast")
    void init(){
        reCAPTCHA = new ReCAPTCHA(this);
        dialogLoading=new DialogLoading(this);

//        captchaCheckBox = findViewById(R.id.captchaCheckBox);

        buttonSetLoginActivity = findViewById(R.id.buttonSetLoginActivity);
        buttonSetRegisterActivity = findViewById(R.id.buttonSetRegisterActivity);
        buttonSendCode = findViewById(R.id.buttonSendCode);

        editTextEmail = findViewById(R.id.editTextEmail);
        passwordTextView = findViewById(R.id.editTextPassword);

        buttonSetLoginActivity.setOnClickListener(setLoginActivityListener);
        buttonSetRegisterActivity.setOnClickListener(setRegisterActivityListener);
        buttonSendCode.setOnClickListener(sendCodeListener);

        loginManager=new LoginManager(new OkHttpClient(), this);
        reCAPTCHA = new ReCAPTCHA(this);
    }

    View.OnClickListener sendCodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialogLoading.showDialog();
            new Thread(new Runnable() {
                public void run() {

                    if(loginManager.forgetPassword(editTextEmail.getText().toString())){
                        Intent intent = new Intent(getApplicationContext(), ChangeForgetPassword.class);
                        intent.putExtra("email", editTextEmail.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                    }
                    dialogLoading.closeDialog();
                }
            }).start();
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

    @Override
    protected void onDestroy() {
        overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
        super.onDestroy();
    }
}