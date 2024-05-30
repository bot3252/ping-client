package com.bot.ping.ui;

import static android.widget.Toast.makeText;

import static androidx.fragment.app.FragmentManagerKt.commit;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.activity.MainActivity;
import com.bot.ping.activity.RegisterActivity;
import com.bot.ping.databinding.ActivityMainBinding;
import com.bot.ping.model.User;
import com.bot.ping.ui.create_group.CreateGroupFragment;
import com.bot.ping.ui.message.MessageMenuFragment;
import com.bot.ping.ui.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

@SuppressLint("ValidFragment")
public class UIManagerActivity extends Fragment {
    private AppBarConfiguration mAppBarConfiguration;
    AppCompatActivity activity;
    ImageButton avatarImageButton;
    NavigationView navigationView;
    androidx.fragment.app.Fragment fragment;
    Bundle bundle;
    ArrayList<User> allContacts;
    public UIManagerActivity(AppCompatActivity activity, User user) {
        this.activity = activity;
        bundle = new Bundle();
        ActivityMainBinding binding = ActivityMainBinding.inflate(activity.getLayoutInflater());
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
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new CreateGroupFragment()).commit();
                        break;
                    case R.id.nav_settings:
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new SettingsFragment()).commit();
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
            activity.startActivity(intent);
        }
    };
    public void setNavAvatar(Bitmap avatar){
        avatarImageButton.setImageBitmap(avatar);
    }
    @SuppressLint("ResourceType")
    public void setAllContacts(ArrayList<User> allContacts){
        this.allContacts = allContacts;

        fragment = activity.getSupportFragmentManager().getFragments().get(0);

        RecyclerView recyclerView = fragment.requireView().findViewById(R.id.list);
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
