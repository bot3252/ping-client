package com.bot.ping.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.PopUpToBuilder;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.activity.RegisterActivity;
import com.bot.ping.databinding.ActivityMainBinding;
import com.bot.ping.model.User;
import com.bot.ping.ui.message.MessageMenuFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class UiManagerActivity extends Fragment {
    private AppBarConfiguration mAppBarConfiguration;
    AppCompatActivity activity;
    ImageButton avatarImageButton;
    NavigationView navigationView;
    @SuppressLint("ValidFragment")
    public UiManagerActivity(AppCompatActivity activity, User user) {
        this.activity = activity;
        ActivityMainBinding binding;
        binding = ActivityMainBinding.inflate(activity.getLayoutInflater());
        activity.setContentView(binding.getRoot());

        activity.setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_message_menu, R.id.nav_create_group, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(activity, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View parentView = navigationView.getHeaderView(0);

        TextView nameText = parentView.findViewById(R.id.nav_name_text);
        TextView emailText = parentView.findViewById(R.id.nav_email_text);
        avatarImageButton = parentView.findViewById(R.id.nav_avatar);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
    }

    View.OnClickListener setEditProfile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(activity.getApplicationContext(), RegisterActivity.class);
            activity.startActivity(intent);
        }
    };

    public void setNavAvatar(Bitmap avatar){
        avatarImageButton.setImageBitmap(avatar);
    }
    public void setAllContacts(ArrayList<User> allContacts){
        RecyclerView recyclerView = activity.getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.list);
        ContactAdapter adapter = new ContactAdapter(activity, allContacts);
        recyclerView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || activity.onSupportNavigateUp();
    }



}
