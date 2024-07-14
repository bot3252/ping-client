package com.bot.ping.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;

import java.io.IOException;

public class UpdateActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_updates);
        button=findViewById(R.id.buttonDownloadUpdate);
        button.setOnClickListener(download);
    }

    View.OnClickListener download = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ping.com:8080/updates/download/ping"));
            startActivity(browserIntent);
        }
    };
}
