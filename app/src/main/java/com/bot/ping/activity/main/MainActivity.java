package com.bot.ping.activity.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.activity.login.NoConnectionActivity;
import com.bot.ping.activity.settings.SettingsActivity;
import com.bot.ping.activity.update.UpdateActivity;
import com.bot.ping.callbacks.WebSocketServiceCallbacks;
import com.bot.ping.activity.login.LoginActivity;
import com.bot.ping.dataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.download.Adress;
import com.bot.ping.manager.download.ConnectCheck;
import com.bot.ping.manager.download.ContactManager;
import com.bot.ping.manager.download.LoginManager;
import com.bot.ping.manager.download.UserManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.service.WebSocketService;
import com.bot.ping.ui.dialog.DialogLoading;
import com.bot.ping.ui.manager.UIManagerActivity;
import com.google.firebase.FirebaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements WebSocketServiceCallbacks, Runnable {
    private WebSocketService webSocketService;
    private boolean bound = false;
    UIManagerActivity uiManagerActivity;
    DataManager dataManager;
    MyUser myUser;
    Context context;
    Bundle arguments;
    UserManager userManager;
    ContactManager contactManager;
    ArrayList<User> allContacts;
    LoginManager loginManager;
    DataBase dataBase;
    DialogLoading dialogLoading;
    public Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ConnectCheck.check(this)) {
            init();
        }else{
            intent = new Intent(context, NoConnectionActivity.class);
            startActivity(intent);
        }
    }
    void init(){
        dialogLoading=new DialogLoading(this);
        dialogLoading.showDialog();

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
        context = this;
        arguments = getIntent().getExtras();

        dataManager = new DataManager(this);

        if (arguments!=null)
            myUser = (MyUser) arguments.get("myUser");
        else
            myUser = dataManager.getMyUser();

        if (myUser == null|| myUser.getUuid() == null) {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else {
            dataBase = new DataBase(this, myUser.getUuid());

            OkHttpClient okHttpClient = new OkHttpClient();

            contactManager = new ContactManager(okHttpClient, myUser.getUuid(), this);
            userManager = new UserManager(okHttpClient, this);
            loginManager = new LoginManager(okHttpClient, context);
            uiManagerActivity = new UIManagerActivity(this, myUser);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        setAvatar();

                        if(loginManager.login(myUser.getEmail(), myUser.getPassword(),dataManager.getToken(),DataManager.getIdDevice(context))==null){
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, "Пароль от аккаунта был сменен", Toast.LENGTH_SHORT).show();
                                }
                            });

                            dataManager.deleteData();
                            intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        }
                        dialogLoading.closeDialog();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
            allContacts=dataBase.getContacts();
            uiManagerActivity.setAllContacts(allContacts);
        }

    }
    void setAvatar(){
        Bitmap avatar=null;
        try {
            avatar = DataManager.getImage(myUser.getUuid(), context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (avatar==null){
            avatar = userManager.downloadAvatar(myUser.getUuid());
            if(avatar==null){
                avatar= BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar1_drawable);
            }
        }
        Bitmap finalAvatar = avatar;
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                uiManagerActivity.setNavAvatar(finalAvatar);
            }
        });
        myUser.setAvatar(avatar);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return uiManagerActivity.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onSupportNavigateUp() {
        return uiManagerActivity.onSupportNavigateUp();
    }
    public void exit_account(MenuItem item) {
        new Thread(new Runnable() {
            public void run() {
                userManager.logout(myUser);
            }
        }).start();
        dataManager.deleteData();
        webSocketService.isCreated=false;
        webSocketService.ws.cancel();

        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(myUser.getUuid()!=null&&!bound) {
            intent = new Intent(this, WebSocketService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            if(webSocketService!=null){
                webSocketService.updateOnlineUser(this);
                setOnlineUser(webSocketService.getUsersOnline());
            }
            bound=true;
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
            setOnlineUser(webSocketService.getUsersOnline());
            bound = true;

            webSocketService.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };
    @Override
    public void onNewMessage(String type,String messageString) {

        try {
            JSONObject jsonObject = new JSONObject(messageString);
            switch (type){
                case "status":
                    String status = jsonObject.getString("status");
                    String uuid = jsonObject.getString("uuid");
                    for(int i = 0; i <= allContacts.size()-1; i++) {
                        if(uuid.equals(allContacts.get(i).getUuid())){
                            if (status.equals(User.TYPE_STATUS_ONLINE)){
                                allContacts.get(i).setStatus(User.TYPE_STATUS_ONLINE);
                            }else {
                                allContacts.get(i).setStatus(User.TYPE_STATUS_OFFLINE);
                            }
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    uiManagerActivity.updateStatusUser(status, uuid);
                                }
                            });
                        }
                    }
                    break;
                case "new_contact":
                    User user = Parser.parseUser(jsonObject.toString());

                    Bitmap bitmap = DataManager.getImage(user.getUuid(), context);

                    user.setAvatar(bitmap);
                    user.setStatus(User.TYPE_STATUS_ONLINE);

                    allContacts.add(user);
                    dataBase.saveContact(user);
                    break;
                case "webSocketService":
                    String state = jsonObject.getString("state");
                    if(state.equals("start")) {
                        setOnlineUser(webSocketService.getUsersOnline());
                    }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void setOnlineUser(ArrayList<String> onlineUsers){
        onlineUsers = webSocketService.getUsersOnline();
        allContacts = webSocketService.getAllContacts();
        dataBase.addNewContacts(allContacts);
        if(onlineUsers!=null) {
            for (int i = 0; i <= allContacts.size() - 1; i++) {
                for (int b = 0; b <= onlineUsers.size() - 1; b++) {
                    if (onlineUsers.get(b).equals(allContacts.get(i).getUuid())) {
                        allContacts.get(i).setStatus(User.TYPE_STATUS_ONLINE);
                    }
                }
            }
        }
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                uiManagerActivity.setAllContacts(allContacts);
            }
        });
    }

    @Override
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    public void set_my_profile_activity(MenuItem item) {
        intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("uuid", myUser.getUuid());
        intent.putExtra("lastActivity", "MainActivity");
        overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        startActivity(intent);
    }

    @Override
    public void run() {

    }

    public void set_settings_activity(MenuItem item) {
        Intent intent=new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}