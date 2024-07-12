package com.bot.ping.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bot.ping.R;
import com.bot.ping.manager.DataManager;
import com.bot.ping.manager.DownloadManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class SelectAvatarActivity extends AppCompatActivity {
    ImageView imageViewAvatar;
    Button buttonSelectAvatarFromGallery, buttonNext;
    ImageButton buttonSelectAvatar1,buttonSelectAvatar2, buttonSelectAvatar3,buttonSelectAvatar4;
    DownloadManager downloadManager;
    Bitmap bitmapAvatar;
    Uri uriAvatar;
    Dialog dialog;
    Activity activity;
    MyUser myUser;
    Bundle arguments;
    Context context;
    int SELECT_AVATAR = 200;
    DataManager dataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);
        init();
    }

    private void init() {
        context=this;
        imageViewAvatar = findViewById(R.id.imageAvatar);
        dataManager = new DataManager(this);
        activity = this;

        arguments = getIntent().getExtras();
        myUser = dataManager.getMyUser();

        bitmapAvatar= DataManager.drawableToBitmap(imageViewAvatar.getDrawable());

        buttonSelectAvatarFromGallery = findViewById(R.id.buttonSelectAvatarFromGallery);
        buttonNext = findViewById(R.id.buttonNext);
        buttonSelectAvatar1 = findViewById(R.id.imageButtonSelectAvatar1);
        buttonSelectAvatar2 = findViewById(R.id.imageButtonSelectAvatar2);
        buttonSelectAvatar3 = findViewById(R.id.imageButtonSelectAvatar3);
        buttonSelectAvatar4 = findViewById(R.id.imageButtonSelectAvatar4);

        buttonNext.setOnClickListener(onClickNext);
        buttonSelectAvatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAvatar.setImageResource(R.mipmap.avatar1);
                bitmapAvatar= DataManager.drawableToBitmap(imageViewAvatar.getDrawable());
            }
        });
        buttonSelectAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAvatar.setImageResource(R.mipmap.avatar2);
                bitmapAvatar= DataManager.drawableToBitmap(imageViewAvatar.getDrawable());
            }
        });
        buttonSelectAvatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAvatar.setImageResource(R.mipmap.avatar3);
                bitmapAvatar= DataManager.drawableToBitmap(imageViewAvatar.getDrawable());
            }
        });
        buttonSelectAvatar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAvatar.setImageResource(R.mipmap.avatar4);
                bitmapAvatar= DataManager.drawableToBitmap(imageViewAvatar.getDrawable());
            }
        });
        buttonSelectAvatarFromGallery.setOnClickListener(onClickSelectAvatarFromGallery);
        downloadManager = new DownloadManager(this);
        dialog = new Dialog(this);
    }

    View.OnClickListener onClickSelectAvatarFromGallery = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            imageChooserFromGallery();
        }
    };
    View.OnClickListener onClickNext = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        downloadManager.sendAvatar(bitmapAvatar, myUser.getUuid(),myUser.getPassword());
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
    };


    void imageChooserFromGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_AVATAR);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_AVATAR) {
                uriAvatar = data.getData();
                if (null != uriAvatar) {
                    try {
                        bitmapAvatar = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriAvatar);
                        imageViewAvatar.setImageBitmap(bitmapAvatar);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
