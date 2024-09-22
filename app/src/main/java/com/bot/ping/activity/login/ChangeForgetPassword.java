package com.bot.ping.activity.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.dataBase.DataBase;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.download.Adress;
import com.bot.ping.manager.download.LoginManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.Timer;
import com.bot.ping.ui.dialog.DialogLoading;

import org.checkerframework.checker.units.qual.C;

import okhttp3.OkHttpClient;

public class ChangeForgetPassword extends AppCompatActivity {

    LoginManager loginManager;
    EditText editTextPassword1;
    EditText editTextPassword2;
    EditText editTextVerificateCode;
    TextView timerTextView;
    Button buttonCheckCode;
    DataManager dataManager;
    Context context;
    DialogLoading dialogLoading;
    Bundle arguments;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_forget_password);

        context=this;
        loginManager=new LoginManager(new OkHttpClient(), this);
        dataManager=new DataManager(this);
        dialogLoading=new DialogLoading(this);
        arguments = getIntent().getExtras();

        editTextPassword1=findViewById(R.id.editTextPassword);
        editTextPassword2=findViewById(R.id.editTextCheckPassword);
        editTextVerificateCode=findViewById(R.id.editTextEntryCode);
        timerTextView = findViewById(R.id.timerTextView);
        Timer.start(timerTextView, 60 * 5);

        buttonCheckCode=findViewById(R.id.buttonCheckCode);
        buttonCheckCode.setOnClickListener(forgetPasswordListener);
    }

    View.OnClickListener forgetPasswordListener = new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             new Thread(new Runnable() {
                 public void run() {
                     if (editTextPassword1.getText().toString().equals(editTextPassword2.getText().toString())) {
                         dialogLoading.showDialog();
                         MyUser myUser=loginManager.checkVereficateCodeForgetPassword(arguments.getString("email"), editTextVerificateCode.getText().toString(), editTextPassword2.getText().toString(), dataManager.getToken());
                         if (myUser!=null) {
                             DataManager.saveUser(myUser, context);
                             Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                             startActivity(intent);
                             overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                         }else {
                             ChangeForgetPassword.this.runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     Toast.makeText(context, "Неправильный код", Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
                         dialogLoading.closeDialog();
                     }else {
                         ChangeForgetPassword.this.runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                 }
             }).start();
         }
    };
}
