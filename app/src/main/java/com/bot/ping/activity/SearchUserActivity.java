package com.bot.ping.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.DataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.manager.DataManager;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.ui.adapter.ContactAdapter;
import com.bot.ping.ui.RecyclerItemClickListener;

import java.io.IOException;
import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity {
    DownloadManager downloadManager;
    DataManager dataManager;
    SearchView searchView;
    Activity activity;
    RecyclerView recyclerView;
    ArrayList<User> users;
    MyUser myUser;
    Bundle arguments;
    DataBase dataBase;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searsh_user);
        activity=this;
        downloadManager=new DownloadManager(this);
        dataManager=new DataManager(this);
        init();
    }

    void init(){
        arguments = getIntent().getExtras();
        myUser = dataManager.getMyUser();
        dataBase = new DataBase(this, myUser.getUuid());

        searchView=findViewById(R.id.searchViewUser);
        recyclerView = findViewById(R.id.list);

        getSupportActionBar().setTitle(R.string.search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) throws IOException {
                        new Thread(new Runnable() {
                            public void run() {
                                User chatUser = users.get(position);
                                try {
                                    downloadManager.sendContact(myUser.getUuid(), myUser.getPassword(), chatUser.getUuid());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                SearchUserActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (dataBase.checkUser(chatUser.getUuid())) {
                                            dataBase.saveContact(chatUser);

                                        }
                                        Intent intent = new Intent(activity, ChatActivity.class);
                                        intent.putExtra("uuid", chatUser.getUuid());
                                        startActivity(intent);
                                        activity.overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
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
                       users = downloadManager.findUser(newText.toString());
                        SearchUserActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                RecyclerView recyclerView = findViewById(R.id.list);
                                ContactAdapter adapter = new ContactAdapter(activity, users);
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
}
