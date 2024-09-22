package com.bot.ping.activity.login;


import static org.chromium.base.ContextUtils.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.data.FirebaseTokenManager;
import com.bot.ping.manager.data.PermissionManager;
import com.bot.ping.manager.download.LoginManager;
import com.bot.ping.model.MailChecker;
import com.bot.ping.model.ReCAPTCHA;
import com.bot.ping.ui.dialog.DialogLoading;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import okhttp3.OkHttpClient;


public class LoginActivity extends AppCompatActivity {
    EditText emailTextView, passwordTextView;
    Button loginButton, registerButton, forgetPasswordButton;
    ReCAPTCHA reCAPTCHA;
    Context context;
//    CheckBox captchaCheckBox;
    LoginManager loginManager;
    DataManager dataManager;
    FirebaseTokenManager firebaseTokenManager;
    DialogLoading dialogLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }
    @SuppressLint("HardwareIds")
    void init() {
        context = this;
        reCAPTCHA = new ReCAPTCHA(this);
        dialogLoading=new DialogLoading(this);

        emailTextView = findViewById(R.id.editTextEmail);
        passwordTextView = findViewById(R.id.editTextPassword);

        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);
        forgetPasswordButton = findViewById(R.id.buttonForgetPassword);
//        captchaCheckBox = findViewById(R.id.captchaCheckBox);

        loginButton.setOnClickListener(loginListener);
        registerButton.setOnClickListener(setRegisterActivityListener);
        forgetPasswordButton.setOnClickListener(setForgetPasswordListener);
//        captchaCheckBox.setOnClickListener(captchaListener);

        dataManager = new DataManager(this);
        new PermissionManager(this);
        firebaseTokenManager = new FirebaseTokenManager(this);
        firebaseTokenManager.updateToken();
        loginManager = new LoginManager(new OkHttpClient(), this);
    }

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (true) {
                if (MailChecker.checkEmail(emailTextView.getText().toString())) {
                    dialogLoading.showDialog();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                if (loginManager.login(emailTextView.getText().toString(), passwordTextView.getText().toString(), firebaseTokenManager.getToken(), dataManager.getIdDevice(context)) != null) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                                    startActivity(intent);
                                } else {
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "неверный логин или пароль", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                dialogLoading.closeDialog();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                }
            } else {
                Toast.makeText(context, "Пройдите капчу", Toast.LENGTH_LONG).show();
            }
        }
    };
    View.OnClickListener setRegisterActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context.getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        }
    };

    View.OnClickListener setForgetPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        }
    };


    View.OnClickListener captchaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if (captchaCheckBox.isChecked()) {
//                reCAPTCHA.startCaptcha(captchaCheckBox);
//            }
        }
    };
}
