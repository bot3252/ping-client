package com.bot.ping.ui;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bot.ping.R;

import com.bot.ping.activity.RegisterActivity;
import com.bot.ping.activity.SearchUserActivity;
import com.bot.ping.activity.SettingsActivity;
import com.bot.ping.databinding.ActivityMainBinding;
import com.bot.ping.model.MyUser;
import com.bot.ping.model.User;
import com.bot.ping.ui.message.MessageMenuFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class UIManagerActivity extends Fragment {
    private AppBarConfiguration mAppBarConfiguration;
    AppCompatActivity activity;
    ImageButton avatarImageButton;
    NavigationView navigationView;
    androidx.fragment.app.Fragment fragment;
    ArrayList<User> allContacts;
    public UIManagerActivity(AppCompatActivity activity, MyUser user) {
        this.activity = activity;
        ActivityMainBinding binding = ActivityMainBinding.inflate(activity.getLayoutInflater());
        activity.setContentView(binding.getRoot());

        activity.setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), SearchUserActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
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
        fragment = activity.getSupportFragmentManager().getFragments().get(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                drawer.closeDrawer(GravityCompat.START);
                switch (id) {
                    case R.id.nav_message_menu:
                        MessageMenuFragment messageMenuFragment = new MessageMenuFragment();
                        messageMenuFragment.allContacts = allContacts;
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, messageMenuFragment).commit();
                        break;
                    case R.id.nav_create_group:
                        break;
                    case R.id.nav_settings:
                        Intent intent = new Intent(activity.getApplicationContext(), SettingsActivity.class);
                        activity.startActivity(intent);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

    }

    View.OnClickListener setEditProfile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(activity.getApplicationContext(), RegisterActivity.class);
            activity.overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
            activity.startActivity(intent);
        }
    };
    public void setNavAvatar(Bitmap avatar){
        avatarImageButton.setImageBitmap(avatar);
    }
    public void setAllContacts(ArrayList<User> allContacts){
        this.allContacts = allContacts;
        setAdapter();
    }

    private void setAdapter() {
        MessageMenuFragment messageMenuFragment = new MessageMenuFragment();
        messageMenuFragment.allContacts = allContacts;
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, messageMenuFragment).commit();
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
