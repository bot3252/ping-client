package com.bot.ping.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.manager.LoginManager;
import com.bot.ping.model.ReCAPTCHA;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity {
    EditText emailTextView, passwordTextView;
    Button loginButton, registerButton, forgetPasswordButton;
    ReCAPTCHA reCAPTCHA;
    Context context;
    LoginManager loginManager;
    CheckBox captchaCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        loginManager= new LoginManager(this);
        reCAPTCHA = new ReCAPTCHA(this);

        init();
    }

    void init(){
        emailTextView = findViewById(R.id.editTextEmail);
        passwordTextView = findViewById(R.id.editTextPassword);

        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);
        forgetPasswordButton = findViewById(R.id.buttonForgetPassword);
        captchaCheckBox = findViewById(R.id.captchaCheckBox);

        loginButton.setOnClickListener(loginListener);
        registerButton.setOnClickListener(setRegisterActivityListener);
        forgetPasswordButton.setOnClickListener(setForgetPasswordListener);
        captchaCheckBox.setOnClickListener(captchaListener);
    }

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                if(captchaCheckBox.isChecked()) {
                    if (isValidEmail(emailTextView.getText().toString())){
                        loginManager.login(emailTextView.getText().toString(), passwordTextView.getText().toString(), new Intent(getApplicationContext(), MainActivity.class));
                    }
                } else {
                    Toast.makeText(context, "Пройдите капчу", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    View.OnClickListener setRegisterActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context.getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener setForgetPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
            startActivity(intent);
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


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
