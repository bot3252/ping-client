package com.bot.ping.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.PopUpToBuilder;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bot.ping.R;
import com.bot.ping.databinding.ActivityMainBinding;
import com.bot.ping.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class UiManagerActivity {
    private AppBarConfiguration mAppBarConfiguration;
    AppCompatActivity activity;
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
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_message_menu, R.id.nav_settings, R.id.nav_create_group)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(activity, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View parentView = navigationView.getHeaderView(0);

        TextView nameText = parentView.findViewById(R.id.nav_name_text);
        TextView emailText = parentView.findViewById(R.id.nav_email_text);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
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
