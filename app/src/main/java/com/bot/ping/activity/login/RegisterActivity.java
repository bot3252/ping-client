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
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.data.FirebaseTokenManager;
import com.bot.ping.manager.download.LoginManager;
import com.bot.ping.model.MailChecker;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.ReCAPTCHA;
import com.bot.ping.ui.dialog.DialogLoading;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class RegisterActivity  extends AppCompatActivity {
    EditText emailTextView, nameTextView, passwordTextView;
    Button loginButton, registerButton, forgetPasswordButton;
//    CheckBox captchaCheckBox;
    LoginManager loginManager;
    DataManager dataManager;
    ReCAPTCHA reCAPTCHA;
    Context context;
    DialogLoading dialogLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }
    @SuppressLint("HardwareIds")
    private void init(){
        context = this;
        reCAPTCHA = new ReCAPTCHA(this);
        dialogLoading=new DialogLoading(this);

        dataManager = new DataManager(this);
        loginManager = new LoginManager(new OkHttpClient(), this);

        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);
//        captchaCheckBox = findViewById(R.id.captchaCheckBox);

        emailTextView = findViewById(R.id.editTextEmail);
        nameTextView = findViewById(R.id.editTextName);
        passwordTextView = findViewById(R.id.editTextPassword);

        loginButton.setOnClickListener(setLoginActivityListener);
        registerButton.setOnClickListener(registerListener);
//        captchaCheckBox.setOnClickListener(captchaListener);
    }

    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(true) {
                if (MailChecker.checkEmail(emailTextView.getText().toString())){
                    dialogLoading.showDialog();
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
                                if(loginManager.register(email, name, password)){
                                    Intent intent = new Intent(context.getApplicationContext(), CheckCodeActivity.class);
                                    intent.putExtra("myUser",user);
                                    context.startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                                }else {
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "no connect", Toast.LENGTH_SHORT).show();
                                            nameTextView.setText("");
                                            emailTextView.setText("");
                                            passwordTextView.setText("");
                                        }
                                    });
                                }
                                dialogLoading.closeDialog();

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(context, "не корректный email", Toast.LENGTH_SHORT).show();
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
//            if (captchaCheckBox.isChecked()){
//                reCAPTCHA.startCaptcha(captchaCheckBox);
//            }
        }
    };
}
