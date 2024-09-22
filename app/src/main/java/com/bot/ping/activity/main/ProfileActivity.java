package com.bot.ping.activity.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bot.ping.activity.login.NoConnectionActivity;
import com.bot.ping.callbacks.WebSocketServiceCallbacks;
import com.bot.ping.dataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.download.ConnectCheck;
import com.bot.ping.manager.download.UserManager;
import com.bot.ping.model.Message;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.service.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class ProfileActivity extends AppCompatActivity implements WebSocketServiceCallbacks{
    TextView textViewName, textViewNickname, textViewDescription;
    ImageButton buttonBack, buttonSend;
    ImageView imageAvatar, imageStatus;
    User chatUser;
    Bundle arguments;
    DataBase dataBase;
    DataManager dataManager;
    String lastActivity;
    Context context;
    MyUser myUser;
    UserManager userManager;
    Intent intent;
    private boolean bound = false;
    private WebSocketService webSocketService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        arguments = getIntent().getExtras();
        if(ConnectCheck.check(this)) {
            init();
        }else{
            intent = new Intent(context, NoConnectionActivity.class);
            startActivity(intent);
        }
    }

    void init(){
        userManager=new UserManager(new OkHttpClient(), this);
        context=this;
        dataManager = new DataManager(this);
        dataBase = new DataBase(this,dataManager.getMyUser().getUuid());
        myUser = dataManager.getMyUser();

        if (!myUser.getUuid().equals(arguments.getString("uuid"))){
            chatUser = dataBase.getContact(arguments.getString("uuid"));
        }else {
            chatUser=myUser.myUserToUser();
        }

        lastActivity=arguments.getString("lastActivity");

        textViewName = findViewById(R.id.textViewName);
        textViewNickname = findViewById(R.id.textViewNickname);
        textViewDescription = findViewById(R.id.textViewDescription);

        imageAvatar = findViewById(R.id.imageAvatar);
        imageStatus = findViewById(R.id.imageViewStatus);

        buttonBack = findViewById(R.id.buttonBack);
        buttonSend = findViewById(R.id.buttonSend);

        if(chatUser!=null) {
            textViewName.setText(chatUser.getName());
            textViewNickname.setText(chatUser.getNickname());
            textViewDescription.setText(chatUser.getDescription());
            imageAvatar.setImageBitmap(chatUser.getAvatar());
        }

        buttonBack.setOnClickListener(backListener);
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(lastActivity.equals("ChatActivity")) {
                intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("uuid", chatUser.getUuid());
            }else{
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }

            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);

        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        if(myUser.getUuid()!=null&&!bound) {
            if(webSocketService!=null) {
                webSocketService.setCallbacks(ProfileActivity.this);
                if (!webSocketService.isCreated)
                    webSocketService.create(myUser.getUuid(), myUser.getPassword(), context, dataBase, userManager);
            }else {
                intent = new Intent(this, WebSocketService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            bound=true;
            updateStatusUsers();
        }
        intent=null;
    }

    private void updateStatusUsers() {
        new Thread(new Runnable() {
            public void run() {
                ArrayList onlineUsers = userManager.getOnlineContacts(myUser.getUuid(), myUser.getPassword());
                webSocketService.setUsersOnline(onlineUsers);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myUser.getUuid()!=null&&!bound) {
            if(webSocketService!=null) {
                webSocketService.setCallbacks(ProfileActivity.this);
                if (!webSocketService.isCreated)
                    webSocketService.create(myUser.getUuid(), myUser.getPassword(), context, dataBase, userManager);
            }else {
                intent = new Intent(this, WebSocketService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            bound=true;

            updateStatusUsers();
        }
        intent=null;
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (bound&&intent==null) {
            webSocketService.close();
            bound = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound && intent == null) {
            bound = false;
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            WebSocketService.LocalBinder binder = (WebSocketService.LocalBinder) service;
            webSocketService = binder.getService();
            if (!webSocketService.isCreated) {
                webSocketService.create(myUser.getUuid(), myUser.getPassword(), context,dataBase, userManager);
            }
            chatUser.setStatus(webSocketService.getStatusUser(chatUser.getUuid()));

            bound = true;
            webSocketService.setCallbacks(ProfileActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void onNewMessage(String type, String messageString) {

    }
}