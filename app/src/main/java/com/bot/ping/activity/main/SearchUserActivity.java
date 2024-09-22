package com.bot.ping.activity.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.activity.login.NoConnectionActivity;
import com.bot.ping.callbacks.WebSocketServiceCallbacks;
import com.bot.ping.dataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.download.ConnectCheck;
import com.bot.ping.manager.download.ContactManager;
import com.bot.ping.manager.download.UserManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.service.WebSocketService;
import com.bot.ping.ui.adapter.ContactAdapter;
import com.bot.ping.ui.listiner.RecyclerItemClickListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class SearchUserActivity extends AppCompatActivity  implements WebSocketServiceCallbacks {
    ContactManager contactManager;
    DataManager dataManager;
    UserManager userManager;
    SearchView searchView;
    Activity activity;
    Context context;
    RecyclerView recyclerView;
    OkHttpClient okHttpClient;
    ArrayList<User> users;
    MyUser myUser;
    Bundle arguments;
    DataBase dataBase;
    Intent intent;
    private boolean bound = false;
    private WebSocketService webSocketService;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searsh_user);
        activity=this;
        context=this;
        dataManager=new DataManager(this);
        if(ConnectCheck.check(this)) {
            init();
        }else{
            intent = new Intent(context, NoConnectionActivity.class);
            startActivity(intent);
        }
    }

    void init(){
        arguments = getIntent().getExtras();
        myUser = dataManager.getMyUser();


        okHttpClient=new OkHttpClient();
        dataBase = new DataBase(this, myUser.getUuid());
        contactManager=new ContactManager(new OkHttpClient(), myUser.getUuid(), this);
        userManager=new UserManager(okHttpClient, this);

        searchView=findViewById(R.id.searchViewUser);
        recyclerView = findViewById(R.id.list);


        new Thread(new Runnable() {
            public void run() {
                users = contactManager.findUser("");
                SearchUserActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        RecyclerView recyclerView = findViewById(R.id.list);
                        ContactAdapter adapter = new ContactAdapter(activity,users);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) throws IOException {
                        new Thread(new Runnable() {
                            public void run() {
                                User chatUser = users.get(position);
                                if (dataBase.checkUser(chatUser.getUuid())) {
                                    dataBase.saveContact(chatUser);
                                    try {
                                        contactManager.sendContact(myUser.getUuid(), myUser.getPassword(), chatUser.getUuid());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                SearchUserActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        intent = new Intent(activity, ChatActivity.class);
                                        intent.putExtra("uuid", chatUser.getUuid());
                                        if(chatUser.getStatus().equals(User.TYPE_STATUS_ONLINE)){
                                            webSocketService.addOnlineUser(chatUser.getUuid());
                                        }
                                        activity.overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).start();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        int i=0;
                    }
                })
        );
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                new Thread(new Runnable() {
                    public void run() {
                       users = contactManager.findUser(newText.toString());
                        SearchUserActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                RecyclerView recyclerView = findViewById(R.id.list);
                                ContactAdapter adapter = new ContactAdapter(activity,users);
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                }).start();
                return true;
            }

            public void callSearch(String query) {

            }

        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(myUser.getUuid()!=null&&!bound) {
            intent = new Intent(this, WebSocketService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            bound=true;
            if(webSocketService!=null)
                webSocketService.create(myUser.getUuid(), myUser.getPassword(), context,dataBase, userManager);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myUser.getUuid()!=null&&!bound) {
            intent = new Intent(this, WebSocketService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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
        intent=null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound && intent == null) {
            webSocketService.close();
            bound = false;
        }
        intent = null;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            bound = true;
            WebSocketService.LocalBinder binder = (WebSocketService.LocalBinder) service;
            webSocketService = binder.getService();
            webSocketService.setCallbacks(SearchUserActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void onNewMessage(String type, String messageString) {

    }

    @Override
    public void setIntent(Intent intent) {
        this.intent = intent;
    }

}
