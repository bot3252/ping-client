package com.bot.ping.activity.main;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.activity.login.NoConnectionActivity;
import com.bot.ping.callbacks.WebSocketServiceCallbacks;
import com.bot.ping.dataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.download.ConnectCheck;
import com.bot.ping.manager.download.MessageManager;
import com.bot.ping.manager.download.UserManager;
import com.bot.ping.model.Message;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.service.WebSocketService;
import com.bot.ping.ui.adapter.ChatAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class ChatActivity extends Activity implements WebSocketServiceCallbacks {
    private WebSocketService webSocketService;
    private boolean bound = false;
    ImageButton buttonBack, buttonSend, avatarButton;
    ImageView statusChatImage;
    TextView textViewName,textViewNickname;
    MultiAutoCompleteTextView editTextMessage;
    RecyclerView recyclerViewMessages;
    Bundle arguments;
    User chatUser;
    MyUser myUser;
    MessageManager messageManager;
    Context context;
    ArrayList<Message> newMessages = new ArrayList<Message>();
    ArrayList<Message> allMessages = new ArrayList<Message>();
    ChatAdapter chatAppMsgAdapter;
    DataManager dataManager;
    DataBase dataBase;
    UserManager userManager;
    OkHttpClient client;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        arguments = getIntent().getExtras();
        if(ConnectCheck.check(this)) {
            init();
        }else{
            intent = new Intent(context, NoConnectionActivity.class);
            startActivity(intent);
        }
    }

    void init(){
        intent=new Intent();
        context = this;
        dataManager = new DataManager(this);

        myUser = dataManager.getMyUser();
        dataBase = new DataBase(this, myUser.getUuid());
        userManager = new UserManager(client,this);
        client = new OkHttpClient();
        messageManager = new MessageManager(client, this);

        chatUser = dataBase.getContact(arguments.getString("uuid"));

        buttonBack = findViewById(R.id.buttonBack);
        buttonSend = findViewById(R.id.buttonSend);
        avatarButton = findViewById(R.id.buttonAvatar);

        textViewName = findViewById(R.id.textViewName);
        textViewNickname = findViewById(R.id.textViewNickname);
        statusChatImage = findViewById(R.id.imageViewStatus);

        editTextMessage = findViewById(R.id.editTextInputMessage);

        recyclerViewMessages = findViewById(R.id.chat_recycler_view);

        buttonBack.setOnClickListener(backListener);
        buttonSend.setOnClickListener(sendListener);
        avatarButton.setOnClickListener(avatarListener);

        textViewName.setText(chatUser.getName());
        textViewNickname.setText(chatUser.getNickname());
        avatarButton.setImageBitmap(chatUser.getAvatar());

        allMessages=dataBase.getMessages(myUser.getUuid(), chatUser.getUuid());

        if (!allMessages.isEmpty()) {
            chatAppMsgAdapter = new ChatAdapter(allMessages);
            recyclerViewMessages.setAdapter(chatAppMsgAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerViewMessages.setLayoutManager(linearLayoutManager);

            int newMsgPosition = allMessages.size() - 1;
            recyclerViewMessages.scrollToPosition(newMsgPosition);
        }
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
        }
    };


    View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (editTextMessage.getText()!=null) {
                String msgContent = editTextMessage.getText().toString()
                        .replace("\n","/n");

                editTextMessage.setText("");
                new Thread(new Runnable() {
                    public void run() {
                        if(!TextUtils.isEmpty(msgContent)) {
                            Message message = messageManager.sendMessage(myUser.getUuid(), chatUser.getUuid(), myUser.getPassword(), msgContent, DataManager.getIdDevice(context));
                            newMessages.add(message);
                            allMessages.add(message);
                            int newMsgPosition = allMessages.size()-1;
                            ChatActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                                    recyclerViewMessages.scrollToPosition(newMsgPosition);
                                    editTextMessage.setText("");
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    };

    View.OnClickListener avatarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("uuid", chatUser.getUuid());
            intent.putExtra("lastActivity", "ChatActivity");
            startActivity(intent);

            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            WebSocketService.LocalBinder binder = (WebSocketService.LocalBinder) service;
            webSocketService = binder.getService();
            if (!webSocketService.isCreated) {
                webSocketService.create(myUser.getUuid(), myUser.getPassword(), context,dataBase, userManager);
            }
            chatUser.setStatus(webSocketService.getStatusUser(chatUser.getUuid()));
            ChatActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    setImageStatusChatUser();
                }
            });
            bound = true;
            webSocketService.setCallbacks(ChatActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };
    @Override
    public void onNewMessage(String type,String messageString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(messageString);

            switch (type) {
                case "status":
                    try {
                        chatUser.setStatus(jsonObject.getString("status"));
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                setImageStatusChatUser();
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "message":
                    if (jsonObject.getString("from").equals(chatUser.getUuid()) || jsonObject.getString("from").equals(myUser.getUuid())){
                        Message message = Parser.parseMessage(messageString, myUser.getUuid());
                        newMessages.add(message);
                        allMessages.add(message);
                        int newMsgPosition = allMessages.size() - 1;
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                recyclerViewMessages.scrollToPosition(newMsgPosition);
                                chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                            }
                        });
                    }
                    break;
                case "webSocketService":
                    String state = jsonObject.getString("state");
                    if(state.equals("start")) {
                        setOnlineUser();
                        new Thread(new Runnable() {
                            public void run() {

                                if (allMessages.isEmpty()) {
                                    newMessages=messageManager.getAllMessage(chatUser.getUuid(), myUser.getUuid(), myUser.getPassword());
                                } else {
                                    Message lastMessage = allMessages.get(allMessages.size()-1);
                                    newMessages = messageManager.getAllNewMessages(myUser.getUuid(), chatUser.getUuid(), myUser.getPassword(), lastMessage.getDate() + " " + lastMessage.getTime());
                                }
                                allMessages.addAll(newMessages);
                                ChatActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        chatAppMsgAdapter = new ChatAdapter(allMessages);
                                        recyclerViewMessages.setAdapter(chatAppMsgAdapter);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                        recyclerViewMessages.setLayoutManager(linearLayoutManager);

                                        int newMsgPosition = allMessages.size()-1;
                                        recyclerViewMessages.scrollToPosition(newMsgPosition);
                                    }
                                });
                            }
                        }).start();
                    }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(myUser.getUuid()!=null&&!bound) {
//            intent = new Intent(this, WebSocketService.class);
//            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//            bound=true;
////            if(webSocketService==null)
////                webSocketService.create(myUser.getUuid(), myUser.getPassword(), context,dataBase, userManager);
//        }
//    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(myUser.getUuid()!=null&&!bound) {
            if(webSocketService!=null) {
                webSocketService.setCallbacks(ChatActivity.this);
                if (!webSocketService.isCreated)
                    webSocketService.create(myUser.getUuid(), myUser.getPassword(), context, dataBase, userManager);
            }else {
                intent = new Intent(this, WebSocketService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }

            bound=true;
        }
        intent=null;
    }
    @Override
    protected void onResume() {
        super.onResume();

        if(myUser.getUuid()!=null&&!bound) {
            if(webSocketService!=null) {
                webSocketService.setCallbacks(ChatActivity.this);
                if (!webSocketService.isCreated)
                    webSocketService.create(myUser.getUuid(), myUser.getPassword(), context, dataBase, userManager);
            }else {
                intent = new Intent(this, WebSocketService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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
        dataBase.saveMessages(newMessages);
        newMessages.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound && intent == null) {
            bound = false;
        }
    }
    void setOnlineUser(){
        ArrayList<String> onlineUsers = webSocketService.getUsersOnline();
        for (int b = 0; b <= onlineUsers.size() - 1; b++) {
            if (onlineUsers.get(b).equals(chatUser.getUuid())) {
                chatUser.setStatus(User.TYPE_STATUS_ONLINE);
            }
        }
        if(chatUser.getStatus().equals(User.TYPE_STATUS_ONLINE)){
            chatUser.setStatus(User.TYPE_STATUS_ONLINE);
        }else{
            chatUser.setStatus(User.TYPE_STATUS_OFFLINE);
        }

        ChatActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                setImageStatusChatUser();
            }
        });
    }

    public void setImageStatusChatUser(){
        if(Objects.equals(chatUser.getStatus(), User.TYPE_STATUS_ONLINE)){
            Drawable drawable= ContextCompat.getDrawable(this, R.drawable.status_online);
            statusChatImage.setImageDrawable(drawable);
        }else {
            Drawable drawable=ContextCompat.getDrawable(this, R.drawable.status_offline);
            statusChatImage.setImageDrawable(drawable);
        }
    }
}