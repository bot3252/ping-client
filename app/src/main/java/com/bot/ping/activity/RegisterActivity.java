package com.bot.ping.activity;

import android.app.Activity;
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

import com.bot.ping.R;
import com.bot.ping.manager.LoginManager;
import com.bot.ping.model.ReCAPTCHA;

import org.json.JSONException;

import java.io.IOException;

public class RegisterActivity extends Activity {
    EditText emailTextView, nameTextView,passwordTextView;
    Button loginButton, registerButton, forgetPasswordButton;
    CheckBox captchaCheckBox;
    LoginManager loginManager;
    ReCAPTCHA reCAPTCHA;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        reCAPTCHA = new ReCAPTCHA(this);
        loginManager= new LoginManager(this);
        init();
    }

    private void init(){
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);
        captchaCheckBox = findViewById(R.id.captchaCheckBox);

        emailTextView = findViewById(R.id.editTextEmail);
        nameTextView = findViewById(R.id.editTextName);
        passwordTextView = findViewById(R.id.editTextPassword);

        loginButton.setOnClickListener(setLoginActivityListener);
        registerButton.setOnClickListener(registerListener);
        captchaCheckBox.setOnClickListener(captchaListener);
    }

    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                if(captchaCheckBox.isChecked()) {
//                    if (isValidEmail(emailTextView.getText().toString())){
                    loginManager.register(emailTextView.getText().toString(), nameTextView.getText().toString(),passwordTextView.getText().toString());
//                    }
                } else {
                    Toast.makeText(context, "Пройдите капчу", Toast.LENGTH_LONG).show();
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };
    View.OnClickListener setLoginActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, LoginActivity.class);
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
