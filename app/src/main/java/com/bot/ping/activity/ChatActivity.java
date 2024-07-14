package com.bot.ping.activity;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.DataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.manager.DataManager;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.model.Message;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.ui.adapter.ChatAdapter;

import java.util.ArrayList;

public class ChatActivity extends Activity {
    ImageButton buttonBack, buttonSend, avatarButton;
    TextView textViewName,  textViewEmail,textViewNickname;
    MultiAutoCompleteTextView editTextMessage;
    RecyclerView recyclerViewMessages;
    Bundle arguments;
    User chatUser;
    MyUser myUser;
    DownloadManager downloadManager;
    Context context;
    ArrayList<Message> messages = new ArrayList<Message>();
    ChatAdapter chatAppMsgAdapter;
    DataManager dataManager;
    DataBase dataBase;
    boolean activityDestroyed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        arguments = getIntent().getExtras();
        init();
    }

    void init(){

        context = this;
        dataManager = new DataManager(this);

        myUser = dataManager.getMyUser();
        dataBase = new DataBase(this, myUser.getUuid());
        downloadManager = new DownloadManager(this);

        chatUser = dataBase.getContact(arguments.getString("uuid"));

        buttonBack = findViewById(R.id.buttonBack);
        buttonSend = findViewById(R.id.buttonSend);
        avatarButton = findViewById(R.id.buttonAvatar);

        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewNickname = findViewById(R.id.textViewNickname);

        editTextMessage = findViewById(R.id.editTextInputMessage);

        recyclerViewMessages = findViewById(R.id.chat_recycler_view);

        buttonBack.setOnClickListener(backListener);
        buttonSend.setOnClickListener(sendListener);
        avatarButton.setOnClickListener(avatarListener);

        textViewName.setText(chatUser.getName());
        textViewEmail.setText(chatUser.getEmail());
        textViewNickname.setText(chatUser.getNickname());
        avatarButton.setImageBitmap(chatUser.getAvatar());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);

        new Thread(new Runnable() {
            public void run() {
                messages = downloadManager.getAllMessage(chatUser.getUuid(), myUser.getUuid(), myUser);
                chatAppMsgAdapter = new ChatAdapter(messages);
                ChatActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        recyclerViewMessages.setAdapter(chatAppMsgAdapter);
                        int newMsgPosition = messages.size() - 1;
                        recyclerViewMessages.scrollToPosition(newMsgPosition);
                    }
                });
                while (true) {
                    if(!activityDestroyed) {
                        ArrayList<Message> messages2 = downloadManager.getAllMessage(chatUser.getUuid(), myUser.getUuid(), myUser);
                        if (messages2 != null) {
                            if (messages.size() != messages2.size()) {
                                int newMsgPosition = messages2.size() - 1;
                                Message message = null;
                                if (!messages2.isEmpty()) {
                                    if(messages.size()>2)
                                        message = messages2.get(messages.size()-2);
                                    else {
                                        message = messages2.get(0);
                                    }
                                    if (message.getMsgType().equals(Message.MSG_TYPE_RECEIVED)) {
                                        messages.add(message);
                                        ChatActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

                                            }
                                        });
                                    }
                                }
                            }
                        }
                        try {
                            sleep(800);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        break;
                    }
                }
            }
        }).start();
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
        }
    };


    View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (editTextMessage.getText()!=null) {

                new Thread(new Runnable() {
                    public void run() {
                        String msgContent = editTextMessage.getText().toString();
                        if(!TextUtils.isEmpty(msgContent)) {
                            Message message = new Message(msgContent, Message.MSG_TYPE_SENT);
                            messages.add(message);
                            int newMsgPosition = messages.size() - 1;
                            downloadManager.sendMessage(myUser.getUuid(), chatUser.getUuid(), myUser.getPassword(), editTextMessage.getText().toString());
                            ChatActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                                    editTextMessage.setText("");
                                    recyclerViewMessages.scrollToPosition(newMsgPosition);
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
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("uuid", chatUser.getUuid());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        activityDestroyed = true;

    }
}