package com.bot.ping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.DataBase.DataBase;
import com.bot.ping.R;
import com.bot.ping.manager.DataManager;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileActivity extends AppCompatActivity{
    TextView textViewName, textViewNickname, textViewEmail, textViewDescription;
    ImageButton buttonBack, buttonSend;
    ImageView imageAvatar;
    User chatUser;
    Bundle arguments;
    DownloadManager downloadManager;
    DataBase dataBase;
    DataManager dataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        arguments = getIntent().getExtras();
        init();
    }

    void init(){
        downloadManager = new DownloadManager(this);
        dataManager = new DataManager(this);
        dataBase = new DataBase(this,dataManager.getMyUser().getUuid());
        chatUser = dataBase.getContact(arguments.getString("uuid"));

        textViewName = findViewById(R.id.textViewName);
        textViewNickname = findViewById(R.id.textViewNickname);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewDescription = findViewById(R.id.textViewDescription);

        imageAvatar = findViewById(R.id.imageAvatar);

        buttonBack = findViewById(R.id.buttonBack);
        buttonSend = findViewById(R.id.buttonSend);

        if(chatUser!=null) {
            textViewName.setText(chatUser.getName());
            textViewNickname.setText(chatUser.getNickname());
            textViewEmail.setText(chatUser.getEmail());
            textViewDescription.setText(chatUser.getDescription());
            imageAvatar.setImageBitmap(chatUser.getAvatar());
        }

        buttonBack.setOnClickListener(backListener);
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("uuid", chatUser.getUuid());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_left, R.anim.anim_slide_out_right);
        }
    };

}