package com.bot.ping.activity.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bot.ping.R;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.manager.download.LoginManager;
import com.bot.ping.manager.download.UserManager;
import com.bot.ping.model.MyUser;
import com.bot.ping.ui.dialog.DialogLoading;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class SelectAvatarActivity extends AppCompatActivity {
    ImageView imageViewAvatar;
    Button buttonSelectAvatarFromGallery, buttonNext;
    ImageButton buttonSelectAvatar1,buttonSelectAvatar2, buttonSelectAvatar3,buttonSelectAvatar4;
    UserManager userManager;
    LoginManager loginManager;
    Bitmap bitmapAvatar;
    Uri uriAvatar;
    Dialog dialog;
    Activity activity;
    MyUser myUser;
    Context context;
    int SELECT_AVATAR = 200;
    DataManager dataManager;
    DialogLoading dialogLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);
        init();
    }

    @SuppressLint("HardwareIds")
    private void init() {
        dialogLoading=new DialogLoading(this);
        context=this;
        imageViewAvatar = findViewById(R.id.imageAvatar);
        dataManager = new DataManager(this);
        activity = this;

        OkHttpClient okHttpClient = new OkHttpClient();

        dataManager = new DataManager(this);
        userManager = new UserManager(okHttpClient, this);
        loginManager = new LoginManager(okHttpClient, this);

        myUser = dataManager.getMyUser();

        bitmapAvatar = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.avatar1);;

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
                Drawable drawable= ContextCompat.getDrawable(activity, R.drawable.avatar1_drawable);
                imageViewAvatar.setImageDrawable(drawable);
                bitmapAvatar= BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.avatar1);
            }
        });
        buttonSelectAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable= ContextCompat.getDrawable(activity, R.drawable.avatar2_drawable);
                imageViewAvatar.setImageDrawable(drawable);
                bitmapAvatar=BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.avatar2);
            }
        });
        buttonSelectAvatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable= ContextCompat.getDrawable(activity, R.drawable.avatar3_drawable);
                imageViewAvatar.setImageDrawable(drawable);
                bitmapAvatar= BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.avatar3);
            }
        });
        buttonSelectAvatar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable= ContextCompat.getDrawable(activity, R.drawable.avatar4_drawable);

                imageViewAvatar.setImageDrawable(drawable);
                bitmapAvatar=  BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.avatar4);
            }
        });
        buttonSelectAvatarFromGallery.setOnClickListener(onClickSelectAvatarFromGallery);
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
            dialogLoading.showDialog();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        userManager.sendAvatar(bitmapAvatar, myUser.getUuid(),myUser.getPassword());
                        loginManager.login(myUser.getEmail(), myUser.getPassword(), myUser.getFirebaseToken(), DataManager.getIdDevice(context));
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        dialogLoading.closeDialog();
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
