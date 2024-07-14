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

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.ReCAPTCHA;
import com.bot.ping.model.User;

import org.json.JSONException;

import java.io.IOException;

public class RegisterActivity  extends AppCompatActivity {
    EditText emailTextView, nameTextView,passwordTextView;
    Button loginButton, registerButton, forgetPasswordButton;
    CheckBox captchaCheckBox;
    DownloadManager downloadManager;
    ReCAPTCHA reCAPTCHA;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        reCAPTCHA = new ReCAPTCHA(this);
        downloadManager = new DownloadManager(this);
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
            if(captchaCheckBox.isChecked()) {
                if (isValidEmail(emailTextView.getText().toString())){
                    new Thread(new Runnable() {
                        public void run() {
                            String name = nameTextView.getText().toString();
                            String email = emailTextView.getText().toString();
                            String password = passwordTextView.getText().toString();

                            MyUser user = new MyUser();
                            user.setName(name);
                            user.setEmail(email);
                            user.setPassword(password);

                            try {
                                if(downloadManager.register(email, name, password)){
                                    Intent intent = new Intent(context.getApplicationContext(), CheckCodeActivity.class);
                                    intent.putExtra("myUser",user);
                                    context.startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                                }else {
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "неверный логин или пароль", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    nameTextView.setText("");
                                    emailTextView.setText("");
                                    passwordTextView.setText("");
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
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
    View.OnClickListener setLoginActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
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
            if (captchaCheckBox.isChecked()){
                reCAPTCHA.startCaptcha(captchaCheckBox);
            }
        }
    };


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
