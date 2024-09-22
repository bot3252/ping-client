package com.bot.ping.activity.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bot.ping.R;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.activity.main.ProfileActivity;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.model.MyUser;

public class SettingsActivity extends PreferenceActivity  {

    Activity activity;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
//        activity=this;
//        getLayoutInflater().inflate(R.layout.settings_toolbar, (ViewGroup)findViewById(android.R.id.content));
//        Toolbar toolbar = findViewById(R.id.settings_toolbar);
//        setActionBar(toolbar);
//        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
//        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
//        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.activity_vertical_margin) + 30, getResources().getDisplayMetrics());
//        getListView().setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);
    }


    @SuppressLint("ValidFragment")
    public class MyPreferenceFragment extends PreferenceFragment {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.menu_settings);
        }
    }
}